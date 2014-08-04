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

import javax.jbi.management.DeploymentException;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.JbiConstants;
import org.apache.servicemix.common.DefaultServiceUnit;
import org.apache.servicemix.common.EndpointSupport;
import org.apache.servicemix.common.endpoints.ConsumerEndpoint;
import org.apache.servicemix.jbi.messaging.MessageExchangeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import cn.edu.buaa.act.service4all.core.component.EngineConstants;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import cn.edu.buaa.act.service4all.core.component.messaging.util.NMUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

/**
 * define the target endpoint and its properties it should provide a constructor
 * with a BusinessUnit argument
 * 
 * 
 * 
 */
public abstract class Invoker extends ConsumerEndpoint {
	protected final Log logger = LogFactory.getLog(Invoker.class);
	protected BusinessUnit unit;
	protected AppEngineContext context;
	protected String TARGET_ENDPOINT;
	protected String TARGET_INTERFACE;
	protected String TARGET_SERVICE;
	protected String TARGET_OPERATION;
	protected String TARGET_URI;
	protected String ENDPOINT;
	protected String SERVICE;
	protected String INTERFACE;
	protected String OPERATION;
	protected String SU_NAME;
	/**
	 * receive the response message for the previous invocation
	 * 
	 */
	@Override
	public void process(MessageExchange exchange) {
		// logger.info("Receiving response MessageExchange: " +
		// NMUtils.formatMessageExchange(exchange));
		MessageExchangeImpl impl = (MessageExchangeImpl) exchange;
		// retrieve the response document from message exchange
		NormalizedMessage outNM = impl.getOutMessage();
		Document responseDoc = NMUtils.retrieveContent(outNM);
		MessageSession session = unit.getSession(exchange);
		// if there is some exception or error
		// unit has to send exception message to client
		if (responseDoc == null) {
			logger.warn("The response document content is null!");
			// tell business to send exception message to report
			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					"The response document content is null!",
					session.getContext());
			excep.setSender((String) exchange
					.getProperty(JbiConstants.SENDER_ENDPOINT));
			excep.setTargetService(exchange.getService());
			unit.handleInvocationException(excep);
			return;
		}

		if (session == null) {
			// ignore this request
			logger.error("The response message exchange doesn't belong to a session! The exchange : "
					+ NMUtils.formatMessageExchange(exchange));
			return;
		}
		// handle the response document
		ExchangeContext context = session.getContext();

