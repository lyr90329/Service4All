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

import java.io.*;
import org.apache.log4j.Logger;
import cn.edu.buaa.act.service4all.core.localsamanager.config.Constants;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileManipulationFactory;
import cn.edu.buaa.act.service4all.core.localsamanager.filemanipulation.FileReproduction;

public class FileReproductionOnWindows implements FileReproduction {

	private static FileReproductionOnWindows fileReproductionOnWindows = null;
	private static Logger logger = Logger.getLogger( FileReproductionOnWindows.class );
	
	public static FileReproductionOnWindows newInstance(){
		if(fileReproductionOnWindows==null){
			fileReproductionOnWindows = new FileReproductionOnWindows();
		}
		return fileReproductionOnWindows;
	}


	private FileReproductionOnWindows() {
	}


	@Override
	public void copyFiles( String pathIn, String pathOut ) throws IOException {
		File f = new File( pathIn );
		if (!f.exists()) {
			logger.error( "The source path does not exist." );
			System.exit( 5 );
		}
		//create the tomcat root directory
		File out = new File( pathOut );
		out.mkdir();

		doCopyFiles( Boolean.TRUE, pathIn, pathOut );
	}
	
	
	protected void doCopyFiles( boolean isRoot, String pathIn, String pathOut )
			throws IOException {

		File f = new File( pathIn );
		if (f.isFile()) {
			this.copyFile( pathIn, f, pathOut );
		} else {
			this.copyDir( pathIn, f, pathOut );
		}
	}


	@Override
	public void copyDir(String pathIn, File f, String pathOut ) throws IOException {
		String pathstr = f.getPath();
		if (pathstr.indexOf( "\\" ) > 0) {
			pathOut = pathOut
					+ pathstr.substring( pathstr.indexOf( pathIn )
							+ pathIn.length(), pathstr.length() );
		} 

		if(!f.getName().equals( Constants.TOMCAT_SUFFIX )){
			pathOut = pathOut+"\\"+f.getName();
			File dir = new File( pathOut );
			dir.mkdir();
		}
		
		File temp[] = f.listFiles();
		for (int i = 0; i < temp.length; i++) {
			this.doCopyFiles( Boolean.FALSE, temp[i].toString(), pathOut );
		}

	}


	@Override
	public void copyFile(String pathIn, File f, String pathOut ) throws IOException {
		DataInputStream in = new DataInputStream( new BufferedInputStream(
				new FileInputStream( f.getPath() ) ) );

		byte[] date = new byte[in.available()];

		in.read( date );

		DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
				new FileOutputStream( pathOut + "/" + f.getName() ) ) );
		out.write( date );
		in.close();
		out.close();
	}


	public static void copyWSFile( File f, String pathOut ) throws IOException {

		DataInputStream in = new DataInputStream( new BufferedInputStream(
				new FileInputStream( f.getPath() ) ) );

		byte[] date = new byte[in.available()];

		in.read( date );

		DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
				new FileOutputStream( pathOut ) ) );
		out.write( date );
		in.close();
		out.close();
	}
	
	@Override
	public void copyAppFile(File f, String pathOut) throws IOException {
		DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(f.getPath())));

        byte[] date = new byte[in.available()];

        in.read(date);
        
        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(pathOut))); 
        out.write(date);
        in.close();
        out.close();
	}	
	
	public static void main( String[] args ) throws IOException {
		FileReproduction fileReproduction = 
			FileManipulationFactory.newFileReproduction();
//		fileReproduction.copyFiles(Constants.TOMCAT_SOURCE_PATH, "C:\\SAManageTool\\SAfolder\\1-apache-tomcat-6.0.14" );
		
//		fileReproduction.copyAppFile(Constants.TOMCAT_SOURCE_PATH, "C:\\SAManageTool\\SAfolder\\0-apache-tomcat-6.0.14\\webapps\\axis2\\WEB-INF\\services\\" );
	}
}
