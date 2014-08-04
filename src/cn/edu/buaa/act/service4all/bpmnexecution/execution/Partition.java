package cn.edu.buaa.act.service4all.bpmnexecution.execution;

import java.util.LinkedList;
import java.util.List;

public class Partition {
	
	 /*
     * record the nodeId
     */
	private List<String> nodeList;
	private String partId;
	/*
	 * default mainPool flag is false
	 */
	private String mainPoolFlag="false";
	private String poolId;

	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public Partition(String partId) {

		this.partId = partId;
		nodeList = new LinkedList<String>();
	}

	public String getPartId() {

		return this.partId;
	}
	
	public boolean addNode(String nodeId) {
		
		return nodeList.add(nodeId);
	}
	
	public List<String> getNodeList() {
		
		return nodeList;
	}
	public void setMainPoolFlag(String flag){
		mainPoolFlag=flag;
	}
	public String getMainPoolFlag(){
		return mainPoolFlag;
	}
}
