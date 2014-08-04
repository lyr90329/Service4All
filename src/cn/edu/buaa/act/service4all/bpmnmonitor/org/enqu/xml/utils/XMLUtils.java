package cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.InputStreamEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLUtils {
	
	private static Log logger = LogFactory.getLog(XMLUtils.class);
	
	public static Transformer createNewTranformer(){
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			return factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create the transformer: " + e.getMessage());
			
			return null;
		}
	}
	
	public static Document createNewDocument(){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create a new document", e);
			return null;
		}
	}
	
	public static InputStream retrieveInputStream(Document doc){
		
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		Transformer transformer = XMLUtils.createNewTranformer();
		try {
			transformer.transform(source, result);
			
			return new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		
		return null;
	}
	
	public static Document constructXml(InputStream input){
		
		if(input == null){
			logger.warn("The input stream is null, so can't construct the document");
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(input);
			
			return doc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create the document from the input stream", e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create the document from the input stream", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create the document from the input stream", e);
		}
		
		return null;
		
		
	}
	
	public static InputStreamEntity retrieveInputStreamEntity(Document doc){
		
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		Transformer transformer = XMLUtils.createNewTranformer();
		try {
			transformer.transform(source, result);
			
			InputStream input = new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
			int l = writer.toString().getBytes().length;
			InputStreamEntity entity = new InputStreamEntity(input, l);
			return entity;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		
		return null;
		
		
	}
}
