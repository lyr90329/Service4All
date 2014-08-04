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
package cn.edu.buaa.act.service4all.core.localsamanager.appliance.registry;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import cn.edu.buaa.act.service4all.core.localsamanager.appliance.TomcatInfo;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;

public class SADeploymentMsg {

	private String deploymentIsSuccessful = null;

	private String unDeploymentIsSuccessful = null;

	private String deploymentDescription = null;

	private String unDeploymentDescription = null;


	public OMElement createSADeploymentResponse( TomcatInfo tomcatInfo,
			String applianceType ) {
		String hostIP = Constants.LOCAL_IP_WITH_HTTP;
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement saDeployment = fac.createOMElement( "saDeploymentResponse",
				null );
		saDeployment.addAttribute( "type", applianceType, null );
		OMElement applianceIP = fac.createOMElement( "ip", null );
		applianceIP.setText( hostIP );
		saDeployment.addChild( applianceIP );
		OMElement appliancePort = fac.createOMElement( "port", null );
		appliancePort.setText( String.valueOf(tomcatInfo.getPort() ));
		saDeployment.addChild( appliancePort );
		OMElement isSuccessful = fac.createOMElement( "isSuccessful", null );
		isSuccessful.setText( this.deploymentIsSuccessful );
		saDeployment.addChild( isSuccessful );
		OMElement decription = fac.createOMElement( "decription", null );
		decription.setText( this.deploymentDescription );
		saDeployment.addChild( decription );
		return saDeployment;
	}


	public OMElement createSAUndeploymentResponse( TomcatInfo tomcatInfo,
			String applianceType ) {
		String hostIP = Constants.LOCAL_IP_WITH_HTTP;
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement saUndeployment = fac.createOMElement(
				"saUndeploymentResponse", null );
		saUndeployment.addAttribute( "type", applianceType, null );
		OMElement applianceIP = fac.createOMElement( "ip", null );
		applianceIP.setText( hostIP );
		saUndeployment.addChild( applianceIP );
		OMElement appliancePort = fac.createOMElement( "port", null );
		appliancePort.setText( String.valueOf(tomcatInfo.getPort() ));
		saUndeployment.addChild( appliancePort );
		OMElement isSuccessful = fac.createOMElement( "isSuccessful", null );
		isSuccessful.setText( this.unDeploymentIsSuccessful );
		saUndeployment.addChild( isSuccessful );
		OMElement decription = fac.createOMElement( "decription", null );
		decription.setText( this.unDeploymentDescription );
		saUndeployment.addChild( decription );
		return saUndeployment;
	}


	public String getDeploymentIsSuccessful() {
		return deploymentIsSuccessful;
	}


	public void setDeploymentIsSuccessful( String deploymentIsSuccessful ) {
		this.deploymentIsSuccessful = deploymentIsSuccessful;
	}


	public String getUnDeploymentIsSuccessful() {
		return unDeploymentIsSuccessful;
	}


	public void setUnDeploymentIsSuccessful( String unDeploymentIsSuccessful ) {
		this.unDeploymentIsSuccessful = unDeploymentIsSuccessful;
	}


	public String getDeploymentDescription() {
		return deploymentDescription;
	}


	public void setDeploymentDescription( String deploymentDescription ) {
		this.deploymentDescription = deploymentDescription;
	}


	public String getUnDeploymentDescription() {
		return unDeploymentDescription;
	}


	public void setUnDeploymentDescription( String unDeploymentDescription ) {
		this.unDeploymentDescription = unDeploymentDescription;
	}
}
