package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.HashMap;
import java.util.Map;


public class TaskExecuteResultManager 
{
	private Map<String,TaskExecuteResult> finished,unfinished;	
	
	public TaskExecuteResultManager()
	{
		finished=new HashMap<String,TaskExecuteResult>();
		unfinished=new HashMap<String,TaskExecuteResult>();		
	}
	
	public boolean startJob(String jobid,TaskExecuteResult result)
	{
		TaskExecuteResult r1=null;
		
		r1=unfinished.get(jobid);
		if(r1!=null)
			return false;
		unfinished.put(jobid, result);
		return true;
	}
	
	public boolean finishJob(String jobid,TaskExecuteResult result)
	{
		TaskExecuteResult r1=null;
		
		r1=unfinished.get(jobid);
		if(r1==null)
			return false;
		unfinished.remove(jobid);
		result.setStartTime(r1.getStartTime());
		finished.put(jobid, result);
		return true;
	}
}
