package com.core.framework.htmlreporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.core.framework.testLogs.StepLogger;
import com.intuit.karate.Results;
import com.intuit.karate.core.Feature;
import com.intuit.karate.core.ScenarioResult;
import com.intuit.karate.core.StepResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class Reporter {
	
	private static Reporter reporter=null;

    private final ExtentSparkReporter htmlReporter;

    private final ExtentReports extentReport;

    private final Map<String, ExtentTest> htmlTestLogs;

    private String reportingFolder;

    private String assetFolder;

    private String summaryFolder;
    
    private String deviceDetails;
    
    private Reporter(String reportingFolder) {
        log.debug("HTML Reporter called");
        log.debug("Output folder created @ "+reportingFolder);
        // creating asset folder
        assetFolder = reportingFolder + "/assets";
        File folder = new File(assetFolder);
        log.debug((folder.mkdirs() ? "asset folder created" : "asset folder creation failed"));
        log.debug("Asset folder created @ " + folder.getAbsolutePath());

        // creating summary folder
        summaryFolder = reportingFolder + "/summary";
        folder = new File(summaryFolder);
        log.debug((folder.mkdirs() ? "asset folder created" : "asset folder creation failed"));
        log.debug("Summary folder created @ " + folder.getAbsolutePath());

        // reporting initialized
        htmlReporter = new ExtentSparkReporter(reportingFolder + "/result.html");
        this.reportingFolder = reportingFolder;
        extentReport = new ExtentReports();
        htmlTestLogs = new HashMap<>();
        if (new File("src/test/resources/extent-config.xml").exists()) {
            try {
                htmlReporter.loadXMLConfig("src/test/resources/extent-config.xml");
            } catch (Exception e) {
                //do Nothing go with default config
            }
        }
        log.debug("html reporting initialized");
    }

    public synchronized void onTestStart(String methodName) {
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
        test.assignAuthor(System.getProperty("user.name"));
        if(deviceDetails !=null) {
        	test.assignDevice(deviceDetails);
        }
        log.debug("html reporting initialized for " + methodName);
    }

    public synchronized void onTestStart(String methodName,String author) {
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
        test.assignAuthor(author);
        if(deviceDetails !=null) {
        	test.assignDevice(deviceDetails);
        }
        log.debug("html reporting initialized for " + methodName);
    }
    public synchronized void onTestStart(String methodName,String author,String category) {
        ExtentTest test = extentReport.createTest(methodName);
        extentReport.attachReporter(htmlReporter);
        htmlTestLogs.put(methodName, test);
        test.assignAuthor(author);
        test.assignCategory(category);
        if(deviceDetails !=null) {
        	test.assignDevice(deviceDetails);
        }
        log.debug("html reporting initialized for " + methodName);
    }

    public synchronized void assignAuthor(String methodName,String author) {
    	htmlTestLogs.get(methodName).assignAuthor(author);
    }

    public synchronized void assignDevice(String methodName,String device) {
    	htmlTestLogs.get(methodName).assignDevice(device);
    }

    public synchronized void log(String methodName, Status status, String log) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        extentTest.log(status, log);
        loggerLog(status, log);
    }

    public synchronized <T> void log(String methodName, String stepDescription, T expected, T actual, String evidence) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        StepLogger log = StepLogger.log(stepDescription, expected, actual, evidence);
        extentTest.log(log.getLogStatus(), log.toString());
        loggerLog(log.getLogStatus(), log.toString());
    }

    public synchronized <T> void log(String methodName, String stepDescription, T expected, T actual) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        StepLogger log = StepLogger.log(stepDescription, expected, actual);
        extentTest.log(log.getLogStatus(), log.toString());
        loggerLog(log.getLogStatus(), log.toString());
    }

    public synchronized <T> void log(String methodName, String stepDescription, T expected, T actual, WebDriver driver) {
        if (!htmlTestLogs.containsKey(methodName)) {
            onTestStart(methodName);
        }
        ExtentTest extentTest = htmlTestLogs.get(methodName);
        StepLogger log = StepLogger.log(stepDescription, expected, actual, takeScreenShotWebPage(driver, stepDescription));
        extentTest.log(log.getLogStatus(), log.toString());
        loggerLog(log.getLogStatus(), log.toString());
    }

    private void loggerLog(Status status, String message) {
        switch (status) {
            case FAIL:
                log.error("[ testlog Status : " + status + ", testlog : " + message + "]");
                break;
            case WARNING:
                log.warn("[ testlog Status : " + status + ", testlog : " + message + "]");
                break;
            default:
                log.info("[ testlog Status : " + status + ", testlog : " + message + "]");
        }
    }

    public String takeScreenShotWebPage(WebDriver driver, String fileName) {
        String folderName = assetFolder + "/";
        File f = (new File(folderName));
        if (!f.exists()) {
            log.debug((f.mkdirs() ? "Screenshot folder created" : "Screenshot folder creation failed"));
        }
        String imgPath = folderName + fileName + ".jpg";
        File s = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(s, new File(imgPath));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "assets/" + fileName + ".jpg";
    }


    public boolean stopReporting() {
        extentReport.flush();
        log.debug("report flush");
        log.info("Report url: "+htmlReporter.getFile().getAbsolutePath());
        return true;
    }

    public boolean writeSummaryFiles() {
        try (
                FileOutputStream pass = new FileOutputStream(getSummaryFolder()+"/pass.txt");
                FileOutputStream fail = new FileOutputStream(getSummaryFolder()+"/fail.txt");
                FileOutputStream skip = new FileOutputStream(getSummaryFolder()+"/skip.txt");
        ){
            for(String tests : htmlTestLogs.keySet()){
                if(htmlTestLogs.get(tests).getStatus().equals(Status.PASS)){
                    pass.write(tests.getBytes(StandardCharsets.UTF_8));
                    pass.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                }
                else if(htmlTestLogs.get(tests).getStatus().equals(Status.FAIL)){
                    fail.write(tests.getBytes(StandardCharsets.UTF_8));
                    fail.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                }
                else{
                    skip.write(tests.getBytes(StandardCharsets.UTF_8));
                    skip.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void setSystemVars(Properties props) {

        if (props == null)
            return;
        if (props.isEmpty())
            return;
        try {
        	this.deviceDetails = System.getProperty("os.name")+"/"+props.getProperty("browser");
        }
        catch(NullPointerException ex) { this.deviceDetails =null;}
        //adding property file details onto report
        for (Object key : props.keySet()) {
            extentReport.setSystemInfo(key.toString(), props.getProperty(key.toString()));
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

    public String getSummaryFolder() {
        return summaryFolder;
    }

    public void setSummaryFolder(String summaryFolder) {
        this.summaryFolder = summaryFolder;
    }

    ExtentReports getExtentReport(){
        return this.extentReport;
    }

    public static synchronized Reporter initializeReporting(String reportingFolderPath) {
        if(reporter==null) {
        	File folder = new File(reportingFolderPath);
            // folder check - if not present create one
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String resultFolder = reportingFolderPath + "/" + String.valueOf(folder.listFiles().length + 1);
            folder = new File(resultFolder);
            // folder check - if not present create one
            if (!folder.exists()) {
                folder.mkdirs();
            }
            reporter = new Reporter(resultFolder);
        }
        return reporter;
    }
}
