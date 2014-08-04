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
 * The status information for the appliances
 * the status information including the Apps deployed in the appliance
 * 
 * The appliance status includes some performance indexes from polling and running event
 * 
 * @author Huangyj
 *
 */
public class ApplianceStatus {
	
	/**
	 * the appliance has been deleted, however still in the cache
	 */
	public final static int APPLIANCE_DELETED = 1;
	public final static int APPLIANCE_STARTED = 2;
	
	/**
	 * the appliance's state hasn't been updated recently for a specified period
	 */
	public final static int APPLIANCE_EXPIRED = 3;
	public final static int APPLIANCE_ERROR = 4;
	public final static int APPLIANCE_WORKING = 5;
	
	protected Appliance appliance;
	protected int status;
//	protected List<AppRepetition> apps = new ArrayList<AppRepetition>();
	protected Date updateDate;
	
	protected float cpuRate;
	protected float memoryfloat;
	protected double port;
	protected int deployedAmount = 0;
	
	protected long reqLoad = 0;
	
	
//	public void deleteAppRepetition(String appId){
//		for(int i = 0; i < apps.size(); i++){
//			AppRepetition rep = apps.get(i);
//			if(rep.getDesription().getId().equalsIgnoreCase(appId)){
//				logger.info("Remove a repetition from the ApplianceStatus!");
//				
//				apps.remove(rep);
//				return;
//			}
//		}
//	}
	
	public Appliance getAppliance() {
		return appliance;
	}
	public void setAppliance(Appliance appliance) {
		this.appliance = appliance;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public synchronized void increateDeployedApps(){
		deployedAmount++;
	}
	
//	public List<AppRepetition> getApps() {
//		return apps;
//	}
//	public void setApps(List<AppRepetition> apps) {
//		this.apps = apps;
//	}
//	public void addApp(AppRepetition app){
//		this.apps.add(app);
//	}
//	public void addApps(List<AppRepetition> apps){
//		this.apps.addAll(apps);
//	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public float getCpuRate() {
		return cpuRate;
	}
	public void setCpuRate(float cpuRate) {
		this.cpuRate = cpuRate;
	}
	public float getMemoryfloat() {
		return memoryfloat;
	}
	public void setMemoryfloat(float memoryfloat) {
		this.memoryfloat = memoryfloat;
	}
	public double getPort() {
		return port;
	}
	public void setPort(double port) {
		this.port = port;
	}
	public int getDeployedAmount() {
		return deployedAmount;
	}
	public void setDeployedAmount(int deployedAmount) {
		this.deployedAmount = deployedAmount;
	}
	public long getReqLoad() {
		synchronized(this){
			return reqLoad;
		}
	}
	public void setReqLoad(long reqLoad) {
		synchronized(this){
			this.reqLoad = reqLoad;
		}
	}
	
	public void increaseReq(){
		synchronized(this){
			this.reqLoad++;
		}
	}
	public void decreaseReq(){
		synchronized(this){
			this.reqLoad--;
		}
		
	}
	
	
}
