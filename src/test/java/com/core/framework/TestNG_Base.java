package com.core.framework;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

public class TestNG_Base {
    protected Reporter logger;
    protected String className;

    protected String testMethod;

    @BeforeClass
    public void beforeClassMethod(ITestContext context){
        logger=Listener.reporter;
        className = this.getClass().getSimpleName();
    }

    @AfterClass
    public void afterClassMethod(ITestContext context){

    }
}
