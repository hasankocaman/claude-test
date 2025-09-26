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
 * Centralised WebDriver lifecycle manager.
 * Handles browser creation based on configuration, thread-local storage and
 * recovery on failures so parallel scenarios can reuse the same utilities.
 */
public final class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
        // Utility class
    }

    /**
     * Lazily initialise a WebDriver instance for the current thread.
     */
    public static void initializeDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver(ConfigReader.getBrowserType()));
            logger.info("WebDriver initialised for thread {} using browser {}",
                    Thread.currentThread().getName(), ConfigReader.getBrowserType());
        }
    }

    /**
     * Obtain the current thread's WebDriver, creating it if necessary.
     *
     * @return active WebDriver instance
     */
    public static WebDriver getDriver() {
        initializeDriver();
        return DRIVER.get();
    }

    /**
     * Create a WebDriver based on browser name.
     *
     * @param browserName Browser identifier from configuration
     * @return configured WebDriver instance
     */
    private static WebDriver createDriver(String browserName) {
        String browser = browserName == null ? "chrome" : browserName.toLowerCase();
        switch (browser) {
            case "firefox":
                return configureDriver(createFirefoxDriver());
            case "edge":
                return configureDriver(createEdgeDriver());
            case "chrome":
            default:
                if (!"chrome".equals(browser)) {
                    logger.warn("Unsupported browser '{}', defaulting to Chrome", browserName);
                }
                return configureDriver(createChromeDriver());
        }
    }

    private static WebDriver configureDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ConfigReader.getScriptTimeout()));

        if (!ConfigReader.isHeadless() && ConfigReader.isWindowMaximize()) {
            driver.manage().window().maximize();
            logger.debug("Browser window maximised");
        }
        return driver;
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--disable-gpu",
                "--disable-web-security",
                "--allow-running-insecure-content",
                "--disable-background-timer-throttling",
                "--disable-background-networking",
                "--disable-background-timers",
                "--disable-client-side-phishing-detection",
                "--disable-notifications",
                "--disable-popup-blocking"
        );

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new", "--disable-logging", "--log-level=3", "--window-size=1920,1080");
            logger.info("Chrome will run in headless mode");
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        options.addPreference("network.http.pipelining", true);
        options.addPreference("network.http.proxy.pipelining", true);
        options.addPreference("network.http.pipelining.maxrequests", 10);
        options.addPreference("nglayout.initialpaint.delay", 0);

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
            logger.info("Firefox will run in headless mode");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-extensions", "--disable-gpu");
        options.addArguments("--disable-notifications", "--disable-popup-blocking");

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless", "--window-size=1920,1080");
            logger.info("Edge will run in headless mode");
        }

        return new EdgeDriver(options);
    }

    /**
     * Check if there is an active driver.
     */
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /**
     * Determine if current driver session responds to commands.
     */
    public static boolean isBrowserHealthy() {
        WebDriver driver = DRIVER.get();
        return driver != null && isDriverHealthy(driver);
    }

    private static boolean isDriverHealthy(WebDriver driver) {
        try {
            driver.getTitle();
            return true;
        } catch (Exception e) {
            logger.warn("Driver health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Quit the active driver and clean up thread local storage.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.warn("Error while quitting WebDriver: {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }

    /**
     * Attempt to recover the browser session by restarting the driver.
     */
    public static boolean recoverSession() {
        try {
            quitDriver();
            initializeDriver();
            return hasDriver();
        } catch (Exception e) {
            logger.error("Session recovery failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Restart the browser when the current session is not healthy.
     */
    public static void restartBrowserIfNeeded() {
        if (!isBrowserHealthy()) {
            logger.info("Browser deemed unhealthy, restarting session");
            recoverSession();
        }
    }

    /**
     * Force a browser restart regardless of current health.
     */
    public static void forceRestartBrowser() {
        logger.info("Force restarting browser session");
        quitDriver();
        initializeDriver();
    }
}
