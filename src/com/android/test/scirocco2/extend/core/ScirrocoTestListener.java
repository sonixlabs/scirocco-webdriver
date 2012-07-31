package com.android.test.scirocco2.extend.core;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.android.test.scirocco2.datamodel.DeviceInfo;
import com.android.test.scirocco2.datamodel.TestCase;
import com.android.test.scirocco2.datamodel.TestClass;
import com.android.test.scirocco2.datamodel.TestImage;
import com.android.test.scirocco2.service.adb.AdbService;
import com.android.test.scirocco2.service.report.ReportService;
import com.android.test.scirocco2.util.CommandUtil.Command;
import com.android.test.scirocco2.util.FileUtil;
import com.android.test.scirocco2.util.PathUtil;
import com.android.test.scirocco2.util.ReferenceUtil;

public class ScirrocoTestListener extends TestRunListener {
    
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    
    private static final String REPORT_ROOT = "report";
    
    private static final String REPORT_OS = "_OS";
    
    private static final String REPORT_BASE = "worktemp";
    
    private static final String REPORT_CSS = "css";
    
    private static final String CSS_NAME = "scirocco.css";
    
    private static final String REPORT_JS = "js";
    
    private static final String JS_NAME = "scirocco.js";
    
    private static final String REPORT_IMG = "images";
    
    private static final String TEMP_FILE = "runtemp.tmp";
    
    private String targetProjectDir;
    
    private List<TestClass> testClassList;
    
    private TestClass testClass;
    
    private List<TestCase> testCaseList;
    
    private List<TestImage> testImageList;
    
    @Override
    public void sessionLaunched(ITestRunSession session) {
        super.sessionLaunched(session);
        // initialize report
        targetProjectDir = session.getLaunchedProject().getResource().getLocationURI().getPath();
        StringBuilder path = new StringBuilder();
        path.append(targetProjectDir);
        path.append(SEPARATOR + REPORT_ROOT);
        FileUtil.makeDir(path.toString());
        path.append(SEPARATOR + REPORT_BASE);
        FileUtil.deleteDirectory(new File(path.toString()));
        FileUtil.makeDir(path.toString());
        String cssDir = path.toString() + SEPARATOR + REPORT_CSS;
        FileUtil.makeDir(cssDir);
        FileUtil.fileCopy(PathUtil.getResourcesPath(CSS_NAME), cssDir + SEPARATOR + CSS_NAME);
        String jsDir = path.toString() + SEPARATOR + REPORT_JS;
        FileUtil.makeDir(jsDir);
        FileUtil.fileCopy(PathUtil.getResourcesPath(JS_NAME), jsDir + SEPARATOR + JS_NAME);
        path.append(SEPARATOR + REPORT_IMG);
        FileUtil.makeDir(path.toString());
        // initialize runtemp (for screen shot)
        String tempFilePath = targetProjectDir + SEPARATOR + TEMP_FILE;
        SciroccoLaunchShortcut launchShortcut = ReferenceUtil.getInstance().getLaunchShortcut();
        DeviceInfo deviceInfo = launchShortcut.getCurrentDeviceInfo();
        FileUtil.createSimpleFile(tempFilePath, deviceInfo.getDeviceId());
    }
    
    @Override
    public void sessionStarted(ITestRunSession session) {
        super.sessionStarted(session);
        testClassList = new ArrayList<TestClass>();
        testImageList = new ArrayList<TestImage>();
    }
    
    @Override
    public void testCaseStarted(ITestCaseElement testCaseElement) {
        super.testCaseStarted(testCaseElement);
        if (testClass == null) {
            testClass = new TestClass();
            testClass.setClassName(testCaseElement.getTestClassName().substring(testCaseElement.getTestClassName().lastIndexOf(".") + 1));
            testCaseList = new ArrayList<TestCase>();
        }
        if(!testClass.getClassName().equals(testCaseElement.getTestClassName().substring(testCaseElement.getTestClassName().lastIndexOf(".") + 1))) {
            // set and add
            testClass.setTestCaseList(testCaseList);
            testClass.setDeviceId(ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getDeviceId());
            testClass.setOsVersion(ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getOsVersion());
            testClass.setElapsedTime(getClassElapsedTime());
            testClass.setTestResult(getClassTestResult());
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            testClass.setFinished(dateFormat.format(new Date()));
            testClassList.add(testClass);
            // initialize class and case list
            testClass = new TestClass();
            testClass.setClassName(testCaseElement.getTestClassName().substring(testCaseElement.getTestClassName().lastIndexOf(".") + 1));
            testCaseList = new ArrayList<TestCase>();
        }
    }
    
