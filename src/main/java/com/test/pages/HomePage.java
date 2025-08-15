package com.test.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage extends BasePage {

    // Amazon'un gerçek arama alanı ve butonu
    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;

    // Arama sonuçları ana konteyneri
    @FindBy(css = "div.s-main-slot")
    private WebElement searchResults;

    // Oturum açma linki (Amazon üst bar)
    @FindBy(id = "nav-link-accountList")
    private WebElement loginLink;

    // Bot/Capcha ekranındaki "Continue shopping" butonu için esnek locator
    private final By continueShoppingButton =
            By.xpath(
                    // <button class="a-button-text" ...>Continue shopping</button>
                    "//button[contains(@class,'a-button') and " +
                            "(normalize-space()='Continue shopping' or @alt='Continue shopping' or .//span[normalize-space()='Continue shopping'])] " +
                            // Bazı varyasyonlarda <input type='submit' value='Continue shopping'>
                            "| //input[@type='submit' and " +
                            "(@value='Continue shopping' or @alt='Continue shopping' or @aria-label='Continue shopping')]"
            );

    // İsteğe bağlı: bot ekranı başlıkları için bir ipucu (loglamak için)
    private final By botCheckHints = By.xpath("//*[contains(translate(., 'AREYOUROBOTENTERCHARACTERS', 'areyourobotentercharacters'), 'are you a robot') or " +
            "contains(translate(., 'ENTERCHARACTERS', 'entercharacters'), 'enter the characters')]");

    public HomePage() {
        super();
    }
    
    public HomePage(WebDriver driver) {
        super(driver);
    }

    private WebDriverWait shortWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(6));
    }

    /**
     * Amazon bot/captcha ekranı görünürse "Continue shopping" butonuna tıklar.
     * Görünmüyorsa sessizce devam eder.
     */
    public void handleBotCheckIfPresent() {
        try {
            // Hızlı bir presence kontrolü
            List<WebElement> candidates = driver.findElements(continueShoppingButton);
            if (!candidates.isEmpty()) {
                WebElement btn = candidates.get(0);
                logger.info("Bot check tespit edildi. 'Continue shopping' butonuna tıklanacak.");
                shortWait().until(ExpectedConditions.elementToBeClickable(btn));
                try {
                    click(btn); // BasePage.click kullan
                } catch (ElementClickInterceptedException e) {
                    // Gerekirse JS click yedek
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                }
                // Tıklama sonrası ya arama kutusu görünür olmalı ya da buton kaybolmalı
                try {
                    shortWait().until(ExpectedConditions.or(
                            ExpectedConditions.visibilityOf(searchBox),
                            ExpectedConditions.invisibilityOfElementLocated(continueShoppingButton)
                    ));
                } catch (TimeoutException te) {
                    // Bilgilendirici log: bazı durumlarda tam captcha çözümü gerekebilir
                    logger.warn("Bot kontrolü sonrasında sayfa beklenen duruma dönmedi. Manuel doğrulama gerekebilir.");
                }
            } else {
                // İpucu metin varsa logla (zorunlu değil)
                if (!driver.findElements(botCheckHints).isEmpty()) {
                    logger.info("Bot kontrolü ipucu görüldü ancak 'Continue shopping' butonu bulunamadı.");
                }
            }
        } catch (Throwable t) {
            logger.warn("Bot kontrolü işlenirken sorun oluştu: " + t.getMessage());
        }
    }

    public void searchFor(String searchTerm) {
        // Her kritik aksiyondan önce bot ekranını kontrol et
        handleBotCheckIfPresent();

        logger.info("Searching for: " + searchTerm);
        sendKeys(searchBox, searchTerm);
        click(searchButton);
    }

    public boolean areSearchResultsDisplayed() {
        boolean isDisplayed = isElementDisplayed(searchResults);
        logger.info("Search results displayed: " + isDisplayed);
        return isDisplayed;
    }

    public String getSearchResultsText() {
        String resultsText = getText(searchResults);
        logger.info("Search results text: " + resultsText);
        return resultsText;
    }

    public void clickLoginLink() {
        // Login'e gitmeden önce de güvenlik ekranını temizle
        handleBotCheckIfPresent();

        logger.info("Clicking login link");
        click(loginLink);
    }
}
