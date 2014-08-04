package cn.edu.buaa.act.service4all.bpmndeployment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNUndeploymentException;

public class BPMNManagement {
	
	private final Log logger = LogFactory.getLog(BPMNManagement.class);
	
	private Map<String, BPMNService> bpmnServices = new HashMap<String, BPMNService>();
	
	private long idcounter = -1;
	
	private final String tmpFile = "deployment/counter.properties";
	private final String deployDir = "deployment";
	
	public BPMNManagement(){
		
		//loading the  id counter and initialized 
		initDeploymentDir();
		try {
			idcounter = initCounter();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			logger.warn("can't initiate the id counter" + e.getMessage());
			idcounter = 1;
		}
	}
	
	public void stop() throws IOException{
		File tmp = new File(tmpFile);
		if(!tmp.exists()){
			logger.warn("The counter properties file does not exist, so there must be some error");
			throw new FileNotFoundException();
		}
		
		FileOutputStream output = new FileOutputStream(tmp);
		Properties props = new Properties();
		
		props.setProperty("counter", String.valueOf(idcounter));
		props.store(output, "The jobCounter");
		
	}
	
	private long initCounter() throws IOException{
		File tmp = new File(tmpFile);
		if(!tmp.exists()){
			logger.info("The properties file does not exist for deployment, so create a new one!");
			tmp.createNewFile();
			return 1;
		}
		
		FileInputStream input = new FileInputStream(tmp);
		Properties props = new Properties();
		props.load(input);
		if(props.getProperty("counter") != null){
			return new Long(props.getProperty("counter"));
		}else{
			return 1;
		}
		
		
	}
	
	/**
	 * if not exist not, create a new one
	 * 
	 */
	private void initDeploymentDir(){
		
		File deployDir = new File(this.deployDir);
		if(!deployDir.exists()){
			
			logger.warn("The deployment directory does not exist!");
			deployDir.mkdir();
			
		}else{
			
			File[] files = deployDir.listFiles();
			logger.info("There are " + files.length + " files deployed");
//			for(int i = 0; i < files.length; i++){
//				
//			}
		}
		
		
	}
	
	/**
	 * add a new bpmn service to the component
	 * return the id of the service if successful, or return null
	 * 
	 * @param serviceName
	 * @param provider
	 * @param serviceDoc
	 * @return
	 */
	public String addService(String serviceName, String provider, Document serviceDoc){
		BPMNService service = new BPMNService();
		
		String serviceID = "BPMN_" + String.valueOf(increaseCounter());
		service.setServiceID(serviceID);
		service.setServiceName(serviceName);
		service.setProvider(provider);
		
		
		try {
			
			//write the BPMN Document to local filesystem
			persistBPMNDoc(service, serviceDoc);
			
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't persist the BPMN Service to local file system: " + e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't persist the BPMN Service to local file system: " + e.getMessage());
			return null;
		}
		
		logger.info("Addint a new service to the component: " + serviceID);
		bpmnServices.put(serviceID, service);
		
		return serviceID;
		
	}
	
	public synchronized long increaseCounter(){
		return idcounter++;
	}
	
	/**
	 * Just persist the bpmn file in the local file system
	 * 
	 * @param service
	 * @param doc
	 * @throws TransformerException 
	 * @throws IOException 
	 */
	private void persistBPMNDoc(BPMNService service, Document doc) 
											throws TransformerException, IOException{
		
		logger.info("Persist the bpmn service to local file system: " + service.getServiceID());
		String bpmnFileName = service.getServiceID() + "_" + service.getServiceName() + ".bpmn";
		//String partFileName = service.getServiceID() + "-" + service.getServiceName() + ".part";
		
		File bpmnFile = new File(this.deployDir + "/" + bpmnFileName);
		
		if(!bpmnFile.exists()){
			boolean flag = bpmnFile.createNewFile();
			logger.info("Create a new file : " + flag);
		}
		
		
		//write the document into the file system using transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new FileOutputStream(bpmnFile));
		
		transformer.transform(source, result);
		
		//set the bpmn service path
		service.setBpmnPath(bpmnFile.getAbsolutePath());
	}
	
	/**
	 * delete the service by using the local 
	 * 
	 * @param serviceName
	 * @param serviceID
	 */
	public void deleteService(String serviceName, String serviceID)throws BPMNUndeploymentException{
		
		//get the BPMNService instance by serviceID
		BPMNService targetService = bpmnServices.get(serviceID);
		if(targetService == null){
			logger.warn("The target service to be deleted does not exist!");
			throw new BPMNUndeploymentException("The target service to be deleted does not exist!");
		}
		
		//delete the bpmn service file
		File targetFile = new File(this.deployDir + "/" + serviceID + "_" + serviceName + ".bpmn");
		if(targetFile.exists()){
			logger.info("delete the bpmn file : " + targetFile.getAbsolutePath());
			targetFile.delete();
		}else{
			//just do nothing when
		}
		
		bpmnServices.remove(serviceID);
		
	}
	
	public BPMNService getServiceById(String serviceID){
		//logAllServices(serviceID);
		logger.info("Get the service instance by : " + serviceID);
		BPMNService service = bpmnServices.get(serviceID);
		if(service == null){
			logger.info("The query result is  null");
		}
		return service;
	}
	
	private void logAllServices(String serviceID){
		Iterator<String> ids = bpmnServices.keySet().iterator();
		logger.info("There are " + bpmnServices.size() + " bpmn services!");
		while(ids.hasNext()){
			String id = ids.next();
			if(id.equals(serviceID)){
				logger.info("serviceID in the maps!");
			}
			if(bpmnServices.get(id) != null){
				logger.info("The service: " + id);
			}
			
		}
	}
	
	public List<BPMNService> getServicesByProvider(String provider){
		List<BPMNService> services = new ArrayList<BPMNService>();
		
		Iterator<BPMNService> it = bpmnServices.values().iterator();
		while(it.hasNext()){
			BPMNService ser = it.next();
			if(ser.getProvider() != null && ser.getProvider().equalsIgnoreCase(provider)){
				services.add(ser);
			}
		}
		
		return services;
	}
	
	public List<BPMNService> getAllServices(){
		return new ArrayList<BPMNService>(bpmnServices.values());
	}
}
