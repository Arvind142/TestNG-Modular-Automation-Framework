package com.core.utility;

import com.core.framework.constant.ReportingConstants;
import com.core.framework.htmlreporter.TestReportManager;
import com.core.framework.listener.Listener;
import com.google.common.base.Function;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

@Slf4j
public class Web {
    /***
     * to initialize webdriver based on sent browserName
     *
     * @return webdriver variable
     */
    public synchronized WebDriver initializeWebDriver() {
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

    /***
     *
     * @param browserName CHROME/EDGE
     * @param options     browserOptions which holds reference for object type of
     *                    ChromeOptions/EdgeOptions
     * @return web driver reference initialized
     */
    public WebDriver initializeLocalChromiumWebBrowsers(String browserName, ChromiumOptions<?> options) {
        WebDriver driver;
        try {
            switch (browserName.toUpperCase()) {
                case "CHROME":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions cOptions = (ChromeOptions) options;
                    driver = new ChromeDriver(cOptions);
                    break;
                case "EDGE":
                case "MSEDGE":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions eOptions = (EdgeOptions) options;
                    driver = new EdgeDriver(eOptions);
                    break;
                default:
                    throw new InvalidArgumentException("Invalid BrowserName");
            }
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
        return driver;
    }

    /***
     * to open any url
     *
     * @param driver web driver variable
     * @param url    url to open
     */
    public void openURL(WebDriver driver, String url) {
        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    /**
     * to close all browser instances
     *
     * @param driver web driver object
     */
    public void destroyWebDriver(WebDriver driver) {
        if (driver != null)
            driver.quit();
    }


    /***
     * method return by method with adequate locator and location value
     *
     * @param locator constant identifier and locator
     * @return By class method
     */
    public By by(WebLocator locator,String value) {
        switch (locator) {
            case ID:
                return By.id(value);
            case NAME:
                return By.name(value);
            case XPATH:
                return By.xpath(value);
            case TAGNAME:
                return By.tagName(value);
            case LINKTEXT:
                return By.linkText(value);
            case CLASS_NAME:
                return By.className(value);
            case CSS_SELECTOR:
                return By.cssSelector(value);
            default:
                throw new InvalidArgumentException("Invalid locator used, kindly use ID,NAME, XPATH, CSS,TAGNAME");
        }
    }

    /***
     * to get webElement present on UI
     *
     * @param driver         WebDriver reference
     * @param locatorFilter        element locatorFilter
     * @param throwException boolean var to return exception when exception found or
     *                       skip exception
     * @param timeOUT        time to wait for element
     * @return WebElement/null
     */
    public WebElement getWebElement(WebDriver driver, WebLocator locatorFilter, String locationIdentifier, Boolean throwException, Integer timeOUT) {
        WebElement element;
        try {
            Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeOUT))
                    .pollingEvery(Duration.ofSeconds(1));
            element = wait.until((Function<WebDriver, WebElement>) input -> driver.findElement(by(locatorFilter,locationIdentifier)));
            highlightWebElement(driver, element);
            return element;
        } catch (Exception e) {
            if (throwException) {
                throw e;
            } else {
                return null;
            }
        }
    }

    /**
     * to get webelement
     *
     * @param driver         webdriver reference
     * @param locator        POM object (by reference)
     * @param throwException true implies throw exception in problem else return
     *                       null
     * @param timeOUT        timeout
     * @return webElement
     */

    public WebElement getWebElement(WebDriver driver, By locator, Boolean throwException, Integer timeOUT) {
        WebElement element;
        try {
            Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeOUT))
                    .pollingEvery(Duration.ofSeconds(1));
            element = wait.until((Function<WebDriver, WebElement>) input -> driver.findElement(locator));
            highlightWebElement(driver, element);
            return element;
        } catch (Exception e) {
            if (throwException) {
                throw e;
            } else {
                return null;
            }
        }
    }

    /***
     * method to send values to fields
     *
     * @param driver         webdriver reference
     * @param locatorValue        element identifier
     * @param text           text to send
     * @param throwException rue if you want exception in case of some exception or
     *                       false to return null in case of exception
     * @param timeOUT        time to wait for element to be visible
     * @return boolean status of true - worked and false some issue.
     */
    public boolean sendKeys(WebDriver driver, WebLocator webLocator,String locatorValue, String text, Boolean throwException, Integer timeOUT) {
        WebElement element;
        try {
            element = getWebElement(driver,webLocator, locatorValue, throwException, timeOUT);
            element.clear();
            element.sendKeys(text);
            return true;
        } catch (Exception e) {
            if (throwException) {
                throw e;
            } else {
                return false;
            }
        }
    }

    /***
     *  take webpage snapshot
     * @param driver webdriver instance
     * @param fileName filename
     * @return return path for screenshot
     */
    public String takeSceenShotWebPage(WebDriver driver, String fileName) {
        String folderName = TestReportManager.getReportingFolder() + "/"+ ReportingConstants.screenshotFolder;
        File f = (new File(folderName));
        if (!f.exists()) {
            f.mkdirs();
        }
        String imgPath = folderName + fileName + ".jpg";
        File s = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(s, new File(imgPath));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return ReportingConstants.screenshotFolder + fileName + ".jpg";
    }

    /**
     * take screenshot of desktop
     *
     * @param fileName screenshotName
     * @return image path
     */

    public String takeScreenShotScreenSnip(String fileName) {
        String folderName = TestReportManager.getReportingFolder() + "/"+ ReportingConstants.screenshotFolder;
        File f = (new File(folderName));
        if (!f.exists()) {
            f.mkdirs();
        }
        String imgPath = folderName + fileName + ".png";
        try {
            BufferedImage img = new Robot()
                    .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(img, "png", new File(imgPath));
            return ReportingConstants.screenshotFolder + fileName + ".png";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void highlightWebElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
    }
}