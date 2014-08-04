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
package cn.edu.buaa.act.service4all.core.localsamanager.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import cn.edu.buaa.act.service4all.core.localsamanager.monitoringinfo.MonitorServiceImpl;

public final class Constants {
	public final static String LOCAL_SM_PORT = Config.getInstance().getProperty("localSMPort");//"8080";
	public final static String LOCAL_SM_SUFFIX = Config.getInstance().getProperty("localSMSuffix");//"";
	
	public final static String URL_SA_MANAGE = Config.getInstance().getProperty("saManagerUrl");
	public final static String AXIS2_SOURCE_PATH = Config.getInstance().getProperty("axis2sourcePath");
	public final static String TOMCAT_SOURCE_PATH = Config.getInstance().getProperty("tomcatsourcePath");
	public final static String BPMN_SOURCE_PATH = Config.getInstance().getProperty("bpmnsourcePath");
	public final static String SA_FOLDER = Config.getInstance().getProperty("safolder");
	public final static String TOMCAT_SUFFIX = Config.getInstance().getProperty("tomcatsuffix");	
	public final static long PC_TIMEOUT = Long.parseLong(Config.getInstance().getProperty("pcTimeout"));;
	public final static long REGISTER_TIME = Long.parseLong(Config.getInstance().getProperty("registerTime"));	
	public final static long STATUS_CHECK_TIMEOUT = Long.parseLong(Config.getInstance().getProperty("statusCheckerTimeout"));
	public final static String REPOSITORY = Config.getInstance().getProperty("repositoryPath");
	public final static String ENTRY = Config.getInstance().getProperty("entryPath");
	
	public final static String SYSTEM_TYPE = getSystemType();
	private static String ETH = Config.getInstance().getProperty("eth");
	public final static String LOCAL_IP_WITH_HTTP = getIPWithHttp();
	public final static String LOCAL_IP_WITHOUT_HTTP = getIPWithoutHttp();
	
	/**
	 * these constants refers to the message type received from SA Manager
	 */
	public static final int AppServerDeploy = 0;
	public static final int AppServerUnDeploy = 1;
	public static final int AppServerRestart = 2;
	public static final int Axis2Deploy = 3;
	public static final int Axis2UnDeploy = 4;
	public static final int Axis2Restart = 5;
	public static final int VmHostRestart= 9;
	public static final int BpmnDeploy = 6;
	
	public static final String ip = "ip";
	public static final String ipList = "iplist";
	public static final String url = "url";
	public static final String testEngineList = "testEngineList";
	
	private static Logger logger = Logger.getLogger( Constants.class );
	
	
	private static String getSystemType(){
		Properties prop = System.getProperties();
		//see http://lopica.sourceforge.net/os.html
		return prop.getProperty( "os.name" );		
	}
	
	private static String getIPWithoutHttp(){  
    	String ip="";
        Enumeration<NetworkInterface> allNetInterfaces;  
        try {  
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
  
            InetAddress ip0 = null;    
            while (allNetInterfaces.hasMoreElements())
            {  
                NetworkInterface netInterface = allNetInterfaces.nextElement(); 
                if(null == ETH || ETH.equals( "" )){//默认eth0
                	System.out.println("Going to get ip from eth0");
                	ETH = "eth0";
                }
                if(netInterface.getName().equals(ETH))
                {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); 
                    while (addresses.hasMoreElements())  
                    {  
                        ip0 = addresses.nextElement();  
                        if (ip0 != null && (ip0 instanceof Inet4Address)) 
                        {  
                        	ip= ip0.getHostAddress();
                            return ip;
                        }   
                   }
                }
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        }  
       return "";
		
//		  String sIP = "";
//		  InetAddress ip = null;  
//		  try {
//		    boolean bFindIP = false;
//		    Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
//		      .getNetworkInterfaces();
//		    while (netInterfaces.hasMoreElements()) {
//		     if(bFindIP){
//		      break;
//		     }
//		     NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
//		     //----------特定情况，可以考虑用ni.getName判断
//		     //遍历所有ip
//		     Enumeration<InetAddress> ips = ni.getInetAddresses();
//		     while (ips.hasMoreElements()) {
//		      ip = (InetAddress) ips.nextElement();
//		      if( ip.isSiteLocalAddress()   
//		                 && !ip.isLoopbackAddress()   //127.开头的都是lookback地址
//		                 && ip.getHostAddress().indexOf(":")==-1){
//		    	  
//		          bFindIP = true;
//		             break;   
//		         } 
//		     }
//
//		    }
//		  } 
//		  catch (Exception e) {
//		   e.printStackTrace();
//		  }  
//
//		  if(null != ip){
//		   sIP = ip.getHostAddress();
//		  }
//		  return sIP;
    }
	
	private static String getIPWithHttp()  {
		String ip="";
        Enumeration<NetworkInterface> allNetInterfaces;  
        try {  
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
  
            InetAddress ip0 = null;    
            while (allNetInterfaces.hasMoreElements())
            {  
                NetworkInterface netInterface = allNetInterfaces.nextElement();  
                if(null == ETH || ETH.equals( "" )){//默认eth0
                	System.out.println("Going to get ip from eth0");
                	ETH = "eth0";
                }
                if(netInterface.getName().equals(ETH))
                {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); 
                    while (addresses.hasMoreElements())  
                    {  
                        ip0 = addresses.nextElement();  
                        if (ip0 != null && (ip0 instanceof Inet4Address)) 
                        {  
                        	ip= ip0.getHostAddress();
                        	ip="http://"+ip;
                            return ip;
                        }   
                   }
                }
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        }  
       return "";
		
//		  String sIP = "";
//		  InetAddress ip = null;  
//		  try {
//		    boolean bFindIP = false;
//		    Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
//		      .getNetworkInterfaces();
//		    while (netInterfaces.hasMoreElements()) {
//		     if(bFindIP){
//		      break;
//		     }
//		     NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
//		     //----------特定情况，可以考虑用ni.getName判断
//		     //遍历所有ip
//		     Enumeration<InetAddress> ips = ni.getInetAddresses();
//		     while (ips.hasMoreElements()) {
//		      ip = (InetAddress) ips.nextElement();
//		      if( ip.isSiteLocalAddress()   
//		                 && !ip.isLoopbackAddress()   //127.开头的都是lookback地址
//		                 && ip.getHostAddress().indexOf(":")==-1){
//		          bFindIP = true;
//		             break;   
//		         } 
//		     }
//
//		    }
//		  } 
//		  catch (Exception e) {
//		   e.printStackTrace();
//		  }  
//
//		  if(null != ip){
//		   sIP = ip.getHostAddress();
//		   sIP = "http://"+sIP;
//		  }
//		  return sIP;
	}
}
