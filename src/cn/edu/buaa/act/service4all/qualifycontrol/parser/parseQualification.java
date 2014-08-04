package cn.edu.buaa.act.service4all.qualifycontrol.parser;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.database.Database;


public class parseQualification extends parser 
{
	private int function;
	
	public parseQualification(int function)
	{
		super();
		this.function=function;
	}

	@Override
	public Document parse(Document doc) 
	{
		Element username,serviceid,root;
		String statement = null;
		Document response;
		
		root=doc.getRootElement();
		username=root.getChild(Constants.userName);
		serviceid=root.getChild(Constants.serviceId);
		switch(function)
		{
		case Constants.qualification_deploy:
			statement=Database.getInstance().deployQualification(username.getText());
			break;
		case Constants.qualification_undeploy:
			statement=Database.getInstance().undeployQualification(username.getText(), serviceid.getText());
			break;
		case Constants.qualification_invoke:	
			statement=Database.getInstance().invokeQualification(username.getText(), serviceid.getText());
		}
		
		if(statement==null)
		{
			response=createResponse(Constants.permit,statement);
		}
		else
		{
			response=createResponse(Constants.deny,statement);
		}		
		return response;
	}
	
	private Document createResponse(String qualification,String statement)
	{
		Element root = null,qual,state;
		Document response;
		
		response=new Document();
		switch(function)
		{
		case Constants.qualification_deploy:
			root=new Element(Constants.responseDeployQualification);
			break;
		case Constants.qualification_undeploy:
			root=new Element(Constants.responseUndeployQualification);
			break;
		case Constants.qualification_invoke:	
			root=new Element(Constants.responseInvokeQualification);
		}
		response.setRootElement(root);
		
		qual=new Element(Constants.qualification);
		qual.setText(qualification);
		root.addContent(qual);
		
		state=new Element(Constants.statement);
		state.setText(statement);
		root.addContent(state);	
		
		return response;
	}
}