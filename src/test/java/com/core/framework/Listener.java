package com.core.framework;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Listener implements ITestListener {
    //base Property
    public static Properties property;
    // reporting variables
    public static String reportingFolder;

    public void onTestStart(ITestResult result) {
    }

    public void onTestSuccess(ITestResult result) {
    }

    public void onTestFailure(ITestResult result) {
    }

    public void onTestSkipped(ITestResult result) {
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    public void onTestFailedWithTimeout(ITestResult result) {
        this.onTestFailure(result);
    }

    public void onStart(ITestContext context) {

        // read config to start with base
        property=readProperty("src/test/resources/Execution-settings.properties");

        // updating reporting folder
        reportingFolder= property.getProperty("reportingFolder");

        //
    }

    public void onFinish(ITestContext context) {
    }


    // _______________ Helper Methods _______________
    public Properties readProperty(String propertyFilePath){
        Properties properties = new Properties();
        try(InputStream ins = new FileInputStream(propertyFilePath)){
            properties.load(ins);
        }
        catch(FileNotFoundException e){
            properties.put("Error","FileNotFound!");
            properties.put("ErrorMessage",e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e){
            properties.put("Error","Some Error!");
            properties.put("ErrorMessage",e.getMessage());
            e.printStackTrace();
        }
        if(properties.size()==0){
            properties.put("reportingFolder",CoreConstants.reportingFolder);
            properties.put("testDataPath",CoreConstants.testDataGiven);
        }
        else{
            if(!properties.contains("reportingFolder"))
                properties.put("reportingFolder",CoreConstants.reportingFolder);
            if(!properties.contains("testDataPath"))
                properties.put("reportingFolder",CoreConstants.testDataGiven);

        }
        return properties;
    }
}
