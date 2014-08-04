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
package cn.edu.buaa.act.service4all.core.samanager.app.undeployment;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppUndeploymentEvent;

public class AppUndeploymentFeedbackBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( AppUndeploymentFeedbackBusinessUnit.class );

	protected final static String SERVICE_ID = "serviceID";
	protected final static String TYPE = "queryType";
	protected final static String SERVICE_NAME = "serviceName";
	protected final static String UNDEPLOY_RESULTS = "undeployResults";
	protected final static String IS_SUCCE = "isSuccessful";


	// protected final static String DEPLOYED_REPS = "deployedRepetitions";
	// protected final static String SERVICE_NAME = "serviceName";

	@Override
	public void dispatch( ExchangeContext context ) {

		AppUndeploymentEvent event = createAppUndeploymentEvent( context );

		List<AppListener> listeners = this.getAppListeners();
		for (AppListener l : listeners) {
			try {
				l.undeployAppFeedback( event );
			} catch (AppException e) {
				logger.error(
						"Failed to update the deployment result: "
								+ e.getMessage(), e );
				try {
					this.getReceiver().sendResponseMessage( context );
				} catch (MessageExchangeInvocationException e1) {
					this.handleInvocationException( e1 );
				}
			}
		}

		// send the message
		sendResponse( event, context );
	}


	protected void sendResponse( AppUndeploymentEvent event,
			ExchangeContext context ) {

		// String serviceName = (String)event.getTargetServiceName();
		// List<AppRepetition> reps =
		// (List<AppRepetition>)event.getDeployedRepetitions();
		//
		// context.storeData(SERVICE_NAME, serviceName);
		// context.storeData(DEPLOYED_REPS, reps);

		doSend( context );
	}


	protected void doSend( ExchangeContext context ) {
		try {
			getReceiver().sendResponseMessage( context );
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	protected AppUndeploymentEvent createAppUndeploymentEvent(
			ExchangeContext context ) {
		AppUndeploymentEvent event = new AppUndeploymentEvent();

		// context.storeData(AppUndeploymentFeedbackBusinessUnit.SERVICE_ID,
		// serviceID);
		// context.storeData(AppUndeploymentFeedbackBusinessUnit.SERVICE_NAME,
		// name);
		// context.storeData(AppUndeploymentFeedbackBusinessUnit.UNDEPLOY_RESULTS,
		// undeployedmaps);
		// context.storeData(AppUndeploymentFeedbackBusinessUnit.TYPE,
		// deployType);
		// context.storeData(AppUndeploymentFeedbackBusinessUnit.IS_SUCCE,
		// String.valueOf(isSuccessful));

		String serviceId = (String) context.getData( SERVICE_ID );
		String serviceName = (String) context.getData( SERVICE_NAME );
		boolean isSuccessful = Boolean.valueOf( (String) context
				.getData( IS_SUCCE ) );
		@SuppressWarnings("unchecked")
		List<String> undeployResults = (List<String>) context
				.getData( UNDEPLOY_RESULTS );

		event.setSuccessful( isSuccessful );
		event.setTargetServiceID( serviceId );
		event.setUndeployedResults( undeployResults );
		event.setTargetServiceName( serviceName );

		return event;
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
