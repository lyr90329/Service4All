package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.List;

public class ServiceInfo {
	private String serviceName;
	private String operation;
	private String serviceNameSpaces;
	private String serviceUrl;

	public ServiceInfo() {
		serviceName = "default";
		operation = "default";
		serviceNameSpaces = "default";
		serviceUrl = "default";
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getOperation() {
		return operation;
	}

	public void setServiceNameSpaces(String spaces) {
		this.serviceNameSpaces = spaces;
	}

	public String getServiceNameSpaces() {
		return serviceNameSpaces;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}
}
