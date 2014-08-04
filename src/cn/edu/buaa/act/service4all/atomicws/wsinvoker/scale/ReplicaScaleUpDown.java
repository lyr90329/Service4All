/**
* Service4All: A Service-oriented Cloud Platform for All about Software Development
* Copyright (C) Institute of Advanced Computing Technology, Beihang University
* Contact: service4all@act.buaa.edu.cn
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3.0 of the License, or any later version. 
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details. 
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*/
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.scale;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.WSInvokerReceiver;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;

public class ReplicaScaleUpDown extends TimerTask{
//	private final static Log logger = LogFactory.getLog(ReplicaScaleUpDown.class);
	Connection con = null;
    Statement stmt = null;
	ResultSet rs = null;
	private static ConnectionPool connectionPool = ConnectionPool.getInstance();
 

    private static final long timeWindow =  WSInvokeConstants.getWSInvokeConstants().getPeriod()/1000;
    private static final int upP =  WSInvokeConstants.getWSInvokeConstants().getUpP();
    private static final int upQ = WSInvokeConstants.getWSInvokeConstants().getUpQ();
    private static final int downP = WSInvokeConstants.getWSInvokeConstants().getDownP();
    private static final int downQ = WSInvokeConstants.getWSInvokeConstants().getDownQ();
    private static final double upFactor = WSInvokeConstants.getWSInvokeConstants().getUpFactor();
    private static final double downFactor = WSInvokeConstants.getWSInvokeConstants().getDownFactor();
    private static final double alpha = WSInvokeConstants.getWSInvokeConstants().getAlpha();
    
    int threshhold = 1000;
	private static EndpointReference deployEPR = new EndpointReference(WSInvokeConstants.getWSInvokeConstants().getDeployEPR());
	private static EndpointReference undeployEPR = new EndpointReference(WSInvokeConstants.getWSInvokeConstants().getUndeployEPR());
    private static Map<String, ServiceInformation> scaleMap = new HashMap<String, ServiceInformation>();
	
