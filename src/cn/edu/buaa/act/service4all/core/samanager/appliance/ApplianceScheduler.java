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
package cn.edu.buaa.act.service4all.core.samanager.appliance;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;

public class ApplianceScheduler {

	private Log logger = LogFactory.getLog( ApplianceScheduler.class );

	private HostManager hostManager;


	public HostManager getHostManager() {
		return hostManager;
	}


	public void setHostManager( HostManager hostManager ) {
		this.hostManager = hostManager;
	}


	public PriorityQueue<Appliance> sortAppliance( List<Appliance> hosts,
			String type ) throws ApplianceException {

		if (hosts == null || hosts.size() <= 0) {
			logger.warn( "The target hosts for deployment scheduling is null!" );
			throw new ApplianceException(
					"The target hosts for deployment scheduling is null!" );
		}

		Comparator comparator = new HostComparator( type );
		PriorityQueue<Appliance> sorteds = new PriorityQueue<Appliance>( 10,
				comparator );
		sorteds.addAll( hosts );

		return sorteds;
	}

}
