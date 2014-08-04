package cn.edu.buaa.act.service4all.bpmnmonitor;

import java.util.Date;

public class Job {
	
	private String jobId;
	private String serviceName;
	private String serviceId;
	private Date timestamp;
	
	
	
	public Job(String jobId, String serviceName, String serviceId,
			Date timestamp) {
		super();
		this.jobId = jobId;
		this.serviceName = serviceName;
		this.serviceId = serviceId;
		this.timestamp = timestamp;
	}
	
	public Job(){
		super();
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString(){
		String str = "Job[ jobId = " + jobId 
					+ ", serviceName = " + serviceName 
					+ ", serviceId = " + serviceId
					+ ", timestamp = " + timestamp + "]\n";
		
		return str;
	}
	
	
	
}
