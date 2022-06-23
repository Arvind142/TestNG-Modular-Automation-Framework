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

    /***
     * to be used when your testMethod doesnt take any paramters
     * @return
     */
    public String getTestCaseName(){
        return getMethodAndClassName();
    }

    /****
     * to be used when your testMethod intakes parmaeters, you have to pass very first parmater of your testmethod
     * as an parameter to this method so that logging can happen correctly.
     * @param firstParam
     * @return
     */
    public String getTestCaseName(Object firstParam){
        // just in case object passed in an array instead of single param
        if(firstParam instanceof Object[]){
            firstParam = ((Object[])firstParam)[0];
        }
        return getMethodAndClassName()+".["+String.valueOf(firstParam)+"]";
    }

    private String getMethodAndClassName(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        String[] quailiferName = element.getClassName().split("\\.");
        return quailiferName[quailiferName.length-1]+"."+element.getMethodName();
    }
}
