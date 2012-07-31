package com.android.test.scirocco2.extend.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.android.test.scirocco2.datamodel.DeviceInfo;
import com.android.test.scirocco2.service.adb.AdbService;
import com.android.test.scirocco2.service.device.InitialService;
import com.android.test.scirocco2.util.CommandUtil.Command;
import com.android.test.scirocco2.util.ReferenceUtil;

public class SciroccoLaunchShortcut extends JUnitLaunchShortcut {
    
    private List<DeviceInfo> deviceInfoList;
    
    private DeviceInfo currentDeviceInfo;

    @Override
    public void launch(IEditorPart editor, String mode) {
        super.launch(editor, mode);
    }

    @Override
    public void launch(ISelection selection, String mode) {
        super.launch(selection, mode);
        ReferenceUtil.getInstance().setLaunchShortcut(null);
        deviceInfoList = null;
        List<DeviceInfo> deviceInfoList = getDeviceInfoList();
        if (deviceInfoList.size() > 0) {
            DeviceInfo deviceInfo = deviceInfoList.get(0);
            InitialService initializer = new InitialService(getLaunchableResource(selection));
            initializer.initTestEnvironment(deviceInfo);
            deviceInfo.setTested(true);
            currentDeviceInfo = deviceInfo; 
        }
    }
    
    public void reLaunch(ISelection selection, String mode) {
        super.launch(selection, mode);
        List<DeviceInfo> deviceInfoList = getDeviceInfoList();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            if (!deviceInfo.isTested()) {
                InitialService initializer = new InitialService(getLaunchableResource(selection));
                initializer.initTestEnvironment(deviceInfo);
                deviceInfo.setTested(true);
                currentDeviceInfo = deviceInfo;
                break;
            }
        }
    }

    public List<DeviceInfo> getDeviceInfoList() {
        if (deviceInfoList == null) {
            deviceInfoList = new ArrayList<DeviceInfo>();
            for (String id : AdbService.getIds()) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceId(id);
                deviceInfo.setOsVersion(AdbService.exeCommand(Command.CMD_VERSION, new String[]{id}));
                deviceInfo.setDeviceModel(AdbService.exeCommand(Command.CMD_DEVICE_MODEL, new String[]{id}));
                deviceInfoList.add(deviceInfo);
            }
            ReferenceUtil.getInstance().setLaunchShortcut(this);
        }
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<DeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public DeviceInfo getCurrentDeviceInfo() {
        return currentDeviceInfo;
    }
}
