package cn.edu.buaa.act.service4all.bpmndeployment.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceDeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public class BPMNDeploymentMsgParser extends MessageParser {
	
	private Log logger = LogFactory.getLog(BPMNDeploymentMsgParser.class);
	
	/**
	 * validate the request message for the serviceName, serivceDoc etc. attributes
	 * 
	 */
	@Override
	public boolean validate(Document request) {
		// TODO Auto-generated method stub
//		Element root = request.getDocumentElement();
		
//		String typeAttr = root.getAttribute("type");
//		if(typeAttr == null || !typeAttr.equalsIgnoreCase("bpmn")){
//			logger.warn("The type of request is not BPMN");
//			return false;
//		}
//		if(request.getElementsByTagName("serviceName") == null 
//				|| request.getElementsByTagName("serviceName").getLength() <= 0){
//			logger.warn("Miss the serviceName element from the request");
//			return false;
//		}
//		return true;
		
		return (validateAttribute(request, "type", "bpmn") 
				&& validateElement(request, "serviceName") 
				&& validateElement(request, "serviceDoc"));
		
	}

	@Override
	public Task parse(Document request) {
		// TODO Auto-generated method stub
		ServiceDeployTask task = new ServiceDeployTask();
		
		String serviceName = getElementStrValue(request, "serviceName");
		task.setTargetServiceName(serviceName);
		
		//judge whether there is a provider element
		if(request.getElementsByTagName("provider") != null 
				&& request.getElementsByTagName("provider").getLength() > 0){
			String provider = request.getElementsByTagName("provider").item(0).getTextContent();
			logger.info("The " +  serviceName + "service's provider is " + provider);
			task.setProvider(provider);
		}
		
		//get the bpmn document from the serviceDoc element
		Document bpmnDoc = getElementDocValue(request, "serviceDoc");
		//maybe the bpmnDoc is null
		task.setBpmnDoc(bpmnDoc);
		
		return task;
	}
	
	
}
