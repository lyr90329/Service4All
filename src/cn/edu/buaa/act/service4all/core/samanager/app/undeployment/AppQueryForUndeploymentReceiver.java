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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerReceiver;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class AppQueryForUndeploymentReceiver extends SAManagerReceiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		// can't check the parameterized list
		@SuppressWarnings("unchecked")
		List<AppReplica> applianceRepetitions = (List<AppReplica>) context
				.getData( AppQueryForUndeploymentBusinessUnit.DEPLOYED_REPS );

		String type = (String) context
				.getData( AppQueryForUndeploymentBusinessUnit.QUERY_TYPE );
		String serviceName = (String) context
				.getData( AppQueryForUndeploymentBusinessUnit.SERVICE_NAME );
		String serviceId = (String) context
				.getData( AppQueryForUndeploymentBusinessUnit.SERVICE_ID );

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warn( "Can't create the document" );
			throw new MessageExchangeInvocationException(
					"Can't create the document for query available servers!" );
		}

		Document document = builder.newDocument();
		Element root = document.createElement( "undeployQueryResponse" );
		root.setAttribute( "type", type );
		document.appendChild( root );

		Element idElement = document.createElement( "serviceID" );
		idElement.setTextContent( serviceId );
		root.appendChild( idElement );

		Element serviceNameElement = document.createElement( "serviceName" );
		serviceNameElement.setTextContent( serviceName );
		root.appendChild( serviceNameElement );

		Element containerListElement = document.createElement( "containerList" );

		if (applianceRepetitions == null) {
			containerListElement.setAttribute( "length", "0" );
		} else {

			int i = 0;
			for (AppReplica rep : applianceRepetitions) {
				Element c = document.createElement( "container" );

				Appliance parent = rep.getContainer();
				if (parent != null) {

					c.setAttribute( "id", parent.getDesp().getId() );
					c.setAttribute( "undeployUrl", parent.getDesp()
							.getUndeployEPR() );
					c.setAttribute( "undeployOperation", parent.getDesp()
							.getUndeployOperation() );
					containerListElement.appendChild( c );
					i++;

				}
			}
			containerListElement.setAttribute( "length", String.valueOf( i ) );
		}

		root.appendChild( containerListElement );

		return document;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Receive undeploy app query request!" );

		if (!validateRequest( request )) {
			logger.warn( "The request message for undeployment app query is invalidate!" );
			throw new MessageExchangeInvocationException(
					"The request message for undeployment app query is invalidate!" );
		}

		// parse the request
		parseRequest( request, context );

		this.unit.dispatch( context );
	}


	@Override
	protected void parseRequest( Document request, ExchangeContext context ) {
		Element root = request.getDocumentElement();
		String queryType = root.getAttribute( "type" );
		context.storeData( AppQueryForUndeploymentBusinessUnit.QUERY_TYPE,
				queryType );

		Element sn = (Element) root.getElementsByTagName( "serviceID" )
				.item( 0 );
		String serviceID = sn.getTextContent();
		context.storeData( AppQueryForUndeploymentBusinessUnit.SERVICE_ID,
				serviceID );

	}

}
