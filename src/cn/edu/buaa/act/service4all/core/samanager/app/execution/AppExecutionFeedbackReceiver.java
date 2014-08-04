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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerReceiver;

public class AppExecutionFeedbackReceiver extends SAManagerReceiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element root = document
					.createElement( "appInvocationFeedbackResponse" );
			document.appendChild( root );
			root.setTextContent( "Good Job!" );

			return document;
		} catch (ParserConfigurationException e) {
			logger.error( e.getMessage(), e );
		}
		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Receive a request for service execution feedback!" );

		if (!validateRequest( request )) {
			logger.warn( "The request message for execution feedback is invalidate!" );
			throw new MessageExchangeInvocationException(
					"The request message for execution feedback is invalidate!" );
		}

		// parse the request and set vars into context
		parseRequest( request, context );

		this.unit.dispatch( context );
	}


	@Override
	protected void parseRequest( Document request, ExchangeContext context ) {

		// serviceID isSuccessful and serviceList
		Element root = request.getDocumentElement();

		String isSuccessful = root.getElementsByTagName( "isSuccessful" )
				.item( 0 ).getTextContent();
		String invokeType = root.getAttribute( "type" );
		context.storeData( AppExecutionFeedbackBusinessUnit.EXEC_TYPE,
				invokeType );
		context.storeData( AppExecutionFeedbackBusinessUnit.IS_SUCCESS,
				isSuccessful );
		// get the <service> list
		List<AppExecutionResult> serviceList = getServiceList( root );
		context.storeData( AppExecutionFeedbackBusinessUnit.SERVICE_LIST,
				serviceList );

		if (invokeType.equals( "id" )) {
			// get the service id
			String serviceID = root.getElementsByTagName( "serviceID" )
					.item( 0 ).getTextContent();
			context.storeData( AppExecutionFeedbackBusinessUnit.SERVICE_ID,
					serviceID );
		}
	}


	protected List<AppExecutionResult> getServiceList( Element root ) {
		List<AppExecutionResult> results = new ArrayList<AppExecutionResult>();
		NodeList nodes = root.getElementsByTagName( "service" );
		for (int i = 0; i < nodes.getLength(); i++) {
			Element serviceElement = (Element) nodes.item( i );
			String url = serviceElement.getElementsByTagName( "url" ).item( 0 )
					.getTextContent();
			String isSuccessful = serviceElement
					.getElementsByTagName( "isSuccessful" ).item( 0 )
					.getTextContent();
			String startDate = serviceElement
					.getElementsByTagName( "startDate" ).item( 0 )
					.getTextContent();
			;

			// create a new AppExecutionResult instance
			AppExecutionResult r = new AppExecutionResult();
			r.setStartDate( new Date( Long.valueOf( startDate ) ) );
			r.setSuccessful( Boolean.valueOf( isSuccessful ) );
			r.setUrl( url );

			if (isSuccessful.equals( "true" )) {
				// get the last time
				String lastTime = serviceElement
						.getElementsByTagName( "lastTime" ).item( 0 )
						.getTextContent();
				r.setLastTime( Long.valueOf( lastTime ) );
			}

			results.add( r );
		}
		return results;

	}

}
