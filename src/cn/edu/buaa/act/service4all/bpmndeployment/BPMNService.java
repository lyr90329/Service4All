package cn.edu.buaa.act.service4all.bpmndeployment;

public class BPMNService {
	
	protected String serviceName;
	protected String serviceID;
	protected String bpmnPath;
	protected String partitionPath;
	protected String provider;
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceID() {
		return serviceID;
	}
	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}
	public String getBpmnPath() {
		return bpmnPath;
	}
	public void setBpmnPath(String bpmnPath) {
		this.bpmnPath = bpmnPath;
	}
	
	public String getPartitionPath() {
		return partitionPath;
	}
	public void setPartitionPath(String partitionPath) {
		this.partitionPath = partitionPath;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	
	
}
