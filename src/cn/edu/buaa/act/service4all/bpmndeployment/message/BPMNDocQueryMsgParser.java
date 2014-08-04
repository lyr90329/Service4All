package cn.edu.buaa.act.service4all.bpmndeployment.message;

import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceQueryTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceUndeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNDocQueryMsgParser extends MessageParser {

	@Override
	public boolean validate(Document request) {
		// TODO Auto-generated method stub
		return (validateAttribute(request, "type", "bpmn") 
				&& validateElement(request, "serviceName") 
				&& validateElement(request, "serviceID")
				&& validateElement(request, "jobId"));
	}

	@Override
	public Task parse(Document request) {
		
		// TODO Auto-generated method stub
		ServiceQueryTask task = new ServiceQueryTask();
		
		String serviceName = getElementStrValue(request, "serviceName");
		task.setTargetServiceName(serviceName);
		
		String serviceID = getElementStrValue(request, "serviceID");
		task.setTargetServiceID(serviceID);
		
		String jobID = getElementStrValue(request, "jobId");
		task.setJobId(jobID);
		
		return task;
	}

}
