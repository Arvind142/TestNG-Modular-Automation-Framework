package com.core.framework;

import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class Listener implements ITestListener {
    //base Property
    private Properties property;
    // reporting variables
    private String reportingFolder;
    // html reporter
    private HTMLReporter reporter;

    public void onTestStart(ITestResult result) {
        System.out.println("========================================================================");
        String testName = getTestCaseName(result);
        System.out.println(testName+" \t>\t test execution started!");
        reporter.onTestStart(testName);
        reporter.log(testName,Status.INFO,"test execution started!");
    }

    public void onTestCompletion(String testName){
        System.out.println(testName+" \t>\t test execution completed!");
        reporter.log(testName,Status.INFO,"test execution completed!");
        System.out.println("========================================================================");
    }

    public void onTestSuccess(ITestResult result) {
        String testName = getTestCaseName(result);
        System.out.println(testName+" \t>\t testcase passed!");
        reporter.log(testName,Status.PASS,"testcase passed!");
        onTestCompletion(testName);
    }

    public void onTestFailure(ITestResult result) {
        String testName = getTestCaseName(result);
        System.out.println(testName+" \t>\t testcase failed!");
        reporter.log(testName,Status.FAIL,"testcase failed!");
        onTestCompletion(testName);
    }

    public void onTestSkipped(ITestResult result) {
        String testName = getTestCaseName(result);
        System.out.println(testName+" \t>\t testcase skipped!");
        reporter.log(testName,Status.SKIP,"testcase skipped!");
        onTestCompletion(testName);
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    public void onTestFailedWithTimeout(ITestResult result) {
        String testName = getTestCaseName(result);
        System.out.println(testName+" \t>\t testcase failed with timeout!");
        reporter.log(testName,Status.FAIL,"testcase failed with timeout!");
        this.onTestFailure(result);
    }

    public void onStart(ITestContext context) {

        // read config to start with base
        property = readProperty("src/test/resources/Execution-settings.properties");

        // updating reporting folder
        reportingFolder = property.getProperty("reportingFolder");

        //reporting initialized
        reporter = HTMLReporter.initializeReporting(reportingFolder);

    }

    public void onFinish(ITestContext context) {
        reporter.stopReporting();
    }


    // _______________ Helper Methods _______________
    public String getTestCaseName(ITestResult result) {
        String[] resultDataArray = result.getMethod().getQualifiedName().split("\\.");
        String testName=resultDataArray[resultDataArray.length - 2] + "." + resultDataArray[resultDataArray.length - 1];
        // if no parameter then return test name
        if(result.getParameters().length==0)
            return testName;

        //get first parameter out of result as we have paramter passed to our test method
        Object paramObject = result.getParameters()[0];

        // if given data is Object array
        if(paramObject instanceof Object[]){
            paramObject = ((Object[]) paramObject)[0];
        }

        // converting object to String
        String paramString = String.valueOf(paramObject);

        // returning testname
        return (testName+".["+paramString+"]");
    }


    public Properties readProperty(String propertyFilePath) {
        Properties properties = new Properties();
        try (InputStream ins = new FileInputStream(propertyFilePath)) {
            properties.load(ins);
        } catch (FileNotFoundException e) {
            properties.put("Error", "FileNotFound!");
            properties.put("ErrorMessage", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            properties.put("Error", "Some Error!");
            properties.put("ErrorMessage", e.getMessage());
            e.printStackTrace();
        }
        if (properties.size() == 0) {
            properties.put("reportingFolder", CoreConstants.Core.reportingFolder);
            properties.put("testDataPath", CoreConstants.Core.testDataGiven);
        } else {
            if (!properties.contains("reportingFolder"))
                properties.put("reportingFolder", CoreConstants.Core.reportingFolder);
            if (!properties.contains("testDataPath"))
                properties.put("testDataPath", CoreConstants.Core.testDataGiven);

        }
        return properties;
    }
}
