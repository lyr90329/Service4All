package cn.edu.buaa.act.service4all.bpmndeployment.endpoints;

import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmndeployment.BPMNBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.BPMNReceiver;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNDeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceDeployTask;

public class BPMNDeploymentBusinessUnit extends BPMNBusinessUnit {

	@Override
	public void dispatch(ExchangeContext context) {
		// TODO Auto-generated method stub
		ServiceDeployTask task = (ServiceDeployTask)context.getData(TASK);
		
		try {
			this.controller.deployService(task);
			
		} catch (BPMNDeploymentException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			//create the exception message 
			task.setException(e);
			
		}
		
		context.storeData(TASK, task);
		try {
			
			this.getReceiver().sendResponseMessage(context);
			
		} catch (MessageExchangeInvocationException e) {
			// TODO Auto-generated catch block
			this.handleInvocationException(e);
		}
		//create the response message by message factory
//		BPMNReceiver r = (BPMNReceiver)this.getReceiver();
//		Document respDoc = r.getFactory().createResponseMsg(task);
//		r.s
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		// TODO Auto-generated method stub

	}

}
