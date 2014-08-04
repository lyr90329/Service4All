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
package cn.edu.buaa.act.service4all.core.samanager.app.deployment;

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
import cn.edu.buaa.act.service4all.core.component.messaging.util.XMLUtils;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.beans.Appliance;

public class ServerQueryForDeploymentReceiver extends Receiver {

	protected Log logger = LogFactory
			.getLog( ServerQueryForDeploymentReceiver.class );


	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {

		@SuppressWarnings("unchecked")
		List<Appliance> appliances = (List<Appliance>) context
				.getData( ServerQueryForDeploymentBusinessUnit.QUERY_RESULT );
		String type = (String) context
				.getData( ServerQueryForDeploymentBusinessUnit.QUERY_TYPE );
		// String serviceName =
		// (String)context.getData(ServerQueryForDeploymentBusinessUnit.SERVICENAME);
		String serviceId = (String) context
				.getData( ServerQueryForDeploymentBusinessUnit.SERVICE_ID );

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.warn( "Can't create the document" );
			throw new MessageExchangeInvocationException(
					"Can't create the document for query available servers!" );
		}

		Document document = builder.newDocument();
		Element root = document.createElement( "availableContainerResponse" );
		root.setAttribute( "type", type );
		document.appendChild( root );

		Element idElement = document.createElement( "serviceID" );
		idElement.setTextContent( serviceId );
		root.appendChild( idElement );

		// Element serviceNameElement = document.createElement("serviceName");
		// serviceNameElement.setTextContent(serviceName);
		// root.appendChild(serviceNameElement);

		Element containerListElement = document.createElement( "containerList" );
		if (appliances == null) {
			containerListElement.setAttribute( "length", "0" );
		} else {
			containerListElement.setAttribute( "length",
					String.valueOf( appliances.size() ) );
			logger.info( "Select appliance to deploy the app : " + serviceId );
			for (Appliance a : appliances) {
				Element c = document.createElement( "container" );

				c.setAttribute( "id", a.getDesp().getId() );
				//为了解决重复部署时出现端口为8080的部署请求，临时解决方案
				String deployUrl = a.getDesp().getDeployEPR();
				if(deployUrl.contains( ":8080" )){
					logger.warn( "Error deploy port 8080, replaced to " + a.getDesp().getPort() );
					deployUrl = deployUrl.replaceFirst( "8080", a.getDesp().getPort() );
				}
				c.setAttribute( "deployUrl", deployUrl );
				c.setAttribute( "deployOperation", a.getDesp()
						.getDeployOperation() );

				c.setAttribute( "cpu",
						String.valueOf( a.getStatus().getCpuRate() ) );
				c.setAttribute( "memory",
						String.valueOf( a.getStatus().getMemoryfloat() ) );
				c.setAttribute( "throughput",
						String.valueOf( a.getStatus().getPort() ) );

				containerListElement.appendChild( c );
			}
		}

		root.appendChild( containerListElement );
		logger.info( "The response for app deployment query: "
				+ XMLUtils.retrieveDocumentAsString( document ) );
		return document;
	}


	@Override
	public void handlRequest( Document request, ExchangeContext context )
			throws MessageExchangeInvocationException {

		logger.info( "Receive the available server query request!" );
		if (!validateRequest( request )) {
			logger.warn( "The request for querying appliances is invalidate!" );
			throw new MessageExchangeInvocationException(
					"The request for querying appliances is invalidate!" );
		}

		parseRequest( request, context );

		this.unit.dispatch( context );
	}


	/**
	 * check the request message
	 * 
	 * @param doc
	 * @return
	 */
	protected boolean validateRequest( Document doc ) {
		return true;
	}


	protected void parseRequest( Document req, ExchangeContext context )
			throws MessageExchangeInvocationException {
		Element root = req.getDocumentElement();

		String queryType = root.getAttribute( "type" );
		context.storeData( ServerQueryForDeploymentBusinessUnit.QUERY_TYPE,
				queryType );

		// ignore the serviceName properties
		if (queryType.equals( "WSReplicaAcquisition" )) {
			String serviceId;
			serviceId = root.getElementsByTagName( "serviceID" ).item( 0 )
					.getTextContent();
			context.storeData( ServerQueryForDeploymentBusinessUnit.SERVICE_ID,
					serviceId );
		}
		// for scale out, by tangyu
		if (root.getElementsByTagName( "scale_out_id" ) != null
				&& root.getElementsByTagName( "scale_out_id" ).getLength() > 0) {
			String scaleOutServiceId = root.getElementsByTagName(
					"scale_out_id" ).item( 0 ).getTextContent();
			context.storeData( ServerQueryForDeploymentBusinessUnit.SCALE_OUT_ID, scaleOutServiceId );
			context.storeData( ServerQueryForDeploymentBusinessUnit.IS_SCALE_OUT, "true" );
		}
		else{
			context.storeData( ServerQueryForDeploymentBusinessUnit.IS_SCALE_OUT, "false" );
		}

		if (root.getElementsByTagName( "deployNum" ) == null
				|| root.getElementsByTagName( "deployNum" ).getLength() <= 0) {
			logger.warn( "Just query the availalbe service but there is no deployNum element!" );
			// do nothing
			throw new MessageExchangeInvocationException(
					"Just query the availalbe service but there is no deployNum element!" );

		} else {
			logger.info( "Query the available severs for deployment: "
					+ queryType );
			Element sn = (Element) root.getElementsByTagName( "deployNum" )
					.item( 0 );
			String deployNum = sn.getTextContent();
			context.storeData( ServerQueryForDeploymentBusinessUnit.DEPLOY_NUM,
					deployNum );
		}
		if (root.getElementsByTagName( "deployWSName" ) != null
				&& root.getElementsByTagName( "deployWSName" ).getLength() > 0) {
			Element deployName = (Element) root.getElementsByTagName(
					"deployWSName" ).item( 0 );
			String deployWSName = deployName.getTextContent();
			context.storeData(
					ServerQueryForDeploymentBusinessUnit.SERVICENAME,
					deployWSName );
		}

	}

}
