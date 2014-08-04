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

import java.util.Date;
import javax.jbi.messaging.MessageExchange;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;

/**
 * this is the message session for a request handling It provides these
 * operations which manage exchanges for business unit
 * 
 * @author Huangyj
 * 
 */
public interface MessageSession {

	public void setRootExchange(MessageExchange root);

	public void addRequestExchange(String targetEndpoint,
			MessageExchange exchange);

	public MessageExchange getRootExchange();

	public MessageExchange getRequestExchange(String targetEndpoint);

	/**
	 * return the date time for the session being created
	 * 
	 * @return
	 */
	public Date getStartDate();

	public boolean isBelongs(MessageExchange exchange);

	/**
	 * in order to persist the status among a exchange session, store some
	 * status data in the context
	 * 
	 * @param context
	 */
	public ExchangeContext getContext();

	public String getSessionID();
}
