package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Parameter;
import cn.edu.buaa.act.service4all.bpmnexecution.TaskExecuteResult;


/**
 * this BPMNExecuteResultReceiver is no use
 * 
 * @author enqu
 *
 */
public class BPMNExecuteResultReceiver extends Receiver
{
	private final Log logger = LogFactory.getLog(BPMNExecuteResultReceiver.class);
	@Override
	public Document createResponseDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
//		Document doc;
//		
//		doc=(Document) context.getData(Constants.ExecuteResult);
//		logger.info(doc);
//		return doc;
//		try 
//		{
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document doc = builder.newDocument();
//		
//			Element root=doc.createElement("BPMNExecuteFeedback");
//			root.setAttribute("serviceId", job.getServiceID());
//			root.setAttribute("jobId", job.getJobID());
//			doc.appendChild(root);
//			
//			Element engList = doc.createElement("engineList");
//			Element engEle = doc.createElement("engine");
//			engEle.setAttribute("id", engine.getEngineID());
//			engEle.setAttribute("status", "true");
//			engList.appendChild(engEle);
//			root.appendChild(engList);
//			
//			return doc;
//		}
//		catch (ParserConfigurationException e) 
//		{
//			// TODO Auto-generated catch block
//			logger.warn("Can't create the request document: " + e);
//		}	
//		return null;
		return null;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		Element root,element;
		TaskExecuteResult result=null;
		String servicename,jobid,description,issuccessful,name,value,type;
		NodeList list;
		int i;
		Parameter parameter;
		
		logger.info(doc);
		result=new TaskExecuteResult();
		root=doc.getDocumentElement();
		
		type=root.getAttributeNode(Constants.type).getValue();
		context.storeData(Constants.type, type);
		
		servicename=root.getElementsByTagName(Constants.serviceName).item(0).getTextContent();
		context.storeData(Constants.serviceName, servicename);
		
		jobid=root.getElementsByTagName(Constants.jobId).item(0).getTextContent();
		result.setJobId(jobid);
		context.storeData(Constants.jobId, jobid);
		
		description=root.getElementsByTagName(Constants.description).item(0).getTextContent();
		result.setDescription(description);
		context.storeData(Constants.description, description);
		
		issuccessful=root.getElementsByTagName(Constants.isSuccessful).item(0).getTextContent();
		context.storeData(Constants.isSuccessful, issuccessful);
		if(issuccessful.equals(Constants.booltrue))
			result.setState(TaskExecuteResult.completed);
		else
			result.setState(TaskExecuteResult.error);
		
		list=root.getElementsByTagName(Constants.Parameter);
		for(i=0;i<list.getLength();i++)
		{
			element=(Element) list.item(i);
			name=element.getElementsByTagName(Constants.parameterName).item(0).getTextContent();
			type=element.getElementsByTagName(Constants.parameterType).item(0).getTextContent();
			value=element.getElementsByTagName(Constants.parameterValue).item(0).getTextContent();
			
			parameter=new Parameter();
			parameter.setParameterName(name);
			parameter.setParameterType(type);
			parameter.setParameterValue(value);
			result.addParameter(parameter);		
		}
		context.storeData(Constants.ExecuteResult,result);	
		this.unit.dispatch(context);
	}
}
