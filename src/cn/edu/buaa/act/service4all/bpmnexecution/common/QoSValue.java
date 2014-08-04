package cn.edu.buaa.act.service4all.bpmnexecution.common;

public class QoSValue {
	private String serviceName;
	private String time;
	private String cost;
	private String availability;
	private String computation;

	public QoSValue() {
		
	}
	public void setServiceName(String serviceName){
		this.serviceName=serviceName;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public void setComputation(String computation) {
		this.computation = computation;
	}

	public String getTime() {
		return time;
	}

	public String getCost() {
		return cost;
	}

	public String getAvailability() {
		return availability;
	}

	public String getComputation() {
		return computation;
	}
	public String getServiceName(){
		return serviceName;
	}
}
