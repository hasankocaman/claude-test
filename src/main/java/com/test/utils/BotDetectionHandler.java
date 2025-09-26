package com.test.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * Handles Amazon's bot detection mechanisms and security measures.
 * Provides automated responses to CAPTCHA, unusual traffic warnings, and other bot checks.
 */
public class BotDetectionHandler {
    
    private static final Logger logger = LogManager.getLogger(BotDetectionHandler.class);
    private static final Random random = new Random();
    
    // Bot detection indicators
    private static final String[] BOT_CHECK_INDICATORS = {
        "continue shopping",
        "captcha",
        "unusual traffic",
        "verify you're human", 
        "robot check",
        "sorry, something went wrong",
        "to continue shopping, please type the characters",
        "authentication required"
    };
    
    // Continue shopping button selectors
    private static final By[] CONTINUE_SHOPPING_SELECTORS = {
        By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'continue shopping')]"),
        By.xpath("//input[@value and contains(translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'continue shopping')]"),
        By.xpath("//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'continue shopping')]"),
        By.cssSelector("button[class*='a-button']:contains('Continue shopping')"),
        By.cssSelector("input[value*='Continue shopping']"),
        By.xpath("//button[contains(@class, 'a-button') and contains(., 'Continue')]"),
        By.xpath("//form[@action='/errors/validateCaptcha']//button")
    };
    
    // CAPTCHA-related selectors
    private static final By[] CAPTCHA_SELECTORS = {
        By.id("captchacharacters"),
        By.name("field-keywords"),
        By.xpath("//input[@placeholder='Type characters']"),
        By.cssSelector("input[name='captchaInput']")
    };
    
    /**
     * Main method to detect and handle bot checks
     * @param driver WebDriver instance
     * @return true if bot detection was handled successfully
     */
    public static boolean handleBotDetection(WebDriver driver) {
        logger.info("Checking for bot detection mechanisms");
        
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            
            // Check page source and URL for bot indicators
            for (String indicator : BOT_CHECK_INDICATORS) {
                if (pageSource.contains(indicator.toLowerCase()) || currentUrl.contains("captcha") || currentUrl.contains("challenge")) {
                    logger.warn("Bot detection triggered: {}", indicator);
                    return handleSpecificBotCheck(driver, indicator, pageSource);
                }
            }
            
            // Check for visual indicators
            if (hasVisualBotIndicators(driver)) {
                logger.warn("Visual bot detection indicators found");
                return handleVisualBotCheck(driver);
            }
            
            logger.debug("No bot detection mechanisms detected");
            return true;
            
        } catch (Exception e) {
            logger.error("Error while checking for bot detection: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle specific type of bot check
     */
    private static boolean handleSpecificBotCheck(WebDriver driver, String indicator, String pageSource) {
        logger.info("Handling specific bot check: {}", indicator);
        
        try {
            switch (indicator.toLowerCase()) {
                case "continue shopping":
                    return handleContinueShopping(driver);
                    
                case "captcha":
                case "verify you're human":
                case "robot check":
                    return handleCaptcha(driver);
                    
                case "unusual traffic":
                case "authentication required":
                    return handleUnusualTraffic(driver);
                    
                default:
                    logger.warn("Unknown bot check type: {}", indicator);
                    return handleGenericBotCheck(driver);
            }
        } catch (Exception e) {
            logger.error("Error handling bot check '{}': {}", indicator, e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle "Continue Shopping" button clicks
     */
    private static boolean handleContinueShopping(WebDriver driver) {
        logger.info("Attempting to handle 'Continue Shopping' button");
        
        try {
            // Add human-like delay
            addHumanDelay();
            
            for (By selector : CONTINUE_SHOPPING_SELECTORS) {
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement button = wait.until(ExpectedConditions.elementToBeClickable(selector));
                    
                    if (button.isDisplayed() && button.isEnabled()) {
                        // Scroll to button and add delay
                        scrollToElementSmoothly(driver, button);
                        addHumanDelay();
                        
                        // Click the button
                        button.click();
                        logger.info("Successfully clicked 'Continue Shopping' button using selector: {}", selector);
                        
                        // Wait for navigation
                        CommonUtils.waitFor(3);
                        
                        // Verify we're no longer on the bot check page
                        if (!isOnBotCheckPage(driver)) {
                            logger.info("Successfully bypassed bot check");
                            return true;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Continue shopping selector failed: {} - {}", selector, e.getMessage());
                }
            }
            
            // Try JavaScript click as fallback
            return tryJavaScriptContinue(driver);
            
        } catch (Exception e) {
            logger.error("Failed to handle continue shopping: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle CAPTCHA challenges
     */
    private static boolean handleCaptcha(WebDriver driver) {
        logger.warn("CAPTCHA detected - attempting automated resolution");
        
        try {
            // Look for CAPTCHA input field
            for (By selector : CAPTCHA_SELECTORS) {
                try {
                    WebElement captchaInput = driver.findElement(selector);
                    if (captchaInput.isDisplayed()) {
                        logger.info("Found CAPTCHA input field");
                        
                        // For now, we'll wait and hope it resolves
                        // In production, you might integrate with CAPTCHA solving services
                        addHumanDelay();
                        
                        // Try to find and click any "Continue" or "Submit" buttons
                        return clickContinueAfterCaptcha(driver);
                    }
                } catch (NoSuchElementException ignored) {
                    // Continue to next selector
                }
            }
            
            logger.warn("Could not locate CAPTCHA input field");
            return false;
            
        } catch (Exception e) {
            logger.error("Error handling CAPTCHA: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle unusual traffic warnings
     */
    private static boolean handleUnusualTraffic(WebDriver driver) {
        logger.info("Handling unusual traffic warning");
        
        try {
            // Add longer delay for unusual traffic
            addHumanDelay(5000, 8000);
            
            // Try refreshing the page
            driver.navigate().refresh();
            CommonUtils.waitFor(5);
            
            // Check if the warning is gone
            if (!isOnBotCheckPage(driver)) {
                logger.info("Unusual traffic warning resolved after refresh");
                return true;
            }
            
            // Try going back to Amazon homepage
            driver.get("https://www.amazon.com");
            CommonUtils.waitFor(3);
            
            return !isOnBotCheckPage(driver);
            
        } catch (Exception e) {
            logger.error("Error handling unusual traffic: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Generic bot check handler
     */
    private static boolean handleGenericBotCheck(WebDriver driver) {
        logger.info("Handling generic bot check");
        
        try {
            // First try continue shopping buttons
            if (handleContinueShopping(driver)) {
                return true;
            }
            
            // Then try unusual traffic approach
            if (handleUnusualTraffic(driver)) {
                return true;
            }
            
            // Last resort: wait and retry
            addHumanDelay(10000, 15000);
            return !isOnBotCheckPage(driver);
            
        } catch (Exception e) {
            logger.error("Error in generic bot check handling: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check for visual bot detection indicators
     */
    private static boolean hasVisualBotIndicators(WebDriver driver) {
        try {
            // Check for common bot detection elements
            By[] visualIndicators = {
                By.cssSelector("[data-component-type='captcha']"),
                By.className("a-alert-error"),
                By.xpath("//div[contains(@class, 'error')]//img[contains(@alt, 'CAPTCHA')]"),
                By.xpath("//form[@action='/errors/validateCaptcha']")
            };
            
            for (By indicator : visualIndicators) {
                try {
                    if (driver.findElement(indicator).isDisplayed()) {
                        return true;
                    }
                } catch (NoSuchElementException ignored) {}
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("Error checking visual bot indicators: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle visual bot detection
     */
    private static boolean handleVisualBotCheck(WebDriver driver) {
        logger.info("Handling visual bot detection");
        
        // Try standard approaches
        if (handleContinueShopping(driver)) return true;
        if (handleCaptcha(driver)) return true;
        if (handleUnusualTraffic(driver)) return true;
        
        return false;
    }
    
    /**
     * Check if we're currently on a bot check page
     */
    private static boolean isOnBotCheckPage(WebDriver driver) {
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            
            for (String indicator : BOT_CHECK_INDICATORS) {
                if (pageSource.contains(indicator.toLowerCase()) || 
                    currentUrl.contains("captcha") || 
                    currentUrl.contains("challenge")) {
                    return true;
                }
            }
            
            return hasVisualBotIndicators(driver);
            
        } catch (Exception e) {
            logger.debug("Error checking if on bot check page: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Try JavaScript-based continue button clicking
     */
    private static boolean tryJavaScriptContinue(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            // JavaScript to find and click continue buttons
            String script = 
                "var buttons = document.querySelectorAll('button, input[type=\"submit\"], input[type=\"button\"], a');" +
                "for (var i = 0; i < buttons.length; i++) {" +
                "  var text = buttons[i].textContent || buttons[i].value || buttons[i].getAttribute('aria-label') || '';" +
                "  if (text.toLowerCase().includes('continue') || text.toLowerCase().includes('shopping')) {" +
                "    buttons[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;";
            
            Boolean clicked = (Boolean) js.executeScript(script);
            if (Boolean.TRUE.equals(clicked)) {
                logger.info("Successfully clicked continue button using JavaScript");
                CommonUtils.waitFor(3);
                return !isOnBotCheckPage(driver);
            }
            
        } catch (Exception e) {
            logger.debug("JavaScript continue click failed: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Click continue button after CAPTCHA
     */
    private static boolean clickContinueAfterCaptcha(WebDriver driver) {
        try {
            By[] submitSelectors = {
                By.xpath("//button[@type='submit']"),
                By.xpath("//input[@type='submit']"),
                By.xpath("//button[contains(., 'Continue')]"),
                By.xpath("//button[contains(., 'Submit')]")
            };
            
            for (By selector : submitSelectors) {
                try {
                    WebElement button = driver.findElement(selector);
                    if (button.isDisplayed() && button.isEnabled()) {
                        addHumanDelay();
                        button.click();
                        logger.info("Clicked submit button after CAPTCHA");
                        CommonUtils.waitFor(3);
                        return !isOnBotCheckPage(driver);
                    }
                } catch (NoSuchElementException ignored) {}
            }
            
        } catch (Exception e) {
            logger.debug("Error clicking continue after CAPTCHA: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Smooth scrolling to element
     */
    private static void scrollToElementSmoothly(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            CommonUtils.waitForMillis(1500); // Wait for smooth scroll
        } catch (Exception e) {
            logger.debug("Smooth scroll failed: {}", e.getMessage());
        }
    }
    
    /**
     * Add human-like delay
     */
    private static void addHumanDelay() {
        addHumanDelay(1000, 3000);
    }
    
    /**
     * Add human-like delay with custom range
     */
    private static void addHumanDelay(int minMs, int maxMs) {
        int delay = random.nextInt(maxMs - minMs) + minMs;
        CommonUtils.waitForMillis(delay);
        logger.debug("Added human-like delay: {}ms", delay);
    }
    
    /**
     * Simulate human-like mouse movement (basic implementation)
     */
    public static void simulateHumanBehavior(WebDriver driver) {
        try {
            // Add random delays
            addHumanDelay(500, 2000);
            
            // Scroll randomly
            JavascriptExecutor js = (JavascriptExecutor) driver;
            int scrollAmount = random.nextInt(300) + 100;
            js.executeScript("window.scrollBy(0, " + scrollAmount + ");");
            
            addHumanDelay(1000, 2000);
            
        } catch (Exception e) {
            logger.debug("Error simulating human behavior: {}", e.getMessage());
        }
    }
}
