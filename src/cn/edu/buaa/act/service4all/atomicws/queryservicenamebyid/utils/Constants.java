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
package cn.edu.buaa.act.service4all.atomicws.queryservicenamebyid.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Constants {
	private static final Log logger = LogFactory.getLog(Constants.class);
	public static final String SERVICEID = "serviceID";
	public static final String RESPONSEDOC = "responseDoc";
	public static final String RESPONSEFROMBUS = "responseFromBus";
	public static final String WSQueryRequestByUserName = "WSQueryRequestByUserName";
	
	private String DriverClassName;
	private String DBUser;
	private String DBPassword;
	private String DBUrl; 
	
	private static Constants instance = null;
	private Constants(){
		readConfig();
	}
	private void readConfig() {
		logger.info("Reading config file");
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
		
		DriverClassName = props.getProperty("DriverClassName");
		DBUser = props.getProperty("DBUser");
		DBPassword = props.getProperty("DBPassword");
		DBUrl = props.getProperty("DBUrl");
		
	}
	
	synchronized public static Constants getConstants(){
	
			if(instance == null){
				instance = new Constants();
			}
		return instance;
	}
	public String getDriverClassName() {
		return DriverClassName;
	}
	public String getDBUser() {
		return DBUser;
	}
	public String getDBPassword() {
		return DBPassword;
	}
	public String getDBUrl() {
		return DBUrl;
	}
}
