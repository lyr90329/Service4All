package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

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
import cn.edu.buaa.act.service4all.core.samanager.app.deployment.ServerQueryForDeploymentBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.app.deployment.ServerQueryForDeploymentReceiver;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class BPMNEngineQueryForDeploymentReceiver extends
		ServerQueryForDeploymentReceiver {
private final Log logger = LogFactory.getLog(ServerQueryForDeploymentReceiver.class);
	
	
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		List<Appliance> appliances = (List<Appliance>)context.getData(ServerQueryForDeploymentBusinessUnit.QUERY_RESULT);
		String type = (String)context.getData(ServerQueryForDeploymentBusinessUnit.QUERY_TYPE);
		String serviceName = (String)context.getData(ServerQueryForDeploymentBusinessUnit.SERVICENAME);
		String serviceId = (String)context.getData(ServerQueryForDeploymentBusinessUnit.SERVICE_ID);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create the document");
			throw new MessageExchangeInvocationException("Can't create the document for query available servers!");
		}
		Document document = builder.newDocument();
		Element root = document.createElement("availableContainerResponse");
		root.setAttribute("type", type);
		document.appendChild(root);
		
		Element idElement = document.createElement("serviceID");
		idElement.setTextContent(serviceId);
		root.appendChild(idElement);
		
		Element serviceNameElement = document.createElement("serviceName");
		serviceNameElement.setTextContent(serviceName);
		root.appendChild(serviceNameElement);
		
		Element containerListElement = document.createElement("containerList");
		if(appliances == null){
			containerListElement.setAttribute("length", "0");
		}else{
			containerListElement.setAttribute("length", String.valueOf(appliances.size()));
			for(Appliance a : appliances){
				Element c = document.createElement("container");
				
				c.setAttribute("id", a.getDesp().getId());
				c.setAttribute("deployUrl", a.getDesp().getDeployEPR());
				c.setAttribute("deployOperation", a.getDesp().getDeployOperation());
				
				c.setAttribute("cup", String.valueOf(a.getStatus().getCpuRate()));
				c.setAttribute("memory", String.valueOf(a.getStatus().getMemoryfloat()));
				c.setAttribute("throughput", String.valueOf(a.getStatus().getPort()));
				
				containerListElement.appendChild(c);
			}
		}
		
		
		root.appendChild(containerListElement);
		
		return document;
	}

	@Override
	public void handlRequest(Document request, 
							ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		logger.info("Receive the available server query request!");
		if(!validateRequest(request)){
			logger.warn("The request for querying appliances is invalidate!");
			throw new MessageExchangeInvocationException("The request for querying appliances is invalidate!");
		}
		
		parseRequest(request, context);
		
		this.unit.dispatch(context);
	}
	
	/**
	 * check the request message 
	 * 
	 * @param doc
	 * @return
	 */
	protected boolean validateRequest(Document doc){
		return true;
	}

	protected void parseRequest(Document req, ExchangeContext context){
		Element root = req.getDocumentElement();
		String queryType = root.getAttribute("type");
		context.storeData(ServerQueryForDeploymentBusinessUnit.QUERY_TYPE, 
							queryType);
		
		if(root.getElementsByTagName("serviceName") == null 
				|| root.getElementsByTagName("serviceName").getLength() <= 0){
			logger.info("Just query the availalbe service and did not deploy one!");
			//do nothing
			
		}else{
			
			logger.info("Query the available severs for deployment");
			Element sn = (Element)root.getElementsByTagName("serviceName").item(0);
			String serviceName = sn.getTextContent();
			context.storeData(ServerQueryForDeploymentBusinessUnit.SERVICENAME,
								serviceName);
			
		}
	}
	
}
