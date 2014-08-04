package cn.edu.buaa.act.service4all.qualifycontrol.Manager;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseLogInOut;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseRegister;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parser;



/**
 * 检测用户的注册、登入、登出权限
 *
 */
public class userManager extends parser
{
	public userManager()
	{
		super();		
	}
	@Override
	public Document parse(Document doc) 
	{
		Element root;
		String name;
		Document response = null;
		
		root=doc.getRootElement();
		name=root.getName();
		
		if(name.equals(Constants.register))
		{
			parseRegister register=new parseRegister();
			response=register.parse(doc);
		}
		if(name.equals(Constants.login))
		{
			parseLogInOut login=new parseLogInOut(Constants.user_Login);
			response=login.parse(doc);
		}
		if(name.equals(Constants.logout))
		{
			parseLogInOut logout=new parseLogInOut(Constants.user_Logout);
			response=logout.parse(doc);
		}
		
		return response;
	}
}