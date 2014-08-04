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
package cn.edu.buaa.act.service4all.webapp.undeployment;

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.WebApplication;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;

public class WebAppUndeployFeedbackInvoker extends Invoker {

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		@SuppressWarnings("unchecked")
		List<WebApplication> apps = (List<WebApplication>) context
				.getData(Constants.UNDEPLOY_APP_LIST);
		String isSucc = (String) context.getData(Constants.UNDEPLOY_RESULT);
		Document doc;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc
					.createElement(Constants.UNDEPLOY_FEEDBACK_REQUEST);
			root.setAttribute(Constants.TYPE, Constants.WEB_APP);
			Element serviceID = doc.createElement(Constants.SERVICE_ID);
			serviceID.setTextContent((String) context
					.getData(Constants.SERVICE_ID));
			root.appendChild(serviceID);
			Element serviceName = doc.createElement(Constants.SERVICE_NAME);
			serviceName.setTextContent((String) context
					.getData(Constants.SERVICE_NAME));
			root.appendChild(serviceName);
			Element isSuccessful = doc.createElement(Constants.IS_SUCC);
			isSuccessful.setTextContent(isSucc);
			root.appendChild(isSuccessful);
			for (WebApplication app : apps) {
				Element containerList = doc.createElement("container");
				containerList.setAttribute("id", app.getApplianceID());
				containerList.setAttribute("isSuccessful",
						String.valueOf(app.isUndeployed()));
				root.appendChild(containerList);
			}
			doc.appendChild(root);
			return doc;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		context.storeData(Constants.UNDEPLOY_RESPONSE,
				DocBuilder.buildUndeployResponse(context));
		this.unit.getReceiver().sendResponseMessage(context);
	}
}
