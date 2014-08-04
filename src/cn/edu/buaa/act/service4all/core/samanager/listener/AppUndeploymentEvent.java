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

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;

/**
 * The undeployment event for one type of application
 * 
 * @author Huangyj
 *
 */
public class AppUndeploymentEvent {
	
	protected String targetServiceID  = null;
	protected String targetServiceName = null;
	
	/**
	 * the undeployment query result which contain all the 
	 */
	protected List<AppReplica> deployedRepetitions;
	
	/**
	 * the applianceId
	 */
	protected List<String> undeployedResults;
	
	protected boolean isSuccessful;
	
	public AppUndeploymentEvent(){
		deployedRepetitions = new ArrayList<AppReplica>();
		undeployedResults = new ArrayList<String>();
		isSuccessful = false;
	}

	public String getTargetServiceID() {
		return targetServiceID;
	}

	public void setTargetServiceID(String targetServiceID) {
		this.targetServiceID = targetServiceID;
	}

	public String getTargetServiceName() {
		return targetServiceName;
	}

	public void setTargetServiceName(String targetServiceName) {
		this.targetServiceName = targetServiceName;
	}

	public List<AppReplica> getDeployedRepetitions() {
		return deployedRepetitions;
	}

	public void setDeployedRepetitions(List<AppReplica> deployedRepetitions) {
		this.deployedRepetitions = deployedRepetitions;
	}

	public List<String> getUndeployedResults() {
		return undeployedResults;
	}

	public void setUndeployedResults(List<String> undeployedResults) {
		this.undeployedResults = undeployedResults;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	
	
	
}
