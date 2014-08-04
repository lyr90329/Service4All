package cn.edu.buaa.act.service4all.bpmnmonitor.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.MonitorRecord;
import cn.edu.buaa.act.service4all.bpmnmonitor.Parameter;
import cn.edu.buaa.act.service4all.bpmnmonitor.ResultRecord;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XMLUtils;

public class BPMNMonitorClientUtils {
	
	private static Log logger = LogFactory.getLog(BPMNMonitorClientUtils.class);
	
	private static String base_url = "localhost:8192/";
	private static String jobQueryPath = "appengine/BPMNJobQuery/";
	private static String resultQueryPath = "appengine/BPMNResult/";
	private static String monitorInfoPath = "appengine/BPMNMonitorInfo/";
	private static String userQueryPath = "appengine/BPMNUserQuery/";
	
	public static List<BPMNService> getBPMNServiceByUser(String user) throws Exception{
		logger.info("Get the BPMN Service by userName: " + user);
		
		Document doc = XMLUtils.createNewDocument();
		if(doc == null){
			logger.warn("Can't create request the document for the bpmn user query!");
			return null;
		}
		
		Element root = doc.createElement("BPMNUserQueryReq");
		root.setAttribute("user", user);
		doc.appendChild(root);
		
		//query specified job
		Document respDoc = queryUserService(doc);		
		if(respDoc == null){
			logger.warn("the user query result document is null");
			return null;
		}
		return parseUserQuery(respDoc, user);
	}
	
	private static List<BPMNService> parseUserQuery(Document doc, String user) throws Exception{
		List<BPMNService> sers = new ArrayList<BPMNService>();
		
		NodeList excepEles = doc.getElementsByTagName("exception");
		if(excepEles != null && excepEles.getLength() > 0){
			// there is some exception when query the service 
			Element excepEle = (Element)excepEles.item(0);
			throw new Exception(excepEle.getTextContent());
		}
		
		NodeList serEles = doc.getElementsByTagName("service");
		if(serEles != null){
			logger.info("Get " + serEles.getLength() + "BPMN Service for the user: " + user);
			for(int i = 0; i < serEles.getLength(); i++){
				Element se = (Element)serEles.item(i);
				BPMNService bpmnSer = new BPMNService();
				bpmnSer.setServiceId(se.getAttribute("id"));
				bpmnSer.setServiceName(se.getAttribute("name"));
				bpmnSer.setUser(user);
				sers.add(bpmnSer);
			}
		}
		
		return sers;
	}
	
