package cn.edu.buaa.act.service4all.bpmndeployment.exception;

import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNDeploymentException extends BPMNException{

	public BPMNDeploymentException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public BPMNDeploymentException(Task task, String msg){
		super(task, msg);
	}
	
}
