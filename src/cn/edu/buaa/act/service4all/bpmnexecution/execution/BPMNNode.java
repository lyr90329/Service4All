package cn.edu.buaa.act.service4all.bpmnexecution.execution;

public class BPMNNode {
	
	
	private String enterNodeId;
	
	private String endNodeId;

	
	
	public BPMNNode() {
		super();
	}

	
	
	public BPMNNode(String enterNodeId, String endNodeId) {
		super();
		this.enterNodeId = enterNodeId;
		this.endNodeId = endNodeId;
	}



	public String getEnterNodeId() {
		return enterNodeId;
	}

	public void setEnterNodeId(String enterNodeId) {
		this.enterNodeId = enterNodeId;
	}

	public String getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(String endNodeId) {
		this.endNodeId = endNodeId;
	}
	
	
}
