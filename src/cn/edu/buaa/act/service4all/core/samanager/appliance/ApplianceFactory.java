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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2Server;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.BPMNEngine;
import cn.edu.buaa.act.service4all.core.samanager.beans.BPMNEngineDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.BPMNEngineRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.BPMNEngineStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServer;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerStatus;

public class ApplianceFactory {
	
	private final static Log logger = LogFactory.getLog(ApplianceFactory.class);
	
	/**
	 * generate an instance of Appliance , with desp status record not null
	 * @param applianceType
	 * @return
	 */
	public static Appliance createAppliance(String applianceType){
		
		if(applianceType.equals("axis2")){
			
			Axis2Server axis2 = new Axis2Server();
			Axis2ServerDescription desp = new Axis2ServerDescription();
			Axis2ServerRecords record = new Axis2ServerRecords();
			Axis2ServerStatus status = new Axis2ServerStatus();
			axis2.setDesp(desp);
			axis2.setRecords(record);
			axis2.setStatus(status);
			
			return axis2;
		}
		
		if(applianceType.equals("appserver")){
			
			TomcatServer server = new TomcatServer();
			TomcatServerDescription desp = new TomcatServerDescription();
			TomcatServerRecords record = new TomcatServerRecords();
			TomcatServerStatus status = new TomcatServerStatus();
			
			server.setDesp(desp);
			server.setRecords(record);
			server.setStatus(status);
			
			return server;
		}
		
		if(applianceType.equals("bpmnengine")){
			BPMNEngine engine = new BPMNEngine();
			BPMNEngineDescription desp = new BPMNEngineDescription();
			BPMNEngineRecords record = new BPMNEngineRecords();
			BPMNEngineStatus status = new BPMNEngineStatus();
			engine.setDesp(desp);
			engine.setRecords(record);
			engine.setStatus(status);
			
			return engine;
		}

		logger.info("The applianc type is not supported for creation : " + applianceType);
		return null;
		
		
	}
}
