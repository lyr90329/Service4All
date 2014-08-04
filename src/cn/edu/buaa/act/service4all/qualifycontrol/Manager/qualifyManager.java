package cn.edu.buaa.act.service4all.qualifycontrol.Manager;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parseQualification;
import cn.edu.buaa.act.service4all.qualifycontrol.parser.parser;



/**
 * 用户是否有部署、反部署权限
 *
 */
public class qualifyManager extends parser
{
	public qualifyManager()
	{
		super();
	}

	@Override
	public Document parse(Document doc)
	{
		Element root;
		String name;
		Document response = null;
		parseQualification qualification = null;
		
		root=doc.getRootElement();
		name=root.getName();
		
		if(name.equals(Constants.deployQualification))
		{
			qualification=new parseQualification(Constants.qualification_deploy);
		}
		
		if(name.equals(Constants.undeployQualification))
		{
			qualification=new parseQualification(Constants.qualification_undeploy);
		}
		
		if(name.equals(Constants.invokeQualification))
		{
			
			qualification=new parseQualification(Constants.qualification_invoke);
		}	
		response=qualification.parse(doc);
		return response;
	}
}
