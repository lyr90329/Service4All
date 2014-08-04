package cn.edu.buaa.act.service4all.core.samanager.beans;

public class AppRepetitionStatus {
	
	public final static int APP_DEPLOYED = 1;
	public final static int APP_STARTED = 2;
	public final static int APP_ERROR = 4;
	public final static int APP_STOPPED = 5;
	
	
	protected AppRepetition repetition;
	protected int status;
	
	
	public AppRepetition getRepetition() {
		return repetition;
	}
	
	public void setRepetition(AppRepetition repetition) {
		this.repetition = repetition;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
//	public long getHandlingRequests() {
//		return handlingRequests;
//	}
//	public void setHandlingRequests(long handlingRequests) {
//		this.handlingRequests = handlingRequests;
//	}
//	public long getFinishedRequests() {
//		return finishedRequests;
//	}
//	public void setFinishedRequests(long finishedRequests) {
//		this.finishedRequests = finishedRequests;
//	}
	
	
}
