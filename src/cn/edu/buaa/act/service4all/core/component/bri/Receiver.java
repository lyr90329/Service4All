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
package cn.edu.buaa.act.service4all.core.component.bri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jbi.management.DeploymentException;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.common.DefaultServiceUnit;
import org.apache.servicemix.common.endpoints.ProviderEndpoint;
import org.apache.servicemix.jbi.jaxp.SourceTransformer;
import org.apache.servicemix.jbi.jaxp.StAXSourceTransformer;
import org.apache.servicemix.jbi.messaging.MessageExchangeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import cn.edu.buaa.act.service4all.core.component.EngineConstants;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import cn.edu.buaa.act.service4all.core.component.messaging.util.NMUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageContextException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public abstract class Receiver extends ProviderEndpoint {
	protected final Log logger = LogFactory.getLog(Receiver.class);
	public final static String ATTACHMENT = "attachment";
	protected BusinessUnit unit;
	protected AppEngineContext context;
	protected String SERVICE;
	protected String ENDPOINT;
	protected String INTERFACE;
	protected String SU_NAME;

	public void setBusinessUnit(BusinessUnit unit) {
		this.unit = unit;
	}

	public void process(MessageExchange me) {
		// logger.info("Receiving a request message: " + me.getExchangeId());
		MessageSession session = unit.startSession(me);
		if (session != null) {
			// logger.info("Start a message exchange session");
			MessageExchangeImpl impl = (MessageExchangeImpl) me;
			NormalizedMessage inNM = impl.getInMessage();
			Document requestDoc;
			List<Object> attachments = new ArrayList<Object>();
			requestDoc = retrieveContent(inNM);
			// get the session context
			// ExchangeContext context = unit.getContext(me);
			ExchangeContext context = session.getContext();

			if (context == null) {
				logger.error(
						"The context is null when receiving a root exchange",
						new MessageContextException(
								"The context is null when receiving a root exchange"));
				sendExceptionMessage(
						"The context is null when receiving a root exchange",
						context);
				return;
			}

			// if the request document is null
			if (requestDoc == null) {
				// log the exception condition and send exception response
				// message
				logger.warn("The request document is null!");
				sendExceptionMessage("The request document is null!", context);
				return;
			}

			try {

				if (hasAttachment(inNM)) {
					Iterator<?> it = inNM.getAttachmentNames().iterator();
					while (it.hasNext()) {
						String attachmentName = (String) it.next();
						Object attachment = inNM.getAttachment(attachmentName);
						logger.info("Adding an attachment to the context: "
								+ attachment.getClass().getSimpleName());
						attachments.add(attachment);
					}
					context.storeData(ATTACHMENT, attachments);
				}
				// handling the request message
				handlRequest(requestDoc, context);
			} catch (MessageExchangeInvocationException e) {
				logger.warn("There is an exception when handling request: "
						+ e.getMessage());
				// sendExceptionMessage(e.getMessage(), context);
				unit.handleInvocationException(e);
			}
		} else {
			// the MessageExchange's status is done or error
			// so have to clean the session or do something for the error
			if (me.getStatus() != ExchangeStatus.DONE) {
				logger.error("Can't start the session, since the MessageExchange's status is : "
						+ me.getStatus());
				sendExceptionMessage(
						"Can't start the session, since the MessageExchange's status is : "
								+ me.getStatus(), me);
			}
		}
	}
	/**
	 * judge whether the normalized message has attachments
	 * 
	 * @param nm
	 * @return
	 */
	private boolean hasAttachment(NormalizedMessage nm) {
		return nm.getAttachmentNames().size() > 0;
	}
	/**
	 * handle request message if there is next invocation return false, else
	 * return true
	 * 
	 * if there are some status data, it would be set in the context
	 * 
	 * this method must be synchronized
	 * 
	 * @param doc
	 */
	public abstract void handlRequest(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException;

	public abstract Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException;
	/**
	 * get the content of the normalized message
	 * 
	 * @param nm
	 * @return
	 */
	protected Document retrieveContent(NormalizedMessage nm) {
		// logger.info("retrieving the content of the normalized message, and
		// transforming to Document");
		SourceTransformer transformer = new StAXSourceTransformer();
		try {
			Document docCnt = transformer.toDOMDocument(nm);
			return docCnt;
		} catch (SAXException se) {
			logger.error(se.getMessage());
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		} catch (ParserConfigurationException pce) {
			logger.error(pce.getMessage());
		} catch (TransformerException te) {
			logger.error(te.getMessage());
		} catch (MessagingException me) {
			logger.error(me.getMessage());
		}
		return null;
	}

	public void sendResponseMessage(Document response, ExchangeContext context) {
		MessageSession session = context.getSession();
		MessageExchangeImpl exchange = (MessageExchangeImpl) session
				.getRootExchange();

		if (NMUtils.createOutNM(exchange, response) == null) {
			logger.error("Can't create the output Normalized Message, so don't send the message");
			return;
		}
		// unit.sendRequest( exchange, context );
		// added by zjg 2012.06.15
		unit.sendResponse(exchange);
		unit.closeSession(exchange);
	}

	public void sendResponseMessage(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// logger.info("Sending the response message through receiver");
		Document respDoc = this.createResponseDocument(context);
		if (respDoc == null) {
			logger.error("Can't create the response document Message, so don't send the message");
			return;
		}
		this.sendResponseMessage(respDoc, context);
	}
	/**
	 * send the exception message
	 * 
	 * @param exception
	 */
	public void sendExceptionMessage(String exception, ExchangeContext context) {
		logger.info("Send the exception message exchange");
		MessageSession session = context.getSession();
		MessageExchangeImpl exchange = (MessageExchangeImpl) session
				.getRootExchange();
		try {
			Document doc = createExceptionDocument(exception);
			// not only create the output Normalized Message
			// but also set into the message exchange
			if (NMUtils.createOutNM(exchange, doc) == null) {
				logger.error("Can't create the output Normalized Message, so don't send the message");
				return;
			}
			unit.sendErrorResponse(exchange);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// then do nothing
		}
	}

	public void sendExceptionMessage(String exception, MessageExchange me) {
		// logger.info("Send the exception message exchange");
		MessageExchangeImpl exchange = (MessageExchangeImpl) me;
		try {
			Document doc = createExceptionDocument(exception);
			// not only create the output Normalized Message
			// but also set into the message exchange
			if (NMUtils.createOutNM(exchange, doc) == null) {
				logger.error("Can't create the output Normalized Message, so don't send the message");
				return;
			}
			unit.sendErrorResponse(exchange);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// then do nothing
		}
	}
	/**
	 * create the exception document according to the specified protocol
	 * 
	 * @return
	 */
	protected Document createExceptionDocument(String exception)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElementNS(EngineConstants.DEFAULT_NAMESPACE,
				"exception");
		root.setTextContent(exception);
		doc.appendChild(root);
		return doc;
	}

	public void init(AppEngineContext context) throws AppEngineException {
		this.context = context;
	}

	public BusinessUnit getBusinessUnit() {
		return this.unit;
	}
	/**
	 * load the invoker's properties by some way maybe from the properties file
	 * or just hard coding
	 * 
	 * but could change as demand
	 * 
	 */
	protected void loadProperties() {
		// can do nothing
	}
	/**
	 * change this method as the xml changes
	 * 
	 * @param node
	 */
	protected void loadProperties(Node node) {
		// retrieve receiver's properties from the document fragment
		if (node instanceof Element) {
			// logger.info("loading the properties of receiver!");
			Element recEle = (Element) node;

			if (recEle.getAttribute("service") == null) {
				logger.warn("The service attribute of receiver element is null!");
				return;
			}
			SERVICE = recEle.getAttribute("service");

			if (recEle.getAttribute("endpoint") == null) {
				logger.warn("The endpoint attribute of receiver element is null!");
				return;
			}

			ENDPOINT = recEle.getAttribute("endpoint");
			INTERFACE = recEle.getAttribute("interface");
			SU_NAME = recEle.getAttribute("serviceUnit");
			return;
		}
		logger.warn("The node is not a type of element when initiate the receiver's properties");
	}

	private void setProperties() {
		// logger.info("Set the properties of receiver!");
		// the appengine.xml file may miss the targetNamespace attribute
		// since it is optional
		String namespace;
		if (unit != null && unit.getTargetNamespace() != null) {
			namespace = unit.getTargetNamespace();
		} else {
			namespace = EngineConstants.DEFAULT_NAMESPACE;
		}
		this.setEndpoint(ENDPOINT);
		this.setInterfaceName(new QName(namespace, INTERFACE));
		this.setService(new QName(namespace, SERVICE));

		// this.setServiceUnit(serviceUnit);
		DefaultServiceUnit su = new DefaultServiceUnit();
		try {
			su.setName(SU_NAME);
			su.addEndpoint(this);
			su.setComponent(this.unit.getComponent());

		} catch (DeploymentException e) {
			logger.warn(
					"Can't set the target endpoint for the default service unit: "
							+ SU_NAME, e);
		}
		this.setServiceUnit(su);
	}

	public void configProperties() {
		loadProperties();
		setProperties();
	}

	public void configProperties(Node node) {
		loadProperties(node);
		setProperties();
	}
}
