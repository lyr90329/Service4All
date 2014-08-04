package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppExecutionFeedbackBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.exception.AppException;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppInvocationEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.AppListener;

public class BPMNExecutionFeedbackBusinessUnit extends
		AppExecutionFeedbackBusinessUnit {
	
	private static final Log logger = LogFactory.getLog(BPMNExecutionFeedbackBusinessUnit.class);
	
	@Override
	public void dispatch(ExchangeContext context) {
		// TODO Auto-generated method stub
		
		String status = (String)context.getData(BPMNExecutionFeedbackReceiver.STATUS);
		String jobId = (String)context.getData(BPMNExecutionFeedbackReceiver.JOB_ID);
		String serviceId = (String)context.getData(BPMNExecutionFeedbackReceiver.SERVICE_ID);
		List<String> engines = (List<String>)context.getData(BPMNExecutionFeedbackReceiver.ENGINES);
		logger.info("Receive the BPMN execution feedback: " + jobId);
		String engineId = engines.get(0);
		if (status != null && status.equals("start")) {
			logger.info("Increase the BPMNEngine's load: " + engineId);
			this.applianceManager.increateApplianceReqLoad(engineId);
			
		}else{
			logger.info("Decrease the BPMNEngine's load: " + engineId);
			this.applianceManager.decreaseApplianceReqLoad(engineId);
		}
		
		// send back the response message to BPMNExecutionComponent
		try {
			this.getReceiver().sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			// TODO Auto-generated catch block
			this.handleInvocationException(e);
		}
		//sendResponse(event, context);
	}

	
}
