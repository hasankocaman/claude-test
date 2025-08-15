package com.test.utils;

import com.test.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class DriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    
    public static void initializeDriver() {
        String browserName = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver = createDriver(browserName);
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        
        if (ConfigReader.isWindowMaximize()) {
            driver.manage().window().maximize();
        }
        
        driverThreadLocal.set(driver);
        logger.info("Driver initialized: " + browserName);
    }
    
    private static WebDriver createDriver(String browserName) {
        switch (browserName) {
            case "chrome":
                return createChromeDriver();
            case "firefox":
                return createFirefoxDriver();
            case "edge":
                return createEdgeDriver();
            default:
                logger.error("Browser not supported: " + browserName);
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
    }
    
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        logger.info("Creating Chrome driver with headless: " + ConfigReader.isHeadless());
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        logger.info("Creating Firefox driver with headless: " + ConfigReader.isHeadless());
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        logger.info("Creating Edge driver with headless: " + ConfigReader.isHeadless());
        return new EdgeDriver(options);
    }
    
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return driver;
    }
    
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            logger.info("Driver quit successfully");
        }
    }
    
    /**
     * Check if driver is initialized
     * @return true if driver exists
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
}