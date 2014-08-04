/**
 * Service4All: A Service-oriented Cloud Platform for All about Software
 * Development Copyright (C) Institute of Advanced Computing Technology, Beihang
 * University Contact: service4all@act.buaa.edu.cn
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import java.io.File;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;

import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WSUndeployUtils {
	private static RPCServiceClient serviceClient = null;

	private static final Log logger = LogFactory.getLog(WSUndeployUtils.class);

	@SuppressWarnings("unchecked")
	public static boolean undeploy(String undeployUrl, String fileName,
			String serviceID, String containerId,String userName) {

		try {
			// serviceClient = new RPCServiceClient();
			// EndpointReference targetEPR = new
			// EndpointReference(replacePort(undeployUrl));
			// Options options = serviceClient.getOptions();
			// options.setTo(targetEPR);
			// options.setAction("urn:undeployService");
			// QName opName = new QName(
			// "http://manageServices.serviceCloud.sdp.act.org.cn",
			// "undeployService");
			// OMElement request = DocsBuilder.buildUndeployEnvelope(fileName,
			// serviceID, retrievePort(containerId));
			// OMElement[] req = new OMElement[] { request };
			// OMElement responseOM = null;
			// responseOM = serviceClient.invokeBlocking(opName, req);
			//
			// OMElement tmp = null;
			// for (Iterator<OMElement> iterator =
			// responseOM.getChildElements(); iterator
			// .hasNext();) {
			// tmp = iterator.next();
			// if (tmp.getLocalName().equals("isSuccessful")) {
			// if (tmp.getText().equals("false")) {
			// logger.info(undeployUrl + " :" + fileName
			// + "fails to be undployed��");
			// return false;
			// } else {
			// logger.info(undeployUrl + " :" + fileName
			// + "is undeployed successfully��");
			// return true;
			//
			// }
			// }
			// }
			RPCServiceClient serviceClient = new RPCServiceClient();

			EndpointReference targetEPR = new EndpointReference(
					replacePort(undeployUrl));
			Options options = new Options();
			options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
			options.setTo(targetEPR);
			options.setProperty(Constants.Configuration.ENABLE_MTOM,
					Constants.VALUE_TRUE);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			serviceClient.setOptions(options);
			OMElement element = DocsBuilder.buildUndeployEnvelope(fileName,
					serviceID, retrievePort(containerId),userName);
			OMElement result = serviceClient.sendReceive(element);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static String retrievePort(String containerId) {
		String port = containerId.substring(containerId.lastIndexOf(':') + 1);
		return port;

	}

	private static String replacePort(String containerUrl) {
		String[] strs = containerUrl.split(":");
		StringBuffer sb = new StringBuffer();
		strs[2] = strs[2].replace(strs[2].substring(0, strs[2].indexOf('/')),
				DeployConstants.DEPLOY_PORT);
		for (String str : strs)
			sb.append(str + ":");
		return sb.toString().substring(0, sb.length() - 1) + "undeployService";
	}
}
