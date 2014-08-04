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
package cn.edu.buaa.act.service4all.core.samanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jbi.JBIException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import cn.edu.buaa.act.service4all.core.component.AppEngineComponent;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.appserver.AppServerManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.appserver.WebAppManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.axis2.Axis2Manager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.axis2.WebServiceManager;
import cn.edu.buaa.act.service4all.core.samanager.bpmnengine.BPMNEngineManager;
import cn.edu.buaa.act.service4all.core.samanager.bpmnengine.BPMNServiceManager;

/**
 * 
 * @author Huangyj
 *
 */
public class SAManagerComponent extends AppEngineComponent{
	
	public final static String APPLIANCE_HOST = "host";
	public final static String APPLIANCE_AXIS2 = "axis2";
	public final static String APPLIANCE_APPSERVER = "appserver";
	public final static String APPLIANCE_BPMNENGINE= "bpmnengine";

	public final static String APP_WS = "webservice";
	public final static String APP_APP = "app";
	public final static String APP_BPMN= "bpmn";
	
	private final long SCAN_INTERNAL = 60000;//scan_internal = 60000;
	//private final long max_internal = 80000000;
	
	/**
	 * type->ApplianceManager
	 */
	private Map<String, ApplianceManager> applianceManagers = new HashMap<String, ApplianceManager>();
	/**
	 * application type -> ApplicationManager
	 */
	private Map<String, AppManager> appManagers = new HashMap<String, AppManager>();
	
	private Scheduler scheduler;
	
	public static Object lock = new Object();
	
//	public void init(ComponentContext context)throws JBIException{
//		super.init(context);
//	}
	
	public void initContextManager(){
		
		initApplianceManagers();
		initAppManagers();
		super.initContextManager();
		try {
			IDCounter.init();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	protected void createNewAppEngineContextManager(){
		logger.info("Initiate the SA Manager Context Manager");
		manager = new SAManagerContextManager();
		
	}
	
	public void stop() throws JBIException{
		super.stop();
		try {
			IDCounter.close();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void start() throws JBIException{
		super.start();
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			
			JobDetail jobDetail = new JobDetail("scanJob", 
									Scheduler.DEFAULT_GROUP, 
									SchedulerJob.class);
			Map map = jobDetail.getJobDataMap();
			map.put("applianceManagers", applianceManagers);
			map.put("appManagers", appManagers);
			
			Trigger trigger = new SimpleTrigger("scanTrigger", 
									Scheduler.DEFAULT_GROUP, new Date(), null, 
									SimpleTrigger.REPEAT_INDEFINITELY, SCAN_INTERNAL);
		
			scheduler.scheduleJob(jobDetail, trigger);
			
			
			//write the BU's log into the file
			writeBULog();
			
		} catch (SchedulerException e) {
			logger.warn("Can't start the Scheduler", e);
		}
	}
	
	private void writeBULog(){
		File buLog = new File("BuLog.log");
		try {
			if(!buLog.exists()){
				buLog.createNewFile();
			}
			
			FileWriter writer = new FileWriter(buLog);
			Iterator<BusinessUnit> bus = this.manager.getAllUnits().iterator();
			while(bus.hasNext()){
				BusinessUnit b = bus.next();
				writer.write(b.toString());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void initApplianceManagers(){
		logger.info("Initiate the appliance managers!");
		HostManager host = new HostManager();
		applianceManagers.put(APPLIANCE_HOST, host);
		
		Axis2Manager axis2 = new Axis2Manager();
		applianceManagers.put(APPLIANCE_AXIS2, axis2);
		
		
		AppServerManager appServerManager = new AppServerManager();
		applianceManagers.put(APPLIANCE_APPSERVER, appServerManager);
		
		BPMNEngineManager bpmnEngineManager = new BPMNEngineManager();
		applianceManagers.put(APPLIANCE_BPMNENGINE, bpmnEngineManager);
		
		host.addManager(APPLIANCE_AXIS2, axis2);
		host.addManager(APPLIANCE_APPSERVER, appServerManager);
		host.addManager(APPLIANCE_BPMNENGINE, bpmnEngineManager);
		
	}
	
	protected void initAppManagers(){
		logger.info("Initiate the app managers!");
		WebServiceManager wsManager = new WebServiceManager();
		appManagers.put(APP_WS, wsManager);
		
		WebAppManager webAppManager = new WebAppManager();
		appManagers.put(APP_APP, webAppManager);
		
		BPMNServiceManager bpmnManager = new BPMNServiceManager();
		appManagers.put(APP_BPMN, bpmnManager);
		
	}
	
	public Map<String, ApplianceManager> getApplianceManagers(){
		return applianceManagers;
	}
	
	public Map<String, AppManager> getAppManagers(){
		return appManagers;
	}
	
	public ApplianceManager getApplianceManager(String name){
		return applianceManagers.get(name);
	}
	public AppManager getAppManager(String name){
		return this.appManagers.get(name);
	}
	
}
