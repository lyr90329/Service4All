package cn.edu.buaa.act.service4all.qualifycontrol.parser;

import java.util.List;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.database.Database;


public class parseDeployUndeployService extends parser
{
	private int function;
	
	public parseDeployUndeployService(int function)
	{
		super();
		this.function=function;
	}
	
	@Override
	public Document parse(Document doc)
	{
		Element root,username;
		Vector<String> servicelist;
		Attribute servicetype;
		Document response;
		String statement = null;
		int type=Integer.MAX_VALUE;
		
		root=doc.getRootElement();
		servicetype=root.getAttribute(Constants.type);
		//--------------------------------tangyu
		if(servicetype.getValue().equals(Constants.bpmn))
		{
//			type=1;
			type=Constants.serviceType_BPMN;
		}
		else if(servicetype.getValue().equals(Constants.webservice)){
//			type=0;
			type=Constants.serviceType_webservice;
		}		
		else if(servicetype.getValue().equals(Constants.WEBAPP)){
			type=Constants.SERVICE_TYPE_WEBAPP;
		}
		//--------------------------------tangyu
		username=root.getChild(Constants.userName);
		servicelist=parseServiceList(root.getChild(Constants.serviceList));
		switch(function)
		{
		case Constants.service_Deploy:
			statement=Database.getInstance().deployService(username.getText(), servicelist,type);
			break;
		case Constants.service_Undeploy:
			statement=Database.getInstance().undeployService(username.getText(),servicelist,type);
		}
		
		if(statement==null)
		{
			response=createResponse(Constants.boolean_True,statement);
		}
		else
		{
			response=createResponse(Constants.boolean_false,statement);
		}		
		return response;
	}
	
	private Vector<String> parseServiceList(Element servicelist)
	{
		List<Element> ids;
		Vector<String> results=new Vector<String>();
		
		ids=servicelist.getChildren(Constants.serviceId);
		for(Element id:ids)
		{
			results.add(id.getText());
		}
		
		return results;
	}
	
	private Document createResponse(String issuccessful,String statement)
	{
		Element root = null,issuccess,state;
		Document response;
		
		response=new Document();
		switch(function)
		{
		case Constants.service_Deploy:
			root=new Element(Constants.responseDeployService);
			break;
		case Constants.service_Undeploy:
			root=new Element(Constants.responseUndeployService);
		}
		response.setRootElement(root);
		
		issuccess=new Element(Constants.isSuccessful);
		issuccess.setText(issuccessful);
		root.addContent(issuccess);
		
		state=new Element(Constants.statement);
		state.setText(statement);
		root.addContent(state);	
		
		return response;
	}
}