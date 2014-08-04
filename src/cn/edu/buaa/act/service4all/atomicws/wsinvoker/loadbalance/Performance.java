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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.loadbalance;

public class Performance {
	private double cpu = 0;
	private double memory = 0;
	private double throughput = 0;
	private int serviceCount = 0;
	
	public Performance( double cpu,double memeory,double throughput,int  serviceCount) {
		this.cpu = cpu;
		this.memory = memeory;
		this.throughput = throughput;
		this.serviceCount = serviceCount;
	}
	public Performance() {
	}
	

	public double getCpu() {
		return cpu;
	}

	public double getMemeory() {
		return memory;
	}

	public double getThroughput() {
		return throughput;
	}

	public int getServiceCount() {
		return serviceCount;
	}
}
