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
package cn.edu.buaa.act.service4all.core.localsamanager.app.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.i18n.Messages;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.app.WebService;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.ShareTomcatList;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;


@SuppressWarnings("unchecked")
public class WebServiceManager extends AppManager {

	private static Logger logger = Logger.getLogger( WebServiceManager.class );
	
	private static WebServiceManager webServiceManager;

	public synchronized static WebServiceManager getInstance(){
		if(webServiceManager == null){
			webServiceManager = new WebServiceManager();
		}
		return webServiceManager;
	}

	private WebServiceManager(){
		super();
	}

	@Override
	public OMElement deploy( OMElement request ) {
		try {
			return deployWebService( request );
		} catch (AxisFault e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public OMElement undeploy( OMElement undeployServiceRequset ) {
		try {
			return undeployWebService( undeployServiceRequset );
		} catch (AxisFault e) {
			e.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings("unused")
	private OMElement localWSDeploy( String wsName, String port, String wsID ) {
		long beginTime = System.currentTimeMillis();
		long deployTime = 0;
		String localRepoPath = Constants.REPOSITORY;
		File localRepoFolder = new File( localRepoPath );
		Iterator<TomcatInfo> it = ShareTomcatList.getNewInstance()
				.getTomcatInfoList();
		String deployPathOut = null;
		String fileSize = null;
		String hostIP = Constants.LOCAL_IP_WITHOUT_HTTP;
		
		EndpointReference targetEPR;
		while (it.hasNext()) {
			TomcatInfo tomcatInfo = it.next();
			if (String.valueOf( tomcatInfo.getPort() ).equals( port )) {
				deployPathOut = tomcatInfo.getTomcatPath()
						+ "/webapps/axis2/WEB-INF/services/";
			}
		}
		if(!wsName.endsWith( ".aar" )){
			wsName += ".aar";
		}
		if (localRepoFolder.exists() && localRepoFolder.isDirectory()) {
			boolean isFound = false;

			String[] repoWS = localRepoFolder.list();
			for (int i = 0; i < repoWS.length; i++) {
//				logger.info( repoWS[i] +" " + wsName);
				if (repoWS[i].equalsIgnoreCase( wsName )) {
					File wsToDeploy = new File( localRepoPath + repoWS[i] );
					try {
						// fileSize = Long.toString( GetFileSize
						// .getFileSizes( wsToDeploy ) );
						fileSize = Long.toString( FileManipulationFactory
								.newFileSizeObtainer()
								.getFileSizes( wsToDeploy ) );
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						// CopyFileAndDir.copyWSFile( wsToDeploy, deployPathOut
						// + wsName + ".aar" );
						FileManipulationFactory
								.newFileReproduction().copyAppFile(
										wsToDeploy,
										deployPathOut + wsName);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
//			while (!isFound) {
//				RPCServiceClient serviceClient = null;
//				OMElement response = null;
//				try {
//					serviceClient = new RPCServiceClient();
//					Options options = serviceClient.getOptions();
//					Object[] opAddEntryArgs = new Object[] { wsName };
//					targetEPR = new EndpointReference( "http://" + hostIP + ":"
//							+ port + "/axis2/services/SAManageService/" );
//					options.setTo( targetEPR );
//					options.setAction( "urn:isWSDeploySuccessful" );
//					QName opAddEntry = new QName(
//							"http://manageServices.serviceCloud.sdp.act.org.cn",
//							"isWSDeploySuccessful" );
//					response = serviceClient.invokeBlocking( opAddEntry,
//							opAddEntryArgs );
//					serviceClient.cleanupTransport();
//					if (response.getFirstElement().getText().equalsIgnoreCase(
//							"true" ))
//					{
//						isFound = true;
//						break;
//					}
//				} catch (AxisFault e) {
//					e.printStackTrace();
//				}
//			}
			deployTime = System.currentTimeMillis() - beginTime;
			//no need to notify update now
//			RPCServiceClient serviceClient;
//			try {
//				serviceClient = new RPCServiceClient();
//				Options options = serviceClient.getOptions();
//				EndpointReference target = new EndpointReference( "http://"
//						+ hostIP
//						+ ":8080/axis2/services/SAManageService/wsRelicaUpdate" );
//				options.setTo( target );
//				options.setAction( "urn:wsRelicaUpdate" );
//				serviceClient.setOptions( options );
//				QName opAddEntry = new QName(
//						"http://manageServices.serviceCloud.sdp.act.org.cn",
//						"wsRelicaUpdate" );
//				Object[] opAddEntryArgs = new Object[] { wsName, "add" };
//				serviceClient.invokeBlocking( opAddEntry, opAddEntryArgs );
//				serviceClient.cleanupTransport();
//
//				OMFactory omf = OMAbstractFactory.getOMFactory();
//				OMElement wsUpdateMsg = omf.createOMElement( "update", null );
//				OMAttribute hostIPAttr = omf.createOMAttribute( "ip", null,
//						hostIP );
//				wsUpdateMsg.addAttribute( hostIPAttr );
//
//				OMElement deployElement = omf.createOMElement( "deploy", null );
//				OMAttribute typeAttr = omf.createOMAttribute( "type", null,
//						"repetition" );
//				OMAttribute portAttr = omf.createOMAttribute( "port", null,
//						port );
//
//				deployElement.addAttribute( typeAttr );
//				deployElement.addAttribute( portAttr );
//				OMElement repetitionElement = omf.createOMElement(
//						"repetition", null );
//				OMAttribute appNameAttr = omf.createOMAttribute( "appName",
//						null, wsName );
//				OMAttribute sizeAttr = omf.createOMAttribute( "size", null,
//						fileSize );
//				OMAttribute deployTimeAttr = omf.createOMAttribute(
//						"deployTime", null, Long.toString( deployTime ) );
//				OMAttribute idAttr = omf.createOMAttribute( "id", null, wsID );
//
//				repetitionElement.addAttribute( appNameAttr );
//				repetitionElement.addAttribute( sizeAttr );
//				repetitionElement.addAttribute( deployTimeAttr );
//				repetitionElement.addAttribute( idAttr );
//
//				deployElement.addChild( repetitionElement );
//				wsUpdateMsg.addChild( deployElement );
//				return wsUpdateMsg;
//				// sendUpdateMsg( sendURL, wsUpdateMsg, "default" );
//			} catch (AxisFault e) {
//				e.printStackTrace();
//			}
		}
		return null;
	}


	private OMElement deployWebService( OMElement deployRequest )
			throws AxisFault {
		String serviceName = "";
		String port = "";
		String isSuccessful = "";
		String serviceID = "";

		Iterator loadRemoteServiceResponseIt = loadRemoteService( deployRequest )
				.getChildElements();
		while (loadRemoteServiceResponseIt.hasNext()) {
			OMElement loadRemoteServiceResponse = (OMElement) loadRemoteServiceResponseIt
					.next();
			if (loadRemoteServiceResponse.getLocalName().equalsIgnoreCase(
					"serviceFileName" )) {
				serviceName = loadRemoteServiceResponse.getText();
				if(serviceName.endsWith( ".aar" )){
					serviceName = serviceName.substring( 0, serviceName.indexOf( ".aar" ) );
				}
			} else if (loadRemoteServiceResponse.getLocalName()
					.equalsIgnoreCase( "port" )) {
				port = loadRemoteServiceResponse.getText();
			} else if (loadRemoteServiceResponse.getLocalName()
					.equalsIgnoreCase( "isSuccessful" )) {
				isSuccessful = loadRemoteServiceResponse.getText();
			} else if (loadRemoteServiceResponse.getLocalName()
					.equalsIgnoreCase( "serviceID" )) {
				serviceID = loadRemoteServiceResponse.getText();
			}
		}
		return createResponse( serviceName, isSuccessful, port, serviceID );
	}

	private OMElement undeployWebService( OMElement undeployServiceRequset )
			throws AxisFault {
//		DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig
//				.getConfigurator();
//		File root = deploymentEngine.getServicesDir();
//		File[] files = root.listFiles();
		String undeployIsSuccessful = "true";
		String undeployResultInf = "NO Error";
		String serviceID = null;
		String serviceGroupName = null;
		String port = null;
		String localRepoPath = Constants.REPOSITORY;
//		File localRepoFolder = new File( localRepoPath );
		Iterator<TomcatInfo> it = ShareTomcatList.getNewInstance()
				.getTomcatInfoList();
		String undeployPathOut = null;
		System.out.print( undeployServiceRequset );
		for (Iterator iter = undeployServiceRequset.getChildElements(); iter.hasNext();) {
			OMElement element = (OMElement) iter.next();
			if (element.getLocalName().equalsIgnoreCase(
					"port" )) {
				port = element.getText();
				System.out.println("端口号是##################" + port);
			}
			if (element.getLocalName().equalsIgnoreCase(
			"serviceName" )) {
				serviceGroupName = element.getText();
			}
			if (element.getLocalName().equalsIgnoreCase(
					"serviceID" )) {
				serviceID = element.getText();
			}
		}
		while (it.hasNext()) {
			TomcatInfo tomcatInfo = it.next();
			if (String.valueOf( tomcatInfo.getPort() ).equals( port )) {
				undeployPathOut = tomcatInfo.getTomcatPath()
						+ "/webapps/axis2/WEB-INF/services/";
			}
		}
		logger.info("Undeploy path:" + undeployPathOut);
		File reproot = new File(localRepoPath);
		File root = new File(undeployPathOut);
		File[] files = root.listFiles();
		File[] repfiFiles = reproot.listFiles();

//		URL repository = axisConfig.getRepository();
//		String appliancePath = repository.getPath().substring( 1,
//				repository.getPath().indexOf( "webapps" ) ).replace( '/', '\\' );
		
//		logger.info( undeployServiceRequset );

		

		
//		String unDeployServiceGroupName = null;
//		Iterator serviceGroupIt = axisConfig.getServiceGroups();
//		while (serviceGroupIt.hasNext()) {
//			AxisServiceGroup serviceGroup = (AxisServiceGroup) serviceGroupIt
//					.next();
//			Iterator serviceIt = serviceGroup.getServices();
//			while (serviceIt.hasNext()) {
//				AxisService service = (AxisService) serviceIt.next();
//				if (service.getName().equalsIgnoreCase( serviceGroupName )) {
//					unDeployServiceGroupName = serviceGroup
//							.getServiceGroupName();
//					break;
//				}
//			}
//		}
		if (repfiFiles != null && repfiFiles.length > 0) {
			int i = 0;
			int repfilesLength = repfiFiles.length;
			while (i < repfilesLength) {
				if (repfiFiles[i].getName().equalsIgnoreCase(
						(serviceGroupName + ".aar") )) {
//					logger.info("Get the replica file");
					File repfile = repfiFiles[i];
					boolean success = repfile.delete();
					if (success){
						break;
					}
				}
				i++;
			}
		}
		
		if (files != null && files.length > 0) {
			int i = 0;
			int filesLength = files.length;
			while (i < filesLength) {
				if (files[i].getName().equalsIgnoreCase(
						(serviceGroupName + ".aar") )) {
//					logger.info("Get the undeploy file");
					File file = files[i];
					boolean success = file.delete();
					if (success) {
//						try {
//							deploymentEngine.loadServices();
//							RPCServiceClient serviceClient;
//							serviceClient = new RPCServiceClient();
//							Options options = serviceClient.getOptions();
//							EndpointReference targetEPR = new EndpointReference(
//									"http://"
//											+ hostIP
//											+ ":8080/axis2/services/SAManageService/wsRelicaUpdate" );
//							options.setTo( targetEPR );
//							options.setAction( "urn:wsRelicaUpdate" );
//							serviceClient.setOptions( options );
//							serviceClient.cleanupTransport();
//							OMFactory omf = OMAbstractFactory.getOMFactory();
//							OMElement saUpdateMsgPrev = omf.createOMElement(
//									"wsMsg", null );
//							OMAttribute appliancePathAttr = omf
//									.createOMAttribute( "appliancePath", null,
//											appliancePath );
//							OMAttribute appName = omf.createOMAttribute(
//									"wsName", null, serviceGroupName );
//							OMAttribute serviceIDAttr = omf.createOMAttribute(
//									"serviceID", null, serviceID );
//
//							saUpdateMsgPrev.addAttribute( appliancePathAttr );
//							saUpdateMsgPrev.addAttribute( appName );
//							saUpdateMsgPrev.addAttribute( serviceIDAttr );
//
//							RPCServiceClient serviceClientForWS = new RPCServiceClient();
//							Options optionsForWS = serviceClientForWS
//									.getOptions();
//							optionsForWS.setTo( targetEPR );
//							optionsForWS.setAction( "urn:sendUpdateMsg" );
//							serviceClientForWS.setOptions( optionsForWS );
//							serviceClientForWS.cleanupTransport();
//						} catch (AxisFault e) {
//							e.printStackTrace();
//						}
//						
						logger.info("Undeployment finished successful");
						break;
					}
					String message = Messages.getMessage(
							"invalidservicegroupname", serviceGroupName );
					AxisFault fault = new AxisFault( Messages.getMessage(
							"invalidservicegroupname", serviceGroupName ) );
					logger.error( message, fault );
					undeployIsSuccessful = "false";
					undeployResultInf = "Error 1";
				}
				i++;
			}
			if (i == files.length) {
				String message = Messages.getMessage(
						"invalidservicegroupname", serviceGroupName );
				AxisFault fault = new AxisFault( Messages.getMessage(
						"invalidservicegroupname", serviceGroupName ) );
				logger.error( message, fault );
				undeployIsSuccessful = "false";
				undeployResultInf = "Error 2:NO Such AAR";
			}

		} else {
			String message = Messages.getMessage( "invalidservicegroupname",
					serviceGroupName );
			AxisFault fault = new AxisFault( Messages.getMessage(
					"invalidservicegroupname", serviceGroupName ) );
			logger.error( message, fault );
			undeployIsSuccessful = "false";
			undeployResultInf = "Error 2:Other Error";
		}
		if (!serviceGroupName.endsWith( ".aar" )) {
			serviceGroupName += ".aar";
		}
//		unDeployedServices.add( serviceGroupName );
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement undeployResponse = omFactory.createOMElement(
				"undeployResponse", null );
		OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
				"Type", null, "WebService" );
		undeployResponse.addAttribute( deployResponseTypeAttr );
		OMElement isSuccessfulElement = omFactory.createOMElement(
				"isSuccessful", null );
		isSuccessfulElement.setText( undeployIsSuccessful );
		OMElement undeployResultInfoElement = omFactory.createOMElement(
				"undeployResultInfo", null );
		undeployResultInfoElement.setText( undeployResultInf );
		undeployResponse.addChild( isSuccessfulElement );
		undeployResponse.addChild( undeployResultInfoElement );
		
		//notify the update to HostInfo
		if(undeployIsSuccessful.equals( "true" )){
			HostInfo.getInstance().deleteApp( Integer.valueOf(port), serviceID );
		}
//		System.out.println("反部署报文###################" + undeployResponse);
		logger.info("Response of undeployment"+undeployResponse);
		return undeployResponse;
	}


	private OMElement loadRemoteService( OMElement serviceElement ) {
		OMElement fileContent = null;
		OMElement fileName = null;

		String deployResultInfo = "Deploy is done";
		String isSuccessful = "true";
		String serviceID = null;
		String port = null;
		// OMElement fileType = null;
		for (Iterator iter = serviceElement.getChildElements(); iter.hasNext();) {
			OMElement element = (OMElement) iter.next();
			if (element.getLocalName().equalsIgnoreCase( "fileData" ))
				fileContent = element;
			if (element.getLocalName().equalsIgnoreCase( "fileName" )){
				fileName = element;
				logger.info( "fileName:\t\t"+fileName );
			}
			if (element.getLocalName().equalsIgnoreCase("port")){
				port = element.getText();
				logger.info( "port:\t\t"+port );
			}
			if (element.getLocalName().equalsIgnoreCase( "serviceID" )) {
				serviceID = element.getText();
				logger.info( "serviceID:\t\t"+serviceID );
			}
		}
		if (fileContent == null) {
			logger.error( "Failed when upload the service file", new AxisFault(
					"Failed when upload the service file" ) );
			deployResultInfo = "Failed when upload the service file";
			isSuccessful = "false";
		}
		OMText text = (OMText) fileContent.getFirstOMChild();
		String name = fileName.getText();
		// String type = fileType.getText();
		// check whether the type of the uploaded service file is "aar"
		if (!name.endsWith( ".aar" )) {
//			logger.error( "Wrong type of the uploaded service file",
//					new AxisFault( "Wrong type of the uploaded service file" ) );
//			deployResultInfo = "Wrong type of the uploaded service file";
//			isSuccessful = "false";
			name += ".aar";
		}

//		DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig
//				.getConfigurator();
//		deploymentEngine.loadServices();
//		URL repository = axisConfig.getRepository();
//		String appliancePath = repository.getPath().substring( 1,
//				repository.getPath().indexOf( "webapps" ) ).replace( '/', '\\' );

		String repositoryPath = Constants.REPOSITORY;
		File wsRepository = new File( repositoryPath );

		if (!wsRepository.isDirectory()) {
			wsRepository.mkdirs();
		}
		File deployWSFile = new File( repositoryPath + "/" + name );
//		String hostIP = Constants.LOCAL_IP_WITHOUT_HTTP;
		if (deployWSFile.exists()) {//if the repository contains the webservice file, deploy it directly.
			logger.warn( 
					"[System Information][Management Operation][Application Deploy]"
					+ "The Web Service for deploying has already in the localRepository" );

			localWSDeploy(name, port, serviceID);
//			RPCServiceClient serviceClientForPort;
//			RPCServiceClient serviceClient;
//			try {
//				serviceClientForPort = new RPCServiceClient();
//				Options optionsForPort = serviceClientForPort.getOptions();
//				EndpointReference targetEPRForPort = new EndpointReference(
//						"http://"
//								+ hostIP
//								+ ":8080/axis2/services/SAManageService/getAppliancePort" );
//				optionsForPort.setTo( targetEPRForPort );
//				optionsForPort.setAction( "urn:getAppliancePort" );
//				serviceClientForPort.setOptions( optionsForPort );
//				QName op = new QName(
//						"http://manageServices.serviceCloud.sdp.act.org.cn",
//						"getAppliancePort" );
//				appliancePath = appliancePath.substring( 0, appliancePath
//						.length() - 1 );
//				Object[] opPara = new Object[] { appliancePath };
//				OMElement portElement = serviceClientForPort.invokeBlocking(
//						op, opPara );
//				System.out.println( "&&&&PORTElement&&&" + portElement );
//				serviceClientForPort.cleanupTransport();
//				serviceClient = new RPCServiceClient();
//				Options options = serviceClient.getOptions();
//				EndpointReference targetEPR = new EndpointReference( "http://"
//						+ hostIP
//						+ ":8080/axis2/services/SAManageService/localWSDeploy" );
//				options.setTo( targetEPR );
//				options.setAction( "urn:localWSDeploy" );
//				serviceClient.setOptions( options );
//				QName opAddEntry = new QName(
//						"http://manageServices.serviceCloud.sdp.act.org.cn",
//						"localWSDeploy" );
//				//name,port,wsId
//				Object[] opAddEntryArgs = new Object[] {
//						name.substring( 0, name.indexOf( ".aar" ) ),
//						portElement.getFirstElement().getText(), serviceID };
//				serviceClient.invokeBlocking( opAddEntry, opAddEntryArgs );
//				serviceClient.cleanupTransport();
//			} catch (AxisFault e) {
//				e.printStackTrace();
//			}

			OMFactory omFactory = OMAbstractFactory.getOMFactory();
			OMElement loadRemoteServiceResponse = omFactory.createOMElement(
					"loadRemoteServiceResponse", null );
			OMElement serviceFileName = omFactory.createOMElement(
					"serviceFileName", null );
			serviceFileName
					.setText( name.substring( 0, name.indexOf( ".aar" ) ) );
//			OMElement deployResponse = omFactory.createOMElement(
//					"deployResponse", null );

//			OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
//					"Type", null, "WebService" );
//			deployResponse.addAttribute( deployResponseTypeAttr );
			OMElement isSuccessfulElement = omFactory.createOMElement(
					"isSuccessful", null );
			isSuccessfulElement.setText( isSuccessful );
			OMElement deployResultInfoElement = omFactory.createOMElement(
					"deployResultInfo", null );
			deployResultInfoElement.setText( deployResultInfo );
			OMElement serviceIDElement = omFactory.createOMElement(
					"serviceID", null );
			serviceIDElement.setText( serviceID );
			OMElement portElement = omFactory.createOMElement(
					"port", null );
			portElement.setText( port );			
			loadRemoteServiceResponse.addChild( isSuccessfulElement );
			loadRemoteServiceResponse.addChild( deployResultInfoElement );
			loadRemoteServiceResponse.addChild( portElement );
			loadRemoteServiceResponse.addChild( serviceIDElement );
			loadRemoteServiceResponse.addChild( serviceFileName );
			return loadRemoteServiceResponse;
		} else {//if the repository don't contains the webservice file, load it from remote place.			
			FileOutputStream os = null;
			InputStream is = null;
			try {
				DataHandler handler = (DataHandler) text.getDataHandler();
				os = new FileOutputStream( deployWSFile );
				is = handler.getInputStream();
				os.write( IOUtils.getStreamAsByteArray( is ) );
				os.close();
				is.close();
				localWSDeploy(name.toString(), port, serviceID);
//				RPCServiceClient serviceClientForPort;
//				RPCServiceClient serviceClient;
//				try {
//					serviceClientForPort = new RPCServiceClient();
//					Options optionsForPort = serviceClientForPort.getOptions();
//					EndpointReference targetEPRForPort = new EndpointReference(
//							"http://"
//									+ hostIP
//									+ ":8080/axis2/services/SAManageService/getAppliancePort" );
//					optionsForPort.setTo( targetEPRForPort );
//					optionsForPort.setAction( "urn:getAppliancePort" );
//					serviceClientForPort.setOptions( optionsForPort );
//					QName op = new QName(
//							"http://manageServices.serviceCloud.sdp.act.org.cn",
//							"getAppliancePort" );
//					appliancePath = appliancePath.substring( 0, appliancePath
//							.length() - 1 );
//					Object[] opPara = new Object[] { appliancePath };
//					OMElement portElement = serviceClientForPort
//							.invokeBlocking( op, opPara );
//					serviceClientForPort.cleanupTransport();
//					serviceClient = new RPCServiceClient();
//					Options options = serviceClient.getOptions();
//					EndpointReference targetEPR = new EndpointReference(
//							"http://"
//									+ hostIP
//									+ ":8080/axis2/services/SAManageService/localWSDeploy" );
//					options.setTo( targetEPR );
//					options.setAction( "urn:localWSDeploy" );
//					serviceClient.setOptions( options );
//					QName opAddEntry = new QName(
//							"http://manageServices.serviceCloud.sdp.act.org.cn",
//							"localWSDeploy" );
//					Object[] opAddEntryArgs = new Object[] {
//							name.substring( 0, name.indexOf( ".aar" ) ),
//							portElement.getFirstElement().getText(), serviceID };
//					serviceClient.invokeBlocking( opAddEntry, opAddEntryArgs );
//					serviceClient.cleanupTransport();
//				} catch (AxisFault e) {
//					e.printStackTrace();
//				}

			} catch (Exception e) {
				logger.error( "Failed when upload the service file",
						new AxisFault( "Failed when upload the service file" ) );
				deployResultInfo = "Failed when upload the service file";
				isSuccessful = "false";
			} finally {
				try {
					is.close();
					os.close();
				} catch (IOException e) {
					logger.error( e );
				}
			}
			OMFactory omFactory = OMAbstractFactory.getOMFactory();
			OMElement loadRemoteServiceResponse = omFactory.createOMElement(
					"loadRemoteServiceResponse", null );

			OMElement serviceFileName = omFactory.createOMElement(
					"serviceFileName", null );
			serviceFileName
					.setText( name.substring( 0, name.indexOf( ".aar" ) ) );
//			OMElement deployResponse = omFactory.createOMElement(
//					"deployResponse", null );
//			OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
//					"Type", null, "WebService" );
//			deployResponse.addAttribute( deployResponseTypeAttr );
			OMElement isSuccessfulElement = omFactory.createOMElement(
					"isSuccessful", null );
			isSuccessfulElement.setText( isSuccessful );
			OMElement deployResultInfoElement = omFactory.createOMElement(
					"deployResultInfo", null );
			OMElement serviceIDElement = omFactory.createOMElement(
					"serviceID", null );
			serviceIDElement.setText( serviceID );
			OMElement portElement = omFactory.createOMElement(
					"port", null );
			portElement.setText( port );
			deployResultInfoElement.setText( deployResultInfo );
			loadRemoteServiceResponse.addChild( isSuccessfulElement );
			loadRemoteServiceResponse.addChild( deployResultInfoElement );
			loadRemoteServiceResponse.addChild( portElement );
			loadRemoteServiceResponse.addChild( serviceIDElement );
			loadRemoteServiceResponse.addChild( serviceFileName );
			return loadRemoteServiceResponse;
		}
	}



	private OMElement createResponse( String serviceName, String deployIsSuccesful, String port, String serviceID ) {
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement deployResponse = omFactory.createOMElement(
				"deployResponse", null );
		OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
				"Type", null, "WebService" );
		deployResponse.addAttribute( deployResponseTypeAttr );
		OMElement isSuccessfulElement = omFactory.createOMElement(
				"isSuccessful", null );
		isSuccessfulElement.setText( deployIsSuccesful );
		OMElement serviceNameElement = omFactory.createOMElement(
				"serviceName", null );
		serviceNameElement.setText( serviceName );
		OMElement deployResultInfoElement = omFactory.createOMElement(
				"deployResultInfo", null );
		deployResultInfoElement.setText( "" );
		OMElement serviceIDElement = omFactory.createOMElement(
				"serviceID", null );
		serviceIDElement.setText( serviceID );
		OMElement serviceWSDLElement = omFactory.createOMElement( "wsdlUrl",
				null );
		deployResponse.addChild( isSuccessfulElement );
		deployResponse.addChild( serviceNameElement );
		deployResponse.addChild( deployResultInfoElement );
		deployResponse.addChild( serviceIDElement );
		deployResponse.addChild( serviceWSDLElement );
		if(Boolean.valueOf( deployIsSuccesful )){//deploy successful
			String invocationAddr = Constants.LOCAL_IP_WITH_HTTP + ":" + port + "/";
			WebService webService = new WebService(serviceName, invocationAddr, serviceID);
			HostInfo.getInstance().addApp( webService, Integer.parseInt(port) );
		}
		return deployResponse;
	}


//	private OMElement deployServiceGroup( String serviceGroupName ) {
//		DeploymentEngine deploymentEngine = (DeploymentEngine) axisConfig
//				.getConfigurator();
//		deploymentEngine.loadServices();
//		if (!serviceGroupName.endsWith( ".aar" )) {
//			serviceGroupName += ".aar";
//		}
////		if (unDeployedServices.contains( serviceGroupName ))
////			unDeployedServices.remove( serviceGroupName );
//
//		int index = serviceGroupName.indexOf( ".aar" );
//		if (index != -1)
//			serviceGroupName = serviceGroupName.substring( 0, index );
//		AxisServiceGroup asGroup = axisCtx.getAxisConfiguration()
//				.getServiceGroup( serviceGroupName );
//		String deployIsSuccesful = "true";
//		if (asGroup == null) {
//			String message = Messages.getMessage( "invalidservicegroupname",
//					serviceGroupName );
//			deployIsSuccesful = "false";
//			OMFactory omFactory = OMAbstractFactory.getOMFactory();
//			OMElement deployResponse = omFactory.createOMElement(
//					"deployResponse", null );
//			OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
//					"Type", null, "WebService" );
//			deployResponse.addAttribute( deployResponseTypeAttr );
//			OMElement isSuccessfulElement = omFactory.createOMElement(
//					"isSuccessful", null );
//			isSuccessfulElement.setText( deployIsSuccesful );
//			OMElement deployResultInfoElement = omFactory.createOMElement(
//					"deployResultInfo", null );
//			deployResultInfoElement
//					.setText( "Web Service's type is not supported" );
//			deployResponse.addChild( isSuccessfulElement );
//			deployResponse.addChild( deployResultInfoElement );
//			AxisFault fault = new AxisFault( Messages.getMessage(
//					"invalidservicegroupname", serviceGroupName ) );
//			logger.error( message, fault );
//			return deployResponse;
//		}
//		Iterator itsgrp = axisConfig.getServiceGroups();
//		while (itsgrp.hasNext()) {
//			AxisServiceGroup axisServiceGroup = (AxisServiceGroup) itsgrp
//					.next();
//			if (axisServiceGroup == null){
////				System.out.println( "null" );
//				logger.warn( "null" );
//			}
//			if (axisServiceGroup.getDocumentation() == null) {
//				Iterator deployedServiceIt = axisServiceGroup.getServices();
//				while (deployedServiceIt.hasNext()) {
//					AxisService DeployingService = (AxisService) deployedServiceIt
//							.next();
//					DeployingService.getEndpointURL();
//
//				}
//			}
//		}
//		OMFactory omFactory = OMAbstractFactory.getOMFactory();
//		OMElement deployResponse = omFactory.createOMElement( "deployResponse",
//				null );
//		OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute(
//				"Type", null, "WebService" );
//		deployResponse.addAttribute( deployResponseTypeAttr );
//		OMElement serviceNameElement = omFactory.createOMElement(
//				"serviceName", null );
//		serviceNameElement.setText( serviceGroupName );
//
//		OMElement isSuccessfulElement = omFactory.createOMElement(
//				"isSuccessful", null );
//		isSuccessfulElement.setText( deployIsSuccesful );
//		OMElement deployResultInfoElement = omFactory.createOMElement(
//				"deployResultInfo", null );
//		OMElement serviceWSDLElement = omFactory.createOMElement( "wsdlUrl",
//				null );
//		deployResponse.addChild( serviceNameElement );
//		deployResponse.addChild( isSuccessfulElement );
//		deployResponse.addChild( serviceWSDLElement );
//		deployResponse.addChild( deployResultInfoElement );
//		
//		//notify the update to the HostInfo
//		if(deployIsSuccesful.equals( "true" )){
//			//TODO 
//			//
////			
////			WebService webService = new WebService(serviceGroupName, invocationAddr, serviceID);
////			HostInfo.getInstance().addApp( webService, Integer.parseInt(port) );
//		}
//
//		return deployResponse;
//	}

}
