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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class XMLUtil {
	public static Document StringToDoc(String str) {
		StringReader sr = new StringReader(str);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = dbf.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static String docToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty("encoding", "utf8");
			DOMSource domSource = new DOMSource(doc);
			Result result = new StreamResult(bos);
			transformer.transform(domSource, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return bos.toString();
	}

	public static OMElement XML2OMElement(File file) {
		if (file == null) {
			System.err.println("Request file does not exit");
		} else {
			try {
				XMLStreamReader parser = XMLInputFactory.newInstance()
						.createXMLStreamReader(new FileInputStream(file));
				StAXOMBuilder builder = new StAXOMBuilder(parser);

				OMDocument doc = builder.getDocument();
				OMElement creElement = doc.getOMDocumentElement();
				return creElement;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Document XML2Document(File file) {
		if (file == null) {
			System.err.println("Request file does not exit");
		} else {
			try {
				InputSource is = new InputSource(new FileInputStream(file));
				DocumentBuilderFactory fac = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = fac.newDocumentBuilder();
				Document doc = builder.parse(is);
				return doc;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static OMElement StringToOM(String str) {
		OMElement request = null;
		try {
			XMLStreamReader parser = XMLInputFactory.newInstance()
					.createXMLStreamReader(new StringBufferInputStream(str));
			StAXOMBuilder builder = new StAXOMBuilder(parser);

			OMDocument doc = builder.getDocument();
			OMElement creElement = doc.getOMDocumentElement();
			return creElement;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return request;
	}

	public static OMElement ElementToOM(Node element) {
		OMElement out = null;
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		long cur = System.currentTimeMillis();
		StreamResult result = new StreamResult(new File("trans_"
				+ String.valueOf(cur) + ".xml"));
		transformer.setOutputProperty("encoding", "utf8");
		try {
			transformer.transform(new DOMSource(element), result);
			XMLStreamReader parser = XMLInputFactory.newInstance()
					.createXMLStreamReader(
							new FileInputStream("trans_" + String.valueOf(cur)
									+ ".xml"));
			StAXOMBuilder builder = new StAXOMBuilder(parser);
			out = builder.getDocumentElement();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (javax.xml.stream.FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return out;
	}

	public static Document omToDoc(OMElement in) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMDocument doc = factory.createOMDocument();
		doc.addChild(in);
		doc.serializeAndConsume(new FileOutputStream("testtemp.xml"));
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document out = builder.parse(new File("testtemp.xml"));
		return out;
	}
}
