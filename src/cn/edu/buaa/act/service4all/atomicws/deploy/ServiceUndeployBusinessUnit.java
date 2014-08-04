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
package cn.edu.buaa.act.service4all.atomicws.deploy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.deploy.threads.UndeployStore2DatabaseThread;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DeployConstants;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.UndeployReplica;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.UserControl;
import cn.edu.buaa.act.service4all.atomicws.deploy.utils.WSUndeployUtils;
import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class ServiceUndeployBusinessUnit extends BusinessUnit {
	private final Log logger = LogFactory.getLog(ServiceUndeployBusinessUnit.class);

	@Override
	public void dispatch(ExchangeContext context) {

		if (getInvokers().get("ReplicaQueryForUndeployInvoker") == null) {
			logger.error("ReplicaQueryForUndeployInvoker dose not exist");
			MessageExchangeInvocationException ep = new MessageExchangeInvocationException(
					"Missing the ReplicaQueryForUndeployInvoker!");
			ep.setSender(getReceiver().getEndpoint());
			this.handleInvocationException(ep);
		}
		Invoker invoker = getInvokers().get("ReplicaQueryForUndeployInvoker");

		try {
			invoker.sendRequestExchange(context);
		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		String serviceID = (String) context.getData(DeployConstants.SERVICEID);
		String serviceName = (String) context
				.getData(DeployConstants.SERVICENAME);
		String userName = (String) context.getData(DeployConstants.USERNAME);
		
		if (!(Boolean) context.getData(DeployConstants.HASAVILABLEREPLICAS)) {
			logger.info("There is no available replica");
			try {
				context.storeData(DeployConstants.RESPONSE,
						DocsBuilder.buildNoAvailableReplicaDoc(serviceID));
				this.getReceiver().sendResponseMessage(context);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
			return;
		}

		Element urls = ((Element) context.getData("urls"));
		int availableReplicaCount = Integer
				.valueOf(urls.getAttribute("length"));

		if (availableReplicaCount < 1) {
			logger.info("length<1. So there is no available replica");
			try {
				context.storeData(DeployConstants.RESPONSE,
						DocsBuilder.buildNoAvailableReplicaDoc(serviceID));
				this.getReceiver().sendResponseMessage(context);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
			return;
		}

		String type = (String) context.getData(DeployConstants.TYPE);
		String successInfo = null;
		String failInfo = null;
		List<UndeployReplica> failUndeployReplica = new ArrayList<UndeployReplica>();
		List<UndeployReplica> unDeployedReplica = new ArrayList<UndeployReplica>();

		if (DeployConstants.REPLICARELEASE.equals(type)) {

			successInfo = "The release is successful.";
			logger.info("Start releasing service...");

			Element urlElement = null;
			int successNum = 0;
			boolean isSuccessful4Container = false;

			for (int i = 0; i < ServiceUndeployReceiver.STEP; i++) {
				UndeployReplica replica = new UndeployReplica();
				urlElement = (Element) urls.getElementsByTagName("container")
						.item(i);
				replica.setContainerID(urlElement.getAttribute("id"));
				replica.setUndeployUrl(urlElement.getAttribute("undeployUrl"));

				isSuccessful4Container = WSUndeployUtils.undeploy(
						replica.getUndeployUrl(), serviceName, serviceID,replica.getContainerID(),userName);
				replica.setSuccessful(isSuccessful4Container);
				if (isSuccessful4Container) {
					successNum++;
					unDeployedReplica.add(replica);
				} else {
					failUndeployReplica.add(replica);
				}
			}

			failInfo = "There are "
					+ (ServiceUndeployReceiver.STEP - successNum)
					+ " replicas failed to release";
			if (successNum == ServiceUndeployReceiver.STEP) {
				context.storeData(DeployConstants.ISSUCCESSFUL, "true");
				context.storeData(DeployConstants.INFO, successInfo);

			} else {
				context.storeData(DeployConstants.ISSUCCESSFUL, "false");
				context.storeData(DeployConstants.INFO, failInfo);
			}

		} else {

			successInfo = "The undeploy is successful.";

			logger.info("Start undeploying...");

			Element urlElement = null;
			int successNum = 0;
			boolean isSuccessful4Container = false;

			for (int i = 0; i < availableReplicaCount; i++) {
				UndeployReplica replica = new UndeployReplica();
				urlElement = (Element) urls.getElementsByTagName("container")
						.item(i);
				replica.setContainerID(urlElement.getAttribute("id"));
				replica.setUndeployUrl(urlElement.getAttribute("undeployUrl"));

				isSuccessful4Container = WSUndeployUtils.undeploy(
						replica.getUndeployUrl(), serviceName, serviceID,replica.getContainerID(),userName);
				replica.setSuccessful(isSuccessful4Container);
				if (isSuccessful4Container) {
					successNum++;
					unDeployedReplica.add(replica);
				} else {
					failUndeployReplica.add(replica);
				}
			}

			failInfo = "There are " + (availableReplicaCount - successNum)
					+ " replicas failed to undeploy";
			if (successNum == availableReplicaCount) {
				context.storeData(DeployConstants.ISSUCCESSFUL, "true");
				context.storeData(DeployConstants.INFO, successInfo);

			} else {
				context.storeData(DeployConstants.ISSUCCESSFUL, "false");
				context.storeData(DeployConstants.INFO, failInfo);
			}

			if ("true".equals(DeployConstants.getInstance()
					.getCheckFb2UserAuthentication())) {
				UserControl.undeployNotification(
						(String) context.getData(DeployConstants.USERNAME),
						serviceID);
			}
		}

		context.storeData(DeployConstants.TOUNDEPLOYREPLICA,
				failUndeployReplica);
		context.storeData(DeployConstants.UNDEPLOYEDREPLICA, unDeployedReplica);

		Thread store2DB = new Thread(new UndeployStore2DatabaseThread(context));
		store2DB.start();

		Invoker invoker = getInvokers().get("UndeployFeedbackInvoker");
		try {

			invoker.sendRequestExchange(context);

		} catch (MessageExchangeInvocationException e) {
			this.handleInvocationException(e);
		}

	}

}
