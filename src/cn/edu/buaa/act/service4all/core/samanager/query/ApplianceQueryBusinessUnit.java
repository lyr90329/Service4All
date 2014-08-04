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
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;

public class ApplianceQueryBusinessUnit extends SAManagerBusinessUnit {

	private Log logger = LogFactory.getLog( ApplianceQueryBusinessUnit.class );

	protected final static String QUERY_TYPE = "queryType";
	protected final static String QUERY_RESULT = "queryResults";


	@Override
	public void dispatch( ExchangeContext context ) {
		String type = (String) context.getData( QUERY_TYPE );

		// query the specific appliance
		try {
			List<Appliance> queryResults = queryAppliance( type );
			context.storeData( QUERY_RESULT, queryResults );

			// send the response to the client by receiver
			this.getReceiver().sendResponseMessage( context );

		} catch (ApplianceException e) {
			this.handleInvocationException( new MessageExchangeInvocationException(
					e.getMessage() ) );
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}


	private List<Appliance> queryAppliance( String type )
			throws ApplianceException {

		logger.info( "Query the type of  appliance query: " + type );
		HostManager hostManager = (HostManager) this.applianceManager;

		if (type.equalsIgnoreCase( "all" )) {
			// query all the appliances by the host column
			List<Appliance> hosts = hostManager.getAllHostsAndSubAppliance();

			return hosts;
		} else if (type.equalsIgnoreCase( "host" )) {
			List<Appliance> hosts = hostManager.getAllHosts();
			return hosts;
		} else if (type.equalsIgnoreCase( "axis2" )) {

			// get the axis2 manager from the hostManager
			ApplianceManager axis2Manager = hostManager
					.getApplianceManager( "axis2" );
			if (axis2Manager == null) {
				logger.warn( "The axis2 manager is null from the host Manager" );
				throw new ApplianceException(
						"The axis2 manager is null from the host Manager" );

			}

			return axis2Manager.getAllAppliances();

		} else if (type.equalsIgnoreCase( "appserver" )) {

			// get the tomcat manager from the hostManager
			ApplianceManager tomcatManager = hostManager
					.getApplianceManager( "appserver" );
			if (tomcatManager == null) {
				logger.warn( "The tomcat manager is null from the host Manager" );
				throw new ApplianceException(
						"The tomcat manager is null from the host Manager" );

			}

			return tomcatManager.getAllAppliances();
		} else {
			logger.warn( "The querying appliance type is unknown: " + type );
			throw new ApplianceException(
					"The querying appliance type is unknown: " + type );
		}

	}
}
