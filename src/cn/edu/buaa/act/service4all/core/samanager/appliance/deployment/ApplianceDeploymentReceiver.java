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

import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

/**
 * @author dell
 * 
 */
public class ApplianceDeploymentReceiver extends Receiver {

	private Log logger = LogFactory.getLog( ApplianceDeploymentReceiver.class );


	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement( "applianceDeploymentResponse" );

			String serviceId = (String) context
					.getData( ApplianceDeploymentBusinessUnit.SERVICE_ID );
			String is = (String) context
					.getData( ApplianceDeploymentBusinessUnit.IS_SUCCESS_DEPLOY );
			String type = (String) context
					.getData( ApplianceDeploymentBusinessUnit.DEPLOY_TYPE );
			String jobId = (String) context.getData( "jobId" );
			logger.info("jobid="+jobId);
			@SuppressWarnings("unchecked")
			List<Appliance> appliances = (List<Appliance>) context
					.getData( ApplianceDeploymentBusinessUnit.NEW_DEPLOYEDS );

			root.setAttribute( "type", type );
			root.setAttribute( "serviceId", serviceId );

			if (jobId != null) {
				logger.info( "Send appliance deployment response for the job: "
						+ jobId );
				root.setAttribute( "jobId", jobId );
			}

			if (is != null && is.equalsIgnoreCase( "false" )) {

				// fail
				Element isSucEle = doc.createElement( "isSuccessful" );
				isSucEle.setTextContent( "false" );
				root.appendChild( isSucEle );

				// root.setTextContent((String)context.getData(ApplianceDeploymentBusinessUnit.EXCEP_DESP));
				Element desp = doc.createElement( "desp" );
				desp.setTextContent( (String) context
						.getData( ApplianceDeploymentBusinessUnit.EXCEP_DESP ) );
				root.appendChild( desp );
			} else {

				// no available servers
				if (appliances == null || appliances.size() <= 0) {
					Element isSucEle = doc.createElement( "isSuccessful" );
					isSucEle.setTextContent( "false" );
					root.appendChild( isSucEle );

					// root.setTextContent((String)context.getData(ApplianceDeploymentBusinessUnit.EXCEP_DESP));
					Element desp = doc.createElement( "desp" );
					desp.setTextContent( "The target deployed appliance is null or its size is 0" );
					root.appendChild( desp );

				} else {

					// add new deployed appliances
					Element isSucEle = doc.createElement( "isSuccessful" );
					isSucEle.setTextContent( "true" );
					root.appendChild( isSucEle );

					Element appliancesEle = doc.createElement( "appliances" );
					appliancesEle.setAttribute( "num",
							String.valueOf( appliances.size() ) );

					for (Appliance a : appliances) {

						Element appEle = doc.createElement( "appliance" );
						appEle.setAttribute( "id", a.getDesp().getId() );
						appEle.setAttribute( "ip", a.getDesp().getIp() );
						appEle.setAttribute( "port", a.getDesp().getPort() );
						String deployEPR = a.getDesp().getIp() + ":"
								+ a.getDesp().getPort()
								+ "/axis2/services/localSAManagerService/";
						appEle.setAttribute( "deployUrl", deployEPR );
						appEle.setAttribute( "deployOperation", a.getDesp()
								.getDeployOperation() );
						appEle.setAttribute( "cpu",
								String.valueOf( a.getStatus().getCpuRate() ) );
						appEle.setAttribute( "memory",
								String.valueOf( a.getStatus().getMemoryfloat() ) );
						appEle.setAttribute( "throughput",
								String.valueOf( a.getStatus().getPort() ) );

						appliancesEle.appendChild( appEle );
					}

					root.appendChild( appliancesEle );
				}

				// root.setTextContent("Appliance is deployed successfully!");
			}

			doc.appendChild( root );
			return doc;
		} catch (ParserConfigurationException e) {
			logger.warn( e.getMessage() );
		}

		return null;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {

		Element root = request.getDocumentElement();

		String type = root.getAttribute( "type" );
		if (type == null) {
			this.sendResponseMessage(
					createExceptionDoc( "Can't judge the deployment type" ),
					context );
			return;
		} 
		else {
			String jobId = root.getAttribute( "jobId" );
			logger.info( "Start an appliance deployment for the job: " + jobId );
			context.storeData( "jobId", jobId );
		}

		context.storeData( ApplianceDeploymentBusinessUnit.DEPLOY_TYPE, type );

		String serviceId = root.getAttribute( "serviceId" );
		if (serviceId == null) {

			this.sendResponseMessage(
					createExceptionDoc( "Can't get the serviceId!" ), context );
			return;
		}
		context.storeData( ApplianceDeploymentBusinessUnit.SERVICE_ID,
				serviceId );

		logger.info( "Receive an appliance deployment request for the type: "
				+ type );
		// if(type.equalsIgnoreCase("axis2")){
		// OMElement deploymentReq =
		// ApplianceDeploymentMsgUtil.deployMsgEncp(Constants.Axis2Deploy);
		//
		// }
		int deployNum;
		if (request.getElementsByTagName( "deployNum" ) == null
				|| request.getElementsByTagName( "deployNum" ).getLength() <= 0) {
			logger.warn( "Miss the deployNum element so set the deployment number defaultly to be 1!" );
			deployNum = 1;
		} else {
			Element deployNumElement = (Element) request.getElementsByTagName(
					"deployNum" ).item( 0 );
			deployNum = Integer.valueOf( deployNumElement.getTextContent() );
		}

		context.storeData( ApplianceDeploymentBusinessUnit.DEPLOY_TYPE, type );
		context.storeData( ApplianceDeploymentBusinessUnit.DEPLOY_NUM,
				deployNum );

		this.unit.dispatch( context );

	}


	private Document createExceptionDoc( String msg ) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement( "applianceDeploymentResponse" );
			root.setAttribute( "isSuccessful", "false" );
			root.setTextContent( msg );

			doc.appendChild( root );
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

}
