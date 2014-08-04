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

import java.util.HashMap;
import java.util.Map;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * store the session context information
 * 
 * 
 * 
 */
public class ExchangeContext {
	private final Log logger = LogFactory.getLog(ExchangeContext.class);
	private MessageSession session;
	private Map<String, Object> data;

	public ExchangeContext() {
		data = new HashMap<String, Object>();
	}

	public Object getData(String name) {
		if (data.containsKey(name)) {
			return data.get(name);
		}
		return null;
	}

	public void storeData(String name, Object d) {
		if (name == null || d == null) {
			logger.warn("The data's name or value is null, so ignore it");
			return;
		}
		data.put(name, d);
	}

	public void setSession(MessageSession session) {
		this.session = session;
	}

	public MessageSession getSession() {
		return session;
	}
}
