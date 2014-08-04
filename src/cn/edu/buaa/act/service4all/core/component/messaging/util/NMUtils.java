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

import java.io.IOException;
import java.io.Reader;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.JbiConstants;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StAXSourceTransformer;
import org.apache.servicemix.jbi.messaging.MessageExchangeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * @author huangyj
 * 
 */
public class NMUtils {

	private static final Log log = LogFactory.getLog(NMUtils.class);

	public static void logNM(NormalizedMessage nm, Log log) {
		Source cnt = nm.getContent();
		String cntStr = readSource(cnt);
		log.info(cntStr);
	}

	public static String readSource(Source cnt) {
		char[] bytes = new char[1024];
		String tmp = new String();
		if (cnt instanceof StreamSource) {
			Reader input = ((StreamSource) cnt).getReader();
			try {
				while (input.read(bytes) != -1) {
					tmp += bytes.toString();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return tmp;
		}
		if (cnt instanceof DOMSource) {
			DOMSource src = (DOMSource) cnt;
			Node root = src.getNode();
			tmp = XMLUtils.retrieveNodeAsString(root);
			return tmp;
		}
		if (cnt instanceof SAXSource) {
			StAXSourceTransformer transformer = new StAXSourceTransformer();
			try {
				Document doc = transformer.toDOMDocument(cnt);
				// write the document to the temp file
				return XMLUtils.retrieveDocumentAsString(doc);
				// return transformer.toDOMDocument(cnt);
			} catch (SAXException se) {
				log.error("Error for the getting the document source", se);
			} catch (TransformerException e) {
				log.error("Error for the getting the document source", e);
			} catch (ParserConfigurationException e) {
				log.error("Error for the getting the document source", e);
			} catch (IOException e) {
				log.error("Error for the getting the document source", e);
			}
		}
		return null;
	}

	public static Fault createErrorNM(MessageExchangeImpl impl, Document cnt) {
		try {
			Fault fault = impl.createFault();
			DOMSource domSrc = new DOMSource();
			domSrc.setNode(cnt);
			fault.setContent(domSrc);
			return fault;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static NormalizedMessage createOutNM(MessageExchangeImpl impl,
			Document cnt) {
		try {
			NormalizedMessage out = impl.createMessage();
			DOMSource domSrc = new DOMSource();
			domSrc.setNode(cnt);
			out.setContent(domSrc);
			impl.setOutMessage(out);
			return out;
		} catch (MessagingException e) {
			log.error("Can't create out Normalized Message: " + e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	public static NormalizedMessage createOutNM(MessageExchangeImpl impl,
			String content) {
		try {
			log.info("Create the output Normalized message");
			NormalizedMessage out = impl.createMessage();
			DOMSource source = new DOMSource();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element resp = doc.createElement("response");
			resp.setTextContent(content);
			source.setNode(resp);
			out.setContent(source);
			return out;
		} catch (MessagingException e) {
			log.error("Can't create out Normalized Message");
			// e.printStackTrace();
		} catch (ParserConfigurationException e) {
			log.warn("Can't create a document builder", e);
		}
		return null;
	}

	/**
	 * return a MessageExchange as a string value by specified format the key
	 * information includes : exchangeID exchange status sender
	 * endpoint(Namespace) target service(Namespace) input message(to be
	 * considered) output message(to be considered)
	 * 
	 * @param me
	 * @return
	 */
	public static String formatMessageExchange(MessageExchange me) {
		QName targetService = me.getService();
		String senderEndpoint = null;
		if (me.getProperty(JbiConstants.SENDER_ENDPOINT) != null) {
			senderEndpoint = (String) me
					.getProperty(JbiConstants.SENDER_ENDPOINT);
		}
		String exchangeID = me.getExchangeId();
		ExchangeStatus status = me.getStatus();
		String result = "\n[\n";
		result += "\tExchangeID: " + exchangeID + "\n";
		result += "\tExchange Status: " + status + "\n";
		result += "\tSender Endpoint: " + senderEndpoint + "\n";
		result += "\tTarget Service: " + targetService + "\n";
		result += "]\n";
		return result;
	}

	/**
	 * get the content of the normalized message
	 * 
	 * @param nm
	 * @return
	 */
	public static Document retrieveContent(NormalizedMessage nm) {

		log.info("retrieving the content of the normalized message, and transforming to Document");
		SourceTransformer transformer = new StAXSourceTransformer();
		try {

			Document docCnt = transformer.toDOMDocument(nm);
			return docCnt;
		} catch (SAXException se) {
			log.error(se.getMessage());
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		} catch (ParserConfigurationException pce) {
			log.error(pce.getMessage());
		} catch (TransformerException te) {
			log.error(te.getMessage());
		} catch (MessagingException me) {
			log.error(me.getMessage());
		}
		return null;
	}
}
