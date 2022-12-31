package com.core.test_package;

import com.core.framework.webdriver.DriverManager;
import com.core.framework.htmlreporter.TestReportManager;
import com.core.framework.annotation.TestDescription;
import com.core.framework.base.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static com.aventstack.extentreports.Status.*;
import static org.testng.Assert.*;

@Slf4j
public class TestRunner extends TestBase {
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
	public void testNoArgs() throws Exception{
		TestReportManager.log(INFO, "test Executed");
		TestReportManager.log(PASS, "FirstLOG");
		WebDriver driver = DriverManager.getWebDriver();
		driver.get("https://youtube.com");
		TestReportManager.log(INFO, driver.getCurrentUrl());
		driver = DriverManager.getInstance();
		TestReportManager.log("Title verification", "YouTube", driver.getTitle());
		TestReportManager.log("Title verification", "YouTube", driver.getTitle());
		TestReportManager.log("Title verification Mismatch", "Youtube_Expected", driver.getTitle());
	}

	@Test(dataProvider = "default")
	@TestDescription(author = "Choudhary, Arvind")
	public void test2Args(Object arg1) {
//		System.out.println(arg1);
		TestReportManager.log(INFO, "test Executed: "+arg1);
	}

	@Test(dataProvider = "default",groups = "valid Working case with Object... as arguments")
	@TestDescription(author = "Choudhary, Arvind")
	public void testArgs(Object... arg2) {
//		System.out.println(arg2[0]);
		TestReportManager.log(INFO, "test Executed: "+arg2[0]);
	}

	@Test(timeOut = 1)
	@TestDescription(author = "Choudhary, Arvind")
	public void testFailWithTimeout() {
		int a = 0;
		int b = 1;
		int c = 0;
		for (int i = 1; i <= 2000000000; i++){
			c = a + b;
			a = b;
			b = c;
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
		assertEquals(0, 1);
	}

	@Test
	public void parentSuccessTest() {
		TestReportManager.log(INFO, "testExecuted");
	}

	@Test(dependsOnMethods = {"parentSuccessTest"})
	public void childSuccessTest() {
		TestReportManager.log(INFO, "testExecuted");
	}

	@Test
	public void parentFailTest() {
		TestReportManager.log(INFO, "testExecuted");
		assertEquals(0, 1);
	}

	@Test(dependsOnMethods = {"parentFailTest"})
	public void childFailTest() {
		TestReportManager.log(INFO, "testExecuted");
	}

	@Test(invocationCount = 5)
	public void invokeTestMethod() {
		TestReportManager.log(INFO, "testExecuted");
	}
}

