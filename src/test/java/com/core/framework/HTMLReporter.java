package com.core.framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HTMLReporter {

    private ExtentSparkReporter htmlReporter = null;

    private ExtentReports extentReport = null;

    private Map<String, ExtentTest> htmlTestLogs = null;

    private HTMLReporter(String reportingFolder) {
        htmlReporter = new ExtentSparkReporter(reportingFolder + "/result.html");
        extentReport = new ExtentReports();
        htmlTestLogs = new HashMap<>();
        try {
            htmlReporter.loadXMLConfig("src/test/resources/extent-config.xml");
        } catch (Exception e) {
            //do Nothing go with default config
        }
    }

    public void onTestStart(String methodName){
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
    }
    public void log(String methodName,TestLog log){
        if(!htmlTestLogs.containsKey(methodName)){
           onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        extentTest.log(log.getStatus(),log.toString());
    }
    public void log(String methodName, Status status, String log){
        if(!htmlTestLogs.containsKey(methodName)){
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        extentTest.log(status,log);
    }



    public boolean stopReporting(){
        extentReport.flush();
        return true;
    }




    public static HTMLReporter initializeReporting(String reportingFolderPath){
        File folder = new File(reportingFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        return new HTMLReporter(reportingFolderPath);
    }
}
