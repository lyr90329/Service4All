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
package cn.edu.buaa.act.service4all.core.samanager.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplianceLog {
	
	private static Log logger = LogFactory.getLog(ApplianceLog.class);
	
	private static final String logFile = "appengine.log";
	
	public static void log(String logInfo){
		File log = new File(logFile);
		
		try {
			
			if(!log.exists()){
				log.createNewFile();
			}
			
			//append to the log file
			appendLog(log, logInfo);
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	private static void appendLog(File file, String logInfo){
		
		BufferedWriter bw = null;
		try {
		
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(logInfo);
			bw.newLine();
		
			bw.flush();
			
		} catch (IOException e) {
			
			logger.warn(e.getMessage());
			
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}
	
}
