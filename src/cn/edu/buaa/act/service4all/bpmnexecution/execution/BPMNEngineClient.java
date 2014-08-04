package cn.edu.buaa.act.service4all.bpmnexecution.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import cn.edu.buaa.act.service4all.bpmnexecution.BPMNEngineInfo;
import cn.edu.buaa.act.service4all.bpmnexecution.Job;



public class BPMNEngineClient {
	
	private final Log logger = LogFactory.getLog(BPMNEngineClient.class);
	
	private static final String START_NODE = "bpmn:StartEvent";
	private static final String END_NODE = "bpmn:EndEvent";
	
//	private EngineManager engineManager;
//	
//	private ServiceRepository serviceRepository;
//	
//	private ComponentConfiguration cmpConfig;
	
//	public BPMNEngineClient(EngineManager engineManager,
//							ServiceRepository serviceRepository,
//							ComponentConfiguration cmpConfig){
//		this.engineManager = engineManager;
//		this.cmpConfig = cmpConfig;
//		this.serviceRepository = serviceRepository;
//	}
	
	/**
	 * send the message to the engine
	 * 
	 * @param job
	 * @return
	 */
	public BPMNEngineInfo invokeService(Job job) 
								throws FileNotFoundException, NullPointerException{
		logger.info("Invoke the service : " + job.getServiceName() + " for the job ID: " + job.getJobID());
		
		//create the invoking data XML message from the arguments
		
		try {
			OMElement data;
			
			data = createData(job);
			
			
//			boolean isSuccess = false;
			List<BPMNEngineInfo> engines = job.getEngines();
			if(engines == null || engines.size() <= 0){
				logger.warn("The target engines is null, so can't invoke the bpmn service");
				return null;
			}
			
			BPMNEngineInfo selected = engines.get(0);
			
			URL engineUrl = new URL(selected.getInvokeUrl());
			//URL engineUrl = new URL("http://192.168.3.210:8080/axis2/services/BpmnEngineService");
			
			EndpointReference epr = new EndpointReference(engineUrl.toString());
			
			RPCServiceClient client;
			try {
				
				logger.info("Invoke the BPMNEngine(" + selected.getEngineID() + ") by its invocation url: "+engineUrl.toString());
				client = new RPCServiceClient();
				Options options = client.getOptions();
				options.setTo(epr);
				options.setAction("urn:executeBpmnFlow");
				ServiceClient sender = new ServiceClient();
				sender.setOptions(options);
				
				logRequest(data);
				OMElement response = sender.sendReceive(data);
				
				//output the response
				logResponse(response);
				
				return selected;
				
			} catch (AxisFault e) {
				// TODO Auto-generated catch block
				logger.error("Can't invoke the service using Axis2", e);
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			logger.warn("The URL form is invalidated: " + e1.getMessage());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			logger.warn(e2.getMessage());
		} catch (TransformerException e2) {
			// TODO Auto-generated catch block
			logger.warn(e2.getMessage());
		}
		
		
		return null;
		
	}
	private void logRequest(OMElement request){
		logger.info("Log the request message for the composite service execution!");
		File file = new File("request.xml");
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream output = new FileOutputStream(file);
			request.serialize(output);
			
		}catch(IOException e){
			logger.error(e.getMessage());
		}catch(XMLStreamException e){
			logger.error(e.getMessage());
		}
	}
	private void logResponse(OMElement response){
		logger.info("Log the response message for the composite service execution!");
		File file = new File("response.xml");
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream output = new FileOutputStream(file);
			response.serialize(output);
			
		}catch(IOException e){
			logger.warn(e.getMessage());
		}catch(XMLStreamException e){
			logger.warn(e.getMessage());
		}
	}
	/**
	 * create the SOAP message for invoking the service by OM
	 * 
	 * @param job
	 * @return
	 * @throws TransformerException 
	 * @throws IOException 
	 */
	private OMElement createData(Job job) throws IOException, TransformerException{
		
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");

		OMElement data = fac.createOMElement("executeBpmnFlow", omNs);
		
		//construct the BPMN Part for the invoking message
//		String bpmnFilePath = target.getBpmnPath();
		String compositeServiceName = job.getServiceName();
		Document bpmn = job.getBpmn();
		
//		if(bpmnFilePath == null){
//			logger.warn("The BPMN XML Document's reference is null");
//			//throw new FileNotFoundException("The BPMN XML Document's reference is null");
//			//get the BPMN file from the local file system
//			bpmn = serviceRepository.findBPMNFile(target);
//		}else{
//			bpmn = new File(bpmnFilePath);
//		}
		
		OMElement bpmnFile = buildBpmnFileEnvelope(job.getServiceName(), bpmn);
		data.addChild(bpmnFile);
		//construct the Partition Part
//		String bpmnPartitionFilePath = target.getPartitionPath();
		
		PoolPartition p = new PoolPartition(bpmn);
		List parts = p.partition();
		Document partDoc = p.writePartitionDoc(-1, parts);
		
		
		
		OMElement bpmnPartitionFile = buildBpmnPartitionFileEnvelope(job, partDoc);
		
		//add the engine url to the partition file
//		addEngineUrl(job, bpmnPartitionFile);
		
		data.addChild(bpmnPartitionFile);
		
		//set the BPMN Node ID
		BPMNNode bpmnNode = setNodeId(bpmn);
		
		logger.info("Get the enter id: " + bpmnNode.getEnterNodeId());
		logger.info("Get the end id: " + bpmnNode.getEndNodeId());
		logger.info("Get the monitor address: " + job.getMonitorIp());
		logger.info("Get the job id:" + job.getJobID());
		
		OMElement executeInfo = buildExecuteInforEnvelope(
				compositeServiceName, job.getJobID(), bpmnNode.getEnterNodeId(), 
				bpmnNode.getEndNodeId(), job.getMonitorIp(), job.getMonitorIp(), 
				job.getMonitorIp(), job.getParameters());
		
		data.addChild(executeInfo);
		
		
		
		return data;
	}
	
	private void addEngineUrl(Job job, OMElement bpmnPartitionFile) throws MalformedURLException{
		logger.info("Add the engine's url to the Partition");
//		BPMNEngine engine = job.getTargetEngine();
		
		URL url = new URL(job.getEngines().get(0).getInvokeUrl());
		if(url == null){
			logger.error("The target engine' url is null when to create the Execution request");
			throw new NullPointerException("The target engine's is null when to create the Execution request");
		}
		
		//set the partition file's engineUrl attribute
		Iterator its = bpmnPartitionFile.getChildrenWithLocalName("part");
		while(its.hasNext()){
			Object o = its.next();
			if(o instanceof OMElement){
				OMElement child = (OMElement)o;
				//change the jobId attribute
				child.addAttribute(new OMAttributeImpl("engineUrl", null, url.toString(), null));
			}
		}
	}
	/**
	 * this method is not ready for invoke
	 * @param job
	 * @param target
	 * @throws NullPointerException
	 * @throws FileNotFoundException
	 */
