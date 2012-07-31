package com.android.test.scirocco2.jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;

public class NDUtil {
    private static final String SEPARATOR = System.getProperty("file.separator");
	private static int sequenceNo = 1;
	private static String preMethodName;
	private static String deviceId;
    
    public static AndroidNativeDriver getDriver() {
        // get current test device id
        try {
            BufferedReader in = 
                    new BufferedReader(new FileReader(System.getProperty("user.dir").concat(System.getProperty("file.separator") + "runtemp.tmp")));
            String line;
            while ((line = in.readLine()) != null) {
                deviceId = line;
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return new AndroidNativeDriverBuilder().withDefaultServer().build();
	}
     
	public static void getScreenShot(AndroidNativeDriver driver) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		updateScreenshotSequenceNo(element);
		preMethodName = element.getMethodName();
		StringBuilder screenshotPath = new StringBuilder();
		screenshotPath.append(getImgDirPath());
		screenshotPath.append(element.getClassName().substring(element.getClassName().lastIndexOf(".") + 1));
		screenshotPath.append("_");
		screenshotPath.append(element.getMethodName());
		screenshotPath.append("_");
		screenshotPath.append(Integer.toString(sequenceNo));
		screenshotPath.append(".png");
        execAdbCommand("shell /data/local/fb2png /data/local/tmp.png");
        execAdbCommand("pull /data/local/tmp.png " + screenshotPath);
	}
	
    private static String getImgDirPath() {
        StringBuilder dirPath = new StringBuilder();
        dirPath.append(System.getProperty("user.dir"));
        dirPath.append(SEPARATOR + "report" + SEPARATOR + "worktemp" + SEPARATOR + "images" + SEPARATOR);
        return dirPath.toString();
    }
	
    private static String getAdbPath() {
		String userDir = System.getProperty("user.dir");
		Properties configuration = new Properties();
    	// scirocco.propertiesからadbパスを読み込む
    	try {
    	      InputStream inputStream = 
    	    		  new FileInputStream(new File(userDir + SEPARATOR + "scirocco.properties"));
    	      configuration.load(inputStream);
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	    }
        return configuration.getProperty("adb_path");
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
	
    private static String execAdbCommand(String cmd) {
        String adbLocation = getAdbPath();
        String result = null;
        try {
            cmd = adbLocation + " -s " + deviceId + " " + cmd;
            result = execCommand(cmd.split(" "));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static String execCommand(String[] cmds) throws IOException, InterruptedException {
        ProcessBuilder b = new ProcessBuilder(cmds);
        //標準エラー出力をマージして出力する
        b.redirectErrorStream(true);
        Process p = b.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuffer ret = new StringBuffer();
        String line = null;
        //標準エラー出力が標準出力にマージして出力されるので、標準出力だけ読み出せばいい
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }
        return ret.toString();
    }
}