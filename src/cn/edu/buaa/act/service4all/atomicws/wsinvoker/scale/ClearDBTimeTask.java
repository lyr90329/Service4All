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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker.scale;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.database.ConnectionPool;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;

public class ClearDBTimeTask extends TimerTask {
	private final static Log logger = LogFactory.getLog(ClearDBTimeTask.class);
	Connection con = null;
    Statement stmt = null;
    private static final long timeWindow =  WSInvokeConstants.getWSInvokeConstants().getClearDBPeriod()/1000;
	private static ConnectionPool connectionPool = ConnectionPool.getInstance();
	
	@Override
	public void run() {
		logger.info("Delete the overdue invoking info");
		con = connectionPool.getConnection();
	    try {
			stmt = con.createStatement();
			String sql = "delete From execution_results where (UNIX_TIMESTAMP(NOW())-"+timeWindow+") > UNIX_TIMESTAMP(startDate)";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(con!= null) connectionPool.releaseConnection(con);
		}
	}

}
