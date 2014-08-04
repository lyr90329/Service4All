//----------------------------DRPS 201308----------------------------------//
package cn.edu.buaa.act.service4all.webapp.appliance;


public class NginxServer {
	private String id;
	private String host;
	private int port;
	private int listenPort;
	
	public NginxServer(String host, int port, int listenPort) {
		this.host = host;
		this.port = port;
		this.listenPort = listenPort;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getListenPort() {
		return listenPort;
	}
}
//------------------------------------------------------------------------//