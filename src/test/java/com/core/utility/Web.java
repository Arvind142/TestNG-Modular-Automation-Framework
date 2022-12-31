package com.core.utility;

import com.core.framework.constant.ReportingConstants;
import com.core.framework.htmlreporter.TestReportManager;
import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

@Slf4j
class Web {

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
    public By by(WebLocator locator, String value) {
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
    public String takeScreenShotWebPage(WebDriver driver, String fileName) {
        String folderName = TestReportManager.getReportingFolder() + "/"+ ReportingConstants.SCREENSHOT_FOLDER;
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
        return ReportingConstants.SCREENSHOT_FOLDER + fileName + ".jpg";
    }

    /**
     * take screenshot of desktop
     *
     * @param fileName screenshotName
     * @return image path
     */

    public String takeScreenShotScreenSnip(String fileName) {
        String folderName = TestReportManager.getReportingFolder() + "/"+ ReportingConstants.SCREENSHOT_FOLDER;
        File f = (new File(folderName));
        if (!f.exists()) {
            f.mkdirs();
        }
        String imgPath = folderName + fileName + ".png";
        try {
            BufferedImage img = new Robot()
                    .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(img, "png", new File(imgPath));
            return ReportingConstants.SCREENSHOT_FOLDER + fileName + ".png";
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