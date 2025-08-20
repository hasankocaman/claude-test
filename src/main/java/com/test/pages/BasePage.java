package com.test.pages;

import com.test.utils.CommonUtils;
import com.test.utils.DriverManager;
import com.test.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract base class for all Page Objects.
 * Encapsulates common WebDriver, waits, logging and robust interaction helpers
 * (click, sendKeys, getText, dropdown selections, navigation, retries, etc.).
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WaitUtils waitUtils;
    protected Logger logger;
    
    /**
     * Default constructor using current thread's WebDriver.
     * Initializes waits, utilities and PageFactory.
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitUtils = new WaitUtils(driver);
        this.logger = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Constructor with explicit WebDriver.
     * @param driver active WebDriver
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitUtils = new WaitUtils(driver);
        this.logger = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Waits until given element is visible.
     */
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        logger.debug("Element is visible: " + element.toString());
    }
    
    /**
     * Waits until given element is clickable.
     */
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        logger.debug("Element is clickable: " + element.toString());
    }
    
    /**
     * Waits until element located by given locator becomes invisible.
     */
    protected void waitForElementToBeInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        logger.debug("Element is invisible: " + locator.toString());
    }
    
    /**
     * Clicks element after ensuring clickability.
     */
    protected void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
        logger.info("Clicked on element: " + element.toString());
    }
    
    /**
     * Types text into element after visibility and clearing.
     */
    protected void sendKeys(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        logger.info("Entered text '" + text + "' into element: " + element.toString());
    }
    
    /**
     * Returns visible text of element after waiting for visibility.
     */
    protected String getText(WebElement element) {
        waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Got text '" + text + "' from element: " + element.toString());
        return text;
    }
    
    /**
     * Safely checks if element is displayed.
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element is not displayed: " + element.toString());
            return false;
        }
    }
    
    /**
     * Finds all elements matching locator.
     */
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }
    
    /**
     * Scrolls page to bring element into view.
     */
    protected void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: " + element.toString());
    }
    
    /**
     * Gets current page title.
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Page title: " + title);
        return title;
    }
    
    /**
     * Gets current page URL.
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: " + url);
        return url;
    }
    
    // Additional methods needed by page classes
    /**
     * Safely checks if element is enabled.
     */
    protected boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element is not enabled: " + element.toString());
            return false;
        }
    }
    
    /**
     * Gets attribute value from element with visibility and error handling.
     */
    protected String getAttribute(WebElement element, String attribute) {
        try {
            waitForElementToBeVisible(element);
            String value = element.getAttribute(attribute);
            logger.debug("Got attribute '{}' value '{}' from element: {}", attribute, value, element.toString());
            return value;
        } catch (Exception e) {
            logger.debug("Could not get attribute '{}' from element: {}", attribute, element.toString());
            return null;
        }
    }
    
    /**
     * Selects dropdown option by value.
     */
    protected void selectDropdownByValue(WebElement element, String value) {
        try {
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected dropdown option by value: {}", value);
        } catch (Exception e) {
            logger.error("Failed to select dropdown option by value '{}': {}", value, e.getMessage());
        }
    }
    
    /**
     * Selects dropdown option by visible text.
     */
    protected void selectDropdownByText(WebElement element, String text) {
        try {
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.info("Selected dropdown option by text: {}", text);
        } catch (Exception e) {
            logger.error("Failed to select dropdown option by text '{}': {}", text, e.getMessage());
        }
    }
    
    /**
     * Navigates back in browser history.
     */
    protected void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }
    
    /**
     * Navigates forward in browser history.
     */
    protected void navigateForward() {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }
    
    /**
     * Refreshes current page.
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }
    
    // Enhanced Error Handling and Retry Mechanisms
    
    /**
     * Bir eylemi retry mekanizması ile yürütür.
     * @param action çalıştırılacak eylem
     * @param actionName log için ad
     * @param maxRetries maksimum deneme sayısı
     * @param retryDelaySeconds denemeler arası bekleme (s)
     * @return başarılıysa true
     */
    protected boolean executeWithRetry(Runnable action, String actionName, int maxRetries, int retryDelaySeconds) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("Executing '{}' (attempt {}/{})", actionName, attempt, maxRetries);
                action.run();
                logger.debug("'{}' succeeded on attempt {}", actionName, attempt);
                return true;
                
            } catch (Exception e) {
                logger.warn("'{}' failed on attempt {}: {}", actionName, attempt, e.getMessage());
                
                if (attempt < maxRetries) {
                    // Check browser health before retry
                    if (!DriverManager.isBrowserHealthy()) {
                        logger.warn("Browser unhealthy before retry, attempting restart...");
                        DriverManager.restartBrowserIfNeeded();
                    }
                    
                    CommonUtils.waitFor(retryDelaySeconds);
                } else {
                    logger.error("'{}' failed after {} attempts", actionName, maxRetries);
                    throw new RuntimeException("Action '" + actionName + "' failed after " + maxRetries + " attempts: " + e.getMessage(), e);
                }
            }
        }
        return false;
    }
    
    /**
     * Sonuç döndüren eylemi retry mekanizması ile yürütür.
     * @param supplier çalıştırılacak fonksiyon
     * @param actionName log için ad
     * @param maxRetries maksimum deneme sayısı
     * @param retryDelaySeconds denemeler arası bekleme (s)
     * @return fonksiyon sonucu
     */
    protected <T> T executeWithRetry(Supplier<T> supplier, String actionName, int maxRetries, int retryDelaySeconds) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("Executing '{}' (attempt {}/{})", actionName, attempt, maxRetries);
                T result = supplier.get();
                logger.debug("'{}' succeeded on attempt {}", actionName, attempt);
                return result;
                
            } catch (Exception e) {
                lastException = e;
                logger.warn("'{}' failed on attempt {}: {}", actionName, attempt, e.getMessage());
                
                if (attempt < maxRetries) {
                    // Check browser health before retry
                    if (!DriverManager.isBrowserHealthy()) {
                        logger.warn("Browser unhealthy before retry, attempting restart...");
                        DriverManager.restartBrowserIfNeeded();
                    }
                    
                    CommonUtils.waitFor(retryDelaySeconds);
                }
            }
        }
        
        logger.error("'{}' failed after {} attempts", actionName, maxRetries);
        throw new RuntimeException("Action '" + actionName + "' failed after " + maxRetries + " attempts: " + 
                                 (lastException != null ? lastException.getMessage() : "Unknown error"), lastException);
    }
    
    /**
     * Gelişmiş tıklama: retry ve JS fallback içerir.
     */
    protected void clickWithRetry(WebElement element, String elementName) {
        executeWithRetry(() -> {
            try {
                // Wait for element to be clickable
                waitForElementToBeClickable(element);
                
                // Try normal click first
                element.click();
                logger.info("Clicked on element: {}", elementName);
                
            } catch (ElementNotInteractableException e) {
                logger.warn("Normal click failed for '{}', trying JavaScript click: {}", elementName, e.getMessage());
                
                // Fallback to JavaScript click
                CommonUtils.clickWithJavaScript(driver, element);
                logger.info("Clicked on element using JavaScript: {}", elementName);
            }
        }, "Click on " + elementName, 3, 1);
    }
    
    /**
     * Gelişmiş yazma: retry ve JS fallback içerir.
     */
    protected void sendKeysWithRetry(WebElement element, String text, String elementName) {
        executeWithRetry(() -> {
            try {
                waitForElementToBeVisible(element);
                element.clear();
                element.sendKeys(text);
                logger.info("Entered text '{}' into element: {}", text, elementName);
                
            } catch (Exception e) {
                logger.warn("Normal sendKeys failed for '{}', trying JavaScript: {}", elementName, e.getMessage());
                
                // Fallback to JavaScript
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].value = arguments[1];", element, text);
                logger.info("Entered text '{}' using JavaScript into element: {}", text, elementName);
            }
        }, "Send keys to " + elementName, 3, 1);
    }
    
    /**
     * Gelişmiş metin okuma: retry ve attribute fallback içerir.
     */
    protected String getTextWithRetry(WebElement element, String elementName) {
        return executeWithRetry(() -> {
            try {
                waitForElementToBeVisible(element);
                String text = element.getText();
                if (text == null || text.trim().isEmpty()) {
                    // Try alternative methods to get text
                    text = getAttribute(element, "textContent");
                    if (text == null || text.trim().isEmpty()) {
                        text = getAttribute(element, "innerText");
                    }
                }
                logger.debug("Got text '{}' from element: {}", text, elementName);
                return text != null ? text.trim() : "";
                
            } catch (Exception e) {
                logger.warn("Failed to get text from '{}': {}", elementName, e.getMessage());
                return "";
            }
        }, "Get text from " + elementName, 2, 1);
    }
    
    /**
     * Retry içeren element bulma yardımcı metodu.
     */
    protected WebElement findElementWithRetry(By by, String elementName) {
        return executeWithRetry(() -> {
            WebElement element = driver.findElement(by);
            logger.debug("Found element: {}", elementName);
            return element;
        }, "Find element " + elementName, 3, 1);
    }
    
    /**
     * Retry içeren çoklu element bulma yardımcı metodu.
     */
    protected List<WebElement> findElementsWithRetry(By by, String elementName) {
        return executeWithRetry(() -> {
            List<WebElement> elements = driver.findElements(by);
            logger.debug("Found {} elements: {}", elements.size(), elementName);
            return elements;
        }, "Find elements " + elementName, 3, 1);
    }
    
    /**
     * Hata toleranslı görünürlük kontrolü.
     */
    protected boolean isElementDisplayedSafe(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException | NullPointerException e) {
            logger.debug("Element not displayed or not found: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Sayfa içeriği belirli süre değişmediğinde stabil kabul eder.
     * @param stabilityTimeMs stabil kalması gereken süre (ms)
     */
    protected boolean waitForPageStability(long stabilityTimeMs) {
        try {
            String initialContent = driver.getPageSource();
            long startTime = System.currentTimeMillis();
            
            while (System.currentTimeMillis() - startTime < stabilityTimeMs) {
                CommonUtils.waitForMillis(200);
                String currentContent = driver.getPageSource();
                
                if (!initialContent.equals(currentContent)) {
                    // Page changed, reset timer
                    initialContent = currentContent;
                    startTime = System.currentTimeMillis();
                }
            }
            
            logger.debug("Page stability achieved for {} ms", stabilityTimeMs);
            return true;
            
        } catch (Exception e) {
            logger.warn("Error waiting for page stability: {}", e.getMessage());
            return false;
        }
    }
}