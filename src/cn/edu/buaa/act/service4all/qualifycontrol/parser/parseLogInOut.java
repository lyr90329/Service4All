package cn.edu.buaa.act.service4all.qualifycontrol.parser;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.database.Database;



public class parseLogInOut extends parser
{
	private int function;
	
	public parseLogInOut(int function)
	{
		super();
		this.function=function;
	}

	@Override
	public Document parse(Document doc) 
	{
		Element root,username,password;
		Database database=Database.getInstance();
		String statement = null;
		Document response;
		
		root=doc.getRootElement();
		username=root.getChild(Constants.userName);
		switch(function)
		{
		case Constants.user_Login:
			password=root.getChild(Constants.password);
			statement=database.login(username.getText(), password.getText());
			break;
		case Constants.user_Logout:
			statement=database.logout(username.getText());			
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
	
	private Document createResponse(String issuccessful,String statement)
	{
		Element root = null,issuccess,state;
		Document response;
		
		response=new Document();
		switch(function)
		{
		case Constants.user_Login:
			root=new Element(Constants.responseLogin);
			break;
		case Constants.user_Logout:
			root=new Element(Constants.responseLogout);
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