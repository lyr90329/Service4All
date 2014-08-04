package cn.edu.buaa.act.service4all.bpmnexecution;

import java.sql.SQLException;

import javax.jbi.JBIException;

import org.act.sdp.appengine.AppEngineComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.bpmnexecution.database.DataBaseUtils;

public class BPMNExecutionComponent extends AppEngineComponent {
	
	private Log logger = LogFactory.getLog(BPMNExecutionComponent.class);
	
	public void start() throws JBIException{
		super.start();
		try {
			DataBaseUtils.init();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			throw new JBIException(e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			throw new JBIException(e.getMessage());
		}
	}
	
	public void stop() throws JBIException{
		super.stop();
		try {
			DataBaseUtils.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			throw new JBIException(e.getMessage());
		}
	}
}
