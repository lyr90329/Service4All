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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager;

import java.io.IOException;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.registry.SADeploymentMsg;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileReproduction;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.TomcatModification;


public class AppServerManager extends SAManager {

	public static OMElement appServerDeploymentOMElement = null;
	private static Logger logger = Logger.getLogger( AppServerManager.class );
	private static AppServerManager manager;

	private AppServerManager() {
		System.getProperty( "user.dir" ).replace( '\\', '/' );
		PropertyConfigurator.configure( System.getProperty( "user.dir" )
				.replace( '\\', '/' )
				+ "\\log\\" + "log4j2.properties" );
		logger.debug( "[System Information][AppServer Manage]Begin Logging" );
	}
	
	public synchronized static  AppServerManager getInstance(){
		if(manager == null){
			manager = new AppServerManager();
		}
		return manager;
	}
	public OMElement deploy() {
		if (!existInActiveTomcat()){
			if (!createTomcat()){
				logger.warn( "AppServer deploy failed." );
			}
		}		
		return appServerDeploymentOMElement;
		
	}


	private static boolean existInActiveTomcat() {
		synchronized (ShareTomcatList.getNewInstance()) {
			int counter = 0;
			String appServerHtml = "http://localhost:";
			TomcatInfo tomcatInfo = new TomcatInfo();
			for (; counter < ShareTomcatList.getNewInstance()
					.getTomcatListSize(); counter++) {// get whether there is an inactive tomcat
				if (ShareTomcatList.getNewInstance().getTomcatInfo(counter)
						.getStatus().equalsIgnoreCase("InActive")) {// there is an inactive tomcat
					tomcatInfo = new TomcatInfo(ShareTomcatList
							.getNewInstance().getTomcatInfo(counter));
					tomcatInfo.setTomcatIndex(counter);
					// start a tomcat
//					String startupPath = tomcatInfo.getTomcatPath() + ""
//							+ "\\bin\\startup.bat";
					logger.debug("[System Information][AppServer Manage]"
							+ "There is an InActive tomcat,In appServer deploy InActive startupPath is:"
							+ tomcatInfo.getTomcatPath());
					new TomcatControllerOnWindows().start(tomcatInfo.getTomcatPath());
					appServerHtml = appServerHtml + tomcatInfo.getPort();
					if (SharedFunction.activeCheckByHtml(
							Constants.STATUS_CHECK_TIMEOUT, appServerHtml)) {//check the status 
																				// update tomcat and record tomcatManageService.tomcatList
//						tomcatInfo.setAppserver(true);
						ShareTomcatList.getNewInstance()
								.getTomcatInfo(tomcatInfo.getTomcatIndex())
								.setStatus("Active");
						SADeploymentMsg appServerDeploymentMsg = new SADeploymentMsg();
						appServerDeploymentMsg
								.setDeploymentIsSuccessful("true");
						appServerDeploymentOMElement = appServerDeploymentMsg
								.createSADeploymentResponse(tomcatInfo,
										"tomcat");
						logger.debug("[System Information][AppServer Manage]"
								+ "there is an InActive tomcat,appServer deploy succeed");
						
						HostInfo.getInstance().addAppliance(tomcatInfo);
						return true;
					}
				}// if there is an inactive tomcat
			}// for 
			return false;
		}
	}


	/**
	 * create a new tomcat
	 * 
	 * @author huanghe
	 */
	private boolean createTomcat() {
		synchronized (ShareTomcatList.getNewInstance()) {
			String appServerHtml = "http://localhost:";
			TomcatInfo tomcatInfo = new TomcatInfo();
			if (SharedFunction.getPathANDPort( tomcatInfo )) {
				try {
					FileReproduction fileReproduction = 
						FileManipulationFactory.newFileReproduction(
									/*Constants.TOMCAT_SOURCE_PATH,
									tomcatInfo.getTomcatPath()*/ );
//					System.out.println(tomcatInfo.getTomcatPath());
					logger.info( tomcatInfo.getTomcatPath() );
					fileReproduction.copyFiles(Constants.TOMCAT_SOURCE_PATH, tomcatInfo.getTomcatPath() );
				} catch (IOException e) {
					e.printStackTrace();
				}
				/*
				 * modify the ports and server.xml modify
				 * CATALINA_HOME,startup.sh and shutdown.sh
				 */
				TomcatModification modifyTomcatFile = FileManipulationFactory.newTomcatModification(); 
				modifyTomcatFile.modifyTomcatFile( tomcatInfo );
//				String startupPath = tomcatInfo.getTomcatPath()
//						+ "/bin/startup.sh";
				logger
						.debug( "[System Information][AppServer Manage]"
								+ "create a new tomcat,In appServer deploy startupPath is:"
								+ tomcatInfo.getTomcatPath() );
				TomcatControllerFactory.getInstance().createTomcatController().start( tomcatInfo.getTomcatPath() );
				appServerHtml = appServerHtml + tomcatInfo.getPort();
				if (SharedFunction.activeCheckByHtml(
						Constants.STATUS_CHECK_TIMEOUT, appServerHtml )) {

					tomcatInfo.setStatus( "Active" );
					ShareTomcatList.getNewInstance().addTomcat( tomcatInfo );
					logger.info("���tomcat�ɹ���" + ShareTomcatList.getNewInstance().getTomcatListSize());

					ShareTomcatList.getNewInstance().addPort( tomcatInfo.getPort()  );
					ShareTomcatList.getNewInstance().addPort(
							tomcatInfo.getShutdownPort() );
					ShareTomcatList.getNewInstance().addPort(
							tomcatInfo.getAjpPort() );
					
					SADeploymentMsg appServerDeploymentMsg = new SADeploymentMsg();
					appServerDeploymentMsg.setDeploymentIsSuccessful( "true" );
					appServerDeploymentOMElement = appServerDeploymentMsg
							.createSADeploymentResponse( tomcatInfo, "tomcat" );
					logger.debug( "[System Information][AppServer Manage]"
							+ "create a new tomcat, appServer deploy succeed" );

					
					HostInfo.getInstance().addAppliance(tomcatInfo);
					return true;
				} else {
					new TomcatControllerOnWindows().shutDown(tomcatInfo.getTomcatPath());
					FileManipulationFactory.newFileDeletion().delFolder( tomcatInfo
							.getTomcatPath() );
					SADeploymentMsg appServerDeploymentMsg = new SADeploymentMsg();
					appServerDeploymentMsg.setDeploymentIsSuccessful( "false" );
					appServerDeploymentMsg
							.setDeploymentDescription( "AppServer fails,delete tomcat" );
					appServerDeploymentOMElement = appServerDeploymentMsg
							.createSADeploymentResponse( tomcatInfo, "tomcat" );
					logger.warn( "[System Information][AppServer Manage]"
							+ "AppServer deployment fails,delete tomcat" );
				}
			}
			return false;
		}
	}


