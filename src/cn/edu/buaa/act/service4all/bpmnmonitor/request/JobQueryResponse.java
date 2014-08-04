package cn.edu.buaa.act.service4all.bpmnmonitor.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;


import cn.edu.buaa.act.service4all.bpmnmonitor.Job;
import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XmlObject;

public class JobQueryResponse extends BPMNMonitorResponse {
	
	private Job job;
	
	private final static String TAG_NAME = "JobQueryResponse";
	
	public JobQueryResponse(Job job){
		this.job = job;
	}

	
	
	@Override
	public Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
		Map<String, String> attrs = new HashMap<String, String>();
		
		addAttribute(JobQueryRequest.jobId, job.getJobId(), attrs);
		addAttribute(JobQueryRequest.serviceId, job.getServiceId(), attrs);
		addAttribute(JobQueryRequest.serviceName, job.getServiceName(), attrs);
		addAttribute(JobQueryRequest.timestamp, String.valueOf(job.getTimestamp().getTime()), attrs);
	
		return attrs;
	}

	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName();
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
