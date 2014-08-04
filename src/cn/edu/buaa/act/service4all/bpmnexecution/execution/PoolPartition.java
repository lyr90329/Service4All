package cn.edu.buaa.act.service4all.bpmnexecution.execution;

import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class PoolPartition {
	/*
	 * the doc to read BPMN file
	 */
	private Document doc;
	/*
	 * log the information
	 */
	private static final Log logger = LogFactory.getLog(PoolPartition.class);
	/**
	 * init PoolPartition
	 * 
	 * @param doc
	 */
	public PoolPartition(Document doc) {
		this.doc = doc;
		
	}
	/**
	 * set BPMN node to the partition according to the pool and title which is
	 * the first pool to execute
	 * 
	 * @param 
	 * @return pool list to register the nodeId which already partition
	 */
	public List partition() {
		List<Partition> partitionList = new ArrayList();
//		Element root = doc.getRootElement();
//		List poolsList = root.elements("pools");
		NodeList poolsList = doc.getElementsByTagName("pools");
		
		int size=poolsList.getLength();
		
		for(int i=0; i<size; i++){
			
			Element poolEm=(Element)poolsList.item(i);
			Partition partition = new Partition(Integer.toString(i));
			//String poolId = poolEm.attributeValue("id");
			String poolId = poolEm.getAttribute("id");
			
			partition.setPoolId(poolId);
			//String mainPool=poolEm.attributeValue("mainPool");
			String mainPool = poolEm.getAttribute("mainPool");
			
			if(mainPool==null)
				mainPool="false";
			
			partition.setMainPoolFlag(mainPool);
			//Iterator lanesIt=poolEm.elements("lanes").iterator();
			NodeList lanesIt = poolEm.getElementsByTagName("lanes");
			for(int j = 0; j < lanesIt.getLength(); j++){
				Node laneNode = lanesIt.item(j);
				NodeList children = laneNode.getChildNodes();
				for(int k = 0; k < children.getLength(); k++){
					if(children.item(k) instanceof Element){
						Element graphicalElement = (Element)children.item(k);
						String nodeId = graphicalElement.getAttribute("id");
						partition.addNode(nodeId);
					}
					
				}
			}
			
			partitionList.add(partition);
		}
		return partitionList;
	}
	/**
	 * initialize the part Document object according to the partitionList and jobId
	 * 
	 * @param jobId
	 *           the jobId to match which is the same partition
	 * @param partitionList
	 *           the pool partition result          
	 * @return Document
	 *           the Document register the pool partition and jobId
	 */
	public  Document writePartitionDoc(long jobId,List<Partition> partitionList) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		
		
			Document partitionDoc = builder.newDocument();
			
			Element rootEm = partitionDoc.createElement("parts");
	//		Document doc = new Document(rootEm);
			partitionDoc.appendChild(rootEm);
			
			//rootEm.addAttribute("jobId", Long.toString(jobId));
			rootEm.setAttribute("jobId", Long.toString(jobId));
			Iterator partitionIt = partitionList.iterator();
			
			while(partitionIt.hasNext()){
				
				Partition partition = (Partition)partitionIt.next();
				Element poolEm =partitionDoc.createElement("part");
				
				String poolId=partition.getPoolId();
				//Attribute poolAttri=new Attribute("poolId",poolId);
				String partId=partition.getPartId();
				
				//Attribute partIdAttri=new Attribute("id",partId);
				String isMainPool=partition.getMainPoolFlag();
				//Attribute mainPoolAttri=new Attribute("isMainPool",isMainPool);
				
	//			poolEm.addAttribute("poolId", poolId);
	//			poolEm.addAttribute("isMainPool", isMainPool);
	//			poolEm.addAttribute("id", partId);
				poolEm.setAttribute("poolId", poolId);
				poolEm.setAttribute("isMainPool", isMainPool);
				poolEm.setAttribute("id", partId);
				
				//rootEm.add(poolEm);
				rootEm.appendChild(poolEm);
				Iterator nodeIt=partition.getNodeList().iterator();
				
				while(nodeIt.hasNext()){
					
					String nodeId=(String)nodeIt.next();
					Element nodeEm = partitionDoc.createElement("node");
					
					nodeEm.setAttribute("id", nodeId);
					
					poolEm.appendChild(nodeEm);
					
				}
				
			}
			return partitionDoc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args){
//		String bpmnFilePath="bpmn//default.bpmn";
//		ServiceMatcher matcher =new ServiceMatcher(bpmnFilePath);
//		Document doc=matcher.getBpmnDom();
//		PoolPartition partition =new PoolPartition(doc);
//		List list=partition.partition();
//		Iterator it=list.iterator();
//		while(it.hasNext()){
//			Partition p=(Partition)it.next();
//			Iterator nodeIt=p.getNodeList().iterator();
//			while(nodeIt.hasNext()){
//				System.out.println(nodeIt.next());
//			}
//			
//		}
//	}
}
