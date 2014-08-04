package cn.edu.buaa.act.service4all.bpmnexecution.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class IDGenerator {
	
	private static String file = "id.properties";
	private static long id;
	
	public static long generateID(){
		try {
			init();
			id++;
			close();
			return id;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return UUID.randomUUID().node();
	}
	
	private static void init() throws FileNotFoundException, IOException{
		File pFile = new File(file);
		if(!pFile.exists()){
			pFile.createNewFile();
			
		}else{
			Properties props = new Properties();
			props.load(new FileInputStream(pFile));
			id = Long.valueOf((String)props.get("id"));
		}
		
	}
	
	private static void close() throws FileNotFoundException, IOException{
		File pFile = new File(file);
		if(!pFile.exists()){
			pFile.createNewFile();
			
		}
		Properties props = new Properties();
		//props.load(new FileInputStream(pFile));
		props.setProperty("id", String.valueOf(id));
		
		props.store(new FileOutputStream(pFile), null);
		
		
	}

}
