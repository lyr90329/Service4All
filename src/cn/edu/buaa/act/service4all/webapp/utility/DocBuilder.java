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
package cn.edu.buaa.act.service4all.webapp.utility;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.webapp.WebApplication;
import cn.edu.buaa.act.service4all.webapp.appliance.AppServer;
import cn.edu.buaa.act.service4all.webapp.appliance.NginxServer;

public class DocBuilder {

	private static Log logger = LogFactory.getLog(DocBuilder.class);
	
	//this is a new code for nginx
	//----------------------------DRPS 201308----------------------------------//
	private static Config Config = new Config();
	static NginxServer nginx = new NginxServer(Config.getNginxIp(), Config.getNginxPort(), 
			Config.getNginxListeningPort());
	//------------------------------------------------------------------------//

	/**
	 * build exception documents when there is no available containers
	 */
	public static Document buildNoContainerDoc(String serviceID,
			String fileName, String errorString) {
		logger.info("no containers available,build exception response.");
		DocumentBuilder builder;
		Document doc = null;

		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
			Element element = doc.createElement(Constants.DEPLOY_WAR_RESPONSE);
			element.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element name = doc.createElement(Constants.SERVICE_NAME);
			name.setTextContent(fileName);
			Element id = doc.createElement(Constants.SERVICE_ID);
			id.setTextContent(serviceID);
			Element isSuccessful = doc.createElement(Constants.IS_SUCC);
			isSuccessful.setTextContent("false");
			Element deployResultInfo = doc.createElement(Constants.EXCEPTION);
			deployResultInfo.setTextContent(errorString);
			element.appendChild(id);
			element.appendChild(name);
			element.appendChild(isSuccessful);
			element.appendChild(deployResultInfo);
			doc.appendChild(element);
		} catch (Exception e) {
		}
		return doc;
	}

	/**
	 * build request documents to query available containers
	 */
	public static Document buildRequestDoc(String serviceName, int deployNum) {
		logger.info("build request documents to query available containers.");
		Document doc = null;
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			doc = fac.newDocumentBuilder().newDocument();
			Element element = doc
					.createElement(Constants.CONTAINTER_QUERY_REQUEST);
			element.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element name = doc.createElement(Constants.DEPLOY_NAME);
			name.setTextContent(serviceName);
			element.appendChild(name);
			Element deployNumElement = doc.createElement(Constants.DEPLOY_NUM);
			deployNumElement.setTextContent(String.valueOf(deployNum));
			element.appendChild(deployNumElement);
			doc.appendChild(element);
			logger.info("container query request:\n" + XMLUtil.docToString(doc));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * build request documents to query available containers for specified service
	 */
	public static Document buildRequestDoc(String serviceName, int deployNum, String serviceId) {
		logger.info("build request documents to query available containers.");
		Document doc = null;
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		try {
			doc = fac.newDocumentBuilder().newDocument();
			Element element = doc
					.createElement(Constants.CONTAINTER_QUERY_REQUEST);
			element.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element name = doc.createElement(Constants.DEPLOY_NAME);
			name.setTextContent(serviceName);
			element.appendChild(name);
			Element deployNumElement = doc.createElement(Constants.DEPLOY_NUM);
			deployNumElement.setTextContent(String.valueOf(deployNum));
			element.appendChild(deployNumElement);
			Element serviceIdElement = doc.createElement(Constants.SCALE_OUT_ID);
			serviceIdElement.setTextContent(serviceId);
			element.appendChild(serviceIdElement);
			doc.appendChild(element);
			logger.info("container query request:\n" + XMLUtil.docToString(doc));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * deploy response when no exception occurs
	 */
	public static Document buildDeployResponse(ExchangeContext context) {
		logger.info("build deploy response");
		Document responseDoc = null;
		String serviceID = (String) context.getData(Constants.SERVICE_ID);
		String serviceName = (String) context.getData(Constants.SERVICE_NAME);
		String isSuccessful = (String) context.getData(Constants.IS_SUCC);
		String info = (String) context.getData(Constants.INFO);
		String invokeUrl = (String) context.getData("InvokeUrl");

		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element element = responseDoc
					.createElement(Constants.DEPLOY_WAR_RESPONSE);
			element.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element id = responseDoc.createElement(Constants.SERVICE_ID);
			id.setTextContent(serviceID);
			element.appendChild(id);
			Element name = responseDoc.createElement(Constants.SERVICE_NAME);
			name.setTextContent(serviceName);
			element.appendChild(name);
			Element isSuccessfulElement = responseDoc
					.createElement(Constants.IS_SUCC);
			isSuccessfulElement.setTextContent(isSuccessful);
			element.appendChild(isSuccessfulElement);
			Element deployResultInfo = responseDoc
					.createElement(Constants.INFO);
			deployResultInfo.setTextContent(info);
			element.appendChild(deployResultInfo);
			Element invokeUrlEl = responseDoc.createElement("invokeUrl");
			invokeUrlEl.setTextContent(invokeUrl);
			element.appendChild(invokeUrlEl);
			responseDoc.appendChild(element);
			logger.info("the document returned to the deployer."
					+ XMLUtil.docToString(responseDoc));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return responseDoc;
	}

	/**
	 * build exception response document when user's authentication fails
	 */
	public static Document buildAuthenticationFailureDoc(String serviceID) {

		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element feed = doc.createElement("undeployResponse");
			feed.setAttribute("type", "app");
			Element id = doc.createElement("serviceID");
			id.setTextContent(serviceID);
			Element isSuccessful = doc.createElement("isSuccessful");
			isSuccessful.setTextContent("false");
			Element deployResultInfo = doc
					.createElement(Constants.UNDEPLOY_RESULT);
			deployResultInfo
					.setTextContent("You are not allowed to undeploy this service for you are not the owner!");
			feed.appendChild(id);
			feed.appendChild(isSuccessful);
			feed.appendChild(deployResultInfo);
			doc.appendChild(feed);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * build document foe deployment feedback
	 */
	public static Document buildDeploymentFeedbackDoc(ExchangeContext context) {
		Document doc = null;
		try {
			String serviceName = (String) context
					.getData(Constants.SERVICE_NAME);
			String serviceID = (String) context.getData(Constants.SERVICE_ID);
			
			//----------------------------DRPS 201308----------------------------------//
			String invokeUrl = "http://" + nginx.getHost() + ":"
					+ nginx.getPort() + "/" + serviceID + "_" + serviceName + "/";
			String userName = (String) context.getData(Constants.USER_NAME);
			//------------------------------------------------------------------------//
			
			
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element root = doc.createElement(Constants.DEPLOY_FEEDBACK);
			root.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element idElment = doc.createElement(Constants.SERVICE_ID);
			idElment.setTextContent(serviceID);
			root.appendChild(idElment);
			Element nameElement = doc.createElement(Constants.SERVICE_NAME);
			nameElement.setTextContent((String) context
					.getData(Constants.SERVICE_NAME));
			root.appendChild(nameElement);
			Element isSuccElement = doc.createElement(Constants.IS_SUCC);
			isSuccElement.setTextContent((String) context
					.getData(Constants.IS_SUCC));
			root.appendChild(isSuccElement);
			@SuppressWarnings("unchecked")
			ArrayList<AppServer> containers = (ArrayList<AppServer>) context
					.getData(Constants.DEPLOY_RESULT);
			for (AppServer appServer : containers) {
				Element container = doc
						.createElement(Constants.DEPLOY_CONTAINER);
				container.setAttribute(Constants.ID, appServer.getId());
				//new code for nginx
				//String invokeUrl = appServer.getId().split("_")[1] + "/" + serviceName;
				logger.info("invokeUrl is " + invokeUrl);
				container.setAttribute(Constants.INVOKE_URL, invokeUrl);
				root.appendChild(container);
			}
			doc.appendChild(root);
			logger.info("deployment feedback to component:"
					+ XMLUtil.docToString(doc));
			
			//----------------------------DRPS 201308----------------------------------//
			(new WebAppInfoSegmentSender()).sendInfo(createDeployedConfInfo(context), nginx,serviceID, serviceName, userName);
			//------------------------------------------------------------------------//
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	//----------------------------DRPS 201308----------------------------------//
	/**
	 * create deployment information segment
	 * @param context
	 * @return
	 */
	public static Document createDeployedConfInfo(ExchangeContext context) {
		Document confInfo = null;
		String serviceID = (String) context.getData(Constants.SERVICE_ID);
		String serviceName = (String) context.getData(Constants.SERVICE_NAME);

		try {
			confInfo = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			confInfo.setXmlVersion("1.0");
			Element root = confInfo.createElement("confInfoSegment");
			Element application = confInfo.createElement("application");
			Element upstream = confInfo.createElement("upstream");
			Element location = confInfo.createElement("location");
			String upstreamText = "";
			String locationText = "";

			upstreamText = "\n upstream" + "  " + serviceID + "_" + serviceName
					+ "  {\n";
			@SuppressWarnings("unchecked")
			ArrayList<AppServer> containers = (ArrayList<AppServer>) context
					.getData(Constants.DEPLOY_RESULT);
			for (AppServer appServer : containers) {
				String serverText = null;
				String serverUrl = appServer.getUrl();
				String temp = serverUrl.replace("http://", "");
				serverText = "\n server  " + temp.split("/")[0] + ";\n";
				upstreamText += serverText;
			}
			upstreamText += "}\n";
			locationText = "location ~* /" + serviceID + "_" + serviceName + "/(.*)$ {\n";
			locationText += "rewrite /" + serviceID + "_" + serviceName + "/(.*)$";
			locationText += " /" + serviceName + "/$1  break;\n";
			locationText += "proxy_pass http://" + serviceID + "_"
					+ serviceName + ";\n" + "} \n";

			root.setAttribute("operation", "de");
			application.setAttribute("id", serviceID);
			application.setAttribute("type", "webApp");
			application.setAttribute("name", serviceName);
			upstream.setTextContent(upstreamText);
			location.setTextContent(locationText);
			application.appendChild(upstream);
			application.appendChild(location);
			root.appendChild(application);
			confInfo.appendChild(root);
			confInfo.setXmlStandalone(true);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return confInfo;
	}
	//------------------------------------------------------------------------//

	public static Document buildWebAppQeuryDoc(String serviceID) {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element element = doc.createElement(Constants.UNDEPLOY_REQEUST);
			element.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element id = doc.createElement(Constants.SERVICE_ID);
			id.setTextContent(serviceID);
			element.appendChild(id);
			doc.appendChild(element);
			logger.info("qeury app info for undeployment:\n"
					+ XMLUtil.docToString(doc));
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document buildUndeployFbDoc(WebApplication webApp) {
		Document doc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc
					.createElement(Constants.UNDEPLOY_FEEDBACK_REQUEST);
			Element serviceID = doc.createElement(Constants.SERVICE_ID);
			serviceID.setTextContent(webApp.getServiceID());
			root.appendChild(serviceID);
			doc.appendChild(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Document buildUndeployAppNotExistedDoc(String appId) {
		Document doc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc.createElement(Constants.UNDEPLOY_RESPONSE);
			Element exception = doc.createElement(Constants.UNDEPLOY_RESULT);
			exception.setTextContent("The app " + appId
					+ " you are looked for does not existed!.");
			root.appendChild(exception);
			doc.appendChild(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Document buildUndeployResponse(ExchangeContext context) {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = builder.newDocument();
		Element root = doc.createElement(Constants.WAR_DEPLOY_RESPONSE);
		String serviceID = (String) context.getData(Constants.SERVICE_ID);
		String result = (String) context.getData(Constants.UNDEPLOY_RESULT);
		Element IDEl = doc.createElement(Constants.SERVICE_ID);
		IDEl.setTextContent(serviceID);
		Element resultEl = doc.createElement(Constants.UNDEPLOY_RESULT);
		resultEl.setTextContent(result);
		root.appendChild(resultEl);
		root.appendChild(IDEl);
		doc.appendChild(root);
		return doc;
	}

	public static Document buildUndeployDenyRsponse(Document response,
			ExchangeContext context) {
		Element rootRsp = response.getDocumentElement();
		String result = rootRsp.getElementsByTagName("statement").item(0)
				.getTextContent();
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = builder.newDocument();
		Element root = doc.createElement(Constants.WAR_DEPLOY_RESPONSE);
		String serviceID = (String) context.getData(Constants.SERVICE_ID);
		Element IDEl = doc.createElement(Constants.SERVICE_ID);
		IDEl.setTextContent(serviceID);
		Element resultEl = doc.createElement(Constants.UNDEPLOY_RESULT);
		resultEl.setTextContent(result);
		root.appendChild(resultEl);
		root.appendChild(IDEl);
		doc.appendChild(root);
		return doc;
	}
}
