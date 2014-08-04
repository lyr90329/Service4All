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
package cn.edu.buaa.act.service4all.core.samanager.app.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;

public class AppDeploymentFeedbackBusinessUnit extends SAManagerBusinessUnit {

	protected Log logger = LogFactory
			.getLog( AppDeploymentFeedbackBusinessUnit.class );

	protected final static String SERVICE_ID = "serviceId";
	protected final static String SERVICE_NAME = "serviceName";
	protected final static String DEPLOY_RESULTS = "deployResults";
	protected final static String IS_SUCCESSFUL = "isSuccessful";
	protected final static String DEPLOY_TYPE = "deployType";

	protected Map<String, AppDeploymentEvent> events = new HashMap<String, AppDeploymentEvent>();


	@Override
	public void dispatch( ExchangeContext context ) {

		AppDeploymentEvent event;
		try {

			event = createAppDeploymentEvent( context );

			List<AppListener> listeners = this.getAppListeners();
			for (AppListener l : listeners) {
				try {
					l.deployAppFeedback( event );
				} catch (AppException e) {

					logger.error( "Failed to update the deployment result: "
							+ e.getMessage(), e );
					try {
						this.getReceiver().sendResponseMessage( context );
					} catch (MessageExchangeInvocationException e1) {
						this.handleInvocationException( e1 );
					}
				}
			}

			// send the message by parse the event
			sendResponse( event, context );

		} catch (AppException e) {
			// handling the exception
			this.handleInvocationException( new MessageExchangeInvocationException(
					e.getMessage() ) );
		}

	}


	protected AppDeploymentEvent createAppDeploymentEvent(
			ExchangeContext context ) throws AppException {

		String serviceID = (String) context.getData( SERVICE_ID );
		String serviceName = (String) context.getData( SERVICE_NAME );
		boolean isSuccessful = Boolean.valueOf( (String) context
				.getData( IS_SUCCESSFUL ) );
		// applianceId->InvokeUrl
		@SuppressWarnings("unchecked")
		Map<String, String> deployedmaps = (Map<String, String>) context
				.getData( "deployResults" );

		// if deploy fail, inform the manager

		logger.info( "Generate the deployment feed back event" );
		AppDeploymentEvent event = new AppDeploymentEvent();
		event.setDeployedServiceId( serviceID );
		event.setDeployedServiceName( serviceName );

		if (!isSuccessful) {
			event.setQueryResults( null );
		} else {
			Iterator<String> ids = deployedmaps.keySet().iterator();
			List<AppReplica> reps = new ArrayList<AppReplica>();
			while (ids.hasNext()) {
				String id = ids.next();
				String invokeUrl = deployedmaps.get( id );

				AppReplica repetition = new AppReplica();
				repetition.setAppName( serviceName );
				repetition.setContainerId( id );
				repetition.setInvocationUrl( invokeUrl );

				reps.add( repetition );
			}
			event.setDeployResults( reps );
		}

		return event;
	}


	/**
	 * 
	 * @param event
	 * @param context
	 */
	protected void deployAppliances( AppDeploymentEvent event,
			ExchangeContext context ) throws MessageExchangeInvocationException {

		Invoker invoker = this.getInvokers().get(
				"ServerQueryForDeploymentInvoker" );
		if (invoker == null) {
			logger.warn( "Can't invoke the ApplianceDeployment BusinessUnit because the  ServerQueryForDeploymentInvoker is null" );
			throw new MessageExchangeInvocationException(
					"Can't invoke the ApplianceDeployment BusinessUnit because the  ServerQueryForDeploymentInvoker is null" );
		}
		invoker.sendRequestExchange( context );

		// add the event into the queue
		events.put( event.getDeployedServiceId(), event );

	}


	protected void sendResponse( AppDeploymentEvent event,
			ExchangeContext context ) {
		doSend( context );
	}


	protected void doSend( ExchangeContext context ) {
		try {
			this.getReceiver().sendResponseMessage( context );
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	@Override
	public void onReceiveResponse( ExchangeContext arg0 ) {

	}

}
