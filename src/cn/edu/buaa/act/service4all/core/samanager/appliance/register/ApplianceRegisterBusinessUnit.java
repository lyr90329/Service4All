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
package cn.edu.buaa.act.service4all.core.samanager.appliance.register;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceRegisterEvent;

public class ApplianceRegisterBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( ApplianceRegisterBusinessUnit.class );

	public static final String HOST_DATA = "appliance";
	public static final String HOSTEDS = "hosteds";


	@Override
	public void dispatch( ExchangeContext context ) {

		logger.info( "Generate a appliance register event" );

		ApplianceRegisterEvent registerEvent = createApplianceRegisterEvent( context );

		List<ApplianceListener> listeners = this.getApplianceListeners();

		for (ApplianceListener l : listeners) {
			try {
				logger.info( "Pass the appliance event to the listener: "
						+ l.getClass().getSimpleName() );
				l.registerAppliance( registerEvent );
			} catch (ApplianceException e) {
				logger.error( e.getMessage() );
			}
		}

		// (HostManager)this.applianceManager
		logger.info( "Send the response message for appliance register!" );
		try {

			this.getReceiver().sendResponseMessage( context );

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException( e );
		}
	}


	protected ApplianceRegisterEvent createApplianceRegisterEvent(
			ExchangeContext context ) {
		Host host = (Host) context.getData( HOST_DATA );
		ApplianceRegisterEvent registerEvent = new ApplianceRegisterEvent();

		registerEvent.setTargetAppliance( host );

		@SuppressWarnings("unchecked")
		List<AppReplica> hosteds = (List<AppReplica>) context
				.getData( HOSTEDS );
		registerEvent.setHosteds( hosteds );

		return registerEvent;
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
