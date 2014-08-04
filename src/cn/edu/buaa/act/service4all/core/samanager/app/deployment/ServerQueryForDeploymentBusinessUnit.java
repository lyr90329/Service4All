/*
*
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
*
*/
package cn.edu.buaa.act.service4all.core.samanager.app.deployment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;

public class ServerQueryForDeploymentBusinessUnit extends SAManagerBusinessUnit {

	protected Log logger = LogFactory
			.getLog(ServerQueryForDeploymentBusinessUnit.class);

	public final static String QUERY_TYPE = "queryType";
	public final static String SERVICENAME = "serviceName";
	public final static String QUERY_RESULT = "queryResults";
	public final static String SERVICE_ID = "serviceId";
	public final static String DEPLOY_NUM = "deployNum";
	public final static String NEW_DEPLOY_NUM = "new deployed num";

	public final static String IS_SUCCESS_DEPLOY = "isSuccessful";
	public final static String NEW_DEPLOYEDS = "new deployeds";
	public final static String DEPLOYED_EXCEP = "deployment exception";
	
	public final static String SCALE_OUT_ID = "scale_out_id";
	public final static String IS_SCALE_OUT = "is_scale_out";
	
	public final static String JOB_ID = "jobId";

	protected Map<String, AppDeploymentEvent> events = new HashMap<String, AppDeploymentEvent>();

	@Override
	public void dispatch(ExchangeContext context) {
		AppDeploymentEvent deploymentEvent;
		try {
			synchronized (this) {
				deploymentEvent = createAppDeploymentEvent(context);
				List<AppListener> ls = this.getAppListeners();
				for (AppListener l : ls) {
					try {

						logger.info("Use the app listener: "
								+ l.getClass().getSimpleName());

						if(context.getData( IS_SCALE_OUT ) == null ||
								((String)context.getData( IS_SCALE_OUT )).equals( "false" )){
							l.queryApplianceForDeployment(deploymentEvent);
						}
						else {
							deploymentEvent
									.setDeployedServiceId( (String) context
											.getData( SCALE_OUT_ID ) );
							l.queryApplianceForScaleout( deploymentEvent );
						}
						

					} catch (AppException e) {
						logger.warn(e.getMessage());
						Document excepResponse = createExceptionMessage("There is some exception when handling the event : "
								+ e.getMessage());
						getReceiver().sendResponseMessage(excepResponse,
								context);
						return;
					}
				}
			}
			// parse the event and send the response message
			dynamicDeployment(deploymentEvent, context);
		} catch (AppException e) {
			this.handleInvocationException(new MessageExchangeInvocationException(
					e.getMessage()));
		}
	}

	protected synchronized void dynamicDeployment(AppDeploymentEvent event,
			ExchangeContext context) throws AppException {

		int newDeployed;
		int wantedDeployed = Integer.valueOf((String) context
				.getData(DEPLOY_NUM));

		String serviceId = event.getDeployedServiceId();
		context.storeData(SERVICE_ID, serviceId);

		// HostManager hostManager = (HostManager) this.getApplianceManager();
		// ApplianceManager axis2Manager = hostManager
		// .getApplianceManager("axis2");
		List<Appliance> queryResults = event.getQueryResults();
		
		if (queryResults == null || queryResults.size() <= 0) {
			logger.info("There is no available appliances for deployment!");
			newDeployed = wantedDeployed;
		} else {
			//wantedDeployed表示要部署的数量，queryResults.size()表示已有的空闲容器数量
			//newDeployed表示要重新起的容器数量
			newDeployed = wantedDeployed - queryResults.size();
		}
		if (newDeployed <= 0) {

			logger.info("There is no necessary to deploy new appliances");
			sendResponse(event, context);

		} else {

			logger.info("Need to deploy  " + newDeployed
					+ " appliances when dynamic deployment!");

			context.storeData(NEW_DEPLOY_NUM, newDeployed);
			Invoker invoker = getDeploymentInvoker();
			if (invoker == null) {
				logger.warn("There is no available invokers");
				throw new AppException("There is no available invokers");
			}

			// invoke the invoker for appliance deployment
			try {
				invoker.sendRequestExchange(context);
				//add the relative deploy event into the map
				this.addAppDeploymentEvent(event);

			} catch (MessageExchangeInvocationException e) {
				this.handleInvocationException(e);
			}
		}
	}

