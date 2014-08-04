package cn.edu.buaa.act.service4all.qualifycontrol.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.act.sdp.appengine.messaging.util.XMLUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class xmlTool 
{
	public xmlTool()
	{
		
	}
	
	private static String convertDomToString(org.w3c.dom.Document doc)
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer trans;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try 
		{
			trans = factory.newTransformer();
			DOMSource domSource = new DOMSource(doc);		

			Result result = new StreamResult(bo);
			trans.transform(domSource, result);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return bo.toString();		
	}
	
	private static org.jdom.Document convertStringToJdom(String source)
	{
		org.jdom.Document doc=null;
		StringReader sr = new StringReader(source);
		InputSource is = new InputSource(sr);	
		org.jdom.input.SAXBuilder saxbuilder=new org.jdom.input.SAXBuilder();
		
		try 
		{
			doc=saxbuilder.build(is);
		}
		catch (org.jdom.JDOMException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;			
	}
	
	private static String convertJdomToString(org.jdom.Document doc)
	{
		String result;
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter xmlout = new XMLOutputter(format);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		
		try
		{
			xmlout.output(doc,bo);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result=bo.toString();
		return result;
	}	
	
	private static org.w3c.dom.Document convertStringToDom(String source)
	{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder;
		org.w3c.dom.Document doc=null;
		
		try 
		{
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(source.getBytes())); 
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return doc;
	}	
	
	public static org.jdom.Document convertDomToJdom(org.w3c.dom.Document doc)
	{
		org.jdom.Document result;
		String s;
		
		s=xmlTool.convertDomToString(doc);
		result=xmlTool.convertStringToJdom(s);
		
		return result;		
	}
	
	public static org.w3c.dom.Document convertJdomToDom(org.jdom.Document doc)
	{
		org.w3c.dom.Document result;
		String s;
		
		s=xmlTool.convertJdomToString(doc);
		result=xmlTool.convertStringToDom(s);
		
		return result;	
	}
}