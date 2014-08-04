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
package cn.edu.buaa.act.service4all.core.samanager.beans;

/**
 * The base bean class for appliance
 * It has three parts including status, description and records
 * 
 * @author dell
 *
 */
public class Appliance {
	
	protected ApplianceStatus status;
	protected ApplianceDescription desp;
	protected ApplianceRecords records;
	
	
	public ApplianceStatus getStatus() {
		return status;
	}
	public void setStatus(ApplianceStatus status) {
		this.status = status;
	}
	public ApplianceDescription getDesp() {
		return desp;
	}
	public void setDesp(ApplianceDescription desp) {
		this.desp = desp;
	}
	public ApplianceRecords getRecords() {
		return records;
	}
	public void setRecords(ApplianceRecords records) {
		this.records = records;
	}
	
	
	
}
