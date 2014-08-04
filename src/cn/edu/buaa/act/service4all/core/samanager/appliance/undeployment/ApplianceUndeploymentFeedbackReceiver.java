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

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ApplianceUndeploymentFeedbackReceiver extends Receiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		logger.info( "Receiving the undeployment feedback message !" );

		logger.info( "The document validated!" );
		Element root = request.getDocumentElement();
		String deployType = root.getAttribute( "type" );
		if (deployType == null) {
			deployType = "webservice";
		}

		Element idElement = (Element) root.getElementsByTagName( "serviceID" )
				.item( 0 );
		String serviceID = idElement.getTextContent();

		String name = serviceID;

		Element isSucElement = (Element) root.getElementsByTagName(
				"isSuccessful" ).item( 0 );

		String isSuccessfulText = isSucElement.getTextContent();
		boolean isSuccessful = Boolean.valueOf( isSuccessfulText );
		if (!isSuccessful) {
			logger.info( "The deployment is unsuccessful!" );
			// do nothing
			this.sendResponseMessage( context );
			return;
		}

		NodeList cs = root.getElementsByTagName( "containerList" );
		Map<String, String> undeployedmaps = new HashMap<String, String>();
		for (int i = 0; i < cs.getLength(); i++) {
			Element d = (Element) cs.item( i );
			String id = d.getAttribute( "id" );
			String is = d.getAttribute( "isSuccessful" );
			undeployedmaps.put( id, is );
		}

		context.storeData( "serviceID", serviceID );
		context.storeData( "serviceName", name );
		context.storeData( "undeployResults", undeployedmaps );

		this.unit.dispatch( context );

	}

}
