package cn.edu.buaa.act.service4all.bpmndeployment.Deploy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;
import cn.edu.buaa.act.service4all.bpmndeployment.File.deployMessage;


public class BPMNDeployInvoker extends Invoker
{

	@Override
	public Document createRequestDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
		
		DocumentBuilder builder;
		Element deployfeedback,issuccessful,servicename,description;
		Document doc=null;
		deployMessage message=null;
		
		message=(deployMessage) context.getData(Constants.deploymessage);				
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
		
		deployfeedback=doc.createElement(Constants.deployfeedback);
		deployfeedback.setAttribute(Constants.type, Constants.bpmn);
		doc.appendChild(deployfeedback);
		
		issuccessful=doc.createElement(Constants.isSuccessful);
		if(message.getStatement())
		{	
			issuccessful.setTextContent("true");			
		}
		else
		{
			issuccessful.setTextContent("false");
		}
		deployfeedback.appendChild(issuccessful);
		
		servicename=doc.createElement(Constants.serviceName);		
		servicename.setTextContent(message.getFileName());
		deployfeedback.appendChild(servicename);
		
		description=doc.createElement(Constants.description);
		description.setTextContent(message.getInformation());
		deployfeedback.appendChild(description);
		
		return doc;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		context.storeData(Constants.deployfeedbackresponse, doc);
		this.unit.onReceiveResponse(context);	
	}
}