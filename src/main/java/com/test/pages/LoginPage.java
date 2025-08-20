package com.test.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Basit Login Sayfası Page Object.
 * Kullanıcı adı/şifre alanlarını doldurma, giriş butonuna tıklama ve sonuç durumunu doğrulama işlemlerini içerir.
 */
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
    
    /**
     * Kullanıcı adı alanına değer yazar.
     * @param username kullanıcı adı
     */
    public void enterUsername(String username) {
        logger.info("Entering username: " + username);
        sendKeys(usernameField, username);
    }
    
    /**
     * Şifre alanına değer yazar.
     * @param password şifre
     */
    public void enterPassword(String password) {
        logger.info("Entering password");
        sendKeys(passwordField, password);
    }
    
    /**
     * Giriş butonuna tıklar.
     */
    public void clickLoginButton() {
        logger.info("Clicking login button");
        click(loginButton);
    }
    
    /**
     * Başarılı giriş mesajının görünürlüğüne göre sonucu döner.
     * @return true ise giriş başarılı
     */
    public boolean isLoginSuccessful() {
        boolean isSuccessful = isElementDisplayed(successMessage);
        logger.info("Login successful: " + isSuccessful);
        return isSuccessful;
    }
    
    /**
     * Hata mesajının görünürlüğüne göre sonucu döner.
     * @return true ise giriş başarısız
     */
    public boolean isLoginFailed() {
        boolean isFailed = isElementDisplayed(errorMessage);
        logger.info("Login failed: " + isFailed);
        return isFailed;
    }
    
    /**
     * Kullanıcı adı ve şifre ile giriş akışını tek metotta gerçekleştirir.
     * @param username kullanıcı adı
     * @param password şifre
     */
    public void loginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
}