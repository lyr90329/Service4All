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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import cn.edu.buaa.act.service4all.core.component.bri.AppEngineContext;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.common.DefaultComponent;
import org.apache.servicemix.jbi.framework.ComponentContextImpl;
import org.apache.servicemix.jbi.messaging.MessageExchangeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.AppEngineComponent;
import cn.edu.buaa.act.service4all.core.component.EngineConstants;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageFactory;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSessionFactory;
import cn.edu.buaa.act.service4all.core.component.messaging.util.NMUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.AppEngineException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

/**
 * The basic business unit for the component which contains the use case logic
 * 
 * The subclass should provide a constructor with no parameter
 * 
 * @author Huangyj
 * 
 */
public abstract class BusinessUnit {
	protected static final Log logger = LogFactory.getLog(BusinessUnit.class);
	public static final String ROOT_RESPONSE_DOC = "rootresponsedocument";
	protected AppEngineComponent parent;
	protected ComponentContext cmpCtx;
	protected DeliveryChannel channel;
	protected MessageFactory messageFactory;
	protected AppEngineContext context;
	protected Map<String, Invoker> invokers = new HashMap<String, Invoker>();
	protected Receiver receiver;
	protected List<MessageSession> sessions = new ArrayList<MessageSession>();
	protected String targetNamespace = EngineConstants.DEFAULT_NAMESPACE;

	/**
	 * this method should be invoked at once being instantiated
	 * 
	 * @param component
	 */
	public void setComponent(AppEngineComponent component) {
		this.parent = component;
		this.cmpCtx = parent.getComponentContext();
		// initiate the Message Factory
		if (cmpCtx instanceof ComponentContextImpl) {
			messageFactory = new MessageFactory((ComponentContextImpl) cmpCtx);
		} else {
			// logger.warn("Can't initiate the Message Factory!");
		}
		try {
			this.channel = this.cmpCtx.getDeliveryChannel();
		} catch (MessagingException e) {
			// logger.warn("Can't get the Delivery Channel", e);
		}
	}

	/**
	 * if need to invoke other units, send the invocation by this method
	 * 
	 * @param context
	 */
	public abstract void dispatch(ExchangeContext context);

	/**
	 * this method will be invoked when the invoker receive the response from
	 * other BusinessUnit
	 * 
	 * if this BU doesn't refer to other BU, this method can be ignored
	 * 
	 * @param context
	 */
	public abstract void onReceiveResponse(ExchangeContext context);

	public InOut createNewInOutExchange() throws MessagingException {
		return messageFactory.createInOutExchange();
	}

	/**
	 * when the receiver receives request, it creates an MessageSession to start
	 * a session
	 * 
	 * @param root
	 * @return
	 */
	public MessageSession startSession(MessageExchange root) {
		// logger.info("Start a session by the root MessageExchange: " +
		// root.getExchangeId());
		if (root.getStatus() == ExchangeStatus.ACTIVE) {
			MessageSession session = MessageSessionFactory
					.createMessageSession();
			session.setRootExchange(root);
			// add the session to the list
			sessions.add(session);
			return session;
		}
		if (root.getStatus() == ExchangeStatus.DONE) {
			// the session has ended
			closeSession(root);
		}
		return null;
	}

	/**
	 * get the session context
	 * 
	 * @return
	 */
	public ExchangeContext getContext(MessageExchange exchange) {
		for (MessageSession s : sessions) {
			if (s.isBelongs(exchange)) {
				return s.getContext();
			}
		}
		return null;
	}

	/**
	 * return the session which specified exchange belongs to
	 * 
	 * @param exchange
	 * @return
	 */
	public MessageSession getSession(MessageExchange exchange) {
		// logger.info("The target session: " + exchange.getExchangeId());
		for (MessageSession s : sessions) {
			if (s.isBelongs(exchange)) {
				// logger.info("The Unit's session: " + s.getSessionID());
				return s;
			}
		}
		return null;
	}

	public void sendResponse(MessageExchange exchange) {
		// whatever exception there is, do send the message
		doSend(exchange);
	}

	/**
	 * send a request MessageExchange for invocation
	 * 
	 * @param exchange
	 */
	public void sendRequest(MessageExchange exchange, ExchangeContext context) {
		// have to show some important information
		logger.info("Sending request message: "
				+ NMUtils.formatMessageExchange(exchange));
		MessageSession session = context.getSession();
		try {
			exchange.setStatus(ExchangeStatus.ACTIVE);
		} catch (MessagingException e) {
			logger.warn(
					"There is a exception when setting the MessageExchange's status",
					e);
		}
		doSend(exchange);
		// adding the exchange to the session
		String targetEndpoint = exchange.getEndpoint().getEndpointName();
		session.addRequestExchange(targetEndpoint, exchange);
	}
	/**
	 * sending the error message exchange
	 * 
	 * @param exchange
	 */
	public void sendErrorResponse(MessageExchange exchange) {
		doSend(exchange);
	}

