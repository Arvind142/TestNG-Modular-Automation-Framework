package com.core.test_package;

import com.core.framework.TestNG_Base;
import com.core.utility.Web;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.aventstack.extentreports.Status.*;

@Listeners(com.core.framework.Listener.class)
public class Runner extends TestNG_Base {
    @DataProvider(name = "default")
    public Object[][] dataProvider() {
        Object[][] arg = new Object[8][1];
        arg[0][0] = "A";
        arg[1][0] = 'B';
        arg[2][0] = 1;
        arg[3][0] = 9.05d;
        arg[4][0] = 20.5f;
        arg[5][0] = true;
        arg[6][0] = "";
        arg[7][0] = null;
        return arg;
    }

    @Test
    public void testNoArgs() {
        testMethod = className + "." + (new Object() {
        }.getClass().getEnclosingMethod().getName());
        logger.log(testMethod, INFO, "test Executed");
        logger.log(testMethod, PASS, "FirstLOG");
        Web web = new Web();
        WebDriver driver = web.initializeWebDriver();
        driver.get("https://google.com");
        logger.log(testMethod,INFO,driver.getCurrentUrl());
        driver.quit();
    }

    @Test(dataProvider = "default")
    public void test2Args(Object arg1) {
        testMethod = className + "." + (new Object() {
        }.getClass().getEnclosingMethod().getName());
        logger.log(testMethod, INFO, "test Executed");
    }

    @Test(dataProvider = "default")
    public void testArgs(Object... arg2) {
        testMethod = className + "." + (new Object() {
        }.getClass().getEnclosingMethod().getName());
        logger.log(testMethod, INFO, "test Executed");
    }

    @Test(timeOut = 1)
    public void testFailWithTimeout() {
        for (int i = 1; i <= 2000000; i++) {
//            System.out.println(i);
        }
    }

    @Test
    public void testSkipped() {
        throw new SkipException("Test Case should be skipped");
    }

    @Test
    public void testFailedWithAssertfailed() {
        Assert.assertEquals(0, 1);
    }
}
