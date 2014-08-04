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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.thread;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;


public class Feedback4ContainerThread implements Runnable {
	private static final Log logger = LogFactory.getLog(Feedback4ContainerThread.class);
	private String userID;
	private String userType;
	private String wsdlUrl;
	private String operation;
	private String startTimeString;
	private long lastTime;
	private boolean isSuccessful;
	private OMElement soapBodyContent;
	private OMElement responseOmElement;
	
	public Feedback4ContainerThread(String userID, String userType,
			String wsdlUrl, String operation, String startTimeString,
			Long lastTime,boolean isSuccessful, OMElement soapBodyContent,
			OMElement responseOmElement) {
		this.userID = userID;
		this.userType = userType;
		this.wsdlUrl = wsdlUrl;
		this.operation = operation;
		this.startTimeString = startTimeString;
		this.lastTime = lastTime;
		this.isSuccessful = isSuccessful;
		this.soapBodyContent = soapBodyContent;
		this.responseOmElement = responseOmElement;
		
	}
	@Override
	public void run() {
		logger.info("Response for container");
		OMElement feedbackOMElment = bulidFeedback4Container(userID, userType, wsdlUrl, operation, startTimeString, lastTime, isSuccessful, soapBodyContent, responseOmElement);
		String containerUrlString = wsdlUrl.substring(0, wsdlUrl.indexOf(":", 7))+":8080/axis2/services/localSAManagerService/WebServiceCallingLogDecapsulation";
		try {
			sendFeedback2Container(feedbackOMElment,containerUrlString);
		} catch (AxisFault e1) {
			e1.printStackTrace();
		}
	}
	private OMElement bulidFeedback4Container(String userID, String userType,
			String wsdlUrl, String operation, String startTimeString,
			Long lastTime,boolean isSuccessful, OMElement soapBodyContent,
			OMElement responseOMElement) {
	
	  OMFactory fac = OMAbstractFactory.getOMFactory();
	  OMElement feedbackOmElement = fac.createOMElement(
			  WSInvokeConstants.WSINVOKATIONFEEDBACK4CONTAINER, null);
	  OMElement userOmElement = fac.createOMElement(WSInvokeConstants.USER, null);
	  userOmElement.addAttribute("ID", userID, null);
	  userOmElement.addAttribute(WSInvokeConstants.TYPE, userType, null);
	  OMElement urlOmElement = fac.createOMElement(WSInvokeConstants.URL, null);
	  urlOmElement.setText(wsdlUrl);
	  OMElement operationOmElement = fac.createOMElement(WSInvokeConstants.OPERATION, null);
	  operationOmElement.setText(operation);
	  OMElement timeOmElement =fac.createOMElement("time", null);
	  timeOmElement.addAttribute("startTime", startTimeString, null);
	  timeOmElement.addAttribute(WSInvokeConstants.LASTTIME, Long.toString(lastTime), null);
	  OMElement isSucessfulOmElement = fac.createOMElement(WSInvokeConstants.ISSUCCESSFUL, null);
	  isSucessfulOmElement.setText(Boolean.toString(isSuccessful));
	  OMElement requestOMElment = fac.createOMElement(WSInvokeConstants.REQUESTSOAP,null);
	  requestOMElment.addChild(soapBodyContent);
	  
	  feedbackOmElement.addChild(userOmElement);
	  feedbackOmElement.addChild(urlOmElement);
	  feedbackOmElement.addChild(operationOmElement);
	  feedbackOmElement.addChild(timeOmElement);
	  feedbackOmElement.addChild(isSucessfulOmElement);
	  feedbackOmElement.addChild(requestOMElment);
	  feedbackOmElement.addChild(responseOMElement);
	  
		return feedbackOmElement;
	}
	
	private void sendFeedback2Container(OMElement feedbackOMElment, String targetERPString) throws AxisFault {

		ServiceClient serviceClient = new ServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetERP = new EndpointReference(targetERPString);
		options.setTo(targetERP);
		serviceClient.setOptions(options);
		serviceClient.fireAndForget(feedbackOMElment);
	
	}
}