    @Override
    public void testCaseFinished(ITestCaseElement testCaseElement) {
        super.testCaseFinished(testCaseElement);
        // add test case to test case list
        TestCase testCase = new TestCase();
        if (testCaseElement.getTestResult(false) != Result.OK) {
            // screen shot
            StringBuilder screenshotPath = new StringBuilder();
            screenshotPath.append(targetProjectDir);
            screenshotPath.append(SEPARATOR + REPORT_ROOT);
            screenshotPath.append(SEPARATOR + REPORT_BASE);
            screenshotPath.append(SEPARATOR + REPORT_IMG + SEPARATOR);
            screenshotPath.append(testCaseElement.getTestClassName().substring(testCaseElement.getTestClassName().lastIndexOf(".") + 1));
            screenshotPath.append("_");
            screenshotPath.append(testCaseElement.getTestMethodName());
            screenshotPath.append("_");
            screenshotPath.append(0);
            screenshotPath.append(".png");
            AdbService.exeCommand(Command.CMD_TAKE_FB2, new String[]{ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getDeviceId()});
            AdbService.exeCommand(Command.CMD_COPY_FB2, new String[]{ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getDeviceId(), screenshotPath.toString()});
            testCase.setFailureTrace(testCaseElement.getFailureTrace().getTrace());
        } else {
            testCase.setFailureTrace("-");
        }
        testCase.setTestImageList(new ArrayList<TestImage>());
        testCase.setMethodName(testCaseElement.getTestMethodName());
        testCase.setTestResult(testCaseElement.getTestResult(false));
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        String formatString = decimalFormat.format(testCaseElement.getElapsedTimeInSeconds());
        testCase.setElapsedTime(Double.parseDouble(formatString));
        testCaseList.add(testCase);
    }
    
