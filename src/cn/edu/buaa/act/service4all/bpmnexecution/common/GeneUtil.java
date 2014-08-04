package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.HashMap;
import java.util.Map;

public class GeneUtil {
	/**
	 * the service name
	 */
	private String serviceName;
	/**
	 * the number bit
	 */
	private int bit;
	/**
	 * the hashMap used to store the bit and wsdl
	 */
	private HashMap<String,String>map;
	/**
	 * the service operation
	 */
	private String operation;

	public GeneUtil(String serviceName,String operation) {
		this.serviceName = serviceName;
		this.operation=operation;
		map=new HashMap();
	}

	public void setBit(int n) {
		bit = n;
	}
	public int getBit(){
		return bit;
	}
	
	public void putMap(String gene,String wsdl){
		map.put(gene, wsdl);
	}
	public HashMap getMap(){
		return map;
	}
	public String getServiceName(){
		return serviceName;
	}
	public String getOperation(){
		return operation;
	}
}
