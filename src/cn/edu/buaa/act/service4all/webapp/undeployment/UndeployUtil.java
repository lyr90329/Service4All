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
package cn.edu.buaa.act.service4all.webapp.undeployment;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;

public class UndeployUtil {
	private static Log logger = LogFactory.getLog(UndeployUtil.class);

	public static OMElement undeploy(String url, String serviceName,
			String serviceID, String port) {
		logger.info("the undeploy port:" + port);
		OMElement response = null;
		try {
			RPCServiceClient client = new RPCServiceClient();
			Options options = new Options();
			options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
			options.setTo(buildEndpoint(url));
			options.setProperty(
					org.apache.axis2.Constants.Configuration.ENABLE_MTOM,
					org.apache.axis2.Constants.VALUE_TRUE);
			options.setTransportInProtocol(org.apache.axis2.Constants.TRANSPORT_HTTP);
			client.setOptions(options);
			url = validateIp(url);
			EndpointReference to = buildEndpoint(url);
			options.setTo(to);
			response = client.sendReceive(buildUndeployOM(serviceName,
					serviceID, port));
			client.cleanupTransport();
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		return response;
	}

	private static OMElement buildUndeployOM(String serviceName,
			String serviceID, String port) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		// OMNamespace oms =
		// fac.createOMNamespace("http://manageServices.serviceCloud.sdp.act.org.cn",
		// "");
		OMNamespace oms = fac.createOMNamespace("", "");
		// OMElement request = fac.createOMElement("JustDoNothing",oms);
		OMElement undeployRequest = fac.createOMElement("undeployRequest", oms);
		OMAttribute type = fac.createOMAttribute(Constants.TYPE, null,
				Constants.WEB_APP);
		undeployRequest.addAttribute(type);
		OMElement serviceIDEl = fac.createOMElement(Constants.SERVICE_ID, oms);
		serviceIDEl.setText(serviceID);
		undeployRequest.addChild(serviceIDEl);
		OMElement portOM = fac.createOMElement(Constants.PORT, oms);
		portOM.setText(port);
		undeployRequest.addChild(portOM);
		OMElement serviceNameEl = fac.createOMElement(Constants.FILE_NAME, oms);
		serviceNameEl.setText(serviceName + ".war");
		undeployRequest.addChild(serviceNameEl);
		// request.addChild(undeployRequest);
		// return request;
		return undeployRequest;
	}

	private static String getIp(String url) {
		String str = url.split(":")[1];
		String[] prefix = str.split("//");
		return prefix[1];
	}

	private static EndpointReference buildEndpoint(String url) {
		String ip = getIp(url);
		ip = validateIp(ip);
		return new EndpointReference(ip + Constants.UNDEPLOY_ENDPOINT);
	}

	private static String validateIp(String ip) {
		String prefix = "http://";
		if (!ip.startsWith(prefix)) {
			ip = prefix + ip;
		}
		return ip;
	}
}
