package cn.edu.buaa.act.service4all.core.samanager.bpmnengine;

public class QueryTask {
	
	protected String jobId;
	protected String serviceId;
	protected String deploymentNum;
	protected String deployType;
	
	public QueryTask(){
		
	}
	
	public QueryTask(String jobId, 
					 String serviceId, 
					 String deploymentNum,
					 String deployType) {
		
		super();
		this.jobId = jobId;
		this.serviceId = serviceId;
		this.deploymentNum = deploymentNum;
		this.deployType = deployType;
		
	}



	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getDeploymentNum() {
		return deploymentNum;
	}
	public void setDeploymentNum(String deploymentNum) {
		this.deploymentNum = deploymentNum;
	}
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	
	
	
}
