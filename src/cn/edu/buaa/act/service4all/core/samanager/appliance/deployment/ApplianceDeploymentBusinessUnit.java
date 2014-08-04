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
package cn.edu.buaa.act.service4all.core.samanager.appliance.deployment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
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
import cn.edu.buaa.act.service4all.core.samanager.appliance.ApplianceFactory;
import cn.edu.buaa.act.service4all.core.samanager.appliance.HostManager;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;
import cn.edu.buaa.act.service4all.core.samanager.beans.Host;
import cn.edu.buaa.act.service4all.core.samanager.exception.ApplianceException;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceDeploymentEvent;
import cn.edu.buaa.act.service4all.core.samanager.listener.ApplianceListener;

public class ApplianceDeploymentBusinessUnit extends SAManagerBusinessUnit {

	private final Log logger = LogFactory
			.getLog( ApplianceDeploymentBusinessUnit.class );

	public final static String IS_SUCCESS_DEPLOY = "isSuccessful";
	public final static String EXCEP_DESP = "description";
	public final static String DEPLOY_TYPE = "type";
	public final static String DEPLOY_NUM = "deployNum";
	public final static String NEW_DEPLOYEDS = "newDeployeds";
	public final static String SERVICE_ID = "serviceId";


	@Override
	public void dispatch( ExchangeContext context ) {
		ApplianceDeploymentEvent event = createApplianceDeploymentEvent( context );
		List<ApplianceListener> ls = this.getApplianceListeners();
		for (ApplianceListener l : ls) {
			try {

				l.deployAppliance( event );
			} catch (ApplianceException e) {
				logger.error( "Can't deploy the appliance: "
						+ event.getDeployType() );
				// send the exception message to the client
				context.storeData( IS_SUCCESS_DEPLOY, "false" );
				context.storeData( EXCEP_DESP, e.getMessage() );

				try {
					this.getReceiver().sendResponseMessage( context );
				} catch (MessageExchangeInvocationException e1) {
					e1.printStackTrace();
				}
			}
		}

		// send the deployment request to Host
		sendDeploymentRequest2Host( event, context );

		try {
			this.getReceiver().sendResponseMessage( context );
		} catch (MessageExchangeInvocationException e1) {
			e1.printStackTrace();
		}
	}


	private void sendDeploymentRequest2Host( ApplianceDeploymentEvent event,
			ExchangeContext context ) {

		String deployType = (String) context.getData( DEPLOY_TYPE );

		logger.info( "The deploy type: " + deployType );

		int deployNum = (Integer) context.getData( DEPLOY_NUM );

		int type;
		if (deployType.equals( "axis2" )) {
			type = 3;
		} else if (deployType.equals( "appserver" )) {
			type = 0;
		}  else if(deployType.equals( "bpmnengine" )){
			type = 6;
		}else {
			logger.warn( "The deploy type is not supported!" );
			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP, "The deploy type is not supported!" );

			return;
		}

		PriorityQueue<Appliance> appliances = parseResultEvent( event );

		if (appliances == null || appliances.size() <= 0) {
			// there is no available host to deploy appliance
			logger.warn( "There is null available Host to deploy appliance!" );
			context.storeData( IS_SUCCESS_DEPLOY, "false" );
			context.storeData( EXCEP_DESP,
					"There is null available Host to deploy appliance!" );
			return;
		}
		// sort the appliances by its child appliances
		// Appliance host = selectAppliance(appliances);
		String desp = "";

		int i = 0;
		List<Appliance> newDeployeds = new ArrayList<Appliance>();
		boolean isFinished = false;

		for (; !isFinished;) {

			// Appliance host = appliances.poll();
			// Appliance host = getMiniHost(appliances, "axis2");
			// Appliance host = getMiniHost(appliances, deployType);
			Appliance host = getMinHost( appliances ); // Emma

			String deployUrl = host.getDesp().getDeployEPR();
			logger.info( "@@@@@@@@Send the deploy url: " + deployUrl );

			String deployOp = host.getDesp().getDeployOperation();
			String hostId = host.getDesp().getId();
			logger.info( "Select an host for deployment : " + hostId );

			OMElement req = ApplianceDeploymentMsgUtil.deployMsgEncp( type );
			try {

				OMElement resp = send2Host( deployUrl, deployOp, req );

				logResponseFromHost( resp );

				if (isSuccessfulDeployment( resp )) {

					Appliance deployed = updateAppliances( resp, host,
							deployType );

					if (deployed != null) {

						newDeployeds.add( deployed );
						i++;
						// j = (++j) % appliances.size();

					}
					// appliances.add(host);
				} else {

					logger.warn( "The " + deployType
							+ "appliance deployment fails for the Host: "
							+ hostId );
					// context.storeData(IS_SUCCESS_DEPLOY, "false");
					// context.storeData(EXCEP_DESP,
					// "The " + deployType +
					// "appliance deployment fails for the Host: " +
					// applianceId);
					desp += "The " + deployType
							+ "appliance deployment fails for the Host: "
							+ hostId + "\n";
					// appliances.remove(j);

				}

			} catch (AxisFault e) {
				logger.error( e.getMessage(), e );
				// context.storeData(IS_SUCCESS_DEPLOY, "false");
				// context.storeData(EXCEP_DESP, e.getMessage());
				desp += "The " + deployType
						+ "appliance deployment fails for the Host: " + hostId
						+ " for the exception : " + e.getMessage() + "\n";
			}

			if (i == deployNum || appliances.size() <= 0) {
				logger.info( "Deployment " + i + " " + deployType
						+ "successfully int the Host" );
				isFinished = true;
			}
		}

