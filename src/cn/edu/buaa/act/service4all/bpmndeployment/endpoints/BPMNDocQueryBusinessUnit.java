package cn.edu.buaa.act.service4all.bpmndeployment.endpoints;

import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.bpmndeployment.BPMNBusinessUnit;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNDeploymentException;
import cn.edu.buaa.act.service4all.bpmndeployment.exception.BPMNQueryException;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceDeployTask;
import cn.edu.buaa.act.service4all.bpmndeployment.task.ServiceQueryTask;

public class BPMNDocQueryBusinessUnit extends BPMNBusinessUnit {
	
	private Log logger = LogFactory.getLog(BPMNDocQueryBusinessUnit.class);
	
	@Override
	public void dispatch(ExchangeContext context) {
		// TODO Auto-generated method stub
		ServiceQueryTask task = (ServiceQueryTask)context.getData(TASK);
		
		try {
			this.controller.queryService(task);
			
		} catch (BPMNQueryException e) {
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
}
