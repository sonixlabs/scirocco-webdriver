package com.android.test.scirocco2.util;

import com.android.test.scirocco2.extend.core.SciroccoLaunchShortcut;

public class ReferenceUtil {
    
    private static ReferenceUtil object = null;
    
    private SciroccoLaunchShortcut launchShortcut;
    
    private ReferenceUtil() {}
    
    public static ReferenceUtil getInstance() {
        if (object == null) {
            object = new ReferenceUtil();    
        }
        return object;
    }

    public SciroccoLaunchShortcut getLaunchShortcut() {
        return launchShortcut;
    }

    public void setLaunchShortcut(SciroccoLaunchShortcut launchShortcut) {
        this.launchShortcut = launchShortcut;
    }

}
