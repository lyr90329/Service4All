package cn.edu.buaa.act.service4all.bpmnexecution;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.bpmnexecution.database.DataBaseUtils;

public class TaskManager 
{
	private Log logger = LogFactory.getLog(TaskManager.class);
	
	
	private TaskStatusManager taskStatusMger;
	private TaskExecuteResultManager executeResultMger;
	private TaskMonitorInfoManager monitorInfoMger;
	private List<Job> jobList;
	
	public TaskManager()
	{
		jobList=new LinkedList<Job>();
		taskStatusMger=new TaskStatusManager();
		monitorInfoMger=new TaskMonitorInfoManager();
		executeResultMger=new TaskExecuteResultManager();
	}
	
	public boolean createJob(Job job)
	{
		job.setJobStatus(Job.Deployed);		
		jobList.add(job);
		return taskStatusMger.addDeployedJob(job);
	}
	
	public boolean ReadyJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if (check)
		{
			TaskExecuteResult result=new TaskExecuteResult();
			job.setJobStatus(Job.Ready);
			check=check&this.taskStatusMger.addReadyJob(job);
			
			result.setStartTime(TaskExecuteResult.getCurrentDate());
			result.setJobId(job.getJobID());
			result.setServiceId(job.getServiceID());
			result.setServiceName(job.getServiceName());
			this.executeResultMger.startJob(job.getJobID(), result);
		}
		return check;
	}
	
	public boolean startJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if(check)
		{
			job.setJobStatus(Job.Started);	
			check=check&this.taskStatusMger.addStartedJob(job);
		}
		
		//write the job into the database;
		DataBaseUtils.persistJob(job);
		
		return check;
	}
	
	private boolean exceptionJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if (check)
		{
			job.setJobStatus(Job.Exception);
			check=check&this.taskStatusMger.addExceptiondJob(job);
		}
		return check;
	}
	
	private boolean errorJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if (check)
		{
			job.setJobStatus(Job.Error);
			check=check&this.taskStatusMger.addErrorJob(job);
		}
		return check;
	}
	
	private boolean completedJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if (check)
		{
			job.setJobStatus(Job.Completed);
			check=check&this.taskStatusMger.addCompleteJob(job);
		}
		return check;
	}
	
	public boolean deleteJob(Job job)
	{
		boolean check;
		check=deleteJobFromPreviousStatus(job);
		if (check)
		{
			job.setJobStatus(Job.Deleted);
			check=check&this.taskStatusMger.addDeletedJob(job);
		}
		return check;
	}
	
	private boolean deleteJobFromPreviousStatus(Job job)
	{
		switch(job.getJobStatus())
		{
		case Job.Deployed:
			return this.taskStatusMger.removeDeployJob(job);
		case Job.Ready:
			return this.taskStatusMger.removeReadyJob(job);
		case Job.Started:
			return this.taskStatusMger.removeStartedJob(job);
		case Job.Exception:
			return this.taskStatusMger.removeExceptiondJob(job);
		case Job.Error:
			return this.taskStatusMger.removeErrorJob(job);
		case Job.Completed:
			return this.taskStatusMger.removeCompleteJob(job);
		case Job.Deleted:
			return this.taskStatusMger.removeDeletedJob(job);
		default:
			return false;		  
		}		
	}
	
	public boolean receiveJobMonitorInfo(MonitorRecord record)
	{
		
		logger.info("Receive a job monitor info : " + record.getJobId());
		Job job;
		
		job=this.getJobByJobID(record.getJobId());
		if(job == null){
			logger.warn("The job does not exist in the job list : " + record.getJobId());
			return false;
		}
		
		if(record.getNodeStatus()==MonitorRecord.Exception)
			this.exceptionJob(job);
		
		monitorInfoMger.addTaskMonitorInfo(job, record);
		
		//write the MonitorRecord into the database
		return DataBaseUtils.persistMonitorRecord(record);
		
	}
	
	public void receiveExceptionMonitorInfo()
	{
		
	}
	
	public boolean receiveJobExecuteResult(TaskExecuteResult result)
	{
		Job job;
		boolean check;
		
		job=this.getJobByJobID(result.getJobId());
		result.setServiceId(job.getServiceID());
		result.setServiceName(job.getServiceName());
		if(result.getState()==TaskExecuteResult.completed)
		{
			this.completedJob(job);
		}
		else
		{
			this.errorJob(job);
		}
		check=this.executeResultMger.finishJob(job.getJobID(), result);
		
		return check;		
	}
	
	// at creating job ,there is no job id for a job ,so we use the request id to look for a specific job
	public Job getJobByID(String id)
	{
		int i;
		Job job;
		for(i=0;i<jobList.size();i++)
		{
			job=jobList.get(i);
			if(job.getJobID().equals(id))
			{
				return job;
			}			
		}
		return null;
	}
	
	public Job getJobByServiceID(String id){
		int i;
		Job job;
		for(i=0;i<jobList.size();i++)
		{
			job=jobList.get(i);
			if(job.getServiceID().equals(id))
			{
				return job;
			}			
		}
		return null;
	}
	public Job getJobByJobID(String Jobid)
	{
		
		int i;
		Job job;
		for(i=0;i<jobList.size();i++)
		{
			job=jobList.get(i);
			if(job.getJobID().equals(Jobid))
				return job;
		}
		return null;
	}
	
	public List queryJobStatus()
	{
		
		return null;		
	}
	
	public List queryJobMonitorInfo()
	{
		return null;
	}
	
	public List queryJobExecuteResult()
	{
		return null;
	}
}