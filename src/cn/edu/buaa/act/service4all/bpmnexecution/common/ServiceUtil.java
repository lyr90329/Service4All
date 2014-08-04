package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.ArrayList;
import java.util.List;

public class ServiceUtil {
	private String serviceName;
	private String operation;
	private List<String> inputType;
	private String outputType;
	private String serviceNameSpaces;
	private String serviceUrl;

	public ServiceUtil() {
		inputType = new ArrayList();
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setInputType(List list) {
		inputType = list;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getOperation() {
		return operation;
	}

	public List getInputType() {
		return inputType;
	}

	public void setOutputType(String type) {
		this.outputType = type;
	}

	public String getOutputType() {
		return outputType;
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
