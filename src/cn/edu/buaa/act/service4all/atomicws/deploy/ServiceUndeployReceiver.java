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
package cn.edu.buaa.act.service4all.atomicws.deploy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.deploy.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.UserControl;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ServiceUndeployReceiver extends Receiver {
	private static final DeployConstants constants = DeployConstants
			.getInstance();

	public static final int STEP = constants.getStep();

	@Override
	public void handlRequest(Document doc, ExchangeContext context) {
		long startTime = System.currentTimeMillis();
		context.storeData(DeployConstants.STARTTIME, startTime);

		logger.info("Start processing the request ...");

		Element req = doc.getDocumentElement();
		String serviceID = req.getElementsByTagName(DeployConstants.SERVICEID)
				.item(0).getTextContent();
		context.storeData(DeployConstants.SERVICEID, serviceID);
		
		String userName = req
			.getElementsByTagName(DeployConstants.USERNAME).item(0)
			.getTextContent();
		context.storeData(DeployConstants.USERNAME, userName);
		
		String type = req.getAttribute(DeployConstants.TYPE);
		if (type.equals(DeployConstants.REPLICARELEASE)) {
			try {
				handleReplicaRelease(context);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("Processing undeployment request");
			context.storeData(DeployConstants.TYPE, DeployConstants.UNDEPLOY);
			if ("true".equals(constants.getCheckFb2UserAuthentication())) {
				if (!UserControl.undeployQualify(userName, serviceID)) {

					try {
						Document responseDoc = DocsBuilder
								.buildAuthenticationFailureDoc(serviceID);
						context.storeData(DeployConstants.RESPONSE, responseDoc);
						this.sendResponseMessage(context);
						return;
					} catch (MessageExchangeInvocationException e) {
						e.printStackTrace();
					}
				}
			}

			unit.dispatch(context);

		}
	}

	private void handleReplicaRelease(ExchangeContext context)
			throws SQLException {
		logger.info("Handling replica releasing request...");
		String serviceID = (String) context.getData(DeployConstants.SERVICEID);

		Connection conn = ConnectionPool.getInstance().getConnection();
		Statement statement = null;
		try {
			statement = conn.createStatement();

			String sql = "select * from webservice where ServiceId='"
					+ serviceID + "'";

			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			int minDeployNum = rs.getInt("MinDeployNum");
			int repetitionNum = rs.getInt("RepetitionNum");

			if (repetitionNum > minDeployNum) {
				context.storeData(DeployConstants.TYPE,
						DeployConstants.REPLICARELEASE);
				unit.dispatch(context);

			} else {
				context.storeData(DeployConstants.ISSUCCESSFUL, "false");
				context.storeData(DeployConstants.RESPONSE,
						DocsBuilder.buildReplicaReleaseResponseDoc(context));
				try {
					sendResponseMessage(context);
				} catch (MessageExchangeInvocationException e) {
					e.printStackTrace();
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
			ConnectionPool.getInstance().releaseConnection(conn);

		}
	}

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		return (Document) context.getData(DeployConstants.RESPONSE);
	}

}
