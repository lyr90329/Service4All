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

import java.util.Date;

/**
 * The description for the appliance 
 * including the appliance id and addresses
 * 
 * @author dell
 *
 */
public class ApplianceDescription {
	
	/**
	 * the uniquely variant to identify appliance
	 * which is formatted by "ip_port"
	 * 
	 */
	
	protected final String suffix = "/axis2/services/localSAManagerService/";
	
	protected String id;
	
	protected String ip = null;
	protected String port = null;

	protected String deployEPR = null;
	protected String deployOperation = "deployRemoteService";
	
	protected String undeployEPR = null;
	protected String undeployOperation = "undeployRemoteService";
	
	protected String startEPR = null;
	protected String startOperation = null;
	
	protected String restartEPR = null;
	protected String restartOperation = null;
	
	protected String stopEPR = null;
	protected String stopOperation = null;
	
	protected Appliance appliance;
	
	protected Date updateDate;
	
	protected Appliance parent;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	
	public String getDeployOperation() {
		return deployOperation;
	}
	public void setDeployOperation(String deployOperation) {
		this.deployOperation = deployOperation;
	}
	
	public void setDeployEPR(String deployEPR) {
		this.deployEPR = deployEPR;
	}
	public void setUndeployEPR(String undeployEPR) {
		this.undeployEPR = undeployEPR;
	}
	public String getUndeployOperation() {
		return undeployOperation;
	}
	public void setUndeployOperation(String undeployOperation) {
		this.undeployOperation = undeployOperation;
	}
	public String getStartEPR() {
		return startEPR;
	}
	public void setStartEPR(String startEPR) {
		this.startEPR = startEPR;
	}
	public String getStartOperation() {
		return startOperation;
	}
	public void setStartOperation(String startOperation) {
		this.startOperation = startOperation;
	}
	public String getRestartEPR() {
		return restartEPR;
	}
	public void setRestartEPR(String restartEPR) {
		this.restartEPR = restartEPR;
	}
	public String getRestartOperation() {
		return restartOperation;
	}
	public void setRestartOperation(String restartOperation) {
		this.restartOperation = restartOperation;
	}
	public String getStopEPR() {
		return stopEPR;
	}
	public void setStopEPR(String stopEPR) {
		this.stopEPR = stopEPR;
	}
	public String getStopOperation() {
		return stopOperation;
	}
	public void setStopOperation(String stopOperation) {
		this.stopOperation = stopOperation;
	}
	public Appliance getAppliance() {
		return appliance;
	}
	public void setAppliance(Appliance appliance) {
		this.appliance = appliance;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Appliance getParent() {
		return parent;
	}
	public void setParent(Appliance parent) {
		this.parent = parent;
	}
	////////////////////////////////////////////////
	public String getUndeployEPR() {
		if(undeployEPR == null){
			if(ip != null && port != null){
				if(ip.startsWith("http")){
					undeployEPR = ip + ":" + port + suffix;
				}else{
					undeployEPR = "http://" + ip + ":" + port + suffix;
				}
			}
		}
		return undeployEPR;
	}
	public String getDeployEPR() {
		
		if(deployEPR == null){
			if(ip != null && port != null){
				if(ip.startsWith("http")){
					deployEPR = ip + ":" + port + suffix;
				}else{
					deployEPR = "http://" + ip + ":" + port + suffix;
				}
			}
		}
		return deployEPR;
	}
	//////////////////////////////////
	
	
	/**
	 * judge whether the appliances are the same one
	 *  
	 * @param description
	 * @return
	 */
	public boolean equals(ApplianceDescription description){
		return description.getId().equals(id);
		
	}
}
