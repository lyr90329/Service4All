package cn.edu.buaa.act.service4all.bpmnmonitor.endpoints;

import java.util.List;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmnmonitor.DataBaseUtils;
import cn.edu.buaa.act.service4all.bpmnmonitor.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnmonitor.Parameter;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XMLUtils;

public class BPMNMonitorInfoReceiver extends Receiver{

	private final static Log logger = LogFactory.getLog(BPMNMonitorInfoReceiver.class);
	
	public final static String JobId = "jobId";
	public final static String QueryResult = "queryResult";
	
	/**
	 * create the response message: 
	 * 
	 * <BPMNMonitorInfoResponse jobId="">
	 * 		<records num="">
	 * 			<record nodeId="" isError="" nodeDesp="" nodeStatus="" timestamp="">
	 * 				<parameters>
	 * 					<parameter name="" value="" type=""/>
	 * 				</parameters>
	 * 			</record>
	 * 		</records>
	 * </BPMNMonitorInfoResponse>
	 * 
	 */
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		Document response = XMLUtils.createNewDocument();
		Element root = response.createElement("BPMNMonitorInfoResponse");
		response.appendChild(root);
		
		// get the job id
		String jobId = (String)context.getData(JobId);
		root.setAttribute("jobId", jobId);
		
		List<MonitorRecord> records = (List<MonitorRecord>)context.getData(QueryResult);
		if(records != null){
			Element redsEle = response.createElement("records");
			root.appendChild(redsEle);
			
			for(MonitorRecord r : records){
				
				Element redEle = response.createElement("record");
				redEle.setAttribute("nodeId", r.getNodeId());
				redEle.setAttribute("isError", String.valueOf(r.isError()));
				redEle.setAttribute("nodeDesp", r.getNodeDesp());
				redEle.setAttribute("nodeStatus", String.valueOf(r.isNodeStatus()));
				redEle.setAttribute("timestamp", String.valueOf(r.getTimestamp().getTime()));
				redsEle.appendChild(redEle);
				
				// set the parameters for the MonitorRecord
				Element psEle = response.createElement("parameters");
				redEle.appendChild(psEle);
				
				List<Parameter> ps = r.getPs();
				for(Parameter p : ps){
					Element pEle = response.createElement("parameter");
					pEle.setAttribute("name", p.getParameterName());
					pEle.setAttribute("value", p.getParameterValue());
					pEle.setAttribute("type", p.getParameterType());
					
					psEle.appendChild(pEle);
				}
				
			}
		}
		
		
		return response;
	}

	/**
	 * the request's xml schema:
	 * 
	 * <BPMNMonitorInfoRequest jobId="the jobId"/>
	 * 
	 */
	@Override
	public void handlRequest(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		Element root = request.getDocumentElement();
		
		String jobId = root.getAttribute("jobId");
		context.storeData(JobId, jobId);
		
		// query the database for the result
		List<MonitorRecord> records = DataBaseUtils.getMonitorRecordByJobId(jobId);
		if(records != null || records.size() > 0){
			context.storeData(QueryResult, records);
		}
		
		// send the response message
		this.sendResponseMessage(context);
		
	}

}
