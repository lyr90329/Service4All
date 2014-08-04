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
package cn.edu.buaa.act.service4all.core.component.messaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jbi.messaging.MessageExchange;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSessionImpl;

/**
 * The implementation which use the map to manage exchanges
 * 
 * @author huangyj
 * 
 */
public class MessageSessionImpl implements MessageSession {
	/**
	 * It is the root message exchange's id
	 */
	protected String sessionID = null;
	protected List<String> ids = new ArrayList<String>();
	/**
	 * the message exchange which triggers the request handling
	 */
	protected MessageExchange root;
	/**
	 * the invocation message for other business unit which can be accessed by
	 * the target endpoint's name
	 */
	protected Map<String, MessageExchange> requests = new HashMap<String, MessageExchange>();
	private Date start;
	private ExchangeContext context;

	public MessageSessionImpl() {
		// set the start date time when this session is created
		this.start = new Date();
		// create the exchange session
		context = new ExchangeContext();
		context.setSession(this);
	}

	public void addRequestExchange(String targetEndpoint,
			MessageExchange exchange) {
		// if the target endpoint's value already exists, then we ignore it
		if (requests.containsKey(targetEndpoint)) {
			// logger.info("The target endpoint(" + targetEndpoint + ")'s
			// MessageExchange already exists");
			return;
		}

		if (exchange != null) {
			requests.put(targetEndpoint, exchange);
			// logger.info("For the root(" + root.getExchangeId() + ") and add a
			// new request: " + exchange.getExchangeId());
			ids.add(exchange.getExchangeId());
		}
		// if the exchange is null, ignore this case
	}

	public MessageExchange getRequestExchange(String targetEndpoint) {
		if (requests.containsKey(targetEndpoint)) {
			return requests.get(targetEndpoint);
		}
		return null;
	}

	public MessageExchange getRootExchange() {
		return root;
	}

	public Date getStartDate() {
		return start;
	}

	public void setRootExchange(MessageExchange root) {
		if (root == null) {
			// logger.warn("The root MessageExchange which want to be set is
			// null");
			return;
		}
		this.root = root;
		this.sessionID = root.getExchangeId();
		ids.add(root.getExchangeId());
	}

	public boolean isBelongs(MessageExchange exchange) {
		if (exchange == null) {
			// logger.warn("The vertifying exchange is null");
			return false;
		}
		String id = exchange.getExchangeId();
		// logger.info("The target Exchange Id: " + id);
		for (String i : ids) {
			// logger.info("The sub exchange id: " + i);
			if (i.equals(id)) {
				return true;
			}
		}
		return false;
	}

	public ExchangeContext getContext() {
		return context;
	}

	public String getSessionID() {
		return sessionID;
	}
}
