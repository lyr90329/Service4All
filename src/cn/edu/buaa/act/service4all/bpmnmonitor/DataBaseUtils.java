package cn.edu.buaa.act.service4all.bpmnmonitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataBaseUtils {
	
	private static Log logger = LogFactory.getLog(DataBaseUtils.class);
	
	private static Connection con;
	
	private static AtomicBoolean isReady = new AtomicBoolean(false);
	
	private static String url = "jdbc:mysql://124.205.18.184:3306/cloud_monitor";
	private static String user = "root";
	private static String pwd = "sdp123";
	
	public static void init() throws ClassNotFoundException, SQLException{
//		String url = "jdbc:mysql://192.168.7.162:3306/cloud_monitor";
//		String user = "root";
//		String pwd = "sdp123";
		logger.info("Initiate the connection to the database ()");
		
		resolve();
		Class.forName("com.mysql.jdbc.Driver");
		
		try {
			con = DriverManager.getConnection(url, user, pwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't get the connection to the database ()");
			throw e;
		}
		
		isReady.set(true);
	}
	
	private static void resolve(){
		try {
			String path = System.getProperty("user.dir")
					+ "/conf/DBConfig.properties";
			Properties props = new Properties();
			props.load(new FileInputStream(path));
			url = props.getProperty("DBUrl");
			user = props.getProperty("DBUser");
			pwd = props.getProperty("DBPassword");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("File Not Found Exception");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Can't get the connection to the database ()");
		}
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			
//			InputStream is = DataBaseUtils.class.getResourceAsStream(
//					"/cn/edu/buaa/act/service4all/bpmnmonitor/DatabaseConfig.xml");
//
//			Document out = builder.parse(is);
//
//			Element webService = (Element) (out.getDocumentElement()
//					.getElementsByTagName("config").item(0));		
//			url = webService.getElementsByTagName(
//					"url").item(0).getTextContent();
//			user = webService.getElementsByTagName(
//					"user").item(0).getTextContent();
//			pwd = webService.getElementsByTagName(
//					"password").item(0).getTextContent();
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
	}
	
	public static void testInit(){
		if(con == null){
			try {
				new DataBaseUtils().init();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.warn("Can't initiate the mysql connection", e);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.warn("Can't initiate the mysql connection", e);
			}
		}
	}
	
	public static void close() throws SQLException{
		if(con != null){
			con.close();
		}
		
		isReady.set(false);
	}

	public static boolean isReady(){
		return isReady.get();
	}
	
	/**
	 * query the job by its id
	 * @param jobId
	 * @return
	 */
	public static Job getJobById(String jobId){
		
		logger.info("Query the target job by its id : " + jobId);
		testInit();
		Statement st = null;
		try {
			st = con.createStatement();
			String querySql = "select * from job where jobId = '" + jobId +"'";
			ResultSet rs = st.executeQuery(querySql);
			
			if(rs == null){
				logger.warn("Can't get result set after query the database for the job: " + jobId);
				return null;
			}
			
			//get the job data from statement
			while(rs.next()){
				Job job = new Job();
				job.setJobId(jobId);
				
				String serviceName = rs.getString("serviceName");
				String serviceId = rs.getString("serviceId");
				Long tl = rs.getLong("time_long");
				
				job.setServiceName(serviceName);
				job.setServiceId(serviceId);
				if(tl != null){
					job.setTimestamp(new Date(tl));
				}
				
				return job;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't query the target job by its id: " + jobId);
			return null;
			
		}finally{
			if(st != null){ 
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.warn("Can't close the statement for the query by its id :" + jobId);
					
				}
			}
		}
		
		return null;
	}
	
	public static List<MonitorRecord> getMonitorRecordByJobId(String jobId){
		
		logger.info("Query the monitor record for the job: " + jobId);
		testInit();
		Statement st = null;
		try {
			
			st = con.createStatement();
			String querySql = "select * from monitor_record where jobId = '" + jobId +"'";
			ResultSet rs = st.executeQuery(querySql);
			
			int count = 0;
			List<MonitorRecord> records = new ArrayList<MonitorRecord>();
			while(rs.next()){
				
				MonitorRecord record = new MonitorRecord();
				
				String nodeId = rs.getString("nodeId");
				boolean status = rs.getBoolean("nodeStatus");
				String desp = rs.getString("nodeDesp");
				boolean isError = rs.getBoolean("isError");
				Long time_long = rs.getLong("time_long");
				
				record.setNodeId(nodeId);
				record.setNodeStatus(status);
				record.setNodeDesp(desp);
				record.setError(isError);
				if(time_long != null){
					record.setTimestamp(new Date(time_long));
				}
				
				//query the parameter list for the node 
				List<Parameter> ps = queryParameters(jobId, nodeId);
				record.setPs(ps);
				
				count++;
				records.add(record);
				
			}
			logger.info("Get " + records.size() + "records for monitor!");
			return records;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't query the target job monitor record by its id: " + jobId, e);
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param jobId
	 * @param nodeId
	 * @return
	 */
	private static List<Parameter> queryParameters(String jobId, String nodeId){
		
		logger.info("Query the job monitor record for the job( " + jobId +" ) and the node( " + nodeId + " )");
		
		Statement st = null;
		try {
			
			st = con.createStatement();
			
			String querySql = "select * from monitor_parameter where jobId='" 
								+ jobId + "' and nodeId='" + nodeId +"'";
			
			
			ResultSet rs = st.executeQuery(querySql);
			if(rs == null){
				logger.warn("Can't query the monitor record for the job( " + jobId +" ) and the node( " + nodeId + " )");
				return null;
			}
			
			List<Parameter> ps = new ArrayList<Parameter>();
			while(rs.next()){
				//get the parameter message
				Parameter p = new Parameter();
				String paraName = rs.getString("parameterName");
				String paraValue = rs.getString("parameterValue");
				String paraType = rs.getString("parameterType");
				
				p.setParameterType(paraType);
				p.setParameterName(paraName);
				p.setParameterValue(paraValue);
				
				ps.add(p);
				
			}
			
			return ps;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("Can's query parameters : " + e.getMessage());
		}
		
		return null;
	}
	
//	public MonitorRecord getLatestRecordByJobId(String jobId){
//		
//	}
	
	/**
	 * if no finished, return null
	 * 
	 * @param jobId
	 * @return
	 */
	public static ResultRecord getResultRecordByJobId(String jobId){
		logger.info("Query the execute result for the job: " + jobId);
		testInit();
		Statement st = null;
		try {
			
			st = con.createStatement();
			String querySql = "select * from execute_result where jobId = '" + jobId +"'";
			ResultSet rs = st.executeQuery(querySql);
			
			int count = 0;
			
			while(rs.next()){
				
				ResultRecord record = new ResultRecord();
				
				
				boolean status = rs.getBoolean("isSuccessful");
				String desp = rs.getString("desp");
				
				Long time_long = rs.getLong("time_long");
				
				
				record.setSuccessful(status);
				record.setDesp(desp);
				
				if(time_long != null){
					record.setTimestamp(new Date(time_long));
				}
				
				//query the parameter list for the node 
				List<Parameter> ps = getResultParameters(jobId);
				record.setPs(ps);
				
				count++;
				
				return record;
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't query the target job monitor record by its id: " + jobId, e);
			e.printStackTrace();
		
		}
		return null;
	}
	
	private static List<Parameter> getResultParameters(String jobId){
		logger.info("Query the job execute result for the job( " + jobId +" )");
		
		Statement st = null;
		try {
			
			st = con.createStatement();
			
			String querySql = "select * from result_parameter where jobId='" 
								+ jobId + "'";
			
			
			ResultSet rs = st.executeQuery(querySql);
			if(rs == null){
				logger.warn("Can't query the monitor record for the job( " + jobId +" )");
				return null;
			}
			
			List<Parameter> ps = new ArrayList<Parameter>();
			while(rs.next()){
				//get the parameter message
				Parameter p = new Parameter();
				String paraName = rs.getString("parameterName");
				String paraValue = rs.getString("parameterValue");
				String paraType = rs.getString("parameterType");
				
				p.setParameterType(paraType);
				p.setParameterName(paraName);
				p.setParameterValue(paraValue);
				
				ps.add(p);
				
			}
			
			return ps;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("Can's query execute result parameters : " + e.getMessage());
		}
		
		return null;
	}
	
	public static void main(String[] args){
		
//		DataBaseUtils.testInit();
		resolve();
		System.out.println(url);
//		DataBaseUtils utils=new DataBaseUtils();
//		
//		List list=utils.getMonitorRecordByJobId("545");
//		System.out.println(list.size());
//		System.out.println(list.get(0));
	}
}
