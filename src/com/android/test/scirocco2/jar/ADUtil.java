package com.android.test.scirocco2.jar;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ADUtil {
    private static final String SEPARATOR = System.getProperty("file.separator");
	
    private static int sequenceNo = 1;

	private static String preMethodName;

	public static void getScreenShot(WebDriver driver) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		updateScreenshotSequenceNo(element);
		preMethodName = element.getMethodName();
		StringBuilder screenshotPath = new StringBuilder();
		screenshotPath.append(element.getClassName().substring(element.getClassName().lastIndexOf(".") + 1));
		screenshotPath.append("_");
		screenshotPath.append(element.getMethodName());
		screenshotPath.append("_");
		screenshotPath.append(Integer.toString(sequenceNo));
		screenshotPath.append(".png");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	    try {
			FileUtils.copyFile(scrFile, new File(getImgDirPath() + screenshotPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getImgDirPath() {
	    StringBuilder dirPath = new StringBuilder();
	    dirPath.append(System.getProperty("user.dir"));
	    dirPath.append(SEPARATOR + "report" + SEPARATOR + "worktemp" + SEPARATOR + "images" + SEPARATOR);
        return dirPath.toString();
	}
    
	private static void updateScreenshotSequenceNo(StackTraceElement element) {
		if (isScreenShotMethodChanged(element)) {
			sequenceNo = 1;
		} else {
			sequenceNo++;
		}
	}

	private static boolean isScreenShotMethodChanged(StackTraceElement element){
		if (element.getMethodName().equals(preMethodName)) {
			return false;
		}
		return true;
	}
}
