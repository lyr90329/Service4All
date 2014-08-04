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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance.registry;

import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import cn.edu.buaa.act.service4all.core.localsamanager.app.Application;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.SoftwareAppliance;

/**
 * The utility classes for appliance registering
 * @author Huangyj
 * 
 */
public class ApplicanceRegistryUtil {

	public static OMElement createHostRegistryRequest(HostInfo host) {

		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement hostRegister = fac.createOMElement("hostRegister", null);

		hostRegister.addAttribute("ip", host.getIp(), null);
		hostRegister.addAttribute("wsepr", host.getAddrEPR(), null);
		hostRegister.addAttribute("deployOp", host.getDeployOp(), null);
		hostRegister.addAttribute("undeployOp", host.getUndeployOp(), null);
		hostRegister.addAttribute("restartOp", host.getRestartOp(), null);
		hostRegister.addAttribute("stopOp", host.getStopOp(), null);

		hostRegister.addAttribute("cpu", String.valueOf(host.getCpu()), null);
		hostRegister.addAttribute("memory", String.valueOf(host.getMemory()),
				null);
		hostRegister.addAttribute("throughput",
				String.valueOf(host.getThroughput()), null);

		OMElement appliancesElement = fac.createOMElement("appliances", null);
		List<SoftwareAppliance> appliances = host.getAppliances();
		for (SoftwareAppliance a : appliances) {

			OMElement applianceElement = fac.createOMElement("appliance", null);
			applianceElement.addAttribute("type", a.getType(), null);
			applianceElement.addAttribute("port", String.valueOf(a.getPort()),
					null);
			applianceElement.addAttribute("deployEPR", a.getDeployEPR(), null);
			applianceElement.addAttribute("deployOperation",
					a.getDeployOperation(), null);
			applianceElement.addAttribute("undeployEPR", a.getUndeployEPR(),
					null);
			applianceElement.addAttribute("undeployOperation",
					a.getUndeployOperation(), null);
			applianceElement
					.addAttribute("restartEPR", a.getRestartEPR(), null);
			applianceElement.addAttribute("restartOperation",
					a.getRestartOperation(), null);
			applianceElement.addAttribute("cpu", String.valueOf(a.getCpu()),
					null);
			applianceElement.addAttribute("memory",
					String.valueOf(a.getMemory()), null);
			applianceElement.addAttribute("throughput",
					String.valueOf(a.getThroughput()), null);

			// adding the application
			OMElement applicationsElement = fac.createOMElement("applications",
					null);
			List<Application> apps =host.getApps(a.getPort());
			for (Application app : apps) {

				OMElement applicationElement = fac.createOMElement(
						"application", null);
				applicationElement.addAttribute("name", app.getName(), null);
				applicationElement.addAttribute("wsdl",
						app.getInvocationAddr(), null);
				applicationElement.addAttribute("serviceID", app.getServiceID(),null);
				
				applicationsElement.addChild(applicationElement);

			}
			applianceElement.addChild(applicationsElement);
			appliancesElement.addChild(applianceElement);
		}

		hostRegister.addChild(appliancesElement);
		return hostRegister;

	}
}
