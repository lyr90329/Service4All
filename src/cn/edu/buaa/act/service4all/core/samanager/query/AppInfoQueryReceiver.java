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
package cn.edu.buaa.act.service4all.core.samanager.query;

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.beans.App;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;

public class AppInfoQueryReceiver extends Receiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		App app = (App) context.getData( AppInfoQueryBusinessUnit.QUERY_RESULT );
		String type = (String) context
				.getData( AppInfoQueryBusinessUnit.QUERY_TYPE );
		String appId = (String) context
				.getData( AppInfoQueryBusinessUnit.APP_ID );

		logger.info( "Create the response message for the : " + type );
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resp = builder.newDocument();
			Element root = resp.createElement( "queryAppInfoResponse" );
			resp.appendChild( root );
			root.setAttribute( "type", type );
			root.setAttribute( "appId", appId );

			// judge the app whether is null
			if (app == null) {

				logger.warn( "The target query app does not exist: " + appId );
				Element repsElement = resp.createElement( "repetitions" );
				root.appendChild( repsElement );
				repsElement.setAttribute( "num", "-1" );

				return resp;

			}

			Element despElement = resp.createElement( "description" );
			// appsElement.setAttribute("num",
			// String.valueOf(queryResults.size()));
			root.appendChild( despElement );
			Element appNameElement = resp.createElement( "appName" );
			appNameElement.setTextContent( app.getName() );
			despElement.appendChild( appNameElement );

			Element repsElement = resp.createElement( "repetitions" );
			root.appendChild( repsElement );

			List<AppReplica> reps = app.getBackups();
			if (reps == null) {
				// there is no app repetition for the app
				repsElement.setAttribute( "num", "0" );

			} else {

				repsElement.setAttribute( "num", String.valueOf( reps.size() ) );
				for (AppReplica r : reps) {

					Element rElement = resp.createElement( "repetition" );
					rElement.setAttribute( "applianceId", r.getContainerId() );
					rElement.setAttribute( "invokeUrl", r.getInvocationUrl() );
					repsElement.appendChild( rElement );

				}
			}

			return resp;
		} catch (ParserConfigurationException e) {
			logger.warn( "Can't create the response message : "
					+ e.getMessage() );
			throw new MessageExchangeInvocationException(
					"Can't create the response message : " + e.getMessage() );
		}
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		Element root = request.getDocumentElement();
		String queryType = root.getAttribute( "type" );
		String appId = root.getAttribute( "appId" );

		if (queryType == null) {
			logger.warn( "Miss the type attribute for the app query request!" );
			throw new MessageExchangeInvocationException(
					"Miss the type attribute for the app query request!" );
		}
		if (appId == null) {
			logger.warn( "Miss the appId attribute for the app query request!" );
			throw new MessageExchangeInvocationException(
					"Miss the appId attribute for the app query request!" );
		}
		context.storeData( AppInfoQueryBusinessUnit.QUERY_TYPE, queryType );
		context.storeData( AppInfoQueryBusinessUnit.APP_ID, appId );

		this.unit.dispatch( context );
	}

}
