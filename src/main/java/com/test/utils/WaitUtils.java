package com.test.utils;

import com.test.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Wait Utilities for Selenium WebDriver
 * Provides various wait mechanisms for different scenarios
 */
public class WaitUtils {
    
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    
    private final WebDriver driver;
    private final WebDriverWait defaultWait;
    private final WebDriverWait longWait;
    private final WebDriverWait shortWait;
    
    // Default timeout configurations
    private static final int DEFAULT_TIMEOUT = 10;
    private static final int LONG_TIMEOUT = 30;
    private static final int SHORT_TIMEOUT = 5;
    private static final int POLLING_INTERVAL = 500; // milliseconds
    
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultWait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(LONG_TIMEOUT));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_TIMEOUT));
    }
    
    // Element Visibility Waits
    
    /**
     * Wait for element to be visible
     * @param element WebElement to wait for
     * @return WebElement once visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        return waitForElementToBeVisible(element, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element to be visible with custom timeout
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement once visible
     */
    public WebElement waitForElementToBeVisible(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("Element became visible within {} seconds", timeoutSeconds);
            return visibleElement;
        } catch (TimeoutException e) {
            logger.error("Element did not become visible within {} seconds", timeoutSeconds);
            throw e;
        }
    }
    
    /**
     * Wait for element located by locator to be visible
     * @param locator By locator
     * @return WebElement once visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        return waitForElementToBeVisible(locator, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element located by locator to be visible with custom timeout
     * @param locator By locator
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement once visible
     */
    public WebElement waitForElementToBeVisible(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element {} became visible within {} seconds", locator, timeoutSeconds);
            return visibleElement;
        } catch (TimeoutException e) {
            logger.error("Element {} did not become visible within {} seconds", locator, timeoutSeconds);
            throw e;
        }
    }
    
    // Element Clickability Waits
    
    /**
     * Wait for element to be clickable
     * @param element WebElement to wait for
     * @return WebElement once clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        return waitForElementToBeClickable(element, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element to be clickable with custom timeout
     * @param element WebElement to wait for
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement once clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(element));
            logger.debug("Element became clickable within {} seconds", timeoutSeconds);
            return clickableElement;
        } catch (TimeoutException e) {
            logger.error("Element did not become clickable within {} seconds", timeoutSeconds);
            throw e;
        }
    }
    
    /**
     * Wait for element located by locator to be clickable
     * @param locator By locator
     * @return WebElement once clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(locator, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element located by locator to be clickable with custom timeout
     * @param locator By locator
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement once clickable
     */
    public WebElement waitForElementToBeClickable(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element {} became clickable within {} seconds", locator, timeoutSeconds);
            return clickableElement;
        } catch (TimeoutException e) {
            logger.error("Element {} did not become clickable within {} seconds", locator, timeoutSeconds);
            throw e;
        }
    }
    
    // Element Presence Waits
    
    /**
     * Wait for element presence in DOM
     * @param locator By locator
     * @return WebElement once present in DOM
     */
    public WebElement waitForElementPresence(By locator) {
        return waitForElementPresence(locator, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element presence in DOM with custom timeout
     * @param locator By locator
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebElement once present in DOM
     */
    public WebElement waitForElementPresence(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement presentElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.debug("Element {} became present in DOM within {} seconds", locator, timeoutSeconds);
            return presentElement;
        } catch (TimeoutException e) {
            logger.error("Element {} did not become present in DOM within {} seconds", locator, timeoutSeconds);
            throw e;
        }
    }
    
    // Element Invisibility Waits
    
    /**
     * Wait for element to become invisible
     * @param element WebElement to wait for invisibility
     * @return true when element becomes invisible
     */
    public boolean waitForElementToBeInvisible(WebElement element) {
        return waitForElementToBeInvisible(element, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element to become invisible with custom timeout
     * @param element WebElement to wait for invisibility
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when element becomes invisible
     */
    public boolean waitForElementToBeInvisible(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean invisible = wait.until(ExpectedConditions.invisibilityOf(element));
            logger.debug("Element became invisible within {} seconds", timeoutSeconds);
            return invisible;
        } catch (TimeoutException e) {
            logger.error("Element did not become invisible within {} seconds", timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Wait for element located by locator to become invisible
     * @param locator By locator
     * @return true when element becomes invisible
     */
    public boolean waitForElementToBeInvisible(By locator) {
        return waitForElementToBeInvisible(locator, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element located by locator to become invisible with custom timeout
     * @param locator By locator
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when element becomes invisible
     */
    public boolean waitForElementToBeInvisible(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean invisible = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element {} became invisible within {} seconds", locator, timeoutSeconds);
            return invisible;
        } catch (TimeoutException e) {
            logger.error("Element {} did not become invisible within {} seconds", locator, timeoutSeconds);
            return false;
        }
    }
    
    // Text-based Waits
    
    /**
     * Wait for element to contain specific text
     * @param element WebElement to check
     * @param text Expected text
     * @return true when element contains expected text
     */
    public boolean waitForElementToContainText(WebElement element, String text) {
        return waitForElementToContainText(element, text, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element to contain specific text with custom timeout
     * @param element WebElement to check
     * @param text Expected text
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when element contains expected text
     */
    public boolean waitForElementToContainText(WebElement element, String text, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean hasText = wait.until(ExpectedConditions.textToBePresentInElement(element, text));
            logger.debug("Element contained text '{}' within {} seconds", text, timeoutSeconds);
            return hasText;
        } catch (TimeoutException e) {
            logger.error("Element did not contain text '{}' within {} seconds", text, timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Wait for element located by locator to contain specific text
     * @param locator By locator
     * @param text Expected text
     * @return true when element contains expected text
     */
    public boolean waitForElementToContainText(By locator, String text) {
        return waitForElementToContainText(locator, text, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for element located by locator to contain specific text with custom timeout
     * @param locator By locator
     * @param text Expected text
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when element contains expected text
     */
    public boolean waitForElementToContainText(By locator, String text, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean hasText = wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.debug("Element {} contained text '{}' within {} seconds", locator, text, timeoutSeconds);
            return hasText;
        } catch (TimeoutException e) {
            logger.error("Element {} did not contain text '{}' within {} seconds", locator, text, timeoutSeconds);
            return false;
        }
    }
    
    // Page Load Waits
    
    /**
     * Wait for page to load completely (JavaScript execution complete)
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(LONG_TIMEOUT);
    }
    
    /**
     * Wait for page to load completely with custom timeout
     * @param timeoutSeconds Custom timeout in seconds
     */
    public void waitForPageToLoad(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
            logger.debug("Page loaded completely within {} seconds", timeoutSeconds);
        } catch (TimeoutException e) {
            logger.warn("Page did not load completely within {} seconds", timeoutSeconds);
        }
    }
    
    /**
     * Wait for page title to contain specific text
     * @param titleText Expected title text
     * @return true when page title contains expected text
     */
    public boolean waitForPageTitleToContain(String titleText) {
        return waitForPageTitleToContain(titleText, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for page title to contain specific text with custom timeout
     * @param titleText Expected title text
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when page title contains expected text
     */
    public boolean waitForPageTitleToContain(String titleText, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean titleContains = wait.until(ExpectedConditions.titleContains(titleText));
            logger.debug("Page title contained '{}' within {} seconds", titleText, timeoutSeconds);
            return titleContains;
        } catch (TimeoutException e) {
            logger.error("Page title did not contain '{}' within {} seconds", titleText, timeoutSeconds);
            return false;
        }
    }
    
    /**
     * Wait for URL to contain specific text
     * @param urlText Expected URL text
     * @return true when URL contains expected text
     */
    public boolean waitForUrlToContain(String urlText) {
        return waitForUrlToContain(urlText, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for URL to contain specific text with custom timeout
     * @param urlText Expected URL text
     * @param timeoutSeconds Custom timeout in seconds
     * @return true when URL contains expected text
     */
    public boolean waitForUrlToContain(String urlText, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            boolean urlContains = wait.until(ExpectedConditions.urlContains(urlText));
            logger.debug("URL contained '{}' within {} seconds", urlText, timeoutSeconds);
            return urlContains;
        } catch (TimeoutException e) {
            logger.error("URL did not contain '{}' within {} seconds", urlText, timeoutSeconds);
            return false;
        }
    }
    
    // Custom Condition Waits
    
    /**
     * Wait for custom condition with default timeout
     * @param condition Custom expected condition
     * @param <T> Return type of condition
     * @return Result of condition when met
     */
    public <T> T waitForCondition(ExpectedCondition<T> condition) {
        return waitForCondition(condition, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for custom condition with custom timeout
     * @param condition Custom expected condition
     * @param timeoutSeconds Custom timeout in seconds
     * @param <T> Return type of condition
     * @return Result of condition when met
     */
    public <T> T waitForCondition(ExpectedCondition<T> condition, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            T result = wait.until(condition);
            logger.debug("Custom condition met within {} seconds", timeoutSeconds);
            return result;
        } catch (TimeoutException e) {
            logger.error("Custom condition not met within {} seconds", timeoutSeconds);
            throw e;
        }
    }
    
    // Fluent Wait
    
    /**
     * Create fluent wait with custom polling and ignore exceptions
     * @param timeoutSeconds Timeout in seconds
     * @param pollingIntervalMillis Polling interval in milliseconds
     * @param exceptionsToIgnore Exceptions to ignore during wait
     * @return FluentWait instance
     */
    public FluentWait<WebDriver> createFluentWait(int timeoutSeconds, long pollingIntervalMillis, 
                                                  Class<? extends Throwable>... exceptionsToIgnore) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingIntervalMillis));
        
        if (exceptionsToIgnore != null && exceptionsToIgnore.length > 0) {
            fluentWait.ignoreAll(java.util.Arrays.asList(exceptionsToIgnore));
        } else {
            // Default exceptions to ignore
            fluentWait.ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(ElementNotInteractableException.class);
        }
        
        logger.debug("Created fluent wait with {}s timeout and {}ms polling", timeoutSeconds, pollingIntervalMillis);
        return fluentWait;
    }
    
    /**
     * Default fluent wait with standard settings
     * @return FluentWait instance with default settings
     */
    public FluentWait<WebDriver> createDefaultFluentWait() {
        return createFluentWait(DEFAULT_TIMEOUT, POLLING_INTERVAL);
    }
    
    // Alert Waits
    
    /**
     * Wait for alert to be present
     * @return Alert when present
     */
    public Alert waitForAlert() {
        return waitForAlert(DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for alert to be present with custom timeout
     * @param timeoutSeconds Custom timeout in seconds
     * @return Alert when present
     */
    public Alert waitForAlert(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            logger.debug("Alert appeared within {} seconds", timeoutSeconds);
            return alert;
        } catch (TimeoutException e) {
            logger.error("Alert did not appear within {} seconds", timeoutSeconds);
            throw e;
        }
    }
    
    // Frame Waits
    
    /**
     * Wait for frame to be available and switch to it
     * @param frameLocator Frame locator
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitchToIt(By frameLocator) {
        return waitForFrameAndSwitchToIt(frameLocator, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for frame to be available and switch to it with custom timeout
     * @param frameLocator Frame locator
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitchToIt(By frameLocator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebDriver frameDriver = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
            logger.debug("Switched to frame {} within {} seconds", frameLocator, timeoutSeconds);
            return frameDriver;
        } catch (TimeoutException e) {
            logger.error("Frame {} was not available within {} seconds", frameLocator, timeoutSeconds);
            throw e;
        }
    }
    
    /**
     * Wait for frame to be available and switch to it by element
     * @param frameElement Frame WebElement
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitchToIt(WebElement frameElement) {
        return waitForFrameAndSwitchToIt(frameElement, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for frame to be available and switch to it by element with custom timeout
     * @param frameElement Frame WebElement
     * @param timeoutSeconds Custom timeout in seconds
     * @return WebDriver focused on frame
     */
    public WebDriver waitForFrameAndSwitchToIt(WebElement frameElement, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebDriver frameDriver = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
            logger.debug("Switched to frame element within {} seconds", timeoutSeconds);
            return frameDriver;
        } catch (TimeoutException e) {
            logger.error("Frame element was not available within {} seconds", timeoutSeconds);
            throw e;
        }
    }
    
    // Utility Methods
    
    /**
     * Sleep for specified milliseconds (should be used sparingly)
     * @param milliseconds Time to sleep
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            logger.debug("Slept for {} milliseconds", milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Sleep for specified seconds (should be used sparingly)
     * @param seconds Time to sleep in seconds
     */
    public static void sleep(int seconds) {
        sleep(seconds * 1000L);
    }
    
    /**
     * Check if element is present on page without waiting
     * @param locator Element locator
     * @return true if element exists, false otherwise
     */
    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    /**
     * Check if element is visible without waiting
     * @param element WebElement to check
     * @return true if element is visible, false otherwise
     */
    public boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element is clickable without waiting
     * @param element WebElement to check
     * @return true if element is clickable, false otherwise
     */
    public boolean isElementClickable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Getter methods for different wait instances
    public WebDriverWait getDefaultWait() {
        return defaultWait;
    }
    
    public WebDriverWait getLongWait() {
        return longWait;
    }
    
    public WebDriverWait getShortWait() {
        return shortWait;
    }
}