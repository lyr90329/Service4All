package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class Job 
{
	//job status
	public final static int Deployed=1;
	public final static int Ready=2;
	public final static int Started=3;
	public final static int Exception=4;
	public final static int Error=5;
	public final static int Completed=6;
	public final static int Deleted=7;
	
	private String JobID,ServiceID,counter,messageFlag;
	private int JobStatus;
	private String TargetBPMNService;
	private String TargetBPMNEngine;
	private String ExecuteResult;
	private String serviceName;
	
	private List<BPMNEngineInfo> engines;
	private Document bpmn;
	
	private Node parameters;
	
	private String monitorIp;
	
	
	
	public Document getBpmn() {
		return bpmn;
	}

	public void setBpmn(Document bpmn) {
		this.bpmn = bpmn;
	}

	public List<BPMNEngineInfo> getEngines() {
		return engines;
	}

	public void setEngines(List<BPMNEngineInfo> engines) {
		this.engines = engines;
	}

	public Job()
	{
		
		JobID=null;
		ServiceID=null;
		
		counter=null;
		messageFlag=null;
	}
	
	public String getJobID() {
		return JobID;
	}

	public void setJobID(String jobID) {
		JobID = jobID;
	}

	public Node getParameters() {
		return parameters;
	}

	public void setParameters(Node parameters) {
		this.parameters = parameters;
	}

	public void setServiceName(String name)
	{
		this.serviceName=name;
	}
	
	public String getServiceName()
	{
		return this.serviceName;
	}
	
//	public void addParameter(Parameter parameter)
//	{
//		parameters.add(parameter);
//	}
	
	public String getCounter()
	{
		return counter;
	}
	
	public void setCounter(String counter)
	{
		this.counter=counter;
	}
	
	public String getMessageFlag()
	{
		return messageFlag;
	}
	
	public void setMessageFlag(String messageflag)
	{
		this.messageFlag=messageflag;
	}
	
	public String getServiceID()
	{
		return ServiceID;
	}
	
	public void setServiceID(String serviceId)
	{
		this.ServiceID=serviceId;
	}
	
	public int getJobStatus()
	{
		return JobStatus;
	}
	
	public void setJobStatus(int jobStatus) 
	{
		JobStatus = jobStatus;
	}
	
	public String getTargetBPMNService() 
	{
		return TargetBPMNService;
	}
	
	public void setTargetBPMNService(String targetBPMNService)
	{
		TargetBPMNService = targetBPMNService;
	}
	
	public String getTargetBPMNEngine() 
	{
		return TargetBPMNEngine;
	}
	
	public void setTargetBPMNEngine(String targetBPMNEngine) 
	{
		TargetBPMNEngine = targetBPMNEngine;
	}
	
	public String getExecuteResult() 
	{
		return ExecuteResult;
	}
	
	public void setExecuteResult(String executeResult)
	{
		ExecuteResult = executeResult;
	}

	public String getMonitorIp() {
		return monitorIp;
	}

	public void setMonitorIp(String monitorIp) {
		this.monitorIp = monitorIp;
	}	
	
	
}