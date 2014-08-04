package cn.edu.buaa.act.service4all.webapp.scale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.webapp.deployment.WebAppDeployFeedbackInvoker;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.DocBuilder;


public class WebAppManualScaleOutFeedbackInvoker extends Invoker {
	private Log logger = LogFactory.getLog(WebAppManualScaleOutFeedbackInvoker.class);

	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("send scale out feedback to global sa.");
		return DocBuilder.buildDeploymentFeedbackDoc(context);
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		logger.info("receive scale out feedback response from sa.");
		Document response = DocBuilder.buildDeployResponse(context);
		context.storeData(Constants.MANUAL_SCALEOUT_RESPONSE, response);
		this.unit.getReceiver().sendResponseMessage(context);
	}
	
}