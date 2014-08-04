package cn.edu.buaa.act.service4all.bpmndeployment.Undeploy;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;


public class BPMNUndeployReceiver extends Receiver 
{

	@Override
	public void handlRequest(Document doc, ExchangeContext context)
	{
		// TODO Auto-generated method stub
		logger.info("Receiving the document!");
		getInformation(doc,context);		
		this.unit.dispatch(context);		
	}
	
	private void getInformation(Document doc,ExchangeContext context)
	{
		Element root;
		String filename;
		root=doc.getDocumentElement();
		filename=root.getElementsByTagName(Constants.filename).item(0).getTextContent();	
		context.storeData(Constants.filename, filename);
	}

	@Override
	public Document createResponseDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		Document doc=null;
		doc=(Document) context.getData(Constants.undeployfeedbackresponse);
		return doc;
	}
}