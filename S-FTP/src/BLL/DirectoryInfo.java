package BLL;

import java.io.File;

public class DirectoryInfo {
	 public static DirectoryInfo _instance = null;
	 public long File_Num, Directory_Num;
	 public DirectoryInfo(){
		 this._instance = this;
		 File_Num = 0;
		 Directory_Num = 0;
	 }
	
	 public long getDirSize(File file) {     
	        //判断文件是否存在     
	        if (file.exists()) {     
	            //如果是目录则递归计算其内容的总大小    
	            if (file.isDirectory()){     	            	
	            	this.Directory_Num++;
	                File[] children = file.listFiles();     
	                long size = 0;     
	                for (File f : children) 
	                    size += getDirSize(f);     
	                return size;     
	            } else {
	            	this.File_Num++;
	                long size = file.length();        
	                return size;     
	            }     
	        } else {     
	            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");     
	            return 0;     
	        }     
	    }     
}
