package cn.edu.buaa.act.service4all.bpmndeployment;

import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.messaging.ExchangeContext;

public class BPMNBusinessUnit extends BusinessUnit{
	
	public final static String TASK = "task";
	
	protected ServiceDeploymentController controller = ServiceDeploymentController.getInstance();
	
	
	@Override
	public void dispatch(ExchangeContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		// TODO Auto-generated method stub
		
	}

}
