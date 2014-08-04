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
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.w3c.dom.Document;

public class Transfer {

	public static OMElement docToOM(Document in) throws XMLStreamException,
			FactoryConfigurationError {

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer();
			try {
				StreamResult result;
				result = new StreamResult(new FileOutputStream(new File(
						"temp.xml")));
				transformer.transform(new DOMSource(in), result);
				XMLStreamReader parser = XMLInputFactory.newInstance()
						.createXMLStreamReader(new FileInputStream("temp.xml"));
				StAXOMBuilder builder = new StAXOMBuilder(parser);
				OMElement out = builder.getDocumentElement();

				return out;
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static Document omToDoc(OMElement in) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMDocument doc = factory.createOMDocument();
		doc.addChild(in);
		doc.serializeAndConsume(new FileOutputStream("temp.xml"));
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document out = builder.parse(new File("temp.xml"));
		return out;

	}

}
