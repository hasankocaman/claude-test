package com.test.utils;

import com.test.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver yaşam döngüsü yöneticisi.
 * Tarayıcı oluşturma, yapılandırma, sağlık kontrolü ve gerektiğinde yeniden başlatma
 * işlevlerini merkezileştirir.
 */
public class DriverManager {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    
    /**
     * Konfigürasyona göre yeni bir sürücü başlatır ve thread-local içine koyar.
     */
    public static void initializeDriver() {
        String browserName = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver = createDriver(browserName);
        
        // Enhanced timeout settings for stability
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Math.max(ConfigReader.getImplicitWait(), 20)));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Math.max(ConfigReader.getPageLoadTimeout(), 60)));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        
        if (ConfigReader.isWindowMaximize()) {
            driver.manage().window().maximize();
        }
        
        // Additional browser stability settings
        if (driver instanceof ChromeDriver) {
            logger.info("Applied extended timeout settings for Chrome driver");
        }
        
        driverThreadLocal.set(driver);
        logger.info("Driver initialized: " + browserName);
    }
    
    /**
     * İstenen tarayıcı adına göre sürücü oluşturur.
     */
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
    
    /**
     * Chrome sürücüsü oluşturur ve istikrar/performans için gerekli seçenekleri uygular.
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        // Enhanced stability options for Amazon testing
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Faster loading
        
        // Memory and performance optimizations
        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        
        // Anti-detection measures for Amazon
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-default-apps");
        
        // Use unique debugging port to avoid conflicts
        int debuggingPort = 9222 + (int)(Math.random() * 1000);
        options.addArguments("--remote-debugging-port=" + debuggingPort);
        
        // Set realistic user agent
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // Set page load strategy for better stability
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        // Additional preferences for stability
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        // Window size for consistency
        if (!ConfigReader.isHeadless()) {
            options.addArguments("--start-maximized");
        } else {
            options.addArguments("--window-size=1920,1080");
        }
        
        logger.info("Creating Chrome driver with headless: " + ConfigReader.isHeadless());
        return new ChromeDriver(options);
    }
    
    /**
     * Firefox sürücüsü oluşturur.
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        logger.info("Creating Firefox driver with headless: " + ConfigReader.isHeadless());
        return new FirefoxDriver(options);
    }
    
    /**
     * Edge sürücüsü oluşturur.
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        logger.info("Creating Edge driver with headless: " + ConfigReader.isHeadless());
        return new EdgeDriver(options);
    }
    
    /**
     * Geçerli thread için WebDriver döner.
     * @throws IllegalStateException initialize edilmeden çağrılırsa
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return driver;
    }
    
    /**
     * Sürücüyü kapatır ve thread-local referansını temizler.
     */
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
    /**
     * Mevcut thread'de sürücü olup olmadığını belirtir.
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Check if browser is still responsive
     * @return true if browser is healthy
     */
    /**
     * Temel bir sağlık kontrolü uygular: sessionId, URL ve JS yürütme testi.
     */
    public static boolean isBrowserHealthy() {
        try {
            WebDriver driver = driverThreadLocal.get();
            if (driver == null) {
                return false;
            }
            
            // Multiple health checks for better reliability
            
            // 1. Check if session ID is valid
            String sessionId = ((ChromeDriver) driver).getSessionId().toString();
            if (sessionId == null || sessionId.isEmpty()) {
                logger.warn("Browser session ID is invalid");
                return false;
            }
            
            // 2. Try to get current URL to check if browser is responsive
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl == null) {
                logger.warn("Browser returned null URL");
                return false;
            }
            
            // 3. Try a simple JavaScript execution
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object result = js.executeScript("return document.readyState");
            if (result == null) {
                logger.warn("Browser JavaScript execution failed");
                return false;
            }
            
            logger.debug("Browser health check passed. Session: {}, URL: {}, ReadyState: {}", 
                        sessionId.substring(0, 8) + "...", currentUrl, result);
            return true;
            
        } catch (Exception e) {
            logger.warn("Browser health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Restart browser if it's not healthy
     * @return true if browser was restarted successfully
     */
    /**
     * Tarayıcı sağlıksız ise yeniden başlatmayı dener.
     */
    public static boolean restartBrowserIfNeeded() {
        if (!isBrowserHealthy()) {
            logger.warn("Browser is not healthy, attempting to restart...");
            return forceRestartBrowser();
        }
        return true;
    }
    
    /**
     * Force restart browser regardless of health status
     * @return true if browser was restarted successfully
     */
    /**
     * Tarayıcıyı zorla yeniden başlatır (kapat-aç) ve sağlık doğrulaması yapar.
     */
    public static boolean forceRestartBrowser() {
        try {
            logger.info("Forcing browser restart...");
            
            // Try graceful shutdown first
            try {
                quitDriver();
            } catch (Exception e) {
                logger.warn("Graceful driver quit failed, continuing with restart: {}", e.getMessage());
            }
            
            // Clear the thread local
            driverThreadLocal.remove();
            
            // Wait a moment for cleanup
            Thread.sleep(1000);
            
            // Initialize new driver
            initializeDriver();
            
            // Verify new driver is healthy
            if (isBrowserHealthy()) {
                logger.info("Browser restarted successfully");
                return true;
            } else {
                logger.error("Browser restart failed - new instance is not healthy");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Failed to restart browser: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Attempt to recover from session issues
     * @return true if recovery was successful
     */
    /**
     * Oturum problemlerinden toparlanmak için çeşitli stratejiler uygular.
     */
    public static boolean recoverSession() {
        logger.info("Attempting session recovery...");
        
        // Try different recovery strategies
        
        // Strategy 1: Simple refresh and health check
        try {
            WebDriver driver = driverThreadLocal.get();
            if (driver != null) {
                driver.navigate().refresh();
                Thread.sleep(2000);
                if (isBrowserHealthy()) {
                    logger.info("Session recovered with refresh");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Refresh recovery failed: {}", e.getMessage());
        }
        
        // Strategy 2: Navigate back to a known good page
        try {
            WebDriver driver = driverThreadLocal.get();
            if (driver != null) {
                driver.navigate().to("about:blank");
                Thread.sleep(1000);
                if (isBrowserHealthy()) {
                    logger.info("Session recovered with navigation to blank");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.debug("Navigation recovery failed: {}", e.getMessage());
        }
        
        // Strategy 3: Force restart as last resort
        logger.warn("All recovery strategies failed, forcing browser restart");
        forceRestartBrowser();
        return true;
    }
    

}