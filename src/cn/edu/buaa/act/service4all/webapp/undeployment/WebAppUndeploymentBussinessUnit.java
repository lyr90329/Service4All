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
package cn.edu.buaa.act.service4all.webapp.undeployment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cn.edu.buaa.act.service4all.core.component.bri.BusinessUnit;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageContextException;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.WebApplication;
import cn.edu.buaa.act.service4all.webapp.appliance.NginxServer;
import cn.edu.buaa.act.service4all.webapp.database.DBHandler;
import cn.edu.buaa.act.service4all.webapp.qualification.UserQualification;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;
import cn.edu.buaa.act.service4all.webapp.utility.Config;
import cn.edu.buaa.act.service4all.webapp.utility.WebAppInfoSegmentSender;

public class WebAppUndeploymentBussinessUnit extends BusinessUnit {

	private Log logger = LogFactory
			.getLog(WebAppUndeploymentBussinessUnit.class);
	
	//this is a new code for nginx
	//----------------------------DRPS 201308----------------------------------//
	private static Config Config =  new Config();
	NginxServer nginx = new NginxServer(Config.getNginxIp(), Config.getNginxPort(), 
			Config.getNginxListeningPort());
	//------------------------------------------------------------------------//

	@Override
	public void dispatch(ExchangeContext context) {
		queryAppInfo(context);
	}

	@Override
	public void onReceiveResponse(ExchangeContext context) {
		Document doc = (Document) context
				.getData(Constants.UNDEPLOY_QUERY_RESPONSE);
		Element root = doc.getDocumentElement();
		Element serviceIdEl = (Element) root.getElementsByTagName(
				Constants.SERVICE_ID).item(0);
		String oldserviceID = (String) context.getData(Constants.SERVICE_ID);
		if (serviceIdEl == null) {
			context.storeData(Constants.UNDEPLOY_RESPONSE,
					DocBuilder.buildUndeployAppNotExistedDoc(oldserviceID));
			try {
				this.receiver.sendResponseMessage(context);
			} catch (MessageExchangeInvocationException e) {
				e.printStackTrace();
			}
			return;
		}
		String serviceID = serviceIdEl.getTextContent();
		Element serviceNameEl = (Element) root.getElementsByTagName(
				Constants.SERVICE_NAME).item(0);
		String serviceName = serviceNameEl.getTextContent();
		context.storeData(Constants.SERVICE_ID, serviceID);
		context.storeData(Constants.SERVICE_NAME, serviceName);
		Element containerListEl = (Element) root.getElementsByTagName(
				Constants.CONTAINER_LIST).item(0);
		int length = Integer.parseInt(containerListEl
				.getAttribute(Constants.LENGTH));
		int undeployed = 0;
		if (length > 0) {
			List<WebApplication> apps = new ArrayList<WebApplication>();
			WebApplication webApp = new WebApplication(serviceID);
			webApp.setServiceName(serviceName);
			for (int i = 0; i < length; i++) {
				Element containerEl = (Element) containerListEl
						.getElementsByTagName(Constants.CONTAINER).item(i);
				String applianceID = containerEl.getAttribute(Constants.ID);
				String unDeployUrl = containerEl
						.getAttribute(Constants.UNDEPLOY_URL);
				OMElement response = UndeployUtil.undeploy(unDeployUrl,
						serviceName, serviceID, applianceID.split(":")[2]);
				if (isUndeploySucc(response)) {
					webApp.setApplianceID(applianceID);
					webApp.setUndeployed(true);
					undeployed++;
					apps.add(webApp);
				} else {
					logger.info("fail to undeploy " + i + " webApp replica.");
				}
			}
			context.storeData(Constants.UNDEPLOY_APP_LIST, apps);
			if (undeployed == length) {// undeploy succeed
				context.storeData(Constants.UNDEPLOY_RESULT, "true");
				// notify the qualification component
				logger.info("notify the qualification component.");
				UserQualification.undeployNotification(
						(String) context.getData(Constants.USER_NAME),
						serviceID);
				// delete the info from the database
				DBHandler handler = new DBHandler();
				handler.deleteAppByID(serviceID);
				
				//----------------------------DRPS 201308----------------------------------//
				handler.deleteAppRPByID(serviceID);
				//------------------------------------------------------------------------//
			}
		} else {
			context.storeData(
					Constants.UNDEPLOY_EXCEPTION,
					"The web app you asked to undeploy does not exist or there's something wrong with"
							+ " the system.");
		}
		feedback(context);
	}

