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
import org.apache.log4j.Logger;

import cn.edu.buaa.act.service4all.core.localsamanager.app.WebApp;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.ShareTomcatList;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;


public class WebAppManager extends AppManager {
	private static Logger logger = Logger.getLogger( WebAppManager.class );
	
	private static WebAppManager webAppManager;
	
	private WebAppManager(){
		super();
	}
	
	public synchronized static WebAppManager getInstance(){
		if(webAppManager == null){
			webAppManager = new WebAppManager();
		}
		return webAppManager;
	}

	public OMElement deploy(OMElement request) {
		OMElement fileContent = null;
		String port = null;
		String fileName = null;
		String deployResultInfo = "Deploy is done";
		String isSuccessful = "true";
		String serviceID = null;
//		Iterator<TomcatInfo> it = ShareTomcatList.getNewInstance().getTomcatInfoList();
		for (@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = request.getChildElements(); iter.hasNext();) {
			OMElement element = (OMElement) iter.next();
			if (element.getLocalName().equalsIgnoreCase("fileData")){
				fileContent = element;
				logger.info("find data element");
			}
			if (element.getLocalName().equalsIgnoreCase("fileName")){
				fileName = element.getText();
				logger.info("web app name:"+fileName);
			}
			if (element.getLocalName().equalsIgnoreCase("port")){
				 port = element.getText();
				 logger.info("web on port:"+port);
			}
			if(element.getLocalName().equalsIgnoreCase("serviceID")){
				serviceID = element.getText();
				logger.info("war app ID:"+ serviceID);
			}
		}

		String repositoryPath = Constants.REPOSITORY;
		File wsRepository = new File(repositoryPath);

		if (!wsRepository.isDirectory()) {
			wsRepository.mkdirs();
		}
		File deployWSFile = new File(repositoryPath + "/" + fileName + ".war");
		
//		if (fileContent == null) {
//			logger.error("Failed when upload the service file", new AxisFault(
//					"Failed when upload the service file"));
//			deployResultInfo = "Failed when upload the service file";
//			isSuccessful = "false";
//		}
//		OMText text = (OMText) fileContent.getFirstOMChild();
		

		
		if (deployWSFile.exists()) {
			logger.warn("[System Information][Management Operation][Application Deploy]"
					+ "The Web Service for deploying has already in the localRepository");
			String url = localWarDeploy(fileName, port, serviceID);
			if("".equals(url)){
				isSuccessful = "false";
			}

			return createDeployResponse(fileName, serviceID, url, isSuccessful, deployResultInfo);
		} else {
			
			if (fileContent == null) {
				logger.error("Failed when upload the service file", new AxisFault(
						"Failed when upload the service file"));
				deployResultInfo = "Failed when upload the service file";
				isSuccessful = "false";
			}
			OMText text = (OMText) fileContent.getFirstOMChild();
//			System.out.println("No app in local resource file");
			logger.warn("No app in local resource file");
			
			FileOutputStream os = null;
			InputStream is = null;
			try {
				DataHandler handler = (DataHandler) text.getDataHandler();
				os = new FileOutputStream(deployWSFile);
				is = handler.getInputStream();
				os.write(IOUtils.getStreamAsByteArray(is));

			} catch (Exception e) {
				logger.error("Failed when upload the service file",
						new AxisFault("Failed when upload the service file"));
				e.printStackTrace();
				deployResultInfo = "Failed when upload the service file";
				isSuccessful = "false";
			} finally {
				try {
					is.close();
					os.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			String url = localWarDeploy(fileName, port, serviceID);
			if("".equals(url)){
				isSuccessful = "false";
			}
//			String url = "";
//			isSuccessful = "false";
			
			return createDeployResponse(fileName, serviceID, url, isSuccessful, deployResultInfo);
		}
		
	}
	

	private String localWarDeploy( String name, String port, String webAppID ){
		String warName = name + ".war";
		String localRepoPath = Constants.REPOSITORY;
		File localRepoFolder = new File( localRepoPath );
		Iterator<TomcatInfo> it = ShareTomcatList.getNewInstance()
				.getTomcatInfoList();
		String deployPathOut = null;
		while (it.hasNext()) {
			TomcatInfo tomcatInfo = it.next();
			if (tomcatInfo.getPort() == Integer.parseInt( port )) {
				deployPathOut = tomcatInfo.getTomcatPath() + "/webapps/";
				logger.info( "path of web app deployment;" + deployPathOut );
				tomcatInfo.setAppserver( true );
			}
		}
		if (localRepoFolder.exists() && localRepoFolder.isDirectory()) {
			File warToDeploy = new File(localRepoPath  + warName);
			try {
				FileManipulationFactory.newFileReproduction(/* null, null */)
						.copyAppFile(warToDeploy, deployPathOut + warName );
				String invokeUrl = Constants.LOCAL_IP_WITH_HTTP + ":" + port + "/"
				+ warName.split( "\\." )[0];
				
				//update
				WebApp app = new WebApp(name, invokeUrl, webAppID);
				HostInfo hostInfo = HostInfo.getInstance();
				hostInfo.addApp(app, Integer.parseInt(port));
				
				return invokeUrl;
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			
		}
		return "";

	}
	
	private OMElement createDeployResponse(String name,String id,String url,String isSucc,String resultInfo){
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement loadRemoteServiceResponse = omFactory.createOMElement(
				"loadRemoteServiceResponse", null);
		
		OMElement serviceFileName = omFactory.createOMElement("serviceFileName", null);
		serviceFileName.setText(name);
		
		OMElement deployResponse = omFactory.createOMElement("deployResponse", null);
		OMAttribute deployResponseTypeAttr = omFactory.createOMAttribute("Type", null, "app");
		deployResponse.addAttribute(deployResponseTypeAttr);
		
		OMElement isSuccessfulElement = omFactory.createOMElement("isSuccessful", null);
		isSuccessfulElement.setText(isSucc);
		
		OMElement deployResultInfoElement = omFactory.createOMElement("deployResultInfo", null);
		deployResultInfoElement.setText(resultInfo);
		
		OMElement deployUrlOM = omFactory.createOMElement("invokeUrl",null);
		deployUrlOM.setText(url);
		
		deployResponse.addChild(isSuccessfulElement);
		deployResponse.addChild(deployResultInfoElement);
		deployResponse.addChild(deployUrlOM);
		
		loadRemoteServiceResponse.addChild(deployResponse);
		loadRemoteServiceResponse.addChild(serviceFileName);
		logger.info("response of deployment:" + loadRemoteServiceResponse);
		
		return loadRemoteServiceResponse;
	}
	
	@Override
	public OMElement undeploy(OMElement request) {
		OMElement fileName = null;
		String port=null,serviceID = null,undeployPathOut = null;
		Iterator<TomcatInfo> it = ShareTomcatList.getNewInstance().getTomcatInfoList();
		for (@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = request.getChildElements(); iter.hasNext();) {
			OMElement element = (OMElement) iter.next();
			if (element.getLocalName().equalsIgnoreCase("fileName")){
				fileName = element;
			}
			if (element.getLocalName().equalsIgnoreCase("port"))
				 port = element.getText();
			if(element.getLocalName().equalsIgnoreCase("serviceID")){
				serviceID = element.getText();
			}
		}
		logger.info("undeployment on port:" + port);
		String name = fileName.getText();
		logger.info("The app name to be undeployed:" + name);
		while (it.hasNext()) {
			TomcatInfo tomcatInfo = it.next();
			if (tomcatInfo.getPort() == Integer.parseInt(port)) {
				undeployPathOut = tomcatInfo.getTomcatPath()
						+ "/webapps/";
				logger.info("undeplpoyment path:" + undeployPathOut);
			}
		}
		File localwarRepoPath = new File(Constants.REPOSITORY + "/" + fileName) ;
		File localRepoFolder = new File(undeployPathOut);
		String[] repoWS = localRepoFolder.list();
		logger.info(repoWS);
		boolean flag = true;
		for (int i = 0; i < repoWS.length; i++) {
			if (repoWS[i].equalsIgnoreCase(fileName.getText())) {
				File wsTounDeploy = new File(undeployPathOut + repoWS[i]);
				logger.info("web app to be undeployed:" + wsTounDeploy.toString());
				if(!wsTounDeploy.delete()){
					logger.info("undeploy failes.");
					flag = false;
					break ;
				}
				localwarRepoPath.delete();
			}
		}
		
	
		return createUdeployReponse(port, serviceID, String.valueOf(flag));
		
	}
	
	private OMElement createUdeployReponse(String port, String serviceID,String isSucc){
		OMFactory omf = OMAbstractFactory.getOMFactory();
		OMElement response = omf.createOMElement("undeployResponse",null);
		OMElement isSuccessful = omf.createOMElement("isSuccessful",null);
		isSuccessful.setText(isSucc);
		
		OMElement idOM = omf.createOMElement("serviceID",null);
		idOM.setText(serviceID);
		response.addChild(isSuccessful);
		response.addChild(idOM);
		
		if(Boolean.parseBoolean( isSucc )){
			HostInfo.getInstance().deleteApp( Integer.parseInt(port), serviceID );
		}
		
		return response;
	}
	
	
}
