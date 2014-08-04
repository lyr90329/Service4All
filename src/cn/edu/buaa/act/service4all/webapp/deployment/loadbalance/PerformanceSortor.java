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
package cn.edu.buaa.act.service4all.webapp.deployment.loadbalance;

import cn.edu.buaa.act.service4all.webapp.appliance.AppServer;

public class PerformanceSortor {
	public static final double MAX = Double.MAX_VALUE;
	double weight[] = null;
	AppServer[] appServers;
	PerformanceRankingTable rankingTable;

	public PerformanceSortor(double weight[], AppServer[] appServers) {
		this.weight = weight;
		this.appServers = appServers;
		rankingTable = new PerformanceRankingTable(appServers.length);
	}

	public int[] sort() {
		int size = appServers.length;
		double[] CPUs = getCPU(appServers);
		double[] memeorysAvailable = getMemeory(appServers);
		double[] throughputs = getThroughput(appServers);
		double[] overalls = new double[size];

		rankingTable.byCPU = rank(CPUs);
		rankingTable.byMemeory = rank(memeorysAvailable);
		rankingTable.byThroughput = rank(throughputs);

		for (int i = 0; i < size; i++) {
			overalls[i] = (weight[0] * rankingTable.byCPU[i] + weight[1]
					* rankingTable.byMemeory[i] + weight[2]
					* rankingTable.byThroughput[i]);
		}
		rankingTable.overallIndicator = rank(overalls);
		return rankingTable.overallIndicator;
	}

	private int[] rank(double[] args) {
		int[] rank = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			double min = Double.MAX_VALUE;
			int flag = i;
			for (int j = 0; j < args.length; j++) {
				if (args[j] < min) {
					min = args[j];
					flag = j;
				}
			}
			args[flag] = Double.MAX_VALUE;
			rank[flag] = i;
		}
		return rank;
	}

	private double[] getCPU(AppServer[] appServers) {
		int size = appServers.length;
		double[] CPUs = new double[size];
		for (int i = 0; i < size; i++) {
			CPUs[i] = appServers[i].getCpu();
		}
		return CPUs;
	}

	private double[] getMemeory(AppServer[] appServers) {
		int size = appServers.length;
		double[] memeorys = new double[size];
		for (int i = 0; i < size; i++) {
			memeorys[i] = -appServers[i].getMemory();
		}
		return memeorys;
	}

	private double[] getThroughput(AppServer[] appServers) {
		int size = appServers.length;
		double[] Throughputs = new double[size];
		for (int i = 0; i < size; i++) {
			Throughputs[i] = appServers[i].getThroughput();
		}
		return Throughputs;
	}
}
