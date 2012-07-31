package com.android.test.scirocco2.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

public class FileUtil {
    public static boolean fileCopy(String targetPath, String destPath) {
        try {
            File inputFile = new File(targetPath);
            File outputFile = new File(destPath);
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int line;
            while ((line = in.read()) != -1) {
              out.write(line);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean jarCopy(String targetPath, String destPath) {
        try {
            FileChannel in  = new FileInputStream(targetPath).getChannel();
            FileChannel out = new FileOutputStream(destPath).getChannel();
            in.transferTo(0,in.size(),out);
            in.close(); 
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static void makeDir(String path) {
        File file = new File(path);
        if(!file.exists()){ 
            file.mkdirs();
        }
    }
    
    public static boolean createSimpleFile(String path, String body) {
        File tempFile = new File(path);
        if(tempFile.exists()){ 
            tempFile.delete();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(body); 
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public static void delete(String path) {
        File file = new File(path);
        if(file.exists()){ 
            file.delete();
        }
    }
    
    public static FilenameFilter getFileExtensionFilter(String extension) {  
        final String _extension = extension;  
        return new FilenameFilter() {  
            public boolean accept(File file, String name) {  
                boolean ret = name.endsWith(_extension);   
                return ret;  
            }  
        };  
    }
    
    public static void createPropertiesFile(String destPath, Map<String, String> data) {
        try{
            File file = new File(destPath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            Iterator<String> mapIterator = data.keySet().iterator();
            while (mapIterator.hasNext()) {
                String key = mapIterator.next();
                Object value = data.get(key);
                bw.write(key);
                bw.write(" = ");
                bw.write(value.toString());
                bw.newLine();
            }
            bw.close();            
        } catch(IOException e){
            System.out.println(e);
        }
    }
    
    public static void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i=0; i<files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        path.delete();
    }
}