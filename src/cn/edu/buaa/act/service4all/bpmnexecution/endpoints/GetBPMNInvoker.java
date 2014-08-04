package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.messaging.util.XMLUtils;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;

public class GetBPMNInvoker extends Invoker {

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		
		DocumentBuilder builder;
		Document doc = null;
		Element root, nameElement, idElement;
		String serviceName;
		String serviceID;
		
		try 
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
		}
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		root=doc.createElement("GetBpmnRequest");
		root.setAttribute("type", "bpmn");
		doc.appendChild(root);
		
		Job job = (Job)context.getData(Constants.job);
		
		//serviceName = (String) context.getData(Constants.serviceName);
		nameElement = doc.createElement(Constants.serviceName);
		nameElement.setTextContent(job.getServiceName());
		
//		serviceID = (String)context.getData(Constants.serviceID);
		idElement = doc.createElement(Constants.serviceID);
		idElement.setTextContent(job.getServiceID());
		
		Element jobIdElement = doc.createElement(Constants.jobId);
		jobIdElement.setTextContent(job.getJobID());
		
		root.appendChild(idElement);
		root.appendChild(nameElement);
		root.appendChild(jobIdElement);
		
		return doc;
	}

	@Override
	public void handleResponse(Document response, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		logger.info("The get bpmn doc response: " + XMLUtils.retrieveDocumentAsString(response));
		
		String serviceID = response.getElementsByTagName("serviceID").item(0).getTextContent();
		String serviceName = response.getElementsByTagName("serviceName").item(0).getTextContent();
		String jobId = response.getElementsByTagName("jobId").item(0).getTextContent();
		
		Element bpmnElement = (Element)response.getElementsByTagName("bpmn").item(0);
		Element bpmnContent = (Element)bpmnElement.getFirstChild();
	
		try{
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document bpmnDoc = builder.newDocument();
			
			//clone the BPMN Node 
			//Node bpmnClone = bpmn.cloneNode(true);
			Node bpmnClone = bpmnDoc.importNode(bpmnContent, true);
			bpmnDoc.appendChild(bpmnClone);
			
			context.storeData(Constants.serviceID, serviceID);
			context.storeData(Constants.serviceName, serviceName);
			
			context.storeData("jobId", jobId);
			context.storeData("bpmn", bpmnDoc);
			
			//by tangyu
			System.out.println("**********GetBPMNInvoker**********");
			System.out.println("jobId" + jobId);
			System.out.println("bpmnDoc" + bpmnDoc);
			System.out.println("**********GetBPMNInvoker**********");
			
			BPMNExecuteReceiveBussinessUnit u = (BPMNExecuteReceiveBussinessUnit)this.unit;
			u.onReceiveQueryResponse(context);
			
		}catch(ParserConfigurationException e){
			logger.warn("The parser configuration exception, so we can't get the BPMN Document", e);
		}
	}

}
