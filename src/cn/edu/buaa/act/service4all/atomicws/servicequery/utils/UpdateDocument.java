/**
* Service4All: A Service-oriented Cloud Platform for All about Software Development
* Copyright (C) Institute of Advanced Computing Technology, Beihang University
* Contact: service4all@act.buaa.edu.cn
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3.0 of the License, or any later version. 
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details. 
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*/
package cn.edu.buaa.act.service4all.atomicws.servicequery.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class UpdateDocument {
	private static UpdateDocument  instance = null;
	private Document doc = null;

	public Document getDoc() {
		return doc;
	}

	private int undeployNum = 0;
	private int deployNum =0;
	private static Log logger = LogFactory.getLog(UpdateDocument.class);
	
	private UpdateDocument(){
		init();
	}
	private void init() {
		try {
			undeployNum = 0;
			deployNum = 0;
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = doc.createElement("queryWebServicesResponse");
			root.setAttribute("type", "increment");
			
			Element deploy = doc.createElement("deploy");
			Element servicesDeploy = doc.createElement("services");
			servicesDeploy.setAttribute("num", "0");
			deploy.appendChild(servicesDeploy);
			
			Element undeploy = doc.createElement("undeploy");
			Element servicesUndeploy = doc.createElement("services");
			servicesUndeploy.setAttribute("num", "0");
			undeploy.appendChild(servicesUndeploy);
			
			root.appendChild(deploy);
			root.appendChild(undeploy);
			
			doc.appendChild(root);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	public static synchronized UpdateDocument getInstance(){
		if(instance == null){
			instance = new UpdateDocument();
		}
		return instance;
	}
	
	public Document addService(String type,Element service){
		int len=service.getElementsByTagName("repetition").getLength();
		if("deploy".equals(type)){
			Element services=(Element) ((Element)doc.getElementsByTagName("deploy").item(0)).getElementsByTagName("services").item(0);
			Element inputService= doc.createElement("service");
			for(int i=0;i<len;i++)
			{
				Element temp=(Element)service.getElementsByTagName("repetition").item(i);
				Element repetition=doc.createElement("repetition");
				repetition.setAttribute("id", getId(temp.getAttribute("invokeUrl")));
				repetition.setAttribute("invokeUrl", temp.getAttribute("invokeUrl"));
				repetition.setAttribute("cpu", temp.getAttribute("cpu"));
				repetition.setAttribute("memory", temp.getAttribute("memory"));
				repetition.setAttribute("throughput", temp.getAttribute("throughput"));
				inputService.appendChild(repetition);
			}
			inputService.setAttribute("id", service.getAttribute("id"));
			inputService.setAttribute("repetitionNum", service.getAttribute("repetitionNum"));
			inputService.setAttribute("serviceName", service.getAttribute("serviceName"));
			inputService.setAttribute("timecost", service.getAttribute("timecost"));
			services.appendChild(inputService);
			services.setAttribute("num", String.valueOf(++deployNum ));
		}else if("undeploy".equals(type)){
			Element services=(Element) ((Element)doc.getElementsByTagName("undeploy").item(0)).getElementsByTagName("services").item(0);
			Element inputService= doc.createElement("service");
			for(int i=0;i<len;i++)
			{
				Element temp=(Element)service.getElementsByTagName("repetition").item(i);
				Element repetition=doc.createElement("repetition");
				repetition.setAttribute("id", getId(temp.getAttribute("invokeUrl")));
				inputService.appendChild(repetition);
			}
			inputService.setAttribute("id", service.getAttribute("id"));
			inputService.setAttribute("repetitionNum", service.getAttribute("repetitionNum"));
			inputService.setAttribute("serviceName", service.getAttribute("serviceName"));
			inputService.setAttribute("timecost", service.getAttribute("timecost"));
			services.appendChild(inputService);
			services.setAttribute("num", String.valueOf(++undeployNum));

		}else{
			logger.warn("Type is error");
		}
		

		
		return instance.doc;
	}
	public void clear(){
		init();
	}
	
	private String getId(String attribute) {
		int i=attribute.lastIndexOf(":");
		
		return attribute.substring(0,i+5);
	}


}
