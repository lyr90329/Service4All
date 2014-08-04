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
package cn.edu.buaa.act.service4all.webapp.query;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;

public class WebAppQueryReceiver extends Receiver {
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		Document doc = (Document) context.getData("queryResponse");
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		Element root = doc.getDocumentElement();
		String userName = root.getElementsByTagName(Constants.USER_NAME)
				.item(0).getTextContent();
		context.storeData(Constants.USER_NAME, userName);
		this.unit.dispatch(context);
	}
}