	private static Document queryUserService(Document doc){
		// send the request to the target Url
		String targetUrl = base_url + userQueryPath;
		HttpExchangeClient client = new HttpExchangeClient();
		try {
			return client.sendGetRequest(doc, targetUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("can't query the query by http ", e);
		}
		return null;
	}
	
	public static Job getJobById(String jobId) {
		
		logger.info("Get the job by its id: " + jobId);
		
		Document doc = XMLUtils.createNewDocument();
		if(doc == null){
			logger.warn("Can't get the job");
			return null;
		}
		
		Element root = doc.createElement("BPMNJobQueryRequest");
		root.setAttribute("jobId", jobId);
		doc.appendChild(root);
		
		//query specified job
		Document respDoc = queryJob(doc);		
		if(respDoc == null){
			logger.warn("the query result document is null");
			return null;
		}
		return parseJob(respDoc);
	}
	
	private static Document queryJob(Document request){
		
		// send the request to the target Url
		String targetUrl = base_url + jobQueryPath;
		HttpExchangeClient client = new HttpExchangeClient();
		try {
			return client.sendGetRequest(request, targetUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("can't query the query by http ", e);
		}
		return null;
	}
	
	private static Job parseJob(Document resp){
		
		Job job = new Job();
		
		Element root = resp.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		job.setJobId(jobId);
		
		String serviceId = root.getAttribute("serviceId");
		job.setServiceId(serviceId);
		
		Long timestamp = Long.parseLong(root.getAttribute("timestamp"));
		job.setTimestamp(new Date(timestamp));

		String serviceName = root.getAttribute("serviceName");
		job.setServiceName(serviceName);
		
		return job;
	}
	
	public static List<MonitorRecord> getMonitorRecordByJobId(String jobId){
		
		Document req = createMonitorRecordReqDoc(jobId);
		
		String queryMonitorUrl = base_url + monitorInfoPath;
		HttpExchangeClient client = new HttpExchangeClient();
		
		try {
			Document resp = client.sendGetRequest(req, queryMonitorUrl);
			return parseMonitorRecords(resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't query the monitor info by http for the job: " + jobId, e);
		}
		return null;
	}
	
	private static Document createMonitorRecordReqDoc(String jobId){
		Document req = XMLUtils.createNewDocument();
		Element root = req.createElement("BPMNMonitorInfoRequest");
		root.setAttribute("jobId", jobId);
		req.appendChild(root);
		
		return req;
	}
	
	private static List<MonitorRecord> parseMonitorRecords(Document resp){
		
		Element root = resp.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		
		NodeList rds = resp.getElementsByTagName("record");
		if(rds != null && rds.getLength() > 0){
			List<MonitorRecord> mrs = new ArrayList<MonitorRecord>();
			for (int i = 0; i < rds.getLength(); i++) {
				
				Element rd = (Element)rds.item(i);
				String nodeId = rd.getAttribute("nodeId");
				Boolean isError = Boolean.valueOf(rd.getAttribute("isError"));
				String nodeDesp = rd.getAttribute("nodeDesp");
				Boolean nStatus = Boolean.valueOf(rd.getAttribute("nodeStatus"));
				Long timestamp = Long.valueOf(rd.getAttribute("timestamp"));
				
				MonitorRecord r = new MonitorRecord();
				r.setError(isError);
				r.setJobId(jobId);
				r.setNodeDesp(nodeDesp);
				r.setNodeId(nodeId);
				r.setTimestamp(new Date(timestamp));
				r.setNodeStatus(nStatus);
				// parse the parameters
				List<Parameter> ps = parseParameters(rd);
				r.setPs(ps);
				
				mrs.add(r);
				
			}
			return mrs;
		}
		
		return null;
	}
	
	private static List<Parameter> parseParameters(Element parent){
		NodeList psElements = parent.getElementsByTagName("parameter");
		if(psElements != null && psElements.getLength() > 0){
			// parse target element
			List<Parameter> ps = new ArrayList<Parameter>();
			for(int i = 0; i < psElements.getLength(); i++){
				Parameter p = new Parameter();
				Element pEle = (Element)psElements.item(i);
				
				String name = pEle.getAttribute("name");
				String value = pEle.getAttribute("value");
				String type = pEle.getAttribute("type");
				p.setParameterName(name);
				p.setParameterType(type);
				p.setParameterValue(value);
				ps.add(p);
			}
			return ps;
		}
		return null;
	}
	
	
	public static ResultRecord getResultRecordByJobId(String jobId){
		logger.info("Create the result record request: " + jobId);
		Document reqDoc = createResultReq(jobId);
		
		HttpExchangeClient client = new HttpExchangeClient();
		String targetUrl = base_url + resultQueryPath;
		try {
			Document resp = client.sendGetRequest(reqDoc, targetUrl);
			return parseResultRecordResp(resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("can't query the execute result for the job: " + jobId, e);
		}
		return null;
	}
	
	private static ResultRecord parseResultRecordResp(Document resp){
		
		logger.info("Parse the response message to get bpmn result record");
		if(resp.getElementsByTagName("exception") != null 
				&& resp.getElementsByTagName("exception").getLength() > 0){
			return null;
		}
		
		ResultRecord r = new ResultRecord();
		
		Element root = resp.getDocumentElement();
		String jobId = root.getAttribute("jobId");
		r.setJobId(jobId);
		
		NodeList psNodeList = root.getElementsByTagName("parameter");
		if(psNodeList != null && psNodeList.getLength() > 0){
			List<Parameter> ps = new ArrayList<Parameter>();
			for(int i = 0; i < psNodeList.getLength(); i++){
				Element pElement = (Element)psNodeList.item(i);
				String name = pElement.getAttribute("name");
				String value = pElement.getAttribute("value");
				String type = pElement.getAttribute("type");
				
				Parameter p = new Parameter();
				p.setParameterName(name);
				p.setParameterType(type);
				p.setParameterValue(value);
				
				ps.add(p);
			}
			r.setPs(ps);
			return r;
		}
		return null;
	}
	
	private static Document createResultReq(String jobId){
		
		Document reqDoc = XMLUtils.createNewDocument();
		
		Element root = reqDoc.createElement("BPMNResultRecordRequest");
		root.setAttribute("jobId", jobId);
		reqDoc.appendChild(root);
		
		return reqDoc;
		
	}
	
}

