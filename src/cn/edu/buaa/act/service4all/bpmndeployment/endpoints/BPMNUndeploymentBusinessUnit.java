package cn.edu.buaa.act.service4all.bpmndeployment.endpoints;

import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;

import cn.edu.buaa.act.service4all.bpmndeployment.BPMNBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNDeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNUndeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceDeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceUndeployTask;

public class BPMNUndeploymentBusinessUnit extends BPMNBusinessUnit {

	@Override
	public void dispatch(ExchangeContext context) {
		// TODO Auto-generated method stub
		ServiceUndeployTask task = (ServiceUndeployTask)context.getData(TASK);
		
		try {
			this.controller.undeployService(task);
			
		} catch (BPMNUndeploymentException e) {
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
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		// TODO Auto-generated method stub

	}

}
