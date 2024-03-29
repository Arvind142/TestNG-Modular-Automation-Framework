package com.core.framework.webdriver;

import com.core.framework.config.ConfigFactory;
import com.core.framework.config.FrameworkConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
class DriverBuilder {
    private Browser browser=null;
    private Boolean isRemote=null;
    private String remoteUrl=null;
    private MutableCapabilities capabilities=null;

    public DriverBuilder(){
        loadProperties();
    }

    public DriverBuilder browser(Browser browser) {
        this.browser = browser;
        return this;
    }

    public DriverBuilder isRemote(Boolean remote) {
        isRemote = remote;
        return this;
    }

    public DriverBuilder remoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
        return this;
    }

    public DriverBuilder capabilities(MutableCapabilities capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    private void loadProperties() {
        FrameworkConfig frameworkConfig = ConfigFactory.getConfig();
        String browserName = frameworkConfig.browser();
        this.isRemote = frameworkConfig.isRemote();
        this.remoteUrl = frameworkConfig.remoteUrl();
        if (browserName.toLowerCase().contains("ie") || browserName.toLowerCase().contains("explorer")) {
            this.browser = Browser.INTERNET_EXPLORER;
        } else if (browserName.toLowerCase().contains("fox") || browserName.toLowerCase().contains("ff")) {
            this.browser = Browser.FIREFOX;
        } else if (browserName.toLowerCase().contains("edge")) {
            this.browser = Browser.EDGE;
        } else {
            this.browser = Browser.CHROME;
        }
    }

    private WebDriver initializeLocalWebDriver() {
        WebDriver driver;
        boolean isCapNull = capabilities==null;
        switch (browser) {
            case CHROME: {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(!isCapNull ? ((ChromeOptions) capabilities) : new ChromeOptions());
                break;
            }
            case EDGE: {
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(!isCapNull ? ((EdgeOptions) capabilities) : new EdgeOptions());
                break;
            }
            case FIREFOX: {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(!isCapNull ? ((FirefoxOptions) capabilities) : new FirefoxOptions());
                break;
            }
            case INTERNET_EXPLORER: {
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver(!isCapNull ? ((InternetExplorerOptions) capabilities) : new InternetExplorerOptions());
                break;
            }
            default:
                throw new InvalidArgumentException("Invalid BrowserName");
        }
        return driver;
    }

    private WebDriver initializeRemoteWebDriver() throws MalformedURLException {
        WebDriver driver;
        boolean isCapNull = capabilities==null;
        URL url= new URL(remoteUrl);
        switch (browser) {
            case CHROME:
                    driver = new RemoteWebDriver(url, (!isCapNull ? (capabilities) : new ChromeOptions()));
                    break;
            case EDGE:
                driver = new RemoteWebDriver(url, (!isCapNull ? ( capabilities) : new EdgeOptions()));
                break;
            case FIREFOX:
                driver = new RemoteWebDriver(url, (!isCapNull ? (capabilities) : new FirefoxOptions()));
                break;
            default:
                throw new InvalidArgumentException("Invalid BrowserName");
        }
        return driver;
    }

    public WebDriver build() throws Exception{
        log.info("build WebDriver with following values: Browser: {}, isRemote: {}, remoteUrl: {}", browser.toString(), isRemote, remoteUrl);
        if(Boolean.TRUE.equals(isRemote)){
            return  initializeRemoteWebDriver();
        }
        else{
            return initializeLocalWebDriver();
        }
    }
}
