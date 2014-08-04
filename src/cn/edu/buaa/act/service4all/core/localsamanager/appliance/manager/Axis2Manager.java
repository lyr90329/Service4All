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

import java.io.File;
import java.io.IOException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.buaa.act.service4all.core.localsamanager.appliance.Axis2Info;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.registry.SADeploymentMsg;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;


public class Axis2Manager extends SAManager {
	public static OMElement axis2DeploymentOMElement = null;	
	private static Logger logger = Logger.getLogger( Axis2Manager.class );
	private final static String tomcatSourcePath = Constants.TOMCAT_SOURCE_PATH;
	private final static String axis2SourcePath = Constants.AXIS2_SOURCE_PATH;
	private final static long statusCheckTimeout = Constants.STATUS_CHECK_TIMEOUT;
	
	private static Axis2Manager manager;


	private Axis2Manager() {
		PropertyConfigurator.configure( System.getProperty( "user.dir" )
				.replace( '\\', '/' )
				+ "/log/" + "log4j2.properties" );
		logger.debug( "[System Information][Axis2 Manage]Begin Logging" );
	}
	
	public synchronized static  Axis2Manager getInstance(){
		if(manager == null){
			manager = new Axis2Manager();
		}
		return manager;
	}


	@Override
	public OMElement deploy() {
//		long beginTime = System.currentTimeMillis();
		TomcatInfo tomcatInfo = existTomcatWithNoAxis2();
		boolean needsToCreateATomcat = (tomcatInfo==null);
		//get an available tomcat
		if (needsToCreateATomcat){
			//create a tomcat with an axis2
			tomcatInfo = createTomcatWithAxis2( 0 );
			if (tomcatInfo==null) {
				logger.error( "failed" );
			}
		}
		//get the deployport
		int deployPort = tomcatInfo.getPort();
//		long saDeployTime = System.currentTimeMillis() - beginTime;
//
//		String hostIP = Constants.LOCAL_IP_WITHOUT_HTTP;
//		OMFactory omf = OMAbstractFactory.getOMFactory();
//		OMElement saUpdateMsg = omf.createOMElement( "update", null );
//		OMAttribute hostIPAttr = omf.createOMAttribute( "ip", null, hostIP );
//		saUpdateMsg.addAttribute( hostIPAttr );
//		OMElement deployElement = omf.createOMElement( "deploy", null );
//		OMAttribute applianceType = omf.createOMAttribute( "type", null,
//				"axis2" );
//		OMAttribute appliancePort = omf.createOMAttribute( "port", null,
//				Integer.toString( deployPort ) );
//		OMAttribute deployTime = omf.createOMAttribute( "lastTime", null, Long
//				.toString( saDeployTime ) );
//		deployElement.addAttribute( applianceType );
//		deployElement.addAttribute( appliancePort );
//		deployElement.addAttribute( deployTime );
//		saUpdateMsg.addChild( deployElement );
		SADeploymentMsg axis2DeploymentMsg = new SADeploymentMsg();
		axis2DeploymentMsg.setDeploymentIsSuccessful( "true" );
		axis2DeploymentOMElement = axis2DeploymentMsg
				.createSADeploymentResponse( tomcatInfo, "axis2" );
		
		//notify the update to the HostInfo
		Axis2Info info = new Axis2Info(deployPort, null, null, null, null);
		if(!needsToCreateATomcat){
			//if the deployment reused a tomcat, remove the tomcat in the hostInfo
			HostInfo.getInstance().deleteApplianceByPort( String.valueOf( deployPort ) );
		}
		HostInfo.getInstance().addAppliance( info );
		
		return axis2DeploymentOMElement;
	}


