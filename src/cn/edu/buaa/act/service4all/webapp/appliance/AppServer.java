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
package cn.edu.buaa.act.service4all.webapp.appliance;

public class AppServer {
	private String url;
	private double cpu;
	private double memory;
	private double throughput = 0;
	private int webAppCount = 0;
	private String id;
	private String port;

	public AppServer(String url) {
		this.url = url;
		this.port = getPortFromUrl();
	}

	public AppServer(String url, String id, double cpu, double memory,
			double throughput) {
		this.url = url;
		this.cpu = cpu;
		this.memory = memory;
		this.throughput = throughput;
		this.port = getPortFromUrl();
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public double getCpu() {
		return cpu;
	}

	public double getMemory() {
		return memory;
	}

	public double getThroughput() {
		return throughput;
	}

	public int getWebAppCount() {
		return webAppCount;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public void setMemory(double memory) {
		this.memory = memory;
	}

	public void setThroughput(double throughput) {
		this.throughput = throughput;
	}

	public void setWebAppCount(int webAppCount) {
		this.webAppCount = webAppCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPort() {
		return port;
	}

	private String getPortFromUrl() {
		String suffix = url.split(":")[2];
		return suffix.split("/")[0];
	}
}
