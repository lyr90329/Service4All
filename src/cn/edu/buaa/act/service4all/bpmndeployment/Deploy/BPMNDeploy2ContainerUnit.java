package cn.edu.buaa.act.service4all.bpmndeployment.Deploy;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;
import cn.edu.buaa.act.service4all.bpmndeployment.File.MyBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.File.deployMessage;
import cn.edu.buaa.act.service4all.bpmndeployment.File.fileList;
import cn.edu.buaa.act.service4all.bpmndeployment.File.myFile;


public class BPMNDeploy2ContainerUnit extends MyBusinessUnit
{
	private final Log logger = LogFactory.getLog(BPMNDeploy2ContainerUnit.class);
		
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);
	}
	
	public void dispatch(ExchangeContext context)
	{		
		deployMessage message = null;
		String filename, bpmndata;

		filename = (String) context.getData(Constants.filename);
		bpmndata = (String) context.getData(Constants.bpmndata);
		message = fileList.getNewInstance().addNewFile(filename, bpmndata,this.getDir());
		logger.info("create bpmn file "+filename+" this content is "+bpmndata);

		context.storeData(Constants.deploymessage, message);

		Invoker invoker = this.getInvokers().get(Constants.bpmndeployinvoker);
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

	@Override
	public void onReceiveResponse(ExchangeContext context) 
	{
		Document doc;
		String servicename,serviceid;
		Element root;
		myFile file;
		
		doc=(Document) context.getData(Constants.deployfeedbackresponse);
		logger.info(doc);
		root=doc.getDocumentElement();
		serviceid=root.getElementsByTagName(Constants.serviceid).item(0).getTextContent();
		servicename=root.getElementsByTagName(Constants.serviceName).item(0).getTextContent();
		file=fileList.getNewInstance().getFileByFileName(servicename);
		if(file!=null)
		{
			file.setServiceID(serviceid);			
		}
		else
		{
			logger.error("bpmn file "+servicename+" is not found");
		}
		
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