    @Override
    public void sessionFinished(ITestRunSession session) {
        super.sessionFinished(session);
        // テスト結果報告書作成
        outputReport(session);
        // next test
        final ReferenceUtil objectBoxUtil = ReferenceUtil.getInstance();
        List<DeviceInfo> deviceInfoList = objectBoxUtil.getLaunchShortcut().getDeviceInfoList();
        if (deviceInfoList != null) {
            boolean testedFlag = false;
            for (final DeviceInfo deviceInfo : deviceInfoList) {
                if (!deviceInfo.isTested()) {
                    testedFlag = deviceInfo.isTested();
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            AdbService.exeCommand(Command.CMD_FW_APP, new String[]{deviceInfo.getDeviceId()});
                            AdbService.exeCommand(Command.CMD_FW_WEB, new String[]{deviceInfo.getDeviceId()});
                            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                            ISelectionService selectionService = workbenchWindow.getSelectionService();
                            objectBoxUtil.getLaunchShortcut().reLaunch(selectionService.getSelection(), "run");
                        }
                    });
                    break;
                }
                testedFlag = deviceInfo.isTested();
            }
            // テスト済みを初期化（正常に全てのテストが終わった時）
            if (testedFlag) {
                ReferenceUtil.getInstance().getLaunchShortcut().setDeviceInfoList(null);
            }
        }
    }
    
    private double getClassElapsedTime() {
        double result = 0;
        for (TestCase testCase : testCaseList) {
            result += testCase.getElapsedTime();
        }
        DecimalFormat decimalFormat = new DecimalFormat(".#");
        String formatString = decimalFormat.format(result);
        return Double.parseDouble(formatString);
    }
    
    private Result getClassTestResult() {
        for (TestCase testCase : testCaseList) {
            if (testCase.getTestResult() != Result.OK) {
                return Result.FAILURE;
            }
        }
        return Result.OK;
    }
    
    private void outputReport(ITestRunSession session) {
        testClass.setTestCaseList(testCaseList);
        testClass.setDeviceId(ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getDeviceId());
        testClass.setOsVersion(ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo().getOsVersion());
        testClass.setElapsedTime(getClassElapsedTime());
        testClass.setTestResult(getClassTestResult());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        testClass.setFinished(dateFormat.format(new Date()));
        testClassList.add(testClass);
        // Screen Shot Utilが取った画像を取得
        StringBuilder imgDir = new StringBuilder();
        imgDir.append(targetProjectDir);
        imgDir.append(SEPARATOR + REPORT_ROOT);
        imgDir.append(SEPARATOR + REPORT_BASE);
        imgDir.append(SEPARATOR + REPORT_IMG);
        File dir = new File(imgDir.toString());
        String[] filesList = dir.list(FileUtil.getFileExtensionFilter(".png"));
        TestImage failImage = null;
        for (String file : filesList) {
            String imgInfoStr= file.substring(0, file.lastIndexOf("."));
            String[] imgInfo = imgInfoStr.split("_");
            TestImage testImage = new TestImage();
            testImage.setClassName(imgInfo[0]);
            testImage.setMethodName(imgInfo[1]);
            testImage.setSeqNo(Integer.parseInt(imgInfo[2]));
            testImage.setImageUrl(REPORT_IMG + SEPARATOR + file);
            if (testImage.getSeqNo() == 0) {
                failImage = new TestImage();
                failImage.setClassName(testImage.getClassName());
                failImage.setMethodName(testImage.getMethodName());
                failImage.setSeqNo(testImage.getSeqNo());
                failImage.setImageUrl(testImage.getImageUrl());
            } else {
                testImageList.add(testImage);
            }
        }
        if (failImage != null) {
            testImageList.add(failImage);
        }
        // 画像と結果merge
        for (TestClass testClass : testClassList) {
            for (TestImage testImage : testImageList) {
                if (testClass.getClassName().equals(testImage.getClassName())) {
                    for (TestCase testCase : testClass.getTestCaseList()) {
                        if (testCase.getMethodName().equals(testImage.getMethodName())) {
                            testCase.getTestImageList().add(testImage);
                        }
                    }
                }
            }
        }
        // html templateから報告書作成(画像リスト、結果リスト)
        StringBuilder path = new StringBuilder();
        path.append(targetProjectDir);
        path.append(SEPARATOR + REPORT_ROOT);
        // create report
        ReportService reporter = new ReportService();
        reporter.excute(testClassList, PathUtil.getResourcesPath(), path.toString() + SEPARATOR + REPORT_BASE);
        // rename work directory to device id 
        File reportBaseDir = new File(path.toString() + SEPARATOR + REPORT_BASE);
        if(!reportBaseDir.exists()){ 
            // show err or no action
        } else {
        	// device
        	DeviceInfo device = ReferenceUtil.getInstance().getLaunchShortcut().getCurrentDeviceInfo();
            path.append(SEPARATOR + device.getDeviceModel() + REPORT_OS + device.getOsVersion());
            // date
        	Calendar cal = Calendar.getInstance();
        	String strDate = String.valueOf(cal.get(Calendar.YEAR)) +
        			String.format("%02d", cal.get(Calendar.MONTH) + 1) +
        			String.format("%02d", cal.get(Calendar.DATE)) +
        			String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) +
        			String.format("%02d", cal.get(Calendar.MINUTE)) +
        			String.format("%02d", cal.get(Calendar.SECOND));
        	String reportNewPath = path.toString() + SEPARATOR + strDate;
            fileMove(reportBaseDir.getPath(), reportNewPath);
            FileUtil.delete(reportBaseDir.getPath());
        }
        // delete temp file
        FileUtil.delete(targetProjectDir + SEPARATOR + TEMP_FILE);
    }
    
    // ディレクトリ内のファイルを移動先へ移動する
    private boolean fileMove(String from, String to ) {
    	try {
    		File dir = new File(from);
    		File[] files = dir.listFiles();
    		if (files == null) return false;
    		File moveDir = new File(to);
    		if (!moveDir.exists()) {
    			moveDir.mkdirs();
    		}
    		for (int i = 0; i < files.length; i++) {
    			if (files[i].isDirectory()) {
    				fileMove(files[i].getPath(), to + SEPARATOR + files[i].getName());
    				files[i].delete();
    			}
    			File moveFile = new File(moveDir.getPath() + SEPARATOR + files[i].getName());
    			if (moveFile.exists()) {
    				moveFile.delete();
    			}
    			files[i].renameTo(moveFile);
    		}
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
}
