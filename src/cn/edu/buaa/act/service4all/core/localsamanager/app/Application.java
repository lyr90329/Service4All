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
package cn.edu.buaa.act.service4all.core.localsamanager.app;


public abstract class Application {

	public final static String WEB_SERVICE = "webservice";
	public final static String WEB_APP = "webapp";

	private String name;
	private String invocationAddr;
	private String serviceID;
//	private String containerPort;


	public Application( String name, String invocationAddr, String serviceID ) {
		super();
		this.name = name;
		this.invocationAddr = invocationAddr;
		this.serviceID = serviceID;
	}


	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	public String getInvocationAddr() {
		return invocationAddr;
	}


	public void setInvocationAddr( String invocationAddr ) {
		this.invocationAddr = invocationAddr;
	}


	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}


	/**
	 * @param serviceID
	 *            the serviceID to set
	 */
	public void setServiceID( String serviceID ) {
		this.serviceID = serviceID;
	}
}
