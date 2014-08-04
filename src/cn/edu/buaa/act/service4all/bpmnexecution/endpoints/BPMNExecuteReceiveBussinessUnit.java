package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.bpmnexecution.BPMNEngineInfo;
import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;
import cn.edu.buaa.act.service4all.bpmnexecution.execution.BPMNEngineClient;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class BPMNExecuteReceiveBussinessUnit extends BPMNExecuteBusinessUnit 
{
	
	private final Log logger = LogFactory.getLog(BPMNExecuteReceiveBussinessUnit.class);
	
	
	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);	
		
	}

	@Override
	public void dispatch(ExchangeContext context)
	{
		// TODO Auto-generated method stub
		Job job;
		job=(Job) context.getData(Constants.job);
		
		
		this.getTaskManager().createJob(job);
		
		Invoker invoker = this.getInvokers().get(Constants.GetAvailableBPMNEngineInvoker);
		try 
		{
			invoker.sendRequestExchange(context);
		}
		catch (MessageExchangeInvocationException e) 
		{
			// TODO Auto-generated catch block
			logger.warn("Can't send the message to get available BPMN", e);
		}		
	}
	
	@Override
	public void onReceiveResponse(ExchangeContext context) 
	{
		// TODO Auto-generated method stub
		Document doc;
		Job job;
		Element root,service;
		String jobid, num;
		NodeList services;
		
		doc=(Document) context.getData(Constants.availableServiceResponse);
		root=doc.getDocumentElement();
		
		//serviceid=root.getElementsByTagName(Constants.serviceID).item(0).getTextContent();
		//requestid=root.getElementsByTagName(Constants.requestID).item(0).getTextContent();
		
//		logger.info("----------------------");
//		NodeList list = root.getElementsByTagName(Constants.jobId);
//		jobid = "";
//		logger.info(list.getLength());
//		for(int i=0;i<list.getLength();i++){
//			logger.info(list.item(i));
//			jobid = list.item(i).getTextContent();
//			logger.info("jobid="+jobid);
//			if(jobid!=null && !jobid.trim().equals("")){
//				break;
//			}
//		}
//		logger.info("----------------------");
		
		jobid = root.getElementsByTagName(Constants.jobId).item(0).getTextContent();
		//num=root.getElementsByTagName(Constants.num).item(0).getTextContent();
		//String serviceID = root.getElementsByTagName("serviceID").item(0).getTextContent();
		
		//////////////////////
		logger.info("Get the job by its id: " + jobid);
		job = this.getTaskManager().getJobByJobID(jobid);
		////////////////////////////
		job.setJobID(jobid);
		
		this.getTaskManager().ReadyJob(job);
		
		
		services=root.getElementsByTagName(Constants.service);
		if(services == null){
			logger.info("There are no available BPMNEngine!");
		
		}else{
			List<BPMNEngineInfo> engines = new ArrayList<BPMNEngineInfo>();
			for(int i = 0; i < services.getLength(); i++){
				
				Element ser = (Element)services.item(i);
				String engineId = ser.getAttributeNode(Constants.targetEngine).getValue();
				String invokeUrl = ser.getTextContent();
				
				BPMNEngineInfo engine = new BPMNEngineInfo();
				engine.setEngineID(engineId);
				engine.setInvokeUrl(invokeUrl);
				engine.setOperation(ser.getAttribute("operation"));
				engines.add(engine);
				
			}
			
			logger.info("Get " + engines.size() + "BPMNEngines for execution!");
			job.setEngines(engines);
		}
		
		
		context.storeData(Constants.job, job);
		
		Invoker invoker = this.getInvokers().get("GetBPMNInvoker");
		try 
		{
			invoker.sendRequestExchange(context);
		}
		catch (MessageExchangeInvocationException e) 
		{
			// TODO Auto-generated catch block
			logger.warn("Can't send the message to get available BPMN", e);
		}
	}
	
	public void onReceiveQueryResponse(ExchangeContext context){
		String jobId = (String)context.getData("jobId");
		//String serviceName = (String)context.getData(Constants.serviceName);
		Document bpmn = (Document)context.getData("bpmn");
		
		logger.info("Get the job after getting the bpmn document : " + jobId);
		Job job = this.getTaskManager().getJobByJobID(jobId);
		
		if(job == null){
			try {
				context.storeData("exception", "Can't get the job after querying the bpmn document");
				this.getReceiver().sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				// TODO Auto-generated catch block
				logger.warn("Can't get the job after querying the bpmn document : " + e.getMessage());
				
			}
			return;
		}
		job.setBpmn(bpmn);
		
//		List<BPMNEngineInfo> engines = new ArrayList<BPMNEngineInfo>();
		List<BPMNEngineInfo> engines = job.getEngines();
		
		BPMNEngineInfo engine = new BPMNEngineInfo();
		
//		engine.setInvokeUrl("http://192.168.3.204:8080/axis2/services/BpmnEngineService");
//		
//		//��operationд����������
//		engine.setOperation("executeBpmnFlow");
//		engine.setEngineID("bpmnengine_http://192.168.3.204:8080");
//		
//		engines.add(engine);
		
//		job.setEngines(engines);
		
		BPMNEngineClient client = new BPMNEngineClient();
		
		try 
		{
			BPMNEngineInfo selected = client.invokeService(job);
			
			context.storeData(Constants.job, job);
			context.storeData(Constants.selected_engine, selected);
			
			// invoke the StartBPMNFeedbackInvoker to response the engine's status
			Invoker inv = this.getInvokers().get("StartBPMNFeedbackInvoker");
			if(inv == null){
				logger.warn("There is no bpmn execution feedback invoker!");
				this.getReceiver().sendResponseMessage(context);
				
			}else{	
				// send the feedback message to samanager component
				inv.sendRequestExchange(context);
			}
			
			//this.getReceiver().sendResponseMessage(context);
		} 
		catch (MessageExchangeInvocationException e) 
		{
			// TODO Auto-generated catch block
			this.handleInvocationException(e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			this.handleInvocationException(new MessageExchangeInvocationException(e.getMessage()));
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			this.handleInvocationException(new MessageExchangeInvocationException(e.getMessage()));
		}
		
		logger.info("Starting the job by send the request to BPMNEngine!");
		this.getTaskManager().startJob(job);
		
	}
	
}