		try {
			//
			handleResponse(responseDoc, context);
		} catch (MessageExchangeInvocationException e) {
			logger.warn(e.getMessage());
			unit.handleInvocationException(e);
		}

	}

	/**
	 * create the request document
	 * 
	 * @param context
	 */
	public abstract Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException;

	/**
	 * according the document created and send it to the target business unit
	 * 
	 * @param requestDoc
	 */
	public void sendRequestExchange(ExchangeContext exchangeContext)
			throws MessageExchangeInvocationException {
		// logger.info("Create the request exchange and send it to the
		// OneBusinessUnit");
		Document reqDoc = createRequestDocument(exchangeContext);
		// if the request document is null
		if (reqDoc == null) {
			// logger.("");
			logger.warn("The request document is null, so can't send the request exchange");
			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					"The request document is null, so can't send the request exchange");
			excep.setTargetService(new QName(this.TARGET_URI,
					this.TARGET_SERVICE));
			excep.setContext(exchangeContext);
			excep.setSender(this.ENDPOINT);
			throw excep;
		}

		try {
			// create an exchange
			MessageExchange exchange = createInvocationExchange();
			// write the request content to the message exchange
			writeRequest(reqDoc, exchange);
			// send the message exchange
			unit.sendRequest(exchange, exchangeContext);
		} catch (MessagingException e) {
			logger.error("Can't start the invocation as there is an error: "
					+ e.getMessage());
			// Throw an invocation exception and the business send back an error
			// report to client
			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					e.getMessage());
			excep.setTargetService(new QName(this.TARGET_URI,
					this.TARGET_SERVICE));
			excep.setContext(exchangeContext);
			excep.setSender(this.ENDPOINT);
			throw excep;
		}

	}

	public void sendRequestExchange(Document reqDoc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// logger.info("Create the request exchange and send it to the target
		// endpoint");
		// if the request document is null
		if (reqDoc == null) {
			// logger.("");
			logger.warn("The request document is null, so can't send the request exchange");
			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					"The request document is null, so can't send the request exchange");
			excep.setTargetService(new QName(this.TARGET_URI,
					this.TARGET_SERVICE));
			excep.setContext(context);
			excep.setSender(this.ENDPOINT);
			throw excep;
		}

		try {
			// create an exchange
			MessageExchange exchange = createInvocationExchange();
			// write the request content to the message exchange
			writeRequest(reqDoc, exchange);
			// send the message exchange
			unit.sendRequest(exchange, context);
		} catch (MessagingException e) {
			logger.error("Can't start the invocation as there is an error: "
					+ e.getMessage());
			// Throw an invocation exception and the business send back an error
			// report to client
			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					e.getMessage());
			excep.setTargetService(new QName(this.TARGET_URI,
					this.TARGET_SERVICE));
			excep.setContext(context);
			excep.setSender(this.ENDPOINT);
			throw excep;
		}
	}

	protected void writeRequest(Document doc, MessageExchange exchange)
			throws MessagingException {
		// logger.info("Write the request document into message exchange: " +
		// NMUtils.formatMessageExchange(exchange));
		MessageExchangeImpl impl = (MessageExchangeImpl) exchange;
		NormalizedMessage nm = impl.createMessage();
		DOMSource source = new DOMSource(doc);
		nm.setContent(source);
		impl.setInMessage(nm);
	}
	/**
	 * create a new Message Exchange and set the properties such as the target
	 * endpoint
	 * 
	 * @return
	 * @throws MessagingException
	 */
	private MessageExchange createInvocationExchange()
			throws MessagingException {
		// change these properties to the GreetEndpoint
		// String namespace = "http://sdp.act.buaa.edu.cn/appengine/sample";
		InOut exchange = unit.createNewInOutExchange();
		QName serviceName = new QName(TARGET_URI, TARGET_SERVICE);
		QName opName = new QName(TARGET_URI, TARGET_OPERATION);
		QName interName = new QName(TARGET_URI, TARGET_INTERFACE);
		exchange.setService(serviceName);
		exchange.setInterfaceName(interName);
		exchange.setOperation(opName);
		String key = EndpointSupport.getKey(this);
		// log.info("Get the endpoint's key: " + key);

		if (key != null) {
			logger.info("Set the endpoint's key to the message exchange: "
					+ key);
			exchange.setProperty(JbiConstants.SENDER_ENDPOINT, key);
		}
		return exchange;
	}

	/**
	 * this method contains the main handling procedure for response message
	 * 
	 * if the other BU responses a exception message, then this method should
	 * recognize it and throw out a MessageExchangeInvocationException;
	 * 
	 * @param response
	 * @param context
	 */
	public abstract void handleResponse(Document response,
			ExchangeContext context) throws MessageExchangeInvocationException;

	public void setBuisnessUnit(BusinessUnit unit) {
		this.unit = unit;
	}

	/**
	 * setting the properties when initializing the invoker
	 * 
	 */
	public void init(AppEngineContext context) throws AppEngineException {
		// logger.info("Initiate the " + this.getClass() + " Invoker !");
		this.context = context;
	}

	/**
	 * load the invoker's properties by some way maybe from the properties file
	 * or just hard coding
	 * 
	 * but could change as demand
	 * 
	 */
	public void loadProperties(Node node) {
		if (node instanceof Element) {
			// logger.info("loading the properties of invoker!");
			Element invEle = (Element) node;
			if (invEle.getAttribute("service") == null) {
				logger.warn("The service attribute of invoker element is null!");
				return;
			}
			SERVICE = invEle.getAttribute("service");
			if (invEle.getAttribute("endpoint") == null) {
				logger.warn("The endpoint attribute of invoker element is null!");
				return;
			}
			ENDPOINT = invEle.getAttribute("endpoint");
			OPERATION = invEle.getAttribute("operation");
			INTERFACE = invEle.getAttribute("interface");
			SU_NAME = invEle.getAttribute("serviceUnit");
			if (invEle.getElementsByTagName("target") != null
					&& invEle.getElementsByTagName("target").getLength() >= 1) {
				Element targetEle = (Element) invEle.getElementsByTagName(
						"target").item(0);
				if (targetEle.getAttribute("service") == null) {
					logger.warn("The service attribute of invoker element is null!");
					return;
				}
				TARGET_SERVICE = targetEle.getAttribute("service");
				if (targetEle.getAttribute("endpoint") == null) {
					logger.warn("The endpoint attribute of invoker element is null!");
					return;
				}
				TARGET_ENDPOINT = targetEle.getAttribute("endpoint");
				TARGET_OPERATION = targetEle.getAttribute("operation");
				TARGET_INTERFACE = targetEle.getAttribute("interface");
				TARGET_URI = targetEle.getAttribute("uri");
			}
			return;
		}
		logger.warn("The node is not a type of element when initiate the invoker's properties");
	}

	// public abstract void loadProperties(DocumentFragment fragment);
	public void configProperties() {
		loadProperties();
		setProperties();
	}

	public void configProperties(Node node) {
		loadProperties(node);
		setProperties();
	}

	public void loadProperties() {
		// can do nothing
	}
	/**
	 * loading the properties from the xml document
	 * 
	 * @param node
	 */
	private void setProperties() {
		// logger.info("Set the properties of invoker!");
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
		// set the target endpoint properties
		this.setTargetEndpoint(TARGET_ENDPOINT);
		String targetUri;
		if (TARGET_URI == null) {
			targetUri = EngineConstants.DEFAULT_NAMESPACE;
		} else {
			targetUri = TARGET_URI;
		}
		this.setTargetUri(targetUri);
		this.setTargetInterface(new QName(targetUri, TARGET_INTERFACE));
		this.setTargetOperation(new QName(targetUri, TARGET_OPERATION));
		this.setTargetService(new QName(targetUri, TARGET_SERVICE));

	}

	public BusinessUnit getBusinessUnit() {
		return this.unit;
	}
}
