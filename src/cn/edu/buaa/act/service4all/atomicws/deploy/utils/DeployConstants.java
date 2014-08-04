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
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeployConstants {
	private static Log logger = LogFactory.getLog(DeployConstants.class);

	private String checkSandbox = null;
	private String checkFb2Bus = "true";
	private double pFailure = 0.2;
	private int step = 1;
	private String checkFb2UserAuthentication = "true";
	private int maxRepetionNum = 8;

	private double cpuRatio = 0.3;
	private double memoryRatio = 0.3;
	private double throughputRatio = 0.4;

	private String userRightAuthenticationUrl = null;
	private String userRightNotifyUrl = null;
	private String updateEpr = "http://192.168.3.242:8192/WebServicesUpdate/";

	public String getUpdateEpr() {
		return updateEpr;
	}

	public double getCpuRatio() {
		return cpuRatio;
	}

	public String getUserRightAuthenticationUrl() {
		return userRightAuthenticationUrl;
	}

	public String getUserRightNotifyUrl() {
		return userRightNotifyUrl;
	}

	public double getMemoryRatio() {
		return memoryRatio;
	}

	public double getThroughputRatio() {
		return throughputRatio;
	}

	private static DeployConstants instance = null;

	private DeployConstants() {
		readConfig();
	}

	synchronized public static DeployConstants getInstance() {
		if (instance == null)
			instance = new DeployConstants();
		return instance;
	}

	public String getCheckFb2UserAuthentication() {

		return checkFb2UserAuthentication;
	}

	public int getMaxRepetionNum() {

		return maxRepetionNum;
	}

	public String getCheckSandbox() {

		return checkSandbox;
	}

	public String getCheckFb2Bus() {

		return checkFb2Bus;
	}

	public double getP() {

		return pFailure;
	}

	public int getStep() {

		return step;
	}

	private void readConfig() {
		logger.info("Readling config");
		String path = System.getProperty("user.dir")
				+ "/conf/WSDeployUndeploy.properties";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		checkSandbox = props.getProperty("checkSandbox");
		checkFb2Bus = props.getProperty("checkFb2Bus");
		pFailure = Double.parseDouble(props.getProperty("pFailure"));
		step = Integer.parseInt(props.getProperty("STEP"));
		checkFb2UserAuthentication = props
				.getProperty("checkFb2UserAuthentication");
		maxRepetionNum = Integer.parseInt(props.getProperty("MAXREPETIONNUM"));

		updateEpr = props.getProperty("UpdateEPR");

		cpuRatio = Double.parseDouble(props.getProperty("cpuRatio"));
		memoryRatio = Double.parseDouble(props.getProperty("memoryRatio"));
		throughputRatio = Double.parseDouble(props
				.getProperty("throughputRatio"));

		userRightNotifyUrl = props.getProperty("userRightNotifyUrl");
		userRightAuthenticationUrl = props
				.getProperty("userRightAuthenticationUrl");

	}

	public static final String RESPONSE = "response";
	public static final String SERVICEID = "serviceID";
	public static final String TYPE = "type";
	public static final String TRUE = "true";
	public static final String DEPLOY = "deploy";
	public static final String QUERYACTIVATE = "queryActivate";
	public static final String ACTIVATED = "activated";
	public static final String REPLICAACQUISITION = "replicaAcquisition";

	public static final String REQ4CONTAINER = "req";
	public static final String DEPLOYNUM = "deployNum";
	public static final String DEPLOYWSNAME = "deployWSName";
	public static final String USERNAME = "userName";
	public static final String SANDBOXERROR = "sandbox-error";
	public static final String INVOKEURL = "InvokeUrl";
	public static final String SERVICENAME = "serviceName";

	public static final String ISSUCCESSFUL = "isSuccessful";

	public static final String INFO = "info";

	public static final String FALSE = "false";
	public static final String HASAVILABLEREPLICAS = "hasAvailableReplicas";

	public static final String TOUNDEPLOYREPLICA = "toUndeployReplica";

	public static final String UNDEPLOYEDREPLICA = "undeployedReplica";
	public static final String FORQUERY = "forQuery";

	public static final String REPLICARELEASE = "replicaRelease";
	public static final String UNDEPLOY = "undeploy";

	public static final String STARTTIME = "startTime";

	public static final String DEPLOYEDRESULTS = "deployedResults";
	public static final String DEPLOY_PORT = "8080";
}