	public void backMsgEncp() {

	}

	@Override
	public OMElement undeploy(String port) {
		synchronized (ShareTomcatList.getNewInstance()) {
			String appServerHtml = "http://localhost:";

			TomcatInfo tomcatInfo = null;
			if (SharedFunction.getTomcatPath( port ) != null) {
				tomcatInfo = new TomcatInfo();
				tomcatInfo = SharedFunction.getTomcatPath( port );
				logger.info(tomcatInfo.getTomcatPath());
				for (int i = 0; i < ShareTomcatList.getNewInstance()
						.getTomcatListSize(); i++) {
					if (ShareTomcatList.getNewInstance().getTomcatInfo( i )
							.getPort()== Integer.parseInt(port))
						tomcatInfo.setTomcatIndex( i );
				}
			}
			if (tomcatInfo != null
					&& tomcatInfo.getStatus().equalsIgnoreCase( "Active" )
					&& !tomcatInfo.isAxis2()) {
				new TomcatControllerOnWindows().shutDown( tomcatInfo.getTomcatPath() );

				appServerHtml = appServerHtml + tomcatInfo.getPort();
				if (SharedFunction.inActiveCheckByHtml(
						Constants.STATUS_CHECK_TIMEOUT, appServerHtml )) {
					logger
							.debug( "[System Information][AppServer Manage]"
									+ "stop tomcat successfully,undeploy appServer successfully, on port "
									+ port );
					tomcatInfo.setStatus( "InActive" );
					ShareTomcatList.getNewInstance().getTomcatInfo(
							tomcatInfo.getTomcatIndex() )
							.setStatus( "InActive" );
					SADeploymentMsg appServerUndeploymentMsg = new SADeploymentMsg();
					appServerUndeploymentMsg.setDeploymentIsSuccessful( "true" );
					appServerDeploymentOMElement = appServerUndeploymentMsg
							.createSAUndeploymentResponse( tomcatInfo, "tomcat" );

					HostInfo.getInstance().deleteApplianceByPort(port);
				}
			}
			SADeploymentMsg appServerUndeploymentMsg = new SADeploymentMsg();
			appServerUndeploymentMsg.setDeploymentIsSuccessful( "false" );
			appServerUndeploymentMsg
					.setUnDeploymentDescription( "fail to undeploy appServer" );
			appServerDeploymentOMElement = appServerUndeploymentMsg
					.createSAUndeploymentResponse( tomcatInfo, "tomcat" );
			logger.warn( "[System Information][AppServer Manage]"
					+ "Fail to undeploy AppServer" );
		}
		return null;
	}

	@Override
	public OMElement restart(String port) {
		synchronized (ShareTomcatList.getNewInstance()) {
			String appServerHtml = "http://localhost:";
			
			TomcatInfo tomcatInfo = null;
			if (SharedFunction.getTomcatPath(port) != null) {
				tomcatInfo = new TomcatInfo();
				tomcatInfo = new TomcatInfo(
						SharedFunction.getTomcatPath(port));
			}
			if (tomcatInfo != null) {
				appServerHtml = appServerHtml + tomcatInfo.getPort();
				if (tomcatInfo.getStatus().equalsIgnoreCase("Active")
						&& SharedFunction.inActiveCheckByHtml(
								Constants.STATUS_CHECK_TIMEOUT,
								appServerHtml)) {// inActive,restart tomcat
//					String startupPath = tomcatInfo.getTomcatPath()
//							+ "\\bin\\startup.sh";
					new TomcatControllerOnWindows().start(tomcatInfo.getTomcatPath());
					if (SharedFunction.activeCheckByHtml(
							Constants.STATUS_CHECK_TIMEOUT, appServerHtml)) {// succeed to restart the tomcat
						
						ShareTomcatList.getNewInstance()
								.getTomcatInfo(tomcatInfo.getTomcatIndex())
								.setStatus("Active");
					}// if active
				}// if inActive and appserver has some exceptions
			}// if tomcatInfo !=null
		}
		return null;
	}
}
