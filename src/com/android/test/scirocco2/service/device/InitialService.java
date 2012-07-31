package com.android.test.scirocco2.service.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IResource;

import com.android.test.scirocco2.datamodel.DeviceInfo;
import com.android.test.scirocco2.service.adb.AdbService;
import com.android.test.scirocco2.util.CommandUtil.Command;
import com.android.test.scirocco2.util.PathUtil;

public class InitialService {
    
    private static final String FB2PNG = "fb2png";
    
    private static final String APK_OLD = "android-server-2.6.0.apk";
    
    private static final String APK_NEW = "android-server-2.13.0.apk";
    
    private static final String PROJECT_PROPERTIES = "scirocco.properties";
    
    private static final String TARGET_APK_KEY = "target_apk";
    
    private IResource targetResource;
    
    private String testTargetId;
    
    public InitialService(IResource targetResource) {
        this.targetResource = targetResource;
    }
    
    public boolean initTestEnvironment(DeviceInfo deviceInfo) {
        // 端末初期化
        AdbService.exeCommand(Command.CMD_INIT, new String[]{deviceInfo.getDeviceId(), getTestTargetId()});
        AdbService.exeCommand(Command.CMD_FW_APP, new String[]{deviceInfo.getDeviceId()});
        if(!isInstalledWebApk(deviceInfo.getDeviceId())) {
            // 端末バージョン取得
            String osVersion = deviceInfo.getOsVersion().substring(0, 3);
            // android-server.apk絶対パスを取得
            String apkPath = getApkPath(osVersion);
            AdbService.exeCommand(Command.CMD_INSTALL, new String[]{deviceInfo.getDeviceId(), apkPath});
        }
        AdbService.exeCommand(Command.CMD_INIT_WEB, new String[]{deviceInfo.getDeviceId()});
        AdbService.exeCommand(Command.CMD_FW_WEB, new String[]{deviceInfo.getDeviceId()});
        AdbService.exeCommand(Command.CMD_INST_FB2, new String[]{deviceInfo.getDeviceId(), PathUtil.getResourcesPath(FB2PNG)});
        AdbService.exeCommand(Command.CMD_INIT_FB2, new String[]{deviceInfo.getDeviceId()});
        return true;
    }
    
    private boolean isInstalledWebApk(String deviceId) {
        String result = AdbService.exeCommand(Command.CMD_INIT_WEB, new String[]{deviceId});
        if (result.matches(".*Error:.*")) {
            return false;
        }
        return true;
    }
    
    private String getApkPath(String osVersion) {
        String path = new String();
        if ("2.2".equals(osVersion) || "2.3".equals(osVersion)) {
            path = PathUtil.getResourcesPath(APK_OLD);
        } else {
            path = PathUtil.getResourcesPath(APK_NEW);
        }
        return path;
    }
    
    private String getTestTargetId() {
        if (this.testTargetId == null) {
            // テストプロジェクトpropertiesから取得
            StringBuilder propertiesPath = new StringBuilder();
            propertiesPath.append(targetResource.getProject().getLocationURI().getPath());
            propertiesPath.append(System.getProperty("file.separator"));
            propertiesPath.append(PROJECT_PROPERTIES);
            Properties configuration = new Properties();
            try {
                InputStream inputStream = new FileInputStream(new File(propertiesPath.toString()));
                configuration.load(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.testTargetId = configuration.getProperty(TARGET_APK_KEY); 
        }
        return this.testTargetId;
    }
}
