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
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.deploy.loadbalance.ContainerReplica;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;

public class DocsBuilder {
	private static final Log logger = LogFactory.getLog(DocsBuilder.class);

	public static Document buildReplicaReleaseResponseDoc(
			ExchangeContext context) {
		logger.info("buildReplicaReleaseResponseDoc...");
		Document responseDoc = null;
		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element root = responseDoc.createElement("replicaRelease");
			root.setAttribute("type", "WebService");
			Element isSuccessful = responseDoc.createElement("isSuccessufl");
			isSuccessful.setTextContent((String) context
					.getData(DeployConstants.ISSUCCESSFUL));
			root.appendChild(isSuccessful);
			responseDoc.appendChild(root);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return responseDoc;
	}

	public static Document buildDeployResponseDoc(ExchangeContext context) {

		Document responseDoc = null;
		String serviceID = (String) context.getData(DeployConstants.SERVICEID);
		String serviceName = (String) context
				.getData(DeployConstants.SERVICENAME);

		String isSuccessful = (String) context
				.getData(DeployConstants.ISSUCCESSFUL);
		String info = (String) context.getData(DeployConstants.INFO);
		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element element = responseDoc.createElement("deployResponse");
			element.setAttribute("type", "WebService");

			Element id = responseDoc.createElement("serviceID");
			id.setTextContent(serviceID);
			Element name = responseDoc.createElement("serviceName");
			name.setTextContent(serviceName);
			Element isSuccessfulElement = responseDoc
					.createElement("isSuccessful");
			isSuccessfulElement.setTextContent(isSuccessful);
			Element deployResultInfo = responseDoc
					.createElement("deployResultInfo");
			deployResultInfo.setTextContent(info);

			element.appendChild(id);
			element.appendChild(name);
			element.appendChild(isSuccessfulElement);
			element.appendChild(deployResultInfo);

			responseDoc.appendChild(element);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return responseDoc;
	}

