/*
*
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
*
*/
package cn.edu.buaa.act.service4all.core.localsamanager.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Config {
	private Map<String, String> properties;
	private static Config config;
	private static Logger logger = Logger.getLogger( Config.class );
	
	private Config(){
		properties = new HashMap<String, String>();
		initConfig();
	}
	
	
	public static Config getInstance(){
		if(config == null){
			config = new Config();
		}
		return config;
	}
	
	public String getProperty(String key){
		System.out.println( key+" "+properties.get(key));
		return properties.get(key);
	}
	
	private void initConfig(){
		Document doc = null;
		SAXBuilder reader = new SAXBuilder();
		try {
			doc = reader.build(new File("conf/config.xml"));
			System.out.println(new File("conf/config.xml").getAbsolutePath());
			Element root = doc.getRootElement();
			parseElement(root);
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void parseElement(Element element){
		@SuppressWarnings("unchecked")
		List<Element> lists = element.getChildren();
		for(Element el : lists){
			if(el.getChildren().size() > 0){
				parseElement(el);
			}else{
				properties.put(el.getName(), el.getValue());
				logger.info(el.getName()+" "+el.getValue());
			}
		}
		
	}
	
}
