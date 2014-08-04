package cn.edu.buaa.act.service4all.bpmndeployment.exception;

import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNException extends Exception{
	
	protected Task task;
	
	public BPMNException(String msg){
		super(msg);
	}
	
	public BPMNException(Task task, String msg){
		super(msg);
		this.task = task;
		this.task.setException(this);
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
		
	}
	
}