	@SuppressWarnings("unchecked")
	public static Document buildDeployFb2BusDoc(ExchangeContext context) {

		Document feedbackDoc = null;
		try {
			feedbackDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element element = feedbackDoc.createElement("deployFeedback");
			element.setAttribute("type", "WebService");

			Element serviceIDElement = feedbackDoc.createElement("serviceID");
			serviceIDElement.setTextContent((String) context
					.getData(DeployConstants.SERVICEID));

			String serviceName = (String) context
					.getData(DeployConstants.SERVICENAME);

			Element serviceNameElement = feedbackDoc
					.createElement("serviceName");
			serviceNameElement.setTextContent(serviceName);

			Element isSuccessfulElement = feedbackDoc
					.createElement("isSuccessful");
			isSuccessfulElement.setTextContent((String) context
					.getData(DeployConstants.ISSUCCESSFUL));

			Element deployedUrls = feedbackDoc.createElement("deployedResults");
			List<ContainerReplica> deployedReplias = new ArrayList<ContainerReplica>();
			deployedReplias = (List<ContainerReplica>) context
					.getData(DeployConstants.DEPLOYEDRESULTS);
			int successDeployNum = deployedReplias.size();
			ContainerReplica tempPer = null;
			for (int i = 0; i < successDeployNum; i++) {
				Element tempElement = feedbackDoc
						.createElement("deployedContainer");
				tempPer = deployedReplias.get(i);
				tempElement.setAttribute("id", tempPer.getContainerId());
//				tempElement.setAttribute("invokeUrl", tempPer.getdeployUrl()
//						.replaceFirst("localSAManagerService", serviceName));
				tempElement.setAttribute("invokeUrl", tempPer.getContainerId().split("_")[1] + "/axis2/services/" + serviceName + "/");
				deployedUrls.appendChild(tempElement);
			}
			element.appendChild(serviceIDElement);
			element.appendChild(serviceNameElement);
			element.appendChild(isSuccessfulElement);
			element.appendChild(deployedUrls);

			feedbackDoc.appendChild(element);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return feedbackDoc;
	}

	public static Document buildReplicaAcquistionResponseDoc(
			ExchangeContext context) {
		logger.info("buildReplicaAcquistionResponseDoc...");
		Document responseDoc = null;
		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element root = responseDoc.createElement("replicaAcquisition");
			root.setAttribute("type", "WebService");
			Element isSuccessful = responseDoc.createElement("isSuccessufl");
			isSuccessful.setTextContent((String) context
					.getData(DeployConstants.ISSUCCESSFUL));
			root.appendChild(isSuccessful);
			responseDoc.appendChild(root);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return responseDoc;
	}

	public static Document buildNoAvailableContainerResponseDoc(
			String serviceID, String fileName, String errorString)
			throws ParserConfigurationException {
		logger.info("buildNoAvailableContainerResponseDoc...");
		Document responseDoc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element element = responseDoc.createElement("deployResponse");
		element.setAttribute("type", "WebService");
		Element name = responseDoc.createElement("serviceName");
		name.setTextContent(fileName);
		Element id = responseDoc.createElement("serviceID");
		id.setTextContent(serviceID);
		Element isSuccessful = responseDoc.createElement("isSuccessful");
		isSuccessful.setTextContent("false");
		Element deployResultInfo = responseDoc
				.createElement("deployResultInfo");
		deployResultInfo.setTextContent(errorString);

		element.appendChild(id);
		element.appendChild(name);
		element.appendChild(isSuccessful);
		element.appendChild(deployResultInfo);
		responseDoc.appendChild(element);

		return responseDoc;
	}

	public static Document buildQueryResponseDoc(boolean isActive)
			throws ParserConfigurationException {
		if (isActive) {
			logger.info("Increment query is started");
		} else {
			logger.info("Increment query is stopped");
		}
		Document responseDoc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element root = responseDoc.createElement("incrementQueryResponse");
		root.setAttribute("type", String.valueOf(isActive));
		responseDoc.appendChild(root);

		return responseDoc;

	}

	public static Document buildAvailableContainerReqDoc4Depoly(
			String serviceName, int deployNum) {
		logger.info("Ask SAManager for available container list...");
		Document doc = null;
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			doc = f.newDocumentBuilder().newDocument();
			Element element = doc
					.createElement("availableContainerRequestForDeployment");
			element.setAttribute("type", "WebService");

			Element name = doc.createElement("deployWSName");
			name.setTextContent(serviceName);

			Element deployNumElement = doc.createElement("deployNum");
			deployNumElement.setTextContent(String.valueOf(deployNum));

			element.appendChild(name);
			element.appendChild(deployNumElement);

			doc.appendChild(element);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Document buildAvailableContainerReqDoc4Acquistion(
			String serviceName, int deployNum, String serviceID) {
		logger.info("Ask SAManager for available container list...");
		Document doc = null;
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			doc = f.newDocumentBuilder().newDocument();
			Element element = doc
					.createElement("availableContainerRequestForDeployment");
			element.setAttribute("type", "WSReplicaAcquisition");

			Element serviceIDElement = doc.createElement("serviceID");
			serviceIDElement.setTextContent(serviceID);

			Element name = doc.createElement("deployWSName");
			name.setTextContent(serviceName);

			Element deployNumElement = doc.createElement("deployNum");
			deployNumElement.setTextContent(String.valueOf(deployNum));

			element.appendChild(name);
			element.appendChild(deployNumElement);
			element.appendChild(serviceIDElement);

			doc.appendChild(element);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Document buildSandboxFailureDoc(String error,
			String fileName, String userName)
			throws ParserConfigurationException {
		logger.info("Sandbox Failure��" + fileName);
		Document responseDoc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element responseElement = responseDoc.createElement("deployResponse");
		responseElement.setAttribute("type", "WebService");
		Element fileNameElement = responseDoc.createElement("serviceName");
		fileNameElement.setTextContent(fileName);
		Element serviceID = responseDoc.createElement("servieID");
		serviceID.setTextContent("sandbox-error");
		Element isSuccessful = responseDoc.createElement("isSuccessful");
		isSuccessful.setTextContent("false");
		Element deployResultInfo = responseDoc
				.createElement("deployResultInfo");
		deployResultInfo.setTextContent(error);
		Element userNameElement = responseDoc.createElement("userName");
		userNameElement.setTextContent(userName);

		responseElement.appendChild(fileNameElement);
		responseElement.appendChild(serviceID);
		responseElement.appendChild(isSuccessful);
		responseElement.appendChild(deployResultInfo);
		responseElement.appendChild(userNameElement);
		responseDoc.appendChild(responseElement);

		return responseDoc;
	}

	public static Document buildAuthenticationFailureDoc(String serviceID) {

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element feed = doc.createElement("undeployFeedback");
			feed.setAttribute("type", "WebService");
			Element id = doc.createElement("serviceID");
			id.setTextContent(serviceID);
			Element isSuccessful = doc.createElement("isSuccessful");
			isSuccessful.setTextContent("false");
			Element deployResultInfo = doc.createElement("undeployResultInfo");
			deployResultInfo
					.setTextContent("You are not allowed to undeploy this service for you are not the owner!");
			feed.appendChild(id);
			feed.appendChild(isSuccessful);
			feed.appendChild(deployResultInfo);
			doc.appendChild(feed);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	public static Document buildUndeployUrlsQueryDoc(String serviceID) {
		logger.info("Query replica address list from SAManager...");
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		try {
			builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element element = doc.createElement("undeployQueryRequest");
			element.setAttribute("type", "WebService");
			Element id = doc.createElement("serviceID");
			id.setTextContent(serviceID);
			element.appendChild(id);
			doc.appendChild(element);

			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document buildNoAvailableReplicaDoc(String serviceID)
			throws ParserConfigurationException {
		logger.info("There is no available replica�� " + serviceID);
		Document responseDoc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element element = responseDoc.createElement("undeployResponse");
		element.setAttribute("type", "WebService");

		Element id = responseDoc.createElement("serviceID");
		id.setTextContent(serviceID);
		Element isSuccessful = responseDoc.createElement("isSuccessful");
		isSuccessful.setTextContent("false");
		Element deployResultInfo = responseDoc
				.createElement("undeployResultInfo");
		deployResultInfo.setTextContent("There is no replicas for the ID: "
				+ serviceID);

		element.appendChild(id);
		element.appendChild(isSuccessful);
		element.appendChild(deployResultInfo);
		responseDoc.appendChild(element);

		return responseDoc;
	}

	// private static Options buildOptions() {
	//
	// Options options = new Options();
	// options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
	//
	// options.setProperty(Constants.Configuration.ENABLE_MTOM,
	// Constants.VALUE_TRUE);
	// options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
	// return options;
	// }

	@SuppressWarnings("unchecked")
	public static Document buildUndeployFb2BusDoc(ExchangeContext context) {
		logger.info("Send undeployment feedback to SAManager");

		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("undeployFeedback");
			root.setAttribute("type", "WebService");

			Element id = doc.createElement("serviceID");
			id.setTextContent((String) (context
					.getData(DeployConstants.SERVICEID)));

			Element isSuccessful = doc.createElement("isSuccessful");

			isSuccessful.setTextContent((String) context
					.getData(DeployConstants.ISSUCCESSFUL));

			Element undeployedUrls = doc.createElement("containerList");
			List<UndeployReplica> unDeployedReplica = (List<UndeployReplica>) context
					.getData(DeployConstants.UNDEPLOYEDREPLICA);
			int length = unDeployedReplica.size();
			undeployedUrls.setAttribute("length", String.valueOf(length));
			UndeployReplica tmpReplica = new UndeployReplica();
			for (Iterator<UndeployReplica> it = unDeployedReplica.iterator(); it
					.hasNext();) {
				tmpReplica = it.next();
				Element container = doc.createElement("container");
				container.setAttribute("id", tmpReplica.getContainerID());
				container.setAttribute("isSuccessful",
						String.valueOf(tmpReplica.isSuccessful()));
				undeployedUrls.appendChild(container);
			}
			root.appendChild(id);
			root.appendChild(isSuccessful);
			root.appendChild(undeployedUrls);
			doc.appendChild(root);

			return doc;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object buildUndeployResponseDoc(ExchangeContext context) {
		logger.info("Build undeployment feedback for the user...");
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		try {
			builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("undeployResponse");
			root.setAttribute("type", "WebService");

			Element serviceID = doc.createElement("seriviceID");
			serviceID.setTextContent((String) (context
					.getData(DeployConstants.SERVICEID)));

			Element isSuccessful = doc.createElement("isSuccessful");
			isSuccessful.setTextContent((String) context
					.getData(DeployConstants.ISSUCCESSFUL));

			Element info = doc.createElement("undeployResultInfo");
			info.setTextContent((String) context.getData(DeployConstants.INFO));

			root.appendChild(serviceID);
			root.appendChild(isSuccessful);
			root.appendChild(info);

			doc.appendChild(root);

			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static OMElement buildDeployUpdateOM(String serviceID,
			String serviceName, long timeCost,
			List<ContainerReplica> deployedResults) {
		logger.info("Build deployment update msg");

		int successDeployNum = deployedResults.size();

		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement element = omFactory.createOMElement("deploy", null);
		OMElement service = omFactory.createOMElement("service", null);
		service.addAttribute("id", serviceID, null);
		service.addAttribute("serviceName", serviceName, null);
		service.addAttribute("repetitionNum", String.valueOf(successDeployNum),
				null);
		service.addAttribute("timecost", String.valueOf(timeCost), null);

		for (int i = 0; i < successDeployNum; i++) {
			OMElement repetition = omFactory
					.createOMElement("repetition", null);
			repetition.addAttribute("invokeUrl", deployedResults.get(i)
					.getdeployUrl()
					.replaceFirst("SAManageService", serviceName), null);
			repetition.addAttribute("cpu",
					String.valueOf(deployedResults.get(i).getCpu()), null);
			repetition.addAttribute("memory",
					String.valueOf(deployedResults.get(i).getMemeory()), null);
			repetition.addAttribute("throughput",
					String.valueOf(deployedResults.get(i).getThroughput()),
					null);
			service.addChild(repetition);
		}

		element.addChild(service);

		return element;
	}

	public static OMElement buildUndeployUpdateOM(String serviceID,
			String serviceName, long timeCost,
			List<UndeployReplica> undeployedReplicas) {
		logger.info("Build undeployment update msg");

		int successDeployNum = undeployedReplicas.size();
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement element = omFactory.createOMElement("undeploy", null);
		OMElement service = omFactory.createOMElement("service", null);
		service.addAttribute("id", serviceID, null);
		service.addAttribute("serviceName", serviceName, null);
		service.addAttribute("repetitionNum", String.valueOf(successDeployNum),
				null);
		service.addAttribute("timecost", String.valueOf(timeCost), null);

		for (int i = 0; i < successDeployNum; i++) {
			OMElement repetition = omFactory
					.createOMElement("repetition", null);
			repetition.addAttribute("invokeUrl", undeployedReplicas.get(i)
					.getUndeployUrl(), null);
			service.addChild(repetition);
		}

		element.addChild(service);

		return element;

	}

	public static OMElement buildUndeployEnvelope(String fileName,
			String serviceID,String port,String userName) {

		OMFactory omFactory = OMAbstractFactory.getOMFactory();

		OMElement element = omFactory.createOMElement("undeployRequest", null);

		OMAttribute attr = omFactory.createOMAttribute("type", null,
				"WebService");
		element.addAttribute(attr);

		OMElement serviceName = omFactory.createOMElement("serviceName", null);
		serviceName.setText(fileName);
		element.addChild(serviceName);

		OMElement serviceIDOm = omFactory.createOMElement("serviceID", null);
		serviceIDOm.setText(serviceID);
		element.addChild(serviceIDOm);
		
		OMElement portOm = omFactory.createOMElement("port", null);
		portOm.setText(port);
		element.addChild(portOm);
		
		OMElement userOM = omFactory.createOMElement("userName",null);
		userOM.setText(userName);
		element.addChild(userOM);
		
		return element;
	}
}
