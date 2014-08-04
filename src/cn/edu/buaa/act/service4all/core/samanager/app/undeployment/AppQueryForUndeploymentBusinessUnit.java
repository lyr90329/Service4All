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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppUndeploymentEvent;

public class AppQueryForUndeploymentBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( AppQueryForUndeploymentBusinessUnit.class );

	protected final static String SERVICE_ID = "serviceID";
	protected final static String QUERY_TYPE = "queryType";

	protected final static String DEPLOYED_REPS = "deployedRepetitions";
	protected final static String SERVICE_NAME = "serviceName";


	@Override
	public void dispatch( ExchangeContext context ) {

		AppUndeploymentEvent event = createAppUndeploymentEvent( context );

		List<AppListener> listeners = this.getAppListeners();
		for (AppListener l : listeners) {
			try {
				l.queryAppRepetitionForUndeployment( event );

			} catch (AppException e) {
				logger.warn( e.getMessage() );
				Document excepResponse = createExceptionMessage( "There is some exception when handling the event : "
						+ e.getMessage() );
				getReceiver().sendResponseMessage( excepResponse, context );
				return;
			}
		}

		sendResponse( event, context );

	}


	protected AppUndeploymentEvent createAppUndeploymentEvent(
			ExchangeContext context ) {

		AppUndeploymentEvent event = new AppUndeploymentEvent();

		String serviceID = (String) context.getData( SERVICE_ID );
		// String type = (String)context.getData(QUERY_TYPE);

		event.setTargetServiceID( serviceID );

		return event;
	}


	protected void sendResponse( AppUndeploymentEvent event,
			ExchangeContext context ) {

		String serviceName = (String) event.getTargetServiceName();
		List<AppReplica> reps = (List<AppReplica>) event
				.getDeployedRepetitions();

		context.storeData( SERVICE_NAME, serviceName );
		context.storeData( DEPLOYED_REPS, reps );

		doSend( context );
	}


	protected void doSend( ExchangeContext context ) {
		try {
			getReceiver().sendResponseMessage( context );
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	protected Document createExceptionMessage( String message ) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warn( "Can't create the document" );
			return null;
		}
		Document document = builder.newDocument();
		Element root = document.createElement( "undeployQueryResponse" );
		Element excep = document.createElement( "exception" );
		excep.setTextContent( message );
		root.appendChild( excep );
		document.appendChild( root );

		return document;
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
