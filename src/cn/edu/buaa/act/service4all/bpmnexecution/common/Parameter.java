package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.io.Serializable;

public class Parameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5546213L;

	private static final String DEFAULT_NAME = "name";

	private static final String DEFAULT_TYPE = "String";

	private static final String DEFAULT_VALUE = " ";

	private String paramName;

	private String paramType;

	private String paramValue;

	private long counter;
	
	private boolean messageFlag;

	public Parameter() {
		this.paramName = DEFAULT_NAME;
		this.paramType = DEFAULT_TYPE;
		this.paramValue = DEFAULT_VALUE;
		this.counter = 0;
		this.messageFlag = false;
	}

	public Parameter(String paramName, String paramValue) {
		this.paramName = paramName;
		this.paramType = DEFAULT_TYPE;
		this.paramValue = paramValue;
		this.counter = 0;
		this.messageFlag = false;
	}

	public Parameter(String paramName, String paramType, String paramValue) {
		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;
		this.counter = 0;
		this.messageFlag = false;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamName() {
		return paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public boolean getMessageFlag() {
		return messageFlag;
	}

	public void setMessageFlag(boolean messageFlag) {
		this.messageFlag = messageFlag;
	}
}

