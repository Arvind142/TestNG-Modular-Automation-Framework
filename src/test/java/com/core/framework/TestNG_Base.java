package com.core.framework;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestNG_Base {
    protected Reporter logger;
    protected String className;

    @BeforeClass
    public void beforeClassMethod(ITestContext context){
        logger=Listener.reporter;
        className = this.getClass().getSimpleName();
    }

    @AfterClass
    public void afterClassMethod(ITestContext context){

    }
}
