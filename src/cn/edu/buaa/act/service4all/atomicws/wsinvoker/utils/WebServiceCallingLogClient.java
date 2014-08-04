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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class WebServiceCallingLogClient {

	private static EndpointReference targetEPR;
	
	public WebServiceCallingLogClient(String ip){
		targetEPR = new EndpointReference("http://"+ip+ 
				":8080/axis2/services/localSAManagerService/setInvokeUser");
		
	}

	public static void sendRequest(OMElement saMsg) throws AxisFault {

		ServiceClient serviceClient = new ServiceClient();
		Options options = serviceClient.getOptions();

		options.setTo(targetEPR);
		serviceClient.setOptions(options);
		serviceClient.fireAndForget(saMsg);
	}



	public  OMElement msgEncp(String userID,String userType,String wsdlUrl) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://manageServices.serviceCloud.sdp.act.org.cn", "");
		OMElement WSInvokeation = fac.createOMElement("WSInvokeation", omNs);

		OMAttribute ID = fac.createOMAttribute("ID", omNs, userID);
		OMAttribute type = fac.createOMAttribute("type", omNs, userType);
		OMElement user = fac.createOMElement("user", omNs);
		user.addAttribute(ID);
		user.addAttribute(type);

		OMElement url = fac.createOMElement("url", omNs);
		url.setText(wsdlUrl);


		WSInvokeation.addChild(user);
		WSInvokeation.addChild(url);

		return WSInvokeation;

	}

	public static void main(String[] args) throws AxisFault {
		WebServiceCallingLogClient c=new WebServiceCallingLogClient("192.168.3.219");
		System.out.println(c.msgEncp("gdl", "owner", "abac"));
		c.sendRequest(c.msgEncp("gdl", "owner", "abac"));
		
	}
}
