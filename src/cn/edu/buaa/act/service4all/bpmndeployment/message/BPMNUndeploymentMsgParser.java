package cn.edu.buaa.act.service4all.bpmndeployment.message;

import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceUndeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNUndeploymentMsgParser extends MessageParser {

	@Override
	public boolean validate(Document request) {
		// TODO Auto-generated method stub
		return (validateAttribute(request, "type", "bpmn") 
				&& validateElement(request, "serviceName") 
				&& validateElement(request, "serviceID"));
	}

	@Override
	public Task parse(Document request) {
		// TODO Auto-generated method stub
		
		ServiceUndeployTask task = new ServiceUndeployTask();
		
		String serviceName = getElementStrValue(request, "serviceName");
		task.setTargetServiceName(serviceName);
		
		String serviceID = getElementStrValue(request, "serviceID");
		task.setTargetServiceID(serviceID);
		
		return task;
	}

}
