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

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerReceiver;

public class AppUndeploymentFeedbackReceiver extends SAManagerReceiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element root = document.createElement( "UndeployFeedbackResponse" );
			root.setTextContent( "feedback successfully!" );
			document.appendChild( root );

			return document;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {

		logger.info( "Receiving the undeployment feedback message :"
				+ XMLUtils.retrieveDocumentAsString( request ) );

		if (!validateRequest( request )) {
			logger.warn( "The request message for app undeployment feedback is invalidate!" );
			throw new MessageExchangeInvocationException(
					"The request message for app undeployment feedback is invalidate!" );
		}

		// parse the request
		parseRequest( request, context );

		this.unit.dispatch( context );

	}


	@Override
	protected void parseRequest( Document request, ExchangeContext context ) {

		// logger.info("The document for the app undeployment feedback is invalidated!");
		Element root = request.getDocumentElement();
		String deployType = root.getAttribute( "type" );

		Element idElement = (Element) root.getElementsByTagName( "serviceID" )
				.item( 0 );
		String serviceID = idElement.getTextContent();

		String name = serviceID;

		Element isSucElement = (Element) root.getElementsByTagName(
				"isSuccessful" ).item( 0 );

		String isSuccessfulText = isSucElement.getTextContent();
		boolean isSuccessful = Boolean.valueOf( isSuccessfulText );

		NodeList cs = root.getElementsByTagName( "container" );
		// Map<String, String> undeployedmaps = new HashMap<String, String>();
		List<String> undeployedResults = new ArrayList<String>();
		for (int i = 0; i < cs.getLength(); i++) {
			Element d = (Element) cs.item( i );
			String id = d.getAttribute( "id" );
			String is = d.getAttribute( "isSuccessful" );
			if (is.equals( "true" )) {
				undeployedResults.add( id );
			}
		}

		context.storeData( AppUndeploymentFeedbackBusinessUnit.SERVICE_ID,
				serviceID );
		context.storeData( AppUndeploymentFeedbackBusinessUnit.SERVICE_NAME,
				name );
		context.storeData(
				AppUndeploymentFeedbackBusinessUnit.UNDEPLOY_RESULTS,
				undeployedResults );
		context.storeData( AppUndeploymentFeedbackBusinessUnit.TYPE, deployType );
		context.storeData( AppUndeploymentFeedbackBusinessUnit.IS_SUCCE,
				String.valueOf( isSuccessful ) );

	}

}
