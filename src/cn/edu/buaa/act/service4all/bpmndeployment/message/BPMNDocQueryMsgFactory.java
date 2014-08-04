package cn.edu.buaa.act.service4all.bpmndeployment.message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceQueryTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNDocQueryMsgFactory extends MessageFactory {

	@Override
	protected void writeExceptionRespMsg(Document doc, Task task) {
		// TODO Auto-generated method stub
		
		ServiceQueryTask t = (ServiceQueryTask)task;
		
		Element root = doc.createElement("queryResponse");
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
		
		Element jobID = doc.createElement("jobId");
		jobID.setTextContent(t.getJobId());
		root.appendChild(jobID);
	}

	@Override
	protected void writeRespMsg(Document doc, Task task) {
		// TODO Auto-generated method stub
		ServiceQueryTask t = (ServiceQueryTask)task;
		
		Element root = doc.createElement("queryResponse");
		root.setAttribute("type", "bpmn");
		doc.appendChild(root);
		
		Element serName = doc.createElement("serviceName");
		serName.setTextContent(t.getTargetServiceName());
		root.appendChild(serName);
		
		Element serID = doc.createElement("serviceID");
		serID.setTextContent(t.getTargetServiceID());
		root.appendChild(serID);
		
		Element bpmn = doc.createElement("bpmn");
		Element bpmnElement = t.getBpmnDoc().getDocumentElement();
		Element newValue = (Element)doc.importNode(bpmnElement, true);
		bpmn.appendChild(newValue);
		root.appendChild(bpmn);
		
		Element jobID = doc.createElement("jobId");
		jobID.setTextContent(t.getJobId());
		root.appendChild(jobID);
		
	}

}
