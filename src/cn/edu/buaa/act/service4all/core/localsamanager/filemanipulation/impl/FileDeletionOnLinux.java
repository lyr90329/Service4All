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
package cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl;

import java.io.File;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileDeletion;


public class FileDeletionOnLinux implements FileDeletion {
	
	private static FileDeletionOnLinux fileDeletionOnLinux = null;
	
	public static FileDeletionOnLinux getInstance(){
		if(fileDeletionOnLinux==null){
			fileDeletionOnLinux = new FileDeletionOnLinux();
		}
		return fileDeletionOnLinux;
	}

	@Override
	public boolean delAllFile( String path ) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean delFolder( String folderPath ) {
		try {
			delAllFile(folderPath); 
			String filePath = folderPath;
			delFile(filePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delFile( String filePath ) {
		try {
			File file = new File(filePath);
			delFile(file);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}

	@Override
	public boolean delFile(File file){
		try {
			file.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 	
	}
}
