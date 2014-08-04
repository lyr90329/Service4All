//----------------------------DRPS 201308----------------------------------//
package cn.edu.buaa.act.service4all.webapp.utility;

import java.io.File;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import cn.edu.buaa.act.service4all.webapp.utility.XMLUtil;

public class Config {
	
	/*
	 static String webAppDeployUrl;
	static String webAppUndeployUrl;
	static String userControlUrl;

	private Config() {
	}

	static {
		Document out = XMLUtil.XML2Document(new File("conf/config.xml"));

		Element webApp = (Element) (out.getDocumentElement()
				.getElementsByTagName("webApp").item(0));
		webAppDeployUrl = webApp.getElementsByTagName("webAppDeploy").item(0)
				.getTextContent();
		webAppUndeployUrl = webApp.getElementsByTagName("webAppUnDeploy")
				.item(0).getTextContent();
		Element authentication = (Element) (out.getDocumentElement()
				.getElementsByTagName("authentication")).item(0);
		userControlUrl = authentication.getElementsByTagName("userControl")
				.item(0).getTextContent();
	}

	public static String getWebAppDeployUrl() {
		return webAppDeployUrl;
	}

	public static void setWebAppDeployUrl(String webAppDeployUrl) {
		Config.webAppDeployUrl = webAppDeployUrl;
	}

	public static String getWebAppUndeployUrl() {
		return webAppUndeployUrl;
	}

	public static void setWebAppUndeployUrl(String webAppUndeployUrl) {
		Config.webAppUndeployUrl = webAppUndeployUrl;
	}

	public static String getUserControlUrl() {
		return userControlUrl;
	}

	public static void setUserControlUrl(String userControlUrl) {
		Config.userControlUrl = userControlUrl;
	}
	 */
	
	private static String nginxIP;
	private static int nginxPort;
	private static int nginxListeningPort;
	private static int batchSize;

	public Config(){
		
	}
	
	static {
		try {
			Document out = XMLUtil.XML2Document(new File("conf/config.xml"));

			Element nginx = (Element) (out.getDocumentElement()
					.getElementsByTagName("nginx").item(0));
			nginxIP = nginx.getElementsByTagName("nginxIP").item(0)
					.getTextContent();
			nginxPort = Integer
					.parseInt(nginx.getElementsByTagName("nginxPort").item(0)
							.getTextContent());
			nginxListeningPort = Integer.parseInt(nginx
					.getElementsByTagName("nginxListenPort").item(0)
					.getTextContent());
			batchSize = Integer
					.parseInt(nginx.getElementsByTagName("batchSize").item(0)
							.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNginxIp() {
		return nginxIP;
	}


	public int getNginxPort() {
		return nginxPort;
	}

	public int getNginxListeningPort() {
		return nginxListeningPort;
	}

	public int getBatchSize() {
		return batchSize;
	}
}
//------------------------------------------------------------------------//