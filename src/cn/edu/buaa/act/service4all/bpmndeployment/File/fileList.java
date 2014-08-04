package cn.edu.buaa.act.service4all.bpmndeployment.File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class fileList 
{
	
	private Vector<myFile> list=null;
	private static fileList filelist=null;
	private final String postfix=".bpmn";
		
	
	private fileList()
	{
		list=new Vector<myFile>();		
	}
	
	public static fileList getNewInstance()
	{
		if(filelist==null)
			filelist=new fileList();
		return filelist;
	}
	
	//receive date from BPMNDeploy2ContainerEndpoint
	public deployMessage addNewFile(String filename,String bpmndata,String dir)
	{
		String path;
		File file=null;
		myFile myfile=null;
		FileOutputStream outputStream=null;
		deployMessage message=null;
		
		filename=adjustPostfix(filename);
		myfile=this.getFileByFileName(filename);
		
		if(myfile!=null)
		{
			message=new deployMessage(false,filename,"The "+filename+" has the same name with other bpmn file that was deployed earlier");
			return message;
		}
		
		path=dir+"/"+filename;
		
		//create the new bpmn file
		file=new File(path);
		try 
		{
			outputStream=new FileOutputStream(file);
		}
		catch (FileNotFoundException e) 
		{
			message=new deployMessage(false,filename,"Can't create the "+filename);
			return message;
		}	
		
		//write data to bpmn file
		try 
		{
			outputStream.write(bpmndata.getBytes());
			myfile=new myFile(true,filename);
			list.add(myfile);
			message=new deployMessage(true,myfile,filename+" is Deployed Succefully");
			
			return message;
		}
		catch (IOException e)
		{
			message=new deployMessage(false,filename,"Error at saving data to "+filename);
			return message;			
		}
		finally
		{
			try 
			{
				outputStream.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}		
	}
	
	public deployMessage deleteFile(String filename,String dir)
	{
		String path;
		File file=null;
		myFile myfile=null;
		deployMessage message=null;
		
		filename=adjustPostfix(filename);
		myfile=this.getFileByFileName(filename);
		if(myfile==null)
		{
			message=new deployMessage(false,filename,filename+" didn't exist");
			return message;			
		}
		
		path=dir+"/"+filename;
		file=new File(path);
		if(file.delete())
		{
			list.remove(myfile);
			message=new deployMessage(true,myfile,filename+" is undeployed successfully");			
		}
		else
		{
			message=new deployMessage(false,filename,filename+" is undeployed unsuccessfully");
		}
		return message;		
	}
	
	public queryMessage query(String filename , String dir)
	{
		queryMessage message=null;
		String path,data;
		File file=null;
		myFile myfile=null;
		FileInputStream inputStream=null;
		
		filename=adjustPostfix(filename);
		myfile=this.getFileByFileName(filename);
		
		if(myfile==null)
		{
			message=new queryMessage(false,filename,"",filename+" doesn't exist in file list");
			return message;			
		}
		
		path=dir+"/"+filename;
		file=new File(path);
		try 
		{		
			inputStream=new FileInputStream(file);		
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			message=new queryMessage(false,filename,"","Can't find "+filename);
			return message;
		}
		try 
		{
			int in;
			char c;
			data="";
			while((in=inputStream.read())!=-1)
			{
				c=(char) in;
				data+=c;
			}
			message=new queryMessage(true,filename,data,"data is read successfully from "+filename);
			return message;			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			message=new queryMessage(false,filename,"","Can't read data from "+filename);
			return message;
		}
		finally
		{
			try 
			{
				inputStream.close();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private String adjustPostfix(String filename)
	{
		if(!filename.toLowerCase().endsWith(postfix))
		{
			filename+=postfix;
		}		
		return filename;		
	}
	
	public  myFile getFileByFileName(String filename)
	{
		int i;
		myFile myfile;
		if(list==null)
			return null;
		for(i=0;i<list.size();i++)
		{
			myfile=list.elementAt(i);
			if(myfile.getFileName().equals(filename))
				return myfile;
		}		
		return null;
	}
}