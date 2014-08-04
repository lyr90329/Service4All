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


public abstract class SoftwareAppliance {

	public final static String HOST_TYPE = "host";
	public final static String AXIS2_TYPE = "axis2";
	public final static String TOMCAT_TYPE = "tomcat";

	protected int port;
	private String path;

	private HostInfo host;

	private String type;
	private String undeployEPR;
	private String undeployOperation;

	private String deployEPR;
	private String deployOperation;

	private String restartEPR;
	private String restartOperation;

	private float cpu;
	private float memory;
	private int throughput;
	
	public SoftwareAppliance(){}

	public SoftwareAppliance( int port, String undeployEPR,
			String undeployOperation, String deployEPR, String deployOperation ) {
		super();
		this.port = port;
		this.undeployEPR = undeployEPR;
		this.undeployOperation = undeployOperation;
		this.deployEPR = deployEPR;
		this.deployOperation = deployOperation;
	}


	public SoftwareAppliance( int port, HostInfo host, String undeployEPR,
			String undeployOperation, String deployEPR, String deployOperation ) {
		super();
		this.port = port;
		this.host = host;
		this.undeployEPR = undeployEPR;
		this.undeployOperation = undeployOperation;
		this.deployEPR = deployEPR;
		this.deployOperation = deployOperation;
	}


	
	public int getPort() {
		return port;
	}


	public void setPort( int port ) {
		this.port = port;
	}

	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUndeployEPR() {
		return undeployEPR;
	}


	public void setUndeployEPR( String undeployEPR ) {
		this.undeployEPR = undeployEPR;
	}


	public String getUndeployOperation() {
		return undeployOperation;
	}


	public void setUndeployOperation( String undeployOperation ) {
		this.undeployOperation = undeployOperation;
	}


	public String getDeployEPR() {
		return deployEPR;
	}


	public void setDeployEPR( String deployEPR ) {
		this.deployEPR = deployEPR;
	}


	public String getDeployOperation() {
		return deployOperation;
	}


	public void setDeployOperation( String deployOperation ) {
		this.deployOperation = deployOperation;
	}


	public HostInfo getHost() {
		return host;
	}


	public void setHost( HostInfo host ) {
		this.host = host;
	}

	public String getType() {
		return type;
	}


	public void setType( String type ) {
		this.type = type;
	}

	public String getRestartEPR() {
		return restartEPR;
	}


	public void setRestartEPR( String restartEPR ) {
		this.restartEPR = restartEPR;
	}


	public String getRestartOperation() {
		return restartOperation;
	}


	public void setRestartOperation( String restartOperation ) {
		this.restartOperation = restartOperation;
	}


	public float getCpu() {
		return cpu;
	}


	public void setCpu( float cpu ) {
		this.cpu = cpu;
	}


	public float getMemory() {
		return memory;
	}


	public void setMemory( float memory ) {
		this.memory = memory;
	}


	public int getThroughput() {
		return throughput;
	}


	public void setThroughput( int throughput ) {
		this.throughput = throughput;
	}
}
