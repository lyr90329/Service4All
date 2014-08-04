package cn.edu.buaa.act.service4all.bpmnexecution.endpoints;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.act.sdp.appengine.cmp.Receiver;
import org.act.sdp.appengine.messaging.ExchangeContext;
import org.act.sdp.appengine.messaging.util.XMLUtils;
import org.act.sdp.appengine.transaction.exception.MessageExchangeInvocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.bpmnexecution.Constants;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;
import cn.edu.buaa.act.service4all.bpmnexecution.Parameter;
import cn.edu.buaa.act.service4all.bpmnexecution.common.IDGenerator;

/**
 * the receiver for bpmn execution
 * 
 * @author enqu
 *
 */
public class BPMNServiceExecuteReceiver extends Receiver 
{
	
	private final Log logger = LogFactory.getLog(BPMNServiceExecuteReceiver.class);
	
	private final String ip_tag = "execute:IP";
	private final String params_tag = "execute:parameters";
	private final String csname_tag = "compositeServiceName";
	
	private final String MONITOR_ADDR = "";
	private final String RESULT_ADDR = "";
	
	private long id = 1;
	
	@Override
	public Document createResponseDocument(ExchangeContext context) throws MessageExchangeInvocationException 
	{
		String jobid;
		DocumentBuilder builder;
		Document doc = null;
		Element root,element;
		String xmlns,type,servicename,serviceid;
		
		try 
		{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.newDocument();
		}
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
			
		}	
		Job job = (Job)context.getData(Constants.job);
//		jobid=(String) context.getData(Constants.jobId);
//		xmlns=(String) context.getData(Constants.xmlnssc);
//		type=(String) context.getData(Constants.type);
//		servicename=(String) context.getData(Constants.serviceName);
//		serviceid=(String) context.getData(Constants.serviceID);
		if(job == null){
			//by tangyu
			logger.warn("*******************************************");		
			logger.warn("Can't get the job after some query");	
			logger.warn("*******************************************");		
			root=doc.createElement(Constants.scExecuteResponse);
			root.setTextContent("Can't get the job after some query");
			doc.appendChild(root);
			
		}else{
					
			jobid = job.getJobID();
			//by tangyu
			logger.info("*******************************************");		
			logger.info("Get the job after querying:" + jobid);
			logger.info("*******************************************");		
	
			root=doc.createElement(Constants.scExecuteResponse);
			root.setAttribute(Constants.serviceName, job.getServiceName());
			root.setAttribute(Constants.serviceID, job.getServiceID());
			doc.appendChild(root);
			
			element=doc.createElement(Constants.jobId);
			element.setTextContent(jobid);
			root.appendChild(element);
			
			element=doc.createElement(Constants.description);
			element.setTextContent("Invoke successfully!");
			root.appendChild(element);
		}
		
		
		return doc;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context) throws MessageExchangeInvocationException 
	{
		// TODO Auto-generated method stub
		logger.info("Receiving the document for bpmn execution:" + XMLUtils.retrieveDocumentAsString(doc));
		createjob(doc,context);		
		this.unit.dispatch(context);			
	}
	
	private synchronized long getID(){
		return IDGenerator.generateID();
	}
	
	private Job createBPMNJob(Document doc){
		
		Job job = new Job();
		
		//set the Job's ID uniquely
//		long jobID = JobCounter.generateJobID();
//		job.setJobID(jobID);
		
		Element root = doc.getDocumentElement();
		String serviceId = root.getAttribute("serviceID");
		String serviceName = root.getAttribute("serviceName");
		
//		Node tServiceNode = doc.getElementsByTagName(csname_tag).item(0);
//		String targetService = tServiceNode.getTextContent();
		job.setServiceName(serviceName);
		job.setServiceID(serviceId);
		
		//get the parameters xml content
		Node parasNode = doc.getElementsByTagName(params_tag).item(0);
		job.setParameters(parasNode);
		
		//set the jobId;
		job.setJobID(String.valueOf(getID()));
		logger.info("BPMNServiceExcecuteReceiver jobId="+job.getJobID());

		//get the IP 
		Node ipNode = doc.getElementsByTagName(ip_tag).item(0);
		//job.setIp(ipNode.getTextContent());
		job.setMonitorIp(ipNode.getTextContent() + MONITOR_ADDR);
		job.setExecuteResult(ipNode.getTextContent()+ RESULT_ADDR);
		
		return job;
	}
	
	private void createjob(Document doc,ExchangeContext context)
	{
//		Element root,element;
//		NodeList list=null;
//		String servicename,serviceid,counter,messageflag,paramname,paramtype,paramvalue,type,xmlns;
//		Parameter parameter;
//		int i;
//		Job job;
//			
//		job=new Job();
//		root=doc.getDocumentElement();
//		
//		servicename=root.getAttributeNode(Constants.serviceName).getValue();
//		job.setServiceName(servicename);
//		context.storeData(Constants.serviceName, servicename);
//		
//		serviceid=root.getAttributeNode(Constants.serviceID).getValue();
//		job.setServiceID(serviceid);		
//		context.storeData(Constants.serviceID, serviceid);
//		
//		type=root.getAttributeNode(Constants.type).getValue();
//		context.storeData(Constants.type, type);
//		
//		xmlns=root.getAttributeNode(Constants.xmlnssc).getValue();
//		context.storeData(Constants.xmlnssc, xmlns);
//		
//		list=root.getElementsByTagName(Constants.Parameter);
//		
//		for(i=0;i<list.getLength();i++)
//		{
//			element=(Element) list.item(i);
//			counter=element.getElementsByTagName(Constants.counter).item(0).getTextContent();
//			messageflag=element.getElementsByTagName(Constants.messageFlag).item(0).getTextContent();
//			paramname=element.getElementsByTagName(Constants.paramName).item(0).getTextContent();
//			paramtype=element.getElementsByTagName(Constants.paramType).item(0).getTextContent();
//			paramvalue=element.getElementsByTagName(Constants.paramValue).item(0).getTextContent();
//			
//			parameter=new Parameter();
//			parameter.setParameterName(paramname);
//			parameter.setParameterType(paramtype);
//			parameter.setParameterValue(paramvalue);
//			parameter.setCounter(counter);
//			parameter.setMessageFlag(messageflag);
//			job.addParameter(parameter);		
//		}
		Job job = createBPMNJob(doc);
		context.storeData(Constants.job, job);
	}
}
