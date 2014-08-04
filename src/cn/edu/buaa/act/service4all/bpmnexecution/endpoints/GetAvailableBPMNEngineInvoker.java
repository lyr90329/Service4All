package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;


public class GetAvailableBPMNEngineInvoker extends Invoker
{
	private final Log logger = LogFactory.getLog(GetAvailableBPMNEngineInvoker.class);
	
	@Override
	public Document createRequestDocument(ExchangeContext context)	throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		DocumentBuilder builder;
		Document doc = null;
		Element root,element, deploymentNumElement;
		String serviceid,requestid;
		
		try 
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
		}
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		}		
		Job job = (Job)context.getData(Constants.job);
		
		root=doc.createElement(Constants.avalibaleServiceRequest);
		root.setAttribute(Constants.type, Constants.bpmnengine);
		root.setAttribute(Constants.algorithm, Constants.roundrobbin);
		doc.appendChild(root);
		
		serviceid= job.getServiceID();
		element=doc.createElement(Constants.serviceID);
		element.setTextContent(serviceid);
		root.appendChild(element);
		
		//requestid=(String) context.getData(Constants.requestID);
		//requestid = job.get
		element=doc.createElement("jobId");
		element.setTextContent(job.getJobID());
		root.appendChild(element);
		
		deploymentNumElement = doc.createElement("deploymentNum");
		deploymentNumElement.setTextContent("1");
		root.appendChild(deploymentNumElement);
		
		return doc;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)	throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		//do nothing just store the document
		context.storeData(Constants.availableServiceResponse, doc);
		//by tangyu
		logger.info("*******************************************");		
		logger.info("doc:" + doc);		
		logger.info("*******************************************");		
		this.unit.onReceiveResponse(context);
	}
}
