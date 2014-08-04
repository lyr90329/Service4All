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
package cn.edu.buaa.act.service4all.webapp.deployment;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;
import cn.edu.buaa.act.service4all.webapp.utility.XMLUtil;

public class WebAppServerQueryInvoker extends Invoker {

	private static Log logger = LogFactory
			.getLog(WebAppServerQueryInvoker.class);

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("send query message to SA.");
		String serviceName = (String) context.getData(Constants.SERVICE_NAME);
		int deployNum = Integer.parseInt((String) context
				.getData(Constants.DEPLOY_NUM));
		Document doc = DocBuilder.buildRequestDoc(serviceName, deployNum);
		return doc;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("Receive available container response from SA.\n Response document is:\n"
				+ XMLUtil.docToString(doc));
		parseResponse(doc, context);
	}

	private void parseResponse(Document doc, ExchangeContext context) {
		Element root = doc.getDocumentElement();
		String serviceID = ((Element) root.getElementsByTagName(
				Constants.SERVICE_ID).item(0)).getTextContent();
		context.storeData(Constants.SERVICE_ID, serviceID);
		Element urls = ((Element) root.getElementsByTagName(
				Constants.CONTAINER_LIST).item(0));
		context.storeData(Constants.CONTAINER_LIST, urls);
		OMElement request = (OMElement) context
				.getData(Constants.DEPLOY_WAR_REQUEST);
		if (request == null) {
			logger.info("*********The request for container is null!");
		}
		unit.onReceiveResponse(context);
	}
}
