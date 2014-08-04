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
package cn.edu.buaa.act.service4all.atomicws.servicequery.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WSQueryUpdateConstants {
private static Log logger = LogFactory.getLog(WSQueryUpdateConstants.class);
	public static final String RESPONSE = "response";
	public static final String QUERY_STATE = "query_state";
	
	private  long timeLimit  = 1000;
	public  long getTimeLimit() {
		return timeLimit;
	}
	
	private static WSQueryUpdateConstants instance = null;
	synchronized public static WSQueryUpdateConstants getInstance(){
		if(instance == null) instance = new WSQueryUpdateConstants();
		return instance;
	}
	private WSQueryUpdateConstants(){
		readConfig();
	}
	
	private  void readConfig(){
		logger.info("Reading the config file for query component");
		String path = System.getProperty("user.dir")
				+ "/conf/WSQueryUpdate.properties";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		deployEpr = props.getProperty("DeployEpr");
		timeLimit = Long.parseLong(props.getProperty("MonitorTimeOut"));
		
	}
//	public  String getDeployEpr(){
//		return deployEpr; 
//	}

}
;