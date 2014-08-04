package cn.edu.buaa.act.service4all.qualifycontrol.serviceControlComponent;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.messaging.util.XMLUtils;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.buaa.act.service4all.qualifycontrol.Manager.serviceManager;
import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.common.xmlTool;



public class serviceControlUnit extends BusinessUnit
{
	private final Log logger = LogFactory.getLog(serviceControlUnit.class);
	private serviceManager manager=null;
	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);
		manager=new serviceManager();
	}

	@Override
	public void dispatch(ExchangeContext context) 
	{
		org.w3c.dom.Document doc,response;
		
		doc=(org.w3c.dom.Document) context.getData(Constants.request);
		logger.info(XMLUtils.retrieveDocumentAsString(doc));
		
		response=xmlTool.convertJdomToDom(manager.parse(xmlTool.convertDomToJdom(doc)));
		logger.info(XMLUtils.retrieveDocumentAsString(response));
		context.storeData(Constants.response, response);
		
		try 
		{
			this.getReceiver().sendResponseMessage(context);
		}
		catch (MessageExchangeInvocationException e) 
		{
			e.printStackTrace();
		}			
	}

	@Override
	public void onReceiveResponse(ExchangeContext arg0) {
		// TODO Auto-generated method stub
		
	}

}
