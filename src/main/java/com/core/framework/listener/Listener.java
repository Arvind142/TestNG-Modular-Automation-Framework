package com.core.framework.listener;

import com.aventstack.extentreports.Status;
import com.core.framework.annotation.TestDescription;
import com.core.framework.constant.FrameworkConst;
import com.core.framework.constant.ReportingConst;
import com.core.framework.htmlreporter.TestReportManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Listener implements ITestListener {
    //base Property
    public static Properties property;
    // reporting variables
    public String reportingFolder;

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("================================================================================================================================================");
        String testName = getTestCaseName(result);
        String author=null;
        String[] category =null;
        try {
        	author = result.getMethod().getConstructorOrMethod().getMethod()
    				.getAnnotation(TestDescription.class).author();
        	author=author.equals(FrameworkConst.not_applicable_const)?null:author;
        	
        	category = result.getMethod().getConstructorOrMethod().getMethod()
    				.getAnnotation(Test.class).groups();
        	category=category.length==0?null:category;
        }
        catch(Exception e) {
        	log.warn("@TestDescription is not used with "+testName);
        }
        if(author!=null && category!=null) {
        	author=author.replaceAll(" ", "&nbsp;");
        	TestReportManager.onTestStart(testName, author, category);
        }
        else if(author!=null) {
        	author=author.replaceAll(" ", "&nbsp;");
            TestReportManager.onTestStart(testName, author);
        }
        else {
            TestReportManager.onTestStart(testName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestReportManager.log(Status.FAIL, "testcase failed! [ " + result.getThrowable().getMessage() + " ]");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if(result.getSkipCausedBy().size()==0) {
            TestReportManager.log(Status.SKIP, "testcase skipped! ");
        }
        else {
            TestReportManager.log(Status.SKIP, "testcase skipped! [ cause: "+result.getSkipCausedBy().get(0).getMethodName()+" ]");
        }
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
        property = readProperty(FrameworkConst.application_global_config);

        if (property != null) {
            log.debug("execution property read");
        } else {
            log.error("failed to read property file");
        }
        // updating reporting folder
        reportingFolder = ReportingConst.resultFolder;

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
        // if no parameter then return test name
        if (result.getParameters().length == 0)
            return testName;

        //get first parameter out of result as we have parameter passed to our test method
        Object paramObject = result.getParameters()[0];

        // if given data is Object array
        if (paramObject instanceof Object[]) {
            paramObject = ((Object[]) paramObject)[0];
        }

        // converting object to String
        String paramString = String.valueOf(paramObject);

        // returning test name
        return (testName + ".[" + paramString + "]");
    }


    public Properties readProperty(String propertyFilePath) {
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
