package com.test.pages;

import com.test.utils.CommonUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Amazon Ana Sayfa Page Object.
 * Arama kutusu/butonunu kullanarak ürün arama, bot/captcha ekranını toleranslı şekilde
 * atlatma ve temel gezinme işlemlerini kapsar.
 */
public class HomePage extends BasePage {

    // Amazon'un gerçek arama alanı ve butonu - Multiple locator strategy
    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;
    
    // Alternative search box locators
    @FindBy(css = "input[name='field-keywords']")
    private WebElement searchBoxAlt1;
    
    @FindBy(xpath = "//input[@placeholder='Search Amazon']")
    private WebElement searchBoxAlt2;
    
    @FindBy(css = "input[type='text'][aria-label*='Search']")
    private WebElement searchBoxAlt3;

    @FindBy(id = "nav-search-submit-button")
    private WebElement searchButton;
    
    // Alternative search button locators
    @FindBy(css = "input[type='submit'][value='Go']")
    private WebElement searchButtonAlt1;
    
    @FindBy(xpath = "//input[@type='submit' and contains(@class,'nav-input')]")
    private WebElement searchButtonAlt2;

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

    /**
     * Varsayılan yapıcı; mevcut sürücüyü kullanır.
     */
    public HomePage() {
        super();
    }
    
    /**
     * Sürücü alan yapıcı.
     * @param driver aktif WebDriver
     */
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
    /**
     * Bot/captcha ekranı görünürse "Continue shopping" düğmesine tıklar; görünmüyorsa sessizce devam eder.
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

    /**
     * Verilen terimle Amazon'da arama yapar. Birden fazla locator stratejisi dener ve başarısızlıkta Enter tuşu ile tetikler.
     * @param searchTerm aranacak metin
     */
    public void searchFor(String searchTerm) {
        // Her kritik aksiyondan önce bot ekranını kontrol et
        handleBotCheckIfPresent();

        logger.info("Searching for: " + searchTerm);
        
        // Try multiple search box locators
        WebElement activeSearchBox = findActiveSearchBox();
        if (activeSearchBox != null) {
            sendKeysWithRetry(activeSearchBox, searchTerm, "search box");
            
            // Wait for search box to be filled and stable
            CommonUtils.waitFor(1);
            
            // Try multiple search button locators
            WebElement activeSearchButton = findActiveSearchButton();
            if (activeSearchButton != null) {
                clickWithRetry(activeSearchButton, "search button");
            } else {
                // Fallback: press Enter on search box
                logger.warn("Search button not found, pressing Enter on search box");
                activeSearchBox.sendKeys(Keys.ENTER);
            }
        } else {
            throw new RuntimeException("Could not find any search box element");
        }
    }
    
    /**
     * Find the active search box using multiple locator strategies
     */
    private WebElement findActiveSearchBox() {
        WebElement[] searchBoxes = {searchBox, searchBoxAlt1, searchBoxAlt2, searchBoxAlt3};
        
        for (WebElement box : searchBoxes) {
            try {
                if (box != null && box.isDisplayed() && box.isEnabled()) {
                    logger.debug("Found active search box: " + box.toString());
                    return box;
                }
            } catch (Exception e) {
                // Continue to next locator
                logger.debug("Search box not available: " + e.getMessage());
            }
        }
        
        // Try by direct locator search
        try {
            WebElement directBox = driver.findElement(By.id("twotabsearchtextbox"));
            if (directBox.isDisplayed() && directBox.isEnabled()) {
                return directBox;
            }
        } catch (Exception e) {
            logger.debug("Direct search box lookup failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find the active search button using multiple locator strategies
     */
    private WebElement findActiveSearchButton() {
        WebElement[] searchButtons = {searchButton, searchButtonAlt1, searchButtonAlt2};
        
        for (WebElement button : searchButtons) {
            try {
                if (button != null && button.isDisplayed() && button.isEnabled()) {
                    logger.debug("Found active search button: " + button.toString());
                    return button;
                }
            } catch (Exception e) {
                // Continue to next locator
                logger.debug("Search button not available: " + e.getMessage());
            }
        }
        
        // Try by direct locator search
        try {
            WebElement directButton = driver.findElement(By.id("nav-search-submit-button"));
            if (directButton.isDisplayed() && directButton.isEnabled()) {
                return directButton;
            }
        } catch (Exception e) {
            logger.debug("Direct search button lookup failed: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Arama sonuçlarının görünüp görünmediğini döner.
     */
    public boolean areSearchResultsDisplayed() {
        boolean isDisplayed = isElementDisplayed(searchResults);
        logger.info("Search results displayed: " + isDisplayed);
        return isDisplayed;
    }

    /**
     * Arama sonuç metnini döner.
     */
    public String getSearchResultsText() {
        String resultsText = getText(searchResults);
        logger.info("Search results text: " + resultsText);
        return resultsText;
    }

    /**
     * Üst bardaki giriş bağlantısına tıklar (gerekirse bot ekranını temizler).
     */
    public void clickLoginLink() {
        // Login'e gitmeden önce de güvenlik ekranını temizle
        handleBotCheckIfPresent();

        logger.info("Clicking login link");
        click(loginLink);
    }
}
