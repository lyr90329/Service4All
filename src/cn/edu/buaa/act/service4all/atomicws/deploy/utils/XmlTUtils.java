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
package cn.edu.buaa.act.service4all.atomicws.deploy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

public class XmlTUtils {
	public XmlTUtils() {

	}

	public Document getResult(Document document, String url) {
		File tmp = null;
		String tmpresult = null;
		Document result = null;

		tmp = saveXmlToFile(document);
		if (tmp == null)
			return null;

		tmpresult = this.sendAndGetResult(tmp, url);
		if (tmpresult == null)
			return null;

		result = convertStringToXML(tmpresult);
		return result;
	}

	@SuppressWarnings("unused")
	private String sendAndGetResult(File tmp, String url) {
		PostMethod post = new PostMethod(url);

		RequestEntity entity = new FileRequestEntity(tmp,
				"text/xml; charset=ISO-8859-1");
		post.setRequestEntity(entity);
		HttpClient client = new HttpClient();
		String result = null;

		try {
			int resultCode = client.executeMethod(post);
			result = post.getResponseBodyAsString();
			System.out.println("Response body:\n " + result);

			tmp.delete();
			post.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private File saveXmlToFile(Document doc) {
		File tmp = new File("tmp_" + Calendar.getInstance().getTimeInMillis()
				+ ".xml");

		if (!tmp.exists()) {
			try {
				tmp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		saveXmlToFile(doc, tmp);

		return tmp;
	}

	private void saveXmlToFile(Document doc, File file) {
		XMLOutputter xmlout = new XMLOutputter();
		Format fmt = Format.getPrettyFormat();

		fmt.setEncoding("UTF-8");
		fmt.setIndent("  ");
		xmlout.setFormat(fmt);

		try {
			FileOutputStream output = new FileOutputStream(file);
			xmlout.output(doc, output);
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document convertStringToXML(String s) {
		StringReader sr = new StringReader(s);
		InputSource is = new InputSource(sr);
		SAXBuilder saxbuilder = new SAXBuilder();
		Document doc = null;

		if (s == null)
			return null;

		try {
			doc = saxbuilder.build(is);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;

	}
}