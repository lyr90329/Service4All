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
import java.io.FileInputStream;
import java.text.DecimalFormat;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileSizeObtainer;


public class FileSizeObtainerOnLinux implements FileSizeObtainer {

	private static FileSizeObtainerOnLinux fileSizeObtainerOnLinux = null;

	public static FileSizeObtainer newInstance() {
		if(fileSizeObtainerOnLinux == null){
			fileSizeObtainerOnLinux = new FileSizeObtainerOnLinux();
		}
		return fileSizeObtainerOnLinux;
	}
	
	@Override
	public String FormetFileSize( long fileS ) {
		DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
	}


	@Override
	public long getFileFolderSize( File f ) throws Exception {
		long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileFolderSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
	}


	@Override
	public long getFileSizes( File f ) throws Exception {
		long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
           s= fis.available();
        } else {
            f.createNewFile();
//            System.out.println("The file ");
        }
        return s;
	}


	@Override
	public long getlist( File f ) {
		long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
	}


}
