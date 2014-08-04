package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.LinkedList;
import java.util.Queue;

public class TaskMonitorInfo 
{
	private String serviceId,serviceName;
	private Queue<MonitorRecord> monitorRecords;
	
	public TaskMonitorInfo(String serviceId,String serviceName)
	{
		this.serviceId=serviceId;
		this.serviceName=serviceName;
		monitorRecords=new LinkedList<MonitorRecord>();
	}
	
	public void addMonitorRecord(MonitorRecord record)
	{		
		((LinkedList<MonitorRecord>) monitorRecords).addFirst(record);
	}
	
	public Queue<MonitorRecord> getMonitorRecords()
	{
		return monitorRecords;
	}
}
