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

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceFactory;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class ServerQueryForDeploymentInvoker extends Invoker{

	private Log logger = LogFactory.getLog(ServerQueryForDeploymentInvoker.class);
	
	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("Create the request document for ServiceDeployBusinessUnit!");
		String appType = (String)context.getData(ServerQueryForDeploymentBusinessUnit.QUERY_TYPE);
		String serviceId = (String)context.getData(ServerQueryForDeploymentBusinessUnit.SERVICE_ID);
		int newDeployed = (Integer)context.getData(ServerQueryForDeploymentBusinessUnit.NEW_DEPLOY_NUM);
		
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		
		try {
			
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("applianceDeploymentRequest");
			String applianceType = getApplianceTypeFromAppType(appType);
			
			root.setAttribute("type", applianceType);
			root.setAttribute("serviceId", serviceId);
			
			//the number to be deployed
			Element deployNumEle = doc.createElement("deployNum");
			deployNumEle.setTextContent(String.valueOf(newDeployed));
			root.appendChild(deployNumEle);
			
			doc.appendChild(root);
		
			return doc;
		
		} catch (ParserConfigurationException e) {
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Can't create the document!", context);
			ep.setSender(unit.getReceiver().getEndpoint());
			// e.setTargetService(service);
			throw ep;
		}
	}
	
	private String getApplianceTypeFromAppType(String appType){
		String applianceType;
		if(appType.equalsIgnoreCase("webservice")){
			applianceType = "axis2";
		}else if(appType.equalsIgnoreCase("app")){
			applianceType = "appserver";
		}else{
			logger.warn("Set the appliance type to the default value: axis2!");
			applianceType = "axis2";
		}
		
		return applianceType;
	}
	
	@Override
	public void handleResponse(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		
		logger.info("Receive the appliance deployment response from ApplianceDeploymentReceiver: " 
						+ XMLUtils.retrieveDocumentAsString(request));
		
		String serviceId = request.getDocumentElement().getAttribute("serviceId");
		if(serviceId == null){
			throw new MessageExchangeInvocationException("The service id from the ApplianceDeployment Response is null!");
		}
		
		String applianceType = request.getDocumentElement().getAttribute("type");
		if(applianceType == null){
			throw new MessageExchangeInvocationException("The appliance type from the ApplianceDeployment Response is null!");
		}
		
		if(request.getElementsByTagName("isSuccessful") == null 
				|| request.getElementsByTagName("isSuccessful").getLength() <= 0){
			throw new MessageExchangeInvocationException("The isSuccessful from the ApplianceDeployment Response is null!");
		}
		
		String isSuccessful = request.getElementsByTagName("isSuccessful").item(0).getTextContent();
		
		context.storeData(ServerQueryForDeploymentBusinessUnit.SERVICE_ID, serviceId);
		context.storeData(ServerQueryForDeploymentBusinessUnit.QUERY_TYPE, applianceType);
		context.storeData(ServerQueryForDeploymentBusinessUnit.IS_SUCCESS_DEPLOY, isSuccessful);
		
		if(isSuccessful.equals("true")){
			
			if(request.getElementsByTagName("appliances") == null 
					|| request.getElementsByTagName("appliances").getLength() <= 0){
				logger.warn("The response document misses the appliances element from ApplianceDeployment");
				throw new MessageExchangeInvocationException("The response document misses the appliances element from ApplianceDeployment");
			}
			
			Element appliances = (Element)request.getElementsByTagName("appliances").item(0);
			List<Appliance> newDeployeds = getNewDeployedApplianceList(appliances, applianceType);
			context.storeData(ServerQueryForDeploymentBusinessUnit.NEW_DEPLOYEDS, newDeployeds);
			
		}else{
			
			String exception;
			if(request.getElementsByTagName("desp") == null 
					|| request.getElementsByTagName("desp").getLength() <= 0){
				logger.warn("The response document misses the exception description element from ApplianceDeployment");
				exception = "The response document misses the exception description element from ApplianceDeployment";
			
			}else{
				Element desp = (Element)request.getElementsByTagName("desp").item(0);
				exception = desp.getTextContent();
			}
			
			context.storeData(ServerQueryForDeploymentBusinessUnit.DEPLOYED_EXCEP, exception);
			
			
		}
		
		this.unit.onReceiveResponse(context);
	}
	
	protected List<Appliance> getNewDeployedApplianceList(Element appliances, String applianceType)
								throws MessageExchangeInvocationException{
		
		NodeList applianceList = appliances.getElementsByTagName("appliance");
		if(applianceList == null){
			logger.warn("The appliance List is missed from the ApplianceDeployment Response Message");
			throw new MessageExchangeInvocationException("The appliance List is missed from the ApplianceDeployment Response Message");
		}
		
		List<Appliance> newDeployeds = new ArrayList<Appliance>();
		for(int i = 0; i < applianceList.getLength(); i++){
			Element applianceElement = (Element)applianceList.item(i);
			Appliance appliance = createNewApplianceInstance(applianceElement, applianceType);
			newDeployeds.add(appliance);
		}
		
		return newDeployeds;
	}
	
	protected Appliance createNewApplianceInstance(Element applianceElement, String applianceType)
							throws MessageExchangeInvocationException{
		Appliance appliance = ApplianceFactory.createAppliance(applianceType);
		
		String applianceId = applianceElement.getAttribute("id");
		String port = applianceElement.getAttribute("port");
		String deployUrl = applianceElement.getAttribute("deployUrl");
		String deployOperation = applianceElement.getAttribute("deployOperation");
		
		String cpu = applianceElement.getAttribute("cpu");
		String memory = applianceElement.getAttribute("memory");
		String throughput = applianceElement.getAttribute("throughput");
		
		
		if(applianceId == null){
			logger.warn("The appliance id is missing from the ApplianceDeployment response! ");
			throw new MessageExchangeInvocationException("The appliance id is missing from the ApplianceDeployment response! ");
		}
		
		appliance.getDesp().setId(applianceId);
		
		if(deployUrl == null){
			logger.warn("The appliance deploy URL is missing from the ApplianceDeployment response! ");
			throw new MessageExchangeInvocationException("The appliance deploy URL is missing from the ApplianceDeployment response! ");
		}
		
		appliance.getDesp().setDeployEPR(deployUrl);
		
		appliance.getDesp().setPort(port);
		appliance.getDesp().setDeployOperation(deployOperation);
		
		appliance.getStatus().setCpuRate(Float.valueOf(cpu));
		appliance.getStatus().setDeployedAmount(1);
		appliance.getStatus().setMemoryfloat(Float.valueOf(memory));
		appliance.getStatus().setPort(Double.valueOf(throughput));
		
		
		return appliance;
	}
	
}
