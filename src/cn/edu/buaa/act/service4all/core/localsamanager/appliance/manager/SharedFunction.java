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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.LinkedList;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class SharedFunction {

	private static Logger logger = Logger.getLogger( SharedFunction.class );
	private static int portArray[]={80,7001,25,110};
	private static int portIndex=0;
	
	public static boolean getPathANDPort( TomcatInfo tomcatInfo ) {
		LinkedList<Integer> list = new LinkedList<Integer>();

		tomcatInfo.setTomcatPath( Constants.SA_FOLDER
				+ ShareTomcatList.getNewInstance().getTomcatListSize() + "-"
				+ Constants.TOMCAT_SUFFIX );
		if (getPorts( 0, list, tomcatInfo ) && getPorts( 1, list, tomcatInfo )
				&& getPorts( 2, list, tomcatInfo )) {
			if(portIndex++ > portArray.length-1)
				return false;
			return true;
		}
		return false;
	}


	/**
	 * get available ports
	 * 
	 * @param type
	 *            0,httpPort 1,shutdownPort 2,ajpPort
	 * @param pList
	 * 
	 * @return available ports
	 */
	private static boolean getPorts( int type, LinkedList<Integer> testList,
			TomcatInfo tomcatInfo ) {

		ServerSocket tmp;
	//	int MINPORT = 5000;
		int MAXPORT = 9999;
		int port = portArray[portIndex];

		for (; port <= MAXPORT; port++) {
			try {
				if (ShareTomcatList.getNewInstance().indexOfPort( port ) == -1
						&& testList.indexOf( port ) == -1) {
					tmp = new ServerSocket( port );
					tmp.close();
					tmp = null;
					testList.add( port );
					switch (type) {
					case 0:
						tomcatInfo.setPort( port );
						break;
					case 1:
						tomcatInfo.setShutdownPort(  port  );
						break;
					case 2:
						tomcatInfo.setAjpPort(  port  );
						break;
					default:
						return false;
					}
					return true;
				}
			} catch (Exception e4) {
				logger.warn( "port " + port + " is already used." );
			}
		}
		return false;
	}


	/**
	 * get tomcat path by port
	 * 
	 * @param httpPort
	 * 
	 * @param tomcatInfo
	 * 
	 * @param tomcatIndex
	 * 
	 * @return
	 */
	public static TomcatInfo getTomcatPath( String httpPort ) {
		TomcatInfo tempInfo = new TomcatInfo();
		TomcatInfo tomcatInfo = null;
		tempInfo.setPort( Integer.parseInt(httpPort ));
		if (ShareTomcatList.getNewInstance().indexOfTomcat( tempInfo ) != -1) {
			int tomcatIndex = ShareTomcatList.getNewInstance().indexOfTomcat(
					tempInfo );
			tomcatInfo = new TomcatInfo();
			tomcatInfo = new TomcatInfo( ShareTomcatList.getNewInstance()
					.getTomcatInfo( tomcatIndex ) );
			tomcatInfo.setTomcatIndex( ShareTomcatList.getNewInstance()
					.indexOfTomcat( tempInfo ) );
		}
		return tomcatInfo;
	}


	/**
	 * check server status by web page
	 * 
	 * @param timeout
	 * @param htmlAddr
	 * @return
	 */
	public static boolean activeCheckByHtml( long timeout, String htmlAddr ) {
		long start = System.currentTimeMillis();
		while (true) {
			long end = System.currentTimeMillis();
			if (timeout < end - start)
				return false;
			if (getResponse( htmlAddr )) {
				return true;
			}
			try {
				Thread.sleep( 1000 );
				logger.info( "wait for another check" );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public static boolean inActiveCheckByHtml( long timeout, String htmlAddr ) {
		long start = System.currentTimeMillis();
		while (true) {
			long end = System.currentTimeMillis();
			if (timeout < end - start)
				return false;
			if (!getResponse( htmlAddr ))
				return true;
			try {
				Thread.sleep( 1000 );
				logger.info( "wait for another check" );
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}


	private static boolean getResponse( String htmlAddr ) {
		URL url;
		try {
			url = new URL( htmlAddr );
			HttpURLConnection connt = (HttpURLConnection) url.openConnection();
			if (connt.getResponseCode() == 404) {
				return false;
			}
			Thread.sleep( 10 );
			connt.disconnect();

		} catch (Exception e) {
			return false;
		}
		return true;
	}


	public static boolean activeCheckByHtmlBegin( long timeout, String htmlAddr ) {
		long start = System.currentTimeMillis();
		while (true) {
			long end = System.currentTimeMillis();
			if (timeout < end - start)
				return false;
			if (getResponseBegin( htmlAddr )) {
				return true;
			}
			try {
				Thread.sleep( 1000 );
				logger.info( "wait for another check" );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	private static boolean getResponseBegin( String htmlAddr ) {

		HttpMethod method;
		HttpClient client;
		String source = null;

		client = new HttpClient();
		try {
			source = new String( htmlAddr.getBytes(), "UTF-8" );
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		method = new GetMethod( source );
		try {
			client.executeMethod( method );
		} catch (HttpException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


}
