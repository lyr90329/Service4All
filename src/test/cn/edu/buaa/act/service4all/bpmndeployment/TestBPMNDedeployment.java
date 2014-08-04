package test.cn.edu.buaa.act.service4all.bpmndeployment;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



public class TestBPMNDedeployment {
	
	public void testDeploy() throws Exception{
		
		Document req = createDeploymentRequest();
		File xmlFile = transferToFile(req);
		
		PostMethod post = new PostMethod(" http://192.168.104.100:8192/appengine/BPMNDeployment/");
		RequestEntity entity = new FileRequestEntity(xmlFile, "text/xml; charset=ISO-8859-1");
		post.setRequestEntity(entity);
		HttpClient client =new HttpClient();
		
		try{
			int result = client.executeMethod(post);
			System.out.println("Response status code: " + result);
			System.out.println("Response body:\n " + post.getResponseBodyAsString());
		}finally{
			post.releaseConnection();
		}

	}
	
	private File transferToFile(Document doc) throws IOException, TransformerException{
		File f = new File("test.xml");
		if(!f.exists()){
			f.createNewFile();
		}
		
		DOMSource source = new DOMSource(doc);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		StreamResult result = new StreamResult(f);
		transformer.transform(source, result);
		
		return f;
		
	}
	
	private Document createDeploymentRequest(){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document bpmnDoc = builder.parse(new File("bpmn/eStore2.bpmn"));
			Element bpmnCnt = bpmnDoc.getDocumentElement();
			
			Document req = builder.newDocument();
			Element root = req.createElement("deployRequest");
			root.setAttribute("type", "bpmn");
			req.appendChild(root);
			
			Element serviceName = req.createElement("serviceName");
			serviceName.setTextContent("eStore2");
			root.appendChild(serviceName);
			
			
			Element provider = req.createElement("provider");
			provider.setTextContent("enqu");
			root.appendChild(provider);
			
			Element docEle = req.createElement("serviceDoc");
			Element newValue = (Element)req.importNode(bpmnCnt, true);
			docEle.appendChild(newValue);
			root.appendChild(docEle);
			
			return req;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void testUndeploy() throws Exception{
		File xmlFile = new File("test/testUndeployment.xml");
		
		PostMethod post = new PostMethod(" http://192.168.104.100:8192/appengine/BPMNUndeployment/");
		RequestEntity entity = new FileRequestEntity(xmlFile, "text/xml; charset=ISO-8859-1");
		post.setRequestEntity(entity);
		HttpClient client =new HttpClient();
		
		try{
			int result = client.executeMethod(post);
			System.out.println("Response status code: " + result);
			System.out.println("Response body:\n " + post.getResponseBodyAsString());
		}finally{
			post.releaseConnection();
		}
	}
	
	public void testBPMNDocQuery() throws Exception{
		File xmlFile = new File("test/testBPMNDocQuery.xml");
		
		PostMethod post = new PostMethod(" http://192.168.104.100:8192/appengine/BPMNDocQuery/");
		RequestEntity entity = new FileRequestEntity(xmlFile, "text/xml; charset=ISO-8859-1");
		post.setRequestEntity(entity);
		HttpClient client =new HttpClient();
		
		try{
			int result = client.executeMethod(post);
			System.out.println("Response status code: " + result);
			System.out.println("Response body:\n " + post.getResponseBodyAsString());
		}finally{
			post.releaseConnection();
		}
	}
	
	
	public void testParseMessageDoc(){
		
		File bpmnFile = new File("bpmn/eStore2.bpmn");
		if(!bpmnFile.exists()){
			System.out.println("The file does not exist: " + bpmnFile.getAbsolutePath());
			return;
		}
		
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			
			DocumentBuilder builder = f.newDocumentBuilder();
			Document oldDoc = builder.parse(bpmnFile);
			Element root = oldDoc.getDocumentElement();
			
			//write the document to a tmm file
			writeDoc(oldDoc, "test_old.xml");
			
			DocumentBuilderFactory f1 = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder1 = f1.newDocumentBuilder();
			Document newDoc = builder1.newDocument();
			
			Element n = (Element)newDoc.importNode(root, true);
			newDoc.appendChild(n);
			writeDoc(newDoc, "test_new.xml");
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void writeDoc(Document doc, String path) 
							throws IOException, TransformerException{
		
		File tmp = new File(path);
		if(!tmp.exists()){
			tmp.createNewFile();
		}
		
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		StreamResult result = new StreamResult(tmp);
		transformer.transform(new DOMSource(doc), result);
		
	}
	
	public static void main(String[] args){
		
		TestBPMNDedeployment test = new TestBPMNDedeployment();
		
		try {
//			test.testBPMNDocQuery();
			test.testDeploy();
//			test.testUndeploy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
