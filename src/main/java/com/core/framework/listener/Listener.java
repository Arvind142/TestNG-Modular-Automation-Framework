package com.core.framework.listener;

import com.aventstack.extentreports.Status;
import com.core.framework.annotation.TestDescription;
import com.core.framework.config.ConfigFactory;
import com.core.framework.config.FrameworkConfig;
import com.core.framework.constant.FrameworkConstants;
import com.core.framework.constant.ReportingConstants;
import com.core.framework.htmlreporter.TestReportManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.annotations.Test;
import java.util.*;

@Slf4j
public class Listener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestCaseName(result);
        String author=null;
        String testDescription = null;
        String[] category =null;
        String[] dependsOnMethod = null;
        String[] dependsOnGroup = null;
        try {
        	author = result.getMethod().getConstructorOrMethod().getMethod()
    				.getAnnotation(TestDescription.class).author();
        	author=author.equals(FrameworkConstants.NOT_APPLICABLE_CONST)?null:author;

            testDescription = result.getMethod().getConstructorOrMethod().getMethod()
                    .getAnnotation(TestDescription.class).testDescription();
            testDescription=testDescription.equals(FrameworkConstants.NOT_APPLICABLE_CONST)?null:testDescription;
        }
        catch(Exception e) {
        	log.warn("@TestDescription is not used with "+testName);
        }

        category = result.getMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(Test.class).groups();
        category=category.length==0?null:category;

        dependsOnMethod = result.getMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(Test.class).dependsOnMethods();
        dependsOnMethod=dependsOnMethod.length==0?null:dependsOnMethod;

        dependsOnGroup = result.getMethod().getConstructorOrMethod().getMethod()
                .getAnnotation(Test.class).dependsOnGroups();
        dependsOnGroup=dependsOnGroup.length==0?null:dependsOnGroup;

        if(author!=null && category!=null) {
        	TestReportManager.onTestStart(testName,testDescription, author, category);
        }
        else if(author!=null) {
            TestReportManager.onTestStart(testName,testDescription, author);
        }
        else {
            TestReportManager.onTestStart(testName,testDescription);
        }
        if(dependsOnGroup!=null || dependsOnMethod!=null){
            if(dependsOnMethod!=null){
                TestReportManager.addDependsOnInfoLog("Method", Arrays.toString(dependsOnMethod));
            }
            if(dependsOnGroup!=null){
                TestReportManager.addDependsOnInfoLog("Group",Arrays.toString(dependsOnMethod));
            }
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
        if(result.getSkipCausedBy().isEmpty()) {
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
        FrameworkConfig frameworkConfig = ConfigFactory.getConfig();
        log.trace("Config initialized");

        //reporting initialized
        TestReportManager.initializeReporting(ReportingConstants.RESULT_FOLDER);
        // set trigger information
        TestReportManager.setTriggerDetails(context);
        // loading properties data into report
        TestReportManager.setSystemVars(frameworkConfig);
    }

    @Override
    public void onFinish(ITestContext context) {
        log.debug("onFinish reached!");
        // flush reporting
        TestReportManager.stopReporting();
    }

    // _______________ Helper Methods _______________
    public static String getTestCaseName(ITestResult result) {
        String[] resultDataArray = result.getMethod().getQualifiedName().split("\\.");
        String testName = resultDataArray[resultDataArray.length - 2] + "." + resultDataArray[resultDataArray.length - 1];

        // get parameters if any :)
        List<String> paramString = getParameter(result);
        if(!paramString.isEmpty()){
            // returning test name
            return (testName + " - [ " + paramString.stream().toArray()[0]+" ]");
        }
        return testName;
    }

    public static List<String> getParameter(ITestResult result){
        // if no parameter then return test name
        if (result.getParameters().length == 0)
            return new ArrayList<>();

        // get all parameters
        Object[] objectList = result.getParameters();

        // new parameter list to fetch all params ( including variable length parameters)
        List<String> newObjectList = new ArrayList<>();

        // iterate over list
        for (Object currentObject : objectList) {
            // if parameter is of variable length args
            if (currentObject instanceof Object[] objArray) {
                for (Object obj : objArray) {
                    newObjectList.add(String.valueOf(obj));
                }
            } else {
                newObjectList.add(String.valueOf(currentObject));
            }
        }
        return  newObjectList;
    }
}
