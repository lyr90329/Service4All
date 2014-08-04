package cn.edu.buaa.act.service4all.bpmnexecution.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;
import cn.edu.buaa.act.service4all.bpmnexecution.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnexecution.Parameter;

public class DataBaseUtils {
	
	private static Log logger = LogFactory.getLog(DataBaseUtils.class);
	
	private static Connection con;
	
	private static String url = "jdbc:mysql://124.205.18.184:3306/cloud_monitor";
	private static String user = "root";
	private static String pwd = "sdp123";	

	public static void init() throws ClassNotFoundException, SQLException{		
     		
		
		logger.info("Initiate the connection to the database ()");
		Class.forName("com.mysql.jdbc.Driver");
		
		try {
			loadConfigure();
			con = DriverManager.getConnection(url, user, pwd);
		} catch (SQLException e) {
			logger.error("Can't get the connection to the database ()");
			e.printStackTrace();
			throw e;
		}
	}
	
	private static void loadConfigure(){
		try {
			String path = System.getProperty("user.dir")
					+ "/conf/DBConfig.properties";
			Properties props = new Properties();
			props.load(new FileInputStream(path));
			url = props.getProperty("DBUrl");
			user = props.getProperty("DBUser");
			pwd = props.getProperty("DBPassword");
			System.out.println(url+" "+user+" "+pwd);
			logger.info(url+" "+user+" "+pwd);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("File Not Found Exception");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Can't get the connection to the database ()");
		}
	}
	
	public static boolean persistJob(Job job){
		String jobId = job.getJobID();
		String serviceName = job.getServiceName();
		Date now = new Date();
		String serviceId = job.getServiceID();
		
		logger.info("Persist the job's result information record:¡¡" + jobId);
		String sql = "insert into job values(";
		sql += "'" + jobId + "',";
		sql += "'" + serviceName + "',";
		sql += "'" + serviceId + "',";
		sql += now.getTime() + ")";
		
		
		try {
			
			con.setAutoCommit(false);
			
			Statement st = con.createStatement();
			st.addBatch(sql);
			
			
			st.executeBatch();
			
			con.commit();
			con.setAutoCommit(true);
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't persist the Execute Result");
			return false;
		}
	}
	
	public static boolean persistMonitorRecord(MonitorRecord record){
		
		if(con == null){
//			logger.warn("The connection to the database (" + url + ") is null");
			return false;
		}
		
		boolean isResult = record.isResult();
		if(isResult){
			logger.info("Trying to persist bpmn execute result...");
			return persitResult(record);
		}else{
			logger.info("Trying to persist bpmn monitor record...");
			return persistMonitor(record);
		}
	}
	
	private static boolean persitResult(MonitorRecord record){
		
		String jobId = record.getJobId();
		boolean isSuc = record.isSuccessful();
		String serviceName = record.getServiceName();
		Date now = new Date();
		List<Parameter> ps = record.getParameters();
		
		logger.info("Persist the job's result information record:¡¡" + jobId);
		String sql = "insert into execute_result values(";
		sql += "'" + jobId + "',";
		sql += isSuc + ",";
		sql += "'the description is null now',";
		sql += now.getTime() + ")";
		
		
		try {
			con.setAutoCommit(false);
			
			Statement st = con.createStatement();
			st.addBatch(sql);
			if(ps != null){
				for(Parameter p : ps){
					String pSql = "insert into result_parameter values(";
					pSql += "'" + jobId + "',";
					pSql += "'" + p.getParameterName() + "',";
					pSql += "'" + p.getParameterValue() + "',";
					pSql += "'" + p.getParameterType() + "')";
					logger.info("Insert the bpmn execute result: " + pSql);
					st.addBatch(pSql);
				}
			}
			
			
			st.executeBatch();
			
			con.commit();
			con.setAutoCommit(true);
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't persist the Execute Result");
			return false;
		}
		
		
	}
	
	private static boolean persistMonitor(MonitorRecord record){
		String jobId = record.getJobId();
		boolean isSuc = record.isSuccessful();
		String serviceName = record.getServiceName();
		Date now = new Date();
		List<Parameter> ps = record.getParameters();
		String nodeId = record.getNodeId();
		
		
		logger.info("Persist the job's monitor information record:¡¡" + jobId);
		String sql = "insert into monitor_record values(";
		sql += "'" + jobId + "',";
		sql += "'" + nodeId + "',";
		sql += record.getNodeStatus() + ",";
		sql += "'" + record.getStatusDesp() + "',";
		sql += isSuc + ",";
		sql += now.getTime() + ")";
		
		try {
			con.setAutoCommit(false);
			
			Statement st = con.createStatement();
			st.addBatch(sql);
			
			if(ps != null){
				logger.info("There are " + ps.size() + "parameters to be inserted");
				for(Parameter p : ps){
					String pSql = "insert into monitor_parameter values(";
					pSql += "'" + jobId + "',";
					pSql += "'" + nodeId + "',";
					pSql += "'" + p.getParameterName() + "',";
					pSql += "'" + p.getParameterValue() + "',";
					pSql += "'" + p.getParameterType() + "')";
					logger.info("Insert the bpmn execute result: " + pSql);
					st.addBatch(pSql);
				}
			}
			
			
			st.executeBatch();
			
			con.commit();
			con.setAutoCommit(true);
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't persist the Execute Result", e);
			return false;
		}
	}
	
	public static void close() throws SQLException{
		if(con != null){
			con.close();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		DataBaseUtils u=new DataBaseUtils();
//		u.persistJob("545");
		init();
	}
}
