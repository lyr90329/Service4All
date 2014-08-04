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
package cn.edu.buaa.act.service4all.core.component.transaction.exception;

import javax.xml.namespace.QName;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;

/**
 * The exception would be thrown out when there are some invocation errors for
 * other services
 * 
 * 
 * 
 */
public class MessageExchangeInvocationException extends Exception {

	private static final long serialVersionUID = 1L;
	// private ExchangeContext context;
	private MessageSession session;
	private QName targetService;
	private String sender;

	public MessageExchangeInvocationException(String message) {
		super(message);
	}

	public MessageExchangeInvocationException(String message,
			ExchangeContext context) {
		super(message);
		// this.context = context;
		this.session = context.getSession();
	}

	public void setContext(ExchangeContext context) {
		// this.context = context;
		this.session = context.getSession();
	}

	public MessageSession getSession() {
		return session;
	}

	public void setTargetService(QName targetService) {
		this.targetService = targetService;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public QName getTargetService() {
		return targetService;
	}
}
