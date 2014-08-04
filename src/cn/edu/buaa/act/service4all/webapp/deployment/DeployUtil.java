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
package cn.edu.buaa.act.service4all.webapp.deployment;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;

public class DeployUtil {
	private static Log logger = LogFactory.getLog(DeployUtil.class);

	public static OMElement deploy(OMElement request, String url)
			throws AxisFault {
		OMElement response = null;
		try {
			RPCServiceClient client = new RPCServiceClient();
			url = validateIp(url);
			EndpointReference to = buildEndpoint(url);
			Options options = buildOptions(to);
			client.setOptions(options);
			if (request == null) {
				logger.info("Invoke request is null");
			}
			response = client.sendReceive(request);
			logger.info("deployment response:" + response);
			client.cleanupTransport();
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		return response;
	}

	private static String validateIp(String ip) {
		String prefix = "http://";
		if (!ip.startsWith(prefix)) {
			ip = prefix + ip;
		}
		return ip;
	}

	private static Options buildOptions(EndpointReference to) {
		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setTo(to);
		// enabling MTOM in the client side
		options.setProperty(
				org.apache.axis2.Constants.Configuration.ENABLE_MTOM,
				org.apache.axis2.Constants.VALUE_TRUE);
		options.setTransportInProtocol(org.apache.axis2.Constants.TRANSPORT_HTTP);
		return options;
	}

	private static String getIp(String url) {
		String str = url.split(":")[1];
		String[] prefix = str.split("//");
		return prefix[1];
	}

	private static EndpointReference buildEndpoint(String url) {
		String ip = getIp(url);
		ip = validateIp(ip);
		return new EndpointReference(ip + Constants.DEPLOY_ENDPOINT);
	}
}
