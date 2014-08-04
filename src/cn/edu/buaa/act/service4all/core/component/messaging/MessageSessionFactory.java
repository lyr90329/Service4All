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

import cn.edu.buaa.act.service4all.core.component.messaging.MessageSession;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSessionFactory;
import cn.edu.buaa.act.service4all.core.component.messaging.MessageSessionImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageSessionFactory {
	private final static Log logger = LogFactory
			.getLog(MessageSessionFactory.class);
	public final static String DEFAULT_IMPL = "map";
	public final static String MAP_IMPL = "map";

	/**
	 * create the default implementation of messagesession
	 * 
	 * @return
	 */
	public static MessageSession createMessageSession() {
		// logger.info("Create a default MessageSession implementation");
		return new MessageSessionImpl();
	}

	/**
	 * UNUSED
	 * 
	 * @param version
	 * @return
	 */
	public static MessageSession createMessageSession(String version) {

		if (version == null) {
			logger.warn("The implementation version's string value is null");
			return null;
		}
		if (version.equals(MAP_IMPL)) {
			// logger.info("Create a map MessageSession implementation");
			return new MessageSessionImpl();
		}
		if (version.equals(DEFAULT_IMPL)) {
			// logger.info("Create a default MessageSession implementation");
			return new MessageSessionImpl();
		}
		logger.info("There is no matched implementation of MessageSession, so create the default version");
		return new MessageSessionImpl();
	}
}
