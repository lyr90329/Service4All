package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MonitorRecord 
{
	public final static int Exception=0;
	public final static int Successful=1;
	
	private String jobId,nodeId,statusDesp;
	private List<Parameter> parameters = new ArrayList<Parameter>();
    private Date timestamp;
    private int nodeStatus;
    
    private boolean isResult;
    private String serviceName;
    
	private boolean isSuccessful;
    
   
	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public boolean isResult() {
		return isResult;
	}

	public void setResult(boolean isResult) {
		this.isResult = isResult;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public MonitorRecord()
	{
		parameters=new LinkedList();
	}
	
	public void addParameter(Parameter parameter)
	{
		parameters.add(parameter);		
	}
	
	
	
	public String getJobId() 
	{
		return jobId;
	}
	
	public void setJobId(String jobId)
	{
		this.jobId = jobId;
	}
	
	public String getNodeId() 
	{
		return nodeId;
	}
	
	public void setNodeId(String nodeId) 
	{
		this.nodeId = nodeId;
	}
	
	public String getStatusDesp()
	{
		return statusDesp;
	}
	
	public void setStatusDesp(String statusDesp) 
	{
		this.statusDesp = statusDesp;
	}
	
	public Date getTimestamp() 
	{
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) 
	{
		this.timestamp = timestamp;
	}
	
	public int getNodeStatus()
	{
		return nodeStatus;
	}
	
	public void setNodeStatus(int nodeStatus)
	{
		this.nodeStatus = nodeStatus;
	}
}
