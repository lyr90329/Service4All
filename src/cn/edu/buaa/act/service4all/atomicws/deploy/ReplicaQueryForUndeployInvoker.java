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
package cn.edu.buaa.act.service4all.atomicws.deploy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ReplicaQueryForUndeployInvoker extends Invoker {

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {

		String serviceID = (String) context.getData(DeployConstants.SERVICEID);
		return DocsBuilder.buildUndeployUrlsQueryDoc(serviceID);
	}

	@Override
	public void handleResponse(Document response, ExchangeContext context)
			throws MessageExchangeInvocationException {
		String cnt = XMLUtils.retrieveDocumentAsString(response);
		logger.info("Replica info returned from the bus \n" + cnt);

		Element root = response.getDocumentElement();

		if (root.getElementsByTagName("exception").item(0) != null) {
			context.storeData(DeployConstants.HASAVILABLEREPLICAS, false);
		} else {
			context.storeData(DeployConstants.HASAVILABLEREPLICAS, true);
			try {
				String serviceName = ((Element) root.getElementsByTagName(
						"serviceName").item(0)).getTextContent();
				context.storeData(DeployConstants.SERVICENAME, serviceName);

				Element urls = (Element) root.getElementsByTagName(
						"containerList").item(0);
				context.storeData("urls", urls);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		unit.onReceiveResponse(context);
	}

}
