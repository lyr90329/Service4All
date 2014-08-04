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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance.manager;

import java.io.IOException;

import org.apache.log4j.Logger;

public class TomcatControllerOnLinux extends TomcatController{
	private static Logger logger = Logger.getLogger( TomcatControllerOnLinux.class );
	@Override
	protected void start(String startPath) {
		try {			
		
			String command =  "chmod 777 "+startPath+"/bin/catalina.sh";
			Runtime.getRuntime().exec( command );
			command =  "chmod 777 "+startPath+"/bin/startup.sh";
			Runtime.getRuntime().exec( command );
			command = "/bin/sh "+startPath+"/bin/startup.sh";
			logger.info("before exec "+command);
			Runtime.getRuntime().exec( command );
			} catch (IOException e) {
				e.printStackTrace();
			}
	}


	@Override
	protected void shutDown(String shutDownPath) {
		try {
//			String command =   "/usr/bin/xterm "+shutDownPath + "/bin/shutdown.sh";			
			
			String command =  "chmod 777 "+shutDownPath+"/bin/shutdown.sh";
			Runtime.getRuntime().exec( command );
			command =  "chmod 777 "+shutDownPath+"/bin/catalina.sh";
			Runtime.getRuntime().exec( command );
			command =  "/bin/sh " + shutDownPath + "/bin/shutdown.sh";
			logger.info("before exec "+command);
			Runtime.getRuntime().exec( command );
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void restart(String restartPath) {
		try {
//			String command =   "/usr/bin/xterm "+restartPath + "/bin/restart.sh";
			
			String command =  "chmod 777 "+restartPath + "/bin/restart.sh";
			Runtime.getRuntime().exec( command );
			command =  "chmod 777 "+restartPath + "/bin/restart.sh";
			Runtime.getRuntime().exec( command );
			command =  "/bin/sh "+restartPath + "/bin/restart.sh";
			Runtime.getRuntime().exec( command );
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
