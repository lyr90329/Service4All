package cn.edu.buaa.act.service4all.bpmndeployment.Undeploy;

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


public class BPMNUndeployInvoker extends Invoker
{

	@Override
	public Document createRequestDocument(ExchangeContext context)	throws MessageExchangeInvocationException 
	{		
		DocumentBuilder builder;
		Element undeployfeedback, issuccessful, servicename, description,serviceid;
		Document doc = null;
		deployMessage message=null;
		
		message=(deployMessage) context.getData(Constants.undeploymessage);
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		undeployfeedback = doc.createElement(Constants.undeployfeedback);
		undeployfeedback.setAttribute(Constants.type, Constants.bpmn);
		doc.appendChild(undeployfeedback);

		issuccessful = doc.createElement(Constants.isSuccessful);
		if (message.getStatement())
		{
			serviceid=doc.createElement(Constants.serviceid);
			serviceid.setTextContent(message.getFile().getServiceID());
			issuccessful.setTextContent("true");
		}
		else
		{
			issuccessful.setTextContent("false");
		}
		undeployfeedback.appendChild(issuccessful);

		servicename = doc.createElement(Constants.serviceName);
		servicename.setTextContent(message.getFileName());
		undeployfeedback.appendChild(servicename);

		description = doc.createElement(Constants.description);
		description.setTextContent(message.getInformation());
		undeployfeedback.appendChild(description);
		return doc;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)	throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		context.storeData(Constants.undeployfeedbackresponse, doc);
		this.unit.onReceiveResponse(context);
		
	}

}
