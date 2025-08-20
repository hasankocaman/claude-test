package com.test.utils;

import com.test.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Selenium test otomasyonu için ortak yardımcı sınıf.
 * Beklemeler, metin/numara-fiyat çıkarımı, JS etkileşimleri, ekran görüntüleri,
 * koleksiyon ve sistem yardımcıları gibi yeniden kullanılabilir statik metodlar içerir.
 */
public class CommonUtils {
    
    private static final Logger logger = LogManager.getLogger(CommonUtils.class);
    
    // Constants
    private static final int DEFAULT_TIMEOUT = 10;
    private static final String SCREENSHOTS_DIR = "target/screenshots";
    private static final String REPORTS_DIR = "target/reports";
    
    // Price extraction patterns
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\$?([0-9,]+(?:\\.[0-9]{1,2})?)");
    private static final Pattern ENHANCED_PRICE_PATTERN = Pattern.compile("([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    
    // Date formats
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private CommonUtils() {
        // Utility class - private constructor
    }
    
    // Wait Utilities
    
    /**
     * Wait for specified amount of time
     * @param seconds Time to wait in seconds
     */
    public static void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Wait for specified amount of time in milliseconds
     * @param milliseconds Time to wait in milliseconds
     */
    public static void waitForMillis(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            logger.debug("Waited for {} milliseconds", milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }
    
    // String Utilities
    
    /**
     * Check if text contains any of the specified keywords (case insensitive)
     * @param text Text to search in
     * @param keywords Keywords to search for
     * @return true if any keyword is found
     */
    public static boolean containsAnyKeyword(String text, String... keywords) {
        if (text == null || keywords == null) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (keyword != null && lowerText.contains(keyword.toLowerCase())) {
                logger.debug("Found keyword '{}' in text: {}", keyword, text);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if text contains all of the specified keywords (case insensitive)
     * @param text Text to search in
     * @param keywords Keywords to search for
     * @return true if all keywords are found
     */
    public static boolean containsAllKeywords(String text, String... keywords) {
        if (text == null || keywords == null) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (keyword == null || !lowerText.contains(keyword.toLowerCase())) {
                return false;
            }
        }
        logger.debug("Found all keywords in text: {}", text);
        return true;
    }
    
    /**
     * Extract numbers from text
     * @param text Text containing numbers
     * @return First number found as string, empty if none found
     */
    public static String extractNumbers(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        Matcher matcher = NUMBER_PATTERN.matcher(text.replaceAll("[,\\s]", ""));
        if (matcher.find()) {
            String number = matcher.group();
            logger.debug("Extracted number '{}' from text: {}", number, text);
            return number;
        }
        
        logger.debug("No numbers found in text: {}", text);
        return "";
    }
    
    /**
     * Extract price from text (supports $1,234.56 format)
     * @param priceText Text containing price
     * @return Price as double, 0.0 if not found
     */
    public static double extractPriceFromText(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            // Try multiple price extraction approaches
            String cleanText = priceText.trim();
            
            // First try standard price pattern
            Matcher matcher = PRICE_PATTERN.matcher(cleanText);
            if (matcher.find()) {
                String priceStr = matcher.group(1).replace(",", "");
                double price = Double.parseDouble(priceStr);
                if (price > 0) {
                    logger.debug("Extracted price {} from text using PRICE_PATTERN: {}", price, priceText);
                    return price;
                }
            }
            
            // Try enhanced pattern without $ symbol
            matcher = ENHANCED_PRICE_PATTERN.matcher(cleanText);
            if (matcher.find()) {
                String priceStr = matcher.group(1).replace(",", "");
                double price = Double.parseDouble(priceStr);
                if (price > 0) {
                    logger.debug("Extracted price {} from text using ENHANCED_PRICE_PATTERN: {}", price, priceText);
                    return price;
                }
            }
            
            // Try to find any decimal number that could be a price (greater than $10)
            Pattern fallbackPattern = Pattern.compile("([0-9,]+\\.[0-9]{2})");
            matcher = fallbackPattern.matcher(cleanText);
            if (matcher.find()) {
                String priceStr = matcher.group(1).replace(",", "");
                double price = Double.parseDouble(priceStr);
                if (price >= 10.0) { // Reasonable minimum price threshold
                    logger.debug("Extracted price {} from text using fallback pattern: {}", price, priceText);
                    return price;
                }
            }
            
            // Try to find integer prices (like 1234, 999, etc.)
            Pattern intPattern = Pattern.compile("([0-9,]{3,})");
            matcher = intPattern.matcher(cleanText.replaceAll("\\$", ""));
            if (matcher.find()) {
                String priceStr = matcher.group(1).replace(",", "");
                if (priceStr.length() >= 3) { // At least 3 digits for reasonable price
                    double price = Double.parseDouble(priceStr);
                    if (price >= 100.0) { // Reasonable minimum price threshold
                        logger.debug("Extracted price {} from text using integer pattern: {}", price, priceText);
                        return price;
                    }
                }
            }
            
        } catch (Exception e) {
            logger.debug("Failed to extract price from text '{}': {}", priceText, e.getMessage());
        }
        
        logger.debug("No price found in text: {}", priceText);
        return 0.0;
    }
    
    /**
     * Format price as currency string
     * @param price Price value
     * @return Formatted price string (e.g., "$1,234.56")
     */
    public static String formatPrice(double price) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(price);
    }
    
    /**
     * Generate random string with specified length
     * @param length Length of random string
     * @return Random string
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return result.toString();
    }
    
    // Date and Time Utilities
    
    /**
     * Get current timestamp as formatted string
     * @return Current timestamp
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
    
    /**
     * Get current date for file naming
     * @return Date string in format yyyy-MM-dd_HH-mm-ss
     */
    public static String getCurrentDateForFileName() {
        return DATE_FORMAT.format(new Date());
    }
    
    // File Utilities
    
    /**
     * Create directory if it doesn't exist
     * @param directoryPath Path to directory
     * @return true if directory exists or was created successfully
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created directory: {}", directoryPath);
            }
            return true;
        } catch (IOException e) {
            logger.error("Failed to create directory {}: {}", directoryPath, e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete file if it exists
     * @param filePath Path to file
     * @return true if file was deleted or doesn't exist
     */
    public static boolean deleteFileIfExists(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Deleted file: {}", filePath);
            }
            return true;
        } catch (IOException e) {
            logger.error("Failed to delete file {}: {}", filePath, e.getMessage());
            return false;
        }
    }
    
    // Screenshot Utilities
    
    /**
     * Take screenshot and save to file
     * @param driver WebDriver instance
     * @param fileName Screenshot file name (without extension)
     * @return Path to saved screenshot file
     */
    public static String takeScreenshot(WebDriver driver, String fileName) {
        try {
            // Create screenshots directory
            createDirectoryIfNotExists(SCREENSHOTS_DIR);
            
            // Take screenshot
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
            
            // Generate unique filename with timestamp
            String timestamp = getCurrentDateForFileName();
            String fullFileName = fileName + "_" + timestamp + ".png";
            String screenshotPath = SCREENSHOTS_DIR + File.separator + fullFileName;
            
            // Copy screenshot to destination
            Files.copy(sourceFile.toPath(), Paths.get(screenshotPath));
            
            logger.info("Screenshot saved: {}", screenshotPath);
            return screenshotPath;
            
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Take screenshot on test failure
     * @param driver WebDriver instance
     * @param testName Test method name
     * @return Path to saved screenshot file
     */
    public static String takeScreenshotOnFailure(WebDriver driver, String testName) {
        return takeScreenshot(driver, "FAILURE_" + testName);
    }
    
    // Dropdown Utilities
    
    /**
     * Get selected option text from dropdown
     * @param dropdownElement Dropdown WebElement
     * @return Selected option text
     */
    public static String getSelectedDropdownOption(WebElement dropdownElement) {
        try {
            Select select = new Select(dropdownElement);
            String selectedText = select.getFirstSelectedOption().getText();
            logger.debug("Selected dropdown option: {}", selectedText);
            return selectedText;
        } catch (Exception e) {
            logger.debug("Failed to get selected dropdown option: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Get all options from dropdown
     * @param dropdownElement Dropdown WebElement
     * @return List of all option texts
     */
    public static List<String> getAllDropdownOptions(WebElement dropdownElement) {
        try {
            Select select = new Select(dropdownElement);
            List<String> options = select.getOptions().stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList());
            logger.debug("Dropdown options: {}", options);
            return options;
        } catch (Exception e) {
            logger.debug("Failed to get dropdown options: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Browser Utilities
    
    /**
     * Get current page title
     * @param driver WebDriver instance
     * @return Current page title
     */
    public static String getCurrentPageTitle(WebDriver driver) {
        String title = driver.getTitle();
        logger.debug("Current page title: {}", title);
        return title;
    }
    
    /**
     * Get current page URL
     * @param driver WebDriver instance
     * @return Current page URL
     */
    public static String getCurrentPageUrl(WebDriver driver) {
        String url = driver.getCurrentUrl();
        logger.debug("Current page URL: {}", url);
        return url;
    }
    
    /**
     * Refresh current page
     * @param driver WebDriver instance
     */
    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }
    
    /**
     * Navigate back
     * @param driver WebDriver instance
     */
    public static void navigateBack(WebDriver driver) {
        driver.navigate().back();
        logger.info("Navigated back");
    }
    
    /**
     * Navigate forward
     * @param driver WebDriver instance
     */
    public static void navigateForward(WebDriver driver) {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }
    
    // JavaScript Utilities
    
    /**
     * Execute JavaScript
     * @param driver WebDriver instance
     * @param script JavaScript code to execute
     * @return Result of script execution
     */
    public static Object executeJavaScript(WebDriver driver, String script) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object result = js.executeScript(script);
            logger.debug("Executed JavaScript: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript '{}': {}", script, e.getMessage());
            return null;
        }
    }
    
    /**
     * Execute JavaScript code with element parameter
     * @param driver WebDriver instance
     * @param script JavaScript code to execute
     * @param element WebElement to pass as argument
     * @return Result of JavaScript execution
     */
    public static Object executeJavaScript(WebDriver driver, String script, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object result = js.executeScript(script, element);
            logger.debug("Executed JavaScript with element: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript '{}' with element: {}", script, e.getMessage());
            return null;
        }
    }
    
    /**
     * Scroll to element using JavaScript
     * @param driver WebDriver instance
     * @param element WebElement to scroll to
     */
    public static void scrollToElement(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            waitForMillis(500); // Small wait for scroll animation
            logger.debug("Scrolled to element");
        } catch (Exception e) {
            logger.warn("Failed to scroll to element: {}", e.getMessage());
        }
    }
    
    /**
     * Click element using JavaScript
     * @param driver WebDriver instance
     * @param element WebElement to click
     */
    public static void clickWithJavaScript(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
            logger.debug("Clicked element using JavaScript");
        } catch (Exception e) {
            logger.error("Failed to click element with JavaScript: {}", e.getMessage());
        }
    }
    
    // Validation Utilities
    
    /**
     * Check if string is valid email address
     * @param email Email address to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailPattern);
    }
    
    /**
     * Check if string is numeric
     * @param str String to check
     * @return true if string contains only numbers
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Collection Utilities
    
    /**
     * Check if list contains any of the specified values (case insensitive)
     * @param list List to search in
     * @param values Values to search for
     * @return true if any value is found
     */
    public static boolean listContainsAny(List<String> list, String... values) {
        if (list == null || list.isEmpty() || values == null) {
            return false;
        }
        
        for (String listItem : list) {
            for (String value : values) {
                if (listItem != null && value != null && 
                    listItem.toLowerCase().contains(value.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Filter list by keyword (case insensitive)
     * @param list List to filter
     * @param keyword Keyword to filter by
     * @return Filtered list
     */
    public static List<String> filterListByKeyword(List<String> list, String keyword) {
        if (list == null || keyword == null) {
            return new ArrayList<>();
        }
        
        return list.stream()
                .filter(item -> item != null && item.toLowerCase().contains(keyword.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // System Utilities
    
    /**
     * Get system property or default value
     * @param propertyName Property name
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static String getSystemProperty(String propertyName, String defaultValue) {
        String value = System.getProperty(propertyName, defaultValue);
        logger.debug("System property '{}' = '{}'", propertyName, value);
        return value;
    }
    
    /**
     * Get environment variable or default value
     * @param variableName Environment variable name
     * @param defaultValue Default value if variable not found
     * @return Environment variable value or default
     */
    public static String getEnvironmentVariable(String variableName, String defaultValue) {
        String value = System.getenv().getOrDefault(variableName, defaultValue);
        logger.debug("Environment variable '{}' = '{}'", variableName, value);
        return value;
    }
    
    // Test Data Utilities
    
    /**
     * Generate test email address
     * @param prefix Email prefix
     * @return Test email address
     */
    public static String generateTestEmail(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String email = prefix + "_" + timestamp + "@test.com";
        logger.debug("Generated test email: {}", email);
        return email;
    }
    
    /**
     * Generate test phone number (US format)
     * @return Test phone number
     */
    public static String generateTestPhoneNumber() {
        Random random = new Random();
        String areaCode = String.format("%03d", 200 + random.nextInt(800)); // 200-999
        String exchange = String.format("%03d", 200 + random.nextInt(800)); // 200-999
        String number = String.format("%04d", random.nextInt(10000)); // 0000-9999
        
        String phoneNumber = String.format("(%s) %s-%s", areaCode, exchange, number);
        logger.debug("Generated test phone number: {}", phoneNumber);
        return phoneNumber;
    }
    
    // Report Utilities
    
    /**
     * Create test report directory
     * @param reportName Report name
     * @return Path to report directory
     */
    public static String createReportDirectory(String reportName) {
        String reportDir = REPORTS_DIR + File.separator + reportName + "_" + getCurrentDateForFileName();
        createDirectoryIfNotExists(reportDir);
        logger.info("Created report directory: {}", reportDir);
        return reportDir;
    }
    
    // Performance Utilities
    
    /**
     * Measure execution time of a Runnable
     * @param task Task to execute
     * @param taskName Name of the task for logging
     * @return Execution time in milliseconds
     */
    public static long measureExecutionTime(Runnable task, String taskName) {
        long startTime = System.currentTimeMillis();
        try {
            task.run();
        } catch (Exception e) {
            logger.error("Task '{}' failed: {}", taskName, e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        logger.info("Task '{}' execution time: {} ms", taskName, executionTime);
        return executionTime;
    }
    
    // Retry Utilities
    
    /**
     * Retry a task with specified attempts
     * @param task Task to retry
     * @param maxAttempts Maximum number of attempts
     * @param delayBetweenAttempts Delay between attempts in milliseconds
     * @param taskName Task name for logging
     * @return true if task succeeded within max attempts
     */
    public static boolean retryTask(Runnable task, int maxAttempts, long delayBetweenAttempts, String taskName) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                task.run();
                logger.info("Task '{}' succeeded on attempt {}", taskName, attempt);
                return true;
            } catch (Exception e) {
                logger.warn("Task '{}' failed on attempt {}: {}", taskName, attempt, e.getMessage());
                
                if (attempt < maxAttempts) {
                    waitForMillis(delayBetweenAttempts);
                }
            }
        }
        
        logger.error("Task '{}' failed after {} attempts", taskName, maxAttempts);
        return false;
    }
}