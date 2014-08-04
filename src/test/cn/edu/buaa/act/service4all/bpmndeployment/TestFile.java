package test.cn.edu.buaa.act.service4all.bpmndeployment;

import java.io.File;
import java.io.IOException;

public class TestFile {
	
	public void testDelete() throws IOException{
		File tmp = new File("/tmp.xml");
		if(!tmp.exists()){
			System.out.println("Create a new file : " + tmp.getAbsolutePath());
			tmp.createNewFile();
		}
		
		//remove the tmp file by filesystem api
		tmp.delete();
	}
	
	public static void main(String[] args){
		TestFile test = new TestFile();
		try {
			test.testDelete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
