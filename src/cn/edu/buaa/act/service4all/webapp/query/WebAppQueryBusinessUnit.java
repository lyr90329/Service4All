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

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.WebApplication;
import cn.edu.buaa.act.service4all.webapp.database.DBHandler;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;

public class WebAppQueryBusinessUnit extends BusinessUnit {

	@Override
	public void dispatch(ExchangeContext context) {
		String userName = (String) context.getData(Constants.USER_NAME);
		DBHandler handler = new DBHandler();
		List<WebApplication> apps = handler.queryAppByUser(userName);
		Document doc = buildResponse(apps);
		context.storeData("queryResponse", doc);
		try {
			this.receiver.sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveResponse(ExchangeContext arg0) {

	}

	private Document buildResponse(List<WebApplication> apps) {
		Document doc = null;
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
			Element root = doc.createElement("QueryResponse");
			for (WebApplication webApplication : apps) {
				Element app = doc.createElement("app");
				Element serviceID = doc.createElement(Constants.SERVICE_ID);
				serviceID.setTextContent(webApplication.getServiceID());
				app.appendChild(serviceID);
				Element serviceName = doc.createElement(Constants.SERVICE_NAME);
				serviceName.setTextContent(webApplication.getServiceName());
				app.appendChild(serviceName);
				Element url = doc.createElement(Constants.INVOKE_URL);
				url.setTextContent(webApplication.getUrl());
				app.appendChild(url);
				root.appendChild(app);
			}
			doc.appendChild(root);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
