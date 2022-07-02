package com.core.framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Reporter {

    private ExtentSparkReporter htmlReporter;

    private ExtentReports extentReport;

    private Map<String, ExtentTest> htmlTestLogs;

    private String reportingFolder;

    private String assetFolder;

    private final Logger logger;

    private Reporter(String reportingFolder) {
        logger=LoggerFactory.getLogger(Reporter.class);
        logger.debug("HTML Reporter called");
        // creatig asset folder
        assetFolder = reportingFolder+"/assets";
        File folder = new File(assetFolder);
        logger.debug((folder.mkdirs()?"asset folder created":"asset folder creation failed"));
        logger.debug("Asset folder created @ "+folder.getAbsolutePath());
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
        logger.debug("html reporting initialized");
    }

    public synchronized void onTestStart(String methodName) {
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
        logger.debug("html reporting initialized for "+methodName);
    }

    public synchronized void log(String methodName, Status status, String log) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        extentTest.log(status, log);
        loggerLog(status,log);
    }

    public synchronized <T> void log(String methodName, String stepDescription,T expected,T actual, String evidence) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        TestLog log = TestLog.log(stepDescription,expected,actual,evidence);
        extentTest.log(log.getLogStatus(), log.toString());;
        loggerLog(log.getLogStatus(),log.toString());
    }

    public synchronized <T> void log(String methodName, String stepDescription,T expected,T actual, RemoteWebDriver driver) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        TestLog log = TestLog.log(stepDescription,expected,actual,takeSceenShotWebPage(driver,stepDescription));
        extentTest.log(log.getLogStatus(), log.toString());
        loggerLog(log.getLogStatus(),log.toString());
    }

    private void loggerLog(Status status, String message){
        switch(status){
            case FAIL:
                logger.error("[ testlog Status : "+status+", testlog : "+message+"]");
                break;
            case WARNING:
                logger.warn("[ testlog Status : "+status+", testlog : "+message+"]");
                break;
            default:
                logger.info("[ testlog Status : "+status+", testlog : "+message+"]");
        }
    }

    public String takeSceenShotWebPage(RemoteWebDriver driver, String fileName) {
        String folderName = reportingFolder + "/Screenshot/";
        File f = (new File(folderName));
        if (!f.exists()) {
            logger.debug((f.mkdirs()?"Screenshot folder created":"Screenshot folder creation failed"));
        }
        String imgPath = folderName + fileName + ".jpg";
        File s = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(s, new File(imgPath));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "Screenshot/" + fileName + ".jpg";
    }


    public boolean stopReporting() {
        extentReport.flush();
        logger.debug("report flush");
        return true;
    }


    public void setSystemVars(Properties props){
        if(props==null)
            return;
        if(props.isEmpty())
            return;
        //adding property file details onto report
        for(Object key:props.keySet()){
            extentReport.setSystemInfo(key.toString(),props.getProperty(key.toString()));
        }
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

    public static Reporter initializeReporting(String reportingFolderPath) {
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
        return new Reporter(resultFolder);
    }
}