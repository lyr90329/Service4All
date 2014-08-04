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
package cn.edu.buaa.act.service4all.core.samanager.listener;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaa.act.service4all.core.samanager.app.execution.AppExecutionResult;
import cn.edu.buaa.act.service4all.core.samanager.beans.AppReplica;

public class AppInvocationEvent {
	
	protected String targetServiceID;
	protected String targetServiceName;
	protected List<AppReplica> repetitions;
	
	protected List<AppExecutionResult> executionResults;
	protected boolean isSuccessful;
	protected String exeType;
	
	public AppInvocationEvent(){
		repetitions = new ArrayList<AppReplica>();
	}
	
	public String getTargetServiceID() {
		return targetServiceID;
	}
	public void setTargetServiceID(String targetServiceID) {
		this.targetServiceID = targetServiceID;
	}
	public String getTargetServiceName() {
		return targetServiceName;
	}
	public void setTargetServiceName(String targetServiceName) {
		this.targetServiceName = targetServiceName;
	}
	public List<AppReplica> getRepetitions() {
		return repetitions;
	}
	public void setRepetitions(List<AppReplica> repetitions) {
		this.repetitions = repetitions;
	}

	public List<AppExecutionResult> getExecutionResults() {
		return executionResults;
	}

	public void setExecutionResults(List<AppExecutionResult> executionResults) {
		this.executionResults = executionResults;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public String getExeType() {
		return exeType;
	}

	public void setExeType(String exeType) {
		this.exeType = exeType;
	}
	
	
}
