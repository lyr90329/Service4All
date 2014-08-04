package cn.edu.buaa.act.service4all.bpmndeployment;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmndeployment.message.MessageFactory;
import cn.edu.buaa.act.service4all.bpmndeployment.message.MessageParser;

public class BPMNReceiver extends Receiver{

	protected MessageParser parser;
	protected MessageFactory factory;
	
	public void init(AppEngineContext context) throws AppEngineException{
		super.init(context);
		
		initParser(context);
		initFactory(context);
	}
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlRequest(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
	}

	public MessageParser getParser() {
		return parser;
	}

	public void setParser(MessageParser parser) {
		this.parser = parser;
	}

	public MessageFactory getFactory() {
		return factory;
	}

	public void setFactory(MessageFactory factory) {
		this.factory = factory;
	}
	
	protected void initParser(AppEngineContext context){
		//just do nothing
	}
	
	protected void initFactory(AppEngineContext context){
		//just do nothing
	}
	

}