	/**
	 * To judge if there exists a tomcat without axis2.
	 * @return the tomcat info. null means no suited tomcat.
	 */
	private static TomcatInfo existTomcatWithNoAxis2() {
		synchronized (ShareTomcatList.getNewInstance()) {
			int counter = 0;
			TomcatInfo tomcatInfo;
			String axis2HtmlUrlString = "http://localhost:";
			for (; counter < ShareTomcatList.getNewInstance()
					.getTomcatListSize(); counter++) {
				TomcatInfo tomcat = ShareTomcatList.getNewInstance()
						.getTomcatInfo( counter );
				if (tomcat.getStatus().equals( "Active" )
						&& !tomcat.isAppserver() && !tomcat.isAxis2()) {

					int index = counter;
					tomcatInfo = new TomcatInfo( ShareTomcatList
							.getNewInstance().getTomcatInfo( counter ) );
					tomcatInfo.setTomcatIndex( index );
					try {
						File axis2File = new File(
								axis2SourcePath );
						FileManipulationFactory.newFileReproduction(
								/*axis2SourcePath,
								tomcatInfo.getTomcatPath() + "/webapps"*/ )
								.copyFile(axis2SourcePath, axis2File,
										tomcatInfo.getTomcatPath() + "/webapps" );
					} catch (IOException e) {
						e.printStackTrace();
					}
					axis2HtmlUrlString = axis2HtmlUrlString + tomcatInfo.getPort() + "/axis2";
					if (SharedFunction.activeCheckByHtml(
							statusCheckTimeout, axis2HtmlUrlString )) {
						ShareTomcatList.getNewInstance().getTomcatInfo( index )
								.setAxis2( true );
						tomcatInfo.setAxis2( true );					

						SADeploymentMsg axis2DeploymentMsg = new SADeploymentMsg();
						axis2DeploymentMsg.setDeploymentIsSuccessful( "true" );
						axis2DeploymentOMElement = axis2DeploymentMsg
								.createSADeploymentResponse( tomcatInfo,
										"axis2" );
						logger.debug( "[System Information][Axis2 Manage]"
								+ "there is an active but empty tomcat,axis2 deployment succeed." );
						logger.debug( "[System Information][Axis2 Manage]"
										+ "Tomcat folder:"
										+ tomcatInfo.getTomcatPath() );
						return tomcatInfo;
					} else {
						SADeploymentMsg axis2DeploymentMsg = new SADeploymentMsg();
						axis2DeploymentMsg.setDeploymentIsSuccessful( "false" );
						axis2DeploymentMsg
								.setDeploymentDescription( "existTomcatWithNoAxis2, Axis2 deployment failed" );
						axis2DeploymentOMElement = axis2DeploymentMsg
								.createSADeploymentResponse( tomcatInfo,
										"axis2" );
						new Axis2Manager().undeploy(String.valueOf(tomcatInfo.getPort()));
						logger.warn( "[System Information][Axis2 Manage]"
								+ "existTomcatWithNoAxis2 Axis2 deployment failed." );
					}
				}
			}
			return null;
		}
	}


