package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.HashMap;
import java.util.Map;

public class TaskMonitorInfoManager 
{
	private Map<String, TaskMonitorInfo> monitorInfos;
	
	public TaskMonitorInfoManager()
	{
		monitorInfos=new HashMap<String,TaskMonitorInfo>();		
	}
	
	public void addTaskMonitorInfo(Job job,MonitorRecord record)
	{
		TaskMonitorInfo info=null;
		
		info=monitorInfos.get(job.getJobID());
		if(info==null)
		{
			info=new TaskMonitorInfo(job.getServiceID(),job.getServiceName());
		    monitorInfos.put(job.getJobID(), info);
		}
		info.addMonitorRecord(record);		
	}
	
	public TaskMonitorInfo getMonitorInfo(String jobid)
	{
		return monitorInfos.get(jobid);
	}
}
