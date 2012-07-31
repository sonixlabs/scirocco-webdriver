package com.android.test.scirocco2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.android.test.scirocco2.Activator;

public class PropUtil {
    
    public static final String SEPARATOR = System.getProperty("file.separator");
    
    private final static String PLUGIN_ID = Activator.PLUGIN_ID;
    
    private final static String PROPERTIES_FILE = "resources" + SEPARATOR +"scirocco.properties";
    
    private final static String RESOURCES_DIR = "resources" + SEPARATOR;
    
    private final static String LIB_DIR = "lib";
    
    public static String getProperty(String key, String[] strs) {
        String result = PropUtil.getProperty(key);
        for (int i = 0; i < strs.length; i++) {
            result = result.replace("{" + i +"}", strs[i]);  
        }
        return result;
    }
    
    public static String getProperty(String key) {
        Properties properties = new Properties();
        try {
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            URL fileURL = bundle.getEntry(PROPERTIES_FILE);
            File propFile = new File(FileLocator.resolve(fileURL).toURI());
            InputStream inputStream = new FileInputStream(propFile);
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }
    
    public static String getResourcesPath() {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        URL fileURL = bundle.getEntry(RESOURCES_DIR);
        String resourcePath = new String();
        try {
            resourcePath = FileLocator.resolve(fileURL).getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourcePath;
    }
    
    public static String getResourcesPath(String name) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        URL fileURL = bundle.getEntry(RESOURCES_DIR + name);
        String resourcePath = new String();
        try {
            resourcePath = FileLocator.resolve(fileURL).getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourcePath;
    }
    
    public static String getLibDirPath() {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        URL fileURL = bundle.getEntry(LIB_DIR);
        String path = new String();
        try {
            path = FileLocator.resolve(fileURL).getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
