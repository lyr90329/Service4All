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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WSInvokeConstants {
	private static Log logger = LogFactory.getLog(WSInvokeConstants.class);
	private boolean isScalable = false;
	// private static boolean hasInit = false;
	private boolean isByBus = false;
	private boolean isByDatabase = false;
	private boolean isFb2Container = false;
	private long period = 10000;
	private String deployEPR = null;
	private String undeployEPR = null;
	private double alpha = 0.2;
	private boolean isRoudRobin = true;
	private boolean isLog = false;
	private boolean isReviseHTML = true;
	private long clearDBPeriod = 0;
	private int upP = 0;
	private int upQ = 0;
	private int downP = 0;
	private int downQ = 0;
	private double upFactor = 1.5;
	private double downFactor = 0.5;
	private int maxRepetionNum = 8;

	public int getMaxRepetionNum() {
		return maxRepetionNum;
	}
	private static WSInvokeConstants instance = null;

	synchronized public static WSInvokeConstants getWSInvokeConstants() {
		if (instance == null) {
			instance = new WSInvokeConstants();
		}
		return instance;
	}

	private WSInvokeConstants() {
		readConfig();
	}

	private void readConfig() {

		logger.info("Reading config file");
		String path = System.getProperty("user.dir")
				+ "/conf/WSInvoke.properties";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isByBus = ("true".equals(props.getProperty("byBus")));
		isByDatabase = ("true".equals(props.getProperty("byDatabase")));
		isFb2Container = ("true".equals(props.getProperty("fb2Container")));
		isLog = ("true".equals(props.getProperty("log4Container")));
		isRoudRobin = ("true".equals(props.getProperty("RoudRobin")));
		isReviseHTML = ("true".equals(props.getProperty("ReviseHTML")));
		maxRepetionNum  = Integer.parseInt(props.getProperty("MAXREPETIONNUM"));
		isScalable = ("true".equals(props.getProperty("isScalable")));
		deployEPR = props.getProperty("deployEPR");
		undeployEPR = props.getProperty("undeployEPR");

		period = Long.parseLong(props.getProperty("period"));
		clearDBPeriod = Long.parseLong(props.getProperty("clearDBPeriod"));
		alpha = Double.parseDouble(props.getProperty("alpha"));
		upP = Integer.parseInt(props.getProperty("upP"));
		upQ = Integer.parseInt(props.getProperty("upQ"));
		downP = Integer.parseInt(props.getProperty("downP"));
		downQ = Integer.parseInt(props.getProperty("downQ"));
		upFactor = Double.parseDouble(props.getProperty("upFactor"));
		downFactor = Double.parseDouble(props.getProperty("downFactor"));
		
	}

	public boolean isByBus() {
		return isByBus;
	}

	public boolean isByDatabase() {
		return isByDatabase;
	}

	public String getDeployEPR() {
		return deployEPR;
	}

	public String getUndeployEPR() {
		return undeployEPR;
	}

	public boolean isScalable() {

		return isScalable;
	}

	public double getAlpha() {

		return alpha;
	}

	public boolean isRoundRobin() {

		return isRoudRobin;
	}

	public boolean isLog() {

		return isLog;
	}

	public long getPeriod() {

		return period;
	}

	public boolean isFb2Container() {

		return isFb2Container;
	}

	public boolean isReviseHTML() {
		return isReviseHTML;
	}

	public long getClearDBPeriod() {
		return clearDBPeriod;
	}

	public int getUpP() {
		return upP;
	}

	public int getUpQ() {
		return upQ;
	}

	public int getDownP() {
		return downP;
	}

	public int getDownQ() {
		return downQ;
	}
	public double getUpFactor() {
		return upFactor;
	}

	public double getDownFactor() {
		return downFactor;
	}
	public static final String STARTTIME = "startTime";

	public static final String INVOKEREQUEST = "invokeRequest";
	public static final String TYPE = "type";
	public static final String WSDL = "wsdl";
	public static final String SERVICEID = "serviceID";
	public static final String PORTTYPE = "portType";
	public static final String OPERATION = "operation";
	public static final String REQUESTSOAP = "requestSoap";
	public static final String USERID = "userID";
	public static final String USERTYPE = "userType";
	
	public static final String AVAILABLESERVICEREQUEST = "availableServiceRequest";
	public static final String WEBSERVICE = "WebService";
	public static final String AVAILABLESERVICERESPONSE = "availableServiceResponse";
	public static final String NUM = "num";
	public static final String SERVICES = "services";
	public static final String SERVICE = "service";
	public static final String URL = "url";
	public static final String CPU = "cpu";
	public static final String MEMORY = "memory";
	public static final String THROUGHPUT = "throughput";
	public static final String DEPLOYEDSERVICEAMOUNT = "deployedServiceAmount";
	public static final String DESCRIPTION = "description";
	public static final String WSINVOKATIONFEEDBACK = "WSInvokationFeedback";
	public static final String ISSUCCESSFUL = "isSuccessful";
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	public static final String STARTDATE = "startDate";
	public static final String LASTTIME = "lastTime";

	public static final String WSINVOKATIONFEEDBACK4CONTAINER = "WSInvokationFeedback4Container";
	public static final String USER = "user";
	public static final String TIME = "time";
	public static final String ENDTIME = "endTime";
	public static final String INPUTSOAP = "inputSoap";
	public static final String OUTPUTSOAP = "outputSoap";

	public static final String INDOC = "inDoc";
	public static final String URLS = "urls";
	public static final String RANK = "rank";
	public static final String SOAPNS = "http://www.w3.org/2003/05/soap-envelope";
	public static final String RESPONSESOAP = "responseSoap";
	public static final String INVOKERESPONSE = "invokeResponse";
	public static final String RESULTINFO = "resultInfo";
	public static final String HASAVAILBALESERVICE = "hasAvailableService";
	public static final String INFO = "info";

	

}
