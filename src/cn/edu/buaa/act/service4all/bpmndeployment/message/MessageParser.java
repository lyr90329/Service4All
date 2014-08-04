package cn.edu.buaa.act.service4all.bpmndeployment.message;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.buaa.act.service4all.bpmndeployment.task.Task;

/**
 * 
 * @author Huangyj
 *
 */
public abstract class MessageParser {
	
	protected Log logger = LogFactory.getLog(MessageParser.class);
	
	
	public abstract boolean validate(Document request);
	
	public abstract Task parse(Document request);
	
	protected String getElementStrValue(Document request, String elementName){
		if(request.getElementsByTagName(elementName) == null 
				|| request.getElementsByTagName(elementName).getLength() <= 0){
			logger.warn("There is no the " + elementName + " element from the request");
			return null;
		}
		
		return request.getElementsByTagName(elementName).item(0).getTextContent();
	}
	
	/**
	 * convert the child node to a new document
	 * 
	 * need to be tested for use
	 * 
	 * @param request
	 * @param elementName
	 * @return
	 */
	protected Document getElementDocValue(Document request, String elementName){
		
		//get the element from the document
		if(request.getElementsByTagName(elementName) == null 
				|| request.getElementsByTagName(elementName).getLength() <= 0){
			logger.warn("There is no the " + elementName + " element from the request");
			return null;
		}
		
		Element targetElement = (Element)request.getElementsByTagName(elementName).item(0);
		Element docValue = (Element)targetElement.getFirstChild();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element newValue = (Element)doc.importNode(docValue, true);
			doc.appendChild(newValue);
			
			return doc;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn("Can't create a new document", e);
			return null;
		}
	}
	
	protected boolean validateElement(Document request, String elementName){
		
		if(request.getElementsByTagName(elementName) == null 
				|| request.getElementsByTagName(elementName).getLength() <= 0){
			logger.warn("Miss the " + elementName + " element from the request");
			return false;
		}
		return true;
	}
	
	protected boolean validateAttribute(Document request, String elementName, 
											String attr, String value){
		if(validateElement(request, elementName)){
			Element ele = (Element)request.getElementsByTagName(elementName).item(0);
			return validateAttribute(ele, attr, value);
		}
		
		return false;
		
	}
	
	protected boolean validateAttribute(Document request, String attr, String value){
		
		Element root = request.getDocumentElement();
		return validateAttribute(root, attr, value);
		
	}
	
	protected boolean validateAttribute(Element targetEle, String attr, String value){
		
		//validate the attribute values
		if(targetEle.getAttribute(attr) == null){
			logger.warn("Miss the " + attr + " attribute of the <" + targetEle.getLocalName() + ">element");
			return false;
		}
		if(!targetEle.getAttribute(attr).equalsIgnoreCase(value)){
			logger.warn("The value " + attr + " attribute of the <" + targetEle.getLocalName() + ">element is not " + value);
			return false;
		}
		
		return true;
	}
	
}
