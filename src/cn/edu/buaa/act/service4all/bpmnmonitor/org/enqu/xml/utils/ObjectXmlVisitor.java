package cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * generate the xml according to the specified object
 * 
 * @author enqu
 *
 */
public class ObjectXmlVisitor implements XmlVisitor{
	
	private XmlObject targetObject;
	
	public ObjectXmlVisitor(XmlObject target) {
		this.targetObject = target;
	}

	@Override
	public Document generateXml() {
		// TODO Auto-generated method stub
		
		Document request = XMLUtils.createNewDocument();
		
		Element element = request.createElement(targetObject.getTagName());
		request.appendChild(element);
		
		// add the attributes
		addAttributes(element, this.targetObject.getAttributes());
		
		if(targetObject.hasChildren()){
			
			List<XmlObject> children = targetObject.getAllChildrens();
			
			for(XmlObject o : children){
				
				Element childElement = generateElement(o, request);
				element.appendChild(childElement);
				
			}
		}
		
		return request;
	}
	
	protected Element generateElement(XmlObject targetObject, Document requestDoc){
		
		//create the element for the target object
		Element targetElement = requestDoc.createElement(targetObject.getTagName());
		
		//add the attribute to the targetElement
		addAttributes(targetElement, targetObject.getAttributes());
		
		if(targetObject.hasChildren()){
			
			List<XmlObject> children = targetObject.getAllChildrens();
			for(XmlObject o : children){
				
				Element childElement = generateElement(o, requestDoc);
				targetElement.appendChild(childElement);
				
			}
		}
		
		return targetElement;
		
	}
	
	protected void addAttributes(Element target, Map<String, String> attrs){
		
		// add attributes
		Iterator<String> keys = attrs.keySet().iterator();
		while(keys.hasNext()){
			String k = keys.next();
			String value = attrs.get(k);
			target.setAttribute(k, value);
			
		}
		
	}

	@Override
	public Object parseResponse(Document resp) {
		// TODO Auto-generated method stub
		return null;
	}

}
