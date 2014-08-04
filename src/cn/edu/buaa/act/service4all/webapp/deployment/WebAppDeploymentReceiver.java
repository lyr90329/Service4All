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
package cn.edu.buaa.act.service4all.webapp.deployment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;

public class WebAppDeploymentReceiver extends Receiver {
	
	private static final double pFailure = 0.2;

	private Log logger = LogFactory.getLog(WebAppDeploymentReceiver.class);

	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		Document doc = (Document) context
				.getData(Constants.WAR_DEPLOY_RESPONSE);
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("receive war deployment request!");

		if (!validateRequest(doc)) {
			logger.info("the request doc is null!please check it.");
		}
		parseRequest(doc, context);
		this.unit.dispatch(context);
	}

	private boolean validateRequest(Document doc) {
		if (null == doc) {
			return false;
		}
		return true;
	}

	private void parseRequest(Document doc, ExchangeContext context) {
		logger.info("parse web app deploy request!");
		Element root = doc.getDocumentElement();
		String userName = root.getElementsByTagName(Constants.USER_NAME)
				.item(0).getTextContent();
		context.storeData(Constants.USER_NAME, userName);
		logger.info("user name:" + userName);
		String deployNum="1";
		try {
			String avaiLevel = root.getElementsByTagName("availability")
					.item(0).getTextContent();
			deployNum = String.valueOf(calDeployCount(Double.parseDouble( avaiLevel )));
		} catch (NullPointerException e1) {
			e1.printStackTrace();
			logger.info("no availability info");
		}
		context.storeData(Constants.DEPLOY_NUM, deployNum); // the num of deployment	
		logger.info("deploy number:" + deployNum);
		String fileName = root.getElementsByTagName(Constants.FILE_NAME)
				.item(0).getTextContent();
		context.storeData(Constants.SERVICE_NAME, fileName);
		logger.info("service name:" + fileName);
		@SuppressWarnings("unchecked")
		List<Object> attachments = (ArrayList<Object>) context
				.getData(ATTACHMENT);
		DataHandler dh = (DataHandler) attachments.get(0);
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
//		OMText textData = omFactory.createOMText(dh, true);
		doMirror(dh, fileName);
		logger.info("build soap enveloap for web app deploying.");
		// create the OMElement for deployment
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNamespace = fac.createOMNamespace(
				"http://manageServices.serviceCloud.sdp.act.org.cn", "");
		try {
			OMElement deployRequest = fac.createOMElement(
					Constants.DEPLOY_WEBAPP, omNamespace);
			OMElement userNameOM = fac.createOMElement(Constants.USER_NAME,
					omNamespace);
			userNameOM.setText(userName);
			deployRequest.addChild(userNameOM);
			OMElement fileNameOM = fac.createOMElement(Constants.FILE_NAME,
					omNamespace);
			fileNameOM.setText(fileName);
			deployRequest.addChild(fileNameOM);
			if (!"live.war".contains(fileName)
					&& !"ybsapp.war".contains(fileName)) {
				OMText textData = omFactory.createOMText(dh, true);
				OMElement fileDataOM = fac.createOMElement(Constants.FILE_DATA,
						omNamespace);
				fileDataOM.addChild(textData);
				deployRequest.addChild(fileDataOM);
			}
			else{
				logger.info("No need to send the file content of " + fileName + ".war");
			}
			logger.info("building end.");
			context.storeData(Constants.DEPLOY_WAR_REQUEST, deployRequest);
		} catch (Exception e) {
			context.storeData(Constants.EXCEPTION,
					"There is something wrong with the request document.");
		}
	}
	
	private int calDeployCount(double availability) {
		return (int) logarithm(1 - availability, pFailure) + 1;
	}

	private double logarithm(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
	
	private void doMirror(DataHandler handler, String fileName){
		String folder = Constants.MIRROR_REPOSITORY;
		FileOutputStream os = null;
		InputStream is = null;
		if(!fileName.endsWith( ".war" )){
			fileName += ".war";
		}
		File MirrorRepo = new File(folder);
		if(!MirrorRepo.exists() || !MirrorRepo.isDirectory()){
			logger.info(MirrorRepo.exists()+" "+MirrorRepo.isDirectory());
			MirrorRepo.mkdir();
		}
		try {
			File mirror = new File(folder+File.separator+fileName);
			logger.info( "--------Mirror path---------" );
			logger.info( mirror.getAbsolutePath() );
			os = new FileOutputStream(mirror);
			is = handler.getInputStream();
			os.write(IOUtils.getStreamAsByteArray(is));

		} catch (Exception e) {
			logger.error("Failed when upload the service file",
					new AxisFault("Failed when upload the service file"));
			e.printStackTrace();
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	
	public static void main( String[] args ) {
		WebAppDeploymentReceiver r = new WebAppDeploymentReceiver();
		System.out.println(r.calDeployCount( 0.2 ));
		System.out.println(r.calDeployCount( 0.9 ));
		System.out.println(r.calDeployCount( 0.99 ));
	}
}
