package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.LinkedList;
import java.util.Queue;

public class TaskStatusManager 
{
	private Queue<Job> deployeds,readys,starteds,exceptions,errors,completeds,deleteds;
	
	public TaskStatusManager()
	{
		deployeds=new LinkedList<Job>();
		readys=new LinkedList<Job>();
		starteds=new LinkedList<Job>();
		exceptions=new LinkedList<Job>();
		errors=new LinkedList<Job>();
		completeds=new LinkedList<Job>();
		deleteds=new LinkedList<Job>();		
	}
	
	public boolean addDeployedJob(Job job)
	{
		return deployeds.add(job);
	}
	
	public boolean removeDeployJob(Job job)
	{
		return deployeds.remove(job);
	}
	
	public Queue<Job> getDeployedList()
	{
		return deployeds;
	}
	
	public boolean addReadyJob(Job job)
	{
		return readys.add(job);
	}
	
	public boolean removeReadyJob(Job job)
	{
		return readys.remove(job);
	}
	
	public Queue<Job> getReadyList()
	{
		return readys;
	}
	
	public boolean addStartedJob(Job job)
	{
		return starteds.add(job);		
	}
	
	public boolean removeStartedJob(Job job)
	{
		return starteds.remove(job);		
	}
	
	public Queue<Job> getStartedList()
	{
		return starteds;
	}
	
	public boolean addExceptiondJob(Job job)
	{
		return exceptions.add(job);		
	}
	
	public boolean removeExceptiondJob(Job job)
	{
		return exceptions.remove(job);		
	}
	
	public Queue<Job> getExceptionList()
	{
		return exceptions;
	}
	
	public boolean addErrorJob(Job job)
	{
		return errors.add(job);		
	}
	
	public boolean removeErrorJob(Job job)
	{
		return errors.remove(job);		
	}
	
	public Queue<Job> getErrorList()
	{
		return errors;
	}
	
	public boolean addCompleteJob(Job job)
	{
		return completeds.add(job);
	}
	
	public boolean removeCompleteJob(Job job)
	{
		return completeds.remove(job);
	}
	
	public Queue<Job> getCompleteList()
	{
		return completeds;
	}
	
	public boolean addDeletedJob(Job job)
	{
		return deleteds.add(job);
	}
	
	public boolean removeDeletedJob(Job job)
	{
		return deleteds.remove(job);
	}
	
	public Queue<Job> getDeleteList()
	{
		return deleteds;
	}
}