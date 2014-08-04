package test.cn.edu.buaa.act.service4all.bpmnmonitor;

import java.sql.SQLException;
import java.util.List;

import cn.edu.buaa.act.service4all.bpmnmonitor.DataBaseUtils;
import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnmonitor.ResultRecord;

public class TestDataQuery {
	
	public TestDataQuery(){
		
		try {
			
			DataBaseUtils.init();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void testJobQuery(String jobId) throws SQLException{
		
		Job job = DataBaseUtils.getJobById(jobId);
		
		System.out.println(job.toString());
		
		DataBaseUtils.close();
		
	}
	
	public void testJobMonitorQuery(String jobId) throws SQLException{
		List<MonitorRecord> records = DataBaseUtils.getMonitorRecordByJobId(jobId);
	
		if(records != null && records.size() > 0){
			for(MonitorRecord r : records){
				System.out.println(r.toString());
			}
		}else{
			System.out.println("The monitor record is null!");
		}
		
		
		DataBaseUtils.close();
	}
	
	public void testJobResultQuery(String jobId) throws SQLException{
		
		ResultRecord result = DataBaseUtils.getResultRecordByJobId(jobId);
		
		if(result != null){
			
			System.out.println(result.toString());
			
		}else{
			System.out.println("The result record is null!");
		}
		
		
		DataBaseUtils.close();
	}
	
	public static void main(String[] args){
		TestDataQuery query = new TestDataQuery();
		try {
			
			
//			query.testJobQuery("24");
//			query.testJobMonitorQuery("24");
			query.testJobResultQuery("24");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