	/**
	 * those which extend this class should override this method to load the specific invoker
	 * @return
	 */
	protected Invoker getDeploymentInvoker() {
		return this.getInvokers().get("ServerQueryForDeploymentInvoker");
	}

	protected AppDeploymentEvent createAppDeploymentEvent(
			ExchangeContext context) throws AppException {

		String type = (String) context.getData(QUERY_TYPE);
		String deployNum = (String) context.getData(DEPLOY_NUM);
		String deployName = (String) context.getData(SERVICENAME);
		String serviceId=(String) context.getData(SERVICE_ID);
		// String serviceName = (String)context.getData(SERVICENAME);

		if (type == null) {
			logger.warn("The query type and the query serviceName is null!");
			// Document excepResponse =
			// createExceptionMessage("The query type and the query serviceName is null!");
			// getReceiver().sendResponseMessage(excepResponse, context);
			throw new AppException(
					"The query type and the query serviceName is null!");
		}

		// generate the server query event
		AppDeploymentEvent event = new AppDeploymentEvent();
		event.setDeployNum(Integer.valueOf(deployNum));
		event.setDeployedServiceName(deployName);
		if(serviceId!=null)
		{
			event.setDeployedServiceId(serviceId);
		}
		// event.setDeployedServiceName(serviceName);

		return event;
	}

	protected void sendResponse(AppDeploymentEvent event,
			ExchangeContext context) {

		List<Appliance> appliances = event.getQueryResults();
		String appId = event.getDeployedServiceId();

		logger.info("Send the response for app deployment query: " + appId);
		context.storeData(QUERY_RESULT, appliances);
		context.storeData(SERVICE_ID, appId);

		doSend(context);

	}

	protected void doSend(ExchangeContext context) {
		try {
			getReceiver().sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {

		String serviceId = (String) context
				.getData(ServerQueryForDeploymentBusinessUnit.SERVICE_ID);
		boolean isSuccessful = Boolean
				.valueOf((String) context
						.getData(ServerQueryForDeploymentBusinessUnit.IS_SUCCESS_DEPLOY));

		AppDeploymentEvent event = getAppDeploymentEvent(serviceId);
		if (event == null) {
			logger.warn("Miss the serivceId("
					+ serviceId
					+ ")'s AppDeploymentEvent when receiving the appliance response message");
			Document excepDoc = createExceptionMessage("Miss the serivceId("
					+ serviceId
					+ ")'s AppDeploymentEvent when receiving the appliance response message");
			this.getReceiver().sendResponseMessage(excepDoc, context);

			return;
		}

		List<Appliance> appliances = event.getQueryResults();

		if (isSuccessful) {

			@SuppressWarnings("unchecked")
			List<Appliance> newDeployeds = (List<Appliance>) context
					.getData(ServerQueryForDeploymentBusinessUnit.NEW_DEPLOYEDS);
			appliances.addAll(newDeployeds);

			context.storeData(
					ServerQueryForDeploymentBusinessUnit.QUERY_RESULT,
					appliances);

		}
		if (appliances == null || appliances.size() <= 0) {
			logger.warn("There is no available appliance for application deployment");
			// set the result to be failed
		}
		doSend(context);
	}

	protected Document createExceptionMessage(String message) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warn("Can't create the document");
			return null;
		}
		Document document = builder.newDocument();
		Element root = document.createElement("availableContainerResponse");
		Element excep = document.createElement("exception");
		excep.setTextContent(message);
		root.appendChild(excep);
		document.appendChild(root);

		return document;
	}

	protected AppDeploymentEvent getAppDeploymentEvent(String serviceId) {
		synchronized (events) {
			return events.get(serviceId);
		}
	}

	protected void addAppDeploymentEvent(AppDeploymentEvent event) {
		synchronized (events) {
			events.put(event.getDeployedServiceId(), event);
		}
	}

	protected void removeAppDeploymentEvent(String serviceId) {
		synchronized (events) {
			events.remove(serviceId);
		}
	}
}
