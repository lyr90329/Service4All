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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.rpc.client.RPCServiceClient;

public class ClientUtil {

	public static OMElement sendOMMessage( OMElement msg, String url,
			String operation ) {

		EndpointReference epr = new EndpointReference( url );

		RPCServiceClient client;
		try {
			client = new RPCServiceClient();
			Options options = client.getOptions();
			options.setTo( epr );
			options.setAction( "urn:" + operation );
			ServiceClient sender = new ServiceClient();
			sender.setOptions( options );
			OMElement response = sender.sendReceive( msg );
			sender.cleanupTransport();
			return response;
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void sendOMMessage( OMElement msg, String url ) {

		EndpointReference epr = new EndpointReference( url );

		RPCServiceClient client;
		try {
			client = new RPCServiceClient();
			Options options = client.getOptions();
			options.setTo( epr );
			ServiceClient sender = new ServiceClient();
			sender.setOptions( options );
//			sender.sendReceive( msg );
			sender.sendRobust( msg );
			sender.cleanupTransport();
//			return response;
		} catch (AxisFault e) {
			e.printStackTrace();
		}
//		return null;
	}

}
