package cn.edu.buaa.act.service4all.webapp.scale;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;
import cn.edu.buaa.act.service4all.webapp.utility.XMLUtil;

public class WebAppManualScaleOutServerQueryInvoker extends Invoker {

	private static Log logger = LogFactory
			.getLog( WebAppManualScaleOutServerQueryInvoker.class );


	@Override
	public Document createRequestDocument( ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "send query message to SA." );
		String serviceName = (String) context.getData( Constants.SERVICE_NAME );
		int deployNum = Integer.parseInt( (String) context
				.getData( Constants.DEPLOY_NUM ) );
		String serviceId = (String) context.getData(Constants.SCALE_OUT_ID);
//		Document doc = DocBuilder.buildRequestDoc( serviceName, deployNum );
		Document doc = DocBuilder.buildRequestDoc( serviceName, deployNum, serviceId );
		return doc;
	}


	@Override
	public void handleResponse( Document doc, ExchangeContext context )
			throws MessageExchangeInvocationException {
		logger.info( "Receive available container response from Global SA.\n Response document is:\n"
					+ XMLUtil.docToString( doc ) );
		parseResponse( doc, context );
	}


	private void parseResponse( Document doc, ExchangeContext context ) {
		Element root = doc.getDocumentElement();
		//no need to get service id
//		String serviceID = ((Element) root.getElementsByTagName(
//				Constants.SERVICE_ID ).item( 0 )).getTextContent();
//		context.storeData( Constants.SERVICE_ID, serviceID );
		Element urls = ((Element) root.getElementsByTagName(
				Constants.CONTAINER_LIST ).item( 0 ));
		context.storeData( Constants.CONTAINER_LIST, urls );
		OMElement request = (OMElement) context
				.getData( Constants.DEPLOY_WAR_REQUEST );
		if (request == null) {
			logger.info( "*********The request for container is null!" );
		}
		unit.onReceiveResponse( context );
	}
}
