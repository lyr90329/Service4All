package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Invoker;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmnexecution.BPMNEngineInfo;
import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;

/**
 * Feedback the end of an BPMN execution
 * 
 * @author dell
 *
 */
public class EndBPMNFeedbackInvoker extends Invoker {
	
	/**
	 * the feedback message:
	 * <BPMNExecutionFeedback serviceId="" jobId="" status="end">
	 * 	<engineList>
	 * 		<engine id="" status="true | false"/>
	 * 		<engine id="" status="true | false"/>
	 * 	</engineList>
	 * </BPMNExecutionFeedback>
	 * 
	 * the context will have a job instance and a selected BPMNEngineInfo instance
	 * 
	 */
	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		// get the serviceId and bpmnengineid
		Job job = (Job)context.getData(Constants.job);
		BPMNEngineInfo engine = (BPMNEngineInfo)context.getData(Constants.selected_engine);
		
		try 
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
		
			Element root=doc.createElement("BPMNExecuteFeedback");
			root.setAttribute("serviceId", job.getServiceID());
			root.setAttribute("jobId", job.getJobID());
			root.setAttribute("status", "end");// indicate the end of bpmn execution
			doc.appendChild(root);
			
			Element engList = doc.createElement("engineList");
			Element engEle = doc.createElement("engine");
			engEle.setAttribute("id", engine.getEngineID());
			engEle.setAttribute("status", "true");
			engList.appendChild(engEle);
			root.appendChild(engList);
			
			return doc;
		}
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			logger.warn("Can't create the request document: " + e);
		}	
		return null;
	}

	/**
	 * the response have to include the jobId
	 * <BPMNExecutionFeedbackResponse jobId="">
	 * 	
	 * </BPMNExecutionFeedbackResponse>
	 * 
	 */
	@Override
	public void handleResponse(Document resp, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		Element root = resp.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		
		BPMNExecuteReceiveBussinessUnit parentUnit = (BPMNExecuteReceiveBussinessUnit)this.unit;
		Job job = parentUnit.getTaskManager().getJobByID(jobId);
		context.storeData(Constants.job, job);
		
		// just send out response message
		this.unit.getReceiver().sendResponseMessage(context);
	}

}
