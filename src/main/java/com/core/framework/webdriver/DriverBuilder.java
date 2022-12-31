package com.core.framework.webdriver;

import com.core.framework.listener.Listener;
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
import java.net.URL;
import java.util.Properties;

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
        Properties globalProperty = Listener.property;
        String browserName = globalProperty.getProperty("browser");
        this.isRemote = globalProperty.getProperty("isRemote").toLowerCase().startsWith("y");
        this.remoteUrl = (globalProperty.getProperty("remoteUrl"));
        if (browserName.toLowerCase().contains("ie") || browserName.toLowerCase().contains("explorer")) {
            this.browser = Browser.InternetExplorer;
        } else if (browserName.toLowerCase().contains("fox") || browserName.toLowerCase().contains("ff")) {
            this.browser = Browser.Firefox;
        } else if (browserName.toLowerCase().contains("edge")) {
            this.browser = Browser.Edge;
        } else {
            this.browser = Browser.Chrome;
        }
    }

    private WebDriver initializeLocalWebDriver() {
        WebDriver driver;
        boolean isCapNull = capabilities==null;
        switch (browser) {
            case Chrome -> {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(!isCapNull ? ((ChromeOptions) capabilities) : new ChromeOptions());
            }
            case Edge -> {
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(!isCapNull ? ((EdgeOptions) capabilities) : new EdgeOptions());
            }
            case Firefox -> {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(!isCapNull ? ((FirefoxOptions) capabilities) : new FirefoxOptions());
            }
            case InternetExplorer -> {
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver(!isCapNull ? ((InternetExplorerOptions) capabilities) : new InternetExplorerOptions());
            }
            default -> throw new InvalidArgumentException("Invalid BrowserName");
        }
        return driver;
    }

    private WebDriver initializeRemoteWebDriver() throws Exception{
        WebDriver driver;
        boolean isCapNull = capabilities==null;
        URL url= new URL(remoteUrl);
        driver = switch (browser) {
            case Chrome ->
                    new RemoteWebDriver(url, (!isCapNull ? (capabilities) : new ChromeOptions()));
            case Edge -> new RemoteWebDriver(url, (!isCapNull ? ( capabilities) : new EdgeOptions()));
            case Firefox ->
                    new RemoteWebDriver(url, (!isCapNull ? (capabilities) : new FirefoxOptions()));
            default -> throw new InvalidArgumentException("Invalid BrowserName");
        };
        return driver;
    }

    public WebDriver build() throws Exception{
        log.info("build WebDriver with following values: Browser: {}, isRemote: {}, remoteUrl: {}", browser.toString(), isRemote, remoteUrl);
        if(isRemote){
            return  initializeRemoteWebDriver();
        }
        else{
            return initializeLocalWebDriver();
        }
    }
}
