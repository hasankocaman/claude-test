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

/**
 * Farklı tarayıcı örnekleri oluşturan WebDriver fabrikası.
 * Chrome, Firefox ve Edge için yapılandırma, headless ve özel seçeneklerle kurulum sağlar.
 */
public class WebDriverFactory {
    
    private static final Logger logger = LogManager.getLogger(WebDriverFactory.class);
    
    /**
     * Creates WebDriver instance based on browser type from configuration
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        logger.info("Creating WebDriver for browser: {}", browser);
        
        WebDriver driver;
        
        switch (browser) {
            case "chrome":
                driver = createChromeDriver();
                break;
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            default:
                logger.warn("Unknown browser '{}', defaulting to Chrome", browser);
                driver = createChromeDriver();
        }
        
        configureDriver(driver);
        return driver;
    }
    
    /**
     * Creates Chrome WebDriver with options
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Basic Chrome options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        
        // Performance optimizations
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timers");
        options.addArguments("--disable-client-side-phishing-detection");
        
        // Headless mode
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-logging");
            options.addArguments("--log-level=3");
            logger.info("Chrome running in headless mode");
        }
        
        // Window size for headless mode
        if (ConfigReader.isHeadless()) {
            options.addArguments("--window-size=1920,1080");
        }
        
        // Disable notifications and popups
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        
        // Set user agent if needed for testing
        // options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        
        logger.info("Created Chrome driver with options");
        return new ChromeDriver(options);
    }
    
    /**
     * Creates Firefox WebDriver with options
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        // Basic Firefox options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        // Headless mode
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
            logger.info("Firefox running in headless mode");
        }
        
        // Disable notifications
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        
        // Performance settings
        options.addPreference("network.http.pipelining", true);
        options.addPreference("network.http.proxy.pipelining", true);
        options.addPreference("network.http.pipelining.maxrequests", 10);
        options.addPreference("nglayout.initialpaint.delay", 0);
        
        logger.info("Created Firefox driver with options");
        return new FirefoxDriver(options);
    }
    
    /**
     * Creates Edge WebDriver with options
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        
        // Basic Edge options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        
        // Headless mode
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
            logger.info("Edge running in headless mode");
        }
        
        // Disable notifications
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        
        logger.info("Created Edge driver with options");
        return new EdgeDriver(options);
    }
    
    /**
     * Configures WebDriver with common settings
     * @param driver WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        
        // Maximize window if not headless
        if (!ConfigReader.isHeadless() && ConfigReader.isWindowMaximize()) {
            driver.manage().window().maximize();
            logger.info("Browser window maximized");
        }
        
        logger.info("WebDriver configured with timeouts and window settings");
    }
    
    /**
     * Creates WebDriver for specific browser (used for parallel testing)
     * @param browserName Browser name (chrome, firefox, edge)
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browserName) {
        logger.info("Creating WebDriver for specific browser: {}", browserName);
        
        WebDriver driver;
        
        switch (browserName.toLowerCase()) {
            case "chrome":
                driver = createChromeDriver();
                break;
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
        
        configureDriver(driver);
        return driver;
    }
    
    /**
     * Creates headless Chrome driver (useful for CI/CD)
     * @return ChromeDriver in headless mode
     */
    public static WebDriver createHeadlessChromeDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=3");
        
        ChromeDriver driver = new ChromeDriver(options);
        
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        
        logger.info("Created headless Chrome driver");
        return driver;
    }
    
    /**
     * Creates Chrome driver with custom options
     * @param customOptions Custom ChromeOptions
     * @return ChromeDriver with custom options
     */
    public static WebDriver createChromeDriverWithOptions(ChromeOptions customOptions) {
        WebDriverManager.chromedriver().setup();
        
        ChromeDriver driver = new ChromeDriver(customOptions);
        configureDriver(driver);
        
        logger.info("Created Chrome driver with custom options");
        return driver;
    }
    
    /**
     * Creates Firefox driver with custom options
     * @param customOptions Custom FirefoxOptions
     * @return FirefoxDriver with custom options
     */
    public static WebDriver createFirefoxDriverWithOptions(FirefoxOptions customOptions) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxDriver driver = new FirefoxDriver(customOptions);
        configureDriver(driver);
        
        logger.info("Created Firefox driver with custom options");
        return driver;
    }
    
    /**
     * Safely quits WebDriver instance
     * @param driver WebDriver instance to quit
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error closing WebDriver: {}", e.getMessage());
            }
        }
    }
}