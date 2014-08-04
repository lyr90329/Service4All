package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.TaskExecuteResult;


public class BPMNExecuteResultBusinessUnit extends BPMNExecuteBusinessUnit
{
	private boolean check;
	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);	
	}

	@Override
	public void dispatch(ExchangeContext context) 
	{
		// TODO Auto-generated method stub
		TaskExecuteResult result=null;
		
		result=(TaskExecuteResult) context.getData(Constants.ExecuteResult);
		check=this.getTaskManager().receiveJobExecuteResult(result);
		sendMessageToBPMNEngine(context);
		sendMessageToSAManager(context);
	}
	
	private void sendMessageToSAManager(ExchangeContext context)
	{
		DocumentBuilder builder;
		Document doc = null;
		Element root,element;
		String jobid,servicename,issuccessful,description;
		
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
		
		root=doc.createElement(Constants.ExecuteResultRecord);
		doc.appendChild(root);
		
		element=doc.createElement(Constants.jobId);
		jobid=(String) context.getData(Constants.jobId);
		element.setTextContent(jobid);
		root.appendChild(element);
		
		element=doc.createElement(Constants.serviceName);
		servicename=(String) context.getData(Constants.serviceName);
		element.setTextContent(servicename);
		root.appendChild(element);
		
		element=doc.createElement(Constants.isSuccessful);
		issuccessful=(String) context.getData(Constants.isSuccessful);
		element.setTextContent(issuccessful);
		root.appendChild(element);
		
		element=doc.createElement(Constants.description);
		description=(String) context.getData(Constants.description);
		element.setTextContent(description);
		root.appendChild(element);
		
		context.storeData(Constants.ExecuteResultRecord, doc);
		
		Invoker invoker = this.getInvokers().get(Constants.BPMNExecuteResultRecordInvoker);
		try 
		{
			invoker.sendRequestExchange(context);
		}
		catch (MessageExchangeInvocationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	private void sendMessageToBPMNEngine(ExchangeContext context)
	{		
		
		DocumentBuilder builder;
		Document doc = null;
		Element root;
		String type;
		
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
		
		root=doc.createElement(Constants.ExecuteResult);
		type=(String) context.getData(Constants.type);
		root.setAttribute(Constants.type, type);
		if(check)
			root.setTextContent(Constants.booltrue);
		else
			root.setTextContent(Constants.boolfalse);
		
		
		context.storeData(Constants.ExecuteResult,doc);
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
		Document doc;
		doc=(Document) context.getData(Constants.ExecuteResultRecordResponse);		
	}
}
