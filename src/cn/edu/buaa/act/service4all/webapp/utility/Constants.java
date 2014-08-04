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
package cn.edu.buaa.act.service4all.webapp.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Constants {

	private Constants() {
	}

	private static Log logger = LogFactory.getLog(Constants.class);
	static {
		File file = new File(Constants.constantsFile);
		if (!file.exists()) {
			logger.info("Constants.properties does not exist!");
			try {
				throw new FileNotFoundException(
						"Constants.properties does not exist!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Properties props = new Properties();
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				props.load(fis);
				if (props.getProperty("qualifyControlAddr") == null) {
					logger.info("qualification address is null");
				}
				QUALIFICATION_CONTROL = props.getProperty("qualifyControlAddr");
				if (null == props.getProperty("serviceControlAddr")) {
					logger.info("qulification address is null.");
				}
				SERVICE_CONTROL = props.getProperty("serviceControlAddr");
				if (null == props.getProperty("databaseUrl")) {
					logger.info("databaseUrl is null.");
				}
				DATABASE_URL = props.getProperty("databaseUrl");

				if (null == props.getProperty("databaseUserName")) {
					logger.info("databaseUserName is null.");
				}
				DB_USER_NAME = props.getProperty("databaseUserName");

				if (null == props.getProperty("databsePwd")) {
					logger.info("databsePwd is null.");
				}
				DB_PASSWORD = props.getProperty("databsePwd");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// database
	public static String DATABASE_URL;
	public static String DB_USER_NAME;
	public static String DB_PASSWORD;
	public static String AXIS2_URL = "axis2/services/localSAManagerService/";
	public static String QUALIFICATION_CONTROL;
	public static String SERVICE_CONTROL;

	// weight of cpu,memory,throughput and application nums in a app server
	public static double[] weights = { 0.3, 0.3, 0.4, 0.0};
	public final static String constantsFile = "conf/warConfig.properties";
	public final static String EXCEPTION = "exception";
	public final static String WAR_DEPLOY_RESPONSE = "warDeployResponse";
	public final static String TYPE = "type";
	public final static String WEB_APP = "app";
	public final static String USER_NAME = "userName";
	public final static String FILE_NAME = "fileName";
	public final static String FILE_DATA = "fileData";
	public final static String DEPLOY_NUM = "deployNum";
	public final static String CONTAINER = "container";
	public final static String CONTAINER_LIST = "containerList";
	public final static String LENGTH = "length";
	public final static String DEPLOY_URL = "deployUrl";
	public final static String ID = "id";
	public final static String CPU = "cpu";
	public final static String MEMORY = "memory";
	public final static String THROUGHPUT = "throughput";
	public final static String UNDEPLOY_AUTHEN = "undeployQualification";
	public final static String SERVICE_ID_QUALI = "serviceId";
	public final static String SERVICE_ID = "serviceID";
	public final static String PORT = "port";
	public final static String SERVICE_NAME = "serviceName";
	public final static String QUALIFICATION = "qualification";
	public final static String UNDEPLOY_SERVICE = "undeployService";
	public final static String DEPLOY_SERVICE = "deployService";
	public final static String SERVICE_LIST = "serviceList";
	public final static String DEPLOY_WEBAPP = "warDeploy";
	public final static String DEPLOY_WAR_REQUEST = "deployRequest";
	public final static String CONTAINTER_QUERY_REQUEST = "availableContainerRequestForDeployment";
	public final static String DEPLOY_NAME = "deployWSName";
	public final static String DEPLOY_RESULT = "deployedResults";
	public final static String DEPLOY_FEEDBACK = "deployFeedback";
	public final static String DEPLOY_CONTAINER = "deployedContainer";
	public final static String INVOKE_URL = "invokeUrl";
	public final static String DEPLOY_WAR_RESPONSE = "deployResponse";
	public final static String UNDEPLOY_QUERY = "undeployQuery";
	public final static String UNDEPLOY_URL = "undeployUrl";
	public final static String UNDEPLOY_QUERY_RESPONSE = "undeployQueryResponse";
	public final static String UNDEPLOY_REQEUST = "undeployRequest";
	public final static String UNDEPLOY_RESPONSE = "undeployResponse";
	public final static String UNDEPLOY_EXCEPTION = "undeployException";
	public final static String UNDEPLOY_APP_LIST = "undeployAppList";
	public final static String UNDEPLOY_RESULT = "undeployResult";
	public final static String UNDEPLOY_FEEDBACK_REQUEST = "undeployFbRequest";
	public final static String IS_SUCC = "isSuccessful";
	public final static String INFO = "info";
	public static final String TMP_FILE_PATH = "temp";
	public static final String FILE_PERFIX = "tmp_";
	public static final String FILE_SUFFIX = ".xml";
	public static final String CODE = "text/xml; charset=utf-8";
	public static final String DEPLOY_ENDPOINT = ":8080/axis2/services/localSAManagerService/deployWebApp";
	public static final String UNDEPLOY_ENDPOINT = ":8080/axis2/services/localSAManagerService/undeployWebApp";
	
	public static final String MIRROR_REPOSITORY = "FileMirror";
//	public static fianl String 
	public static final String MANUAL_SCALEOUT_RESPONSE = "manual_scaleout_response";
	public static final String SCALE_OUT_ID = "scale_out_id";
	
}
