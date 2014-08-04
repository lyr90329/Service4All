package cn.edu.buaa.act.service4all.bpmnmonitor;

import java.util.Date;
import java.util.List;

public class ResultRecord {
	
	private String jobId;
	private boolean isSuccessful;
	private String desp;
	private Date timestamp;
	private List<Parameter> ps;
	
	
	
	public ResultRecord(String jobId, boolean isSuccessful, String desp,
			Date timestamp, List<Parameter> ps) {
		super();
		this.jobId = jobId;
		this.isSuccessful = isSuccessful;
		this.desp = desp;
		this.timestamp = timestamp;
		this.ps = ps;
	}
	
	public ResultRecord(){
		
	}
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public boolean isSuccessful() {
		return isSuccessful;
	}
	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	public String getDesp() {
		return desp;
	}
	public void setDesp(String desp) {
		this.desp = desp;
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
		String str = "ResultRecord [ jobId = " + jobId
						+ ", desp = " + desp
						+ ", isSuccessful + " + isSuccessful
						+ ", timestamp = " + timestamp + "]\n";
		if(ps != null && ps.size() > 0){
			for(Parameter p : ps){
				str += "\t" + p.toString();
			}
		}
		return str;
	}
	
}
