package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.bpmnengine.QueryTask;
import cn.edu.buaa.act.service4all.core.samanager.IDCounter;
import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppQueryForExecutionBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class BPMNQueryForExecutionBusinessUnit extends
		AppQueryForExecutionBusinessUnit {
	
	private Log logger = LogFactory.getLog(BPMNQueryForExecutionBusinessUnit.class);
	
	public final static String JOB_ID = "jobId";
	public final static String TASK = "task";
	public final static String DEPLOY_NUM = "deploymentNum";
	public final static String IS_SUCCESS = "isSuccessful";
	public final static String NEW_DEPLOYEDS = "newDeployed";
	public final static String DEPLOY_EXCEP = "deploymentException";
	
	private final int MAX_LOAD = 10;
	
	public final static Map<String, QueryTask> tasks = new HashMap<String, QueryTask>();
	
	public void dispatch(ExchangeContext context){
		
		
		//need to get the serviceID, jobID, deploymentNum
		String serviceId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.SERVICE_ID);
		String jobId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.JOB_ID);
		String deploymentNum = (String)context.getData(BPMNQueryForExecutionBusinessUnit.DEPLOY_NUM);
		String deploymentType = (String)context.getData(BPMNQueryForExecutionBusinessUnit.QUERY_TYPE);
		
		logger.info("Receiving the bpmn query for executiong: " + jobId + " with the service : " + serviceId);
		
		ApplianceManager bpmnManager = this.getApplianceManager();
		List<Appliance> bpmnengines = bpmnManager.getAllAppliances();
	
		if(bpmnengines == null){
			logger.info("There is no BPMN Engine so need to start one!");
			
		}
		
//		//generate new id for the job
//		jobId = String.valueOf(IDCounter.generateJobID());
//		context.storeData(BPMNQueryForExecutionBusinessUnit.JOB_ID, jobId);
		
		Appliance selected = selectBPMNEngine(bpmnengines);
		if(selected == null){
			QueryTask task = new QueryTask();
			task.setJobId(jobId);
			task.setServiceId(serviceId);
			task.setDeploymentNum(deploymentNum);
			task.setDeployType(deploymentType);
			
			tasks.put(jobId, task);
			context.storeData(TASK, task);
			
			Invoker invoker = this.getInvokers().get("BPMNEngineCreationInvoker");
			if(invoker == null){
				logger.warn("The invoker of BPMNEngineCreation is null");
				this.getReceiver().sendExceptionMessage("The invoker of BPMNEngineCreation is null", context);
				return;
			}
			
			try {
				invoker.sendRequestExchange(context);
			} catch (MessageExchangeInvocationException e) {
				// TODO Auto-generated catch block
				this.handleInvocationException(e);
			}
		}else{
			logger.info("Select one bpmn engine for execution: " + selected.getDesp().getId() + " with its load: " + selected.getStatus().getReqLoad());
			List<Appliance> es = new ArrayList<Appliance>();
			es.add(selected);
			context.storeData(NEW_DEPLOYEDS, es);
			
			try {
				this.getReceiver().sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				// TODO Auto-generated catch block
				this.handleInvocationException(e);
			}
		}
	}
	
	/**
	 * selected one fit bpmn engines 
	 * if no one fit return null
	 * 
	 * @param appliances
	 * @return
	 */
	private Appliance selectBPMNEngine(List<Appliance> appliances){
		for(Appliance a : appliances){
			if(a.getStatus().getReqLoad() < MAX_LOAD){
				return a;
			}
		}
		return null;
	}
	
	public void onReceiveResponse(ExchangeContext context){
		
		
		String jobId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.JOB_ID);
		String serviceId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.SERVICE_ID);
		String queryType = (String)context.getData(BPMNQueryForExecutionBusinessUnit.QUERY_TYPE);
		String isSuccessful = (String)context.getData(BPMNQueryForExecutionBusinessUnit.IS_SUCCESS);
		List<Appliance> newDeployeds = (List<Appliance>)context.getData(BPMNQueryForExecutionBusinessUnit.NEW_DEPLOYEDS);
		
		logger.info("Receive the BPMNEngineCreationInvoker response for bpmnEngine deployment");
		//update the new Deployed appliance into the ApplianceManager
		
		try {
			this.getReceiver().sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't send the response message from BPMNQueryForExecutionBusinessUnit: " + e.getMessage());
			this.handleInvocationException(e);
		}
	}
}
