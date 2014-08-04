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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance;

import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class TomcatInfo extends SoftwareAppliance {
	
//	private int port;
	private int shutdownPort;
	private int ajpPort;
	private String tomcatPath;
	/**
	 * 分为"InActive" 和 "Active"两种状态
	 */
	private String status;
	private boolean isBpmnEngine;
	private boolean axis2;
	private boolean appserver;
	private int tomcatIndex;	
	
	/**
	 * @return the shutdownPort
	 */
	public int getShutdownPort() {
		return shutdownPort;
	}
	/**
	 * @param shutdownPort the shutdownPort to set
	 */
	public void setShutdownPort(int shutdownPort) {
		this.shutdownPort = shutdownPort;
	}
	/**
	 * @return the ajpPort
	 */
	public int getAjpPort() {
		return ajpPort;
	}
	/**
	 * @param ajpPort the ajpPort to set
	 */
	public void setAjpPort(int ajpPort) {
		this.ajpPort = ajpPort;
	}
	/**
	 * @return the tomcatPath
	 */
	public String getTomcatPath() {
		return tomcatPath;
	}
	/**
	 * @param tomcatPath the tomcatPath to set
	 */
	public void setTomcatPath(String tomcatPath) {
		this.tomcatPath = tomcatPath;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the axis2
	 */
	public boolean isAxis2() {
		return axis2;
	}
	/**
	 * @param axis2 the axis2 to set
	 */
	public void setAxis2(boolean axis2) {
		this.axis2 = axis2;
	}
	/**
	 * @return the appserver
	 */
	public boolean isAppserver() {
		return appserver;
	}
	
	/**
	 * @return is used to act as a bpmn engine
	 */
	public boolean isBpmnEngine() {
		return isBpmnEngine;
	}
	
	public void setBpmnEngine( boolean isBpmnEngine ) {
		this.isBpmnEngine = isBpmnEngine;
	}
	/**
	 * @param appserver the appserver to set
	 */
	public void setAppserver(boolean appserver) {
		this.appserver = appserver;
	}
	/**
	 * @return the tomcatIndex
	 */
	public int getTomcatIndex() {
		return tomcatIndex;
	}
	/**
	 * @param tomcatIndex the tomcatIndex to set
	 */
	public void setTomcatIndex(int tomcatIndex) {
		this.tomcatIndex = tomcatIndex;
	}
	
	public TomcatInfo(){
		super.setType(SoftwareAppliance.TOMCAT_TYPE);
		super.setDeployEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX);
		super.setDeployOperation("saDeploy");
		super.setUndeployEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX );
		super.setUndeployOperation("saUndeploy");
		super.setRestartEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX );
		super.setRestartOperation("restart");
	}
	
	public TomcatInfo(TomcatInfo tomcatInfo){
		super.setType(SoftwareAppliance.TOMCAT_TYPE);
		super.setDeployEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX);
		super.setDeployOperation("saDeploy");
		super.setUndeployEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX );
		super.setUndeployOperation("saUndeploy");
		super.setRestartEPR(Constants.LOCAL_IP_WITH_HTTP + ":" + Constants.LOCAL_SM_PORT 
				+ Constants.LOCAL_SM_SUFFIX );
		super.setRestartOperation("restart");
		port = tomcatInfo.port;	
		shutdownPort = tomcatInfo.shutdownPort;
		ajpPort = tomcatInfo.ajpPort;
		tomcatPath = tomcatInfo.tomcatPath;
		status = tomcatInfo.status;
		axis2 = tomcatInfo.axis2;
		isBpmnEngine = tomcatInfo.isBpmnEngine;
	}
	
	@Override public boolean equals(Object tomcatInfo) { 
	    return tomcatInfo != null &&
	    	( ( (TomcatInfo) tomcatInfo ).getPort() == (port) ); 
	}
	

	@Override public int hashCode(){
		int result = 17;
		result = 31 * result + tomcatIndex;
		result = 31 * result + port;
		result = 31 * result + shutdownPort;
		result = 31 * result + ajpPort;
		result = 31 * result + (axis2?1:0);
		result = 31 * result + tomcatPath.hashCode();
		return result;
	}
	
}
