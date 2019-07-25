package BLL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileTime {
	
	/** 
     * 读取文件创建时间 
     */  
    public static String getCreateTime(String filePath){            
    	Path p = Paths.get(filePath);    			 		 
        BasicFileAttributes att;
        Date time = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			att = Files.readAttributes(p, BasicFileAttributes.class);		
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(att.creationTime().toMillis());
			time = calendar.getTime();			     			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//获取文件的属性
		return formatter.format(time);            
    }
    
    public static String getModifiedTime(String filePath){  
        File f = new File(filePath);              
        Calendar cal = Calendar.getInstance();  
        long time = f.lastModified();  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");              
        return formatter.format(cal.getTime());         
    }  
    
    public static String getLatestAccessTime(String filePath){
    	Path p = Paths.get(filePath);    			 		 
        BasicFileAttributes att;
        Date time = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                            
		try {
			att = Files.readAttributes(p, BasicFileAttributes.class);		
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(att.lastAccessTime().toMillis());
			time = calendar.getTime();			     			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//获取文件的属性
		return formatter.format(time);   
    }	
}
