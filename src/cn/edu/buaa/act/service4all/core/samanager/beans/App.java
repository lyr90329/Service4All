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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The description class for Application
 * 
 * @author huanghe
 *
 */
public class App {
	
	protected Log logger = LogFactory.getLog(App.class);
	
	/**
	 * the id is generated when App is deployed
	 */
	protected String id;
	protected String name;
	protected List<AppReplica> backups = new ArrayList<AppReplica>();
	
	public String getId() {
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AppReplica> getBackups() 
	{
		return backups;
	}
	public void setBackups(List<AppReplica> backups) {
		this.backups = backups;
	}
	public void addBackup(AppReplica rep){
		synchronized(backups){
			boolean has = false;
			for(AppReplica r : backups){
				if(r.getContainerId().equalsIgnoreCase(rep.getContainerId())){
					has = true;
					break;
				}
			}
			if(!has){
				this.backups.add(rep);				
			}
		}
	}
	
	public void addBackups(List<AppReplica> backups)
	{
		this.backups.addAll(backups);
	}
	
	public void removeBackupByAppliance(String applianceId){
		synchronized(backups)
		{			
			List<AppReplica> removes=new LinkedList<AppReplica>();
			for(AppReplica rep : backups){
				if(rep.getContainerId().equals(applianceId))
				{					
					logger.info("Removing a repetition : " + applianceId);
					removes.add(rep);
				}
			}
			
			for(AppReplica rep:removes)
			{
				backups.remove(rep);
			}
			
			removes=null;
		}
		
	}
	
	public App clone(){
		App c = new App();
		
		return c;
	}
}
