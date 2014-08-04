package cn.edu.buaa.act.service4all.bpmndeployment.message;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

public abstract class MessageFactory {
	
	private Log logger = LogFactory.getLog(MessageFactory.class);
	
	public Document createResponseMsg(Task task){
		
		Document resp = newDocument();
		if(resp == null){
			//can't create a new document
			logger.warn("Can't create a new Document for responese");
			return null;
		}
		
		if(task.getException() == null){
			
			//write the response message according to the task
			writeRespMsg(resp, task);
			
		}else{
			
			//there is some exception
			writeExceptionRespMsg(resp, task);
			
		}
		
		
		return resp;
	}
	
	public Document createExceptionDocument(String exception){
		Document resp = newDocument();
		if(resp == null){
			//can't create a new document
			logger.warn("Can't create a new Document exception response");
			return null;
		}
		
		//create the content of the response message
		writeExceptionRespMsg(resp, exception);
		
		return resp;
	}
	
	protected void writeExceptionRespMsg(Document doc, String exception){
		
		Element root = doc.createElement("response");
		Element excep = doc.createElement("exception");
		excep.setTextContent(exception);
		root.appendChild(excep);
		
		doc.appendChild(root);
	}
	
	/**
	 * create the response message according to the task object
	 * 
	 * @param doc
	 * @param task
	 */
	protected abstract void writeExceptionRespMsg(Document doc, Task task);
	
	protected abstract void writeRespMsg(Document doc, Task task);
	
	private Document newDocument(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			//logger.warn("Can't create a new document: " + e.getMessage());
			return null;
		}
		
	}
}
