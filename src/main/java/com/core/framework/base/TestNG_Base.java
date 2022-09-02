package com.core.framework.base;

import com.core.framework.htmlreporter.BDDReporter;
import com.core.framework.listener.Listener;
import com.core.framework.htmlreporter.Reporter;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.util.List;

@Slf4j
public class TestNG_Base {
    protected Reporter reporter;
    protected BDDReporter bddReporter;
    protected String className;

    protected String testMethod;

    @BeforeClass
    public void beforeClassMethod(ITestContext context){
        reporter = Listener.reporter;
        bddReporter = Listener.bddReporter;
        className = this.getClass().getSimpleName();
    }

    @AfterClass
    public void afterClassMethod(ITestContext context){

    }

    /***
     * to be used when your testMethod doesn't take any parmaters
     * @return returns strign which will identify test case framework for reporting
     */
    public String getTestCaseName(){
        return getMethodAndClassName();
    }

    /****
     * to be used when your testMethod intakes parmaeters, you have to pass very first parmater of your testmethod
     * as an parameter to this method so that logging can happen correctly.
     * @param firstParam first param which you received from your data provider
     * @return returns strign which will identify test case framework for reporting
     */
    public String getTestCaseName(Object firstParam){
        // just in case object passed in an array instead of single param
        if(firstParam instanceof Object[]){
            firstParam = ((Object[])firstParam)[0];
        }
        return getMethodAndClassName()+".["+firstParam+"]";
    }

    private String getMethodAndClassName(){
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        List<String> quailiferName = Splitter.on('.').splitToList(element.getClassName());
        return quailiferName.get(quailiferName.size()-1)+"."+element.getMethodName();
    }
}
