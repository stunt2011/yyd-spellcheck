package com.yyd.spellchecker.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	public static String getResourcePath() {		
		String rootPath = ClassLoader.getSystemResource("").getPath();
		rootPath = rootPath.substring(1);//去掉开头的"/"，c++类的模块会无法加载对应的文件
		return rootPath;
	}

	public static String buildFilePath(String path) throws Exception {
		String entireFilePath = getResourcePath() + path;		
		return entireFilePath;
	}
	

	/**
	 * 
	 * @param path：可以是目录也可以是文件
	 * @return
	 */
	public static List<String> read(String filePath){
		List<String> dataList = new ArrayList<String>();
		
		//判断文件是否存在
		File file = new File(filePath);
		if(!file.exists()){
			return dataList;
		}
		
		if(!file.isFile()){
			return dataList;
		}
		
		loadFile(file,dataList);
		
		return dataList;		
	}
	
	private static  boolean loadFile(File file,List<String> dataList){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
					
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(fis, "UTF-8");			 
			BufferedReader br = new BufferedReader(isr); 
		     String line = null; 
		     try {
		    	 	while ((line = br.readLine()) != null) { 
		    	  		if(!line.isEmpty()){
		    	  			dataList.add(line);
		    	  		}
		    	  	}
		     } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		     } 
		      
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	public static void write(String filePath,List<String> dataList) throws UnsupportedEncodingException{
		FileOutputStream writerStream = null;
		try {
			writerStream = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));

		try {
				int count = dataList.size();
				for(int i = 0;i < count;i++){
					String term = dataList.get(i);
					writer.write(term);					
					if(i < count-1){
						writer.write("\n");
					}
				}
				
				
		} 
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