	@Override
	public void run() {
		try {	
		    con = connectionPool.getConnection();
		    stmt = con.createStatement();
					
			String selectSql = "select serviceID, AVG(lastTime) from execution_results where (UNIX_TIMESTAMP(NOW())-"+timeWindow+") < UNIX_TIMESTAMP(startDate) group by serviceID";
			rs = stmt.executeQuery(selectSql);
			
			String serviceID = null;
			int lastTime;
			ServiceInformation sInfo = null;
			while(rs.next()){
				serviceID = rs.getString("serviceID");
					if(scaleMap.containsKey(serviceID)){

						lastTime =  rs.getInt("AVG(lastTime)");
						sInfo = scaleMap.get(serviceID);
						upOrDown(lastTime,sInfo);

						
						if(scaleMap.get(serviceID).getCountUpQ() > upQ || scaleMap.get(serviceID).getCountUpP()> upP  ){
							scaleUp(serviceID);
							sInfo.setThreshhold((int) (alpha*sInfo.getThreshhold()+(1-alpha)*lastTime));
						}else if (scaleMap.get(serviceID).getCountDownP() > downP || scaleMap.get(serviceID).getCountDownQ() > downQ ) {
							scaleDown(serviceID);
							sInfo.setThreshhold((int) (alpha*sInfo.getThreshhold()+(1-alpha)*lastTime));
						}
					}else{
						int threshhold = this.threshhold;
						ServiceInformation serviceInformation = new ServiceInformation(threshhold);
						scaleMap.put(serviceID, serviceInformation);
					}
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!= null)
				try {
					rs.close();
					if(stmt!=null) stmt.close();
					if(con!= null) connectionPool.releaseConnection(con);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		

	}

	private void scaleDown(String serviceID) {
		Date startDate = new Date(System.currentTimeMillis());
	
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement element = omFactory.createOMElement("undeployRemoteService",
				null);
		element.addAttribute("type", "replicaRelease", null);
		OMElement idOM = omFactory.createOMElement("serviceID", null);
		idOM.setText(serviceID);
		element.addChild(idOM);
		
		Options options;
		OMElement resp = null;
		boolean isSuccessful = false;
		try {
			options = buildOptions(undeployEPR );
			ServiceClient sc = new ServiceClient();
			sc.setOptions(options);
			resp = sc.sendReceive(element);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
	
		scaleMap.get(serviceID).clearPQ();
		
		isSuccessful = isScaleUpOrDownSuccessful(resp);
		
		if(isSuccessful){
		WSInvokerReceiver.wsdlMap.remove(serviceID);
		}
		
		Thread t = new Thread(new Store2DB("scaledown",serviceID,startDate,isSuccessful));
		t.start();
		
	}
	


	private boolean isScaleUpOrDownSuccessful(OMElement resp) {
        if(resp!= null){
        	QName isSuccessfulQN = new QName("isSuccessufl");
			OMElement isSuccessfulOM = resp.getFirstChildWithName(isSuccessfulQN);
			if(("true").equals(isSuccessfulOM.getText())){
				return true;
			}
		}
		return false;
	}

	private void scaleUp(String serviceID) {
		Date startDate = new Date(System.currentTimeMillis());	
	
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement element = omFactory.createOMElement("deployRemoteService",
				null);
		element.addAttribute("type", "replicaAcquisition", null);
		OMElement idOM = omFactory.createOMElement("serviceID", null);
		idOM.setText(serviceID);
		element.addChild(idOM);
		
		
		Options options;
		OMElement resp = null;
		try {
			options = buildOptions(deployEPR );
			ServiceClient sc = new ServiceClient();
			sc.setOptions(options);
			resp = sc.sendReceive(element);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		
		scaleMap.get(serviceID).clearPQ();
		
        boolean isSuccessful = isScaleUpOrDownSuccessful(resp);
		
		if(isSuccessful){
		WSInvokerReceiver.wsdlMap.remove(serviceID);
		}
				
		Thread t = new Thread(new Store2DB("scaleup",serviceID,startDate,isSuccessful));
		t.start();
	}
	private void upOrDown(int lastTime, ServiceInformation serviceInformation) {
		if(lastTime > serviceInformation.getThreshhold()*upFactor){
			if(lastTime > serviceInformation.getPrevious()){
				serviceInformation.incrementCountUpP();
			}else {
				serviceInformation.setCountUpP(0);
			}
			serviceInformation.incrementCountUpQ();
			serviceInformation.clearDownPQ();
		}else if(lastTime >= serviceInformation.getThreshhold()*downFactor){
			serviceInformation.clearPQ();
		}else{
			if(lastTime < serviceInformation.getPrevious()){
				serviceInformation.incrementCountDownP();
			}else {
				serviceInformation.setCountDownP(0);
			}
			serviceInformation.incrementCountDownQ();
			serviceInformation.clearUpPQ();
		}
	
		serviceInformation.setPrevious(lastTime);
		

		serviceInformation.show();
	}
private Options buildOptions (EndpointReference targetEPR) throws AxisFault {
		
		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setTo(targetEPR);
		options.setProperty(Constants.Configuration.ENABLE_MTOM,
				Constants.VALUE_TRUE);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		return options;
		
	}


}


class Store2DB implements Runnable{
	private static ConnectionPool connectionPool = ConnectionPool.getInstance();
	private long lastTime;
	private String serviceID;
	String tableName;
	private String startTimeString;
	private String isSuccessfulString;

	public Store2DB(String tableName, String serviceID, Date startDate, boolean isSuccessful) {
		 SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 startTimeString = sdFormat.format(startDate);
		 this.tableName = tableName;
		 this.serviceID = serviceID;
		 this.isSuccessfulString = String.valueOf(isSuccessful);
		 lastTime = System.currentTimeMillis() - startDate.getTime();
	}

	@Override
	public void run() {
		Connection connection = connectionPool.getConnection();
	    Statement statement = null;
		try {
			statement = connection.createStatement();
			statement = connection.createStatement();
			String insertSql = "insert into "+tableName+"(serviceID,startTime,lastTime,isSuccessful) VALUES ('"+serviceID+"','"+startTimeString+"','"+lastTime+"','"+isSuccessfulString+"')";
			statement.execute(insertSql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally{
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(connection!= null) connectionPool.releaseConnection(connection);
			
		}
	}
}


class ServiceInformation{
	private int threshhold;
public void setThreshhold(int threshhold) {
		this.threshhold = threshhold;
	}
	private int previous = 0;
	private int countUpP = 0;
	private int countUpQ = 0;
	private int countDownP = 0;
	private int countDownQ = 0;
	
	public ServiceInformation(int threshhold) {
		this.threshhold = threshhold;
		this.previous = threshhold;
	}
	public void clearDownPQ() {
		countDownP = 0;
		countDownQ = 0;
		
	}
	public void clearUpPQ() {
		countUpP = 0;
		countUpQ = 0;		
	}
	public void clearPQ() {
		clearUpPQ();
		clearDownPQ();
	}
	public void incrementCountUpP(){
		countUpP++;
	}
	public void incrementCountUpQ(){
		countUpQ++;
	}
	public void incrementCountDownP(){
		countDownP++;
	}
	public void incrementCountDownQ(){
		countDownQ++;
	}
	public int getCountUpP() {
		return countUpP;
	}
	public void setCountUpP(int countUpP) {
		this.countUpP = countUpP;
	}
	public int getCountUpQ() {
		return countUpQ;
	}
	public void setCountUpQ(int countUpQ) {
		this.countUpQ = countUpQ;
	}
	public int getCountDownP() {
		return countDownP;
	}
	public void setCountDownP(int countDownP) {
		this.countDownP = countDownP;
	}
	public int getCountDownQ() {
		return countDownQ;
	}
	public void setCountDownQ(int countDownQ) {
		this.countDownQ = countDownQ;
	}
	public int getThreshhold() {
		return threshhold;
	}
	public int getPrevious() {
		return previous;
	}
	public void setPrevious(int previous) {
		this.previous = previous;
	}
    void show() {
    	System.out.println("-------------------------------");
		System.out.println("threshold:\t"+threshhold+"\tprevious:\t"+previous);
		System.out.println("countUpP:\t"+countUpP+"\tcountUpQ:\t"+countUpQ);
		System.out.println("countDownP:\t"+countDownP+"\tcountDownQ:\t"+countDownQ);
		System.out.println("-------------------------------");
	}
}

