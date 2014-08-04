/*
*
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
*
*/
package cn.edu.buaa.act.service4all.core.samanager.query;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceUtils;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;

public class ApplianceQueryReceiver extends Receiver {

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		String type = (String) context
				.getData( ApplianceQueryBusinessUnit.QUERY_TYPE );
		if (type == null) {
			logger.warn( "Miss the appliance query type when send response message from the receiver" );
			throw new MessageExchangeInvocationException(
					"Miss the appliance query type when send response message from the receiver" );
		}

		@SuppressWarnings("unchecked")
		List<Appliance> queryResults = (List<Appliance>) context
				.getData( ApplianceQueryBusinessUnit.QUERY_RESULT );
		if (queryResults == null) {
			logger.warn( "Miss the appliance query result when send response message from the receiver" );
			throw new MessageExchangeInvocationException(
					"Miss the appliance query result when send response message from the receiver" );
		}

		try {

			return buildResponseMessageByType( type, queryResults );

		} catch (ApplianceException e) {

			logger.warn( e.getMessage() );
			throw new MessageExchangeInvocationException( e.getMessage() );
		}
	}


	/**
	 * 根据之前查询的类型来创建响应报文
	 * 
	 * @param type
	 * @param queryResults
	 * @return
	 */
	private Document buildResponseMessageByType( String type,
			List<Appliance> queryResults ) throws ApplianceException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resp = builder.newDocument();

			if (type.equalsIgnoreCase( "all" )) {

				// build the response message for the query type of all
				buildRespMsgForAll( queryResults, resp );

			} else if (type.equalsIgnoreCase( "host" )) {

				// build the response message for the query type of host
				buildRespMsgForHost( queryResults, resp );

			} else if (type.equalsIgnoreCase( "axis2" )) {

				// build the response message for the query type of axis2
				buildRespMsgForAxis2( queryResults, resp );

			} else if (type.equalsIgnoreCase( "appserver" )) {

				// build the response message for the query type of Tomcat
				buildRespMsgForTomcat( queryResults, resp );
			} else {
				logger.warn( "The querying appliance type is unknown: " + type );
				throw new ApplianceException(
						"The querying appliance type is unknown: " + type );
			}

			return resp;

		} catch (ParserConfigurationException e) {
			logger.warn( e.getMessage() );
			return null;
		}
	}


	private void buildRespMsgForAll( List<Appliance> hosts, Document doc ) {

		Element root = doc.createElement( "queryApplianceResponse" );
		root.setAttribute( "type", "all" );
		doc.appendChild( root );

		Element hostsEle = doc.createElement( "hosts" );
		root.appendChild( hostsEle );

		if (hosts == null || hosts.size() <= 0) {
			logger.info( "There is no host registered in the app engine!" );
			hostsEle.setAttribute( "num", "0" );
			return;
		}

		hostsEle.setAttribute( "num", String.valueOf( hosts.size() ) );
		for (Appliance a : hosts) {

			if (a instanceof Host) {
				Host h = (Host) a;

				Element hEle = doc.createElement( "host" );
				hostsEle.appendChild( hEle );

				String hostId = h.getDesp().getId();
				hEle.setAttribute( "id", hostId );
				hEle.setAttribute( "timestamp", String.valueOf( Calendar
						.getInstance().getTimeInMillis() ) );
				float cpu = h.getStatus().getCpuRate();
				hEle.setAttribute( "cpu", String.valueOf( cpu ) );
				float memory = h.getStatus().getMemoryfloat();
				hEle.setAttribute( "memory", String.valueOf( memory / 1000.0 ) );

				int childApplianceNum = 0;

				Iterator<List<Appliance>> lists = h.getChildAppliances()
						.values().iterator();
				while (lists.hasNext()) {

					List<Appliance> l = lists.next();
					for (Appliance child : l) {
						try {

							Element subAppEle = doc
									.createElement( "subAppliance" );
							hEle.appendChild( subAppEle );

							String childId = child.getDesp().getId();

							String applianceType = ApplianceUtils
									.getApplianceTypeFromId( childId );
							// add the applianceType to the subAppElement
							subAppEle.setAttribute( "type", applianceType );
							subAppEle.setAttribute( "id", childId );
							subAppEle.setAttribute( "appNum", String
									.valueOf( child.getStatus()
											.getDeployedAmount() ) );
							subAppEle.setAttribute( "timestamp", String
									.valueOf( Calendar.getInstance()
											.getTimeInMillis() ) );
							childApplianceNum++;

						} catch (ApplianceException e) {
							logger.warn( e.getMessage() );
						}
					}
				}

				hEle.setAttribute( "childApplianceNum",
						String.valueOf( childApplianceNum ) );

			} else {
				logger.warn( "The queryed appliance is not a host: "
						+ a.getDesp().getId() );
				// ignore this condition
			}
		}
	}


	private void buildRespMsgForHost( List<Appliance> queryResults, Document doc )
			throws DOMException, ApplianceException {

		Element root = doc.createElement( "queryApplianceResponse" );
		doc.appendChild( root );
		root.setAttribute( "type", "host" );

		Element hostsEle = doc.createElement( "hosts" );
		root.appendChild( hostsEle );

		if (queryResults == null || queryResults.size() <= 0) {
			logger.warn( "There is no hosts registered in the appengine" );
			hostsEle.setAttribute( "num", "0" );
			return;
		}
		int hostNum = 0;

		for (Appliance a : queryResults) {

			if (a instanceof Host) {
				Host h = (Host) a;
				String applianceId = h.getDesp().getId();

				Element hEle = doc.createElement( "host" );
				hEle.setAttribute( "id", applianceId );
				hEle.setAttribute( "childApplanceNum",
						String.valueOf( h.getStatus().getDeployedAmount() ) );
				hEle.setAttribute( "timestamp", String.valueOf( Calendar
						.getInstance().getTimeInMillis() ) );
				hEle.setAttribute( "type",
						ApplianceUtils.getApplianceTypeFromId( applianceId ) );
				hostsEle.appendChild( hEle );

				float cpu = h.getStatus().getCpuRate();
				hEle.setAttribute( "cpu", String.valueOf( cpu ) );
				float memory = h.getStatus().getMemoryfloat();
				hEle.setAttribute( "memory", String.valueOf( memory / 1000.0 ) );

				hostNum++;

			} else {
				logger.warn( "The target appliance is not a host: "
						+ a.getDesp().getId() );
				// ignore this appliance
			}

		}

		hostsEle.setAttribute( "num", String.valueOf( hostNum ) );
	}


	private void buildRespMsgForAxis2( List<Appliance> queryResults,
			Document doc ) {

		Element root = doc.createElement( "queryApplianceResponse" );
		doc.appendChild( root );

		root.setAttribute( "type", "axis2" );

		Element axis2sEle = doc.createElement( "axis2s" );
		root.appendChild( axis2sEle );

		if (queryResults == null || queryResults.size() <= 0) {
			logger.warn( "There is no axis2s registered in the appengine" );
			axis2sEle.setAttribute( "num", "0" );
			return;
		}
		int axis2Num = 0;

		for (Appliance a : queryResults) {

			String applianceId = a.getDesp().getId();

			Element aEle = doc.createElement( "axis2" );
			aEle.setAttribute( "id", applianceId );
			aEle.setAttribute( "appNum",
					String.valueOf( a.getStatus().getDeployedAmount() ) );
			aEle.setAttribute( "timestamp", Calendar.getInstance().toString() );

			axis2sEle.appendChild( aEle );

			axis2Num++;
		}

		axis2sEle.setAttribute( "num", String.valueOf( axis2Num ) );
	}


	private void buildRespMsgForTomcat( List<Appliance> queryResults,
			Document doc ) {

		Element root = doc.createElement( "queryApplianceResponse" );
		doc.appendChild( root );

		root.setAttribute( "type", "appserver" );

		Element tomcatsEle = doc.createElement( "appservers" );
		doc.appendChild( tomcatsEle );

		if (queryResults == null || queryResults.size() <= 0) {
			logger.warn( "There is no tomcats registered in the appengine" );
			tomcatsEle.setAttribute( "num", "0" );
			return;
		}

		int tomcatNum = 0;

		for (Appliance a : queryResults) {

			String applianceId = a.getDesp().getId();

			Element aEle = doc.createElement( "appserver" );
			aEle.setAttribute( "id", applianceId );
			aEle.setAttribute( "appNum",
					String.valueOf( a.getStatus().getDeployedAmount() ) );
			aEle.setAttribute( "timestamp", Calendar.getInstance().toString() );

			tomcatsEle.appendChild( aEle );

			tomcatNum++;
		}

		tomcatsEle.setAttribute( "num", String.valueOf( tomcatNum ) );

	}


	@Override
	public void handlRequest( Document req, ExchangeContext context )
			throws MessageExchangeInvocationException {

		logger.info( "Get the query reuqest message : "
				+ XMLUtils.retrieveDocumentAsString( req ) );
		Element root = req.getDocumentElement();
		String type = root.getAttribute( "type" );

		if (type == null) {
			logger.warn( "Miss the type attribute for the appliance query request!" );
			throw new MessageExchangeInvocationException(
					"Miss the type attribute for the appliance query request!" );
		}

		context.storeData( ApplianceQueryBusinessUnit.QUERY_TYPE, type );
		this.unit.dispatch( context );
	}


	protected boolean validate( Document req ) {
		return true;
	}

}
