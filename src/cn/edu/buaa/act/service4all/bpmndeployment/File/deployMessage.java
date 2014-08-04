package cn.edu.buaa.act.service4all.bpmndeployment.File;

public class deployMessage 
{
	private boolean statement;
	private String information=null,fileName=null;
	private myFile file=null;
	
	public deployMessage(boolean statement,myFile file, String information)
	{
		this.statement=statement;
		this.file=file;
		this.information=information;	
		fileName=file.getFileName();
	}
	
	public deployMessage(boolean statement, String fileName,String information)
	{
		this.statement=statement;
		this.fileName=fileName;
		this.information=information;
	}
	
	public boolean getStatement() 
	{
		return statement;
	}
	
	public myFile getFile()
	{
		return file;
	}
	
	public String getInformation() 
	{
		return information;
	}
	
	public String getFileName()
	{
		return fileName;
	}
}
