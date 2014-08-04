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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSInvokerBusinessUnit extends BusinessUnit {

	public void dispatch(ExchangeContext arg0) {
		Invoker invoker = getInvoker("AvailableAppQueryInvoker");
		try {
			invoker.sendRequestExchange(arg0);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		boolean hasAvailableService = (Boolean) context
				.getData(WSInvokeConstants.HASAVAILBALESERVICE);
		if (hasAvailableService) {
			WSInvokerUtils.invokeWSByID(context);
			try {
				this.receiver.sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
		} else {
			String serviceID = (String) context
					.getData(WSInvokeConstants.SERVICEID);
			logger.error("There is no available service!");
			String infoString = "Waring!There is no available service for the ID:"
					+ serviceID;
			OMElement responseOmElement = OMAbstractFactory.getOMFactory()
					.createOMElement(WSInvokeConstants.RESPONSESOAP, null);
			responseOmElement.setText(infoString);
			
			context.storeData(WSInvokeConstants.RESPONSESOAP, responseOmElement);
			
			try {
				this.receiver.sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
		}

	}

	public Invoker getInvoker(String name) {
		if (getInvokers().get(name) == null) {
			logger.error("Missing the Invoker!");
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Missing the AvailableServerQueryInvoker!");
			ep.setSender(getReceiver().getEndpoint());
			this.handleInvocationException(ep);
		}
		return getInvokers().get(name);
	}
}
