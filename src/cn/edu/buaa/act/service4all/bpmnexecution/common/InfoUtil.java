package cn.edu.buaa.act.service4all.bpmnexecution.common;
import java.util.*;
public class InfoUtil {
    private long jobId;
	private String compositeServiceName;
	public InfoUtil(){
		compositeServiceName="";
		jobId=-1;
	}
    public void setJobId(long jobId){
    	this.jobId=jobId;
    }
	public void setCompositeServiceName(String name){
		this.compositeServiceName=name;
	}
    public long getJobId(){
    	return jobId;
    }
	public String getCompositeServiceName(){
		return compositeServiceName;
	}
}