		context.storeData( EXCEP_DESP, desp );
		context.storeData( NEW_DEPLOYEDS, newDeployeds );
	}


	// protected Appliance getMiniHost(PriorityQueue<Appliance> appliances,
	// String type){
	// logger.info("deployType:" + type);
	// Iterator<Appliance> it = appliances.iterator();
	// int mini = 100000;
	// Appliance a = null;
	// while(it.hasNext()){
	// Host h = (Host)it.next();
	//
	// if(h.getChildAppliances().get(type) == null){
	// logger.info(type + " is null");
	// return h;
	// }
	// if(h.getChildAppliances().get(type).size() < mini){
	// mini = h.getChildAppliances().get(type).size();
	// a = h;
	// }
	// }
	// return a;
	// }
	protected Appliance getMinHost( PriorityQueue<Appliance> appliances ) {
		Iterator<Appliance> it = appliances.iterator();
		int mini = 100000;
		Appliance a = null;
		while (it.hasNext()) {
			Host h = (Host) it.next();
			if (h.getChildAppliances().size() < mini) {
				mini = h.getChildAppliances().size();
				a = h;
			}
		}
		return a;
	}


	private void logResponseFromHost( OMElement resp ) {
		File tmp = new File( "applianceDeploymentResponse.xml" );

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


	protected Appliance updateAppliances( OMElement resp, Appliance host,
			String applianceType ) {
		logger.info( "Update the appliances information according to the deployment response" );
		Calendar now = Calendar.getInstance();
		String ip;
		String port;

		OMElement ipElement = AXIOMUtils.getChildElementByTagName( resp, "ip" );
		if (ipElement == null) {
			logger.warn( "The ip address is null from the host for appliance deployment" );
			return null;
		} else {
			ip = ipElement.getText();
		}

		OMElement portElement = AXIOMUtils.getChildElementByTagName( resp,
				"port" );
		if (portElement == null) {
			logger.warn( "The port is null from the host for appliance deployment" );
			return null;
		} else {
			port = portElement.getText();
		}

		logger.info( "Deploy a new appliance with the type: " + applianceType );
		Appliance deployedAppliance = ApplianceFactory
				.createAppliance( applianceType );
		deployedAppliance.getDesp().setId(
				applianceType + "_" + ip + ":" + port );
		deployedAppliance.getDesp().setIp( ip );
		deployedAppliance.getDesp().setPort( port );

		deployedAppliance.getStatus()
				.setCpuRate( host.getStatus().getCpuRate() );
		deployedAppliance.getStatus().setMemoryfloat(
				host.getStatus().getMemoryfloat() );
		deployedAppliance.getStatus().setPort( host.getStatus().getPort() );

		Host host1 = (Host) host;
		host1.addChildAppliance( applianceType, deployedAppliance );

		if (applianceManager instanceof HostManager) {

			HostManager hostManager = (HostManager) this.applianceManager;
			hostManager.addSubAppliance( applianceType, host,
					deployedAppliance, now );

		} else {

			logger.warn( "The target ApplianceManager is not Host Manager!" );
			return null;

		}

		return deployedAppliance;
	}


	protected PriorityQueue<Appliance> parseResultEvent(
			ApplianceDeploymentEvent event ) {

		return event.getTargetAppliances();
	}


	protected boolean isSuccessfulDeployment( OMElement resp ) {
		OMElement isElement = AXIOMUtils.getChildElementByTagName( resp,
				"isSuccessful" );
		return Boolean.valueOf( isElement.getText() );

	}


	protected Appliance selectAppliance( List<Appliance> aps ) {
		logger.info( "There are " + aps.size()
				+ " available Hosts for deployment" );
		return aps.get( 0 );
	}


	private OMElement send2Host( String deployUrl, String op, OMElement msg )
			throws AxisFault {

		logger.info( "Send the deployment requst to host: " + deployUrl );

		ServiceClient serviceClient = new ServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference( deployUrl );
		logger.info( "&&&&&" + targetEPR .getAddress());
		logger.info( "Send the deployment requst to host: " + deployUrl );

		options.setTo( targetEPR );
		options.setTimeOutInMilliSeconds( 10000000 );
		options.setAction( "urn:" + op );
		serviceClient.setOptions( options );

		OMElement response = serviceClient.sendReceive( msg );
		logger.info( response );

		logger.info( response.toString() );
		return response;

	}


	protected ApplianceDeploymentEvent createApplianceDeploymentEvent(
			ExchangeContext context ) {
		ApplianceDeploymentEvent event = new ApplianceDeploymentEvent();
		// event.setDeployType((String)context.getData("deploymentType"));
		event.setDeployType( (String) context
				.getData( ApplianceDeploymentBusinessUnit.DEPLOY_TYPE ) );
		return event;
	}


	@Override
	public void onReceiveResponse( ExchangeContext context ) {

	}

}
