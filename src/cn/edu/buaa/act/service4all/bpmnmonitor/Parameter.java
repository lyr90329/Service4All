package cn.edu.buaa.act.service4all.bpmnmonitor;

public class Parameter {
	
	
	private String parameterName;
	private String parameterType;
	private String parameterValue;
	
	public Parameter(){
		
	}
	
	public Parameter(String parameterName, String paramaterType,
			String parameterValue) {
		super();
		this.parameterName = parameterName;
		this.parameterType = paramaterType;
		this.parameterValue = parameterValue;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterType() {
		return parameterType;
	}
	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	
	public String toString(){
		String str = "Parameter[ parameterName = " + parameterName 
						+ ", parameterType = " + parameterType 
						+ ", parameterValue = " + parameterValue + "]\n";
		
		
		return str;
	}
}
