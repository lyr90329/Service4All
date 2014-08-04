package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.messaging.util.XMLUtils;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnexecution.Parameter;


public class BPMNMonitorInfoFeedbackReceiver extends Receiver
{
	private final Log logger = LogFactory.getLog(BPMNMonitorInfoFeedbackReceiver.class);
	@Override
	public Document createResponseDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
		Document doc;
		
		doc=(Document) context.getData(Constants.MonitorInfoFeedbackResponse);
		logger.info(doc);
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context) throws MessageExchangeInvocationException 
	{	
		logger.info("The monitor info : " + XMLUtils.retrieveDocumentAsString(doc));
		
		// TODO Auto-generated method stub
		Element root,element,parameters,parameter;
		MonitorRecord record=null;
		NodeList list=null;
		Parameter para=null;
		String jobid,nodeid,nodestatus,nodedesp,name,value,type;
		int i;
	
		record=new MonitorRecord();
		root=doc.getDocumentElement();
		
		jobid=root.getElementsByTagName(Constants.jobId).item(0).getTextContent();
		record.setJobId(jobid);
		
		String serviceName = root.getElementsByTagName("serviceName").item(0).getTextContent();
		record.setServiceName(serviceName);
		
		if(root.getLocalName().equalsIgnoreCase("ExecuteResult")){
			//执行结果信息
			record.setResult(true);
			
			
			String is = root.getElementsByTagName("isSuccessful").item(0).getTextContent();
			record.setSuccessful(Boolean.getBoolean(is));
			
		}else if(root.getLocalName().equalsIgnoreCase("ExecuteErrorResult")){
			//中间执行错误结果信息
			record.setResult(false);
			nodeid=root.getElementsByTagName(Constants.nodeId).item(0).getTextContent();
			record.setNodeId(nodeid);
			
			String is = root.getElementsByTagName("isSuccessful").item(0).getTextContent();
			record.setSuccessful(Boolean.getBoolean(is));
			record.setNodeStatus(MonitorRecord.Exception);
			record.setStatusDesp("There are some error while executing node");
			
		}else{
			//执行监控信息
			record.setResult(false);
			nodeid=root.getElementsByTagName(Constants.nodeId).item(0).getTextContent();
			record.setNodeId(nodeid);
			
			nodestatus=root.getElementsByTagName(Constants.nodeStatus).item(0).getTextContent();
			if(nodestatus.equals(Constants.booltrue)){
				record.setNodeStatus(MonitorRecord.Successful);
			}else{
				record.setNodeStatus(MonitorRecord.Exception);
			}
			
			nodedesp=root.getElementsByTagName(Constants.nodeDesp).item(0).getTextContent();
			record.setStatusDesp(nodedesp);
			
		}
		
		
		parameters = (Element) root.getElementsByTagName(Constants.parameters).item(0);
		if(parameters != null){
			
			list = parameters.getElementsByTagName(Constants.Parameter);
			if(list != null){
				logger.info("There are "+ list.getLength() +"parameters to be added...");
				
				for(i=0; i<list.getLength(); i++)
				{
					parameter = (Element) list.item(i);
					name = parameter.getElementsByTagName(Constants.parameterName).item(0).getTextContent();
					type = parameter.getElementsByTagName(Constants.parameterType).item(0).getTextContent();
					value = parameter.getElementsByTagName(Constants.parameterValue).item(0).getTextContent();
					
					para = new Parameter();
					para.setParameterName(name);
					para.setParameterType(type);
					para.setParameterValue(value);	
					
					logger.info("Adding a new paramenter(" + name + ", " + type + ", " + value + ")");
					record.addParameter(para);
				}
			}
			
		}
		
		context.storeData(Constants.MonitorInfoFeedback, record);
		this.unit.dispatch(context);
	}
}
