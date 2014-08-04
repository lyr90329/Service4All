package cn.edu.buaa.act.service4all.bpmndeployment.File;

import java.io.File;

import org.act.sdp.appengine.cmp.AppEngineContext;
import org.act.sdp.appengine.cmp.BusinessUnit;
import org.act.sdp.appengine.transaction.exception.AppEngineException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class MyBusinessUnit extends BusinessUnit
{
	private static String dir;
	private final Log logger = LogFactory.getLog(MyBusinessUnit.class);
	private static boolean direxist=false;
	
	public void init(AppEngineContext context) throws AppEngineException
	{
		super.init(context);
		makeDir();
	}
	
	private void makeDir()
	{
		File file=null;			
		dir=this.context.getCmpWorkspace()+Constants.addpath;
    	file=new File(dir);
    	if((!file.exists())&&(!file.isDirectory()))
    	{
	    	file.mkdir();	
	    	direxist=true;
    	}	
	}
	
	public String getDir()
	{
		return dir;
	}

}
