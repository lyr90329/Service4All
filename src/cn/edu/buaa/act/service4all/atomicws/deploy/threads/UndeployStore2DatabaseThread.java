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
package cn.edu.buaa.act.service4all.atomicws.deploy.threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.atomicws.deploy.DeployComponent;
import cn.edu.buaa.act.service4all.atomicws.deploy.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.UndeployReplica;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;

public class UndeployStore2DatabaseThread implements Runnable {

	private static final Log logger = LogFactory
			.getLog(UndeployStore2DatabaseThread.class);

	String updateERP = DeployConstants.getInstance().getUpdateEpr();
	private String type;
	
	private String serviceID;
	  private String serviceName;

	  private List<UndeployReplica> unDeployedReplica = new ArrayList<UndeployReplica>();
	  private List<UndeployReplica> toUndeployReplica = new ArrayList<UndeployReplica>();

	  private long timeCost;


	@SuppressWarnings("unchecked")
	public UndeployStore2DatabaseThread (ExchangeContext context) {
		this.type = (String) context.getData(DeployConstants.TYPE);
		serviceID = (String) context.getData(DeployConstants.SERVICEID);
		serviceName = (String) context.getData(DeployConstants.SERVICENAME);
		unDeployedReplica = (List<UndeployReplica>)context.getData(DeployConstants.UNDEPLOYEDREPLICA);
		toUndeployReplica = (List<UndeployReplica>) context.getData(DeployConstants.TOUNDEPLOYREPLICA);
		timeCost = System.currentTimeMillis() - (Long)context.getData(DeployConstants.STARTTIME);
	}

	@Override
	public void run() {
		logger.info("Write undeployment result into database");
		Statement statement = null;
		Connection conn = null;
		String sql = null;

		int successUndeployNum = unDeployedReplica.size();
		int failedUndeployNum = toUndeployReplica.size();

		try {
			conn = ConnectionPool.getInstance().getConnection();
			statement = conn.createStatement();
			if (type.equals(DeployConstants.UNDEPLOY)) {
				if (failedUndeployNum == 0) {
					logger.info("Successfully undeployed.");
					sql = "delete from repetition where ServiceId='"
						+ serviceID + "'";
				    statement.executeUpdate(sql);
				
					sql = "delete from webservice where ServiceId='"
							+ serviceID + "'";
					statement.executeUpdate(sql);
					
				} else {	
					logger.info("Undeploy failed. Delete the released replica");
					sql = "update webservice set RepetitionNum = "
							+ failedUndeployNum + " where ServiceId='"
							+ serviceID + "'";
					statement.executeUpdate(sql);
					deleteRepetition(statement);
				}

			}else{
				sql = "update webservice set RepetitionNum = RepetitionNum - "
					+ successUndeployNum + " where ServiceId='" + serviceID
					+ "'";
			statement.executeUpdate(sql);
			deleteRepetition(statement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
		if (DeployComponent.getState()){
			update();
		}
	}

//	private void showUndpeloyedReplica() {
//		logger.info("Print the undeployed or released replica list£º");
//		int length = unDeployedReplica.size();
//		UndeployReplica tmp = null;
//		for (int i = 0; i < length; i++) {
//			tmp = unDeployedReplica.get(i);
//			logger.info("ID:" + tmp.getContainerID() + " Url:" + tmp.getUndeployUrl());
//		}		
//	}

	private void update() {
		logger.info("Notificate the web service query comp...");
		OMElement requestOmElement = DocsBuilder.buildUndeployUpdateOM(serviceID, serviceName, timeCost, unDeployedReplica);
		
		Options options = buildOptions();
		options.setTo(new EndpointReference(updateERP));
		ServiceClient sc;
		try {
			sc = new ServiceClient();
			sc.setOptions(options);
			sc.sendReceive(requestOmElement);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
	}
	private static Options buildOptions() {

		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

		options.setProperty(Constants.Configuration.ENABLE_MTOM,
				Constants.VALUE_TRUE);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		return options;
	}

//	private void releaseRepetition(Statement statement) throws SQLException {
//		String invokeUrl = null;
//		String sql = null;
//		for (Iterator<UndeployReplica> it = unDeployedReplica.iterator();it.hasNext();) {
//			invokeUrl = it.next()
//					.getUndeployUrl().replaceFirst("SAManageService", serviceName);
//			sql = "update repetition set Status='idle' where InvokeUrl = '"+invokeUrl+"'";
//			statement.executeUpdate(sql);
//		}
//	}

	private void deleteRepetition(Statement statement) throws SQLException {
		String invokeUrl = null;
		String sql = null;
		for (Iterator<UndeployReplica> it = unDeployedReplica.iterator();it.hasNext();) {
			invokeUrl = it.next().getUndeployUrl().replaceFirst("SAManageService", serviceName);

			sql = "delete from repetition where InvokeUrl ='"
					+ invokeUrl + "'";
			statement.executeUpdate(sql);
		}
	}
}
