package cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public abstract class XmlObject {
	
	protected final static String[] simepleTypes = new String[]{
		"int", "float", "Date", "long", "double", "String", "char",
		"short", "byte"
	};
	
	public abstract Map<String, String> getAttributes();
	
	public abstract String getTagName();
	
	public abstract boolean hasChildren();
	
	public abstract List<XmlObject> getAllChildrens();
	
	public abstract QName getNamespace();
	
	public abstract Object getObject();
	
	protected void addAttribute(String name, String value, Map<String, String> attrs){
		if(name == null || value == null){
			return;
		}
		attrs.put(name, value);
	}
	
	protected Map<String, Object> getFields(){
		
		Class targetClass = getObject().getClass();
		Field[] fields = targetClass.getFields();
		
		for(int i = 0; i < fields.length; i++){
			Class fieldType = fields[i].getType();
			
			
		}
		
		Object target = getObject();
		return null;
	}
	
	protected boolean isSimpleType(Class fieldType){
		return false;
	}
	
}
