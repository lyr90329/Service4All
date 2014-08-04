package cn.edu.buaa.act.service4all.bpmnexecution;

public class Parameter 
{
	private String parameterName,parameterValue,parameterType,counter,messageFlag;
	
	public Parameter()
	{
		
	}
	
	public String getCounter()
	{
		return counter;
	}

	public void setCounter(String counter) 
	{
		this.counter = counter;
	}

	public String getMessageFlag() 
	{
		return messageFlag;
	}

	public void setMessageFlag(String messageFlag) 
	{
		this.messageFlag = messageFlag;
	}
	
	public String getParameterName() 
	{
		return parameterName;
	}

	public void setParameterName(String parameterName) 
	{
		this.parameterName = parameterName;
	}

	public String getParameterValue() 
	{
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) 
	{
		this.parameterValue = parameterValue;
	}

	public String getParameterType()
	{
		return parameterType;
	}

	public void setParameterType(String parameterType) 
	{
		this.parameterType = parameterType;
	}
}