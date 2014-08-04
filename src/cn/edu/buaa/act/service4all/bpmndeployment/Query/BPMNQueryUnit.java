package cn.edu.buaa.act.service4all.bpmndeployment.Query;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;
import cn.edu.buaa.act.service4all.bpmndeployment.File.MyBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.File.fileList;
import cn.edu.buaa.act.service4all.bpmndeployment.File.queryMessage;


public class BPMNQueryUnit extends MyBusinessUnit
{
	private final Log logger = LogFactory.getLog(BPMNQueryUnit.class);

	@Override
	public void dispatch(ExchangeContext context) 
	{
		// TODO Auto-generated method stub
		String servicename,serviceid,jobid;
		queryMessage message;
		DocumentBuilder builder;
		Document doc = null;
		Element root,element;
		
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
		
		servicename=(String) context.getData(Constants.serviceName);
		serviceid=(String) context.getData(Constants.serviceid);
		jobid=(String) context.getData(Constants.jobId);
		
		message=fileList.getNewInstance().query(servicename, this.getDir());
		logger.info(message.getInformation());
		
		root=doc.createElement(Constants.getBpmnResponse);
		doc.appendChild(root);
		
		element=doc.createElement(Constants.serviceName);
		element.setTextContent(servicename);
		root.appendChild(element);
		
		element=doc.createElement(Constants.serviceid);
		element.setTextContent(serviceid);
		root.appendChild(element);
		
		element=doc.createElement(Constants.jobId);
		element.setTextContent(Constants.jobId);
		root.appendChild(element);
		
		element=doc.createElement(Constants.bpmn);
		element.setTextContent(message.getData());
		root.appendChild(element);
		
		logger.info(doc);
		context.storeData(Constants.getBpmnResponse, doc);
		
		try 
		{
			this.getReceiver().sendResponseMessage(context);
		} 
		catch (MessageExchangeInvocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) 
	{
		// TODO Auto-generated method stub
		
	}

}
