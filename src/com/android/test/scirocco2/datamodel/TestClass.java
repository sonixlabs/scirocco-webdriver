package com.android.test.scirocco2.datamodel;

import java.util.List;
import org.eclipse.jdt.junit.model.ITestElement.Result;

public class TestClass {
    
    private String className;
    
    private Result testResult;
    
    private String deviceId;
    
    private String osVersion;
    
    private String finished;
    
    private double elapsedTime;
    
    private List<TestCase> testCaseList;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Result getTestResult() {
        return testResult;
    }

    public void setTestResult(Result testResult) {
        this.testResult = testResult;
    }

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

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public void setTestCaseList(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }
}