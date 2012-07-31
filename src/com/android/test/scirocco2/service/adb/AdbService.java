package com.android.test.scirocco2.service.adb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.android.test.scirocco2.Activator;
import com.android.test.scirocco2.preference.Scirocco2PreferenceInitializer;
import com.android.test.scirocco2.util.CommandUtil;
import com.android.test.scirocco2.util.CommandUtil.Command;

public class AdbService {
    
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    private static final String CMD_DEVICES = "devices";
    
    public static List<String> getIds() {
        List<String> result = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(getAdbPath() + CMD_DEVICES);
            process.waitFor();
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i != 0 && line.length() > 6) {
                    result.add(line.replaceAll("device", "").trim());
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String getAdbPath() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String sdkLocation = store.getString(Scirocco2PreferenceInitializer.ANDROID_SDK_LOCATION);
        StringBuilder adbPath = new StringBuilder();
        adbPath.append(sdkLocation);
        adbPath.append(SEPARATOR);
        adbPath.append("platform-tools");
        adbPath.append(SEPARATOR);
        adbPath.append("adb ");
        return adbPath.toString();
    }
    
    public static String exeCommand(Command command, String[] param) {
        StringBuilder commandStr = new StringBuilder();
        commandStr.append(getAdbPath());
        commandStr.append(CommandUtil.getCommand(command, param));
        StringBuilder result = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(commandStr.toString());
            process.waitFor();
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
