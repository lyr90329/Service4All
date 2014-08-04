package cn.edu.buaa.act.service4all.bpmndeployment.File;

public class queryMessage 
{
	private boolean statement;
	private String filename,data,information;
	
	public queryMessage(boolean statement,String fileName,String data,String information)
	{
		this.statement=statement;
		this.filename=fileName;
		this.data=data;
		this.information=information;
	}
	
	public boolean getStatement() 
	{		
		return statement;
	}
	
	public String getFilename() 
	{
		return filename;
	}
	
	public String getData() 
	{
		return data;
	}
	
	public String getInformation() 
	{
		return information;
	}
}