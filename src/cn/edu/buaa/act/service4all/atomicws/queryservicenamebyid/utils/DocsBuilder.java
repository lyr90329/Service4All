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
package cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocsBuilder {
	public static Document buildQueryRequestDoc(String id) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element rootElement = doc.createElement("queryAppInfoRequest");
			rootElement.setAttribute("type", "webservice");
			rootElement.setAttribute("appId", id);
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return doc;
	}

	public static Document buildResponseDoc(Document respFromBus) {

		Element rootElement = respFromBus.getDocumentElement();
		String serviceID = rootElement.getAttribute("appId");
		Element repetitionsElement = (Element) rootElement
				.getElementsByTagName("repetitions").item(0);
		int length = Integer.parseInt(repetitionsElement.getAttribute("num"));

		Document returnDocument = null;
		try {
			returnDocument = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element rootElement4return = returnDocument
				.createElement("ContainerQueryResponseForExecution");
		rootElement4return.setAttribute("type", "WebService");
		rootElement4return.setAttribute("serviceID", serviceID);
		Element servicesElement = returnDocument.createElement("services");
		servicesElement.setAttribute("length", String.valueOf(length));

		for (int i = 0; i < length; i++) {
			Element repetition = (Element) repetitionsElement
					.getElementsByTagName("repetition").item(i);
			Element urlElement = returnDocument.createElement("url");
			urlElement.setTextContent(repetition.getAttribute("invokeUrl"));
			servicesElement.appendChild(urlElement);
		}

		rootElement4return.appendChild(servicesElement);
		returnDocument.appendChild(rootElement4return);

		return returnDocument;

	}
}
