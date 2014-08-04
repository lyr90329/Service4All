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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.atomicws.deploy.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import sdp.servicecloud.axis2.sandbox.analyzer.utils.Sandbox;

public class ServiceDeployReceiver extends Receiver {

	private static int STEP = DeployConstants.getInstance().getStep();
	private static double pFailure = DeployConstants.getInstance().getP();
	private static String checkSandbox = DeployConstants.getInstance()
			.getCheckSandbox();
	public static OMElement sandboxDoc = null;

	@SuppressWarnings("unchecked")
	private ArrayList<String> sandbox(OMElement text, String userName)
			throws Exception {
		Sandbox sb = new Sandbox();
		ArrayList<String> array = new ArrayList<String>();
		array = sb.sandboxIner(text, userName);
		if (sb.getError(array).size() == 0)
			sandboxDoc = sb.getElement();
		return sb.getError(array);
	}

	public OMElement getElement() {
		return sandboxDoc;
	}

	public void handlRequest(Document doc, ExchangeContext context) {

		long startTime = System.currentTimeMillis();
		context.storeData(DeployConstants.STARTTIME, startTime);

		logger.info("Start handling request...");

		Element req = doc.getDocumentElement();

		if (req.getAttribute(DeployConstants.TYPE).equals(
				DeployConstants.REPLICAACQUISITION)) {
			handleReplicaAcquistion(req, context);
		} else if (req.getNodeName() != null
				&& req.getNodeName().equals(DeployConstants.QUERYACTIVATE)) {
			handleQueryRequest(req, context);
		} else {
			handleDeployRequest(req, context);
		}
	}

	private void handleReplicaAcquistion(Element req, ExchangeContext context) {
		logger.info("Handling replica acquisition request");

		context.storeData(DeployConstants.TYPE,
				DeployConstants.REPLICAACQUISITION);
		String serviceID = req.getElementsByTagName(DeployConstants.SERVICEID)
				.item(0).getTextContent();
		context.storeData(DeployConstants.SERVICEID, serviceID);
		
		String userName = req.getElementsByTagName("userName").item(0)
				.getTextContent();
		context.storeData(DeployConstants.USERNAME, userName);
		
		try {
			Connection conn = ConnectionPool.getInstance().getConnection();
			Statement statement = conn.createStatement();
			String sql = "select Name,RepetitionNum,serviceName from webservice where ServiceId = '"
					+ serviceID + "'";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();

			String fileName = rs.getString("Name");
			context.storeData(DeployConstants.SERVICENAME, fileName);

			int repetitionNum = rs.getInt("RepetitionNum");

			if (repetitionNum < DeployConstants.getInstance()
					.getMaxRepetionNum()) {
				logger.info("Current replica num " + repetitionNum + "<"
						+ DeployConstants.getInstance().getMaxRepetionNum()
						+ ".So, one more replica is needed");
				sql = "select InvokeUrl from repetition where ServiceId='"
						+ serviceID + "'";
				rs = statement.executeQuery(sql);
				rs.next();
				context.storeData(DeployConstants.INVOKEURL,
						rs.getString("InvokeUrl"));
				ConnectionPool.getInstance().releaseConnection(conn);
				context.storeData(DeployConstants.DEPLOYNUM, STEP);
				unit.dispatch(context);
			} else {
				ConnectionPool.getInstance().releaseConnection(conn);

				logger.info("Current replica num is " + repetitionNum + "> ="
						+ DeployConstants.getInstance().getMaxRepetionNum()
						+ ". So, there is no need to add one");
				context.storeData(DeployConstants.ISSUCCESSFUL,
						DeployConstants.FALSE);
				context.storeData(DeployConstants.RESPONSE,
						DocsBuilder.buildReplicaAcquistionResponseDoc(context));
				try {
					sendResponseMessage(context);
				} catch (MessageExchangeInvocationException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void handleDeployRequest(Element req, ExchangeContext context) {
		logger.info("Handling deploy request");
		context.storeData(DeployConstants.TYPE, DeployConstants.DEPLOY);

		String userName = req.getElementsByTagName("userName").item(0)
				.getTextContent();
		context.storeData(DeployConstants.USERNAME, userName);

		String availability = req.getElementsByTagName("availability").item(0)
				.getTextContent();
		int deployNum = calDeployCount(Double.parseDouble(availability));
		context.storeData(DeployConstants.DEPLOYNUM, deployNum);
		;
		String fileName = req.getElementsByTagName("fileName").item(0)
				.getTextContent();
		logger.info("Filename��" + fileName);

		context.storeData(DeployConstants.SERVICENAME,
				fileName.substring(0, fileName.lastIndexOf('.')));

		List<Object> attachments = (ArrayList<Object>) context
				.getData(ATTACHMENT);
		DataHandler dh = (DataHandler) attachments.get(0);
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMText textData = omFactory.createOMText(dh, true);
		try {
			OMElement indoc = omFactory.createOMElement("deployService",
					null);
			OMElement fileNameOM = omFactory.createOMElement("fileName", null);
			fileNameOM.setText(fileName);
			OMElement fileData = omFactory.createOMElement("fileData", null);
			fileData.addChild(textData);
			indoc.addChild(fileNameOM);
			indoc.addChild(fileData);

			if ("true".equals(checkSandbox)) {

				logger.info("Starting sandbox");
				ArrayList<String> al = new ArrayList<String>();
				al = sandbox(indoc, userName);
				String error = "";
				if (al.size() != 0) {
					for (int i = 0; i < al.size(); i++) {
						error += al.get(i) + "\n";
					}
					context.storeData(DeployConstants.RESPONSE, DocsBuilder
							.buildSandboxFailureDoc(error, fileName, userName));
					sendResponseMessage(context);
					return;
				}
				indoc = getElement();

			}
			context.storeData(DeployConstants.REQ4CONTAINER, indoc);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				context.storeData(
						DeployConstants.RESPONSE,
						DocsBuilder
								.buildSandboxFailureDoc(
										"Your input file is not a valid service package",
										fileName, userName));
				sendResponseMessage(context);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (MessageExchangeInvocationException e1) {
				e1.printStackTrace();
			}

			return;
		}
		unit.dispatch(context);
	}

	private void handleQueryRequest(Element req, ExchangeContext context) {
		logger.info("Handling query request from query component");

		String state = req.getAttribute(DeployConstants.ACTIVATED);

		if (state.equals(DeployConstants.TRUE))
			DeployComponent.setState(true);
		else
			DeployComponent.setState(false);

		try {
			Document responseDoc = DocsBuilder
					.buildQueryResponseDoc(DeployComponent.getState());
			context.storeData(DeployConstants.RESPONSE, responseDoc);
			this.sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {

		logger.info("Build response msg for user");
		return (Document) context.getData(DeployConstants.RESPONSE);
	}

	private int calDeployCount(double availability) {
		return (int) logarithm(1 - availability, pFailure) + 1;
	}

	private double logarithm(double value, double base) {
		return Math.log(value) / Math.log(base);
	}

//	public static void main( String[] args ) {
//		ServiceDeployReceiver r = new ServiceDeployReceiver();
//		System.out.println(r.calDeployCount( 0.2 ));
//		System.out.println(r.calDeployCount( 0.9 ));
//		System.out.println(r.calDeployCount( 0.99 ));
//	}
}
