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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.thread;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.database.ConnectionPool;



public class Store2DatabaseThread implements Runnable {
	private static final Log logger = LogFactory
			.getLog(Store2DatabaseThread.class);
	private String serviceID;
	private String wsdlUrl;
	private String startTimeString;
	private long lastTime;
	private boolean isSuccessful;



	public Store2DatabaseThread(String serviceID, String wsdlUrl,long startTime,
			boolean isSuccessful) {
		this.serviceID = serviceID;
		this.wsdlUrl = wsdlUrl;
		this.isSuccessful = isSuccessful;
		
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		this.startTimeString = sdFormat.format(new Date(startTime));

		
		this.lastTime = System.currentTimeMillis() - startTime;
	}

	@Override
	public void run() {
		if (serviceID == null) {
			logger.warn("serviceID is null");
		}
		Connection connection = ConnectionPool.getInstance().getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String insertSql = "insert into execution_results (serviceID,execution_url,startDate,lastTime,isSuccessful) VALUES ('"
					+ serviceID
					+ "','"
					+ wsdlUrl
					+ "','"
					+ startTimeString
					+ "','" + lastTime + "','" + isSuccessful + "')";
			statement.execute(insertSql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (connection != null)
				ConnectionPool.getInstance().releaseConnection(connection);
		
		}
	}

}
