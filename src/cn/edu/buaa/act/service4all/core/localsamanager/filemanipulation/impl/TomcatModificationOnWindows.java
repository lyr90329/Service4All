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
package cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.TomcatModification;

public class TomcatModificationOnWindows implements TomcatModification {
	
	private static TomcatModificationOnWindows tomcatModificationOnWindows = null;
	
	public static TomcatModificationOnWindows newInstance(){
		if(tomcatModificationOnWindows==null){
			tomcatModificationOnWindows = new TomcatModificationOnWindows();
		}
		return tomcatModificationOnWindows;
	}
	
	@Override
	public void modifyTomcatFile(TomcatInfo tomcatInfo){
		String serverXMLPath = tomcatInfo.getTomcatPath() + "\\conf\\server.xml";
		String startupPath = tomcatInfo.getTomcatPath()  + "\\bin\\startup.bat";
		String shutdownPath = tomcatInfo.getTomcatPath()  + "\\bin\\shutdown.bat";
		String catalinaPath = Constants.TOMCAT_SOURCE_PATH + "\\bin\\catalina.bat";
		modifyServerXML(serverXMLPath, tomcatInfo);
		modifyStartup(startupPath); 
		modifyShutdown(shutdownPath); 
		modifyCatalina(catalinaPath);
	}
	
	
	@Override
	public boolean modifyServerXML(String serverXMLPath, TomcatInfo tomcatInfo){
		File serverFile = new File (serverXMLPath);
		File tmpFile = new File (serverXMLPath.substring(0, serverXMLPath.indexOf("server.xml"))+"temp.xml");
		try {
			BufferedReader in = new BufferedReader( new FileReader(serverFile ) );
			StringBuffer sb= new StringBuffer("");			
			String s;
			while( (s = in.readLine()) != null) {	
				if(s.contains("8080")&& !s.contains("!--")){
					s = s.replace("8080", String.valueOf(tomcatInfo.getPort()));
				}
				if(s.contains("8005")&& !s.contains("!--")){
					s = s.replace("8005", String.valueOf(tomcatInfo.getShutdownPort()));
				}
				if(s.contains("8009")&& !s.contains("!--")){
					s = s.replace("8009", String.valueOf(tomcatInfo.getAjpPort()));
				}
				sb.append(s+"\r\n");
			}
			in.close();
			
			FileOutputStream out = new FileOutputStream( serverXMLPath.substring(0, serverXMLPath.indexOf("server.xml"))+"temp.xml");
			out.write(sb.toString().getBytes());
			out.close(); 
			
			serverFile.delete();			
			String rootPath = tmpFile.getParent();
			File newFile = new File(rootPath+ File.separator+"server.xml");
			if(tmpFile.renameTo(newFile))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean modifyCatalina( String catalinaPath ) {
		File startFile = new File( catalinaPath);
		File tmpFile = new File (catalinaPath.substring(0, catalinaPath.indexOf("catalina.bat"))+"temp.bat");			
		try {
			BufferedReader in = new BufferedReader( new FileReader(startFile ) );			
			String s;
			StringBuffer sb= new StringBuffer("");
			while ( ( s = in.readLine()) != null )
			{
				if( s.equalsIgnoreCase("set CATALINA_HOME=%CURRENT_DIR%" ) )
					s = s.replace("%CURRENT_DIR%","\""+ catalinaPath.substring(0, catalinaPath.indexOf("\\bin")) + "\"");
				if(s.equalsIgnoreCase("set CATALINA_HOME=%cd%" ))
					s = s.replace("%cd%", "\""+catalinaPath.substring(0, catalinaPath.indexOf("\\bin")) +"\"");
				sb.append(s+"\r\n");	
			}
			in.close();
			
			FileOutputStream out = new FileOutputStream( catalinaPath.substring(0, catalinaPath.indexOf("catalina.bat"))+"temp.bat");
			out.write(sb.toString().getBytes());
			out.close(); 
			
			startFile.delete();
			
			String rootPath = tmpFile.getParent();
			File newFile = new File(rootPath+ File.separator+"catalina.bat");
			if(tmpFile.renameTo(newFile))
				return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public boolean modifyShutdown( String shutdownPath ) {
		File startFile = new File( shutdownPath);
		File tmpFile = new File (shutdownPath.substring(0, shutdownPath.indexOf("shutdown.bat"))+"temp.bat");			
		try {
			BufferedReader in = new BufferedReader( new FileReader(startFile ) );			
			String s;
			StringBuffer sb= new StringBuffer("");
			while ( ( s = in.readLine()) != null )
			{
				if( s.equalsIgnoreCase("set CATALINA_HOME=%CURRENT_DIR%" ) )
					s = s.replace("%CURRENT_DIR%","\""+ shutdownPath.substring(0, shutdownPath.indexOf("\\bin")) + "\"");
				if(s.equalsIgnoreCase("set CATALINA_HOME=%cd%" ))
					s = s.replace("%cd%", "\""+shutdownPath.substring(0, shutdownPath.indexOf("\\bin")) +"\"");
				sb.append(s+"\r\n");	
			}
			in.close();
			
			FileOutputStream out = new FileOutputStream( shutdownPath.substring(0, shutdownPath.indexOf("shutdown.bat"))+"temp.bat");
			out.write(sb.toString().getBytes());
			out.close(); 
			
			startFile.delete();
			
			String rootPath = tmpFile.getParent();
			File newFile = new File(rootPath+ File.separator+"shutdown.bat");
			if(tmpFile.renameTo(newFile))
				return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public boolean modifyStartup( String startupPath ) {
		File startFile = new File( startupPath);
		File tmpFile = new File (startupPath.substring(0, startupPath.indexOf("startup.bat"))+"temp.bat");			
		try {
			BufferedReader in = new BufferedReader( new FileReader(startFile ) );			
			String s;
			StringBuffer sb= new StringBuffer("");	
			while ( ( s = in.readLine()) != null )
			{
				if( s.equalsIgnoreCase("set CATALINA_HOME=%CURRENT_DIR%" ) )
					s = s.replace("%CURRENT_DIR%", "\""+startupPath.substring(0, startupPath.indexOf("\\bin")) +"\"");
				if(s.equalsIgnoreCase("set CATALINA_HOME=%cd%" ))
					s = s.replace("%cd%", "\""+startupPath.substring(0, startupPath.indexOf("\\bin")) +"\"");
				sb.append(s+"\r\n");
			}
			in.close();
			
			FileOutputStream out = new FileOutputStream( startupPath.substring(0, startupPath.indexOf("startup.bat"))+"temp.bat");
			out.write(sb.toString().getBytes());
			out.close(); 
			
			startFile.delete();			
			String rootPath = tmpFile.getParent();
			File newFile = new File(rootPath+ File.separator+"startup.bat");
			if(tmpFile.renameTo(newFile))
				return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
