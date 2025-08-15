package com.test.pages;

import com.test.utils.DriverManager;
import com.test.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WaitUtils waitUtils;
    protected Logger logger;
    
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitUtils = new WaitUtils(driver);
        this.logger = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.waitUtils = new WaitUtils(driver);
        this.logger = LogManager.getLogger(this.getClass());
        PageFactory.initElements(driver, this);
    }
    
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        logger.debug("Element is visible: " + element.toString());
    }
    
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        logger.debug("Element is clickable: " + element.toString());
    }
    
    protected void waitForElementToBeInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        logger.debug("Element is invisible: " + locator.toString());
    }
    
    protected void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
        logger.info("Clicked on element: " + element.toString());
    }
    
    protected void sendKeys(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        logger.info("Entered text '" + text + "' into element: " + element.toString());
    }
    
    protected String getText(WebElement element) {
        waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Got text '" + text + "' from element: " + element.toString());
        return text;
    }
    
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element is not displayed: " + element.toString());
            return false;
        }
    }
    
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }
    
    protected void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: " + element.toString());
    }
    
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Page title: " + title);
        return title;
    }
    
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: " + url);
        return url;
    }
    
    // Additional methods needed by page classes
    protected boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element is not enabled: " + element.toString());
            return false;
        }
    }
    
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
    
    protected void selectDropdownByValue(WebElement element, String value) {
        try {
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected dropdown option by value: {}", value);
        } catch (Exception e) {
            logger.error("Failed to select dropdown option by value '{}': {}", value, e.getMessage());
        }
    }
    
    protected void selectDropdownByText(WebElement element, String text) {
        try {
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.info("Selected dropdown option by text: {}", text);
        } catch (Exception e) {
            logger.error("Failed to select dropdown option by text '{}': {}", text, e.getMessage());
        }
    }
    
    protected void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }
    
    protected void navigateForward() {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }
    
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }
}