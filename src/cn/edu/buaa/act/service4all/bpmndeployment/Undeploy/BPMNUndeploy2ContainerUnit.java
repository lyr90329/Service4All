package cn.edu.buaa.act.service4all.bpmndeployment.Undeploy;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;

import cn.edu.buaa.act.service4all.bpmndeployment.File.Constants;
import cn.edu.buaa.act.service4all.bpmndeployment.File.MyBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.File.deployMessage;
import cn.edu.buaa.act.service4all.bpmndeployment.File.fileList;


public class BPMNUndeploy2ContainerUnit extends MyBusinessUnit
{	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);
	}	
	
	public void dispatch(ExchangeContext context)
	{		
		deployMessage message = null;
		String filename;
		Invoker invoker;

		filename = (String) context.getData(Constants.filename);
		message = fileList.getNewInstance().deleteFile(filename,this.getDir());		
		context.storeData(Constants.undeploymessage, message);
		
		invoker=this.getInvokers().get(Constants.bpmnundeployinvoker);
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
