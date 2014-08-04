/**
* Service4All: A Service-oriented Cloud Platform for All about Software Development
* Copyright (C) Institute of Advanced Computing Technology, Beihang University
* Contact: service4all@act.buaa.edu.cn
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3.0 of the License, or any later version. 
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details. 
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*/
package cn.edu.buaa.act.service4all.atomicws.deploy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.atomicws.deploy.loadbalance.ContainerReplica;
import cn.edu.buaa.act.service4all.atomicws.deploy.loadbalance.PerformanceSortor;
import cn.edu.buaa.act.service4all.atomicws.deploy.threads.DeployStore2DatabaseThread;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.Transfer;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.UserControl;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ServiceDeployBusinessUnit extends BusinessUnit {

	private static final Log logger = LogFactory
			.getLog(ServiceDeployBusinessUnit.class);

	private static final double[] weight = {
			DeployConstants.getInstance().getCpuRatio(),
			DeployConstants.getInstance().getMemoryRatio(),
			DeployConstants.getInstance().getThroughputRatio() };

	public void dispatch(ExchangeContext context) {

		if (getInvokers().get("AvailableServerQueryInvoker") == null) {
			logger.error("Missing AvailableServerQueryInvoker!");
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Missing the AvailableServerQueryInvoker!");
			ep.setSender(getReceiver().getEndpoint());
			this.handleInvocationException(ep);
		}
		Invoker invoker = getInvokers().get("AvailableServerQueryInvoker");

		try {
			invoker.sendRequestExchange(context);

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}

	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		logger.info("Handling response msg");

		Element urls = ((Element) context.getData("urls"));
		int availableContainerCount = Integer.valueOf(urls
				.getAttribute("length"));
		String serviceID = (String) context.getData(DeployConstants.SERVICEID);
		if (availableContainerCount < 1) {
			String errorString = "There is no available container!!";
			String serviceName = (String) context
					.getData(DeployConstants.SERVICENAME);
			try {
				context.storeData(DeployConstants.RESPONSE, DocsBuilder
						.buildNoAvailableContainerResponseDoc(serviceID,
								serviceName, errorString));
				this.getReceiver().sendResponseMessage(context);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
			return;
		}

		ContainerReplica[] containerPerfs = new ContainerReplica[availableContainerCount];
		Element urlElement = null;
		for (int i = 0; i < availableContainerCount; i++) {
			urlElement = (Element) urls.getElementsByTagName("container").item(
					i);
			String deployUrl = urlElement.getAttribute("deployUrl");
			String id = urlElement.getAttribute("id");
			double cpu = Double.parseDouble(urlElement.getAttribute("cpu"));
			double memeory = Double.parseDouble(urlElement
					.getAttribute("memory"));
			double throughput = Double.parseDouble(urlElement
					.getAttribute("throughput"));

			ContainerReplica p = new ContainerReplica(deployUrl, id, cpu,
					memeory, throughput);
			containerPerfs[i] = p;
		}

		int[] rank = new int[availableContainerCount];
		for (int i = 0; i < availableContainerCount; i++) {
			rank[i] = i;
		}

		int deployNum = (Integer) context.getData(DeployConstants.DEPLOYNUM);
		if (availableContainerCount > deployNum) {
			PerformanceSortor sortor = new PerformanceSortor(weight,
					containerPerfs);
			rank = sortor.sort();

		}

		if (((String) context.getData("type")).equals("deploy")) {
			try {
				deploy(containerPerfs, rank, deployNum, context);
			} catch (AxisFault e) {
				e.printStackTrace();
			}
		} else {
			replicaAcqustion(containerPerfs, rank, deployNum, context);
		}
	}

	private void replicaAcqustion(ContainerReplica[] performances, int[] rank,
			int copyCount, ExchangeContext context) {

		List<ContainerReplica> deployedReplicas = new ArrayList<ContainerReplica>();
		String sourceInvokeUrl = null;
		String sourceIP = null;

		String serviceID = (String) context.getData(DeployConstants.SERVICEID);

		String fileName = (String) context.getData(DeployConstants.SERVICENAME);
		
		String userName = (String) context.getData(DeployConstants.USERNAME);
		sourceInvokeUrl = (String) context.getData(DeployConstants.INVOKEURL);
		sourceIP = getIp(sourceInvokeUrl);

		int successDeployNum = 0;
		String targetIP = null;
		String targetPort = null;

		for (int i = 0; i < copyCount; i++) {
			if (rank[i] < copyCount) {
				targetIP = getIp(performances[i].getdeployUrl());
				targetPort = getPort(performances[i].getdeployUrl());
				if (!sourceIP.equals(targetIP)) {
					logger.info(sourceIP + "-->" + targetIP);
					TransWSToRemote(sourceIP, fileName, targetIP, targetPort, userName);
				}
				try {
					localDeploy(targetIP, targetPort, fileName, serviceID, userName);
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
				successDeployNum++;
				deployedReplicas.add(performances[i]);
			}
		}
		String deployResultInfo = null;
		if (successDeployNum > 0) {
			context.storeData(DeployConstants.ISSUCCESSFUL, "true");
			deployResultInfo = "The increment is successful";

		} else {
			context.storeData(DeployConstants.ISSUCCESSFUL, "false");
			deployResultInfo = "The increment fails";

		}
		context.storeData(DeployConstants.INFO, deployResultInfo);

		context.storeData(DeployConstants.DEPLOYEDRESULTS, deployedReplicas);

		Thread s2dbThread = new Thread(new DeployStore2DatabaseThread(context));
		s2dbThread.run();

		Invoker invoker = getInvokers().get("DeploymentFeedbackInvoker");
		try {

			invoker.sendRequestExchange(context);

		} catch (MessageExchangeInvocationException e) {

			this.handleInvocationException(e);
		}

	}

	private void deploy(ContainerReplica[] performances, int[] rank,
			int deployNum, ExchangeContext context) throws AxisFault {
		logger.info("Start Deploying...��Deploy" + deployNum + "replications��");

		int successDeployNum = 0;
		List<ContainerReplica> deployedReplicas = new ArrayList<ContainerReplica>();
		OMElement outOMElement = null;
		OMElement input = (OMElement) context
				.getData(DeployConstants.REQ4CONTAINER);
		
		String serviceID = (String) context.getData(DeployConstants.SERVICEID);
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement serviceIDOm = omFactory.createOMElement("serviceID", null);
		serviceIDOm.setText(serviceID);
		input.addChild(serviceIDOm);
		
		String userName = (String) context.getData(DeployConstants.USERNAME);
		OMElement userOM = omFactory.createOMElement("userName",null);
		userOM.setText(userName);
		input.addChild(userOM);
		
		String containerUrl;
		int j;
		String serviceName = null;
		String info = null;
		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = bulidOptions();

		Document output = null;

		for (j = 0; j < rank.length; j++) {
			if (rank[j] < deployNum) {
				String isSuccessful = null;
				String port = retrievePort(performances[j].getdeployUrl());
				containerUrl = replacePort(performances[j].getdeployUrl());
				logger.info("before deploy url:"
						+ performances[j].getdeployUrl());

				logger.info("Deploying the" + (successDeployNum + 1)
						+ "th replica");
				try {
					OMElement portOm = omFactory.createOMElement("port", null);
					portOm.setText(port);
					input.addChild(portOm);
					options.setTo(new EndpointReference(containerUrl));
					serviceClient.setOptions(options);

					outOMElement = serviceClient.sendReceive(input);
					output = Transfer.omToDoc(outOMElement);

					logger.info(XMLUtils.retrieveDocumentAsString(output));

					if (serviceName == null) {

						serviceName = (String) output.getDocumentElement()
								.getElementsByTagName("serviceName").item(0)
								.getTextContent();
						context.storeData(DeployConstants.SERVICENAME,
								serviceName);
					}
					isSuccessful = output.getDocumentElement()
							.getElementsByTagName(DeployConstants.ISSUCCESSFUL)
							.item(0).getTextContent();
					if (DeployConstants.FALSE.equals(isSuccessful)) {

						logger.warn("Deploying the " + (successDeployNum + 1)
								+ "th replica failed��" + containerUrl);

						info = output.getElementsByTagName("deployResultInfo")
								.item(0).getTextContent();

						context.storeData(DeployConstants.INFO, info);
					} else if (DeployConstants.TRUE.equals(isSuccessful)) {
						logger.info("Deploying the " + (successDeployNum + 1)
								+ "th replica sucessful��" + containerUrl);
						successDeployNum++;
						deployedReplicas.add(performances[j]);
						logger.info("after deployment:"
								+ performances[j].getdeployUrl());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (successDeployNum == deployNum) {
			String deployResultInfo = "The deployment is successful";
			context.storeData(DeployConstants.ISSUCCESSFUL, "true");
			context.storeData(DeployConstants.INFO, deployResultInfo);

		} else {
			context.storeData(DeployConstants.ISSUCCESSFUL, "false");
		}
		context.storeData(DeployConstants.DEPLOYEDRESULTS, deployedReplicas);

		Thread s2dbThread = new Thread(new DeployStore2DatabaseThread(context));
		s2dbThread.run();

		if ("true".equals(DeployConstants.getInstance()
				.getCheckFb2UserAuthentication())) {
			UserControl.deployNotification(
					(String) context.getData(DeployConstants.USERNAME),
					(String) context.getData(DeployConstants.SERVICEID));
			logger.info("Notificate the user control comp");
		}

		Invoker invoker = getInvokers().get("DeploymentFeedbackInvoker");
		try {

			invoker.sendRequestExchange(context);

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}

	}

	private String replacePort(String containerUrl) {
		String[] strs = containerUrl.split(":");
		StringBuffer sb = new StringBuffer();
		strs[2] = strs[2].replace(strs[2].substring(0, strs[2].indexOf('/')),
				DeployConstants.DEPLOY_PORT);
		for (String str : strs)
			sb.append(str + ":");
		return sb.toString().substring(0, sb.length() - 1);
	}

	private String retrievePort(String containerUrl) {
		String[] strs = containerUrl.split(":");
		return strs[2].substring(0, strs[2].indexOf('/'));
	}

	private Options bulidOptions() {
		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setProperty(Constants.Configuration.ENABLE_MTOM,
				Constants.VALUE_TRUE);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		return options;
	}

	private void TransWSToRemote(String ip, String fileName, String targetIp,
			String targetPort, String userName) {

		RPCServiceClient serviceClient;
		try {
			serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference(
					"http://"
							+ ip
							+ ":8080/axis2/services/localSAManagerService/localWStoRemote");
			options.setTo(targetEPR);
			options.setAction("urn:localWStoRemote");
			serviceClient.setOptions(options);
			QName opAddEntry = new QName(
					"http://manageServices.serviceCloud.sdp.act.org.cn",
					"localWStoRemote");
			Object[] opAddEntryArgs = new Object[] { fileName, targetIp, "",
					targetPort ,userName};
			OMElement response = serviceClient.invokeBlocking(opAddEntry,
					opAddEntryArgs);

			logger.info("Remote transfer result��");

			try {
				response.serialize(System.out);
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			e.printStackTrace();
		}

	}

	private void localDeploy(String ip, String port, String fileName,
			String serviceID,String userName) throws XMLStreamException {
		RPCServiceClient serviceClient;
		try {
			serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference(
					"http://"
							+ ip
							+ ":8080/axis2/services/localSAManagerService/deployService");
			options.setTo(targetEPR);
			options.setAction("urn:deployService");
			serviceClient.setOptions(options);
			QName opAddEntry = new QName(
					"http://manageServices.serviceCloud.sdp.act.org.cn",
					"deployService");
			Object[] opAddEntryArgs = new Object[] { fileName, port, serviceID,userName};
			OMElement response = serviceClient.invokeBlocking(opAddEntry,
					opAddEntryArgs);
			logger.info("Remote deployment result��");
			response.serialize(System.out);

		} catch (AxisFault e) {
			e.printStackTrace();
		}
	}

	private static String getIp(String url) {
		int begin = url.indexOf("//");
		int end = url.lastIndexOf(':');
		return url.substring(begin + 2, end);
	}

	private static String getPort(String url) {
		int i = url.lastIndexOf(':');
		return url.substring(i + 1, url.indexOf('/', 10));
	}

}
