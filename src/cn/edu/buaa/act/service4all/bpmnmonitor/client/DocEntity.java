package cn.edu.buaa.act.service4all.bpmnmonitor.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.xbean.spring.generator.LogFacade;
import org.w3c.dom.Document;

import cn.edu.buaa.act.service4all.bpmnmonitor.org.enqu.xml.utils.XMLUtils;

public class DocEntity implements HttpEntity {
	
	private static Log logger = LogFactory.getLog(DocEntity.class);
	
	private Document doc;
	
	//private InputStream inputStream;
	private InputStreamEntity inputEntity;
	

	public DocEntity(Document doc) throws NullPointerException{
		this.doc = doc;
		
		// create the input stream for the xml document
		if(doc == null){
			logger.warn("The target document is null");
			throw new NullPointerException("The target document is null");
		}
		
		inputEntity = XMLUtils.retrieveInputStreamEntity(this.doc);
		if(inputEntity == null){
			logger.warn("Can't retrieve the input stream from the document");
			throw new NullPointerException("Can't retrieve the input stream from the document");
		}
		
	}

	@Override
	public void consumeContent() throws IOException {
		// TODO Auto-generated method stub
		inputEntity.consumeContent();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		if(inputEntity == null){
			logger.warn("The input entity wrappered is null");
			return null;
		}
		return inputEntity.getContent();
	}

	@Override
	public Header getContentEncoding() {
		// TODO Auto-generated method stub
		return inputEntity.getContentEncoding();
	}

	@Override
	public long getContentLength() {
		// TODO Auto-generated method stub
		return inputEntity.getContentLength();
	}

	@Override
	public Header getContentType() {
		// TODO Auto-generated method stub
		return inputEntity.getContentType();
	}

	@Override
	public boolean isChunked() {
		// TODO Auto-generated method stub
		return inputEntity.isChunked();
	}

	@Override
	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return inputEntity.isRepeatable();
	}

	@Override
	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return inputEntity.isStreaming();
	}

	@Override
	public void writeTo(OutputStream output) throws IOException {
		// TODO Auto-generated method stub
		inputEntity.writeTo(output);
	}

}
