package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.LinkedList;

public class Node {

	private String nodeId;
	private LinkedList<String> adjacentNodes;
	private String nodeType;//type: service or inservice

	public Node(String nodeId) {
		
		this.nodeId = nodeId;
		adjacentNodes = new LinkedList<String>();
		this.nodeType = "service";
	}
	
	public Node(String nodeId, String nodeType) {
		
		this.nodeId = nodeId;
		adjacentNodes = new LinkedList<String>();
		this.nodeType = nodeType;
	}
		
	public String getNodeId() {
		
		return nodeId;
	}
	
	public LinkedList<String> getAdjacentNodes() {
		
		return adjacentNodes;
	}
	
	public String getNodeType() {
		
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		
		this.nodeType = nodeType;
	}
	
	public boolean addAdjacentNode(String nodeId) {
		
		return adjacentNodes.add(nodeId);
	}
	
	public boolean removeAdjacentNode(String nodeId) {
		
		return adjacentNodes.remove(nodeId);
	}
	
}
