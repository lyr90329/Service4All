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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ApplianceUndeploymentReceiver extends Receiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement( "applianceUndeploymentResponse" );
			String is = (String) context
					.getData( ApplianceUndeploymentBusinessUnit.IS_SUCCESS_DEPLOY );

			if (is != null && is.equalsIgnoreCase( "false" )) {
				root.setAttribute( "isSuccessful", is );
				root.setTextContent( (String) context
						.getData( ApplianceUndeploymentBusinessUnit.EXCEP_DESP ) );
			} else {
				root.setAttribute( "isSuccessful", "true" );
				root.setTextContent( "Appliance is undeployed successfully!" );
			}

			doc.appendChild( root );
			return doc;
		} catch (ParserConfigurationException e) {
			logger.warn( e.getMessage() );
		}

		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		Element root = request.getDocumentElement();
		String type = root.getAttribute( "type" );
		String id = root.getAttribute( "id" );

		if (type == null) {

			this.sendResponseMessage(
					createExceptionDoc( "Can't judge the undeployment type" ),
					context );
			return;

		}

		context.storeData( ApplianceUndeploymentBusinessUnit.UNDEPLOYMENT_TYPE,
				type );
		context.storeData( ApplianceUndeploymentBusinessUnit.APPLIANCE_ID, id );

		this.unit.dispatch( context );
	}


	private Document createExceptionDoc( String msg ) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement( "applianceUndeploymentResponse" );
			root.setAttribute( "isSuccessful", "false" );
			root.setTextContent( msg );

			doc.appendChild( root );
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

}
