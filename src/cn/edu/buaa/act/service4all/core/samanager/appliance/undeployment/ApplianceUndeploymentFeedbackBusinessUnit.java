/*
*
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
*
*/
package cn.edu.buaa.act.service4all.core.samanager.appliance.undeployment;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;

public class ApplianceUndeploymentFeedbackBusinessUnit extends
		SAManagerBusinessUnit {

	@Override
	public void dispatch( ExchangeContext context ) {
		// String serviceID = (String)context.getData("serviceID");
		// String serviceName = (String)context.getData("serviceName");
		// Map<String, String> deployedmaps = (Map<String,
		// String>)context.getData("undeployResults");
		//
		// logger.info("Generate the undeployment feed back event");
		// AppDeploymentEvent event = new AppDeploymentEvent();
		// //event.setDeployResults(deployedmaps);
		// //event.setEventType(AppDeploymentEvent.APP_WS_DEPLOYMENT_FEEDBACK);
		// event.setDeployedServiceId(serviceID);
		// event.setDeployedServiceName(serviceName);
		//
		// List<AppListener> listeners = this.getAppListeners();
		// for(AppListener l : listeners){
		// try {
		// //l.undeployAppFeedback(event);
		// } catch (AppException e) {
		// logger.error("Failed to update the deployment result: " +
		// e.getMessage(), e);
		// try {
		// this.getReceiver().sendResponseMessage(context);
		// } catch (MessageExchangeInvocationException e1) {
		// this.handleInvocationException(e1);
		// }
		// }
		// }
		// try {
		// this.getReceiver().sendResponseMessage(context);
		// } catch (MessageExchangeInvocationException e) {
		// this.handleInvocationException(e);
		// }
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
