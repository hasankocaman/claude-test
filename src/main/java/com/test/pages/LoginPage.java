package com.test.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    
    @FindBy(css = "input[name='username']")
    private WebElement usernameField;
    
    @FindBy(css = "input[name='password']")
    private WebElement passwordField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(css = ".success-message")
    private WebElement successMessage;
    
    @FindBy(css = ".error-message")
    private WebElement errorMessage;
    
    public void enterUsername(String username) {
        logger.info("Entering username: " + username);
        sendKeys(usernameField, username);
    }
    
    public void enterPassword(String password) {
        logger.info("Entering password");
        sendKeys(passwordField, password);
    }
    
    public void clickLoginButton() {
        logger.info("Clicking login button");
        click(loginButton);
    }
    
    public boolean isLoginSuccessful() {
        boolean isSuccessful = isElementDisplayed(successMessage);
        logger.info("Login successful: " + isSuccessful);
        return isSuccessful;
    }
    
    public boolean isLoginFailed() {
        boolean isFailed = isElementDisplayed(errorMessage);
        logger.info("Login failed: " + isFailed);
        return isFailed;
    }
    
    public void loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
}