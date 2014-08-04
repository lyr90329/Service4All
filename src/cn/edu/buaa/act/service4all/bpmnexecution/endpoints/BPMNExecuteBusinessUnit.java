package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.transaction.exception.AppEngineException;

import cn.edu.buaa.act.service4all.bpmnexecution.TaskManager;


public abstract class BPMNExecuteBusinessUnit extends BusinessUnit
{
	private static TaskManager taskmanager=null;
	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);
		if(taskmanager==null)
			taskmanager=new TaskManager();		
	}
	
	protected TaskManager getTaskManager()
	{
		return taskmanager;
	}
}
