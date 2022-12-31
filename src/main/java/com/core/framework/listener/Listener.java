package com.core.framework.listener;

import com.aventstack.extentreports.Status;
import com.core.framework.annotation.TestDescription;
import com.core.framework.constant.FrameworkConstants;
import com.core.framework.constant.ReportingConstants;
import com.core.framework.htmlreporter.TestReportManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Listener implements ITestListener {
    //base Property
    public static Properties property;
    // reporting variables
    public String reportingFolder;

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestCaseName(result);
        String author=null;
        String testDescription = null;
        String[] category =null;
        try {
        	author = result.getMethod().getConstructorOrMethod().getMethod()
    				.getAnnotation(TestDescription.class).author();
        	author=author.equals(FrameworkConstants.not_applicable_const)?null:author;

            testDescription = result.getMethod().getConstructorOrMethod().getMethod()
                    .getAnnotation(TestDescription.class).testDescription();
            testDescription=testDescription.equals(FrameworkConstants.not_applicable_const)?null:testDescription;

            category = result.getMethod().getConstructorOrMethod().getMethod()
                    .getAnnotation(Test.class).groups();
        	category=category.length==0?null:category;

        }
        catch(Exception e) {
        	log.warn("@TestDescription is not used with "+testName);
        }
        if(author!=null && category!=null) {
        	TestReportManager.onTestStart(testName,testDescription, author, category);
        }
        else if(author!=null) {
            TestReportManager.onTestStart(testName,testDescription, author);
        }
        else {
            TestReportManager.onTestStart(testName,testDescription);
        }
        TestReportManager.checkAndAddParametersToReport(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TestReportManager.attachSnapshot();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestReportManager.log(Status.FAIL, "testcase failed! [ " + result.getThrowable().getMessage() + " ]");
        TestReportManager.checkAndAddRetryReport(result);
        TestReportManager.attachSnapshot();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // testcase is being retried
        if(result.wasRetried()){
            onTestFailure(result);
            return;
        }
        if(result.getSkipCausedBy().size()==0) {
            TestReportManager.log(Status.SKIP, "testcase skipped! ");
        }
        else {
            TestReportManager.log(Status.SKIP, "testcase skipped! [ cause: "+result.getSkipCausedBy().get(0).getMethodName()+" ]");
        }
        TestReportManager.attachSnapshot();
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        TestReportManager.log(Status.FAIL, "testcase failed with timeout!");
        this.onTestFailure(result);
    }

    @Override
    public void onStart(ITestContext context) {

        log.debug("log initialized!");

        // read config to start with base
        property = readProperty(FrameworkConstants.application_global_config);

        if (property != null) {
            log.debug("execution property read");
        } else {
            log.error("failed to read property file");
        }
        // updating reporting folder
        reportingFolder = ReportingConstants.resultFolder;

        //reporting initialized
        TestReportManager.initializeReporting(reportingFolder);

        // loading properties data into report
        TestReportManager.setSystemVars(property);
    }

    @Override
    public void onFinish(ITestContext context) {
        // flush reporting
        TestReportManager.stopReporting();

        log.debug("onFinish reached!");
    }

    // _______________ Helper Methods _______________
    public static String getTestCaseName(ITestResult result) {
        String[] resultDataArray = result.getMethod().getQualifiedName().split("\\.");
        String testName = resultDataArray[resultDataArray.length - 2] + "." + resultDataArray[resultDataArray.length - 1];

        // get parameters if any :)
        List<String> paramString = getParameter(result);
        if(paramString!=null){
            // returning test name
            return (testName + " - [ " + paramString.stream().toArray()[0]+" ]");
        }
        return testName;
    }

    public static List<String> getParameter(ITestResult result){
        // if no parameter then return test name
        if (result.getParameters().length == 0)
            return null;

        // get all parameters
        List<Object> objectList = Arrays.stream(result.getParameters()).toList();

        // new parameter list to fetch all params ( including variable length parameters)
        List<String> newObjectList = new ArrayList<>();

        // iterate over list
        for (Object currentObject : objectList) {
            // if parameter is of variable length args
            if (currentObject instanceof Object[]) {
                for (Object obj : ((Object[]) currentObject)) {
                    newObjectList.add(String.valueOf(obj));
                }
            } else {
                newObjectList.add(String.valueOf(currentObject));
            }
        }
        return  newObjectList;
    }


    public Properties readProperty(String propertyFilePath) {
        Properties properties = new Properties();
        try (InputStream ins = new FileInputStream(propertyFilePath)) {
            properties.load(ins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // replace property values with env vars :)
        for(Object iterator:properties.keySet()){
            if(System.getenv().containsKey(iterator)){
                log.trace("Env value found against key: {}",iterator);
                String systemEnvValue = System.getenv(iterator.toString());
                if(systemEnvValue.equalsIgnoreCase(properties.getProperty(iterator.toString()))){
                    log.trace("key has env value same as property value");
                }
                else{
                    log.trace("key has env value as {} and property value as {}",systemEnvValue,properties.getProperty(iterator.toString()));
                    properties.replace(iterator,System.getenv(iterator.toString()));
                }
            }
        }

        return properties;
    }
}
