package cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils;

import org.w3c.dom.Document;

public interface XmlVisitor {
	
	public Document generateXml();	
	
	public Object parseResponse(Document resp);
	
	
}
