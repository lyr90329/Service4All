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
package cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation;

import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileDeletionOnLinux;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileDeletionOnWindows;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileReproductionOnLinux;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileReproductionOnWindows;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileSizeObtainerOnLinux;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.FileSizeObtainerOnWindows;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.TomcatModificationOnLinux;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.impl.TomcatModificationOnWindows;


public abstract class FileManipulationFactory {

	public static FileDeletion newFileDeletion(){
		if(Constants.SYSTEM_TYPE.startsWith( "Win" )){
			return FileDeletionOnWindows.getInstance();
		}
		else if(Constants.SYSTEM_TYPE.startsWith( "Linux" )){
			return FileDeletionOnLinux.getInstance();
		}
		else{
			return null;
		}
	}
	
	public static FileReproduction newFileReproduction(){
		if(Constants.SYSTEM_TYPE.startsWith( "Win" )){
//			return new FileReproductionOnWindows(pathfrom, pathout);
			return FileReproductionOnWindows.newInstance();
		}
		else if(Constants.SYSTEM_TYPE.startsWith( "Linux" )){
			return FileReproductionOnLinux.newInstance();
		}
		else{
			return null;
		}
	}
	
	public static FileSizeObtainer newFileSizeObtainer(){
		if(Constants.SYSTEM_TYPE.startsWith( "Win" )){
			return FileSizeObtainerOnWindows.newInstance();
		}
		else if(Constants.SYSTEM_TYPE.startsWith( "Linux" )){
			return FileSizeObtainerOnLinux.newInstance();
		}
		else{
			return null;
		}
	}
	
	public static TomcatModification newTomcatModification(){
		if(Constants.SYSTEM_TYPE.startsWith( "Win" )){
			return TomcatModificationOnWindows.newInstance();
		}
		else if(Constants.SYSTEM_TYPE.startsWith( "Linux" )){
			return TomcatModificationOnLinux.newInstance();
		}
		else{
			return null;
		}
	}
}
