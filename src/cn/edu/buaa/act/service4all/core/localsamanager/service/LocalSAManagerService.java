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
package cn.edu.buaa.act.service4all.core.localsamanager.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.i18n.Messages;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import cn.edu.buaa.act.service4all.core.localsamanager.app.manager.WebAppManager;
import cn.edu.buaa.act.service4all.core.localsamanager.app.manager.WebServiceManager;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.AppServerManager;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.Axis2Manager;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.BpmnManage;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.log.LogManager;
import cn.edu.buaa.act.service4all.core.localsamanager.messagehandler.SAMsgDecapsulate;
import cn.edu.buaa.act.service4all.core.localsamanager.timer.PCTimer;

public class LocalSAManagerService {
	
//	public static ServiceContext serviceContext;
	public static ConfigurationContext axisCtx;
	public static AxisConfiguration axisConfig;
//	public static ServiceContext serviceCtx;
	private static Logger logger = Logger.getLogger( LocalSAManagerService.class
			.getName() );

	public void initial(){
		PropertyConfigurator.configure( System.getProperty( "user.dir" )
				.replace( '\\', '/' )
				+ "/log/" + "log4j2.properties" );

		PCTimer pcTimer = new PCTimer();
		pcTimer.start();
	}
	
	public OMElement getSystemLog(){
		LogManager logManager = new LogManager();
		return logManager.getSystemLog();
	}
	
	public OMElement getUserLog(String userID,
			String appliancePort, String webServiceName){
		return new LogManager().getUserLog( userID,appliancePort, webServiceName );
	}
	public void setInvokeUser(OMElement userMsg){
		new LogManager().setInvoker(userMsg);
	}
	
	
	public OMElement saDeploy( OMElement deployMsg ) {
		long beginTime = System.currentTimeMillis();
		switch (new SAMsgDecapsulate().deployMsgDecp( deployMsg )) {
		case Constants.AppServerDeploy:
			AppServerManager appManager =  AppServerManager.getInstance();
			return appManager.deploy();			
		case Constants.Axis2Deploy:
			Axis2Manager axis2Manager = Axis2Manager.getInstance();
			return axis2Manager.deploy();
		case Constants.BpmnDeploy:
			BpmnManage bpmnManage = new BpmnManage();
			return bpmnManage.deploy();
//			String deployPort = 
//			long saDeployTime = System.currentTimeMillis() - beginTime;
//			logger.info("[System Information][Management Operation][BpmnEngine Deploy]"
//					+ "BpmnEngine部署成功，其反馈消息为"
//					+ BpmnManage.bpmnDeploymentOMElement.toString());
//			try {
//				InetAddress addr = InetAddress.getLocalHost();
//				String hostIP = addr.getHostAddress().toString();
//				OMFactory omf = OMAbstractFactory.getOMFactory();
//				OMElement saUpdateMsg = omf.createOMElement("update", null);
//				OMAttribute hostIPAttr = omf.createOMAttribute("ip", null,
//						hostIP);
//				saUpdateMsg.addAttribute(hostIPAttr);
//				OMElement deployElement = omf.createOMElement("deploy", null);
//				OMAttribute applianceType = omf.createOMAttribute("type", null,
//						"bpmnengine");
//				OMAttribute appliancePort = omf.createOMAttribute("port", null,
//						deployPort);
//				OMAttribute deployTime = omf.createOMAttribute("startTime",
//						null, Long.toString(saDeployTime));
//				deployElement.addAttribute(applianceType);
//				deployElement.addAttribute(appliancePort);
//				deployElement.addAttribute(deployTime);
//				saUpdateMsg.addChild(deployElement);
////				sendUpdateMsg(sendURL, saUpdateMsg, "saDeployUpdate");
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			}
//			// System.out.println("非常重要BpmnEngine部署"
//			// + BpmnManage.bpmnDeploymentOMElement.toString());
//			// System.out.println("bpmn is deployed completely");
//			return BpmnManage.bpmnDeploymentOMElement;
		default:
			return null;
		}
	}
	
	public OMElement saUnDeploy( OMElement unDeployMsg ) {
		String undeployPort = null;
		if (unDeployMsg != null) {
			@SuppressWarnings("unchecked")
			Iterator<OMElement> it = unDeployMsg.getChildren();
			while (it.hasNext()) {
				OMElement msgItem = (OMElement) it.next();
				if (msgItem.getLocalName().equalsIgnoreCase( "port" )) {
					undeployPort = msgItem.getText();
				}
			}
		}
		switch (new SAMsgDecapsulate().unDeployMsgDecp( unDeployMsg )) {
		case Constants.AppServerUnDeploy:
			return  AppServerManager.getInstance().undeploy(undeployPort);
		case Constants.Axis2UnDeploy:
			Axis2Manager axis2Manage = Axis2Manager.getInstance();
			axis2Manage.undeploy(undeployPort);
		default:
			return null;
		}
	}
	
	public void restart( OMElement restartMsg ) {
		String restartPort = null;
		if (restartPort != null) {
			@SuppressWarnings("unchecked")
			Iterator<OMElement> it = restartMsg.getChildren();
			while (it.hasNext()) {
				OMElement msgItem = (OMElement) it.next();
				if (msgItem.getLocalName().equalsIgnoreCase( "port" )) {
					restartPort = msgItem.getText();
				}
			}
		}
		switch (new SAMsgDecapsulate().reStartMsgDecp( restartMsg )) {
		case Constants.VmHostRestart:
			break;
		case Constants.AppServerRestart:
			AppServerManager appManager = AppServerManager.getInstance();
			appManager.restart(restartPort);
			break;
		case Constants.Axis2Restart:
			Axis2Manager axis2Manager = Axis2Manager.getInstance();
			axis2Manager.restart(restartPort);
			break;
		default:
			break;
		}
	}
	
