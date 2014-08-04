package cn.edu.buaa.act.service4all.qualifycontrol.Manager;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseDeployUndeployService;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseLogInOut;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseRegister;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parser;



/**
 * 用户关于某个服务的部署、反部署权限
 *
 */
public class serviceManager extends parser
{
	public serviceManager()
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
		
		if(name.equals(Constants.deployService))
		{
			parseDeployUndeployService service=new parseDeployUndeployService(Constants.service_Deploy);
			response=service.parse(doc);
		}
		if(name.equals(Constants.undeployService))
		{
			parseDeployUndeployService service=new parseDeployUndeployService(Constants.service_Undeploy);
			response=service.parse(doc);
		}
		return response;
	}
}
