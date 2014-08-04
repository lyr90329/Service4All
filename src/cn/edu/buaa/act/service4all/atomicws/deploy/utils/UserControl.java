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
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

public class UserControl {

	private static final Log logger = LogFactory.getLog( UserControl.class );
	private static final String userRightNotifyUrl = DeployConstants
			.getInstance().getUserRightNotifyUrl();
	private static final String userRightAuthenticationUrl = DeployConstants
			.getInstance().getUserRightAuthenticationUrl();


	public static boolean undeployQualify( String name, String id ) {

		logger.info("Ask for undeploy authentication");
		Document doc = new Document();
		Document response;
		Element root, username, serviceId;
		XmlTUtils tool = new XmlTUtils();

		root = new Element( "undeployQualification" );
		doc.setRootElement( root );

		username = new Element( "userName" );
		username.setText( name );
		root.addContent( username );

		serviceId = new Element( "serviceId" );
		serviceId.setText( id );
		root.addContent( serviceId );

		response = tool.getResult( doc, userRightAuthenticationUrl );
		if (response.getRootElement().getChild( "qualification" ).getText()
				.equals( "permit" ))
			return true;
		return false;
	}


	public static void undeployNotification( String name, String id ) {

		Document doc = new Document();
		Element root, username, servicelist, serviceid;
		XmlTUtils tool = new XmlTUtils();

		root = new Element( "undeployService" );
		doc.setRootElement( root );
		root.setAttribute( "type", "webservice" );

		username = new Element( "userName" );
		username.setText( name );
		root.addContent( username );

		servicelist = new Element( "serviceList" );
		root.addContent( servicelist );

		serviceid = new Element( "serviceId" );
		serviceid.setText( id );
		servicelist.addContent( serviceid );

		tool.getResult( doc, userRightNotifyUrl );
	}


	public static void deployNotification( String name, String id ) {

		Document doc = new Document();
		Element root, username, servicelist, serviceid;
		XmlTUtils tool = new XmlTUtils();
		root = new Element( "deployService" );
		doc.setRootElement( root );
		root.setAttribute( "type", "webservice" );

		username = new Element( "userName" );
		username.setText( name );
		root.addContent( username );

		servicelist = new Element( "serviceList" );
		root.addContent( servicelist );

		serviceid = new Element( "serviceId" );
		serviceid.setText( id );
		servicelist.addContent( serviceid );

		tool.getResult( doc, userRightNotifyUrl );
	}

}