//	private void createPartitionElement(Job job)
//												throws NullPointerException, FileNotFoundException{
////		String bpmnPartitionFilePath = target.getPartitionPath();
////		if(bpmnPartitionFilePath == null){
////			log.warn("The BPMN Partition XML Document's reference is null");
////			throw new FileNotFoundException("The BPMN Partition XML Document's reference is null");
////		}
////		
////		File parition = new File(bpmnPartitionFilePath);
////		OMElement bpmnPartitionFile = buildBpmnPartitionFileEnvelope(
////				compositeServiceName, parition);
////		data.addChild(bpmnPartitionFile);
//		//create the Partition document
//		Document bpmn = job.getBpmn();
//		if(bpmn == null){
//			//read the bpmn from the local file path
////			if(job.getBpmnPath() == null){
////				log.error("The BPMN path for the Composite Service is null: " + target.getServiceName());
////				throw new NullPointerException("The BPMN path for the Composite Service is null: " + target.getServiceName());
////			}
//			try{
//				File f = new File(target.getBpmnPath());
//				if(!f.exists()){
//					log.error("The BPMN file does not exist: " + target.getServiceName());
//					throw new FileNotFoundException("The BPMN file does not exist: " + target.getServiceName());
//				}
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//				DocumentBuilder builder = factory.newDocumentBuilder();
//				bpmn =  builder.parse(f);
//				
//			}catch(ParserConfigurationException e){
//				log.error(e.getMessage());
//			}catch(IOException e){
//				log.error(e.getMessage());
//			}catch(SAXException e){
//				log.error(e.getMessage());
//			}
//			
//		}
//	}
	/**
	 * build the BPMN File Envelope
	 * 
	 * @param compositeServiceName
	 * @param bpmnFile
	 * @return
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	private OMElement buildBpmnFileEnvelope(String compositeServiceName,
			Document bpmn) throws IOException, TransformerException {

		//write the bpmn document to the file
		File tmp = new File("tmp.xml");
		if(!tmp.exists()){
			tmp.createNewFile();
		}
		//write the bpmn file
		DOMSource source = new DOMSource(bpmn);
		TransformerFactory f = TransformerFactory.newInstance();
		Transformer t = f.newTransformer();
		Result r = new StreamResult(tmp);
		t.transform(source, r);
		
		
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");
		OMElement bpmnEle = omFactory.createOMElement("bpmnFile", omNs);
		OMElement cnt = getElementFromFile(tmp);
		if(cnt == null){
			logger.error("We can't get the content from the file");
		}else{
			bpmnEle.addChild(cnt);
		}
		
		
//		FileDataSource dataSource = new FileDataSource(bpmnFile);
//		DataHandler dh = new DataHandler(dataSource);
//		OMText textData = omFactory.createOMText(dh, true);
//		bpmn.addChild(textData);
//		try {
//			StAXOMBuilder builder = new StAXOMBuilder(bpmnFile.getAbsolutePath());
//			OMDocument document = builder.getDocument();
//			OMElement root = document.getOMDocumentElement();
//			OMElement clone = root.cloneOMElement();
//			bpmn.addChild(clone);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			//These exception procession should be recovered
//			log.warn(e.getMessage());
//		} catch (XMLStreamException e) {
//			// TODO Auto-generated catch block
//			log.warn(e.getMessage());
//		}
		
		return bpmnEle;
	}
	private OMElement getElementFromFile(File file){
		logger.info("Get the OM Element from the file : " + file.getAbsolutePath());
		
		try {
			
			XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
			OMFactory omFactory = new OMLinkedListImplFactory();
			XMLStreamReader reader = xmlFactory.createXMLStreamReader(new FileInputStream(file));
			StAXOMBuilder builder = new StAXOMBuilder(omFactory, reader);
			
			OMDocument document = builder.getDocument();
			
			OMElement root = document.getOMDocumentElement();
			return root.cloneOMElement();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//These exception procession should be recovered
			logger.warn(e.getMessage());
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		return null;
	}
	/**
	 * build the BPMN Partition File Envelope
	 * 
	 * @param compositeServiceName
	 * @param bpmnPartitionFile
	 * @return
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	private OMElement buildBpmnPartitionFileEnvelope(Job job, Document parDoc) throws IOException, TransformerException {
		
		//write the bpmn document to the file
		File tmp = new File("tmp1.xml");
		if(!tmp.exists()){
			tmp.createNewFile();
		}
		//write the bpmn file
		DOMSource source = new DOMSource(parDoc);
		TransformerFactory f = TransformerFactory.newInstance();
		Transformer t = f.newTransformer();
		Result r = new StreamResult(tmp);
		t.transform(source, r);
		
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");
		OMElement partition = omFactory.createOMElement("bpmnPartitionFile",
				omNs);
		OMElement cnt = getElementFromFile(tmp);
		
		addEngineUrl(job, cnt);
		
		if(cnt == null){
			logger.error("We can't get the content from the file");
		}else{
			//change the jobId attribute
			cnt.addAttribute(new OMAttributeImpl("jobId", null, String.valueOf(job.getJobID()), null));
			partition.addChild(cnt);
		}
//		FileDataSource dataSource = new FileDataSource(bpmnPartitionFile);
//		DataHandler dh = new DataHandler(dataSource);
//		OMText textData = omFactory.createOMText(dh, true);
//		partition.addChild(textData);
		return partition;
	}

	/**
	 * 
	 * 
	 * 
	 */
	private OMElement buildExecuteInforEnvelope(
			String compositeServiceName, String jobId, String enterNodeId,
			String endNodeId, String databaseInfo, String monitorInfo,
			String busAddress, Node paramData) {

		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");
		OMElement executeInfo = omFactory.createOMElement("executeInfo", omNs);

//		javax.xml.stream.XMLStreamReader reader = BeanUtil
//				.getPullParser(buildInvokeInfo(compositeServiceName, jobId,
//						enterNodeId, endNodeId, databaseInfo, monitorInfo,
//						busAddress));
//		StreamWrapper parser = new StreamWrapper(reader);
//		StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory.createStAXOMBuilder(
//				OMAbstractFactory.getOMFactory(), parser);
//		OMElement invokeInfo = stAXOMBuilder.getDocumentElement();
		
		
		
		//create the invoke by hand
		OMElement invokeInfo = createInvokeInfo(compositeServiceName, jobId, enterNodeId, endNodeId,
												databaseInfo, monitorInfo, busAddress, null, null);
		
		executeInfo.addChild(invokeInfo);

		OMElement parameters = buildParamDataEnvelope(paramData);
		if (parameters != null) {

			executeInfo.addChild(parameters);
		}

		return executeInfo;
	}
	public OMElement createInvokeInfo(String compositeServiceName, String jobId, String enterNodeId,
		String endNodeId, String databaseInfo, String monitorInfo,
		String busAddress, String resourceId, String resourceAddr){
		
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");
		OMNamespace xsi = omFactory.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		OMAttributeImpl nilAttr = new OMAttributeImpl("nil", xsi, "true", omFactory);
		
		
		OMElement invoke = omFactory.createOMElement("InvokeInfo", null);
		String typeValue = "cn.org.act.sdp.bpmnengine.common.InvokeInfo";
		invoke.addAttribute(new OMAttributeImpl("type", null, typeValue, omFactory));
		
		//the reference value
//		String busAddress = null;
//		String compositeServiceName = "eStore1";
//		String databaseInfo = null;
//		String endNodeId = "10031";
//		String enterNodeId = "10022";
//		String jobId = "120";
//		String monitorInfo = null;
//		String resourceId = null;
//		String resourceAddr = null;
		
		OMElement busAddElement = addChild("busAddress", busAddress, omFactory, nilAttr);
		invoke.addChild(busAddElement);
		
		OMElement csElement = addChild("compositeServiceName", compositeServiceName, omFactory, nilAttr);
		invoke.addChild(csElement);
		
		OMElement dbInfoElement = addChild("databaseInfo", databaseInfo, omFactory, nilAttr);
		invoke.addChild(dbInfoElement);
		
		OMElement endNodeElement = addChild("endNodeId", endNodeId, omFactory, nilAttr);
		invoke.addChild(endNodeElement);
		
		OMElement enterNodeElement = addChild("enterNodeId", enterNodeId, omFactory, nilAttr);
		invoke.addChild(enterNodeElement);
		
		OMElement jobIdElement = addChild("jobId", String.valueOf(jobId), omFactory, nilAttr);
		invoke.addChild(jobIdElement);
		
		OMElement monitorInfoElement = addChild("monitorInfo", monitorInfo, omFactory, nilAttr);
		invoke.addChild(monitorInfoElement);
		
		OMElement resourceElelment = addChild("resourceId", resourceId, omFactory, nilAttr);
		invoke.addChild(resourceElelment);
		
		OMElement resourceAddElement = addChild("resourceAddr", resourceAddr, omFactory, nilAttr);
		invoke.addChild(resourceAddElement);
		
		return invoke;
	}
	
	private OMElement addChild(String name, String value, OMFactory factory, OMAttribute attr){
		OMElement child = factory.createOMElement(name, null);
		if(value != null && value.length() > 0){
			child.setText(value);
		}else{
			child.addAttribute(attr);
		}
		return child;
	}
	
	/**
	 * 
	 */
	private InvokeInfo buildInvokeInfo(String compositeServiceName,
			long jobId, String enterNodeId, String endNodeId,
			String databaseInfo, String monitorInfo, String busAddress) {

		InvokeInfo invokeInfo = new InvokeInfo();
		invokeInfo.setCompositeServiceName(compositeServiceName);
		invokeInfo.setJobId(jobId);
		invokeInfo.setEnterNodeId(enterNodeId);
		invokeInfo.setEndNodeId(endNodeId);
		
		invokeInfo.setBusAddress(busAddress);
		invokeInfo.setDatabaseInfo(databaseInfo);
		invokeInfo.setMonitorInfo(monitorInfo);
		// invokeInfo.setResourceAddr(resourceAddr);
		// invokeInfo.setResourceId(resourceId);
		return invokeInfo;

	}
	
	/**
	 * set the enterNodeId which tell the engine the first node to execute and
	 * endNodeId which tell the engine the last node to execute
	 * 
	 * @param doc
	 */
