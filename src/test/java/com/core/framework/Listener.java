package com.core.framework;

import com.aventstack.extentreports.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Listener implements ITestListener {
    //base Property
    public static Properties property;
    // reporting variables
    public String reportingFolder;
    // html reporter
    public static Reporter reporter;
    public static Logger logger;

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("================================================================================================================================================");
        String testName = getTestCaseName(result);
        reporter.onTestStart(testName);
        reporter.log(testName,Status.INFO,"test execution started!");
    }

    public void onTestCompletion(String testName){
        reporter.log(testName,Status.INFO,"test execution completed!");
        System.out.println("================================================================================================================================================");
    }
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestCaseName(result);
        reporter.log(testName,Status.PASS,"testcase passed!");
        onTestCompletion(testName);
    }
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestCaseName(result);
        reporter.log(testName,Status.FAIL,"testcase failed!");
        onTestCompletion(testName);
    }
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestCaseName(result);
        reporter.log(testName,Status.SKIP,"testcase skipped!");
        onTestCompletion(testName);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String testName = getTestCaseName(result);
        reporter.log(testName,Status.FAIL,"testcase failed with timeout!");
        this.onTestFailure(result);
    }
    @Override
    public void onStart(ITestContext context) {
        // logger initialized
        logger = LoggerFactory.getLogger(Listener.class);

        logger.debug("logger initialized!");

        // read config to start with base
        property = readProperty("src/test/resources/Execution-settings.properties");

        if(property!=null){
            logger.debug("execution property read");
        }
        else{
            logger.error("failed to read property file");
        }
        // updating reporting folder
        reportingFolder = "test-output";

        //reporting initialized
        reporter = Reporter.initializeReporting(reportingFolder);

    }
    @Override
    public void onFinish(ITestContext context) {
        reporter.stopReporting();
        logger.debug("onFinish reached!");
    }

    // _______________ Helper Methods _______________
    public static String getTestCaseName(ITestResult result) {
        String[] resultDataArray = result.getMethod().getQualifiedName().split("\\.");
        String testName=resultDataArray[resultDataArray.length - 2] + "." + resultDataArray[resultDataArray.length - 1];
        // if no parameter then return test name
        if(result.getParameters().length==0)
            return testName;

        //get first parameter out of result as we have parameter passed to our test method
        Object paramObject = result.getParameters()[0];

        // if given data is Object array
        if(paramObject instanceof Object[]){
            paramObject = ((Object[]) paramObject)[0];
        }

        // converting object to String
        String paramString = String.valueOf(paramObject);

        // returning test name
        return (testName+".["+paramString+"]");
    }


    public Properties readProperty(String propertyFilePath){
        Properties properties = new Properties();
        try (InputStream ins = new FileInputStream(propertyFilePath)) {
            properties.load(ins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return properties;
    }
}
