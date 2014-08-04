package cn.edu.buaa.act.service4all.bpmndeployment.message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNUndeploymentMsgFactory extends MessageFactory {

	@Override
	protected void writeExceptionRespMsg(Document doc, Task task) {
		// TODO Auto-generated method stub
		Element root = doc.createElement("undeployResponse");
		root.setAttribute("type", "bpmn");
		doc.appendChild(root);
		
		Element serName = doc.createElement("serviceName");
		serName.setTextContent(task.getTargetServiceName());
		root.appendChild(serName);
		
		Element serID = doc.createElement("serviceID");
		serID.setTextContent(task.getTargetServiceID());
		root.appendChild(serID);
		
		Element isSuccessful = doc.createElement("isSuccessful");
		isSuccessful.setTextContent("false");
		root.appendChild(isSuccessful);
		
		if(task.getException() != null){
			Element exception = doc.createElement("exception");
			exception.setTextContent(task.getException().getMessage());
			root.appendChild(exception);
		}
	}

	@Override
	protected void writeRespMsg(Document doc, Task task) {
		// TODO Auto-generated method stub
		Element root = doc.createElement("undeployResponse");
		root.setAttribute("type", "bpmn");
		doc.appendChild(root);
		
		Element serName = doc.createElement("serviceName");
		serName.setTextContent(task.getTargetServiceName());
		root.appendChild(serName);
		
		Element serID = doc.createElement("serviceID");
		serID.setTextContent(task.getTargetServiceID());
		root.appendChild(serID);
		
		Element isSuccessful = doc.createElement("isSuccessful");
		isSuccessful.setTextContent("true");
		root.appendChild(isSuccessful);
		
	}

}
