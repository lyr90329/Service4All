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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Host extends Appliance {
	Log logger = LogFactory.getLog(Host.class);
	//protected List<Appliance> childAppliances = new ArrayList<Appliance>();
	
	/**
	 * String represents the type of the appliance
	 */
	protected Map<String, List<Appliance>> children = new HashMap<String, List<Appliance>>();
	
	public Host(){
		
	}
	public Host(ApplianceStatus status, ApplianceDescription desp,
			ApplianceRecords records) {
		
	}
	public Map<String, List<Appliance>> getChildAppliances() {
		return children;
	}
	public void setChildAppliances(String type, List<Appliance> childAppliances) {
		children.put(type, childAppliances);
	}
	
	public void addChildAppliance(String type, Appliance appliance){
		if(children.get(type) == null){
			children.put(type, new ArrayList<Appliance>());
		}
		String applianceId = appliance.getDesp().getId();
		List<Appliance> cs = children.get(type);
		boolean found = false;
		for(Appliance c : cs){
			if(c.getDesp().getId().equalsIgnoreCase(applianceId)){
				//get the id from the appliance
				found = true;
			}
		}
		if(!found){
			cs.add(appliance);
			this.getStatus().increateDeployedApps();
		}
		
		
	}
	
	public void setChildrenAppliances(Map<String, List<Appliance>> c){
		this.children = c;
	}
}
