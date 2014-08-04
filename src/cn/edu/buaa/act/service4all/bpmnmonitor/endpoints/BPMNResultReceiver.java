package cn.edu.buaa.act.service4all.bpmnmonitor.endpoints;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import cn.edu.buaa.act.service4all.bpmnmonitor.DataBaseUtils;
import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.Parameter;
import cn.edu.buaa.act.service4all.bpmnmonitor.ResultRecord;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XMLUtils;

public class BPMNResultReceiver extends Receiver {

	private final static Log logger = LogFactory.getLog(BPMNResultReceiver.class);
	
	public final static String EXCEP = "exception";
	
	public final static String IS_OK = "is_ok";
	
	public final static String TARGET_RESULT = "targetResult";
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		
		
		Boolean isOK = (Boolean)context.getData(IS_OK);
		if(isOK != null && !isOK){
			//handle the exception condition
			logger.warn("The query is not ok for the job ");
			
		}else{
			ResultRecord rr = (ResultRecord)context.getData(TARGET_RESULT);
			return createRespDoc(rr);
		}
		return null;
	}
	
	/**
	 * the result record xml response:
	 * 
	 * <BPMNResultRecordResponse jobId="">
	 * 		<parameters>
	 * 			<parameter name="" value="" type=""/>
	 * 		</parameters>
	 * </BPMNResultRecordResponse>
	 * 
	 * @param rr
	 * @return
	 */
	protected Document createRespDoc(ResultRecord rr){
		
		Document resp = XMLUtils.createNewDocument();
		
		Element root = resp.createElement("BPMNResultRecordResponse");
		root.setAttribute("jobId", rr.getJobId());
		resp.appendChild(root);
		
		List<Parameter> ps = rr.getPs();
		if(ps != null && ps.size() > 0){
			Element psEle = resp.createElement("parameters");
			for(Parameter p : ps){
				Element pEle = resp.createElement("parameter");
				pEle.setAttribute("name", p.getParameterName());
				pEle.setAttribute("value", p.getParameterValue());
				pEle.setAttribute("type", p.getParameterType());
				
				psEle.appendChild(pEle);
			}
			root.appendChild(psEle);
			
			return resp;
		}
		
		return null;
		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.newDocument();
//			
//			Element root = doc.createElement("response");
//			doc.appendChild(root);
//			
//			Element jobEle = doc.createElement("jobId");
//			root.appendChild(jobEle);
//			jobEle.setTextContent(rr.getJobId());
//			
//			List<Parameter> ps = rr.getPs();
//			if(ps != null){
//				
//				Element psEle = doc.createElement("parameters");
//				root.appendChild(psEle);
//				
//				for(Parameter p : ps){
//					Element pEle = doc.createElement("parameter");
//					psEle.appendChild(pEle);
//					
//					Element nameEle = doc.createElement("name");
//					nameEle.setTextContent(p.getParameterName());
//					pEle.appendChild(nameEle);
//					
//					Element valueEle = doc.createElement("value");
//					valueEle.setTextContent(p.getParameterValue());
//					pEle.appendChild(valueEle);
//					
//					Element typeEle = doc.createElement("type");
//					typeEle.setTextContent(p.getParameterType());
//					pEle.appendChild(typeEle);
//				}
//			}else{
//				logger.warn("There is no parameter for the job: " + rr.getJobId());
//			}
//			
//			
//			
//			return doc;
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			logger.warn("Can't create the response message", e);
//		}
//		return null;
	}

	@Override
	public void handlRequest(Document doc, ExchangeContext context)
			throws MessageExchangeInvocationException {
		// TODO Auto-generated method stub
		Job job = parseJob(doc);
		
		if(job == null){
			// the request message is invalidate
			context.storeData(IS_OK, Boolean.valueOf("false"));
			context.storeData(EXCEP, "Can't parse the job from the request!");
			
		}else{
			
//			//The database is Ready?
//			if(!DataBaseUtils.isReady()){
//				try {
//					DataBaseUtils.init();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					logger.warn("Can't find the jdbc driver class", e);
//					throw new MessageExchangeInvocationException("Can't find the jdbc driver class");
//				
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					logger.warn("Can't execute the sql sentence", e);
//					throw new MessageExchangeInvocationException("Can't execute the sql sentence");
//				}
//			}
			
			String jobId = job.getJobId();
			ResultRecord rr = DataBaseUtils.getResultRecordByJobId(jobId);
			
			//get the result record
			if(rr != null){
				context.storeData(TARGET_RESULT, rr);
				this.sendResponseMessage(context);
				return;
			}else{
				//can't get the result record
				logger.warn("Can't get the result record for the job : " + jobId);
				context.storeData(IS_OK, Boolean.valueOf("false"));
				context.storeData(EXCEP, "Can't get the result record for the job : " + jobId);
			}
		}
		
	}

	protected Job parseJob(Document doc){
		Job job = new Job();
//		
//		if(doc.getElementsByTagName("") == null 
//				|| doc.getElementsByTagName("").getLength() <= 0){
//			logger.warn("Get get the jobId element from the request");
//			return null;
//		}
//		
//		Element jobIdEle = (Element)doc.getElementsByTagName("jobId").item(0);
//		String jobId = jobIdEle.getTextContent();
		Element root = doc.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		
		logger.info("Get the job : " + jobId);
		job.setJobId(jobId);
		
		return job;
	}
}