	protected boolean closeSession(MessageExchange exchange) {
		// logger.info("Close the session which is specified by the
		// MessageExchange: " + exchange.getExchangeId());
		if (sessions == null || sessions.size() <= 0) {
			// logger.warn("There no session exist!");
			return false;
		}

		for (MessageSession s : sessions) {
			if (s.isBelongs(exchange)) {
				logger.info("Removing the session(" + s.getSessionID()
						+ ") from the list");
				sessions.remove(s);
				return true;
			}
		}
		return false;
	}

	protected void doSend(MessageExchange me) {
		if (this.channel == null) {
			// logger.error("The channel is null, so can't send the message");
			// do nothing currently
			return;
		}

		try {
			this.channel.send(me);
		} catch (MessagingException e) {
			logger.error("Can't send the message, so close the session", e);
			closeSession(me);
		}
	}
	/**
	 * handle the exception condition when invoke other BusinessUnits or
	 * services
	 * 
	 */
	public void handleInvocationException(
			MessageExchangeInvocationException exception) {
		logger.info("Handliing the invocation exception( "
				+ "\n\ttargetService: " + exception.getTargetService()
				+ "\n\tsender: " + exception.getSender() + ")");
		// retrieve exception message
		Document excpDoc = createInvocationExceptionDocument(exception);
		// retrieve the session
		MessageSession session = exception.getSession();
		if (session == null) {
			logger.error("Can't get the exchange session according to exchange");
			// do nothing currently
			return;
		}
		// set the response document into the exchange
		MessageExchange rootExchange = session.getRootExchange();

		try {
			NormalizedMessage outMessage = rootExchange.createMessage();
			outMessage.setContent(new DOMSource(excpDoc));

			// MessageExchangeImpl impl = (MessageExchangeImpl)rootExchange;
			// impl.setOutMessage(outMessage);
			((MessageExchangeImpl) rootExchange).setOutMessage(outMessage);
			// send the exchange
			this.sendResponse(rootExchange);
		} catch (MessagingException e) {
			logger.error("Can't create the Normalized Message!", e);
		}
	}
	/**
	 * create the document which contains the details for the invocation
	 * exception If there is some changes about communication, just change this
	 * method
	 * 
	 * @param exception
	 * @return
	 */
	protected Document createInvocationExceptionDocument(
			MessageExchangeInvocationException exception) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElementNS(
					EngineConstants.DEFAULT_NAMESPACE, "InvocationException");
			String message = exception.getMessage();
			root.setTextContent(message);
			doc.appendChild(root);
			return doc;
		} catch (ParserConfigurationException e) {
			logger.error("Can't create exception document", e);
			// then do nothings
		}
		return null;
	}

	public void init(AppEngineContext context) throws AppEngineException {
		this.parent = context.getComponent();
		this.cmpCtx = parent.getComponentContext();
		// initiate the Message Factory
		if (cmpCtx instanceof ComponentContextImpl) {
			messageFactory = new MessageFactory((ComponentContextImpl) cmpCtx);
		} else {
			logger.warn("Can't initiate the Message Factory!");
		}

		try {
			this.channel = this.cmpCtx.getDeliveryChannel();
		} catch (MessagingException e) {
			logger.warn("Can't get the Delivery Channel", e);
		}

		this.context = context;
		if (receiver == null || invokers == null) {
			logger.warn("The receiver or the invokers is null, so can't initialize the unit");
			throw new AppEngineException(
					"The receiver or the invokers is null, so can't initialize the unit");
		}
		// initialize the receiver
		// logger.info("Initialized the receiver and invokers!");
		receiver.init(context);
		Collection<Invoker> invs = invokers.values();
		for (Invoker i : invs) {
			i.init(context);
		}
	}

	public DefaultComponent getComponent() {
		return parent;
	}

	public Map<String, Invoker> getInvokers() {
		return invokers;
	}

	public void setInvokers(Map<String, Invoker> invokers) {
		this.invokers = invokers;
	}
	/**
	 * use the endpoint name firstly if it's null, then use the class name
	 * 
	 * @param invoker
	 */
	public void addInvoker(Invoker invoker) {
		logger.info("add an invoker into the business unit");
		Class<?> invClass = invoker.getClass();
		String name = invClass.getSimpleName();
		invokers.put(name, invoker);
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

}
