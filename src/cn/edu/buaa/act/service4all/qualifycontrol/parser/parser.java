package cn.edu.buaa.act.service4all.qualifycontrol.parser;

import org.jdom.Document;

public abstract class parser 
{
	public parser()
	{
		
	}
	
	//return response document
	public abstract Document parse(Document doc);	
}
