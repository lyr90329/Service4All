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

import java.util.List;

/**
 * This class is designed for managing a user's privilege of 
 * app operations,but it is not used currently.
 * @author Huangyj
 *	
 */
public class User {
	
	
	private String id;
	private String password;
	
	private List<String> writable;
	private List<String> readable;
	private List<String> owned;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getWritable() {
		return writable;
	}
	public void setWritable(List<String> writable) {
		this.writable = writable;
	}
	
	public void addWritableApp(String appId){
		this.writable.add(appId);
	}
	
	public void removeWritableApp(String appId){
		this.writable.remove(appId);
	}
	
	public List<String> getReadable() {
		return readable;
	}
	public void setReadable(List<String> readable) {
		this.readable = readable;
	}
	public void removeReadableApp(String appId){
		this.readable.remove(appId);
	}
	public void addReadableApp(String appId){
		readable.add(appId);
	}
	
	public List<String> getOwned() {
		return owned;
	}
	public void setOwned(List<String> owned) {
		this.owned = owned;
	}
	public void removeOwnedApp(String appId){
		this.owned.remove(appId);
	}
	public void addOwnedApp(String appId){
		owned.add(appId);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
