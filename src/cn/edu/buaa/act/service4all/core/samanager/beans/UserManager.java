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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is designed for managing a user's privilege of 
 * app operations,but it is not used currently.
 * @author Huangyj
 *	
 */
public class UserManager {
	
	private Log logger = LogFactory.getLog(UserManager.class);
	
	private Map<String, User> users = new HashMap<String, User>();
	
	public void addUser(User user){
		logger.info("Adding a new User: " + user.getId());
		users.put(user.getId(), user);
	}
	
	public void removeUser(String userId){
		logger.info("Removing a User: " + userId);
		users.remove(userId);
	}
	
	public User getUserById(String id){
		logger.info("Get the user by id : " + id);
		if(users.get(id) == null){
			logger.warn("The user doesn't exist: " + id);
			return null;
		}
		
		return users.get(id);
	}
	
	public boolean checkUser(String id, String password){
		if(users.get(id) == null){
			logger.warn("The user doesn't exist: " + id);
			return false;
		}
		
		User user = users.get(id);
		if(!user.getPassword().endsWith(password)){
			logger.warn("The password is wrong for the user: " + id);
			return false;
		}
		return true;
	}
}
