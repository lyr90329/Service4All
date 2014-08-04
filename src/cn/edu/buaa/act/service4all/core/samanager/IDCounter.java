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
package cn.edu.buaa.act.service4all.core.samanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * the job counter for the job created during processing the request
 * 
 * @author Enqu
 * 
 */
public class IDCounter {

	private static final Log log = LogFactory.getLog( IDCounter.class );

	private static long counter = 0;

	private static final String COUNTER = "counter";

	private static String tmpFile = "jobcounter.properties";


	/**
	 * read the counter's value when the component starts
	 * 
	 */
	public synchronized static void init() throws IOException {
		File tmp = new File( tmpFile );
		if (!tmp.exists()) {
			log.info( "The properties file does not exist, so create a new one" );
			tmp.createNewFile();
			counter = 1;
		} else {
			FileInputStream input = new FileInputStream( tmp );
			Properties props = new Properties();
			props.load( input );
			if (props.getProperty( COUNTER ) != null) {
				counter = new Long( props.getProperty( COUNTER ) );
				log.info( "Get the job counter: " + counter );
			} else {
				counter = 1;
			}
			input.close();
		}
	}


	public synchronized static long generateJobID() {
		if (counter <= 0) {
			counter = 1;
		}
		counter++;
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return counter;

	}


	public static long getCurJobID() {
		return counter;
	}


	public synchronized static void close() throws IOException {

		File tmp = new File( tmpFile );
		if (!tmp.exists()) {
			log.warn( "The tmp file does not exist, so there must be some error when counter initiates" );
			throw new FileNotFoundException();
		}
		FileOutputStream output = new FileOutputStream( tmp );
		Properties props = new Properties();
		props.setProperty( COUNTER, String.valueOf( counter ) );
		props.store( output, "The job counter" );
		output.close();

	}
}
