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
package cn.edu.buaa.act.service4all.atomicws.servicequery.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocsBuilder {

	private static final Log logger = LogFactory.getLog(DocsBuilder.class);

	public static Document buildUpdateResDoc() {

		Document responseDoc = null;
		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element root = responseDoc.createElement("replicaUpdate");
			root.setAttribute("type", "WebService");
			Element isSuccessful = responseDoc.createElement("isSuccessufl");
			isSuccessful.setTextContent("true");
			root.appendChild(isSuccessful);
			responseDoc.appendChild(root);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return responseDoc;
	}

	public static Document buildUpdateQueryStateDoc(String state) {
		logger.info("Activate or cancle increment query...");

		Document responseDoc = null;
		try {
			responseDoc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element root = responseDoc.createElement("queryActivate");
			root.setAttribute("activated", state);
			responseDoc.appendChild(root);

		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		}
		return responseDoc;

	}


}
