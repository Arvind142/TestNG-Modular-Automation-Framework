package com.core.test_package;

import com.core.framework.listener.Listener;
import com.core.framework.annotation.TestDescription;
import com.core.framework.base.TestNG_Base;
import com.core.utility.Web;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

import static com.aventstack.extentreports.Status.*;
@Slf4j
@Listeners(Listener.class)
public class TestRunner extends TestNG_Base {
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
	@TestDescription(author = "Choudhary, Arvind")
	public void testNoArgs() throws MalformedURLException {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "test Executed");
		reporter.log(testMethod, PASS, "FirstLOG");
		Web web = new Web();
		WebDriver driver = web.initializeWebDriver();
		driver.get("https://google.com");
		reporter.log(testMethod, INFO, driver.getCurrentUrl());
		reporter.log(testMethod, "Title verification", "Google", driver.getTitle());
		reporter.log(testMethod, "Title verification", "Google", driver.getTitle(), reporter.takeScreenShotWebPage(driver, "someName"));
		reporter.log(testMethod, "Title verification Mismatch", "Google_Expected", driver.getTitle(), driver);
		driver.quit();
	}

	@Test(dataProvider = "default")
	@TestDescription(author = "Choudhary, Arvind")
	public void test2Args(Object arg1) {
		testMethod = getTestCaseName(arg1);
		reporter.log(testMethod, INFO, "test Executed");
	}

	@Test(dataProvider = "default",groups = "valid Working case with Object... as arguments")
	@TestDescription(author = "Choudhary, Arvind")
	public void testArgs(Object... arg2) {
		testMethod = getTestCaseName(arg2);
		reporter.log(testMethod, INFO, "test Executed");
	}

	@Test(timeOut = 1)
	@TestDescription(author = "Choudhary, Arvind")
	public void testFailWithTimeout() {
		for (int i = 1; i <= 2000000; i++) {
//            System.out.println(i);
		}
	}

	@Test
	@TestDescription(author = "Choudhary, Arvind")
	public void testSkipped() {
		throw new SkipException("Test Case should be skipped");
	}

	@Test
	@TestDescription(author = "Choudhary, Arvind")
	public void testFailedWithAssertfailed() {
		Assert.assertEquals(0, 1);
	}

	@Test
	public void parentSuccessTest() {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "testExecuted");
	}

	@Test(dependsOnMethods = {"parentSuccessTest"})
	public void childSuccessTest() {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "testExecuted");
	}

	@Test
	public void parentFailTest() {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "testExecuted");
		Assert.assertEquals(0, 1);
	}

	@Test(dependsOnMethods = {"parentFailTest"})
	public void childFailTest() {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "testExecuted");
	}

	@Test(invocationCount = 5)
	public void invokeTestMethod() {
		testMethod = getTestCaseName();
		reporter.log(testMethod, INFO, "testExecuted");
	}
}

