package com.core.framework.htmlreporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.core.framework.config.ConfigFactory;
import com.core.framework.config.FrameworkConfig;
import com.core.framework.webdriver.DriverManager;
import com.core.framework.constant.FrameworkConstants;
import com.core.framework.constant.ReportingConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

@Slf4j
class Reporter {
	
	private static Reporter reporter=null;

    private final ExtentSparkReporter htmlReporter;

    private final ExtentReports extentReport;

    private final String reportingFolder;

    private final String assetFolder;

    private String deviceDetails;

    private Reporter(String reportingFolder) {
        log.debug("HTML Reporter called");
        log.debug("Output folder created @ "+reportingFolder);
        // creating screenshot folder
        assetFolder = reportingFolder + ReportingConstants.SCREENSHOT_FOLDER;
        File folder = new File(assetFolder);
        log.debug((folder.mkdirs() ? "screenshot folder created" : "screenshot folder creation failed"));
        log.debug("Asset folder created @ " + folder.getAbsolutePath());

        // reporting initialized
        htmlReporter = new ExtentSparkReporter(reportingFolder + ReportingConstants.HTML_REPORT_NAME);
        this.reportingFolder = reportingFolder;
        extentReport = new ExtentReports();
        if (new File(FrameworkConstants.EXTENT_CONFIG_XML).exists()) {
            try {
                htmlReporter.loadXMLConfig(FrameworkConstants.EXTENT_CONFIG_XML);
            } catch (Exception e) {
                //do Nothing go with default config
            }
        }
        // attach reporter :)
        extentReport.attachReporter(htmlReporter);
        log.debug("html reporting initialized");
    }

    private String getUniqueString(){
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
        return format.format(Calendar.getInstance().getTime())+"_"+(new SecureRandom()).nextDouble();
    }

    public String takeScreenShotWebPage(String fileName) {
        String folderName = assetFolder + "/";
        File f = (new File(folderName));
        if (!f.exists()) {
            log.debug((f.mkdirs() ? "Screenshot folder created" : "Screenshot folder creation failed"));
        }
        fileName = fileName+"_"+getUniqueString()+".jpg";
        String imgPath = folderName + fileName;
        File s;
        WebDriver driver = DriverManager.getInstance();
        if(driver instanceof FirefoxDriver){
            s = ((FirefoxDriver) driver).getFullPageScreenshotAs(OutputType.FILE);
        }
        else{
            s = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        }
        try {
            FileUtils.copyFile(s, new File(imgPath));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return ReportingConstants.SCREENSHOT_FOLDER + fileName;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void stopReporting() {
        extentReport.flush();
        log.debug("report flush");
        log.info("Report url: file:///"+htmlReporter.getFile().getAbsolutePath().replace("\\","/"));
        writeSummary();
    }

    public void writeSummary(){
        try(
                FileOutputStream file = new FileOutputStream(reportingFolder+ReportingConstants.SUMMARY_FILE_NAME)
                ){
            file.write("[Status] \t[TestCase Name]".getBytes());
            for(Test tests: extentReport.getReport().getTestList()){
                file.write(("\r\n"+tests.getStatus()+"\t\t"+ tests.getName()).getBytes());
            }
        }
        catch (Exception e){
            log.error("Summary creation failed due to: "+e.getMessage());
        }
    }

    public void setSystemVars(FrameworkConfig frameworkConfig) {
        try {
        	this.deviceDetails = System.getProperty("os.name")+"/"+frameworkConfig.browser();
        }
        catch(NullPointerException ex) { this.deviceDetails =null;}
        extentReport.setSystemInfo("Browser", frameworkConfig.browser());
        extentReport.setSystemInfo("Remote Execution", frameworkConfig.isRemote()?"Y":"N");
        if(Boolean.TRUE.equals(frameworkConfig.isRemote())){
            extentReport.setSystemInfo("remote url", frameworkConfig.remoteUrl());
        }
    }

    public void setSystemVar(String key,String value) {
        extentReport.setSystemInfo(key, value);
    }

    public String getReportingFolder() {
        return reportingFolder;
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
            String resultFolder;
            if(Boolean.TRUE.equals(ReportingConstants.HAVE_MULIPLE_REPORT_FOLDER)){
                if(folder.listFiles()!=null) {
                    resultFolder = reportingFolderPath + (folder.listFiles().length + 1);
                }
                else{
                    resultFolder = reportingFolderPath + (1);
                }
            }
            else{
                resultFolder = reportingFolderPath;
            }
            resultFolder = resultFolder+"/";
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
