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
package cn.edu.buaa.act.service4all.atomicws.servicequery.availableservices;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.UpdateDocument;
import cn.edu.buaa.act.service4all.atomicws.servicequery.utils.WSQueryUpdateConstants;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class AvailableServicesQueryReceiver extends Receiver {

	private Timer timer = new Timer();

	public void handlRequest(Document doc, ExchangeContext context) {
		timer.cancel();

		Element req = doc.getDocumentElement();
		String type = req.getAttribute("type");
		if ("all".equals(type)) {
			logger.info("Global query");
			try {
				UpdateDocument.getInstance().clear();
				context.storeData(WSQueryUpdateConstants.RESPONSE,
						queryServices());
				context.storeData(WSQueryUpdateConstants.QUERY_STATE,
						String.valueOf(true));
				unit.dispatch(context);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		} else if ("increment".equals(type)) {
			logger.info("increment query");

			context.storeData(WSQueryUpdateConstants.RESPONSE, UpdateDocument
					.getInstance().getDoc());
			UpdateDocument.getInstance().clear();
		} else {
			logger.error("Input msg error£¡");
		}

		try {
			sendResponseMessage(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				DocsBuilder.buildUpdateQueryStateDoc("false");
				System.out
						.println("No need to update for there is no monitor(Monitor is time out).");
				timer.cancel();
			}
		}, WSQueryUpdateConstants.getInstance().getTimeLimit());

		return (Document) context.getData(WSQueryUpdateConstants.RESPONSE);
	}

	private static Document queryServices() throws ParserConfigurationException {

		int num;
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document result = builder.newDocument();
		Element response = result.createElement("queryWebServicesResponse");
		response.setAttribute("type", "all");
		Element services = result.createElement("services");
		try {
			Connection conn = ConnectionPool.getInstance().getConnection();
			Statement statement = conn.createStatement();

			String sql = "select count(*) from webservice";
			ResultSet rs = statement.executeQuery(sql);
			rs.next();
			num = rs.getInt(1);
			services.setAttribute("num", String.valueOf(num));

			sql = "select * from webservice";
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				Element temp = result.createElement("service");
				temp.setAttribute("id",
						String.valueOf(rs.getString("ServiceId")));
				temp.setAttribute("serviceName",
						String.valueOf(rs.getString("Name")));
				temp.setAttribute("timecost",
						String.valueOf(rs.getInt("Timecost")));
				Statement stat2 = conn.createStatement();
				sql = "select * from repetition where ServiceId='"
						+ temp.getAttribute("id") + "' and Status='active'";
				ResultSet rs2 = stat2.executeQuery(sql);
				int sum = 0;
				while (rs2.next()) {
					sum++;
					Element repetition = result.createElement("repetition");
					repetition.setAttribute("invokeUrl",
							rs2.getString("InvokeUrl"));
					repetition.setAttribute("id",
							getId(repetition.getAttribute("invokeUrl")));
					repetition.setAttribute("cpu",
							String.valueOf(rs2.getDouble("Cpu")));
					repetition.setAttribute("memory",
							String.valueOf(rs2.getDouble("Memory")));
					repetition.setAttribute("throughput",
							String.valueOf(rs2.getDouble("Throughput")));
					temp.appendChild(repetition);
				}
				temp.setAttribute("repetitionNum", String.valueOf(sum));
				services.appendChild(temp);
			}
			ConnectionPool.getInstance().releaseConnection(conn);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		response.appendChild(services);
		result.appendChild(response);
		return result;
	}

	private static String getId(String attribute) {
		int i = attribute.lastIndexOf(":");
		return attribute.substring(0, i + 5);
	}

}
