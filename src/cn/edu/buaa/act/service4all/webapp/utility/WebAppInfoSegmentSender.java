//----------------------------DRPS 201308----------------------------------//
package cn.edu.buaa.act.service4all.webapp.utility;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import cn.edu.buaa.act.service4all.webapp.appliance.NginxServer;
import cn.edu.buaa.act.service4all.webapp.database.DBHandler;

public class WebAppInfoSegmentSender {
	private static int deinfonum = 0;
	private Config config = new Config();
	private final int batchSize = config.getBatchSize();
	private static Document dedoc = null;
	private static ArrayList<String> sqllist = new ArrayList<String>();
	private static Object o = new Object();
	
	public WebAppInfoSegmentSender(){
	} 
	
	/**
	 * Create Database query
	 * @param serviceID
	 * @param serviceName
	 * @param userName
	 * @param nginx
	 * @return
	 */
	private String sqlinfo(String serviceID,String serviceName,String userName,NginxServer nginx){
		String invokeUrl = "http://" + nginx.getHost() + ":"
				+ nginx.getPort() + "/" + serviceID + "_" + serviceName + "/";
		
		String sql = "INSERT INTO webAppRP(app_id,app_name,invokeurl,owner) VALUES ('"
				+ serviceID
				+ "','"
				+ serviceName
				+ "','"
				+ invokeUrl
				+ "','"
				+ userName
				+ "');";
		
		return sql;
	}
	
	
	/**
	 * send the Information Segment to Nginx
	 * @param doc
	 * @param nginx
	 */
	public void sendInfoSegment2Ngnix(Document doc, NginxServer nginx){
		try {
			Socket client = new Socket(nginx.getHost(), nginx.getListenPort());
			PrintStream outBuf = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			try {
				TransformerFactory transFactory = TransformerFactory
						.newInstance();
				Transformer transFormer = transFactory.newTransformer();
				transFormer.setOutputProperty(OutputKeys.INDENT, "yes");
				transFormer.setOutputProperty("encoding", "UTF-8");
				DOMSource domSource = new DOMSource(doc);
				StreamResult xmlResult = new StreamResult(bos);
				transFormer.transform(domSource, xmlResult);
			} catch (TransformerException e) {
				e.printStackTrace();
			}

			BufferedReader inBuf = new BufferedReader(new InputStreamReader(client.getInputStream()));
			outBuf = new PrintStream(client.getOutputStream());
			long segSize = bos.toString().length();
			outBuf.print(segSize);
			System.out.println(segSize);

			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			String line = null;
			while ((line = br.readLine()) != null) {
				outBuf.println(line);
				System.out.println(line);
			}
			
			line = inBuf.readLine();
			if( line.equals("@@@@")){
				br.close();
				client.shutdownOutput();
				inBuf.close();
				outBuf.close();
				bos.close();
				bis.close();
				client.close();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * store the Information Segment to the buffer pool
	 * @param tempdoc
	 */
	private void addDocument(Document tempdoc){
		if (deinfonum == 0) {
			dedoc = tempdoc;
		} else {
			Element clone = (Element)dedoc.importNode(tempdoc.getDocumentElement().getFirstChild(), true);
			dedoc.getDocumentElement().appendChild(clone);
		}
		deinfonum++;
	}
	
	
	/**
	 * If the number of information segment is equal to the batchSize,send
	 */
	public void sendInfo(Document doc,NginxServer nginx,String serviceID,String serviceName,String userName){
		synchronized(o){
			addDocument(doc);
			sqllist.add(sqlinfo(serviceID,serviceName,userName,nginx));
			if (deinfonum == batchSize) {
				sendInfoSegment2Ngnix(dedoc, nginx);
				
				DBHandler handler = new DBHandler();
				for (Iterator<String> it = sqllist.iterator(); it.hasNext(); ) {
					String sqlstr = it.next();
					handler.insertNewAppRP(sqlstr);
					it.remove();
				}
				
				deinfonum = 0;
				dedoc = null;
			}
		}
	}
}
//------------------------------------------------------------------------//