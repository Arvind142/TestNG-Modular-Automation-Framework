package com.core.framework.webdriver;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;


@Slf4j
public class DriverManager {

    private DriverManager(){

    }
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getWebDriver() throws Exception{
        driverThreadLocal.set((new DriverBuilder()).build());
        return driverThreadLocal.get();
    }
    public static WebDriver getWebDriver(Browser browser) throws Exception {
        driverThreadLocal.set((new DriverBuilder().browser(browser)).build());
        return driverThreadLocal.get();
    }

    public static WebDriver getWebDriver(MutableCapabilities capabilities) throws Exception {
        driverThreadLocal.set((new DriverBuilder()).capabilities(capabilities).build());
        return driverThreadLocal.get();
    }

    public static WebDriver getWebDriver(Browser browser,MutableCapabilities capabilities) throws Exception {
        driverThreadLocal.set((new DriverBuilder()).browser(browser).capabilities(capabilities).build());
        return driverThreadLocal.get();
    }

    public static WebDriver getInstance(){
        return  driverThreadLocal.get();
    }

    public static void closeConnection() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }

    public static boolean isConnectionOpen() {
        return driverThreadLocal.get() != null;
    }
}
