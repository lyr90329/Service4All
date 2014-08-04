package cn.edu.buaa.act.service4all.qualifycontrol.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.database.Database;


public class parseRegister extends parser
{
	private final Log logger = LogFactory.getLog(parseRegister.class);
	public parseRegister()
	{
		super();
	}

	public Document parse(Document doc) 
	{
		Element root,username,password;
		Database database=Database.getInstance();
		String statement;
		Document response;
		
		root=doc.getRootElement();
		username=root.getChild(Constants.userName);
		password=root.getChild(Constants.password);
		statement=database.register(username.getText(), password.getText());
		if(statement==null)
		{
			response=createResponse(Constants.boolean_True,statement);
		}
		else
		{
			logger.error(statement);
			response=createResponse(Constants.boolean_false,statement);
		}
		
		return response;
	}
	
	private Document createResponse(String issuccessful,String statement)
	{
		Element root,issuccess,state;
		Document response;
		
		response=new Document();
		root=new Element(Constants.responseRegister);
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