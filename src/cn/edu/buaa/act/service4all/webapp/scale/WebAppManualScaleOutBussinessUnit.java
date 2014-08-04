package cn.edu.buaa.act.service4all.webapp.scale;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageContextException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.appliance.AppServer;
import cn.edu.buaa.act.service4all.webapp.database.DBHandler;
import cn.edu.buaa.act.service4all.webapp.deployment.DeployUtil;
import cn.edu.buaa.act.service4all.webapp.deployment.loadbalance.PerformanceSortor;
import cn.edu.buaa.act.service4all.webapp.qualification.UserQualification;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;


public class WebAppManualScaleOutBussinessUnit extends BusinessUnit {

	private Log logger = LogFactory.getLog(WebAppManualScaleOutBussinessUnit.class);
	
	private  static final double[] weight = {Constants.weights[0],Constants.weights[1],Constants.weights[2]};
	
	@Override
	public void dispatch( ExchangeContext context ) {
		queryServer(context);
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {
		logger.info("receive server query result!Start to process the response.");
		Element urls = ((Element) context.getData(Constants.CONTAINER_LIST));
		int availableContainerCount = Integer.valueOf(urls
				.getAttribute("length")); // count of available web
		// servers
		String serviceID = (String) context.getData(Constants.SCALE_OUT_ID);
		String serviceName = (String) context.getData(Constants.SERVICE_NAME);
		if (availableContainerCount < 1) {// no available web servers
			String errorString = "There is no available container!!";
			try {
				context.storeData(Constants.MANUAL_SCALEOUT_RESPONSE, DocBuilder
						.buildNoContainerDoc(serviceID, serviceName,
								errorString));
				this.getReceiver().sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
		} else {
			ArrayList<AppServer> containers = parseUrls(urls);
			ArrayList<AppServer> serverToDeploy;
			List<AppServer> deployedServer = new ArrayList<AppServer>();
			OMElement response = null;
			int deployNum = Integer.parseInt((String) context
					.getData(Constants.DEPLOY_NUM));
			if (availableContainerCount > deployNum) {
				serverToDeploy = sortServer(containers);
			} else {
				logger.warn( "Availble container number is "
						+ availableContainerCount + ", but require "
						+ deployNum );
				serverToDeploy = containers;
			}
			int deployed = 0;
			// deploy for only once
			ArrayList<String> invokeUrls = new ArrayList<String>();
			for (int i = 0; i < deployNum; i++) {
				String invokeUrl = "";
				OMElement request = buildRequestOM(context,
						serverToDeploy.get(i).getPort());
				try {
					logger.info("begin to deploy the " + i + " replica on "
							+ serverToDeploy.get(i).getUrl());
					response = DeployUtil.deploy(request, serverToDeploy.get(i)
							.getUrl());
					if (isDeploySucc(response)) {
						deployedServer.add(serverToDeploy.get(i));
						deployed++;
						logger.info("Successfully deploy the " + i
								+ " replica.");
					} else {
						logger.info("Fail to deploy the " + i + "replica");
					}

					invokeUrl = getUrl(response);
					invokeUrls.add(invokeUrl);
					context.storeData("InvokeUrl", invokeUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (deployed == deployNum) {
				context.storeData(Constants.IS_SUCC, "true");
				context.storeData(Constants.INFO, "Successful");
				// write the result to the databse
				logger.info("insert into databse");
				DBHandler handler = new DBHandler();
				String userName = (String) context.getData(Constants.USER_NAME);
				for (int i = 0; i < deployNum; i++) {
					// TODO 写数据库可以用之前的接口
					handler.insertNewApp(serviceID, serviceName,
							invokeUrls.get(i), deployNum, "active", userName);
				}
				
				// notify the qualify component
				logger.info("notify the qualify component.");
				UserQualification.deployNotification(userName, serviceID);
			} else {
				context.storeData(Constants.IS_SUCC, "false");
			}
			context.storeData(Constants.DEPLOY_RESULT, deployedServer);
			feedback(context);
		}
	}
	
	private ArrayList<AppServer> parseUrls(Element urls) {
		//TODO 用之前的接口就行，剔除部署有同名容器的功能在Global SA里已经做了
		ArrayList<AppServer> containers = new ArrayList<AppServer>();
		Element urlElement;
		for (int i = 0; i < urls.getChildNodes().getLength(); i++) {
			urlElement = (Element) urls.getElementsByTagName(
					Constants.CONTAINER).item(i);
			String deployUrl = urlElement.getAttribute(Constants.DEPLOY_URL);
			String id = urlElement.getAttribute(Constants.ID);
			double cpu = Double.parseDouble(urlElement
					.getAttribute(Constants.CPU));
			double memory = Double.parseDouble(urlElement
					.getAttribute(Constants.MEMORY));
			double throughput = Double.parseDouble(urlElement
					.getAttribute(Constants.THROUGHPUT));
			AppServer server = new AppServer(deployUrl, id, cpu, memory,
					throughput);
			containers.add(server);
		}
		return containers;
	}
	
	private ArrayList<AppServer> sortServer(
			ArrayList<AppServer> candidates) {
		int size = candidates.size();
		AppServer[] appServers =  (AppServer[])candidates.toArray(new AppServer[size]);
		PerformanceSortor sortor = new PerformanceSortor(weight, appServers);
		int[] rank=sortor.sort();
		
		ArrayList<AppServer> tempAppServers = new ArrayList<AppServer>();
		for(int i=0;i<size;i++)
		{			
			tempAppServers.add(appServers[rank[i]]);
		}
		return tempAppServers;
	}
	
	private OMElement buildRequestOM(ExchangeContext context, String port) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNamespace = fac.createOMNamespace("", "");
		OMElement serviceIDOM = fac.createOMElement(Constants.SERVICE_ID,
				omNamespace);
		serviceIDOM.setText((String) context.getData(Constants.SCALE_OUT_ID));
		OMElement portOM = fac.createOMElement(Constants.PORT, omNamespace);
		portOM.setText(port);
		OMElement request = (OMElement) context.getData(Constants.DEPLOY_WAR_REQUEST);
		request.addChild(serviceIDOM);
		request.addChild(portOM);
		return request;
	}

	private void queryServer(ExchangeContext context) {
		Invoker invoker = this.getInvokers().get("WebAppManualScaleOutServerQueryInvoker");
		if (null == invoker) {
			logger.info("can not get invoker WebAppManualScaleOutServerQueryInvoker");
			try {
				throw new MessageContextException(
						"can not get invoker WebAppManualScaleOutServerQueryInvoker.");
			} catch (MessageContextException e) {
				e.printStackTrace();
			}
		}
		try {
			invoker.sendRequestExchange(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDeploySucc(OMElement response) {
		OMElement resp = response.getFirstElement();
		OMElement element;
		boolean flag = false;
		for (@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = resp.getChildElements(); iter.hasNext();) {
			element = iter.next();
			if (element.getLocalName().equals(Constants.IS_SUCC)) {
				if ("true".equalsIgnoreCase(element.getText())) {
					flag = true;
				}
			}
		}
		return flag;
	}
	
	private String getUrl(OMElement response) {
		OMElement resp = response.getFirstElement();
		OMElement element;
		String url = "";
		for (@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = resp.getChildElements(); iter.hasNext();) {
			element = iter.next();
			if (element.getLocalName().equals("invokeUrl")) {
				url = element.getText();
				break;
			}
		}
		logger.info("the invoke url is " + url);
		return url;
	}
	
	private void feedback(ExchangeContext context) {
		Invoker invoker = this.getInvokers().get("WebAppManualScaleOutFeedbackInvoker");
		if (null == invoker) {
			logger.info("can not get invoker WebAppManualScaleOutFeedbackInvoker");
			try {
				throw new MessageContextException(
						"can not get invoker WebAppManualScaleOutFeedbackInvoker.");
			} catch (MessageContextException e) {
				e.printStackTrace();
			}
		}
		try {
			invoker.sendRequestExchange(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}
}
