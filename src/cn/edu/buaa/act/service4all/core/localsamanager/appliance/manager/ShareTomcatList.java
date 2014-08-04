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

import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;

public class ShareTomcatList {

	private LinkedList<TomcatInfo> tomcatList = null;
	private LinkedList<Integer> portList = null;

	private static ShareTomcatList sharetomcatlist = null;
	private static Logger logger = Logger.getLogger( ShareTomcatList.class );


	public static ShareTomcatList getNewInstance() {
		if (sharetomcatlist == null)
			sharetomcatlist = new ShareTomcatList();
		return sharetomcatlist;
	}


	private ShareTomcatList() {
		tomcatList = new LinkedList<TomcatInfo>();
		portList = new LinkedList<Integer>();
	}


	public TomcatInfo getTomcatInfo( int tomcatIndex ) {
		synchronized (tomcatList) {
			if (tomcatIndex >= tomcatList.size())
				logger.error( "error at get Tomcat index" );
			return tomcatList.get( tomcatIndex );
		}
	}


	public Iterator<TomcatInfo> getTomcatInfoList() {
		synchronized (tomcatList) {
			return this.tomcatList.iterator();
		}
	}


	public void setTomcatInfo( int tomcatIndex, TomcatInfo tomcatInfo ) {
		synchronized (tomcatList) {
			if (tomcatIndex >= tomcatList.size())
				logger.error( "error at get Tomcat index" );
			tomcatList.set( tomcatIndex, tomcatInfo );
		}
	}


	public void addTomcat( TomcatInfo tomcatinfo ) {
		synchronized (tomcatList) {
			tomcatList.add( tomcatinfo );
		}
	}


	public void removeTomcat( int tomcatIndex ) {
		synchronized (tomcatList) {
			if (tomcatIndex >= tomcatList.size())
				logger.error( "error at get Tomcat index" );
			tomcatList.remove( tomcatIndex );
		}
	}


	public Integer getPortNumber( int portIndex ) {
		synchronized (portList) {
			if (portIndex >= portList.size())
				logger.error( "error at get Tomcat index" );
			return portList.get( portIndex );
		}
	}


	public void addPort( int portNumber ) {
		synchronized (portList) {
			portList.add( portNumber );
		}
	}


	public void removePort( int portIndex ) {
		synchronized (portList) {
			if (portIndex >= portList.size())
				logger.error( "error at get Tomcat index" );
			portList.remove( portIndex );
		}
	}


	public int getTomcatListSize() {
		synchronized (tomcatList) {
			return tomcatList.size();
		}
	}


	public int getPortListSize() {
		synchronized (portList) {
			return portList.size();
		}
	}


	public int indexOfPort( int portNumber ) {
		return portList.indexOf( portNumber );
	}


	public int indexOfTomcat( TomcatInfo tomcatinfo ) {
		return tomcatList.indexOf( tomcatinfo );
	}
}
