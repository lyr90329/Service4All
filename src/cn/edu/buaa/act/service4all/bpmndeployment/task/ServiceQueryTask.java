package cn.edu.buaa.act.service4all.bpmndeployment.task;

import org.w3c.dom.Document;

public class ServiceQueryTask extends Task {
	
	
	protected Document bpmnDoc;
	protected String jobId;

	
	public Document getBpmnDoc() {
		return bpmnDoc;
	}
	public void setBpmnDoc(Document bpmnDoc) {
		this.bpmnDoc = bpmnDoc;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	
	
}