	/**
	 * To startup a tomcat with axis2
	 * @return the tomcat info. null means startup fail.
	 */
	public static TomcatInfo createTomcatWithAxis2( int type ) {
		synchronized (ShareTomcatList.getNewInstance()) {
			TomcatInfo tomcatInfo = new TomcatInfo();
			String axis2Html = "http://localhost:";

			if (SharedFunction.getPathANDPort( tomcatInfo )) {

				try {
					logger.info( "Tomcat copy path:"
							+ tomcatInfo.getTomcatPath().toString() );
//					new CopyFileAndDir( LocalSAManagerService.tomcatSourcePath,
//							tomcatInfo.getTomcatPath() ).copyFiles(
//									LocalSAManagerService.tomcatSourcePath, tomcatInfo
//									.getTomcatPath() );
					FileManipulationFactory.newFileReproduction(
							/*tomcatSourcePath,
							tomcatInfo.getTomcatPath()*/ ).copyFiles(
							tomcatSourcePath,
							tomcatInfo.getTomcatPath() );
					File axis2File = new File( axis2SourcePath );
//					new CopyFileAndDir( LocalSAManagerService.axis2SourcePath,
//							tomcatInfo.getTomcatPath() + "/webapps" )
//							.copyFile( axis2File, tomcatInfo.getTomcatPath()
//									+ "/webapps" );
					FileManipulationFactory.newFileReproduction(
							/*axis2SourcePath,
							tomcatInfo.getTomcatPath() + "/webapps"*/ )
							.copyFile( axis2SourcePath, axis2File,
									tomcatInfo.getTomcatPath() + "/webapps" );
				} catch (IOException e) {
					e.printStackTrace();
				}

				FileManipulationFactory.newTomcatModification().modifyTomcatFile( tomcatInfo );

				TomcatController tomcatController = TomcatControllerFactory.getInstance().createTomcatController();
				
				logger.info("before start tomcat "+tomcatInfo.getTomcatPath()+" by "+tomcatController.getClass());
				tomcatController.start(tomcatInfo.getTomcatPath());
				
				axis2Html = axis2Html + tomcatInfo.getPort() + "/axis2";
				logger.info("before check html "+axis2Html);
				
				if (SharedFunction.activeCheckByHtml(
						statusCheckTimeout, axis2Html )) {
					tomcatInfo.setStatus( "Active" );
					tomcatInfo.setAxis2( true );
					tomcatInfo.setTomcatIndex( ShareTomcatList.getNewInstance()
							.getTomcatListSize() );
					ShareTomcatList.getNewInstance().addTomcat( tomcatInfo );

					ShareTomcatList.getNewInstance().addPort(tomcatInfo.getPort()  );
					ShareTomcatList.getNewInstance().addPort( tomcatInfo.getShutdownPort()  );
					ShareTomcatList.getNewInstance().addPort( tomcatInfo.getAjpPort() );

//					deployPort = tomcatInfo.getPort();

					if (type == 0) {//axis2
						SADeploymentMsg axis2DeploymentMsg = new SADeploymentMsg();
						axis2DeploymentMsg.setDeploymentIsSuccessful( "true" );
						axis2DeploymentOMElement = axis2DeploymentMsg
								.createSADeploymentResponse( tomcatInfo, "axis2" );
						logger.info( "[System Information][Axis2 Manage]createTomcatWithAxis2,succeed to create a axis2" );
//						logger.debug( "[System Information][Axis2 Manage]createTomcatWithAxis2,succeed to create a axis2" );
					}
					return tomcatInfo;
				} else {
					new Axis2Manager().undeploy(String.valueOf(tomcatInfo.getPort()));
					SADeploymentMsg axis2DeploymentMsg = new SADeploymentMsg();
					axis2DeploymentMsg.setDeploymentIsSuccessful( "false" );
					axis2DeploymentMsg
							.setDeploymentDescription( "createTomcatWithAxis2,Axis2 deployment failed" );
					axis2DeploymentOMElement = axis2DeploymentMsg
							.createSADeploymentResponse( tomcatInfo, "axis2" );
					logger.warn( "[System Information][Axis2 Manage]createTomcatWithAxis2,Axis2 deployment failed" );

				}
			}
			return null;
		}
	}
	
	
	@Override
	public OMElement undeploy(String port){
		synchronized (ShareTomcatList.getNewInstance()) {
			TomcatController tomcatController = TomcatControllerFactory.getInstance().createTomcatController();
			String tomcatHtml = "http://localhost:";
			TomcatInfo tomcatInfo = null;
			if (SharedFunction.getTomcatPath( port ) != null) {
				tomcatInfo = new TomcatInfo();
				tomcatInfo = new TomcatInfo( SharedFunction
						.getTomcatPath( port ) );
				for (int i = 0; i < ShareTomcatList.getNewInstance()
						.getTomcatListSize(); i++) {
					if (ShareTomcatList.getNewInstance().getTomcatInfo( i )
							.getPort() == Integer.parseInt(port))
						tomcatInfo.setTomcatIndex( i );
				}
			}
			if (tomcatInfo != null && tomcatInfo.isAxis2()) {
				if (tomcatInfo.getStatus().equalsIgnoreCase( "Active" )) {
					logger.info( "Closing the tomcat" );
					tomcatController.shutDown(tomcatInfo.getTomcatPath());
					String axis2Html = tomcatHtml + tomcatInfo.getPort()
							+ "/axis2";
					if (SharedFunction.inActiveCheckByHtml(
							statusCheckTimeout, axis2Html )) {
//						new DeleteFileAndDir().delFolder( tomcatInfo
//								.getTomcatPath()
//								+ "/webapps/axis2" );
						logger.info( "Removing the axis2 from tomcat" );
						boolean hasAxis2Removed = false;
						hasAxis2Removed = FileManipulationFactory.newFileDeletion()
								.delFolder(tomcatInfo.getTomcatPath()
										+ "/webapps/axis2" );
//						new DeleteFileAndDir().delFile( tomcatInfo
//								.getTomcatPath()
//								+ "/webapps/axis2.war" );
						hasAxis2Removed = hasAxis2Removed
								&& FileManipulationFactory.newFileDeletion()
										.delFile( tomcatInfo.getTomcatPath()
														+ "/webapps/axis2.war" );
						if(hasAxis2Removed){
							logger.info( "Axis2 removed success" );	
							tomcatInfo.setAxis2( false );
						}
						else{
							logger.info( "Axis2 removed failed" );
						}
						
						logger.info( "Starting the tomcat" );
						tomcatController.start(tomcatInfo.getTomcatPath());
						String appServerHtml = tomcatHtml + port;
						if (SharedFunction.activeCheckByHtml(
								statusCheckTimeout,
								appServerHtml )) {

							logger.debug( "[System Information][Axis2 Manage]"
									+ "undeploy axis2 successfully on port "
									+ port );

							tomcatInfo.setAxis2( false );
							ShareTomcatList.getNewInstance().getTomcatInfo(
									tomcatInfo.getTomcatIndex() ).setAxis2(
									false );
							ShareTomcatList.getNewInstance().setTomcatInfo(
									tomcatInfo.getTomcatIndex(), tomcatInfo );
							SADeploymentMsg axis2UndeploymentMsg = new SADeploymentMsg();
							axis2UndeploymentMsg
									.setUnDeploymentIsSuccessful( "true" );
							axis2DeploymentOMElement = axis2UndeploymentMsg
									.createSAUndeploymentResponse( tomcatInfo,
											"axis2" );
							return axis2DeploymentOMElement;
						}
					}
				} else {
					logger.info( "No need to shutdown the tomcat" );
//					new DeleteFileAndDir().delFolder( tomcatInfo
//							.getTomcatPath()
//							+ "/webapps/axis2" );
					logger.info( "Removing the axis2 from tomcat" );
					boolean hasAxis2Removed = false;
					hasAxis2Removed = FileManipulationFactory.newFileDeletion().delFolder( 
							tomcatInfo.getTomcatPath()
							+ "/webapps/axis2" );
//					new DeleteFileAndDir().delFile( tomcatInfo.getTomcatPath()
//							+ "/webapps/axis2.war" );
					hasAxis2Removed = hasAxis2Removed && 
							FileManipulationFactory.newFileDeletion().delFile(
							tomcatInfo.getTomcatPath()
							+ "/webapps/axis2.war" );
					if(hasAxis2Removed){
						logger.info( "Axis2 removed success" );						
					}
					else{
						logger.info( "Axis2 removed failed" );
					}
					
					tomcatInfo.setAxis2( false );
					ShareTomcatList.getNewInstance().getTomcatInfo(
							tomcatInfo.getTomcatIndex() ).setAxis2( false );
					SADeploymentMsg axis2UndeploymentMsg = new SADeploymentMsg();
					axis2UndeploymentMsg.setUnDeploymentIsSuccessful( "true" );
					axis2DeploymentOMElement = axis2UndeploymentMsg
							.createSAUndeploymentResponse( tomcatInfo, "axis2" );
					return axis2DeploymentOMElement;
				}
			}
			return axis2DeploymentOMElement;
		}
	}
	

