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
package cn.edu.buaa.act.service4all.core.localsamanager.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class LogManager {

	private static Logger logger = Logger.getLogger( LogManager.class
			.getName() );

	public void setInvoker( OMElement userMsg ) {
		WebServiceCallingLog fb = new WebServiceCallingLog();
		for (Iterator<?> iterator = userMsg.getChildElements(); iterator
				.hasNext();) {
			OMElement childElement = (OMElement) iterator.next();
			if (childElement.getLocalName().equalsIgnoreCase( "user" )) {
				for (Iterator<?> atIter = childElement.getAllAttributes(); atIter
						.hasNext();) {
					OMAttribute attrs = (OMAttribute) atIter.next();
					if (attrs.getLocalName().equalsIgnoreCase( "ID" )) {
						fb.setUserID( attrs.getAttributeValue() );
					}
					if (attrs.getLocalName().equalsIgnoreCase( "Type" ))
						fb.setUserType( attrs.getAttributeValue() );
				}
			}

			if (childElement.getLocalName().equalsIgnoreCase( "url" )) {
				fb.setUrl( childElement.getText() );
			}

			try {
				String url = fb.getUrl();
				String serviceID = url.split( "/" )[url.split( "/" ).length - 1];
				File file = new File( System.getProperty( "user.dir" ).replace(
						"\\", "/" )
						+ "/log/WSCallingLog/" + serviceID + ".txt" );
				OutputStreamWriter out = null;
				out = new OutputStreamWriter( new FileOutputStream( file ) );
				out.flush();
				out.close();
			} catch (Exception e) {
			}
		}
	}


	public OMElement getSystemLog() {
		String systemLogFilePath = System.getProperty( "user.dir" ).replace(
				'\\', '/' )
				+ "/log/" + "Systemlog.txt";
		// DataHandler dh = new DataHandler(dataSource);
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMElement systemLog = omFactory.createOMElement( "systemLog", null );
		OMElement applianceIP = omFactory.createOMElement( "HostIP", null );
		applianceIP.setText( Constants.LOCAL_IP_WITHOUT_HTTP );
		systemLog.addChild( applianceIP );
		Date dt = new Date();
		SimpleDateFormat systemLogTimeStamp = new SimpleDateFormat( "yyyyMMdd" );
		String systemLogTime = systemLogTimeStamp.format( dt );
		OMElement systemLogRecordTime = omFactory.createOMElement(
				"systemLogTime", null );
		systemLogRecordTime.setText( systemLogTime );
		systemLog.addChild( systemLogRecordTime );
		File systemLogFile = new File( systemLogFilePath );
		FileDataSource systemLogDataSource = new FileDataSource( systemLogFile );
		DataHandler dh = new DataHandler( systemLogDataSource );
		OMText systemLogText = omFactory.createOMText( dh, true );
		OMElement systemLogContent = omFactory.createOMElement(
				"systemLogContent", null );
		systemLogContent.addChild( systemLogText );
		systemLog.addChild( systemLogContent );
		return systemLog;
	}


	public OMElement getUserLog(String userID,String appliancePort, String webServiceName) {
		OMFactory omf = OMAbstractFactory.getOMFactory();
		OMElement userDebugLog = omf.createOMElement( "UserDebugLog", null );
		OMElement applianceIP = omf.createOMElement( "HostIP", null );
		OMElement webServiceNameElement = omf.createOMElement(
				"WebServiceName", null );
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			String hostIP = addr.getHostAddress().toString();
			applianceIP.setText( hostIP );
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		userDebugLog.addChild( applianceIP );
		OMElement userIDElement = omf.createOMElement( "UserID", null );
		userIDElement.setText( userID );
		userDebugLog.addChild( userIDElement );
		OMElement userLogRecordTime = omf.createOMElement(
				"UserDebugLogRecordTime", null );
		Date dt = new Date();
		SimpleDateFormat systemLogTimeStamp = new SimpleDateFormat(
				"yyyyMMdd_HHmmss" );
		String wsCallingLogTime = systemLogTimeStamp.format( dt );
		userLogRecordTime.setText( wsCallingLogTime );
		userDebugLog.addChild( userLogRecordTime );
		String userDebugFilePath = System.getProperty( "user.dir" ).replace(
				"\\", "/" )
				+ "/DebugLog";
		File logFileFolder = new File( userDebugFilePath );
		if (logFileFolder.isDirectory()) {
			String[] userLogFileList = logFileFolder.list();

			for (int i = 0; i < userLogFileList.length; i++) {
				if (userLogFileList[i]
						.equals( userID + webServiceName + ".log" )) {

					webServiceNameElement.setText( webServiceName );
					userDebugLog.addChild( webServiceNameElement );
					File userDebugLogFile = new File( userDebugFilePath + "/"
							+ userLogFileList[i] );
					FileDataSource userDebugLogDataSource = new FileDataSource(
							userDebugLogFile );
					DataHandler dh = new DataHandler( userDebugLogDataSource );
					OMText userDebugLogText = omf.createOMText( dh, true );
					OMElement userDebugLogContent = omf.createOMElement(
							"UserDebugLogContent", null );
					userDebugLogContent.addChild( userDebugLogText );
					userDebugLog.addChild( userDebugLogContent );
				} else
					logger.warn( "[System Information][Management Operation][getUserDebugLogFileFromHost]"
									+ "Can not find such a debug log file, please check your 'userID' case matters" );
			}
			return userDebugLog;
		} else {
			logger.warn( "[System Information][Management Operation][getUserDebugLogFileFromHost]Did not find "
							+ userID
							+ "'s debug logs,please check the userID,appliancePort and webServiceName" );
			return userDebugLog;
		}
	}
}
