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
package cn.edu.buaa.act.service4all.core.localsamanager.messagesender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;


public class AppUndeployUpdateMessageSender {
	
	private static Logger logger = Logger.getLogger( AppUndeployUpdateMessageSender.class );

	public static OMElement sendUpdateMsg( OMElement msg, String type, String sendURL ){
		msg = msg.getFirstElement();
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			String hostIP = addr.getHostAddress().toString();
			OMFactory omf = OMAbstractFactory.getOMFactory();
			OMElement wsUpdateMsg = omf.createOMElement( "update", null );
			OMAttribute hostIPAttr = omf.createOMAttribute( "ip", null,
					hostIP );
			wsUpdateMsg.addAttribute( hostIPAttr );
			OMElement deployElement = omf
					.createOMElement( "undeploy", null );
			OMAttribute wsType = omf.createOMAttribute( "type", null,
					"repetition" );
			String newWSPathFrom = msg.getAttributeValue( new QName( null,
					"appliancePath" ) );
			newWSPathFrom = newWSPathFrom.substring( 0, newWSPathFrom
					.length() - 1 );
			EndpointReference targetEPR = new EndpointReference( "http://"
					+ hostIP
					+ ":8080/axis2/services/SAManageService/wsRelicaUpdate" );
			RPCServiceClient serviceClientForWS;
			OMElement wsPort = null;
			try {
				serviceClientForWS = new RPCServiceClient();
				Options optionsForWS = serviceClientForWS.getOptions();
				optionsForWS.setTo( targetEPR );
				optionsForWS.setAction( "urn:getAppliancePort" );
				serviceClientForWS.setOptions( optionsForWS );
				QName opAddEntry = new QName(
						"http://service.localsamanager.core.service4all.act.buaa.edu.cn",
						"getAppliancePort" );
				Object[] opAddEntryArgs = new Object[] { newWSPathFrom };
				wsPort = serviceClientForWS.invokeBlocking( opAddEntry,
						opAddEntryArgs );
				logger.info( "PORT:" + wsPort.toString() );
				serviceClientForWS.cleanupTransport();
			} catch (AxisFault e) {
				e.printStackTrace();
			}
			OMAttribute port = null;
			if (wsPort != null) {
				port = omf.createOMAttribute( "port", null, wsPort
						.getFirstElement().getText() );
			}
			deployElement.addAttribute( wsType );
			deployElement.addAttribute( port );
			OMElement repetition = omf.createOMElement( "repetition", null );
			String wsName = msg.getAttributeValue( new QName( null,
					"wsName" ) );
			OMAttribute appName = omf.createOMAttribute( "appName", null,
					wsName );
			String serviceID = msg.getAttributeValue( new QName( null,
					"serviceID" ) );
			OMAttribute serviceIDAttr = omf.createOMAttribute( "id", null,
					serviceID );

			repetition.addAttribute( appName );
			repetition.addAttribute( serviceIDAttr );

			deployElement.addChild( repetition );
			wsUpdateMsg.addChild( deployElement );
			ClientUtil.sendOMMessage( wsUpdateMsg, sendURL );
			return wsUpdateMsg;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
