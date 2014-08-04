package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class TaskExecuteResult 
{
	public final static int completed=1;
	public final static int error=2;
	
	private String serviceId,jobId,serviceName,description;
	private List<Parameter> parameters;
	private ExecuteResultIndex resultIndex;	
	private int state;
	
	public TaskExecuteResult()
	{
		parameters=new LinkedList();
		resultIndex=new ExecuteResultIndex();
	}	
	
	public void setState(int state)
	{
		this.state=state;		
	}
	
	public int getState()
	{
		return state;
	}
	
	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}	
	
	public void setStartTime(Date date)
	{
		this.resultIndex.setStartTime(date);		
	}
	
	public void setEndTime(Date date)
	{
		this.resultIndex.setEndTime(date);
	}
	
	public Date getStartTime()
	{
		return this.resultIndex.getStartTime();
	}
	
	public Date getEndTime()
	{
		return this.resultIndex.getEndTime();
	}
	
	public String getServiceId() 
	{
		return serviceId;
	}
	
	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}
	
	public String getJobId() 
	{
		return jobId;
	}
	
	public void setJobId(String jobId) 
	{
		this.jobId = jobId;
	}
	
	public String getServiceName() 
	{
		return serviceName;
	}
	
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	
	public List<Parameter> getParameters() 
	{
		return parameters;
	}
	public void addParameter(Parameter parameter) 
	{
		parameters.add(parameter);		
	}
	
	public static Date getCurrentDate()
	{
		Calendar cal=Calendar.getInstance();
		Date date=cal.getTime();
		return date;
	}
}