	private void queryAppInfo(ExchangeContext context) {
		Invoker invoker = this.getInvokers().get("WebAppQueryInvoker");
		if (null == invoker) {
			logger.info("can not get invoker WebAppQueryInvoker");
			try {
				throw new MessageContextException(
						"can not get invoker WebAppQueryInvoker.");
			} catch (MessageContextException e) {
				e.printStackTrace();
			}
		}
		try {
			invoker.sendRequestExchange(context);
			
			//----------------------------DRPS 201308----------------------------------//
			String serviceID = (String) context.getData(Constants.SERVICE_ID);
			this.sendInfoSegment2Ngnix(createUndeployedConfInfo(serviceID), nginx);
			//------------------------------------------------------------------------//
			
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}
	
	//----------------------------DRPS 201308----------------------------------//
	/**
	 * create undeployment information segment
	 * @param ServiceID
	 * @return
	 */
	private Document createUndeployedConfInfo(String ServiceID) {
		Document confInfo = null;
		try {
			confInfo = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			confInfo.setXmlVersion("1.0");
			Element root = confInfo.createElement("confInfoSegment");
			Element remove = confInfo.createElement("remove");
			root.setAttribute("operation", "un");
			remove.setAttribute("id", ServiceID);
			root.appendChild(remove);
			confInfo.appendChild(root);
			confInfo.setXmlStandalone(true);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return confInfo;
	}
	
	
	/**
	 * send the Information Segment to Nginx
	 * @param doc
	 * @param nginx
	 */
	public void sendInfoSegment2Ngnix(Document doc, NginxServer nginx){
		try {
			System.out.println(nginx.getHost());
			System.out.println(nginx.getListenPort());
			Socket client = new Socket(nginx.getHost(), nginx.getListenPort());
			PrintStream outBuf = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			try {
				TransformerFactory transFactory = TransformerFactory
						.newInstance();
				Transformer transFormer = transFactory.newTransformer();
				transFormer.setOutputProperty(OutputKeys.INDENT, "yes");
				transFormer.setOutputProperty("encoding", "UTF-8");
				DOMSource domSource = new DOMSource(doc);
				StreamResult xmlResult = new StreamResult(bos);
				transFormer.transform(domSource, xmlResult);
			} catch (TransformerException e) {
				e.printStackTrace();
			}

			BufferedReader inBuf = new BufferedReader(new InputStreamReader(client.getInputStream()));
			outBuf = new PrintStream(client.getOutputStream());
			long segSize = bos.toString().length();
			outBuf.print(segSize);
			System.out.println(segSize);

			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			String line = null;
			while ((line = br.readLine()) != null) {
				outBuf.println(line);
				System.out.println(line);
			}
			
			line = inBuf.readLine();
			if( line.equals("@@@@")){
				br.close();
				client.shutdownOutput();
				inBuf.close();
				outBuf.close();
				bos.close();
				bis.close();
				client.close();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//------------------------------------------------------------------------//
	

	private boolean isUndeploySucc(OMElement response) {
		OMElement element;
		boolean flag = false;
		for (@SuppressWarnings("unchecked")
		Iterator<OMElement> iter = response.getChildElements(); iter.hasNext();) {
			element = iter.next();
			if (element.getLocalName().equals(Constants.IS_SUCC)) {
				if ("true".equalsIgnoreCase(element.getText())) {
					flag = true;
				}
			}

		}
		return flag;
	}

	private void feedback(ExchangeContext context) {
		Invoker invoker = this.getInvokers().get(
				"WebAppUndeployFeedbackInvoker");
		if (null == invoker) {
			logger.info("can not get invoker WebAppUndeployFeedbackInvoker");
			try {
				throw new MessageContextException(
						"can not get invoker WebAppUndeployFeedbackInvoker.");
			} catch (MessageContextException e) {
				e.printStackTrace();
			}
		}
		try {
			invoker.sendRequestExchange(context);
		} catch (MessageExchangeInvocationException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		NginxServer nginx = new NginxServer("121.199.25.81", 5200, 4353);
		WebAppUndeploymentBussinessUnit bu = new WebAppUndeploymentBussinessUnit();
		bu.sendInfoSegment2Ngnix(bu.createUndeployedConfInfo("APP_1037"), nginx);
	}
}
