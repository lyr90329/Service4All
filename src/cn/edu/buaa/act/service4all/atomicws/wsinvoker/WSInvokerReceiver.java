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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.WSInvokerReceiver;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.database.ConnectionPoolForDeployUndeploy;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.thread.Store2DatabaseThread;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.ServiceWSDLs;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.TransformerUtils;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSInvokerReceiver extends Receiver {
	private static final Log logger = LogFactory
			.getLog(WSInvokerReceiver.class);
	private static final WSInvokeConstants constants = WSInvokeConstants
			.getWSInvokeConstants();
	private static final boolean roundRobin = constants.isRoundRobin();

	private static final ConnectionPoolForDeployUndeploy conPool = ConnectionPoolForDeployUndeploy
			.getInstance();


	public static Map<String, ServiceWSDLs> wsdlMap = new HashMap<String, ServiceWSDLs>();

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("Response for user");
		Document doc = null;
		OMElement response = (OMElement) context
				.getData(WSInvokeConstants.RESPONSESOAP);
		if (response != null) {
			try {
				doc = TransformerUtils.omToDoc(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (((String) context.getData("isEngine")).equals("false"))
			return doc;
		else {
			Element root = doc.createElement("WebServiceInvokeResponse");
			String ret = doc.getDocumentElement().getFirstChild()
					.getTextContent();
			root.setTextContent(ret);
			doc.removeChild(doc.getDocumentElement());
			doc.appendChild(root);
			return doc;
		}

	}

	@Override
	public void handlRequest(Document inDoc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		long startTime = System.currentTimeMillis();
		context.storeData(WSInvokeConstants.STARTTIME, startTime);

		logger.info(TransformerUtils.getStringFromDocument(inDoc));

		 {
		
		 logger.info("Revise Doucment:'&lt;'-->'<'");

		 inDoc = TransformerUtils.getDocumentFromString(TransformerUtils
		 .getStringFromDocument(inDoc));
		 }


		Element rootElement = inDoc.getDocumentElement();

		String type = rootElement.getAttribute(WSInvokeConstants.TYPE);
		String operation = rootElement
				.getElementsByTagName(WSInvokeConstants.OPERATION).item(0)
				.getTextContent();
		OMElement requestSoap = WSInvokerUtils.getSoapBody(inDoc);

		context.storeData(WSInvokeConstants.OPERATION, operation);
		context.storeData(WSInvokeConstants.REQUESTSOAP, requestSoap);

		if (type != null && type.equalsIgnoreCase(WSInvokeConstants.WSDL)) {
			logger.info("invoke by wsdl");
			String wsdlUrl = rootElement
					.getElementsByTagName(WSInvokeConstants.WSDL).item(0)
					.getTextContent();
			context.storeData(WSInvokeConstants.WSDL, wsdlUrl);
			invokeWithWSDL(context);
		} else if (type != null
				&& type.equalsIgnoreCase(WSInvokeConstants.SERVICEID)) {
			logger.info("invoke by serviceID");
			String serviceID = rootElement
					.getElementsByTagName(WSInvokeConstants.SERVICEID).item(0)
					.getTextContent();
			context.storeData(WSInvokeConstants.SERVICEID, serviceID);
			
			String isEngine = rootElement.getAttribute("isEngine"); 
			if (isEngine != null && isEngine.endsWith("true"))
				context.storeData("isEngine", "true");
			else
				context.storeData("isEngine", "false");
			
			logger.info((String)context.getData("isEngine"));
			if (roundRobin) {
				invokeRoundRobin(context);
			} else {
				this.unit.dispatch(context);
			}
		}
	}

	
	private void invokeRoundRobin(ExchangeContext context) {

		String serviceID = (String) context
				.getData(WSInvokeConstants.SERVICEID);
		List<String> wsdls = new ArrayList<String>();
		int[] rank = null;
		if (wsdlMap.containsKey(serviceID)) {

			ServiceWSDLs tmp = wsdlMap.get(serviceID);
			wsdls = tmp.getWsdls();
			int repetionNum = tmp.size();
			int index = tmp.getNextIndex();
			int i = 0;
			int j = i + index;
			rank = new int[repetionNum];
			for (; i < repetionNum; i++, j++) {
				rank[j % repetionNum] = i;
			}

		} else {
			logger.info("Read replica list from database");
			Connection conn = conPool.getConnection();
			Statement statement = null;
			String sql = null;
			ResultSet rs = null;
			try {
				statement = conn.createStatement();
				sql = "select InvokeUrl from repetition where ServiceId='"
						+ serviceID + "'";
				rs = statement.executeQuery(sql);
				int i = 0;
				while (rs.next()) {
					wsdls.add(rs.getString("InvokeUrl"));
					i++;
				}
				if (i == 0) {
					String infoString = "Waring! There is no available service for the ID:"
							+ serviceID;
					context.storeData(WSInvokeConstants.RESPONSESOAP,DocsBuilder.buildNoAvailableServiceOMElement(infoString));
					try {
						sendResponseMessage(context);
					} catch (MessageExchangeInvocationException e) {
						e.printStackTrace();
					}
					return;
				} else {
					rank = new int[i];
					for (int j = 0; j < i; j++) {
						rank[j] = j;
					}
					ServiceWSDLs tmp = new ServiceWSDLs(wsdls);
					wsdlMap.put(serviceID, tmp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					conPool.releaseConnection(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		context.storeData(WSInvokeConstants.URLS, wsdls);
		context.storeData(WSInvokeConstants.RANK, rank);
		WSInvokerUtils.invokeWSByID(context);

		try {
			sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}


	private void invokeWithWSDL(ExchangeContext context) {

		String wsdlUrl = (String) context.getData(WSInvokeConstants.WSDL);
		String operation = (String) context
				.getData(WSInvokeConstants.OPERATION);
		OMElement soapBodyOmElement = (OMElement) context
				.getData(WSInvokeConstants.REQUESTSOAP);
		long startTime = (Long) context.getData(WSInvokeConstants.STARTTIME);

		String serviceID = null;
		boolean isSuccessful = false;

		OMElement responseOmElement = null;
		try {
			responseOmElement = WSInvokerUtils.invokeWithSoapRequestAXIOM(
					wsdlUrl, operation, soapBodyOmElement)
					.getResponseOmElement();
			isSuccessful = true;
		} catch (AxisFault e) {
			String exception = e.getMessage();
			OMFactory factory = OMAbstractFactory.getOMFactory();
			responseOmElement = factory.createOMElement("exception", null);
			responseOmElement.setText(exception);

			e.printStackTrace();
		} finally {
			Thread store2DBThread = new Thread(new Store2DatabaseThread(
					serviceID, wsdlUrl, startTime, isSuccessful));
			store2DBThread.start();

			context.storeData(WSInvokeConstants.RESPONSESOAP, responseOmElement);


			try {
				sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
		}
	}


}
