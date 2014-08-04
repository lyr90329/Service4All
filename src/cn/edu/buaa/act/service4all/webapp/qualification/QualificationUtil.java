/**
* Service4All: A Service-oriented Cloud Platform for All about Software Development
* Copyright (C) Institute of Advanced Computing Technology, Beihang University
* Contact: service4all@act.buaa.edu.cn
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3.0 of the License, or any later version. 
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details. 
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*/
package cn.edu.buaa.act.service4all.webapp.qualification;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import cn.edu.buaa.act.service4all.webapp.utility.Constants;
import cn.edu.buaa.act.service4all.webapp.utility.XMLUtil;

public class QualificationUtil {
	private final static Log logger = LogFactory.getLog(QualificationUtil.class);

	public static Document getResult(Document document, String url) {
		File tmp = null;
		String tmpresult = null;
		Document result = null;
		tmp = saveXmlToFile(document);
		if (tmp == null)
			return null;
		tmpresult = sendAndGetResult(tmp, url);
		if (tmpresult == null)
			return null;
		result = XMLUtil.StringToDoc(tmpresult);
		return result;
	}

	private static String sendAndGetResult(File tmp, String url) {
		PostMethod post = new PostMethod(url);
		RequestEntity entity = new FileRequestEntity(tmp, Constants.CODE);
		post.setRequestEntity(entity);
		HttpClient client = new HttpClient();
		String result = null;
		try {
			int resultCode = client.executeMethod(post);
			logger.info(resultCode);
			result = post.getResponseBodyAsString();
			logger.info("Response body:\n " + result);
			// remove the tmp file
			tmp.delete();
			post.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static File saveXmlToFile(Document doc) {
		File tmp = new File(Constants.FILE_PERFIX
				+ Calendar.getInstance().getTimeInMillis()
				+ Constants.FILE_SUFFIX);
		if (!tmp.exists()) {
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		TransformerFactory transFac = TransformerFactory.newInstance();
		try {
			Transformer trans = transFac.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(tmp);
			trans.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return tmp;
	}
}
