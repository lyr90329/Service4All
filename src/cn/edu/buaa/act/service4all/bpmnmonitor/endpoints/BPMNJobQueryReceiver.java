package cn.edu.buaa.act.service4all.bpmnmonitor.endpoints;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmnmonitor.DataBaseUtils;
import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XMLUtils;

public class BPMNJobQueryReceiver extends Receiver {
	
	private final static String JobId = "jobId";
	private final static String QueryResult = "queryResult";
	/**
	 * the response message: 
	 * 
	 * <BPMNJobQueryResponse jobId="" serviceId="" serviceName="" timestamp="">
	 * 
	 * </BPMNJobQueryResponse>
	 */
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		Document resp = XMLUtils.createNewDocument();
		
		Job job = (Job)context.getData(JobId);
		Element root = resp.createElement("BPMNJobQueryResponse");
		if(job != null){
			
			root.setAttribute("jobId", job.getJobId());
			root.setAttribute("serviceId", job.getServiceId());
			root.setAttribute("serviceName", job.getServiceName());
			root.setAttribute("timestamp", String.valueOf(job.getTimestamp().getTime()));
			
		}else{
			
			// get the relationships
			String jobId = (String)context.getData("jobId");
			root.setAttribute("jobId", jobId);
			resp.setTextContent("There is no available job instance");
		}
		
		resp.appendChild(root);
		
		
		return resp;
	}

	/**
	 * the request message:
	 * <BPMNJobQueryRequest jobId=""/>
	 */
	@Override
	public void handlRequest(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		//parse the job query request
		Element root = request.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		
		if(jobId != null){
			context.storeData(JobId, jobId);
		}
		
		Job job = DataBaseUtils.getJobById(jobId);
		context.storeData(QueryResult, job);
		
		// send the response 
		this.sendResponseMessage(context);
		
	}

}
