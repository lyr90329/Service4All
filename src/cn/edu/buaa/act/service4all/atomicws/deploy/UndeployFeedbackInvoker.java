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

import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class UndeployFeedbackInvoker extends Invoker {

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		return DocsBuilder.buildUndeployFb2BusDoc(context);
	}

	@Override
	public void handleResponse(Document arg0, ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("The feedback in received by SAMangager");
		if (((String) context.getData(DeployConstants.TYPE))
				.equals(DeployConstants.UNDEPLOY)) {
			context.storeData(DeployConstants.RESPONSE,
					DocsBuilder.buildUndeployResponseDoc(context));
		} else {
			context.storeData(DeployConstants.RESPONSE,
					DocsBuilder.buildReplicaReleaseResponseDoc(context));
		}

		try {
			this.unit.getReceiver().sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}

	}
}
