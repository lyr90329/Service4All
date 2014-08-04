package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;

public class BPMNExecuteResultRecordInvoker extends Invoker
{
	private final Log logger = LogFactory.getLog(BPMNExecuteResultRecordInvoker.class);
	
	@Override
	public Document createRequestDocument(ExchangeContext context)	throws MessageExchangeInvocationException 
	{
		Document doc;
		doc=(Document) context.getData(Constants.ExecuteResultRecord);
		logger.info(doc);
		return doc;
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)	throws MessageExchangeInvocationException 
	{		
		// TODO Auto-generated method stub
		logger.info(doc);
		context.storeData(Constants.ExecuteResultRecordResponse, doc);
		this.unit.onReceiveResponse(context);
	}

}
