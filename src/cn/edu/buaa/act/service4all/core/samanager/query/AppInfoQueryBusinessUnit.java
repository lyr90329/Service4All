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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.App;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;

public class AppInfoQueryBusinessUnit extends SAManagerBusinessUnit {

	private Log logger = LogFactory.getLog( AppInfoQueryBusinessUnit.class );

	protected static String QUERY_TYPE = "queryType";
	protected static String APP_ID = "appId";
	protected static String QUERY_RESULT = "queryResult";


	@Override
	public void dispatch( ExchangeContext context ) {

		String type = (String) context.getData( QUERY_TYPE );
		String appId = (String) context.getData( APP_ID );
		try {
			App queryResult = queryAppByType( type, appId );

			// set the query result to the exchange context
			context.storeData( QUERY_RESULT, queryResult );

			// send the response message by the receiver
			this.getReceiver().sendResponseMessage( context );

		} catch (AppException e) {
			this.handleInvocationException( new MessageExchangeInvocationException(
					e.getMessage() ) );
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	private App queryAppByType( String type, String appId ) throws AppException {

		HostManager hostManager = (HostManager) this.applianceManager;

		if (type.equalsIgnoreCase( "webservice" )) {

			// get the axis2 manager from the host manager
			ApplianceManager axis2Manager = hostManager
					.getApplianceManager( "axis2" );
			AppManager wsManager = axis2Manager.getAppManager();

			logger.info( "Query the app's information: " + appId );
			return wsManager.getAppById( appId );

		} else {

			logger.warn( "The query type is not supported recently : " + type );
			throw new AppException(
					"The query type is not supported recently : " + type );

		}

	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
