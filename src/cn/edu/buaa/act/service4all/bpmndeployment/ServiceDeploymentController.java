package cn.edu.buaa.act.service4all.bpmndeployment;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNDeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNQueryException;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNUndeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceDeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceQueryTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceUndeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

/**
 * single pattern
 * 
 * @author enqu
 *
 */
public class ServiceDeploymentController {
	
	private Log logger = LogFactory.getLog(ServiceDeploymentController.class);
	
	
	private static ServiceDeploymentController self;
	
	private BPMNManagement manager;
	
	private ServiceDeploymentController(){
		//do nothing currently
		manager = new BPMNManagement();
	}
	
	public static ServiceDeploymentController getInstance(){
		if(self == null){
			self = new ServiceDeploymentController();
		}
		return self;
	}
	
	public void deployService(Task task) throws BPMNDeploymentException{
		
		ServiceDeployTask deployTask = (ServiceDeployTask)task;
		String serviceID = manager.addService(deployTask.getTargetServiceName(), 
												deployTask.getProvider(), 
												deployTask.getBpmnDoc());
		
		if(serviceID == null){
			logger.warn("Can't deploy the service by its name: " + task.getTargetServiceName());
			throw new BPMNDeploymentException("Can't deploy the service by its name: " + task.getTargetServiceName());
		}
		
		task.setTargetServiceID(serviceID);
	}
	
	public void undeployService(Task task) throws BPMNUndeploymentException{
		
		logger.info("Undeploy the service : " + task.getTargetServiceID());
		ServiceUndeployTask undeployTask = (ServiceUndeployTask)task;
		
		manager.deleteService(undeployTask.getTargetServiceName(), 
								undeployTask.getTargetServiceID());
		
		
	}
	
	public void queryService(Task task) throws BPMNQueryException{
		
		logger.info("Query the service's document: " + task.getTargetServiceName());
		ServiceQueryTask queryTask = (ServiceQueryTask)task;
		
		BPMNService service = manager.getServiceById(queryTask.getTargetServiceID());
		//queryTask.setBpmnDoc(service.)
		//get the file path of the bpmn document
		
		if(service == null){
			logger.warn("The service is null : " + task.getTargetServiceID());
			throw new BPMNQueryException("The service is null : " + task.getTargetServiceID());
		}
		
		String bpmnpath = service.getBpmnPath();
		Document bpmnDoc = retrieveDoc(bpmnpath);
		queryTask.setBpmnDoc(bpmnDoc);
		
	}
	
	private Document retrieveDoc(String path){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(path));
			
			return doc;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		
		return null;
	}
	
	public void stop() throws IOException{
		logger.info("Stop the service deployment controller!");
		
		this.manager.stop();
	}
	
}
