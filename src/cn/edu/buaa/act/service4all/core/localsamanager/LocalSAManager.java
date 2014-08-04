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
package cn.edu.buaa.act.service4all.core.localsamanager;


import javax.xml.namespace.QName;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager.SharedFunction;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class LocalSAManager {
	
	private static Logger logger = Logger.getLogger( LocalSAManager.class );
	
	private static boolean axis2Start() {
		int counter = 0;
		for (counter = 0; counter < 3; counter++) {
			try {
				String command = Constants.ENTRY;
				Runtime.getRuntime().exec(command);
				Thread.sleep(1000);
				String axis2HtmlInTool = "http://localhost:8080/axis2/services/localSAManagerService?wsdl";
				if (SharedFunction.activeCheckByHtmlBegin(Constants.PC_TIMEOUT, axis2HtmlInTool)) {	
					logger.info("Tomcat has started");
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * @param args
	 * @throws AxisFault
	 */
	public static void main(String[] args) {

		if (axis2Start())
		{

			RPCServiceClient serviceClient;
			try {
				serviceClient = new RPCServiceClient();
				Options options = serviceClient.getOptions();

				Object[] opAddEntryArgs = new Object[] {};
				EndpointReference targetEPR = new EndpointReference(
						"http://localhost:8080/axis2/services/localSAManagerService");
				options.setTo(targetEPR);
				QName opAddEntry = new QName(
						"http://service.localsamanager.core.service4all.act.buaa.edu.cn",
						"initial");
				try { 
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				serviceClient.invokeRobust(opAddEntry, opAddEntryArgs);
			} catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
	}
}
