/*
*
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
*
*/
package cn.edu.buaa.act.service4all.core.localsamanager.log;
import org.apache.axiom.om.*;

public class WebServiceCallingLog {
	private String userID;
	private String userType;
	private String url;
	private String operation;
	private String startTime;
	private String lastTime;
	private String isiSucessful;
	private OMElement requestSoap;
	private OMElement responseSoap;
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}
	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the lastTime
	 */
	public String getLastTime() {
		return lastTime;
	}
	/**
	 * @param lastTime the lastTime to set
	 */
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * @return the requestSoap
	 */
	public OMElement getRequestSoap() {
		return requestSoap;
	}
	/**
	 * @param requestSoap the requestSoap to set
	 */
	public void setRequestSoap(OMElement requestSoap) {
		this.requestSoap = requestSoap;
	}
	/**
	 * @return the responseSoap
	 */
	public OMElement getResponseSoap() {
		return responseSoap;
	}
	/**
	 * @param responseSoap the responseSoap to set
	 */
	public void setResponseSoap(OMElement responseSoap) {
		this.responseSoap = responseSoap;
	}
	/**
	 * @return the isiSucessful
	 */
	public String getIsiSucessful() {
		return isiSucessful;
	}
	/**
	 * @param isiSucessful the isiSucessful to set
	 */
	public void setIsiSucessful(String isiSucessful) {
		this.isiSucessful = isiSucessful;
	}
	

}
