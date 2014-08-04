package cn.edu.buaa.act.service4all.bpmnmonitor;

import java.util.Date;
import java.util.List;

public class MonitorRecord {
	
	private String jobId;
	private String nodeId;
	private boolean nodeStatus;
	private String nodeDesp;
	private boolean isError;
	private Date timestamp;
	private List<Parameter> ps;
	
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public boolean isNodeStatus() {
		return nodeStatus;
	}
	public void setNodeStatus(boolean nodeStatus) {
		this.nodeStatus = nodeStatus;
	}
	public String getNodeDesp() {
		return nodeDesp;
	}
	public void setNodeDesp(String nodeDesp) {
		this.nodeDesp = nodeDesp;
	}
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public List<Parameter> getPs() {
		return ps;
	}
	public void setPs(List<Parameter> ps) {
		this.ps = ps;
	}
	
	public String toString(){
		String str = "MonitorRecord [ jobId = " + jobId 
						+ ", nodeId = " + nodeId 
						+ ", nodeDesp = " + nodeDesp
						+ ", nodeStatus + " + nodeStatus
						+ ", timestamp = " + timestamp + "]\n";
		if(ps != null && ps.size() > 0){
			for(Parameter p : ps){
				str += "\t" + p.toString();
			}
		}
		return str;
	}
	
	
}
