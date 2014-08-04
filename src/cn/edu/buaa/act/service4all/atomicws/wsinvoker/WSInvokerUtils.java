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

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.atomicws.wsinvoker.thread.Store2DatabaseThread;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.ExecutionResult;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.TransformerUtils;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WSInvokeConstants;
import cn.edu.buaa.act.service4all.atomicws.wsinvoker.utils.WebServiceCallingLogClient;

public class WSInvokerUtils {
	private static final Log logger = LogFactory.getLog(WSInvokerUtils.class);
	private static final boolean isLog = WSInvokeConstants
			.getWSInvokeConstants().isLog();

	public static ExecutionResult invokeWithSoapRequestAXIOM(String wsdlUrl,
			String operation, OMElement soapBodyOmElement) throws AxisFault {

		ExecutionResult executionResult = new ExecutionResult(false, null);
		if (isLog) {

			QName userIDQname = new QName(WSInvokeConstants.USERID);
			QName userTypeQName = new QName(WSInvokeConstants.USERTYPE);
			String userID = soapBodyOmElement.getAttributeValue(userIDQname);
			String userType = soapBodyOmElement
					.getAttributeValue(userTypeQName);

			String ip = getIp(wsdlUrl);

			logger.info("For http://" + ip
					+ ":8080/axis2/services/localSAManagerService/setInvokeUser");
			WebServiceCallingLogClient client = new WebServiceCallingLogClient(
					ip);
			OMElement Msg = client.msgEncp(userID, userType, wsdlUrl);
			try {
				WebServiceCallingLogClient.sendRequest(Msg);
			} catch (AxisFault e1) {
				e1.printStackTrace();
			}
		}

		Options options = new Options();
		options.setAction("urn:" + operation);
		EndpointReference epr = new EndpointReference(wsdlUrl);
		options.setTo(epr);
		OMElement soapBodyContent = soapBodyOmElement.getFirstElement();

		ServiceClient sender = new ServiceClient();

		sender.setOptions(options);
		OMElement responseOmElement = sender.sendReceive(soapBodyContent);

		executionResult.setSuccessful(true);
		executionResult.setResponseOmElement(responseOmElement);
		return executionResult;
	}

	private static String getIp(String url) {
		int begin = url.indexOf("//");
		int end = url.lastIndexOf(':');
		return url.substring(begin + 2, end);
	}

	@SuppressWarnings("unchecked")
	public static void invokeWSByID(cn.edu.buaa.act.service4all.core.component.messaging.ExchangeContext context) {
		String serviceID = (String) context
				.getData(WSInvokeConstants.SERVICEID);
		String operation = (String) context
				.getData(WSInvokeConstants.OPERATION);
		OMElement soapBodyOmElement = (OMElement) context
				.getData(WSInvokeConstants.REQUESTSOAP);
		long startTime = (Long) context.getData(WSInvokeConstants.STARTTIME);
		boolean hasFailed = false;

		String wsdlUrl = null;
		List<String> wsdls = (List<String>) context
				.getData(WSInvokeConstants.URLS);
		int[] rank = (int[]) context.getData(WSInvokeConstants.RANK);
		int i;
		int j;
		ExecutionResult executionResult = new ExecutionResult();
		String exception = null;
		for (i = 0; i < wsdls.size(); i++) {
			for (j = 0; j < rank.length; j++) {
				if (rank[j] == i) {
					break;
				}
			}
			wsdlUrl = wsdls.get(j);

			// if (("true").equals((String) context.getData("isEngine"))) {
			// logger.info("invoked by BPMNEngine");
			// OMElement responseOmElement;
			// try {
			// responseOmElement = invokeWSByWSDLParser(wsdlUrl,
			// operation, getParas(soapBodyOmElement));
			// boolean isSuccessful = false;
			// if (("return").equals(responseOmElement.getFirstElement()
			// .getLocalName()))
			// isSuccessful = true;
			//
			// executionResult = new ExecutionResult(isSuccessful,
			// responseOmElement);
			//
			// logger.error("The" + i
			// + "th replica is invoked successfully£¡" + wsdlUrl);
			// break;
			// } catch (Exception e) {
			// hasFailed = true;
			// e.printStackTrace();
			// exception = e.getMessage();
			// logger.error("Fail to invoke the " + i
			// + "th replica. Try again! " + wsdlUrl);
			// continue;
			// }
			//
			// } else {

			try {
				executionResult = invokeWithSoapRequestAXIOM(wsdlUrl,
						operation, soapBodyOmElement);
				logger.error("The" + i + "th replica is invoked successfully£¡"
						+ wsdlUrl);
				break;
			} catch (AxisFault e) {
				hasFailed = true;
				exception = e.getMessage();
				e.printStackTrace();
				logger.error("Fail to invoke the " + i
						+ "th replica. Try again! " + wsdlUrl);
				continue;
			}
		}

		// }
		if (!executionResult.isSuccessful()) {
			OMFactory factory = OMAbstractFactory.getOMFactory();
			OMElement responseOmElement = factory.createOMElement("exception",
					null);
			responseOmElement.setText(exception);
			executionResult.setResponseOmElement(responseOmElement);
		}
		context.storeData(WSInvokeConstants.RESPONSESOAP,
				executionResult.getResponseOmElement());
		if (hasFailed) {
			WSInvokerReceiver.wsdlMap.remove(serviceID);
		}

		Thread store2DBThread = new Thread(new Store2DatabaseThread(serviceID,
				wsdlUrl, startTime, executionResult.isSuccessful()));
		store2DBThread.start();

	}

