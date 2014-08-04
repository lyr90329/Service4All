package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.app.deployment.ServerQueryForDeploymentBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceFactory;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class BPMNEngineCreationInvoker extends Invoker {

	private final Log logger = LogFactory.getLog(BPMNEngineCreationInvoker.class);
	
	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		logger.info("Create the request document for ServiceDeployBusinessUnit!");
		String applianceType = (String)context.getData(BPMNQueryForExecutionBusinessUnit.QUERY_TYPE);
		String serviceId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.SERVICE_ID);
		String newDeployed = (String)context.getData(BPMNQueryForExecutionBusinessUnit.DEPLOY_NUM);
		String jobId = (String)context.getData(BPMNQueryForExecutionBusinessUnit.JOB_ID);
		
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		
		try {
			
			DocumentBuilder builder = f.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("applianceDeploymentRequest");
			
			root.setAttribute("type", applianceType);
			root.setAttribute("serviceId", serviceId);
			root.setAttribute("jobId", jobId);
			
			//添加要重新部署的容器设备数目
			Element deployNumEle = doc.createElement("deployNum");
			deployNumEle.setTextContent(String.valueOf(newDeployed));
			root.appendChild(deployNumEle);
			
			doc.appendChild(root);
		
			return doc;
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Can't create the document!", context);
			ep.setSender(unit.getReceiver().getEndpoint());
			// e.setTargetService(service);
			throw ep;
		}
	}

	@Override
	public void handleResponse(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		
		logger.info("Receive the appliance deployment response from ApplianceDeploymentReceiver: " 
						+ XMLUtils.retrieveDocumentAsString(request));
		
		// TODO Auto-generated method stub
		String serviceId = request.getDocumentElement().getAttribute("serviceId");
		if(serviceId == null){
			throw new MessageExchangeInvocationException("The service id from the ApplianceDeployment Response is null!");
		}
		
		String jobId = request.getDocumentElement().getAttribute("jobId");
		if(jobId == null){
			throw new MessageExchangeInvocationException("The job id from the ApplianceDeployment Response is null!");
		}
		
		String applianceType = request.getDocumentElement().getAttribute("type");
		if(applianceType == null){
			throw new MessageExchangeInvocationException("The appliance type from the ApplianceDeployment Response is null!");
		}
		
		if(request.getElementsByTagName("isSuccessful") == null 
				|| request.getElementsByTagName("isSuccessful").getLength() <= 0){
			throw new MessageExchangeInvocationException("The isSuccessful from the ApplianceDeployment Response is null!");
		}
		
		String isSuccessful = request.getElementsByTagName("isSuccessful").item(0).getTextContent();
		
		context.storeData(BPMNQueryForExecutionBusinessUnit.SERVICE_ID, serviceId);
		context.storeData(BPMNQueryForExecutionBusinessUnit.QUERY_TYPE, applianceType);
		context.storeData(BPMNQueryForExecutionBusinessUnit.IS_SUCCESS, isSuccessful);
		logger.info("*******************************************");		
		logger.info("BPMNEngineCreationInvoker jobid=" + jobId);
		logger.info("*******************************************");		
		context.storeData(BPMNQueryForExecutionBusinessUnit.JOB_ID, jobId);
		
		
		//如果部署成功，则会根据返回的容器部署结果
		if(isSuccessful.equals("true")){
			
			if(request.getElementsByTagName("appliances") == null 
					|| request.getElementsByTagName("appliances").getLength() <= 0){
				logger.warn("The response document misses the appliances element from ApplianceDeployment");
				throw new MessageExchangeInvocationException("The response document misses the appliances element from ApplianceDeployment");
			}
			
			Element appliances = (Element)request.getElementsByTagName("appliances").item(0);
			List<Appliance> newDeployeds = getNewDeployedApplianceList(appliances, applianceType);
			context.storeData(BPMNQueryForExecutionBusinessUnit.NEW_DEPLOYEDS, newDeployeds);
			
		}else{
			
			//部署失败，则获取相应的异常描述信息
			String exception;
			if(request.getElementsByTagName("desp") == null 
					|| request.getElementsByTagName("desp").getLength() <= 0){
				logger.warn("The response document misses the exception description element from ApplianceDeployment");
				exception = "The response document misses the exception description element from ApplianceDeployment";
			
			}else{
				Element desp = (Element)request.getElementsByTagName("desp").item(0);
				exception = desp.getTextContent();
			}
			
			context.storeData(BPMNQueryForExecutionBusinessUnit.DEPLOY_EXCEP, exception);
			
			
		}
		
		this.unit.onReceiveResponse(context);
	}
	
	protected List<Appliance> getNewDeployedApplianceList(Element appliances, String applianceType)
			throws MessageExchangeInvocationException{
		
		NodeList applianceList = appliances.getElementsByTagName("appliance");
		if(applianceList == null){
			logger.warn("The appliance List is missed from the ApplianceDeployment Response Message");
			throw new MessageExchangeInvocationException("The appliance List is missed from the ApplianceDeployment Response Message");
		}
		
		List<Appliance> newDeployeds = new ArrayList<Appliance>();
		for(int i = 0; i < applianceList.getLength(); i++){
			Element applianceElement = (Element)applianceList.item(i);
			Appliance appliance = createNewApplianceInstance(applianceElement, applianceType);
			newDeployeds.add(appliance);
		}
		
		return newDeployeds;
	}
	
	protected Appliance createNewApplianceInstance(Element applianceElement, String applianceType)
			throws MessageExchangeInvocationException{
		Appliance appliance = ApplianceFactory.createAppliance(applianceType);
		
		String applianceId = applianceElement.getAttribute("id");
		String port = applianceElement.getAttribute("port");
		String deployUrl = applianceElement.getAttribute("deployUrl");
		String deployOperation = applianceElement.getAttribute("deployOperation");
		
		//性能指标
		String cpu = applianceElement.getAttribute("cpu");
		String memory = applianceElement.getAttribute("memory");
		String throughput = applianceElement.getAttribute("throughput");
		
		
		if(applianceId == null){
			logger.warn("The appliance id is missing from the ApplianceDeployment response! ");
			throw new MessageExchangeInvocationException("The appliance id is missing from the ApplianceDeployment response! ");
		}
		
		appliance.getDesp().setId(applianceId);
		
		if(deployUrl == null){
			logger.warn("The appliance deploy URL is missing from the ApplianceDeployment response! ");
			throw new MessageExchangeInvocationException("The appliance deploy URL is missing from the ApplianceDeployment response! ");
		}
		
		
		appliance.getDesp().setDeployEPR(deployUrl);
		
		appliance.getDesp().setPort(port);
		appliance.getDesp().setDeployOperation(deployOperation);
		
		//其实还需要的是一些性能指标数据,这些数据可以从设备所在的Host中获取
		//所以这部分的工作可以留给ApplianceDeployment来进行数据添加
		appliance.getStatus().setCpuRate(Float.valueOf(cpu));
		appliance.getStatus().setDeployedAmount(1);
		appliance.getStatus().setMemoryfloat(Float.valueOf(memory));
		appliance.getStatus().setPort(Double.valueOf(throughput));
		
		
		return appliance;
	}
}
