package cn.edu.buaa.act.service4all.core.samanager.beans;

/**
 * The real body for App invocation
 * Each App may have some repetitions in different appliances
 * 
 * @author Huangyj
 *
 */
public class AppRepetition {
	
	protected Appliance container;
	protected App desription;
	protected String invocationUrl;
	protected AppRecords records;
	protected String containerId;
	protected AppRepetitionStatus status;
	
	protected String appName;
	protected String appId;
	
	
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Appliance getContainer() {
		return container;
	}
	
	public void setContainer(Appliance container) {
		this.container = container;
//		this.containerId = container.getDesp().getId();
	}
	
	public String getContainerId(){
		return this.containerId;
	}
	
	public App getDesription() {
		return desription;
	}
	
	public void setDesription(App desription) {
		this.desription = desription;
	}
	
	public String getInvocationUrl() {
		return invocationUrl;
	}
	
	public void setInvocationUrl(String invocationUrl) {
		this.invocationUrl = invocationUrl;
	}
	
	/**
	 * We have to reconsider the interface operations about App Records
	 * 
	 * @return
	 */
	public AppRecords getRecords() {
		return records;
	}
	
	public void setRecords(AppRecords records) {
		this.records = records;
	}

	public AppRepetitionStatus getStatus() {
		return status;
	}

	public void setStatus(AppRepetitionStatus status) {
		this.status = status;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	
	
}