	@Override
	public OMElement restart(String port) {
		synchronized (ShareTomcatList.getNewInstance()) {
			String result = "";
			String axis2Html = "http://localhost:";
			TomcatInfo tomcatInfo = null;
			if (SharedFunction.getTomcatPath( port ) != null) {
				tomcatInfo = new TomcatInfo();
				tomcatInfo = new TomcatInfo( SharedFunction
						.getTomcatPath( port ) );
				for (int i = 0; i < ShareTomcatList.getNewInstance()
						.getTomcatListSize(); i++) {
					if (ShareTomcatList.getNewInstance().getTomcatInfo( i )
							.getPort() == Integer.parseInt(port))
						tomcatInfo.setTomcatIndex( i );
				}
				File deployedWSFolder = new File( tomcatInfo.getTomcatPath()
						+ "/webapps/axis2/WEB-INF/services/" );
				String[] deployedWSName = deployedWSFolder.list();
				for (int i = 0; i < deployedWSName.length; i++) {
					if (!deployedWSName[i]
							.equalsIgnoreCase( "SAManageService.aar" )) {
						File wsToDelete = new File( tomcatInfo.getTomcatPath()
								+ "/webapps/axis2/WEB-INF/services/"
								+ deployedWSName[i] );
						logger.info( "The file to be deleted " + i + ":"
								+ wsToDelete.getAbsolutePath() );
						wsToDelete.delete();
					}
				}
			}
			
			axis2Html = axis2Html + tomcatInfo.getPort() + "/axis2";

			TomcatController tomcatController = TomcatControllerFactory.getInstance().createTomcatController();
			tomcatController.start(tomcatInfo.getTomcatPath());
			if (SharedFunction.activeCheckByHtml(
					statusCheckTimeout, axis2Html )) {
				tomcatInfo.setStatus( "Active" );
				ShareTomcatList.getNewInstance().setTomcatInfo(
						tomcatInfo.getTomcatIndex(), tomcatInfo );
				result = "axis2 restart success.";
				logger.info( result );
				return null;
			}
			result = "axis2 does not exist , restart failed";
			logger.info( result );
			return null;
		}
	}

}
