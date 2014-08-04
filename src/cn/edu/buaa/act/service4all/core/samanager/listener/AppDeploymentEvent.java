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
package cn.edu.buaa.act.service4all.core.samanager.listener;

import java.util.List;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class AppDeploymentEvent {

	private List<Appliance> queryResults;
	/**
	 * the appliances' id list who host the app repetitions
	 */
	private List<AppReplica> deployedResults;
	private String deployedServiceId = null;
	private String deployedServiceName;
	private int deployNum;


	public AppDeploymentEvent() {
	}


	public List<Appliance> getQueryResults() {
		return queryResults;
	}


	public void setQueryResults( List<Appliance> queryResults ) {
		this.queryResults = queryResults;
	}


	public List<AppReplica> getDeployResults() {
		return deployedResults;
	}


	public void setDeployResults( List<AppReplica> deployResults ) {
		this.deployedResults = deployResults;
	}


	public String getDeployedServiceId() {
		return deployedServiceId;
	}


	public void setDeployedServiceId( String deployedServiceId ) {
		this.deployedServiceId = deployedServiceId;
	}


	public String getDeployedServiceName() {
		return deployedServiceName;
	}


	public void setDeployedServiceName( String deployedServiceName ) {
		this.deployedServiceName = deployedServiceName;
	}


	public List<AppReplica> getDeployedResults() {
		return deployedResults;
	}


	public void setDeployedResults( List<AppReplica> deployedResults ) {
		this.deployedResults = deployedResults;
	}


	public int getDeployNum() {
		return deployNum;
	}


	public void setDeployNum( int deployNum ) {
		this.deployNum = deployNum;
	}

}
