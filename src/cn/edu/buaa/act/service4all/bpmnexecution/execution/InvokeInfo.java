package cn.edu.buaa.act.service4all.bpmnexecution.execution;

public class InvokeInfo {
	private String compositeServiceName;
	private long jobId;
	private String enterNodeId;
	private String endNodeId;
	
	private String busAddress;
	private String monitorInfo;
	private String databaseInfo;
	private String resourceAddr;
	private String resourceId;

	public InvokeInfo() {

		compositeServiceName = "default";

		enterNodeId = "default";
		endNodeId = "default";
	}

	public String getCompositeServiceName() {
		return compositeServiceName;
	}

	public void setCompositeServiceName(String compositeServiceName) {
		this.compositeServiceName = compositeServiceName;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getEnterNodeId() {
		return enterNodeId;
	}

	public void setEnterNodeId(String nodeId) {
		this.enterNodeId = nodeId;
	}

	public String getResourceAddr() {
		return resourceAddr;
	}

	public void setResourceAddr(String resourceAddr) {
		this.resourceAddr = resourceAddr;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(String endNodeId) {
		this.endNodeId = endNodeId;
	}
	public String getBusAddress() {
		return busAddress;
	}

	public void setBusAddress(String busAddress) {
		this.busAddress = busAddress;
	}

	public String getMonitorInfo() {
		return monitorInfo;
	}

	public void setMonitorInfo(String monitorInfo) {
		this.monitorInfo = monitorInfo;
	}

	public String getDatabaseInfo() {
		return databaseInfo;
	}

	public void setDatabaseInfo(String databaseInfo) {
		this.databaseInfo = databaseInfo;
	}
}
