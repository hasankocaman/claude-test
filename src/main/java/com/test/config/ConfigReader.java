package com.test.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration reader utility.
 * Loads properties from `src/test/resources/config/config.properties` and
 * exposes typed getters to be used across the framework (browser, waits, URL, screenshots, etc.).
 */
public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config/config.properties";
    
    static {
        loadProperties();
    }
    
    /**
     * Loads the configuration properties file into memory.
     * Throws a runtime exception if the file cannot be found or read.
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Configuration file not found at: " + CONFIG_FILE_PATH, e);
        }
    }
    
    /**
     * Returns property value for given key. Trims whitespace.
     * @param key property key
     * @return value for key
     * @throws RuntimeException when key not found
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        }
        throw new RuntimeException("Property '" + key + "' not found in config file");
    }
    
    /**
     * Returns property value for given key or default value when missing.
     * @param key property key
     * @param defaultValue fallback value when key is absent
     * @return resolved value
     */
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? value.trim() : defaultValue;
    }
    
    /**
     * Gets target browser name from configuration. Default is chrome.
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    /**
     * Whether browsers should run headless.
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }
    
    /**
     * Whether the window should be maximized on start.
     */
    public static boolean isWindowMaximize() {
        return Boolean.parseBoolean(getProperty("window.maximize", "true"));
    }
    
    /**
     * Implicit wait duration in seconds.
     */
    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }
    
    /**
     * Explicit wait duration in seconds.
     */
    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "20"));
    }
    
    /**
     * Page load timeout in seconds.
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "30"));
    }
    
    /**
     * Whether to capture screenshots on failures.
     */
    public static boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
    
    /**
     * Base path where screenshots will be saved.
     */
    public static String getScreenshotPath() {
        return getProperty("screenshot.path", "target/screenshots");
    }
    
    /**
     * Base URL of the application under test.
     */
    public static String getBaseUrl() {
        return getProperty("base.url", "https://www.amazon.com/");
    }
}