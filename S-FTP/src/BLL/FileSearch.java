package BLL;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JOptionPane;

import UI.MainForm;

public class FileSearch {
	 public static void bfsSearchFile(String path,String regex,boolean isDisplyDir,boolean isDisplayFile)  
	    {  
		    boolean isFind = false;
		    regex = "(.*)" + regex + "(.*)";
	        if(!(isDisplayFile||isDisplyDir))  
	        {  
	            throw new IllegalArgumentException("isDisplyDir和isDisplayFile中至少要有一个为true");  
	        }  
	        Queue<File> queue=new LinkedList<>();  
	        
	        File[] fs=new File(path).listFiles();  
	        //遍历第一层  
	        for(File f:fs)  
	        {  
	            //把第一层文件夹加入队列  
	            if(f.isDirectory())  
	            {  
	                queue.offer(f);  
	            }  
	            else  
	            {  
	                if(f.getName().matches(regex)&&isDisplayFile)  
	                {  
	                	isFind = true;
	                	MainForm._instance.Maps.put(f.getName(), f.getAbsolutePath());
	                	MainForm._instance.defaultListModel.addElement(f.getName());
	                	MainForm._instance.AllIcons[MainForm._instance.Icon_Counter++] = GetFileIcon.getSingleSmallIcon(f.getAbsolutePath());
	                }  
	            }  
	        }  
	        //逐层搜索下去  
	        while (!queue.isEmpty()) {  
	            File fileTemp=queue.poll();//从队列头取一个元素  
	            if(isDisplyDir)  
	            {  
	                if(fileTemp.getName().matches(regex))  
	                {  
	                	isFind = true;
	                	MainForm._instance.Maps.put(fileTemp.getName(), fileTemp.getAbsolutePath());
	                	MainForm._instance.defaultListModel.addElement(fileTemp.getName());
	                	MainForm._instance.AllIcons[MainForm._instance.Icon_Counter++] = GetFileIcon.getSingleSmallIcon(fileTemp.getAbsolutePath());
	                }  
	            }  
	              
	            File[] fileListTemp=fileTemp.listFiles();  
	            if(fileListTemp==null)  
	                continue;//遇到无法访问的文件夹跳过  
	            for(File f:fileListTemp)  
	            {  
	                if(f.isDirectory())  
	                {  
	                    queue.offer(f);////从队列尾插入一个元素  
	                }  
	                else  
	                {  
	                    if(f.getName().matches(regex)&&isDisplayFile)  
	                    {  
	                    	isFind = true;
	                    	MainForm._instance.Maps.put(f.getName(), f.getAbsolutePath());
		                	MainForm._instance.defaultListModel.addElement(f.getName());
		                	MainForm._instance.AllIcons[MainForm._instance.Icon_Counter++] = GetFileIcon.getSingleSmallIcon(f.getAbsolutePath());
	                    }  
	                }  
	            }  	              
	        }  
	        if(!isFind){
	        	 JOptionPane.showMessageDialog(null, "未找到相关结果", "确认对话框", JOptionPane.YES_OPTION);
	        }
	    }
}
