package cn.edu.buaa.act.service4all.webapp.scale;

import java.io.File;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;


public class WebAppManualScaleOutReceiver extends Receiver {
	
	private Log logger = LogFactory.getLog(WebAppManualScaleOutReceiver.class);

	@Override
	public Document createResponseDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		Document doc = (Document) context
				.getData( Constants.MANUAL_SCALEOUT_RESPONSE );
		return doc;
	}


	@Override
	public void handlRequest( Document doc, ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Received manual scale out request." );
		if(doc == null){
			logger.info("the request doc is null! please check it.");
		}
		parseRequest(doc, context);
		this.unit.dispatch(context);
	}
	
	/*
	 */
	private void parseRequest(Document doc, ExchangeContext context) {
		Element root = doc.getDocumentElement();
		String userName = root.getElementsByTagName(Constants.USER_NAME)
				.item(0).getTextContent();
		context.storeData(Constants.USER_NAME, userName);
		logger.info("user name:" + userName);
		String deployNum="1";
		try {
			deployNum = root.getElementsByTagName(Constants.DEPLOY_NUM)
				.item(0).getTextContent();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			logger.warn("Deploy number set to 1");
		}
		context.storeData(Constants.DEPLOY_NUM, deployNum); // the num of deployment	
		logger.info("deploy number:" + deployNum);
		String fileName = root.getElementsByTagName(Constants.FILE_NAME)
				.item(0).getTextContent();
		context.storeData(Constants.SERVICE_NAME, fileName);
		logger.info("webapp name:" + fileName);
		String webappId = root.getElementsByTagName(Constants.SCALE_OUT_ID)
				.item(0).getTextContent();
		context.storeData(Constants.SCALE_OUT_ID, webappId);
		context.storeData(Constants.SERVICE_ID, webappId);
		logger.info("webapp id:" + webappId);
		buildScaleOutRequest( userName, deployNum, fileName, webappId, context );
	}
	

	/**
	 * 构建向localSA发部署消息的请求
	 * @param userName
	 * @param deployNum
	 * @param fileName
	 * @param webappId
	 * @param context
	 */
	private void buildScaleOutRequest( String userName, String deployNum,
			String fileName, String webappId, ExchangeContext context ) {
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
			
			if(!fileName.endsWith( ".war" )){
				fileName += ".war";
			}
			logger.info("--------------------"+fileName);
			//不包含特定文件，才向localSA传递文件内容
			if (!fileName.equals("live.war") && !fileName.equals("ybsapp.war")) {
				OMElement fileDataOM = fac.createOMElement(Constants.FILE_DATA,
						omNamespace);
				DataHandler dh = new DataHandler(
						new FileDataSource(Constants.MIRROR_REPOSITORY
								+ File.separator + fileName));
				OMText textData = fac.createOMText(dh, true);

				fileDataOM.addChild(textData);
				deployRequest.addChild(fileDataOM);
			}
			logger.info("building end.");
			context.storeData(Constants.DEPLOY_WAR_REQUEST, deployRequest);
		} catch (Exception e) {
			context.storeData(Constants.EXCEPTION,
					"There is something wrong with the request document.");
		}
	}

}
