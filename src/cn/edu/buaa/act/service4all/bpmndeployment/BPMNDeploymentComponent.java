package cn.edu.buaa.act.service4all.bpmndeployment;

import java.io.IOException;

import javax.jbi.JBIException;

import org.act.sdp.appengine.AppEngineComponent;

public class BPMNDeploymentComponent extends AppEngineComponent {
	
	
	public void stop() throws JBIException{
		super.stop();
		try {
			ServiceDeploymentController.getInstance().stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new JBIException(e.getMessage());
		}
	}
}
