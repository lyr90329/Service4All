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
package cn.edu.buaa.act.service4all.webapp.qualification;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.XMLUtil;

public class UserQualification {
	private final static Log logger = LogFactory.getLog(UserQualification.class);

	public UserQualification() {
	}

	public static boolean undeployQualify(String name, String id) {
		Document doc, response;
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc.createElement(Constants.UNDEPLOY_AUTHEN);
			Element userNameEl = doc.createElement(Constants.USER_NAME);
			userNameEl.setTextContent(name);
			root.appendChild(userNameEl);
			Element serviceIDEl = doc.createElement(Constants.SERVICE_ID_QUALI);
			serviceIDEl.setTextContent(id);
			root.appendChild(serviceIDEl);
			doc.appendChild(root);
			logger.info(XMLUtil.docToString(doc));
			response = QualificationUtil.getResult(doc,
					Constants.QUALIFICATION_CONTROL);
			return isQualified(response);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean isQualified(Document document) {
		Element root = document.getDocumentElement();
		String permit = root.getElementsByTagName(Constants.QUALIFICATION)
				.item(0).getTextContent();
		if ("permit".equals(permit)) {
			return true;
		}
		return false;
	}

	public static void undeployNotification(String name, String id) {
		Document doc;
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc.createElement(Constants.UNDEPLOY_SERVICE);
			root.setAttribute(Constants.TYPE, "webapp");
			Element userName = doc.createElement(Constants.USER_NAME);
			userName.setTextContent(name);
			root.appendChild(userName);
			Element serviceList = doc.createElement(Constants.SERVICE_LIST);
			Element serviceID = doc.createElement(Constants.SERVICE_ID_QUALI);
			serviceID.setTextContent(id);
			serviceList.appendChild(serviceID);
			root.appendChild(serviceList);
			doc.appendChild(root);
			QualificationUtil.getResult(doc, Constants.SERVICE_CONTROL);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void deployNotification(String name, String id) {
		Document doc;
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc.createElement(Constants.DEPLOY_SERVICE);
			root.setAttribute(Constants.TYPE, "webapp");
			Element userName = doc.createElement(Constants.USER_NAME);
			userName.setTextContent(name);
			root.appendChild(userName);
			Element serviceList = doc.createElement(Constants.SERVICE_LIST);
			Element serviceID = doc.createElement(Constants.SERVICE_ID_QUALI);
			serviceID.setTextContent(id);
			serviceList.appendChild(serviceID);
			root.appendChild(serviceList);
			doc.appendChild(root);
			QualificationUtil.getResult(doc, Constants.SERVICE_CONTROL);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
