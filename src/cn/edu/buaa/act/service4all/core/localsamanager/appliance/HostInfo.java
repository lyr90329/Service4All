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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.edu.buaa.act.service4all.core.localsamanager.app.Application;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class HostInfo{
	private String ip = Constants.LOCAL_IP_WITH_HTTP;
	private String addrEPR = Constants.LOCAL_IP_WITH_HTTP + ":" +Constants.LOCAL_SM_PORT + Constants.LOCAL_SM_SUFFIX;
	private String deployOp = "saDeploy";
	private String undeployOp = "saUnDeploy";
	private String restartOp = "restart";
	private String stopOp="";
	
	private float cpu;
	private float memory;
	private int throughput;
	
	private static HostInfo host;
	private Map<Integer, List<Application>> deployeds;
	private List<SoftwareAppliance> appliances;
	
	private HostInfo(){
		appliances = new ArrayList<SoftwareAppliance>();
		deployeds = new HashMap<Integer, List<Application>>();
	}
	
	public synchronized static HostInfo getInstance(){
		if(host == null){
			host = new HostInfo();
		}
		return host;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getAddrEPR() {
		return addrEPR;
	}
	public void setAddrEPR(String addrEPR) {
		this.addrEPR = addrEPR;
	}
	public String getDeployOp() {
		return deployOp;
	}
	public void setDeployOp(String deployOp) {
		this.deployOp = deployOp;
	}
	public String getUndeployOp() {
		return undeployOp;
	}
	public void setUndeployOp(String undeployOp) {
		this.undeployOp = undeployOp;
	}
	public String getRestartOp() {
		return restartOp;
	}
	public void setRestartOp(String restartOp) {
		this.restartOp = restartOp;
	}
	

	public String getStopOp() {
		return stopOp;
	}

	public void setStopOp(String stopOp) {
		this.stopOp = stopOp;
	}

	public float getCpu() {
		return cpu;
	}

	public void setCpu(float cpu) {
		this.cpu = cpu;
	}

	public float getMemory() {
		return memory;
	}

	public void setMemory(float memory) {
		this.memory = memory;
	}

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}
	

	public synchronized List<SoftwareAppliance> getAppliances(){
		return appliances;
	}
	
	public synchronized void addAppliance(SoftwareAppliance appliance){
		appliances.add(appliance);
	}
	public synchronized void deleteAppliance(SoftwareAppliance appliance){
		appliances.remove(appliance);
	}
	public synchronized void deleteApplianceByPort(String port){
		for(int i = 0; i < appliances.size();i++){			
			if(port.equals(String.valueOf(appliances.get(i).getPort()))){
				appliances.remove(i);
			}
		}
	}
	public synchronized void addApp(Application application,Integer port){
		List<Application> list = deployeds.get(port);
		if(list == null){
			list = new ArrayList<Application>();
		}
		list.add(application);
		deployeds.put(port, list);
	}
	public synchronized void deleteApp(Integer port,String serviceID){
		List<Application> list = deployeds.get(port);
		if(list != null){
			for(int i = 0 ;i < list.size();i++){
				if(list.get(i).getServiceID().equals(serviceID)){
					list.remove(i);
				}
			}
		}
	}
	public synchronized List<Application> getApps(int port){
		return deployeds.get(port);
	}
	
//	public void print(){
//		logger.info("************Application***************");
//		Set<Integer> keys = deployeds.keySet();
//		for(Iterator<Integer> it = keys.iterator(); it.hasNext();){
//			List<Application> list = deployeds.get( it.next() );
//			for(int i = 0 ; i < list.size();i++){
//				logger.info(list.get(i).getName());
//			}
//		}
//	}
	
}
