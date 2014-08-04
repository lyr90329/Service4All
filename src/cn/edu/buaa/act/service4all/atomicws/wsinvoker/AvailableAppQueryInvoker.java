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
package cn.edu.buaa.act.service4all.atomicws.wsinvoker;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.loadbalance.Performance;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.loadbalance.PerformanceSortor;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.DocsBuilder;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;
import cn.edu.buaa.act.service4all.core.component.bri.Invoker;
import cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext;
import cn.edu.buaa.act.service4all.core.component.transaction.exception.MessageExchangeInvocationException;

public class AvailableAppQueryInvoker extends Invoker{

//	private static final Log logger = LogFactory.getLog(AvailableAppQueryInvoker.class);
	@Override
	public Document createRequestDocument(ExchangeContext context)
			throws MessageExchangeInvocationException {
		
		String serviceID = (String) context.getData(WSInvokeConstants.SERVICEID);
		return DocsBuilder.buildeAvailableAppQueryDoc(serviceID);
	}

	@Override
	public void handleResponse(Document doc, ExchangeContext context)
		throws MessageExchangeInvocationException {

		Element root = doc.getDocumentElement();
		int num = Integer.parseInt(doc.getElementsByTagName("num").item(0).getTextContent());
		boolean hasAvailableService = false ;
		if(num > 0){
			hasAvailableService = true ;
			List<String> wsdls = new ArrayList<String>();

			Performance [] performances = new Performance[num];
			for (int i = 0; i < num; i++) {
				Element serviceElement = (Element) root.getElementsByTagName(WSInvokeConstants.SERVICE).item(i);

				wsdls.add(serviceElement.getAttribute(WSInvokeConstants.URL));
				double cpu = Double.parseDouble(serviceElement.getAttribute(WSInvokeConstants.CPU));
				double memeory = Double.parseDouble(serviceElement.getAttribute(WSInvokeConstants.MEMORY));
				double throughput = Double.parseDouble(serviceElement.getAttribute(WSInvokeConstants.THROUGHPUT));
				int serviceCount = Integer.parseInt(serviceElement.getAttribute(WSInvokeConstants.DEPLOYEDSERVICEAMOUNT));
				Performance p = new Performance(cpu, memeory, throughput, serviceCount);
				performances[i] = p;
			}

			
			double [] weight = {0.25,0.25,0.5,0};
			PerformanceSortor sortor = new PerformanceSortor(weight, performances);
			int [] rank = sortor.sort();
			
			context.storeData(WSInvokeConstants.URLS, wsdls);
			context.storeData(WSInvokeConstants.RANK, rank);

		}
			

		context.storeData(WSInvokeConstants.HASAVAILBALESERVICE, hasAvailableService);
		this.unit.onReceiveResponse(context);
	}
	
}
