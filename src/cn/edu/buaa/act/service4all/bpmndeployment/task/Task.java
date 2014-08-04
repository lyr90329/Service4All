package cn.edu.buaa.act.service4all.bpmndeployment.task;

import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNException;

public class Task {
	
	protected String targetServiceName;
	protected String targetServiceID;
	protected BPMNException exception;
	protected long taskID;
	
	public String getTargetServiceName() {
		return targetServiceName;
	}
	public void setTargetServiceName(String targetServiceName) {
		this.targetServiceName = targetServiceName;
	}
	public String getTargetServiceID() {
		return targetServiceID;
	}
	public void setTargetServiceID(String targetServiceID) {
		this.targetServiceID = targetServiceID;
	}
	public BPMNException getException() {
		return exception;
	}
	public void setException(BPMNException exception) {
		this.exception = exception;
	}
	public long getTaskID() {
		return taskID;
	}
	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}
	
	
	
}
