package com.android.test.scirocco2.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.android.test.scirocco2.Activator;

public class PathUtil {
    
    private static final String RESOURCES_DIR = "resources" + System.getProperty("file.separator");
    
    private static final String LIB_DIR = "lib";
    
    public static String getResourcesPath() {
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
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
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
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
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
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
