package cn.edu.buaa.act.service4all.bpmndeployment.Query;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;


public class BPMNQueryReceiver extends Receiver
{

	@Override
	public Document createResponseDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		Document doc;
		
		doc=(Document) context.getData(Constants.getBpmnResponse);
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		Element root,element;
		String serviceid,servicename,jobid;
		
		root=doc.getDocumentElement();
		servicename=root.getElementsByTagName(Constants.serviceName).item(0).getTextContent();
		serviceid=root.getElementsByTagName(Constants.serviceid).item(0).getTextContent();
		jobid=root.getElementsByTagName(Constants.jobId).item(0).getTextContent();
		
		context.storeData(Constants.serviceName, servicename);
		context.storeData(Constants.serviceid, serviceid);
		context.storeData(Constants.jobId, jobid);
		this.unit.dispatch(context);
	}
}
