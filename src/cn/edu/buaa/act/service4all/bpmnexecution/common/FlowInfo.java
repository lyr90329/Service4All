package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.ArrayList;
import java.util.List;

public class FlowInfo {
	/**
	 * the flow node type
	 */
	private String type;
	/**
	 * the start node  id in the flow 
	 */
	private String startNodeId;
	/**
	 * the end node  id in the flow 
	 */
	private String endNodeId;
	/**
	 * the middle node in the flow 
	 */
	private List<FlowInfo> nodeList;
	/**
	 * the serviceUtil
	 */
	private ServiceUtil util;
	/**
	 * QoS Value
	 */
	private QoSValue qos;

	public FlowInfo(String type) {
		this.type = type;
		nodeList = new ArrayList();
	}

	public void setStartNodeId(String startNodeId) {
		this.startNodeId = startNodeId;
	}

	public void setEndNodeId(String endNodeId) {
		this.endNodeId = endNodeId;
	}

	public void addFlowInfo(FlowInfo info) {
		nodeList.add(info);
	}

	public String getType() {
		return type;
	}

	public String getStartNodeId() {
		return startNodeId;
	}

	public String getEndNodeId() {
		return endNodeId;
	}

	public List getNodeList() {
		return nodeList;
	}
	public void setServiceUtil(ServiceUtil util){
		this.util=util;
	}
	public ServiceUtil getServiceUtil(){
		return util;
	}
	public void setQoSValue(QoSValue qos){
		this.qos=qos;
	}
	public QoSValue getQoSValue(){
		return qos;
	}
}
