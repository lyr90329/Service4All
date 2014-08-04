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

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class AppDeploymentFeedbackReceiver extends Receiver {

	protected Log logger = LogFactory
			.getLog( AppDeploymentFeedbackReceiver.class );


	@Override
	public Document createResponseDocument( ExchangeContext arg0 )
			throws MessageExchangeInvocationException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element root = document.createElement( "feedbackResponse" );
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

		logger.info( "Receiving the deployment feedback message !" );
		logger.info( "The request: "
				+ XMLUtils.retrieveDocumentAsString( request ) );

		if (!validate( request )) {
			logger.warn( "The message document is invalidated for deployment" );
			// send the exception message
			this.sendResponseMessage( context );
			return;
		}

		parseRequest( request, context );

		this.unit.dispatch( context );

	}


	protected void parseRequest( Document req, ExchangeContext context ) {

		logger.info( "The document validated!" );

		Element root = req.getDocumentElement();
		String deployType = root.getAttribute( "type" );

		Element idElement = (Element) root.getElementsByTagName( "serviceID" )
				.item( 0 );
		String serviceID = idElement.getTextContent();

		Element nameElement = (Element) root.getElementsByTagName(
				"serviceName" ).item( 0 );
		String name = nameElement.getTextContent();

		Element isSucElement = (Element) root.getElementsByTagName(
				"isSuccessful" ).item( 0 );
		String isSuccessfulText = isSucElement.getTextContent();
		boolean isSuccessful = Boolean.valueOf( isSuccessfulText );

		NodeList cs = root.getElementsByTagName( "deployedContainer" );
		Map<String, String> deployedmaps = new HashMap<String, String>();

		for (int i = 0; i < cs.getLength(); i++) {
			Element d = (Element) cs.item( i );
			String id = d.getAttribute( "id" );
			String invokeUrl = d.getAttribute( "invokeUrl" );
			deployedmaps.put( id, invokeUrl );
		}

		context.storeData( AppDeploymentFeedbackBusinessUnit.SERVICE_ID,
				serviceID );
		context.storeData( AppDeploymentFeedbackBusinessUnit.SERVICE_NAME, name );
		context.storeData( AppDeploymentFeedbackBusinessUnit.DEPLOY_RESULTS,
				deployedmaps );
		context.storeData( AppDeploymentFeedbackBusinessUnit.IS_SUCCESSFUL,
				String.valueOf( isSuccessful ) );
		context.storeData( AppDeploymentFeedbackBusinessUnit.DEPLOY_TYPE,
				deployType );
	}


	protected boolean validate( Document doc ) {
		if (doc.getElementsByTagName( "deployFeedback" ) == null
				|| doc.getElementsByTagName( "deployFeedback" ).getLength() <= 0) {
			logger.warn( "Miss the [deployfeedback] element for app deployment feedback!" );
			return false;
		}
		if (doc.getElementsByTagName( "serviceID" ) == null
				|| doc.getElementsByTagName( "serviceID" ).getLength() <= 0) {
			logger.warn( "Miss the [serviceID] element for app deployment feedback!" );
			return false;
		}
		if (doc.getElementsByTagName( "isSuccessful" ) == null
				|| doc.getElementsByTagName( "isSuccessful" ).getLength() <= 0) {
			logger.warn( "Miss the [isSuccessful] element for app deployment feedback!" );
			return false;
		}

		return true;
	}

}
