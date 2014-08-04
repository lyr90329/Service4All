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
import java.util.List;

import javax.xml.stream.XMLStreamException;

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
import cn.edu.buaa.act.service4all.atomicws.deploy.loadbalance.ContainerReplica;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;

/**
 * @author linjj
 */
public class DeployStore2DatabaseThread implements Runnable {
	private static final Log logger = LogFactory
			.getLog(DeployStore2DatabaseThread.class);

	private static String type;
	private String serviceID;
	static String updateERP = DeployConstants.getInstance()
			.getUpdateEpr();
	private String serviceName;
	private long timeCost;
	private List<ContainerReplica> deployedResults = new ArrayList<ContainerReplica>();
	private int deployNum = 0;
	private String ownerName = null;

	private String fileName;

	@SuppressWarnings("unchecked")
	public DeployStore2DatabaseThread(ExchangeContext context) {

		type = (String) context.getData(DeployConstants.TYPE);
		if (type.equals(DeployConstants.DEPLOY)) {
			deployNum = (Integer) context
					.getData(DeployConstants.DEPLOYNUM);
			ownerName = (String) context
					.getData(DeployConstants.USERNAME);
		}
		serviceID = (String) context.getData(DeployConstants.SERVICEID);
		serviceName = (String) context
				.getData(DeployConstants.SERVICENAME);

		fileName = (String) context
				.getData(DeployConstants.SERVICENAME);
		deployedResults = (List<ContainerReplica>) context
				.getData(DeployConstants.DEPLOYEDRESULTS);

		timeCost = System.currentTimeMillis()
				- (Long) context.getData(DeployConstants.STARTTIME);
	}

	@Override
	public void run() {
		logger.info("Insert deploy result into database");

		Statement statement = null;
		Connection conn = null;
		String sql = null;
		int successDeployNum = deployedResults.size();
		if (successDeployNum > 0) {
			try {
				conn = ConnectionPool.getInstance().getConnection();
				statement = conn.createStatement();
			
				ContainerReplica tempReplica = null;
				String invokeUrl = null;
				//Emma
				String url = null;

				if (type.equals(DeployConstants.DEPLOY)) {
					
					sql = "insert into webservice (ServiceId,Name,RepetitionNum,Timecost,MinDeployNum,Owner,ServiceName) values('"
							+ serviceID
							+ "','"
							+ fileName
							+ "',"
							+ successDeployNum
							+ ","
							+ timeCost
							+ ","
							+ deployNum
							+ ",'"
							+ ownerName
							+ "','"
							+ serviceName + "')";
				} else {
					sql = "update webservice set RepetitionNum = RepetitionNum + "
							+ successDeployNum
							+ " where ServiceId='"
							+ serviceID + "'";//
				}
				statement.executeUpdate(sql);

				for (int i = 0; i < successDeployNum; i++) {

					tempReplica = deployedResults.get(i);
					
					//Emma
					url = tempReplica.getContainerId().split("_")[1];
					invokeUrl = url + "/axis2/services/" + serviceName + "/";
					
//					invokeUrl = tempReplica.getdeployUrl().replaceFirst(
//							"localSAManagerService", serviceName);
					sql = "insert into repetition (ServiceId,InvokeUrl,Cpu,Memory,Throughput) values('"
							+ serviceID
							+ "','"
							+ invokeUrl
							+ "',"
							+ tempReplica.getCpu()
							+ ","
							+ tempReplica.getMemeory()
							+ ","
							+ tempReplica.getThroughput() + ")";
					statement.executeUpdate(sql);
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

			if (DeployComponent.getState()) {
				update();
			}
		}

	}


	private void update() {
		logger.info("Notificating web service query comp...");
		OMElement requestOmElement = DocsBuilder.buildDeployUpdateOM(serviceID,
				serviceName, timeCost, deployedResults);

		Options options = buildOptions();
		options.setTo(new EndpointReference(updateERP));
		ServiceClient sc;
		try {
			sc = new ServiceClient();
			sc.setOptions(options);
			OMElement response = sc.sendReceive(requestOmElement);
			response.serialize(System.out);
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
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

//	private void showResultDeployedInfo() {
//		logger.info("Deployment containers list£º");
//		int length = deployedResults.size();
//		ContainerReplica tmp = null;
//		for (int i = 0; i < length; i++) {
//			tmp = deployedResults.get(i);
//			logger.info("ID:" + tmp.getContainerId() + " Url:"
//					+ tmp.getdeployUrl());
//
//		}
//	}

}
