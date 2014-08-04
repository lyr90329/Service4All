package test.cn.edu.buaa.act.service4all.bpmnexecution.execution;

import java.sql.SQLException;
import java.util.Date;

import cn.edu.buaa.act.service4all.bpmnexecution.Job;
import cn.edu.buaa.act.service4all.bpmnexecution.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnexecution.Parameter;
import cn.edu.buaa.act.service4all.bpmnexecution.database.DataBaseUtils;

public class TestDatabase {
	
	public void testInsertJob() throws ClassNotFoundException, SQLException{
		Job job = createJob();
		DataBaseUtils.init();
		
		DataBaseUtils.persistJob(job);
		
		DataBaseUtils.close();
	}
	
	public void testInsertMonitor() throws ClassNotFoundException, SQLException{
		MonitorRecord r = createMonitorRecord();
		DataBaseUtils.init();
		
		DataBaseUtils.persistMonitorRecord(r);
		
		DataBaseUtils.close();
	}
	
	public void testInsertResult() throws ClassNotFoundException, SQLException {
		MonitorRecord r = createResult();
		DataBaseUtils.init();
		
		DataBaseUtils.persistMonitorRecord(r);
		
		DataBaseUtils.close();
	}
	
	private Job createJob(){
		Job job = new Job();
		
		job.setJobID("a1");
		job.setServiceID("serviceID");
		job.setServiceName("serviceName");
		
		
		return job;
	}
	private MonitorRecord createMonitorRecord(){
		MonitorRecord r = new MonitorRecord();
		
		r.setJobId("a5");
		r.setNodeId("1024");
		r.setNodeStatus(0);
		r.setResult(false);
		r.setServiceName("JobService");
		
		//r.setStatusDesp("Good job for this service");
		r.setSuccessful(true);
		
		//construct the Parameters
		Parameter p1 = new Parameter();
		p1.setParameterName("name");
		p1.setParameterType("string");
		p1.setParameterValue("enqu");
		r.addParameter(p1);
		
		Parameter p2 = new Parameter();
		p2.setParameterName("age");
		p2.setParameterType("int");
		p2.setParameterValue("123");
		r.addParameter(p2);
		return r;
	}
	
	private MonitorRecord createResult(){
		MonitorRecord r = new MonitorRecord();
		
		r.setJobId("400");
		r.setResult(true);
		r.setServiceName("JobService");
		//r.setStatusDesp("Good job for this service");
		r.setSuccessful(true);
		
		//construct the Parameters
		Parameter p1 = new Parameter();
		p1.setParameterName("name");
		p1.setParameterType("string");
		p1.setParameterValue("enqu");
		r.addParameter(p1);
		
		Parameter p2 = new Parameter();
		p2.setParameterName("age");
		p2.setParameterType("int");
		p2.setParameterValue("123");
		r.addParameter(p2);
		return r;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		TestDatabase test = new TestDatabase();
		
		try {
//			test.testInsertJob();
//			test.testInsertMonitor();
			test.testInsertResult();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
