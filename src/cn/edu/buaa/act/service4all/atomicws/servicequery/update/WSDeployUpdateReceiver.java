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
package cn.edu.buaa.act.service4all.atomicws.servicequery.update;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.UpdateDocument;
import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.WSQueryUpdateConstants;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSDeployUpdateReceiver extends Receiver {
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		return (Document) context.getData(WSQueryUpdateConstants.RESPONSE);
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {

		logger.info("Update msg is received ");

		Element update = doc.getDocumentElement();
		String type = update.getNodeName();
		Element service = ((Element) update.getElementsByTagName("service")
				.item(0));
		UpdateDocument.getInstance().addService(type, service);
		context.storeData(WSQueryUpdateConstants.RESPONSE,
				DocsBuilder.buildUpdateResDoc());
		this.sendResponseMessage(context);
	}
}
