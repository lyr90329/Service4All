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


public class PerformanceSortor {
	public static final double MAX = Double.MAX_VALUE;
	double weight[] = null;
	Performance [] performances;
	PerformanceRankingTable rankingTable;

	public PerformanceSortor(double weight[],
			Performance [] performances) {
		this.weight = weight;
		this.performances = performances;
		rankingTable = new PerformanceRankingTable(performances.length);
	}

	public int[] sort() {
		int size = performances.length;
		double[] CPUs = getCPU(performances);
		double[] memeorysAvailable = getMemeory(performances);
		double[] throughputs = getThroughput(performances);
		int[] counts = getServiceCount(performances);
		double[] overalls = new double[size];

		rankingTable.byCPU = rank(CPUs);
		rankingTable.byMemeory = rank(memeorysAvailable);
		rankingTable.byThroughput = rank(throughputs);
		rankingTable.byServiceAmount = rank(counts);
		
		for (int i = 0; i < size; i++) {
			overalls[i] = (weight[0] * rankingTable.byCPU[i] +
					weight[1]* rankingTable.byMemeory[i] + 
					weight[2]* rankingTable.byThroughput[i] +
					weight[3]* rankingTable.byServiceAmount[i]);
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
			rank[flag] =  i;
		}
		return rank;
	}

	private int [] rank(int[] args) {
		int[] rank = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			int min = Integer.MAX_VALUE;
			int flag = i;
			for (int j = 0; j < args.length; j++) {
				if (args[j] < min) {
					min = args[j];
					flag = j;
				}
			}
			args[flag] = Integer.MAX_VALUE; 
			rank[flag] = i;
		}
		return rank;
	}

	private double[] getCPU(Performance [] performances) {
		int size = performances.length;
		double[] CPUs = new double[size];
		for (int i = 0; i < size; i++) {
			CPUs[i] = performances[i].getCpu();
		}
		return CPUs;
	}

	private double[] getMemeory(Performance [] performances) {
		int size = performances.length;
		double[] memeorys = new double[size];
		for (int i = 0; i < size; i++) {
			memeorys[i] = -performances[i].getMemeory();
		}
		return memeorys;
	}

	private double[] getThroughput(Performance [] performances) {
		int size = performances.length;
		double[] Throughputs = new double[size];
		for (int i = 0; i < size; i++) {
			Throughputs[i] = performances[i].getThroughput();
		}
		return Throughputs;
	}

	private int[] getServiceCount(Performance [] performances) {
		int size = performances.length;
		int[] serviceCounts = new int[size];
		for (int i = 0; i < size; i++) {
			serviceCounts[i] = performances[i].getServiceCount();
		}
		return serviceCounts;
	}

}
