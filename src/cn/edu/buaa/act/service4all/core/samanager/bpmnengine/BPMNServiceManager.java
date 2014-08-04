package cn.edu.buaa.act.service4all.core.samanager.bpmnengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.samanager.IDCounter;
import cn.edu.buaa.act.service4all.core.samanager.app.AppManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.App;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppRepetition;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppInvocationEvent;

public class BPMNServiceManager extends AppManager {
	
	private final Log logger = LogFactory.getLog(BPMNServiceManager.class);
	
	@Override
	protected synchronized String generateServiceId(String appName) {
		// TODO Auto-generated method stub
		
		return "BPMN_" + String.valueOf(IDCounter.generateJobID());
	}

	@Override
	public void queryApplianceForDeployment(AppDeploymentEvent event)
			throws AppException {
		// TODO Auto-generated method stub
		String serviceName = event.getDeployedServiceName();
		String serviceId = generateServiceId(serviceName);
		
		logger.info("Query the appliances for deployment: " + serviceId);
		
		//query the available appliances
//		List<Appliance> results = applianceManager.queryAvailableAppliances();
//		event.setQueryResults(results);
		
		//create a new App instance
		App targetApp = new App();
		targetApp.setId(serviceId);
		targetApp.setName(serviceName);
		
		//adding to the toBeDeployed List
		toBeDeployed.put(serviceId, targetApp);
		event.setDeployedServiceId(serviceId);
	}
	
	@Override
	public void queryAppRepetitionForExecution(AppInvocationEvent event)
			throws AppException {
		// TODO Auto-generated method stub
		String serviceID = event.getTargetServiceID();
		logger.info("Query the app repetition for execution: " + serviceID);
		
//		App app = apps.get(serviceID);
//		if(app == null){
//			logger.warn("The target service is not deployed: " + serviceID);
//			throw new AppException("The target service is not deployed: " + serviceID);
//		}
		List<AppReplica> rs = new ArrayList<AppReplica>();
		event.setRepetitions(rs);
		
		//It's possible to change the app's status but now just do nothing
	}
	
}
