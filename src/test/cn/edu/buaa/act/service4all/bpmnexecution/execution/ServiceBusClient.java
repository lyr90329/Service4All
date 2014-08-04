package test.cn.edu.buaa.act.service4all.bpmnexecution.execution;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.rpc.client.RPCServiceClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.axis2.util.StreamWrapper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.edu.buaa.act.service4all.bpmnexecution.common.InfoUtil;
import cn.edu.buaa.act.service4all.bpmnexecution.common.Parameter;
import cn.edu.buaa.act.service4all.bpmnexecution.common.QoSValue;

public class ServiceBusClient {
	/*
	 * target service address
	 */
	private EndpointReference targetEPR;
	/*
	 * the RPCServiceClient
	 */
	private RPCServiceClient serviceClient = null;
	/*
	 * the bus address
	 */
	private String busAddress = null;
	/*
	 * log the information
	 */
	static Logger logger = Logger.getLogger(ServiceBusClient.class.getName());

	/**
	 * init ServiceBusClient
	 */
	public ServiceBusClient(String busAddress) {
		PropertyConfigurator.configure("log4j.properties");
		this.busAddress = busAddress;
		try {
			serviceClient = new RPCServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		targetEPR = new EndpointReference(busAddress);
	}

	/**
	 * invoke the serviceBusService
	 * 
	 * @param compositeServiceName
	 * @param parmList
	 *            the user's input such as execution parameter
	 * @return String
	 */
	public String invokeService(String compositeServiceName, List parmList,
			String IP, String serviceID, String serviceName) {
		Options options = serviceClient.getOptions();
		options.setTo(targetEPR);
		options.setAction("urn:receiveData");
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender.setOptions(options);
		// 构造soap报文
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement data = fac.createOMElement("execute", omNs);
		data.addAttribute("serviceID", serviceID, null);
		data.addAttribute("serviceName", serviceName, null);
		OMElement executeInfo = buildExecuteInforEnvelope(compositeServiceName,
				parmList, IP);
		data.addChild(executeInfo);
		OMElement response = null;
		try {
			data.serialize(System.out);
			response = sender.sendReceive(data);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(response);
		String jobId = response.getText();
		return jobId;
	}

	public String invokeService(String compositeServiceName, List parmList,
			QoSValue qos, String IP) {
		Options options = serviceClient.getOptions();
		options.setTo(targetEPR);
		options.setAction("urn:receiveDatawithQoS");
		// options.setAction("urn:receiveData");
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender.setOptions(options);
		// 构造soap报文
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement data = fac.createOMElement("execute", omNs);
		OMElement executeInfo = buildExecuteInforEnvelope(compositeServiceName,
				parmList, qos, IP);
		data.addChild(executeInfo);
		OMElement response = null;
		logger.info(data);
		try {
			response = sender.sendReceive(data);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(response);
		String jobId = response.getText();
		return jobId;
	}

	public List getBusInfo() {
		List list = new ArrayList();
		/*
		 * list中第一个存储jobId。第二个为databaseInfo,第三个为moniteInfo 第四个为busAddress
		 */
		Options options = serviceClient.getOptions();
		EndpointReference busTarget = new EndpointReference(busAddress);
		options.setTo(busTarget);
		options.setAction("urn:getBusInfo");
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender.setOptions(options);
		// 构造soap报文
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement data = fac.createOMElement("getBusInfo", omNs);
		OMElement response = null;
		logger.info(data);
		try {
			response = sender.sendReceive(data);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator it = response.getChildElements();
		while (it.hasNext()) {
			OMElement om = (OMElement) it.next();
			String result = om.getText();
			logger.info(result);
			list.add(result);
		}
		return list;
	}

	/**
	 * insert the execution information into the soap message
	 * 
	 * @param compositeServiceName
	 * @param parmList
	 *            the user's input such as execution parameter
	 * @return OMElement
	 */
	private static OMElement buildExecuteInforEnvelope(
			String compositeServiceName, List<Parameter> parmData, String IP) {

		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement executeInfo = omFactory.createOMElement("execute", omNs);
		OMElement IPElement = omFactory.createOMElement("IP", omNs);
		IPElement.setText(IP);
		InfoUtil infoArray = new InfoUtil();
		infoArray.setCompositeServiceName(compositeServiceName);

		javax.xml.stream.XMLStreamReader reader = BeanUtil
				.getPullParser(infoArray);
		StreamWrapper parser = new StreamWrapper(reader);
		StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory.createStAXOMBuilder(
				OMAbstractFactory.getOMFactory(), parser);
		OMElement invokeInfo = stAXOMBuilder.getDocumentElement();
		executeInfo.addChild(invokeInfo);
		executeInfo.addChild(IPElement);
		OMElement parameters = buildParamDataEnvelope(parmData);
		if (parameters != null) {

			executeInfo.addChild(parameters);
		}
		logger.info(parameters);
		return executeInfo;
	}

	private static OMElement buildExecuteInforEnvelope(
			String compositeServiceName, List<Parameter> parmData,
			QoSValue qos, String IP) {

		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement executeInfo = omFactory.createOMElement("execute", omNs);
		OMElement IPElement = omFactory.createOMElement("IP", omNs);
		IPElement.setText(IP);
		InfoUtil infoArray = new InfoUtil();
		infoArray.setCompositeServiceName(compositeServiceName);

		javax.xml.stream.XMLStreamReader reader = BeanUtil
				.getPullParser(infoArray);
		StreamWrapper parser = new StreamWrapper(reader);
		StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory.createStAXOMBuilder(
				OMAbstractFactory.getOMFactory(), parser);
		OMElement invokeInfo = stAXOMBuilder.getDocumentElement();
		executeInfo.addChild(invokeInfo);
		executeInfo.addChild(IPElement);
		OMElement parameters = buildParamDataEnvelope(parmData);
		if (parameters != null) {

			executeInfo.addChild(parameters);
		}
		logger.info(parameters);
		javax.xml.stream.XMLStreamReader reader2 = BeanUtil.getPullParser(qos);
		StreamWrapper parser2 = new StreamWrapper(reader2);
		StAXOMBuilder stAXOMBuilder2 = OMXMLBuilderFactory.createStAXOMBuilder(
				OMAbstractFactory.getOMFactory(), parser2);
		OMElement invokeInfo2 = stAXOMBuilder2.getDocumentElement();
		executeInfo.addChild(invokeInfo2);
		logger.info(invokeInfo2);
		return executeInfo;
	}

	/**
	 * insert the parameter list into the soap message
	 * 
	 * @param parmList
	 *            the user's input such as execution parameter
	 * @return OMElement
	 */
	private static OMElement buildParamDataEnvelope(List<Parameter> paramData) {

		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement parameters = omFactory.createOMElement("parameters", omNs);

		if (paramData == null) {
			return null;
		}

		for (Iterator<Parameter> it = paramData.iterator(); it.hasNext();) {

			Parameter p = it.next();
			javax.xml.stream.XMLStreamReader reader = BeanUtil.getPullParser(p);
			StreamWrapper parser = new StreamWrapper(reader);
			StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory
					.createStAXOMBuilder(OMAbstractFactory.getOMFactory(),
							parser);
			OMElement parameter = stAXOMBuilder.getDocumentElement();
			parameters.addChild(parameter);

		}

		return parameters;
	}

	public String getRuntime(String jobId, String address) {
		Options options = serviceClient.getOptions();
		EndpointReference busTarget = new EndpointReference(address);
		options.setTo(busTarget);
		options.setAction("urn:getRuntime");
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender.setOptions(options);
		// 构造soap报文
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://executeresultstoring.bus.sdp.act.org.cn", "getRuntime");
		OMElement data = fac.createOMElement("getRuntime", omNs);
		OMElement jobIdOM = fac.createOMElement("jobId", omNs);
		jobIdOM.addChild(fac.createOMText(jobId));
		data.addChild(jobIdOM);// 添加jobIdOM到soap消息中
		OMElement response = null;
		logger.info(data);
		try {
			response = sender.sendReceive(data);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String runtime = response.getFirstElement().getFirstElement().getText();
		return runtime;
	}

	/**
	 * 配合090728演示需要，添加发送qos客户端
	 */
	public boolean sendQoS(String jobId, String serviceName, String time,
			String cost) {
		Options options = serviceClient.getOptions();
		options.setTimeOutInMilliSeconds(60000);
		options.setTo(targetEPR);
		options.setAction("urn:receiveQoS");
		ServiceClient sender = null;
		try {
			sender = new ServiceClient();
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sender.setOptions(options);
		// 构造soap报文
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement data = fac.createOMElement("execute", omNs);
		OMElement executeInfo = buildQoS(jobId, serviceName, time, cost);
		data.addChild(executeInfo);
		OMElement response = null;
		try {
			response = sender.sendReceive(data);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(response);
		boolean flag = Boolean.parseBoolean(response.getText());
		return flag;
	}

	/**
	 * 配合090728演示，添加QoS属性
	 */
	public OMElement buildQoS(String jobId, String serviceName, String time,
			String cost) {
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://servicebus.sdp.act.org.cn", "execute");
		OMElement executeInfo = omFactory.createOMElement("execute", omNs);
		InfoUtil infoArray = new InfoUtil();
		infoArray.setCompositeServiceName(serviceName);
		infoArray.setJobId(Long.parseLong(jobId));

		javax.xml.stream.XMLStreamReader reader = BeanUtil
				.getPullParser(infoArray);
		StreamWrapper parser = new StreamWrapper(reader);
		StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory.createStAXOMBuilder(
				OMAbstractFactory.getOMFactory(), parser);
		OMElement invokeInfo = stAXOMBuilder.getDocumentElement();
		executeInfo.addChild(invokeInfo);
		// 添加QoS属性
		QoSValue qos = new QoSValue();
		qos.setTime(time);
		qos.setCost(cost);
		javax.xml.stream.XMLStreamReader reader2 = BeanUtil.getPullParser(qos);
		StreamWrapper parser2 = new StreamWrapper(reader2);
		StAXOMBuilder stAXOMBuilder2 = OMXMLBuilderFactory.createStAXOMBuilder(
				OMAbstractFactory.getOMFactory(), parser2);
		OMElement invokeInfo2 = stAXOMBuilder2.getDocumentElement();
		executeInfo.addChild(invokeInfo2);
		return executeInfo;
	}

	public boolean sendFlexData(String busAddress, String jobId, String nodeId) {
		EndpointReference targetEPR = new EndpointReference(busAddress);
		Options options = serviceClient.getOptions();
		options.setTo(targetEPR);
		options.setAction("urn:receiveEngineData");
		String nameSpaces = "http://servicebus.sdp.act.org.cn";
		QName opName = new QName(nameSpaces, "receiveEngineData");
		String[] in = { jobId, nodeId };
		OMElement response = null;
		try {
			response = serviceClient.invokeBlocking(opName, in);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = response.getFirstElement().getText();
		logger.info(result);
		boolean flag = Boolean.parseBoolean(result);
		return flag;
	}

	/**
	 * test the client
	 */
	public static void main(String[] args) {
		
		String busAddress = "http://192.168.3.204:8192/appengine/BPMNExecute/";
		//String busAddress = "http://localhost:8080/axis2/services/BusService";
		ServiceBusClient client = new ServiceBusClient(busAddress);
		LinkedList<Parameter> parmData = new LinkedList<Parameter>();

		Parameter name = new Parameter();
		Parameter password = new Parameter();
		Parameter bookIsbn = new Parameter();
		Parameter VIP = new Parameter("orderId", "String", "sp");

		name.setParamName("userName");
		name.setParamType("String");
		name.setParamValue("jiyipeng");

		password.setParamName("password");
		password.setParamType("String");
		password.setParamValue("jiyipeng");

		bookIsbn.setParamName("goodName");
		bookIsbn.setParamType("String");
		bookIsbn.setParamValue("thinking in java");

		parmData.add(name);
		parmData.add(password);
		parmData.add(bookIsbn);
		parmData.add(VIP);
		
		//String compositeServiceName = "testAllnew2";
		String compositeServiceName = "eStore2";
		String IP = "http://192.168.104.100:8192/appengine/BPMNMonitorInfoFeedbackReceiver/";
		String jobId = client.invokeService(compositeServiceName, 
											parmData, 
											IP, 
											"BPMN_2", 
											"eStore2");
		
		System.out.println(jobId);
	}
}
