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
package cn.edu.buaa.act.service4all.core.samanager.appliance.register;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerComponent;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.ApplianceStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2Server;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.Axis2ServerStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.beans.HostDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.HostRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.HostStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServer;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerDescription;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerRecords;
import cn.edu.buaa.act.service4all.core.samanager.beans.TomcatServerStatus;
import cn.edu.buaa.act.service4all.core.samanager.beans.WebAppReplica;
import cn.edu.buaa.act.service4all.core.samanager.beans.WebServiceReplica;

/**
 * Receiving the appliance request and registered the appliance
 * 
 * @author Huangyj
 * 
 */
public class ApplianceRegisterReceiver extends Receiver {

	private final Log logger = LogFactory
			.getLog( ApplianceRegisterReceiver.class );

	public static final String APP = "application";
	public static final String APP_NAME = "name";
	public static final String APP_URL = "wsdl";
	public static final String APP_ID = "serviceID";

	public static final String APPLIANCE_AXIS2 = "axis2";
	public static final String APPLIANCE_TOMCAT = "tomcat";
	public static final String APPLIANCE_HOST = "host";


	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element response = doc.createElement( "registerResponse" );
			response.setTextContent( "true" );
			doc.appendChild( response );
			return doc;

		} catch (ParserConfigurationException e) {

			// the exception handling should be rethought and redone
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * it would parse the message
	 */
	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {

		// need to judge the type of the appliance
		List<AppReplica> reps = new ArrayList<AppReplica>();
		Host host = parseRegisterDocument( request, reps );

		logger.info( "Receiving the appliance register request: "
				+ host.getDesp().getId() );

		context.storeData( ApplianceRegisterBusinessUnit.HOST_DATA, host );
		context.storeData( ApplianceRegisterBusinessUnit.HOSTEDS, reps );

		this.unit.dispatch( context );
	}


	private Host parseRegisterDocument( Document request,
			List<AppReplica> reps ) {

		if (request == null) {
			logger.error( "The register request document is null!" );
			return null;
		}
		if (request.getElementsByTagName( "hostRegister" ) != null
				&& request.getElementsByTagName( "hostRegister" ).getLength() >= 1) {
			// parse host register
			return parseHost( request, reps );
		}

		// else if(request.getElementsByTagName("applianceRegister") != null
		// && request.getElementsByTagName("applianceRegister").getLength() >=
		// 1){
		// //parse appliance register
		// return parseAppliance(request);
		// }else{
		// logger.warn("The document is invalidated for appliance registered!");
		// return null;
		// }

		return null;
	}


	private Host parseHost( Document xmlDoc, List<AppReplica> reps ) {

		Host host = new Host();
		HostDescription hostDesp = new HostDescription();

		Element hostRegister = (Element) xmlDoc.getElementsByTagName(
				"hostRegister" ).item( 0 );

		String hostIp = hostRegister.getAttribute( "ip" );
		String hostwsepr = hostRegister.getAttribute( "wsepr" );

		String hostDeployOp = hostRegister.getAttribute( "deployOp" );
		String hostUndeployOp = hostRegister.getAttribute( "undeployOp" );
		String hostRestartOp = hostRegister.getAttribute( "restartOp" );

		String cpu = hostRegister.getAttribute( "cpu" );
		String memory = hostRegister.getAttribute( "memory" );
		String throughput = hostRegister.getAttribute( "throughput" );

		// String hostStopOp = hostRegister.getAttribute("stopOp");

		hostDesp.setIp( hostIp );
		hostDesp.setId( SAManagerComponent.APPLIANCE_HOST + "_" + hostIp );

		hostDesp.setDeployEPR( hostwsepr );
		hostDesp.setUndeployEPR( hostwsepr );
		hostDesp.setRestartEPR( hostwsepr );
		hostDesp.setStopEPR( hostwsepr );

		hostDesp.setDeployOperation( hostDeployOp );
		hostDesp.setUndeployOperation( hostUndeployOp );
		hostDesp.setRestartOperation( hostRestartOp );
		// hostDesp.setStopOperation(hostStopOp);

		hostDesp.setAppliance( host );
		host.setDesp( hostDesp );

		HostStatus hostStatus = new HostStatus();
		hostStatus.setAppliance( host );
		hostStatus.setStatus( ApplianceStatus.APPLIANCE_STARTED );
		hostStatus.setUpdateDate( new Date() );

		hostStatus.setCpuRate( Float.valueOf( cpu ) );
		hostStatus.setMemoryfloat( Float.valueOf( memory ) );
		hostStatus.setPort( Float.valueOf( throughput ) );

		host.setStatus( hostStatus );

		HostRecords records = new HostRecords();
		host.setRecords( records );

		NodeList nodes = xmlDoc.getElementsByTagName( "appliance" );

		logger.info( "There are " + nodes.getLength()
				+ " subappliances to be registered!" );
		for (int i = 0; i < nodes.getLength(); i++) {
			Element applianceElement = (Element) nodes.item( i );
			logger.info( "Get a sub appliance register: "
					+ XMLUtils.retrieveNodeAsString( applianceElement ) );
			parseAppliance( applianceElement, host, hostIp, reps );
		}
		logger.info( "The num of the appliances in the host: "
				+ host.getChildAppliances().size() );
		return host;
	}


	// private Appliance parseAppliance(Document xmlDoc){
	// Element applianceElement =
	// (Element)xmlDoc.getElementsByTagName("appliance").item(0);
	// String hostIp = applianceElement.getAttribute("host");
	// return parseAppliance(applianceElement, hostIp);
	// }

	private void parseAppliance( Element element, Host host, String hostIp,
			List<AppReplica> reps ) {

		String applianceType = element.getAttribute( "type" );

		Appliance appliance;
		ApplianceDescription desp;
		ApplianceRecords records;
		ApplianceStatus status;
		String applianceId = "";

		if (applianceType == null) {
			logger.warn( "The appliance type is null, so can't create one!" );
			return;
		}

		if (applianceType.equalsIgnoreCase( "axis2" )) {
			applianceId += SAManagerComponent.APPLIANCE_AXIS2 + "_";
			appliance = new Axis2Server();
			desp = new Axis2ServerDescription();
			records = new Axis2ServerRecords();
			status = new Axis2ServerStatus();

		} else if (applianceType.equalsIgnoreCase( "tomcat" )) {
			applianceId += SAManagerComponent.APPLIANCE_APPSERVER + "_";
			appliance = new TomcatServer();
			desp = new TomcatServerDescription();
			records = new TomcatServerRecords();
			status = new TomcatServerStatus();

		} else {

			logger.warn( "The appliance type is invalidated!" );
			return;
		}

		appliance.setDesp( desp );
		desp.setAppliance( appliance );

		appliance.setRecords( records );
		records.setAppliance( appliance );

		String port = element.getAttribute( "port" );
		String deployEPR = element.getAttribute( "deployEPR" );
		String deployOperation = element.getAttribute( "deployOperation" );
		String undeployEPR = element.getAttribute( "undeployEPR" );
		String undeployOperation = element.getAttribute( "undeployOperation" );
		String restartEPR = element.getAttribute( "restartEPR" );
		String restartOperation = element.getAttribute( "restartOperation" );

		String cpu = element.getAttribute( "cpu" );
		String memory = element.getAttribute( "memory" );
		String throughput = element.getAttribute( "throughput" );
		status.setCpuRate( Float.valueOf( cpu ) );
		status.setMemoryfloat( Float.valueOf( memory ) );
		status.setPort( Float.valueOf( throughput ) );

		appliance.setStatus( status );
		status.setAppliance( appliance );

		desp.setDeployEPR( deployEPR );
		desp.setDeployOperation( deployOperation );
		desp.setUndeployEPR( undeployEPR );
		desp.setUndeployOperation( undeployOperation );
		desp.setRestartEPR( restartEPR );
		desp.setRestartOperation( restartOperation );

		desp.setId( applianceId + hostIp + ":" + port );
		desp.setIp( hostIp );
		desp.setPort( port );

		// parse the application element
		NodeList list = element.getElementsByTagName( APP );
		if (applianceType.equalsIgnoreCase( APPLIANCE_AXIS2 )) {
			for (int i = 0; i < list.getLength(); i++) {
				Element appElement = (Element) list.item( i );
				WebServiceReplica ws = parseWebService( appElement,
						appliance );
				// appliance.getStatus().addApps(ws.getBackups());
				reps.add( ws );
			}
		} else if (applianceType.equalsIgnoreCase( APPLIANCE_TOMCAT )) {
			applianceType = "appserver";
			for (int i = 0; i < list.getLength(); i++) {
				Element appElement = (Element) list.item( i );
				AppReplica ws = parseWebApp( appElement, appliance );// parseWebService(appElement,
																		// appliance);
				// appliance.getStatus().addApps(ws.getBackups());
				reps.add( ws );
			}
		} else {
			logger.info( "Can't support this app now for the appliance : "
					+ applianceType );

		}

		if (appliance != null) {
			logger.info( "Registering a sub appliance : "
					+ appliance.getDesp().getId() );
			host.addChildAppliance( applianceType, appliance );
		}

	}


	private WebServiceReplica parseWebService( Element appElement,
			Appliance appliance ) {

		WebServiceReplica rep = new WebServiceReplica();

		rep.setContainer( appliance );
		rep.setContainerId( appliance.getDesp().getId() );

		String name = appElement.getAttribute( APP_NAME );
		String url = appElement.getAttribute( APP_URL );
		String serviceID = appElement.getAttribute( APP_ID );

		rep.setInvocationUrl( url );
		rep.setWsdlUrl( url );

		rep.setAppName( name );
		rep.setAppId( serviceID );

		return rep;
	}


	private WebAppReplica parseWebApp( Element appElement,
			Appliance appliance ) {

		WebAppReplica rep = new WebAppReplica();

		rep.setContainer( appliance );
		rep.setContainerId( appliance.getDesp().getId() );

		String name = appElement.getAttribute( APP_NAME );
		String url = appElement.getAttribute( APP_URL );
		String serviceID = appElement.getAttribute( APP_ID );

		rep.setInvocationUrl( url );
		rep.setInvokeUrl( url );

		rep.setAppName( name );
		rep.setAppId( serviceID );

		return rep;
	}

}
