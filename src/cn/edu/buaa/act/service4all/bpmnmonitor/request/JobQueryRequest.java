package cn.edu.buaa.act.service4all.bpmnmonitor.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;


import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XmlObject;

/**
 * the request :
 * 
 * <JobQueryRequest jobId="jobId for the job"/>
 * 
 * @author enqu
 *
 */
public class JobQueryRequest extends  BPMNMonitorRequest{
	
	private Job job;
	
	public final static String TAG_NAME = "JobQueryRequest";
	public final static String jobId = "jobId";
	public final static String serviceId = "serviceId";
	public final static String serviceName = "serviceName";
	public final static String timestamp = "timestamp";
	
	public JobQueryRequest(Job job){
		this.job = job;
	}

	@Override
	public Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
		Map<String, String> attrs = new HashMap<String, String>();
		
		addAttribute(jobId, job.getJobId(), attrs);
//		addAttribute(serviceId, job.getServiceId(), attrs);
//		addAttribute(serviceName, job.getServiceName(), attrs);
//		addAttribute(timestamp, String.valueOf(job.getTimestamp().getTime()), attrs);
		
		return attrs;
	}
	
	

	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return TAG_NAME;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<XmlObject> getAllChildrens() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QName getNamespace() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Object getObject(){
		return job;
	}
	
	

}
