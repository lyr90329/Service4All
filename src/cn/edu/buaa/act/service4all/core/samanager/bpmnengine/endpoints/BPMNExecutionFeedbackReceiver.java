package cn.edu.buaa.act.service4all.core.samanager.bpmnengine.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppExecutionFeedbackReceiver;

/**
 * receive the feedback for bpmn execution
 * 
 * @author dell
 *
 */
public class BPMNExecutionFeedbackReceiver extends AppExecutionFeedbackReceiver {
	
	private static final Log logger = LogFactory.getLog(BPMNExecutionFeedbackReceiver.class);
	
	public static final String JOB_ID = "jobId";
	public static final String SERVICE_ID = "serviceId";
	public static final String STATUS = "status";
	public static final String ENGINES = "engines";
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		String jobId = (String)context.getData(JOB_ID);
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement("BPMNExecutionFeedbackResponse");
			document.appendChild(root);
			root.setAttribute("jobId", jobId);
			
			return document;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public void handlRequest(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		logger.info("Receive a request for bpmn service execution feedback!");
		
		Element root = request.getDocumentElement();
		String serviceId = root.getAttribute("serviceId");
		String jobId = root.getAttribute("jobId");
		String status = root.getAttribute("status");
		
		NodeList engEles = root.getElementsByTagName("engine");
		List<String> engineIds = new ArrayList<String>();
		if(engEles != null && engEles.getLength() > 0){
			// get the engine element
			for (int i = 0; i < engEles.getLength(); i++) {
				Element engEle = (Element)engEles.item(0);
				String id = engEle.getAttribute("id");
				String engStatus = engEle.getAttribute("status");
				if(engStatus != null && engStatus.equalsIgnoreCase("true")){
					engineIds.add(id);
				}
			}
		}
		
		// store some necessary data into context
		context.storeData(SERVICE_ID, serviceId);
		context.storeData(JOB_ID, jobId);
		context.storeData(STATUS, status);
		context.storeData(ENGINES, engineIds);
		
		
		this.unit.dispatch(context);
	}

	
}
