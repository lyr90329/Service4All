package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BpmnUtil {
	/**
	 * compositerServiceName
	 */
	private String compositeServiceName;
	/**
	 * record the service information such as service name,service URL,input type
	 * ,output type and operation
	 */
	private List<ServiceUtil> serviceList;
    /**
     * init BpmnUtil
     * 
     * @param compositerServiceName
     */
	public BpmnUtil(String compositeServiceName) {
		this.compositeServiceName = compositeServiceName;
		serviceList = new ArrayList();
	}
    /**
     * add serviceUtil to BpmnUtil
     * 
     * @param service
     */
	public void addServiceToBpmnUtil(ServiceUtil service) {
		serviceList.add(service);
	}
	/**
	 * get the engine url from BPMN part file to invoke the engine execution
	 * 
	 * @return List 
	 *         record serviceUtil list
	 */
	public List getServiceList() {
		return serviceList;
	}
	/**
	 * get the compositerServiceName
	 * 
	 * @return String
	 */
	public String getCompositeServiceName() {
		return compositeServiceName;
	}
}