private BPMNNode setNodeId(Document doc) {
		
		Element root = doc.getDocumentElement();
		NodeList poolsIt = root.getElementsByTagName("pools");
		
		BPMNNode node = new BPMNNode();
		
		for (int i = 0; i < poolsIt.getLength(); i++) {
			Element poolsEm = (Element) poolsIt.item(i);
			
			String mainPool = poolsEm.getAttribute("mainPool");
			
			if ((mainPool != null) && (mainPool.equalsIgnoreCase("true"))){
				
				NodeList lanesIt = poolsEm.getElementsByTagName("lanes");
				
				for (int j = 0; j < lanesIt.getLength(); j++) {
					
					Element lanesEm = (Element) lanesIt.item(j);
					
					NodeList graphicalElementsIt = lanesEm.getElementsByTagName("graphicalElements");
					
					for (int k = 0; k < graphicalElementsIt.getLength(); k++) {
						
						Element g = (Element) graphicalElementsIt.item(k);
						//Namespace ns = g.("xsi");
						
//						if(ns == null){
//							log.warn("Can't get the xsi namespace when set node ID");
//						
//						}else{
							//set the enter node
							//String nsURI = ns.getURI();
							NamedNodeMap attrs = g.getAttributes();
							for(int q = 0; q < attrs.getLength(); q++){
								Node n = attrs.item(q);
								String name = n.getNodeName();
								if(name.indexOf("type") != -1){
									String type = n.getNodeValue();
									if (type.equalsIgnoreCase("bpmn:StartEvent")) {
										
										node.setEnterNodeId(g.getAttribute("id"));
									}
									if (type.equalsIgnoreCase("bpmn:EndEvent")) {
										
										node.setEndNodeId(g.getAttribute("id"));
									}
									break;
								}
							}
//							String type = g.getAttribute("type");
//							if(type == null){
//								type = g.getAttribute("xsi:type");
//							}
//							log.info(type);
//							if (type.equalsIgnoreCase(START_NODE)) {
//								
//								node.setEnterNodeId(g.getAttribute("id"));
//							}
//							if (type.equalsIgnoreCase(END_NODE)) {
//								
//								node.setEndNodeId(g.getAttribute("id"));
//							}
//						}
					}
				}
			}
		}
		return node;
	}
	/**
	 * transfer the w3c's Node to The OMElement
	 * @param paramData
	 * @return
	 */
	private OMElement buildParamDataEnvelope(Node paramData) {
		
		if (paramData == null) {
			logger.info("The parameters data is null");
			return null;
		}
		
		OMFactory omFactory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = omFactory.createOMNamespace(
				"http://bpmnengine.sdp.act.org.cn", "executeBpmnFlow");
		OMElement parameters = transferDom2OM(paramData, omFactory, omNs);

//		for (Iterator<Parameter> it = paramData.iterator(); it.hasNext();) {
//
//			Parameter p = it.next();
//			javax.xml.stream.XMLStreamReader reader = BeanUtil.getPullParser(p);
//			StreamWrapper parser = new StreamWrapper(reader);
//			StAXOMBuilder stAXOMBuilder = OMXMLBuilderFactory
//					.createStAXOMBuilder(OMAbstractFactory.getOMFactory(),
//							parser);
//			OMElement parameter = stAXOMBuilder.getDocumentElement();
//			parameters.addChild(parameter);
//
//		}
		
		return parameters;
	}
	
	public static String removePrefix(String name){
		int i = name.indexOf(':');
		if(i == -1){
			return name;
		}else{
			return removePrefix(name.substring(i + 1, name.length()));
		}
	}
	
	public static OMElement transferDom2OM(Node currentNode,
									OMFactory omFactory, 
									OMNamespace omNS){
		//create the current node
		String ns = currentNode.getNamespaceURI();
		String nodeName = currentNode.getNodeName();
		
		//remove the prefix of the node name
		String name = removePrefix(nodeName);
		
		//OMElement cur = omFactory.createOMElement(new QName(ns, nodeName));
		OMElement cur = omFactory.createOMElement(name, omNS);
		//add the attributes for the current node
		if(currentNode.hasAttributes()){
			NamedNodeMap attrs = currentNode.getAttributes();
			for(int j = 0; j < attrs.getLength(); j++){
				Node a = attrs.item(j);
				cur.addAttribute(a.getNodeName(), 
								a.getNodeValue(), 
								null);
			}
		}
		
		if(currentNode.hasChildNodes()){
			
			NodeList nodes = currentNode.getChildNodes();
			for(int i = 0; i < nodes.getLength(); i++){
				Node n = nodes.item(i);
				short type = n.getNodeType();
				switch(type){
				case Node.ELEMENT_NODE:
					cur.addChild(transferDom2OM(n, omFactory, omNS));
					break;
				case Node.TEXT_NODE:
					if(n.getNodeValue() != null && n.getNodeValue().length() >= 1){
						cur.setText(n.getNodeValue());
					}
					break;
				default: break;
				}
			}
			
		}
		
		return cur;
	}
	
//	public EngineManager getEngineManager() {
//		return engineManager;
//	}
//
//	public void setEngineManager(EngineManager engineManager) {
//		this.engineManager = engineManager;
//	}
//
//	public ComponentConfiguration getCmpConfig() {
//		return cmpConfig;
//	}
//
//	public void setCmpConfig(ComponentConfiguration cmpConfig) {
//		this.cmpConfig = cmpConfig;
//	}
	
//	public static void main(String[] args){
//		System.out.println(BPMNEngineClient.removePrefix("12:34:4dfdf"));
//	}
}
