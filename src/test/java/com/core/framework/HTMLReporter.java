package com.core.framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class HTMLReporter {

    private ExtentSparkReporter htmlReporter = null;

    private ExtentReports extentReport = null;

    private Map<String, ExtentTest> htmlTestLogs = null;

    public String reportingFolder = "";

    public String assetFolder = "";

    private HTMLReporter(String reportingFolder) {
        // creatig asset folder
        assetFolder = reportingFolder+"/assets";
        File folder = new File(assetFolder);
        folder.mkdirs();
        htmlReporter = new ExtentSparkReporter(reportingFolder + "/result.html");
        this.reportingFolder=reportingFolder;
        extentReport = new ExtentReports();
        htmlTestLogs = new HashMap<>();
        if (new File("src/test/resources/extent-config.xml").exists()) {
            try {
                htmlReporter.loadXMLConfig("src/test/resources/extent-config.xml");
            } catch (Exception e) {
                //do Nothing go with default config
            }
        }
    }

    public void onTestStart(String methodName) {
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
    }

    public synchronized void log(String methodName, Status status, String log) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        extentTest.log(status, log);
    }

    public synchronized <T> void log(String methodName, String stepDescription,T expected,T actual, String evidence) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        TestLog log = TestLog.log(stepDescription,expected,actual,evidence);
        extentTest.log(log.getLogStatus(), log.toString());
    }


    public boolean stopReporting() {
        extentReport.flush();
        return true;
    }


    public String getReportingFolder() {
        return reportingFolder;
    }

    public void setReportingFolder(String reportingFolder) {
        this.reportingFolder = reportingFolder;
    }

    public String getAssetFolder() {
        return assetFolder;
    }

    public void setAssetFolder(String assetFolder) {
        this.assetFolder = assetFolder;
    }

    public static HTMLReporter initializeReporting(String reportingFolderPath) {
        File folder = new File(reportingFolderPath);
        // folder check - if not present create one
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String resultFolder = reportingFolderPath+"/"+String.valueOf(folder.listFiles().length+1);
        folder = new File(resultFolder);
        // folder check - if not present create one
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new HTMLReporter(resultFolder);
    }
}
