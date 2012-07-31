package com.android.test.scirocco2.datamodel;

public class DeviceInfo {
    
    private String deviceId;
    
    private String osVersion;
    
    private String deviceModel;
    
    private boolean isTested;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public boolean isTested() {
        return isTested;
    }

    public void setTested(boolean isTested) {
        this.isTested = isTested;
    }

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

}
