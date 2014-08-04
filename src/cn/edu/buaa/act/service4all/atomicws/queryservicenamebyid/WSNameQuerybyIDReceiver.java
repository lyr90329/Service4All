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
package cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils.Constants;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class WSNameQuerybyIDReceiver extends Receiver {

	private static String driverClassName = Constants.getConstants()
			.getDriverClassName();
	private static String username = Constants.getConstants().getDBUser();
	private static String password = Constants.getConstants().getDBPassword();
	private static String url = Constants.getConstants().getDBUrl();

	public void handlRequest(Document doc, ExchangeContext context) {

		logger.info("Handling the AvailableContainersQueryReceiver request");

		Element root = doc.getDocumentElement();

		if (root.getNodeName() != null
				&& root.getNodeName()
						.equals(Constants.WSQueryRequestByUserName)) {
			logger.info("Query service name and ID by user name");
			String userName = root.getElementsByTagName("userName").item(0)
					.getTextContent();
			context.storeData(Constants.RESPONSEDOC,
					WSQueryRequestByUserName(userName));
			try {
				sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}

		} else {
			logger.info("Query service name and deployed url by service ID");
			String id = root.getElementsByTagName("serviceID").item(0)
					.getTextContent();
			context.storeData(Constants.SERVICEID, id);
			unit.dispatch(context);
		}

	}

	private static Document WSQueryRequestByUserName(String userName) {

		Document doc = null;
		Connection conn = null;
		Statement statement = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element rootElement = doc
					.createElement("WSQueryResponseByUserName");
			rootElement.setAttribute("type", "WebService");

			Element servicesElement = doc.createElement("services");

			Class.forName(driverClassName);
			System.out.println( "2url:"+url+",username"+username+",password"+password );
			conn = java.sql.DriverManager
					.getConnection(url, username, password);
			statement = conn.createStatement();
			String sql = "select ServiceId,Name from webservice where Owner = '"
					+ userName + "'";

			ResultSet rs = statement.executeQuery(sql);
			String serviceID = null;
			String serviceName = null;
			int length = 0;
			while (rs.next()) {
				length++;
				Element serviceElement = doc.createElement("service");
				serviceID = rs.getString("ServiceId");
				serviceName = rs.getString("Name");
				serviceElement.setAttribute("id", serviceID);
				serviceElement.setAttribute("name", serviceName);
				servicesElement.appendChild(serviceElement);
			}
			servicesElement.setAttribute("length", String.valueOf(length));
			rootElement.appendChild(servicesElement);
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return doc;

	}

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {

		return (Document) context.getData(Constants.RESPONSEDOC);
	}

	public static void main( String[] args ) {
		Document doc = null;
		Connection conn = null;
		Statement statement = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element rootElement = doc
					.createElement("WSQueryResponseByUserName");
			rootElement.setAttribute("type", "WebService");

			Element servicesElement = doc.createElement("services");

			Class.forName(driverClassName);
			System.out.println( "2url:"+url+",username"+username+",password"+password );
			conn = java.sql.DriverManager
					.getConnection(url, username, password);
			statement = conn.createStatement();
			String sql = "select ServiceId,Name from webservice where Owner = '"
					+ username + "'";

			ResultSet rs = statement.executeQuery(sql);
			String serviceID = null;
			String serviceName = null;
			int length = 0;
			while (rs.next()) {
				length++;
				Element serviceElement = doc.createElement("service");
				serviceID = rs.getString("ServiceId");
				serviceName = rs.getString("Name");
				serviceElement.setAttribute("id", serviceID);
				serviceElement.setAttribute("name", serviceName);
				servicesElement.appendChild(serviceElement);
			}
			servicesElement.setAttribute("length", String.valueOf(length));
			rootElement.appendChild(servicesElement);
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
