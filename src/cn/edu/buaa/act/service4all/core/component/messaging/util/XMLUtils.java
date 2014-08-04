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
package cn.edu.buaa.act.service4all.core.component.messaging.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * It should be the tools library for the future development
 * 
 * @author huangyj
 * 
 */
public class XMLUtils {
	private final static Log logger = LogFactory.getLog(XMLUtils.class);
	private final static String internal = " ";

	public static void logXMLDocument(Document doc, OutputStream output) {
		logXMLNode(doc, output);
	}

	public static String retrieveDocumentAsString(Document doc) {
		Node node = doc.getDocumentElement();
		return retrieveNodeAsString(node);
	}

	public static String retrieveNodeAsString(Node node) {
		String nodeStr = new String("<");
		nodeStr += node.getNodeName() + internal;
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			// add the attrubite name-value pairs
			for (int i = 0; i < attrs.getLength(); i++) {
				Node a = attrs.item(i);
				nodeStr += a.getNodeName() + "=" + a.getNodeValue() + internal;
			}
		}

		if (node.hasChildNodes()) {
			nodeStr += ">\n";
			NodeList ns = node.getChildNodes();
			for (int i = 0; i < ns.getLength(); i++) {
				nodeStr += logXMLSubNode(ns.item(i), 1);
			}
			nodeStr += "<" + node.getNodeName() + "/>\n";
		} else {
			nodeStr += "/>\n";
		}
		return nodeStr;
	}

	public static void logXMLNode(Document doc, OutputStream output) {
		String nodeStr = retrieveDocumentAsString(doc);
		try {
			output.write(nodeStr.getBytes());
			output.close();
		} catch (IOException ioe) {
			System.out
					.println("There happens some error when reading the xml fragment");
		}
	}

	/**
	 * output the xml fragment into the output stream maybe as a string or bytes
	 * 
	 * @param doc
	 * @param output
	 */
	public static void logXMLFragment(Document doc, OutputStream output) {
		logXMLNode(doc, output);
	}

	private static String logXMLSubNode(Node node, int deepth) {
		int i;
		String nodeStr = new String();
		String interStr = new String();
		for (i = 0; i < deepth; i++)
			interStr += "\t";
		nodeStr += interStr + "<" + node.getNodeName() + internal;
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			// add the attrubite name-value pairs
			for (i = 0; i < attrs.getLength(); i++) {
				Node a = attrs.item(i);
				nodeStr += a.getNodeName() + "=" + a.getNodeValue() + internal;
			}
		}
		if (node.hasChildNodes()) {
			nodeStr += ">\n";
			NodeList ns = node.getChildNodes();
			for (i = 0; i < ns.getLength(); i++) {
				nodeStr += logXMLSubNode(ns.item(i), deepth + 1);
			}
			nodeStr += interStr + "</" + node.getNodeName() + ">\n";
		} else {
			if (node.getNodeValue() != null) {
				nodeStr += ">" + node.getNodeValue() + "<" + node.getNodeName();
			}
			nodeStr += "/>\n";
		}
		return nodeStr;
	}

	public static Document readXMLFromFile(String filePath) throws Exception {
		File dFile = new File(filePath);
		if (!dFile.exists()) {
			throw new Exception("The target file doesn't exist!");
		}
		return readXMLFromFile(dFile);
	}

	public static Document readXMLFromFile(File dFile) throws Exception {
		if (dFile == null) {
			throw new Exception("The file point is null!");
		}
		if (!dFile.exists()) {
			throw new Exception("The target file doesn't exist!");
		}
		if (dFile.isDirectory() || !dFile.getName().endsWith(".xml")) {
			throw new Exception(
					"The file is not a xml file! Maybe it is a directory!");
		}
		Document doc = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = builder.parse(dFile);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return doc;
	}

	public static org.dom4j.Document newDocument() {
		return DocumentHelper.createDocument();
	}

	public static void main(String[] args) {
		try {
			Document doc = XMLUtils.readXMLFromFile("./jbi.xml");
			XMLUtils.logXMLDocument(doc, System.out);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}
}
