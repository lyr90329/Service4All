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
package cn.edu.buaa.act.service4all.core.localsamanager.monitoringinfo;

import java.io.*;
import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class MonitorServiceImpl implements IMonitorService {

	private static Logger logger = Logger.getLogger( MonitorServiceImpl.class );
//	private static final int CPUTIME = 30;
//
//	private static final int PERCENT = 100;
//
//	private static final int FAULTLENGTH = 10;

//	private static final File versionFile = new File("/proc/version");
	private static String linuxVersion =System.getProperty("os.version");;
	private String tomcatPath;

	/**
	 */
	public MonitorInfoBean getMonitorInfoBean() throws Exception {
		int kb = 1024;

		long totalMemory = Runtime.getRuntime().totalMemory() / kb;
		long freeMemory = Runtime.getRuntime().freeMemory() / kb;
		long maxMemory = Runtime.getRuntime().maxMemory() / kb;

		OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();

		String osName = System.getProperty("os.name");
		long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
		long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
		long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb
				.getFreePhysicalMemorySize()) / kb;

		ThreadGroup parentThread;
		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
				.getParent() != null; parentThread = parentThread.getParent())
			;
		int totalThread = parentThread.activeCount();

		double cpuRatio = 0;
		if (osName.toLowerCase().startsWith("windows")) {
			cpuRatio = this.getCpuRatioForWindows();
		} else {
			cpuRatio = this.getCpuRateForLinux();
		}

		int throughPut = this
				.getTroughPut((this.tomcatPath));

		MonitorInfoBean infoBean = new MonitorInfoBean();
		infoBean.setFreeMemory(freeMemory);
		infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
		infoBean.setMaxMemory(maxMemory);
		infoBean.setOsName(osName);
		infoBean.setTotalMemory(totalMemory);
		infoBean.setTotalMemorySize(totalMemorySize);
		infoBean.setTotalThread(totalThread);
		infoBean.setUsedMemory(usedMemory);
		infoBean.setCpuRatio(cpuRatio);
		infoBean.setThroughPut(throughPut);

		return infoBean;
	}
	
	private double getCpuRateForLinux() {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader brStat = null;
		StringTokenizer tokenStat = null;
		try {
			logger.info("Get usage rate of CUP , linux version: "
					+ linuxVersion);

			Process process = Runtime.getRuntime().exec("top -b -i -n 2");//迭代一次的话cpu不会刷新
			try {
				Thread.sleep( 3000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			brStat = new BufferedReader(isr);
			logger.info("parse the cpu");

			if (linuxVersion.startsWith("2.4")) {
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();
				brStat.readLine();

				tokenStat = new StringTokenizer(brStat.readLine());
				tokenStat.nextToken();
				tokenStat.nextToken();
				String user = tokenStat.nextToken();
				tokenStat.nextToken();
				String system = tokenStat.nextToken();
				tokenStat.nextToken();
				String nice = tokenStat.nextToken();

				logger.info(user + " , " + system + " , " + nice);

				user = user.substring(0, user.indexOf("%"));
				system = system.substring(0, system.indexOf("%"));
				nice = nice.substring(0, nice.indexOf("%"));

				float userUsage = new Float(user).floatValue();
				float systemUsage = new Float(system).floatValue();
				float niceUsage = new Float(nice).floatValue();

				return (userUsage + systemUsage + niceUsage) ;
			} 
			else if(linuxVersion.startsWith("2.6")){
				for(int i=0;i<4;i++){//step 4 lines
					brStat.readLine();
				}
				String Cpu = "";
				while(!Cpu.startsWith( "Cpu(s)")){
					Cpu = brStat.readLine();
				}
//				logger.info(Cpu);
				tokenStat = new StringTokenizer(Cpu);
				
//				tokenStat.nextToken();
//				tokenStat.nextToken();
//				tokenStat.nextToken();
//				tokenStat.nextToken();
//				tokenStat.nextToken();
//				tokenStat.nextToken();
				String cpuUsage = tokenStat.nextToken();
				while(!cpuUsage.endsWith( "%id," )){
					cpuUsage = tokenStat.nextToken();
//					logger.info(cpuUsage);
					if(cpuUsage == null){
						cpuUsage = "100.0%id";
					}
				}
				if(cpuUsage.endsWith( "100.0%id," )){//avoid the situation of "x.x%ni,100.0%id"
					cpuUsage = "100%id";
				}

				logger.info("CPU idle : " + cpuUsage);
				Float usage = new Float(cpuUsage.substring(0,
						cpuUsage.indexOf("%")));

				DecimalFormat df = new DecimalFormat("0.0");
				return Double.parseDouble(df.format(100 - usage.floatValue()));
			}		
			else if(linuxVersion.startsWith("3.2")){
				for(int i=0;i<4;i++){//step 4 lines
					brStat.readLine();
				}
				String Cpu = "";
				while(!Cpu.startsWith( "%Cpu(s)")){
					Cpu = brStat.readLine();
				}
				
				String[] tokens = Cpu.split(",");
				for(String token : tokens){
					if(token.endsWith("id")){
						String idle = token.substring(0, token.indexOf(" id"));
						logger.info("CPU idle : " + idle);
						Float usage = 100 - Float.parseFloat(idle);
						DecimalFormat df = new DecimalFormat("0.0");
						return Double.parseDouble(df.format(usage));
					}
				}
				DecimalFormat df = new DecimalFormat("0.0");
				return Double.parseDouble(df.format(50));
			}
			else {
				logger.info("Unknown linux version, may lead to register problems");
				brStat.readLine();
				brStat.readLine();

				tokenStat = new StringTokenizer(brStat.readLine());
				tokenStat.nextToken();
				tokenStat.nextToken();
				tokenStat.nextToken();
				tokenStat.nextToken();
				tokenStat.nextToken();
				tokenStat.nextToken();
				tokenStat.nextToken();
				String cpuUsage = tokenStat.nextToken();

				logger.info("CPU idle : " + cpuUsage);
				Float usage = new Float(cpuUsage.substring(0,
						cpuUsage.indexOf("%")));

				return (1 - usage.floatValue() / 100) * 100;
			}

		} catch (IOException ioe) {
			logger.info(ioe.getMessage());
			freeResource(is, isr, brStat);
			return 1;
		} finally {
			freeResource(is, isr, brStat);
		}

	}

	private static void freeResource(InputStream is, InputStreamReader isr,
			BufferedReader br) {
		try {
			if (is != null)
				is.close();
			if (isr != null)
				isr.close();
			if (br != null)
				br.close();
		} catch (IOException ioe) {
			logger.info(ioe.getMessage());
		}
	}

	public double getCpuRatioForWindows() throws InterruptedException,
			IOException {

//		try {
//
//			String procCmd = System.getenv("windir")
//					+ "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
//					+ "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
//			long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
//			Thread.sleep(CPUTIME);
//			long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
//			if (c0 != null && c1 != null) {
//				long idletime = c1[0] - c0[0];
//				long busytime = c1[1] - c0[1];
//				return Double.valueOf(
//						PERCENT * busytime / (busytime + idletime))
//						.doubleValue();
//			} else {
//				return 0.0;
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return 0.0;
//		}
//		String procCmd = System.getenv("windir") + "\\system32\\wbem\\wmic.exe cpu get loadpercentage/value";
		String procCmd = "wmic cpu get loadpercentage/value";
		Process result = Runtime.getRuntime().exec(procCmd);
		result.getOutputStream().close();
		InputStreamReader ir = new InputStreamReader(result.getInputStream());
		LineNumberReader input = new LineNumberReader(ir);
		String line = input.readLine();

		String percentage = "0";
		while(line !=null){
			if(line.contains("LoadPercentage")){
				percentage = line.substring(line.indexOf("=")+1);
			}
			line = input.readLine();
		}
		double percentageResult;
		try {
			percentageResult = Double.parseDouble(percentage);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			logger.error( e.getMessage() );
			logger.info( "Set the cpu ratio to 0." );
			percentageResult = 0.0;
		}
		return percentageResult;

	}

//	private long[] readCpu(final Process proc) {
//		long[] retn = new long[2];
//		try {
//			proc.getOutputStream().close();
//			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
//			LineNumberReader input = new LineNumberReader(ir);
//			String line = input.readLine();
//			if (line == null || line.length() < FAULTLENGTH) {
//				return null;
//			}
//			int capidx = line.indexOf("Caption");
//			int cmdidx = line.indexOf("CommandLine");
//			int rocidx = line.indexOf("ReadOperationCount");
//			int umtidx = line.indexOf("UserModeTime");
//			int kmtidx = line.indexOf("KernelModeTime");
//			int wocidx = line.indexOf("WriteOperationCount");
//			long idletime = 0;
//			long kneltime = 0;
//			long usertime = 0;
//			while ((line = input.readLine()) != null) {
//				if (line.length() < wocidx) {
//					continue;
//				}
//				// order:Caption,CommandLine,KernelModeTime,ReadOperationCount,
//				// ThreadCount,UserModeTime,WriteOperation
//				String caption = Bytes.substring(line, capidx, cmdidx - 1)
//						.trim();
//				String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
//				if (cmd.indexOf("wmic.exe") >= 0) {
//					continue;
//				}
//				// log.info("line="+line);
//				if (caption.equals("System Idle Process")
//						|| caption.equals("System")) {
//					idletime += Long.valueOf(
//							Bytes.substring(line, kmtidx, rocidx - 1).trim())
//							.longValue();
//					idletime += Long.valueOf(
//							Bytes.substring(line, umtidx, wocidx - 1).trim())
//							.longValue();
//					continue;
//				}
//
//				kneltime += Long.valueOf(
//						Bytes.substring(line, kmtidx, rocidx - 1).trim())
//						.longValue();
//				usertime += Long.valueOf(
//						Bytes.substring(line, umtidx, wocidx - 1).trim())
//						.longValue();
//			}
//			retn[0] = idletime;
//			retn[1] = kneltime + usertime;
//			return retn;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			try {
//				proc.getInputStream().close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	public int getTroughPut(String tomcatPath) {
		int throughPut = 0;
		String prefix = "localhost_access_log.";
		String sufix = ".log";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String now = format.format(new Date());
		InputStream in = null;
		BufferedReader br = null;
		String tomcatLogPath = tomcatPath + "\\logs\\" + prefix + now + sufix;
		try {

			in = new BufferedInputStream(new FileInputStream(tomcatLogPath));

			br = new BufferedReader(new InputStreamReader(in));
			String temp = br.readLine();
//			String temp1 = null;
			String[] strs = null;
			String[] strs1 = temp.split(" ");
			int flow = 0;
//			String time;

			while (temp != null) {
				strs = temp.split(" ");
				if (strs[2].equals("-")) 
					flow = 0;
				else
					flow = Integer.parseInt(strs[2]);

				// System.out.println(flow);

				/*
				 * for test
				 * 
				 * for(int i=0;i<strs.length;i++){ System.out.println(strs[i]);
				 * }
				 */

				if (strs[0].equals(strs1[0]))
					throughPut += flow;
				else
					throughPut = flow;

				strs1 = strs;
				temp = br.readLine();
			}

			br.close();
			in.close();

		} catch (Exception e) {

		}
		return throughPut;
	}

	public String getTomcatAccessLogPath(String path) {
		String prefix = "localhost_access_log.";
		String sufix = ".log";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String now = format.format(new Date());

		String tomcatAccessLogPath = path + "logs/" + prefix + now + sufix;


		return tomcatAccessLogPath;
	}

	public String getTomcatPath() {
		return tomcatPath;
	}

	public void setTomcatPath(String tomcatPath) {
		this.tomcatPath = tomcatPath;
	}
	
	public static void main(String[] args) {
//		MonitorServiceImpl imp = new MonitorServiceImpl();
//		String osVersion = System.getProperty("os.version");
//		System.out.println(osVersion);
//		try {
//			MonitorInfoBean bean = imp.getMonitorInfoBean();
//			logger.info(bean.getCpuRatio() + "\t" + bean.getFreePhysicalMemorySize());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		DecimalFormat df = new DecimalFormat("0.0");
		double d = 123.90001; 
		String db = df.format(d);
		System.out.println(Double.parseDouble( db ));
	}
}
