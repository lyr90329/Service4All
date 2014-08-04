package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.Date;

public class ExecuteResultIndex 
{
	private Date startTime,endTime;
	
	public ExecuteResultIndex()
	{
		
	}
	
	public Date getStartTime() 
	{
		return startTime;
	}

	public void setStartTime(Date startTime) 
	{
		this.startTime = startTime;
	}

	public Date getEndTime() 
	{
		return endTime;
	}

	public void setEndTime(Date endTime) 
	{
		this.endTime = endTime;
	}
}
