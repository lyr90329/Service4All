package aspecttry;
import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 *
 *Copyright
 */

public aspect fileIsolation{
public String refactor ="D:\\sandbox\\virtualstorage\\AccumulateRequirement.aar\\";
   // pointcut captureCallConsturctor(String value) : call(File.new(String))&& args(value);
    
    pointcut captureCallConsturctorFile(String parent,String child) : call(File.new(String,String))&& args(parent,child);

    pointcut captureCallConsturctorFileInStream(String name) :call(FileInputStream.new(String))&&args(name);
    
    pointcut captureCallConsturctorFileOutStream(String name) :call(FileOutputStream.new(String))&&args(name);
    
    pointcut captureCallConsturctorFileOutStream2(String name,boolean append) :call(FileOutputStream.new(String,boolean))&&args(name,append);
    
    pointcut captureCallConsturctorFileWriter(String filename):call(FileWriter.new(String))&&args(filename);
    
    pointcut captureCallConsturctorFileWriter2(String filename,boolean append):call(FileWriter.new(String,boolean))&&args(filename,append);
    
public fileIsolation(){
    
    }
   
    //Advice declaration
/*    before(String value) : captureCallConsturctor(value) {
        System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(value);
        System.out.println("------------------------------------------");
    }
    
    Object around(String value) : captureCallConsturctor(value)
    {
       //System.out.println(value);
       value = value.replace(":", "");
       return new File(value);
    }*/
    
    
    before(String parent,String child):captureCallConsturctorFile(parent,child){
    
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(parent+"..."+child);
        System.out.println("------------------------------------------");
    }
    
    Object around(String parent,String child) throws IOException : captureCallConsturctorFile(parent,child)
    {
      
       String value = parent+child;
       value = value.replace(":", "");
       String path = refactor+value;
       File f = new File(path);
       f.getParentFile().mkdirs();
       return f;
    }
    
    before(String name):captureCallConsturctorFileInStream(name){
   
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(name);
        System.out.println("------------------------------------------");
    }
    
    Object around(String name) throws FileNotFoundException:captureCallConsturctorFileInStream(name){
    
    	name = name.replace(":", "");
    	File f = new File(refactor+name);
    	(new File(f.getParent())).mkdirs();
        System.out.println(f.getParent());
    	return new FileInputStream(f);
    }
    
    before(String name):captureCallConsturctorFileOutStream(name){
    
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(name);
        System.out.println("------------------------------------------");
    }
    
    Object around(String name) throws FileNotFoundException:captureCallConsturctorFileOutStream(name){
 
    	name = name.replace(":", "");
    	File f = new File(refactor+name);
    	(new File(f.getParent())).mkdirs();
    	return new FileOutputStream(f);
    }
    
    before(String name,boolean append):captureCallConsturctorFileOutStream2(name,append){
    
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(name);
        System.out.println("------------------------------------------");
    }
    
    Object around(String name,boolean append) throws FileNotFoundException:captureCallConsturctorFileOutStream2(name,append){
    
    	name = name.replace(":", "");
    	File f = new File(refactor+name);
    	(new File(f.getParent())).mkdirs();
    	return new FileOutputStream(f,append);
    }
    
    before(String filename):captureCallConsturctorFileWriter(filename){
  
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(filename);
        System.out.println("------------------------------------------");
    }
    
    Object around(String filename)throws Exception:captureCallConsturctorFileWriter(filename){
    	
    	filename = filename.replace(":", "");
    	File f = new File(refactor+filename);
    	(new File(f.getParent())).mkdirs();
    	return new FileWriter(f);
    }
    
    
    before(String filename,boolean append):captureCallConsturctorFileWriter2(filename,append){
    
    	System.out.println("------------------- Aspect Advice Logic -------------------");
        System.out.println("In the advice attached to the call point cut");
        System.out.println(filename);
        System.out.println("------------------------------------------");
    }
    
    Object around(String filename,boolean append)throws Exception:captureCallConsturctorFileWriter2(filename,append){
    	
    	filename = filename.replace(":", "");
    	File f = new File(refactor+filename);
    	(new File(f.getParent())).mkdirs();
    	return new FileWriter(f,append);
    }   
}
