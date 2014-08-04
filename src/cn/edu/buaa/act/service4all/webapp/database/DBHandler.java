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
package cn.edu.buaa.act.service4all.webapp.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cn.edu.buaa.act.service4all.webapp.WebApplication;

public class DBHandler {
	private final static Log logger = LogFactory.getLog(DBHandler.class);
	public void insertNewApp(String appID, String appName, String url,
			int repetition, String status, String userName) {
		Connection conn = null;
		Statement statement = null;
		String sql = "INSERT INTO webapp(app_id,app_name,url,repetition,state,owner) VALUES ('"
				+ appID
				+ "','"
				+ appName
				+ "','"
				+ url
				+ "',"
				+ repetition
				+ ",'" 
				+ status
				+ "','"
				+ userName
				+ "');";
		logger.info(sql);
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	public void deleteAppByID(String appID) {
		Connection conn = null;
		Statement statement = null;
		String sql = "DELETE FROM webapp WHERE app_id = '" + appID + "';";
		logger.info(sql);
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}

	//----------------------------DRPS 201308----------------------------------//
	/*public List<WebApplication> queryAppByUser(String userName) {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM webapp WHERE owner = '" + userName + "'";
		conn = ConnectionPool.getInstance().getConnection();
		List<WebApplication> applications = new ArrayList<WebApplication>();
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("app_id");
				WebApplication application = new WebApplication(id);
				application.setUrl(rs.getString("url"));
				application.setServiceName(rs.getString("app_name"));
				applications.add(application);
			}
			return applications;
		} catch (Exception e) {
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
		return new ArrayList<WebApplication>();
	}*/
	//------------------------------------------------------------------------//

	
	public void deleteAppByUrl(String url) {
		Connection conn = null;
		Statement statement = null;
		String sql = "DELETE FROM webapp WHERE url = '" + url + "';";
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	
	//this is a new code for nginx
	//----------------------------DRPS 201308----------------------------------//
	/**
	 * delete webapp's information from database by appID
	 * @param appID
	 */
	public void deleteAppRPByID(String appID) {
		Connection conn = null;
		Statement statement = null;
		String sql = "DELETE FROM webAppRP WHERE app_id = '" + appID + "';";
		logger.info(sql);
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	
	/**
	 * insert new info into database;
	 * @param appID
	 * @param appName
	 * @param invokeurl
	 * @param userName
	 */
	public void insertNewAppRP(String sql) {
		Connection conn = null;
		Statement statement = null;
		logger.info(sql);
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}
	/*public void insertNewAppRP(String appID, String appName, String invokeurl, String userName) {
		Connection conn = null;
		Statement statement = null;
		String sql = "INSERT INTO webAppRP(app_id,app_name,invokeurl,owner) VALUES ('"
				+ appID
				+ "','"
				+ appName
				+ "','"
				+ invokeurl
				+ "','"
				+ userName
				+ "');";
		logger.info(sql);
		conn = ConnectionPool.getInstance().getConnection();
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
	}*/
	
	/**
	 * get appinfo from database by userName
	 * @param userName
	 * @return
	 */
	public List<WebApplication> queryAppByUser(String userName) {
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM webAppRP WHERE owner = '" + userName + "'";
		conn = ConnectionPool.getInstance().getConnection();
		List<WebApplication> applications = new ArrayList<WebApplication>();
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("app_id");
				WebApplication application = new WebApplication(id);
				application.setUrl(rs.getString("invokeurl"));
				application.setServiceName(rs.getString("app_name"));
				applications.add(application);
			}
		} catch (Exception e) {
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				ConnectionPool.getInstance().releaseConnection(conn);
		}
		return applications;
	}
	//------------------------------------------------------------------------//
}
