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

import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils.Constants;
import cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSNameQuerybyIDQueryInvoker extends Invoker {

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {

		String id = (String) context.getData(Constants.SERVICEID);
		return DocsBuilder.buildQueryRequestDoc(id);
	}

	@Override
	public void handleResponse(Document response, ExchangeContext context)
			throws MessageExchangeInvocationException {
		context.storeData(Constants.RESPONSEFROMBUS, response);
		unit.onReceiveResponse(context);
	}

}
