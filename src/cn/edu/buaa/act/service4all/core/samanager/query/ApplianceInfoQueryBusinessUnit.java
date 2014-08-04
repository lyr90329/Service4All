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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;

public class ApplianceInfoQueryBusinessUnit extends SAManagerBusinessUnit {

	private Log logger = LogFactory
			.getLog( ApplianceInfoQueryBusinessUnit.class );

	protected static String QUERY_TYPE = "queryType";
	protected static String APPLIANCE_ID = "applianceId";
	/**
	 * the query result refers to a App instance
	 */
	protected static String QUERY_RESULT = "queryResult";
	protected static String QUERY_APPS_RESULT = "queryAppRepetionResult";


	@Override
	public void dispatch( ExchangeContext context ) {

		String applianceId = (String) context.getData( APPLIANCE_ID );
		String type = (String) context.getData( QUERY_TYPE );

		logger.info( "Query the type( " + type + " )of the appliance : "
				+ applianceId );

		try {

			queryAppliance( type, applianceId, context );

			this.getReceiver().sendResponseMessage( context );

		} catch (ApplianceException e) {

			MessageExchangeInvocationException excep = new MessageExchangeInvocationException(
					e.getMessage() );
			this.handleInvocationException( excep );

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	private void queryAppliance( String type, String applianceId,
			ExchangeContext context ) throws ApplianceException {

		HostManager hostManager = (HostManager) this.applianceManager;

		if (type.equals( "host" )) {
			context.storeData( QUERY_RESULT,
					hostManager.getApplianceById( applianceId ) );
			return;
		}

		ApplianceManager typedApplianceManager = hostManager
				.getApplianceManager( type );
		if (typedApplianceManager == null) {
			logger.error( "Can't get the appliance manager by the appliance type : "
					+ type );
			throw new ApplianceException(
					"Can't get the appliance manager by the appliance type : "
							+ type );
		}

		Appliance result = typedApplianceManager.getApplianceById( applianceId );
		context.storeData( QUERY_RESULT, result );

		// get the app on this appliance
		// List<String> appIds =
		// typedApplianceManager.getAppIdsByApplianceId(applianceId);

		AppManager appManager = typedApplianceManager.getAppManager();
		if (appManager == null) {
			logger.warn( "There is no apps manager in the appliance: "
					+ applianceId );
			return;
		}

		List<AppReplica> reps = appManager
				.getAppRepetitionsByApplianceId( applianceId );
		context.storeData( QUERY_APPS_RESULT, reps );

	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
