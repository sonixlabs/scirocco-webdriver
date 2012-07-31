package com.android.test.scirocco2.datamodel;

import java.util.List;
import org.eclipse.jdt.junit.model.ITestElement.Result;

public class TestCase {
    
    private String methodName;
    
    private Result testResult;
    
    private String failureTrace;
    
    private List<TestImage> testImageList;

    private double elapsedTime;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Result getTestResult() {
        return testResult;
    }

    public void setTestResult(Result testResult) {
        this.testResult = testResult;
    }

    public String getFailureTrace() {
        return failureTrace;
    }

    public void setFailureTrace(String failureTrace) {
        this.failureTrace = failureTrace;
    }

    public List<TestImage> getTestImageList() {
        return testImageList;
    }

    public void setTestImageList(List<TestImage> testImageList) {
        this.testImageList = testImageList;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
