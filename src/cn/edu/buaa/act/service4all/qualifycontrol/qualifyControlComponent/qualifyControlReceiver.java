package cn.edu.buaa.act.service4all.qualifycontrol.qualifyControlComponent;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;


public class qualifyControlReceiver extends Receiver
{
	@Override
	public Document createResponseDocument(ExchangeContext context)throws MessageExchangeInvocationException 
	{
		Document doc;
		
		doc=(Document) context.getData(Constants.response);
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context)	throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		context.storeData(Constants.request, doc);
		this.unit.dispatch(context);
	}
}
