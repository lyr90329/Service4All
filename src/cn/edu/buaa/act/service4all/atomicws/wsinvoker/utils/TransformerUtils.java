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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class TransformerUtils {
	private final static String internal = " ";
	private final static String tag = "  ";
	private static Logger logger = Logger.getLogger("TransformerUtils");


	public static String getStringFromDocument(Document doc) {
		NodeList nodeList = doc.getChildNodes();
		StringBuffer sb = new StringBuffer(1024);
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			sb.append(getStringFromNode(node, 0));
		}
		return sb.toString();
	}

	public  static void getOutputStreamFromDocument(Document doc, OutputStream output) {
		String nodeStr = getStringFromDocument(doc);
		try {
			output.write(nodeStr.getBytes());
			output.close();
		} catch (IOException ioe) {
			logger.warn("There happens some error when reading the document");
		}
	}

	public static String getFirstElementBodyStringByTagName(Document inDoc,
			String tagName) {
		NodeList nodeList = inDoc.getElementsByTagName(tagName);
		Node node = (Node) nodeList.item(0);// The first element
	
		NodeList nodeChildren = node.getChildNodes();
		StringBuffer sb = new StringBuffer(1024);

		int length = nodeChildren.getLength();
		if (length == 1) {
			sb.append(getStringFromNode(nodeChildren.item(0), 0));
		} else {
			for (int i = 1; i < length - 1; i++) {
				Node nodeTmp = nodeChildren.item(i);
				sb.append(getStringFromNode(nodeTmp, 0));
			}
		}
		return sb.toString();
	}


	public static OMElement getFirstOMElementByTagName(Document doc,
			String tagName) {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer;
		StreamResult result = new StreamResult();

		NodeList nodeList = doc.getElementsByTagName(tagName);

		Node node = (Node) nodeList.item(0);
		if (node == null) {
			throw new NullPointerException("The node is null");
		}

		try {
			transformer = factory.newTransformer();
			DOMSource source = new DOMSource(node);

			File file = new File("tmp.xml");
			if (!file.exists()) {
				file.createNewFile();
			}

			result.setOutputStream(new FileOutputStream(file));

			transformer.transform(source, result);

			javax.xml.stream.XMLStreamReader parser = XMLInputFactory
					.newInstance().createXMLStreamReader(
							new FileInputStream(file));

			StAXOMBuilder builder = new StAXOMBuilder(parser);

			OMElement e = builder.getDocumentElement();


			file.delete();

			return e;
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
			// }

		}
		return null;

	}

	/**
	 * get xml string from the given node in the given depth.
	 */
	private static String getStringFromNode(Node node, int depth) {
		int i;
		StringBuffer strBuffer = new StringBuffer(1024);
		String interStr = "";
		for (i = 0; i < depth; i++)
			interStr += tag;

		if (node instanceof Element) {
			strBuffer.append(interStr + "<" + node.getNodeName());
			/*
			 * add attribute pairs to the strBuffer
			 */
			if (node.hasAttributes()) {
				NamedNodeMap attrs = node.getAttributes();
				for (i = 0; i < attrs.getLength(); i++) {
					Node a = attrs.item(i);
					strBuffer.append(internal + a.getNodeName() + "=\""
							+ a.getNodeValue() + "\"");
				}
			}
			/*
			 * add child nodes to the strBuffer
			 */
			if (node.hasChildNodes()) {
				strBuffer.append(">");
				NodeList ns = node.getChildNodes();
				for (i = 0; i < ns.getLength(); i++) {
					Node nodeChild = ns.item(i);
					strBuffer.append(getStringFromNode(nodeChild, depth + 1));
				}
				if (i == 1) {
					strBuffer.append("</" + node.getNodeName() + ">");
				} else {
					strBuffer
							.append(interStr + "</" + node.getNodeName() + ">");
				}
			} else {
				strBuffer.append("/>");
			}
		} else if (node instanceof CDATASection) {
			strBuffer.append(interStr + "<![CDATA[\n");
			strBuffer.append(interStr + tag + node.getNodeValue());
			strBuffer.append(interStr + "]]>");
		} else if (node instanceof Comment) {
			strBuffer.append(interStr + "<!--" + node.getNodeValue() + "-->");
		} else if (node instanceof Document) {
			strBuffer.append(getStringFromNode(
					((Document) node).getDocumentElement(), depth + 1));
		} else if (node instanceof DocumentFragment) {
			// TODO
		} else if (node instanceof Entity) {
			// TODO
		} else if (node instanceof EntityReference) {
			// TODO
		} else if (node instanceof Notation) {
			strBuffer.append(interStr + "<!--" + node.getNodeValue() + "-->");
		} else if (node instanceof Text) {
			strBuffer.append(node.getNodeValue());
		}
		else {// ProcessInstuction
			strBuffer.append(interStr + "<?" + node.getNodeValue() + "?>");
		}

		return strBuffer.toString();
	}

	@SuppressWarnings("finally")
	public static Document getDocumentFromFile(FileInputStream inStream) {
		Document doc = null;
		try {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = (DocumentBuilder) docBF
					.newDocumentBuilder();
			doc = docBuilder.parse(inStream);
		} catch (Exception e) {
			logger.warn("readDOM throw err: " + e.getClass().getName()
					+ e.getMessage());
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				logger.warn("Fail to close input stream: " + e.getMessage());
			}
			return doc;
		}
	}


	@SuppressWarnings("finally")
	public static Document getDocumentFromString(String xml) {

		ByteArrayInputStream inStream = null;
		try {
			inStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ue) {
			logger.warn("Fail to create byte input stream from string:"
					+ ue.getMessage());
		}
		Document doc = null;
		try {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = (DocumentBuilder) docBF
					.newDocumentBuilder();
			doc = docBuilder.parse(inStream);
		} catch (Exception e) {
			logger.warn("readDOM throw err: " + e.getClass().getName()
					+ e.getMessage());
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (Exception e) {
				logger.warn("Fail to close input stream: " + e.getMessage());
			}
			return doc;
		}
	}


	public static Document getDocumentFromOMElement(OMElement response) {


		File tempFile = new File("temp.xml");
		if (!tempFile.exists()) {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileOutputStream outputStream;
		Document document = null;
		try {
			outputStream = new FileOutputStream(tempFile);
			response.serialize(outputStream);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = null;
			builder = factory.newDocumentBuilder();
			document = builder.parse(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;

	}


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
	
	public static void getFileFromDocument(Document document, File file) {
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			getOutputStreamFromDocument(document, out);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	public static String omToString(OMElement in) {
		String string = null;
		string = getStringFromDocument(getDocumentFromOMElement(in));
		return string;
	}
	
}