//	public OMElement localWSDeploy( String wsName, String port, String wsID ) {
//		WebServiceManager webServiceManager = new WebServiceManager();
//		webServiceManager.localWSDeploy( wsName, port, wsID );
//		return null;
//	}
	

	public OMElement deployService( String serviceName, String serviceID )
			throws AxisFault {
		return deployServiceGroup( serviceName );
	}
	
	public OMElement deployServiceGroup( String serviceGroupName ) {
		DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig
				.getConfigurator();
		deploymentEngine.loadServices();
		if (!serviceGroupName.endsWith( ".aar" )) {
			serviceGroupName += ".aar";
		}

		int index = serviceGroupName.indexOf( ".aar" );
		if (index != -1)
			serviceGroupName = serviceGroupName.substring( 0, index );
		AxisServiceGroup asGroup = axisCtx.getAxisConfiguration()
				.getServiceGroup( serviceGroupName );
		String deployIsSuccesful = "true";
		if (asGroup == null) {
			String message = Messages.getMessage( "invalidservicegroupname",
					serviceGroupName );
			deployIsSuccesful = "false";
			OMFactory omFactory = OMAbstractFactory.getOMFactory();
			OMElement deployResponse = omFactory.createOMElement(
					"deployResponse", null );
			OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
					"Type", null, "WebService" );
			deployResponse.addAttribute( deployResponseTypeAttr );
			OMElement isSuccessfulElement = omFactory.createOMElement(
					"isSuccessful", null );
			isSuccessfulElement.setText( deployIsSuccesful );
			OMElement deployResultInfoElement = omFactory.createOMElement(
					"deployResultInfo", null );
			deployResultInfoElement
					.setText( "Web Service's type is not supported" );
			deployResponse.addChild( isSuccessfulElement );
			deployResponse.addChild( deployResultInfoElement );
			AxisFault fault = new AxisFault( Messages.getMessage(
					"invalidservicegroupname", serviceGroupName ) );
			logger.error( message, fault );
			return deployResponse;
		}
		Iterator<?> itsgrp = axisConfig.getServiceGroups();
		while (itsgrp.hasNext()) {
			AxisServiceGroup axisServiceGroup = (AxisServiceGroup) itsgrp
					.next();
			if (axisServiceGroup == null)
				logger.warn( "null" );
			if (axisServiceGroup.getDocumentation() == null) {
				Iterator<?> deployedServiceIt = axisServiceGroup.getServices();
				while (deployedServiceIt.hasNext()) {
					AxisService DeployingService = (AxisService) deployedServiceIt
							.next();					
					DeployingService.getEndpointURL();

				}
			}
		}
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement deployResponse = omFactory.createOMElement( "deployResponse",
				null );
		OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
				"Type", null, "WebService" );
		deployResponse.addAttribute( deployResponseTypeAttr );
		OMElement serviceNameElement = omFactory.createOMElement(
				"serviceName", null );
		serviceNameElement.setText( serviceGroupName );

		OMElement isSuccessfulElement = omFactory.createOMElement(
				"isSuccessful", null );
		isSuccessfulElement.setText( deployIsSuccesful );
		OMElement deployResultInfoElement = omFactory.createOMElement(
				"deployResultInfo", null );
		OMElement serviceWSDLElement = omFactory.createOMElement( "wsdlUrl",
				null );
		deployResponse.addChild( serviceNameElement );
		deployResponse.addChild( isSuccessfulElement );
		deployResponse.addChild( serviceWSDLElement );
		deployResponse.addChild( deployResultInfoElement );

		return deployResponse;
	}
	
	public OMElement deployService(OMElement deployRequest) throws AxisFault{
		return WebServiceManager.getInstance().deploy(deployRequest);
	}
	public OMElement undeployService(OMElement undeployRequest) throws AxisFault{
		return WebServiceManager.getInstance().undeploy(undeployRequest);
	}
	public OMElement deployWebApp(OMElement deployReuqest) throws XMLStreamException{
		return WebAppManager.getInstance().deploy(deployReuqest);
	}
	public OMElement undeployWebApp(OMElement request) throws XMLStreamException{
		return WebAppManager.getInstance().undeploy(request);
	}
	
	public static void main(String[] args) {
		new LocalSAManagerService().initial();
	}
}
