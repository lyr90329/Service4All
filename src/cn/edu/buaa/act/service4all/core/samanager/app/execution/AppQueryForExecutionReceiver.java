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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.IDCounter;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceStatus;

public class AppQueryForExecutionReceiver extends Receiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Create the response message document for apps query!" );

		String serviceID = (String) context
				.getData( AppQueryForExecutionBusinessUnit.SERVICE_ID );
		String type = (String) context
				.getData( AppQueryForExecutionBusinessUnit.QUERY_TYPE );
		@SuppressWarnings("unchecked")
		List<AppReplica> apps = (List<AppReplica>) context
				.getData( AppQueryForExecutionBusinessUnit.SERVICE_LIST );

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element root = document.createElement( "availableServiceResponse" );
			root.setAttribute( "type", type );
			// root.setAttribute(QUERY_ALGORITHM, event.getAlgorithm());

			Element idElement = document.createElement( "serviceID" );
			idElement.setTextContent( serviceID );
			root.appendChild( idElement );

			Element jobIDElement = document.createElement( "jobId" );
			jobIDElement.setTextContent( String.valueOf( IDCounter
					.generateJobID() ) );
			root.appendChild( jobIDElement );

			Element numElement = document.createElement( "num" );

			if (apps == null || apps.size() <= 0) {

				numElement.setTextContent( "0" );
				root.appendChild( numElement );

				Element despElement = document.createElement( "description" );
				despElement
						.setTextContent( "There is no available service for the ID: "
								+ serviceID );
				root.appendChild( despElement );

			} else {
				numElement.setTextContent( String.valueOf( apps.size() ) );
				root.appendChild( numElement );
				// adding the service list information
				Element serviceList = document.createElement( "services" );
				for (AppReplica a : apps) {

					Element service = document.createElement( "service" );
					service.setAttribute( "url", a.getInvocationUrl() );

					Appliance appliance = a.getContainer();
					if (appliance != null) {
						ApplianceStatus status = appliance.getStatus();
						float cpu = status.getCpuRate();
						float memory = status.getMemoryfloat();
						double throughput = status.getPort();
						int deployedAmount = status.getDeployedAmount();

						service.setAttribute( "cpu", String.valueOf( cpu ) );
						service.setAttribute( "memory", String.valueOf( memory ) );
						service.setAttribute( "throughput",
								String.valueOf( throughput ) );
						service.setAttribute( "deployedServiceAmount",
								String.valueOf( deployedAmount ) );

					}

					serviceList.appendChild( service );

				}

				root.appendChild( serviceList );
			}

			document.appendChild( root );
			return document;
		} catch (ParserConfigurationException e) {
			logger.error( e.getMessage(), e );
		}
		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Handling the service query request!" );
		// AppQueryEvent event = generateQueryEvent(request);

		// if(event == null){
		// try {
		//
		// Document respExcepDocument =
		// createDocParseExceptionDocument("The request document is invalidate!");
		// this.sendResponseMessage(respExcepDocument, context);
		//
		// return;
		// } catch (ParserConfigurationException e) {
		// logger.warn(e.getMessage(), e);
		// }
		// }
		// //store the event into the context
		// context.storeData(AppQueryForExecutionBusinessUnit.APP_QUERY_EVENT,
		// event);
		// this.unit.dispatch(context);

		if (!validate( request )) {
			logger.warn( "The request message for querying execution app is invalidate!" );
			throw new MessageExchangeInvocationException(
					"The request message for querying execution app is invalidate!" );
		}

		// parse the request message
		parseRequest( request, context );
		this.unit.dispatch( context );
	}


	protected void parseRequest( Document request, ExchangeContext context ) {

		String type = request.getDocumentElement().getAttribute( "type" );

		Element serviceIDElement = (Element) request.getElementsByTagName(
				"serviceID" ).item( 0 );
		String id = serviceIDElement.getTextContent();

		context.storeData( AppQueryForExecutionBusinessUnit.SERVICE_ID, id );
		context.storeData( AppQueryForExecutionBusinessUnit.QUERY_TYPE, type );

	}


	protected boolean validate( Document req ) {
		return true;
	}


	// private AppQueryEvent generateQueryEvent(Document request){
	//
	// AppQueryEvent event = new AppQueryEvent();
	// if(request.getElementsByTagName(SERVICE_ID) != null
	// && request.getElementsByTagName(SERVICE_ID).getLength() >= 1){
	//
	// String type = request.getDocumentElement().getAttribute(QUERY_TYPE);
	// String algorithm =
	// request.getDocumentElement().getAttribute(QUERY_ALGORITHM);
	// event.setAlgorithm(algorithm);
	// event.setQueryType(type);
	//
	// Element serviceIDElement =
	// (Element)request.getElementsByTagName(SERVICE_ID).item(0);
	// String id = serviceIDElement.getTextContent();
	// event.setTargetServiceID(id);
	//
	// return event;
	//
	// }
	// return null;
	//
	// }

	protected Document createExceptionDocument( String message,
			ExchangeContext context ) throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element root = document.createElement( "availableServiceResponse" );
		root.setAttribute( "type", (String) context
				.getData( AppQueryForExecutionBusinessUnit.QUERY_TYPE ) );

		Element idElement = document.createElement( "serviceID" );
		idElement.setTextContent( "0" );
		root.appendChild( idElement );

		Element numElement = document.createElement( "num" );
		numElement.setTextContent( "0" );
		root.appendChild( numElement );

		Element despElement = document.createElement( "description" );
		despElement.setTextContent( message );
		root.appendChild( despElement );

		document.appendChild( root );

		return document;
	}

}
