package BLL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GetFileNames
{
    public static String [] getFileName(String path)//得到目录下第一级的文件/文件夹名
    {
        File file = new File(path);
        String [] fileName = file.list();
        return fileName;
    }
    public static void getAllFileName(String path,ArrayList<String> fileName)//得到目录下所有的文件/文件夹名
    {
        File file = new File(path);
        File [] files = file.listFiles();
        String [] names = file.list();
        if(names != null)
        fileName.addAll(Arrays.asList(names));
        for(File a:files)
        {
            if(a.isDirectory())
            {
                getAllFileName(a.getAbsolutePath(),fileName);
            }
        }
    }
}