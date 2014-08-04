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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author huangyj
 * 
 */
public class XMLHandler extends DefaultHandler {

	private final Log log = LogFactory.getLog(XMLHandler.class);
	private Document doc;
	private Node currentNode;

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String cnt = new String(ch, start, length);
		String first = cnt.replaceAll("&lt;", "<");
		String sec = first.replaceAll("&gt;", ">");
		currentNode.setTextContent(sec);
	}

	public void endDocument() throws SAXException {
		log.info("The end of the document");
		this.currentNode = doc;
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		currentNode = currentNode.getParentNode();
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void startDocument() throws SAXException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.doc = builder.newDocument();
			currentNode = doc;
		} catch (ParserConfigurationException e) {
			log.warn("Can't create a new document", e);
		}
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		Node newNode = doc.createElement(name);
		this.currentNode.appendChild(newNode);
		this.currentNode = newNode;
	}

	public Document getSourceContent() {
		return doc;
	}
}
