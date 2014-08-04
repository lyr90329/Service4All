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
package cn.edu.buaa.act.service4all.core.samanager.beans;

/**
 * The real body for App invocation
 * Each App may have some repetitions in different appliances
 * 
 * @author Huangyj
 *
 */
public class AppReplica {
	
	protected Appliance container;
	protected App app;
	protected String invocationUrl;
	protected AppRecords records;
	protected String containerId;
	protected AppReplicaStatus status;
	
	protected String appName;
	protected String appId;
	
	
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Appliance getContainer() {
		return container;
	}
	
	public void setContainer(Appliance container) {
		this.container = container;
//		this.containerId = container.getDesp().getId();
	}
	
	public String getContainerId(){
		return this.containerId;
	}
	
	public App getDesription() {
		return app;
	}
	
	public void setDesription(App desription) {
		this.app = desription;
	}
	
	public String getInvocationUrl() {
		return invocationUrl;
	}
	
	public void setInvocationUrl(String invocationUrl) {
		this.invocationUrl = invocationUrl;
	}
	
	/**
	 * We have to reconsider the interface operations about App Records
	 * 
	 * @return
	 */
	public AppRecords getRecords() {
		return records;
	}
	
	public void setRecords(AppRecords records) {
		this.records = records;
	}

	public AppReplicaStatus getStatus() {
		return status;
	}

	public void setStatus(AppReplicaStatus status) {
		this.status = status;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	
	
}
