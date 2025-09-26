package com.test.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Basic wait utilities class for BasePage compatibility.
 * Provides basic waiting functionality while the enhanced version has more advanced features.
 */
public class WaitUtils {
    
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private static final Duration DEFAULT_SLEEP_STEP = Duration.ofMillis(250);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 20;

    /**
     * Static helper to pause without relying on Thread.sleep.
     * Uses Selenium's Sleeper implementation to stay consistent with WebDriver waits.
     *
     * @param duration Requested pause duration
     */
    public static void sleep(Duration duration) {
        Duration effectiveDuration = duration == null || duration.isNegative()
                ? DEFAULT_SLEEP_STEP
                : duration;
        try {
            Sleeper.SYSTEM_SLEEPER.sleep(effectiveDuration);
            logger.debug("Paused execution for {} ms", effectiveDuration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted after {} ms", effectiveDuration.toMillis());
        }
    }

    /**
     * Convenience wrapper for sleeping with seconds granularity.
     *
     * @param seconds seconds to pause
     */
    public static void sleepSeconds(long seconds) {
        sleep(Duration.ofSeconds(Math.max(0, seconds)));
    }

    /**
     * Convenience wrapper for sleeping with millisecond granularity.
     *
     * @param millis milliseconds to pause
     */
    public static void sleepMillis(long millis) {
        sleep(Duration.ofMillis(Math.max(0, millis)));
    }
    
    /**
     * Constructor with WebDriver
     * @param driver WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }
    
    /**
     * Wait for element to be visible
     * @param element WebElement to wait for
     * @return WebElement that is visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            logger.error("Element did not become visible within {} seconds", DEFAULT_TIMEOUT);
            throw e;
        }
    }
    
    /**
     * Wait for element to be visible by locator
     * @param locator By locator
     * @return WebElement that is visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element did not become visible within {} seconds: {}", DEFAULT_TIMEOUT, locator);
            throw e;
        }
    }
    
    /**
     * Wait for element to be clickable
     * @param element WebElement to wait for
     * @return WebElement that is clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            logger.error("Element did not become clickable within {} seconds", DEFAULT_TIMEOUT);
            throw e;
        }
    }
    
    /**
     * Wait for element to be clickable by locator
     * @param locator By locator
     * @return WebElement that is clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            logger.error("Element did not become clickable within {} seconds: {}", DEFAULT_TIMEOUT, locator);
            throw e;
        }
    }
    
    /**
     * Wait for element to be present
     * @param locator By locator
     * @return WebElement that is present
     */
    public WebElement waitForElementToBePresent(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element did not become present within {} seconds: {}", DEFAULT_TIMEOUT, locator);
            throw e;
        }
    }
    
    /**
     * Wait for page to load
     */
    public void waitForPageToLoad() {
        try {
            wait.until(webDriver -> 
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
            );
        } catch (TimeoutException e) {
            logger.warn("Page did not finish loading within {} seconds", DEFAULT_TIMEOUT);
        }
    }
    
    /**
     * Wait for element to disappear
     * @param locator By locator
     * @return true if element disappeared
     */
    public boolean waitForElementToDisappear(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.warn("Element did not disappear within {} seconds: {}", DEFAULT_TIMEOUT, locator);
            return false;
        }
    }
    
    /**
     * Wait for text to be present in element
     * @param locator By locator
     * @param text Text to wait for
     * @return true if text is present
     */
    public boolean waitForTextToBePresentInElement(By locator, String text) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            logger.error("Text '{}' did not appear in element within {} seconds: {}", text, DEFAULT_TIMEOUT, locator);
            return false;
        }
    }
    
    /**
     * Simple wait
     * @param seconds Seconds to wait
     */
    public void waitFor(int seconds) {
        CommonUtils.waitFor(seconds);
    }
}
