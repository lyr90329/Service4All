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
package cn.edu.buaa.act.service4all.core.samanager.query;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.core.component.bri.Receiver;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;
import cn.edu.buaa.act.service4all.core.samanager.beans.App;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;

public class AppQueryReceiver extends Receiver {

	private Log logger = LogFactory.getLog(AppQueryReceiver.class);
		
	
	@Override
	public Document createResponseDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
	
		@SuppressWarnings("unchecked")
		List<App> queryResults = (List<App>)context.getData(AppQueryBusinessUnit.QUERY_RESULTS);
		String type = (String)context.getData(AppQueryBusinessUnit.QUERY_TYPE);
		
		logger.info("Create the response message for the : " + type);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resp = builder.newDocument();
			Element root = resp.createElement("queryAppResponse");
			resp.appendChild(root);
			root.setAttribute("type", type);
			
			Element appsElement = resp.createElement("apps");
			appsElement.setAttribute("num", String.valueOf(queryResults.size()));
			root.appendChild(appsElement);
			
			for(App app : queryResults){
				
				Element appElement = resp.createElement("app");
				appElement.setAttribute("id", app.getId());
				appElement.setAttribute("appName", app.getName());
				appsElement.appendChild(appElement);
				
				//add the app repetitions
				List<AppReplica> reps = app.getBackups();
				if(reps == null){
					
					// there is no app repetition for the app
					appElement.setAttribute("repetitionNum", "0");
					
				}else{
					
					appElement.setAttribute("repetitionNum", String.valueOf(reps.size()));
					for(AppReplica r : reps){
						Element rElement = resp.createElement("repetition");
						rElement.setAttribute("applianceId", r.getContainerId());
						rElement.setAttribute("invokeUrl", r.getInvocationUrl());
						appElement.appendChild(rElement);
						
					}
					
				}
				
			}
			
			return resp;
		} catch (ParserConfigurationException e) {
			logger.warn("Can't create the response message : " + e.getMessage());
			throw new MessageExchangeInvocationException("Can't create the response message : " + e.getMessage());
		}
		
	}

	@Override
	public void handlRequest(Document request, ExchangeContext context)
			throws MessageExchangeInvocationException {
		
		Element root = request.getDocumentElement();
		String queryType = root.getAttribute("type");
		
		if(queryType == null){
			logger.warn("Miss the type attribute for the app query request!");
			throw new MessageExchangeInvocationException("Miss the type attribute for the app query request!");
		}
		
		context.storeData(AppQueryBusinessUnit.QUERY_TYPE, queryType);
		
		this.unit.dispatch(context);
		
	}

}
