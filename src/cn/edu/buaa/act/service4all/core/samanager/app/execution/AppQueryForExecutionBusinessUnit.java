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
package cn.edu.buaa.act.service4all.core.samanager.app.execution;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppInvocationEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;

public class AppQueryForExecutionBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( AppQueryForExecutionBusinessUnit.class );

	public static final String SERVICE_ID = "serviceID";
	public static final String SERVICE_LIST = "services";

	public static final String QUERY_TYPE = "type";


	@Override
	public void dispatch( ExchangeContext context ) {
		logger.info( "AppManager( "
				+ this.getAppManager().getClass().getSimpleName()
				+ " ) and applianceManager("
				+ this.getApplianceManager().getClass().getSimpleName()
				+ " ) for this " + this.getClass().getSimpleName()
				+ "BusinessUnit!" );
		AppInvocationEvent event = createAppInvocationEvent( context );

		List<AppListener> listeners = this.getAppListeners();
		for (AppListener l : listeners) {
			try {

				logger.info( "The Listener: " + l.getClass().getSimpleName() );
				l.queryAppRepetitionForExecution( event );
			} catch (AppException e) {
				logger.error(
						"There happens some exception when query available applications",
						e );
			}
		}

		// parse the event and send the response message
		sendResponse( event, context );

	}


	protected AppInvocationEvent createAppInvocationEvent(
			ExchangeContext context ) {
		String serviceID = (String) context.getData( SERVICE_ID );

		AppInvocationEvent event = new AppInvocationEvent();
		event.setTargetServiceID( serviceID );

		return event;
	}


	protected void sendResponse( AppInvocationEvent event,
			ExchangeContext context ) {
		parseAppInvocationEvent( event, context );
		doSend( context );
	}


	protected void parseAppInvocationEvent( AppInvocationEvent event,
			ExchangeContext context ) {
		List<AppReplica> reps = event.getRepetitions();
//		String serviceID = event.getTargetServiceID();

		context.storeData( SERVICE_LIST, reps );

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
