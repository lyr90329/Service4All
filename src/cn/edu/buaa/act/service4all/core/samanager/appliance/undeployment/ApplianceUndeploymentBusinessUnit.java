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
package cn.edu.buaa.act.service4all.core.samanager.appliance.undeployment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.messaging.util.AXIOMUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.SAManagerBusinessUnit;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.appliance.deployment.ApplianceDeploymentMsgUtil;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceListener;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceUndeploymentEvent;

public class ApplianceUndeploymentBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( ApplianceUndeploymentBusinessUnit.class );

	public final static String IS_SUCCESS_DEPLOY = "isSuccessful";
	public final static String EXCEP_DESP = "description";

	public final static String APPLIANCE_ID = "applianceId";
	public final static String UNDEPLOYMENT_TYPE = "type";


	@Override
	public void dispatch( ExchangeContext context ) {

		ApplianceUndeploymentEvent event = createApplianceUndeploymentEvent( context );

		List<ApplianceListener> ls = this.getApplianceListeners();
		for (ApplianceListener l : ls) {
			try {
				// get host info from ApplianceManager
				l.undeployAppliance( event );

			} catch (ApplianceException e) {
				logger.error( "Can't deploy the appliance: " + event.getType() );
				// send the exception message to the client
				context.storeData( IS_SUCCESS_DEPLOY, "false" );
				context.storeData( EXCEP_DESP, e.getMessage() );

				try {
					this.getReceiver().sendResponseMessage( context );
				} catch (MessageExchangeInvocationException e1) {
					this.handleInvocationException( e1 );
				}
				return;
			}
		}
		if (event.isResult()) {
			try {
				sendUndeployment2Host( event, context );
			} catch (ApplianceException e) {
				context.storeData( IS_SUCCESS_DEPLOY, "false" );
				context.storeData( EXCEP_DESP, e.getMessage() );
			}

		} else {

			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP, event.getDesp() );
		}

		try {
			this.getReceiver().sendResponseMessage( context );
		} catch (MessageExchangeInvocationException e1) {
			this.handleInvocationException( e1 );
		}
	}


	protected void sendUndeployment2Host( ApplianceUndeploymentEvent event,
			ExchangeContext context ) throws ApplianceException {

		Host host = event.getHost();

		String hostId = host.getDesp().getId();
		String undeployUrl = host.getDesp().getUndeployEPR();
		String undeployOp = host.getDesp().getUndeployOperation();
		String undeployType = event.getType();

		String subApplianceId = event.getTargetAppliance();

		int type;
		if (undeployType.equals( "axis2" )) {
			type = 4;
		} else if (undeployType.equals( "tomcat" )) {
			type = 1;
		} else {
			logger.warn( "The undeploy type is not supported!" );
			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP,
					"The undeploy type is not supported!" );
			return;
		}

		String port = this.getPortFromApplianceId( subApplianceId );
		if (port == null) {
			logger.warn( "The target appliance is host so can't undeployed!" );
			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP,
					"The target appliance is host so can't undeployed!" );
			return;
		}

		// need to indicate the port of the target appliance
		OMElement req = ApplianceDeploymentMsgUtil.unDeployMsgEncp( type, port );
		try {
			OMElement resp = send2Host( undeployUrl, undeployOp, req );

			if (resp == null) {
				logger.warn( "The appliance undeployment response from host is null: "
						+ subApplianceId );
				context.storeData( IS_SUCCESS_DEPLOY, "false" );
				context.storeData( EXCEP_DESP,
						"The appliance undeployment response from host is null: "
								+ subApplianceId );
				return;
			}

			logResponseFromHost( resp );

			if (!isSuccessfulDeployment( resp )
					|| !deleteAppliance( resp, hostId, subApplianceId,
							undeployType )) {

				logger.warn( "The " + undeployType
						+ "appliance deployment fails for the Host: " + hostId );
				context.storeData( IS_SUCCESS_DEPLOY, "false" );
				context.storeData( EXCEP_DESP, "The " + undeployType
						+ "appliance deployment fails for the Host: " + hostId );

			}

		} catch (AxisFault e) {
			logger.error( e.getMessage() );
			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP, e.getMessage() );
		}
	}


	private void logResponseFromHost( OMElement resp ) {
		File tmp = new File( "applianceUndeploymentResponse.xml" );

		try {
			if (!tmp.exists()) {
				logger.info( "Create a new temp file: " + tmp.getAbsolutePath() );
				tmp.createNewFile();
			}
			FileOutputStream output = new FileOutputStream( tmp );
			resp.serialize( output );
			output.close();

		} catch (IOException e) {
			logger.warn( "Can't log the appliance response from Host: "
					+ e.getMessage() );
		} catch (XMLStreamException e) {
			logger.warn( "Can't log the appliance response from Host: "
					+ e.getMessage() );
		}

	}


	/**
	 * get the port
	 * 
	 * @param applianceId
	 * @return
	 */
	private String getPortFromApplianceId( String applianceId ) {
		int firstIndex = applianceId.indexOf( ":" );
		int lastIndex = applianceId.lastIndexOf( ":" );

		if (firstIndex == lastIndex) {
			return null;
		}

		return applianceId.substring( lastIndex );
	}


	/**
	 * 
	 * @param resp
	 * @param hostId
	 * @param applianceId
	 * @param undeployType
	 * @return
	 * @throws ApplianceException
	 */
	protected boolean deleteAppliance( OMElement resp, String hostId,
			String applianceId, String undeployType ) throws ApplianceException {

		logger.info( "Delete sub appliance by receiving the OMElement response: "
				+ applianceId );

		if (applianceManager instanceof HostManager) {
			HostManager hostManager = (HostManager) this.applianceManager;
			// hostManager.addSubAppliance(applianceType, host,
			// deployedAppliance);
			// delete sub appliance from host manager
			return hostManager.deleteSubAppliance( hostId, applianceId,
					undeployType );

		} else {
			logger.warn( "The target ApplianceManager is not Host Manager!" );
			return false;
		}
	}


	protected boolean isSuccessfulDeployment( OMElement resp ) {

		OMElement isElement = AXIOMUtils.getChildElementByTagName( resp,
				"isSuccessful" );
		return Boolean.valueOf( isElement.getText() );

	}


	protected OMElement send2Host( String deployUrl, String op, OMElement msg )
			throws AxisFault {
		ServiceClient serviceClient = new ServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference( deployUrl );

		options.setTo( targetEPR );
		options.setAction( "urn:" + op );
		serviceClient.setOptions( options );

		return serviceClient.sendReceive( msg );

	}


	protected ApplianceUndeploymentEvent createApplianceUndeploymentEvent(
			ExchangeContext context ) {

		ApplianceUndeploymentEvent event = new ApplianceUndeploymentEvent();

		event.setTargetAppliance( (String) context.getData( APPLIANCE_ID ) );
		event.setType( (String) context.getData( UNDEPLOYMENT_TYPE ) );

		return event;
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
