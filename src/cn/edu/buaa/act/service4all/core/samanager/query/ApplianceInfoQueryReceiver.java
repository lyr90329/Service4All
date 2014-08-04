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
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceUtils;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.logging.ApplianceLog;

public class ApplianceInfoQueryReceiver extends Receiver {

	private Log logger = LogFactory.getLog( ApplianceInfoQueryReceiver.class );


	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		String type = (String) context
				.getData( ApplianceInfoQueryBusinessUnit.QUERY_TYPE );
		String applianceId = (String) context
				.getData( ApplianceInfoQueryBusinessUnit.APPLIANCE_ID );
		Appliance appliance = (Appliance) context
				.getData( ApplianceInfoQueryBusinessUnit.QUERY_RESULT );
		@SuppressWarnings("unchecked")
		List<AppReplica> appReps = (List<AppReplica>) context
				.getData( ApplianceInfoQueryBusinessUnit.QUERY_APPS_RESULT );

		if (appliance == null) {

			logger.warn( "The target queried appliance is null: " + applianceId );
			// throw new
			// MessageExchangeInvocationException("The target queried appliance is null!");
			return createExceptionDocument( type, applianceId,
					"The target queried appliance is null: " + applianceId );
		}

		// log the appliance
		ApplianceLog.log( logQuery( type, applianceId, appliance ) );

		try {

			return buildQueryResponseMsg( type, applianceId, appliance, appReps );

		} catch (DOMException e) {
			logger.warn( "The exception : " + e.getMessage() );
			return null;
		} catch (ApplianceException e) {
			logger.warn( "The exception : " + e.getMessage() );
			return null;
		}
	}


	/**
	 * log the query result to the log file
	 * ------------------------------------- Date Time( The query date time )[
	 * query type: webservice target applianceId: applianceId the child
	 * appliances(2)[ subappliance(type)[ applianceId: applianceId appNum: the
	 * number of the deployed applications ]
	 * 
	 * ] ] ---------------------------------------
	 * 
	 * @param type
	 * @param applianceId
	 * @param appliance
	 * @param reps
	 * @return
	 */
	private String logQuery( String type, String applianceId,
			Appliance appliance ) {

		String str = "------------------------------\n";
		Calendar c = Calendar.getInstance();

		str += "Date Time( " + c.getTime().toString() + ")[\n";
		str += "\tquery type: " + type + "\n";
		str += "\ttarget applianceId: " + applianceId + "\n";

		if (appliance instanceof Host) {

			// this is a host
			Host h = (Host) appliance;
			Map<String, List<Appliance>> children = h.getChildAppliances();
			str += "the child appliances(" + children + ")[\n";

			Iterator<String> types = children.keySet().iterator();
			while (types.hasNext()) {
				String t = types.next();
				List<Appliance> cs = children.get( t );
				for (Appliance a : cs) {
					str += "\t\tsubappliance( " + type + " )[\n";
					str += "\t\t\tapplianceId: " + a.getDesp().getId() + "\n";
					str += "\t\t\tappNum: " + a.getStatus().getStatus() + "\n";
					str += "\t\t]\n";
				}
			}

		} else {

			// this is a appliance
			str += "\tappNum: " + appliance.getStatus().getStatus() + "\n";
		}

		str += "]\n";

		str += "------------------------------\n";
		return str;

	}


	private Document createExceptionDocument( String type, String applianceId,
			String exception ) throws MessageExchangeInvocationException {

		// parse the appliance which is specified
		// create the document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resp = builder.newDocument();

			Element root = resp.createElement( "queryApplianceInfoResponse" );
			root.setAttribute( "type", type );
			root.setAttribute( "applianceId", applianceId );
			resp.appendChild( root );

			Element exceptionEle = resp.createElement( "exception" );
			exceptionEle.setTextContent( exception );
			root.appendChild( exceptionEle );

			return resp;

		} catch (ParserConfigurationException e) {
			logger.error( e.getMessage() );
			throw new MessageExchangeInvocationException( e.getMessage() );
		}
	}


	/**
	 * build the query response message according to the query type
	 * 
	 * @param type
	 * @param applance
	 * @param appliance
	 * @param reps
	 * @throws ApplianceException
	 * @throws DOMException
	 */
	private Document buildQueryResponseMsg( String type, String applianceId,
			Appliance appliance, List<AppReplica> reps )
			throws MessageExchangeInvocationException, DOMException,
			ApplianceException {

		// parse the appliance which is specified
		// create the document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resp = builder.newDocument();

			// create the document for the appliance query
			Element root = resp.createElement( "queryApplianceInfoResponse" );
			resp.appendChild( root );
			root.setAttribute( "type", type );
			root.setAttribute( "applianceId", applianceId );

			Element despEle = resp.createElement( "description" );
			root.appendChild( despEle );

			despEle.setAttribute( "ip", appliance.getDesp().getIp() );
			despEle.setAttribute( "wsepr", appliance.getDesp().getDeployEPR() );
			despEle.setAttribute( "deployOp", appliance.getDesp()
					.getDeployOperation() );
			despEle.setAttribute( "undeployOp", appliance.getDesp()
					.getUndeployOperation() );
			despEle.setAttribute( "restartOp", appliance.getDesp()
					.getRestartOperation() );

			Element statusEle = resp.createElement( "status" );
			root.appendChild( statusEle );

			if (type.equalsIgnoreCase( "host" )) {

				// the query target is host
				Host host = (Host) appliance;
				int i = 0;

				Element asEle = resp.createElement( "appliances" );
				statusEle.appendChild( asEle );

				Iterator<List<Appliance>> ls = host.getChildAppliances()
						.values().iterator();
				while (ls.hasNext()) {
					List<Appliance> l = ls.next();
					for (Appliance a : l) {
						Element aEle = resp.createElement( "appliance" );
						asEle.appendChild( aEle );
						aEle.setAttribute( "id", a.getDesp().getId() );
						aEle.setAttribute( "type", ApplianceUtils
								.getApplianceTypeFromId( a.getDesp().getId() ) );
						i++;
					}
				}

				asEle.setAttribute( "num", String.valueOf( i ) );

			} else {

				// the other type of appliance
				Element asEle = resp.createElement( "apps" );
				statusEle.appendChild( asEle );
				int i = 0;
				if (reps == null) {

					asEle.setAttribute( "num", "0" );

				} else {

					for (AppReplica r : reps) {

						Element repEle = resp.createElement( "app" );
						repEle.setAttribute( "id", r.getAppId() );
						repEle.setAttribute( "appName", r.getAppName() );
						repEle.setAttribute( "invokeUrl", r.getInvocationUrl() );
						asEle.appendChild( repEle );
						i++;
					}

					asEle.setAttribute( "num", String.valueOf( i ) );

				}

			}

			return resp;

		} catch (ParserConfigurationException e) {
			logger.error( e.getMessage() );
			throw new MessageExchangeInvocationException( e.getMessage() );
		}

	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {
		Element root = request.getDocumentElement();

		String type = root.getAttribute( "type" );
		String applianceId = root.getAttribute( "applianceId" );

		if (type == null || applianceId == null) {
			logger.warn( "Miss the query type value or the applianceId value for the query request" );
			throw new MessageExchangeInvocationException(
					"Miss the query type value or the applianceId value for the query request" );
		}

		context.storeData( ApplianceInfoQueryBusinessUnit.QUERY_TYPE, type );
		context.storeData( ApplianceInfoQueryBusinessUnit.APPLIANCE_ID,
				applianceId );

		this.unit.dispatch( context );
	}

}
