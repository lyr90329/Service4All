package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.IDCounter;
import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppQueryForExecutionBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppQueryForExecutionReceiver;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppRepetition;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceStatus;

public class BPMNQueryForExecutionReceiver extends AppQueryForExecutionReceiver {
	
	private Log logger = LogFactory.getLog(BPMNQueryForExecutionReceiver.class);
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		
		String serviceID = (String)context.getData(AppQueryForExecutionBusinessUnit.SERVICE_ID);
		String type = (String)context.getData(AppQueryForExecutionBusinessUnit.QUERY_TYPE);
		List<Appliance> engines = (List<Appliance>)context.getData(BPMNQueryForExecutionBusinessUnit.NEW_DEPLOYEDS);
		String jobId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.JOB_ID);
		logger.info("Create the response message document for bpmn execution query for the job id: " + jobId);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement("availableServiceResponse");
			root.setAttribute("type", type);
//			root.setAttribute(QUERY_ALGORITHM, event.getAlgorithm());
			
			Element idElement = document.createElement("serviceID");
			idElement.setTextContent(serviceID);
			root.appendChild(idElement);
			
			Element jobIDElement = document.createElement("jobId");
			jobIDElement.setTextContent(jobId);
//			jobIDElement.setTextContent(String.valueOf(jobId));
			root.appendChild(jobIDElement);
			
			Element numElement = document.createElement("num");
			
			
			if(engines == null || engines.size() <= 0 ){
				
				numElement.setTextContent("0");
				root.appendChild(numElement);
				
				Element despElement = document.createElement("description");
				despElement.setTextContent("There is no available BPMNEngine for the ID: " 
												+ serviceID);
				root.appendChild(despElement);
				
			}else{
				numElement.setTextContent(String.valueOf(engines.size()));
				root.appendChild(numElement);
				//adding the service list information
				Element serviceList = document.createElement("services");
				for(Appliance a : engines){
					
					Element service = document.createElement("service");
					
					service.setAttribute("targetEngine", a.getDesp().getId());
					service.setAttribute("operation", "executeBpmnFlow");
					
					String deployEPR = a.getDesp().getDeployEPR();
					String invokeUrl = getInvokeUrl(deployEPR);
					logger.info("Get a bpmnengine with its EPR : " + invokeUrl);
					service.setTextContent(invokeUrl);
					
					ApplianceStatus status = a.getStatus();
					float cpu = status.getCpuRate();
					float memory = status.getMemoryfloat();
					double throughput = status.getPort();
					int deployedAmount = status.getDeployedAmount();
					long reqLoad = status.getReqLoad();
						
					service.setAttribute("cpu", String.valueOf(cpu));
					service.setAttribute("memory", String.valueOf(memory));
					service.setAttribute("throughput", String.valueOf(throughput));
					service.setAttribute("deployedServiceAmount", String.valueOf(deployedAmount));
					
					serviceList.appendChild(service);
					
				}
				
				root.appendChild(serviceList);
			}
			
			document.appendChild(root);
			return document;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private String getInvokeUrl(String deployUrl){
		try {
			URL url = new URL(deployUrl);
			String invokeUrl = "http://" + url.getHost() + ":" + url.getPort() + "/axis2/services/BpmnEngineService/";
			return invokeUrl;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.warn("The deploy url is invalidated : " + deployUrl);
			return null;
		}
		
	}
	
	protected void parseRequest(Document request, ExchangeContext context){
		
		
		
		String type = request.getDocumentElement().getAttribute("type");
		
		Element serviceIDElement = (Element)request.getElementsByTagName("serviceID").item(0);
		String id = serviceIDElement.getTextContent();
		
		Element jobIdElement = (Element)request.getElementsByTagName("jobId").item(0);
		String jobId = jobIdElement.getTextContent();
		
		logger.info("Get the bpmn query request for execution : " + jobId);
		
		Element deploymentNumElement = (Element)request.getElementsByTagName("deploymentNum").item(0);
		String deploymentNum = deploymentNumElement.getTextContent();
		
		context.storeData(BPMNQueryForExecutionBusinessUnit.DEPLOY_NUM, deploymentNum);
		logger.info("***************************");
		logger.info("jobId="+jobId);
		logger.info("***************************");
		context.storeData(BPMNQueryForExecutionBusinessUnit.JOB_ID, jobId);
		context.storeData(AppQueryForExecutionBusinessUnit.SERVICE_ID, id);
		context.storeData(AppQueryForExecutionBusinessUnit.QUERY_TYPE, type);
		
		
	}
}
