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
package cn.edu.buaa.act.service4all.core.localsamanager.timer;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.buaa.act.service4all.core.localsamanager.app.Application;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.HostInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.SoftwareAppliance;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.messagesender.ClientUtil;
import cn.edu.buaa.act.service4all.core.localsamanager.monitoringinfo.IMonitorService;
import cn.edu.buaa.act.service4all.core.localsamanager.monitoringinfo.MonitorInfoBean;
import cn.edu.buaa.act.service4all.core.localsamanager.monitoringinfo.MonitorServiceImpl;


public class PCTimer extends Thread {

//	public static String SAManageServiceEPREnd = "/axis2/services/SAManageService/";
//	public static String localAgentPort = "8080";
//	public static String Axis2ServiceEnd = "/axis2/services/";
//	public static String Axis2RemoteDeployOp = "deployRemoteService";
//	public static String Axis2UnDeployOp = "unDeployService";
//	public static String Axis2RestartOp = "restart";
	public static String SAManagerUrl = Constants.URL_SA_MANAGE;
	private static Logger logger = Logger.getLogger( PCTimer.class );

	public PCTimer() {
		System.getProperty( "user.dir" ).replace( '\\', '/' );
		PropertyConfigurator.configure( System.getProperty( "user.dir" )
				.replace( '\\', '/' )
				+ "/log/" + "log4j2.properties" );
		logger.debug( "The host begins to register and pooling" );
		IMonitorService hostInfoGet = new MonitorServiceImpl();
		MonitorInfoBean hostMonitorInfo = null;
		
		try {
			hostMonitorInfo = hostInfoGet.getMonitorInfoBean();
			logger.info( "[System Information][Host PC Information]"
					+ "OS Name: "
					+ hostMonitorInfo.getOsName()
					+ '\t'
					+ "CPU Ratio: "
					+ Double.toString( hostMonitorInfo.getCpuRatio() )
					+ "%"
					+ '\t'
					+ "Total Physical Memory(kb): "
					+ Long.toString( hostMonitorInfo.getTotalMemorySize() )
					+ '\t'
					+ "Free Physical Memory(kb):"
					+ Long.toString( hostMonitorInfo
							.getFreePhysicalMemorySize() ) );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	public void run() {
		execute();
	}


	private void execute() {
		Timer timer = new Timer();
		PCTimertask pcTimeTask = new PCTimertask();
		Date date = new Date();
		logger.info( "[System Information][Host Timer timestamp] "
				+ Constants.PC_TIMEOUT );
		long timestamp = Constants.REGISTER_TIME ;
		timer.schedule( pcTimeTask, date, timestamp );
	}


	class PCTimertask extends TimerTask {

		public synchronized void run() {
			logger.debug( "The host begins to register and pooling" );
			IMonitorService hostInfoGet = new MonitorServiceImpl();
			MonitorInfoBean hostMonitorInfo = null;
			
			try {
				hostMonitorInfo = hostInfoGet.getMonitorInfoBean();
				logger.info( "[System Information][Host PC Information]"
						+ "OS Name: "
						+ hostMonitorInfo.getOsName()
						+ '\t'
						+ "CPU Ratio: "
						+ Double.toString( hostMonitorInfo.getCpuRatio() )
						+ "%"
						+ '\t'
						+ "Total Physical Memory(kb): "
						+ Long.toString( hostMonitorInfo.getTotalMemorySize() )
						+ '\t'
						+ "Free Physical Memory(kb):"
						+ Long.toString( hostMonitorInfo
								.getFreePhysicalMemorySize() ) );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			HostInfo hostInfo = HostInfo.getInstance();
			hostInfo.setCpu( (float) (hostMonitorInfo.getCpuRatio()) );
			hostInfo.setMemory( (float) hostMonitorInfo
					.getFreePhysicalMemorySize() );
			hostInfo.setThroughput( 300 );
			hostInfo.setStopOp( "Have not StopOp" );
			OMElement request = createHostRegistryRequest(hostInfo);
			logger.info("The register request:" + request);
			ClientUtil.sendOMMessage(request, SAManagerUrl);

		}
	}
	
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
					a.getUndeployEPR(), null);
			applianceElement
					.addAttribute("restartEPR", a.getRestartEPR(), null);
			applianceElement.addAttribute("restartOperation",
					a.getRestartOperation(), null);
			applianceElement.addAttribute("cpu", String.valueOf(host.getCpu()),
					null);
			applianceElement.addAttribute("memory",
					String.valueOf(host.getMemory()), null);
			applianceElement.addAttribute("throughput",
					String.valueOf(host.getThroughput()), null);

			// adding the application
			OMElement applicationsElement = fac.createOMElement("applications",
					null);
			List<Application> apps =host.getApps(a.getPort());
			if(apps != null){
				for (Application app : apps) {		
					logger.info("Add an application:" + app.getName());
					OMElement applicationElement = fac.createOMElement(
							"application", null);
					applicationElement.addAttribute("name", app.getName(), null);
					applicationElement.addAttribute("wsdl",
							app.getInvocationAddr(), null);
					applicationElement.addAttribute("serviceID", app.getServiceID(),null);
					
					applicationsElement.addChild(applicationElement);

				}
			}
			applianceElement.addChild(applicationsElement);
			appliancesElement.addChild(applianceElement);
		}

		hostRegister.addChild(appliancesElement);
		return hostRegister;

	}

}