	@SuppressWarnings("unchecked")
	public static OMElement getSoapBody(Document inDoc) {

		OMElement requestSoap = TransformerUtils.getFirstOMElementByTagName(
				inDoc, WSInvokeConstants.REQUESTSOAP);
		if (requestSoap == null)
			System.out
					.println("requestSoap is null_____________________________________________\n\n");

		OMElement env = requestSoap.getFirstElement();

		Iterator<OMElement> childIterator = env.getChildElements();

		OMElement child = null;
		while (childIterator.hasNext()) {
			child = childIterator.next();
			if (child.getLocalName().equals("Body")) {
				break;
			}
		}
		return child;

	}

	// public static OMElement invokeWSByWSDLParser(String wsaddress,
	// String operation, Parameter[] parameters) throws AxisFault,
	// WSDLException {
	// logger.info("Start parsing wsdl and invoking web service");
	// RPCServiceClient serviceClient = new RPCServiceClient();
	// Options options = serviceClient.getOptions();
	// EndpointReference targetEPR = new EndpointReference(wsaddress);
	//
	// String tarNamespace = "";
	//
	// WSDLFactory factory = WSDLFactory.newInstance();
	// WSDLReader reader = factory.newWSDLReader();
	// reader.setFeature("javax.wsdl.verbose", true);
	// reader.setFeature("javax.wsdl.importDocuments", true);
	// int k = wsaddress.lastIndexOf("/");
	// Definition def = reader.readWSDL(wsaddress.substring(0, k) + "?wsdl");
	// tarNamespace = def.getTargetNamespace();
	//
	// OMFactory fac = OMAbstractFactory.getOMFactory();
	// OMNamespace omNs = fac.createOMNamespace(tarNamespace, "abc");
	// OMElement statsElement = fac.createOMElement(operation, omNs);
	// for (int i = 0; i < parameters.length; i++) {
	// OMElement nameEle = fac.createOMElement(
	// parameters[i].getParamName(), omNs);
	// nameEle.setText(parameters[i].getParamValue());
	// statsElement.addChild(nameEle);
	// }
	//
	// options.setTo(targetEPR);
	//
	// OMElement out = serviceClient.sendReceive(statsElement);
	//
	// return out;
	//
	// }

	// @SuppressWarnings("rawtypes")
	// private static Parameter[] getParas(OMElement soapBody) {
	//
	// OMElement paras = soapBody.getFirstElement();
	//
	// System.out.println(paras);
	//
	// OMElement para;
	// int sum = 0;
	// Iterator ite = paras.getChildElements();
	// while (ite.hasNext()) {
	// sum++;
	// ite.next();
	// }
	//
	// Parameter[] paraList = new Parameter[sum];
	// ite = paras.getChildElements();
	// for (int i = 0; i < sum; i++) {
	// paraList[i] = new Parameter();
	// para = (OMElement) ite.next();
	// Iterator<OMElement> temp;
	// OMElement ele;
	// temp = para.getChildElements();
	// ele = (OMElement) temp.next();
	// paraList[i].setParamName(ele.getText());
	// ele = (OMElement) temp.next();
	// paraList[i].setParamType(ele.getText());
	// ele = (OMElement) temp.next();
	// paraList[i].setParamValue(ele.getText());
	// System.out.print(para);
	// }
	// return paraList;
	// }

}
