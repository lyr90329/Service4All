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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DocsBuilder {
private static final Log logger = LogFactory.getLog(DocsBuilder.class);
	public static Document buildeAvailableAppQueryDoc(String serviceID){
		logger.info("Query available service list from SAManager");	
		try {
			Document requestDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element rootElement2 = requestDoc.createElement(WSInvokeConstants.AVAILABLESERVICEREQUEST);
			rootElement2.setAttribute(WSInvokeConstants.TYPE, WSInvokeConstants.WEBSERVICE);
			Element serviceIDElement = requestDoc.createElement(WSInvokeConstants.SERVICEID);
			serviceIDElement.setTextContent(serviceID);
			rootElement2.appendChild(serviceIDElement);
			requestDoc.appendChild(rootElement2);
	
			return requestDoc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		logger.error("Failed to create availableServiceRequest!");
		return null;
	}
	public static OMElement buildNoAvailableServiceOMElement(String infoString) {
		OMElement responseOmElement =OMAbstractFactory
		.getOMFactory().createOMElement(
				WSInvokeConstants.INVOKERESPONSE, null);
		
		OMElement respSoapOmElement = OMAbstractFactory
		.getOMFactory().createOMElement(
				WSInvokeConstants.RESPONSESOAP, null);
		respSoapOmElement.setText(infoString);
		
		OMElement descrpOmElement = OMAbstractFactory
		.getOMFactory().createOMElement("description", null);
		descrpOmElement.setText(infoString);
		
		responseOmElement.addChild(respSoapOmElement);
		responseOmElement.addChild(descrpOmElement);
		
		return responseOmElement;
	}
}
