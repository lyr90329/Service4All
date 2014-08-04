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
package cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils.Constants;
import cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSNameQuerybyIDBusinessUnit extends BusinessUnit {
	private final Log logger = LogFactory.getLog(WSNameQuerybyIDBusinessUnit.class);

	public void dispatch(ExchangeContext context) {
		if (getInvokers().get("WSNameQuerybyIDQueryInvoker") == null) {
			logger.error("Missing the WSNameQuerybyIDQueryInvoker!");
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Missing the WSNameQuerybyIDQueryInvoker!");
			ep.setSender(getReceiver().getEndpoint());
			this.handleInvocationException(ep);
		}
		Invoker invoker = getInvokers().get("WSNameQuerybyIDQueryInvoker");

		try {

			invoker.sendRequestExchange(context);

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}

	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {

		Document respFromBus = (Document) context
				.getData(Constants.RESPONSEFROMBUS);

		context.storeData(Constants.RESPONSEDOC,
				DocsBuilder.buildResponseDoc(respFromBus));

		try {
			getReceiver().sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {

			e.printStackTrace();
		}

	}

}
