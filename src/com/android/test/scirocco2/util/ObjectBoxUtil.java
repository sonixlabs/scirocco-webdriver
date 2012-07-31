package com.android.test.scirocco2.util;

import com.android.test.scirocco2.extend.core.SciroccoLaunchShortcut;

public class ObjectBoxUtil {
    
    private static ObjectBoxUtil objectBank = null;
    
    private SciroccoLaunchShortcut launchShortcut;
    
    private ObjectBoxUtil() {}
    
    public static ObjectBoxUtil getInstance() {
        if (objectBank == null) {
            objectBank = new ObjectBoxUtil();    
        }
        return objectBank;
    }

    public SciroccoLaunchShortcut getLaunchShortcut() {
        return launchShortcut;
    }

    public void setLaunchShortcut(SciroccoLaunchShortcut launchShortcut) {
        this.launchShortcut = launchShortcut;
    }

}
