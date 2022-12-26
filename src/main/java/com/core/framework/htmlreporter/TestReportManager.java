package com.core.framework.htmlreporter;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.core.framework.testLogs.TableLog;
import org.openqa.selenium.WebDriver;
import java.util.Properties;

public class TestReportManager {
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private static Reporter reporter;

    public static void initializeReporting(String outputFolder){
        reporter = Reporter.initializeReporting(outputFolder);
    }

    public static void onTestStart(String testName,String description) {
        if(description!=null){
            description=description.replaceAll(" ", "&nbsp;");
        }
        onTestStart(testName,description,null);
    }

    public static void onTestStart(String testName,String description,String author) {
        onTestStart(testName,description,author, (String[]) null);
    }
    public static void onTestStart(String testName,String description,String author,String... category) {
        extentTestThreadLocal.set(reporter.getExtentReport().createTest(testName,description));
        assignAuthor(author);
        assignCategory(category);
        assignDevice();
    }

    public static void setSystemVars(Properties pros){
        reporter.setSystemVars(pros);
    }

    public static String getReportingFolder(){
        return reporter.getReportingFolder();
    }
    public static void log(Status status,String message){
        extentTestThreadLocal.get().log(status,message);
    }

    public static <T> void log(String stepDescription, T expected, T actual, String evidence) {
        TableLog log = TableLog.log(stepDescription, expected, actual, evidence);
        extentTestThreadLocal.get().log(log.getLogStatus(), log.getEquivalent());
    }

    public static <T> void log(String stepDescription, T expected, T actual) {
        TableLog log = TableLog.log(stepDescription, expected, actual);
        extentTestThreadLocal.get().log(log.getLogStatus(), log.getEquivalent());
    }

    public static <T> void log(String stepDescription, T expected, T actual, WebDriver driver) {
        TableLog log = TableLog.log(stepDescription, expected, actual, takeScreenShotWebPage(driver,stepDescription));
        extentTestThreadLocal.get().log(log.getLogStatus(), log.getEquivalent());
    }

    public static void stopReporting(){
        reporter.stopReporting();
    }

    public static void assignAuthor(String author){
        if(author!=null){
            author=author.replaceAll(" ", "&nbsp;");
        }
        extentTestThreadLocal.get().assignAuthor(author==null?(System.getProperty("user.name")):author);
    }

    public static void assignDevice(){
        if (reporter.getDeviceDetails() != null) {
            extentTestThreadLocal.get().assignDevice(reporter.getDeviceDetails());
        }
    }

    public static void assignCategory(String... category){
        extentTestThreadLocal.get().assignCategory(category);
    }

    public static String takeScreenShotWebPage(WebDriver driver, String fileName) {
        return reporter.takeScreenShotWebPage(driver,fileName);
    }
}
