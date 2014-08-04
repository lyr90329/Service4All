package cn.edu.buaa.act.service4all.bpmndeployment.task;

import org.w3c.dom.Document;

public class ServiceDeployTask extends Task {
	
	private String provider;
	private Document bpmnDoc;
	private Document partitionDoc;
	
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public Document getBpmnDoc() {
		return bpmnDoc;
	}
	public void setBpmnDoc(Document bpmnDoc) {
		this.bpmnDoc = bpmnDoc;
	}
	public Document getPartitionDoc() {
		return partitionDoc;
	}
	public void setPartitionDoc(Document partitionDoc) {
		this.partitionDoc = partitionDoc;
	}
	
	
}
