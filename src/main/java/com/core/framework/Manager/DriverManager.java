package com.core.framework.Manager;

import com.core.framework.listener.Listener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Properties;

public class DriverManager {
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // static block to initialize webdriver threadlocal var with null!
    {
        driverThreadLocal.set(null);
    }

    public static WebDriver getWebDriver(){
        if(driverThreadLocal.get()==null){
            setWebDriver();
        }
        return driverThreadLocal.get();
    }

    public static void closeConnection(){
        if(driverThreadLocal.get()!=null){
            driverThreadLocal.get().quit();
            driverThreadLocal.set(null);
        }
    }

    public static boolean isConnectionOpen(){
        return driverThreadLocal.get()!=null;
    }

    private static void setWebDriver(){
        driverThreadLocal.set(initializeWebDriver());
    }

    private static WebDriver initializeWebDriver() {
        Properties globalProperty = Listener.property;
        WebDriver driver;
        String browserName = globalProperty.getProperty("browser");
        try {
            if (globalProperty.getProperty("isRemote").toLowerCase().startsWith("n")) {
                //WDM use confirmation
                switch (browserName.toUpperCase()) {
                    case "CHROME":
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                        break;
                    case "EDGE":
                    case "MSEDGE":
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver();
                        break;
                    case "FIREFOX":
                    case "FOX":
                    case "FF":
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver();
                        break;
                    case "IE":
                    case "INTERNETEXPLORER":
                        WebDriverManager.iedriver().setup();
                        driver = new InternetExplorerDriver();
                        break;
                    default:
                        throw new InvalidArgumentException("Invalid BrowserName");
                }
            } else {
                URL url = new URL(globalProperty.getProperty("remoteUrl"));
                switch (browserName.toUpperCase()) {
                    case "CHROME":
                        driver = new RemoteWebDriver(url, new ChromeOptions());
                        break;
                    case "EDGE":
                    case "MSEDGE":
                        driver = new RemoteWebDriver(url, new EdgeOptions());
                        break;
                    case "FIREFOX":
                    case "FOX":
                    case "FF":
                        driver = new RemoteWebDriver(url, new FirefoxOptions());
                        break;
                    default:
                        throw new InvalidArgumentException("Invalid BrowserName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return driver;
    }
}
