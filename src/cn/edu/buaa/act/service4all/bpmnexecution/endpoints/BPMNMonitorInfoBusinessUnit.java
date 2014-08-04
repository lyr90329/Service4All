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
import cn.edu.buaa.act.service4all.bpmnexecution.Job;
import cn.edu.buaa.act.service4all.bpmnexecution.MonitorRecord;


public class BPMNMonitorInfoBusinessUnit extends BPMNExecuteBusinessUnit 
{
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);	
	}

	@Override
	public void dispatch(ExchangeContext context) 
	{
		MonitorRecord record;
		DocumentBuilder builder;
		Document doc = null;
		Element root;
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
		
		record=(MonitorRecord) context.getData(Constants.MonitorInfoFeedback);
		
		this.getTaskManager().receiveJobMonitorInfo(record);
		
		if(record.isResult()){
			// end the execution of the bpmn
			String jobId = record.getJobId();
			Job job = this.getTaskManager().getJobByID(jobId);
			context.storeData(Constants.job, job);
			
			Invoker invoker = this.getInvokers().get("EndBPMNFeedbackInvoker");
			if(invoker == null){
				logger.warn("There is no EndBPMNFeedbackInvoker");
				// so do nothing just response to the server
				root=doc.createElement(Constants.MonitorInfoFeedbackResponse);
//				if(check)
//					root.setTextContent(Constants.booltrue);
//				else
//					root.setTextContent(Constants.boolfalse);
				root.setTextContent("true");
				doc.appendChild(root);
				
				context.storeData(Constants.MonitorInfoFeedbackResponse, doc);
				try 
				{
					this.getReceiver().sendResponseMessage(context);
				} 
				catch (MessageExchangeInvocationException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					invoker.sendRequestExchange(context);
				} catch (MessageExchangeInvocationException e) {
					// TODO Auto-generated catch block
					this.handleInvocationException(e);
				}
			}
			
			
		}else{
			root=doc.createElement(Constants.MonitorInfoFeedbackResponse);
//			if(check)
//				root.setTextContent(Constants.booltrue);
//			else
//				root.setTextContent(Constants.boolfalse);
			root.setTextContent("true");
			doc.appendChild(root);
			
			context.storeData(Constants.MonitorInfoFeedbackResponse, doc);
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
		
		
	}

	@Override
	public void onReceiveResponse(ExchangeContext arg0) 
	{
		// TODO Auto-generated method stub
		
	}

}
