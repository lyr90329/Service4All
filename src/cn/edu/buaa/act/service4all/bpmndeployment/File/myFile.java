package cn.edu.buaa.act.service4all.bpmndeployment.File;

public class myFile 
{
	//filename of this file
	private String name,serviceid;
	
	//this statement of this file
	private boolean statement;
	
	public myFile(boolean statement, String fileName)
	{
		this.name=fileName;
		this.statement=statement;
	}
	
	public String getFileName()
	{
		return name;
	}
	
	public void setServiceID(String serviceid)
	{
		this.serviceid=serviceid;
	}
	
	public String getServiceID()
	{
		return serviceid;
	}
}
