package com.test.pages;

import com.test.utils.CommonUtils;
import com.test.utils.ErrorRecoveryManager;
import com.test.utils.PerformanceMonitor;
import com.test.utils.WaitUtils;
import com.test.model.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Amazon Arama Sonuçları Page Object.
 * Sonuç listesini bekleme, ürün bilgisi çıkarımı (başlık/fiyat/puan), sıralama ve ürün tıklama
 * işlemlerini içerir. En pahalı MacBook Pro'yu bulma gibi iş mantıkları barındırır.
 */
public class SearchResultsPage extends BasePage {

    // Page Elements using Page Factory

    // Search Results Container
    @FindBy(css = "[data-component-type='s-search-result']")
    private List<WebElement> searchResults;

    @FindBy(css = "h1.a-size-base-plus")
    private WebElement resultsHeader;

    // **Bu container sayfa yüklenmesini beklemek için kullanılıyor**
    @FindBy(css = ".s-result-list")
    private WebElement resultsList;

    // Product Elements - En geniş selector
    @FindBy(css = "span")
    private List<WebElement> productTitles;

    // Updated for new Amazon layout - screenshot'ta gördüğümüz fiyat yapısı
    @FindBy(css = ".a-price-whole, .a-price .a-offscreen")
    private List<WebElement> productPrices;

    @FindBy(css = ".a-price .a-offscreen")
    private List<WebElement> productPriceScreenReader;

    @FindBy(css = "[data-component-type='s-search-result'] .a-rating .a-icon-alt")
    private List<WebElement> productRatings;

    @FindBy(css = "[data-component-type='s-search-result'] .a-size-base")
    private List<WebElement> productReviewCounts;

    // Sorting and Filtering
    @FindBy(css = "[data-action='a-dropdown-select']")
    private WebElement sortDropdown;

    @FindBy(css = "#s-result-sort-select")
    private WebElement sortByDropdown;

    @FindBy(css = "a[data-value='price-desc-rank']")
    private WebElement sortByPriceHighToLow;

    @FindBy(css = "a[data-value='price-asc-rank']")
    private WebElement sortByPriceLowToHigh;

    // Constructor
    public SearchResultsPage(WebDriver driver) {
        super(driver);
        waitForSearchResultsToLoad();
        logger.info("SearchResultsPage initialized");
    }

    /**
     * Arama sonuç sayfasının yüklenmesini bekler.
     */
    private void waitForSearchResultsToLoad() {
        try {
            waitUtils.waitForElementToBeVisible(resultsList);
            waitForPageLoad();
            logger.debug("Search results page loaded");
        } catch (Exception e) {
            logger.warn("Search results may not have loaded completely: {}", e.getMessage());
        }
    }
    
    /**
     * Sayfanın tamamen yüklenmesini bekler.
     */
    private void waitForPageLoad() {
        waitUtils.waitForPageToLoad();
        logger.debug("Page loaded completely");
    }

    /**
     * Toplam arama sonucu sayısını döner.
     */
    public int getSearchResultsCount() {
        int count = searchResults.size();
        logger.info("Total search results found: {}", count);
        return count;
    }

    /**
     * Arama sonuçları görünüyor mu kontrol eder.
     */
    public boolean areSearchResultsDisplayed() {
        boolean displayed = !searchResults.isEmpty() && isElementDisplayed(resultsList);
        logger.debug("Search results displayed: {}", displayed);
        return displayed;
    }

    /**
     * Bu sayfadaki tüm ürün başlıklarını döner.
     */
    public List<String> getProductTitles() {
        logger.info("Extracting product titles using JavaScript method");
        
        try {
            // JavaScript ile direkt DOM'dan çıkarma - daha güvenilir
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            
            String jsScript = "var productTitles = [];" +
                "var allSpans = document.querySelectorAll('span');" +
                "for (var i = 0; i < allSpans.length; i++) {" +
                    "var span = allSpans[i];" +
                    "var text = span.textContent || span.innerText;" +
                    "if (text && text.trim() && text.length > 30) {" +
                        "var lowerText = text.toLowerCase();" +
                        "if ((lowerText.includes('macbook pro') || lowerText.includes('macbook')) && " +
                            "(lowerText.includes('apple') || lowerText.includes('cpu') || lowerText.includes('core') || lowerText.includes('inch'))) {" +
                            "if (!productTitles.includes(text.trim())) {" +
                                "productTitles.push(text.trim());" +
                            "}" +
                        "}" +
                    "}" +
                "}" +
                "return productTitles;";
            
            @SuppressWarnings("unchecked")
            List<String> jsTitles = (List<String>) jsExecutor.executeScript(jsScript);
            
            if (jsTitles != null && !jsTitles.isEmpty()) {
                logger.info("JavaScript extraction successful: {} titles found", jsTitles.size());
                logger.debug("Sample JS titles: {}", jsTitles.stream().limit(3).collect(Collectors.toList()));
                return jsTitles;
            }
            
        } catch (Exception e) {
            logger.warn("JavaScript title extraction failed: {}", e.getMessage());
        }
        
        // Fallback to original method if JavaScript fails
        logger.info("Falling back to WebElement-based extraction");
        List<String> titles = productTitles.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .filter(text -> text != null && !text.trim().isEmpty()) 
                .filter(text -> text.length() > 20) // MacBook Pro başlıkları en az 20 karakter olmalı
                .filter(text -> text.toLowerCase().contains("macbook") || text.toLowerCase().contains("mac book"))
                .collect(Collectors.toList());

        logger.debug("WebElement extraction: {} product titles", titles.size());
        logger.debug("Sample titles: {}", titles.stream().limit(3).collect(Collectors.toList()));
        return titles;
    }

    /**
     * Bu sayfadaki ürün fiyatlarını numerik liste olarak döner.
     */
    public List<Double> getProductPrices() {
        logger.info("Extracting product prices using JavaScript method");
        
        try {
            // JavaScript ile direkt DOM'dan fiyat çıkarma
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            
            String jsScript = "var productPrices = [];" +
                "var allElements = document.querySelectorAll('*');" +
                "for (var i = 0; i < allElements.length; i++) {" +
                    "var element = allElements[i];" +
                    "var text = element.textContent || element.innerText;" +
                    "if (text && text.match && text.match(/^\\$[0-9,]+\\.[0-9]{2}$/)) {" +
                        "var priceMatch = text.match(/\\$([0-9,]+\\.[0-9]{2})/);" +
                        "if (priceMatch && priceMatch[1]) {" +
                            "var priceNum = parseFloat(priceMatch[1].replace(/,/g, ''));" +
                            "if (priceNum >= 1000) {" +
                                "productPrices.push(priceNum);" +
                            "}" +
                        "}" +
                    "}" +
                "}" +
                "return Array.from(new Set(productPrices)).sort(function(a, b) { return b - a; });";
            
            @SuppressWarnings("unchecked")
            List<Number> jsPrices = (List<Number>) jsExecutor.executeScript(jsScript);
            
            if (jsPrices != null && !jsPrices.isEmpty()) {
                List<Double> prices = jsPrices.stream()
                    .map(Number::doubleValue)
                    .collect(Collectors.toList());
                logger.info("JavaScript price extraction successful: {} prices found", prices.size());
                return prices;
            }
            
        } catch (Exception e) {
            logger.warn("JavaScript price extraction failed: {}", e.getMessage());
        }
        
        // Fallback to original method
        logger.info("Falling back to WebElement-based price extraction");
        List<Double> prices = new ArrayList<>();

        // Try to get prices from screen reader elements first (more reliable)
        for (WebElement priceElement : productPriceScreenReader) {
            if (isElementDisplayed(priceElement)) {
                String priceText = getText(priceElement);
                double price = CommonUtils.extractPriceFromText(priceText);
                if (price > 0) {
                    prices.add(price);
                }
            }
        }

        // If screen reader prices are not available, try visible prices
        if (prices.isEmpty()) {
            for (WebElement priceElement : productPrices) {
                if (isElementDisplayed(priceElement)) {
                    String priceText = getText(priceElement);
                    double price = CommonUtils.extractPriceFromText(priceText);
                    if (price > 0) {
                        prices.add(price);
                    }
                }
            }
        }

        logger.info("Retrieved {} product prices", prices.size());
        return prices;
    }

    /**
     * Verilen sonuç indeksine ait ürün bilgisini döner.
     */
    public ProductInfo getProductInfo(int index) {
        if (index < 0 || index >= searchResults.size()) {
            throw new IndexOutOfBoundsException("Product index out of range: " + index);
        }

        WebElement productElement = searchResults.get(index);

        String title = getProductTitleFromElement(productElement);
        double price = getProductPriceFromElement(productElement);
        String rating = getProductRatingFromElement(productElement);
        int reviewCount = getReviewCountFromElement(productElement);
        boolean isSponsored = isProductSponsored(productElement);

        ProductInfo productInfo = new ProductInfo(title, price, rating, reviewCount, isSponsored, index);
        logger.debug("Product info for index {}: {}", index, productInfo);

        return productInfo;
    }

    /**
     * Geçerli sonuçlar içinde en pahalı MacBook Pro'yu bulur.
     * Enhanced with performance monitoring and error recovery
     */
    public ProductInfo findMostExpensiveMacBookPro() {
        return PerformanceMonitor.timeOperation("findMostExpensiveMacBookPro", 
            PerformanceMonitor.PerformanceCategory.BUSINESS_LOGIC, () -> {
                
            return ErrorRecoveryManager.executeWithRecovery(() -> {
                logger.info("Looking for most expensive MacBook Pro");
                logger.info("Search results found: {}", searchResults.size());

                if (searchResults.isEmpty()) {
                    logger.error("No search results found");
                    throw new RuntimeException("No search results found. Please check if search was successful.");
                }

                List<ProductInfo> macBookProducts = new ArrayList<>();

                logger.debug("Processing {} search results", searchResults.size());

                for (int i = 0; i < Math.min(searchResults.size(), 20); i++) { // Limit to first 20 results for performance
                    try {
                        final int index = i; // Make it final for lambda
                        ProductInfo productInfo = ErrorRecoveryManager.executeWithRecovery(
                            () -> getProductInfo(index), 
                            "getProductInfo_" + index, 
                            2, 
                            driver
                        );
                        
                        logger.debug("Product {}: Title='{}', Price=${}", i, productInfo.getTitle(), productInfo.getPrice());

                        // Check if product title contains MacBook Pro (case insensitive)
                        if (CommonUtils.containsAnyKeyword(productInfo.getTitle().toLowerCase(),
                                "macbook pro", "macbook", "mac book")) {
                            macBookProducts.add(productInfo);
                            logger.info("Found MacBook Pro: {} - ${}", productInfo.getTitle(), productInfo.getPrice());
                        }
                    } catch (Exception e) {
                        logger.warn("Error processing search result {}: {}", i, e.getMessage());
                    }
                }

                if (macBookProducts.isEmpty()) {
                    logger.error("No MacBook Pro products found in {} search results", searchResults.size());
                    throw new RuntimeException("No MacBook Pro products found in search results");
                }

                // Find the most expensive one
                ProductInfo mostExpensive = macBookProducts.stream()
                        .filter(p -> p.getPrice() > 0) // Only consider products with valid prices
                        .max(Comparator.comparing(ProductInfo::getPrice))
                        .orElse(macBookProducts.get(0)); // Fallback to first if no prices found

                logger.info("Most expensive MacBook Pro found: {} - ${}",
                        mostExpensive.getTitle(), mostExpensive.getPrice());

                return mostExpensive;
                
            }, "findMostExpensiveMacBookPro", 3, driver);
        });
    }

    /**
     * Verilen indeksteki ürüne tıklar ve ürün detay sayfasına gider.
     */
    public ProductDetailPage clickOnProduct(int index) {
        if (index < 0 || index >= searchResults.size()) {
            throw new IndexOutOfBoundsException("Product index out of range: " + index);
        }

        WebElement productElement = searchResults.get(index);
        WebElement titleLink = findProductLink(productElement);

        if (titleLink != null) {
            String productTitle = getText(titleLink);
            logger.info("Clicking on product {}: {}", index, productTitle);
        } else {
            throw new RuntimeException("Could not find clickable product link for index: " + index);
        }

        // Scroll to element and click
        scrollToElement(titleLink);
        click(titleLink);

        waitUtils.waitForPageToLoad();
        logger.info("Navigated to product detail page");

        return new ProductDetailPage(driver);
    }

    /**
     * En pahalı MacBook Pro ürününe tıklar.
     */
    public ProductDetailPage clickOnMostExpensiveMacBookPro() {
        ProductInfo mostExpensive = findMostExpensiveMacBookPro();
        return clickOnProduct(mostExpensive.getIndex());
    }

    /**
     * Fiyat: yüksekten düşüğe sıralama uygular.
     */
    public void sortByPriceHighToLow() {
        logger.info("Sorting by price: High to Low");

        try {
            // Try clicking the dropdown first
            if (isElementDisplayed(sortByDropdown)) {
                click(sortByDropdown);
                CommonUtils.waitFor(1);
            }

            // Click on the price high to low option
            waitUtils.waitForElementToBeClickable(sortByPriceHighToLow);
            click(sortByPriceHighToLow);

            waitForSearchResultsToLoad();
            logger.info("Successfully sorted by price: High to Low");

        } catch (Exception e) {
            logger.warn("Failed to sort by price high to low using dropdown, trying alternative method");
            sortByPriceAlternative("price-desc-rank");
        }
    }

    /**
     * Fiyat: düşükten yükseğe sıralama uygular.
     */
    public void sortByPriceLowToHigh() {
        logger.info("Sorting by price: Low to High");

        try {
            if (isElementDisplayed(sortByDropdown)) {
                click(sortByDropdown);
                CommonUtils.waitFor(1);
            }

            waitUtils.waitForElementToBeClickable(sortByPriceLowToHigh);
            click(sortByPriceLowToHigh);

            waitForSearchResultsToLoad();
            logger.info("Successfully sorted by price: Low to High");

        } catch (Exception e) {
            logger.warn("Failed to sort by price low to high using dropdown, trying alternative method");
            sortByPriceAlternative("price-asc-rank");
        }
    }

    /**
     * URL parametresi ile alternatif sıralama uygular.
     */
    private void sortByPriceAlternative(String sortValue) {
        try {
            String currentUrl = getCurrentUrl();
            String sortedUrl;

            if (currentUrl.contains("&s=")) {
                sortedUrl = currentUrl.replaceAll("&s=[^&]*", "&s=" + sortValue);
            } else {
                sortedUrl = currentUrl + "&s=" + sortValue;
            }

            driver.navigate().to(sortedUrl);
            waitForSearchResultsToLoad();
            logger.info("Applied sorting using URL method: {}", sortValue);

        } catch (Exception e) {
            logger.error("Failed to apply sorting: {}", e.getMessage());
        }
    }

    /**
     * Marka filtresi uygular (örn. Apple). Placeholder.
     */
    public void applyBrandFilter(String brandName) {
        logger.info("Applying brand filter: {}", brandName);
        // Implementation for brand filtering
        logger.info("Brand filter applied: {}", brandName);
    }

    // Helper Methods

    private String getProductTitleFromElement(WebElement productElement) {
        try {
            // Screenshot'tan gördüğümüz yeni Amazon layout'una göre güncellenmiş selectors
            String[] titleSelectors = {
                    "h2 a span[class*='a-size']",  // Yeni layout - screenshot'ta gördüğümüz
                    "h2 a span",
                    "h2 a",
                    ".a-link-normal .a-size-medium",
                    ".a-link-normal .a-text-normal", 
                    "[data-cy='title-recipe-title']",
                    ".s-title-instructions-style span",
                    ".a-size-base-plus a span",
                    ".s-link-style a span"
            };

            // Try CSS selectors first
            for (String selector : titleSelectors) {
                try {
                    WebElement titleElement = productElement.findElement(By.cssSelector(selector));
                    if (titleElement != null && isElementDisplayed(titleElement)) {
                        String title = getText(titleElement);
                        if (title != null && !title.trim().isEmpty() && !title.equals("Unknown Product")) {
                            logger.debug("Extracted title with selector '{}': {}", selector, title);
                            return title;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

        } catch (Exception e) {
            logger.debug("Could not extract title from product element: {}", e.getMessage());
        }

        logger.debug("No valid title found for product element");
        return "Unknown Product";
    }

    private double getProductPriceFromElement(WebElement productElement) {
        try {
            // Enhanced price selectors with Amazon's latest DOM structure
            String[] priceSelectors = {
                    ".a-price .a-offscreen",                    // Screen reader price text (most reliable)
                    ".a-price-whole",                           // Whole price number
                    ".a-price .a-price-whole",                  // Combined price selector
                    ".a-price-range .a-price .a-offscreen",     // Price range
                    "[data-a-price]",                           // Data attribute price
                    ".a-size-medium.a-color-price",             // Medium size price
                    ".s-price .a-offscreen",                    // Search price off-screen
                    ".s-price",                                 // Search price visible
                    ".a-color-price",                           // Generic price color
                    "[data-testid='price']",                    // Test ID price
                    ".a-price-symbol + .a-price-whole",         // Price after symbol
                    ".a-text-price",                            // Text price class
                    ".a-size-base.a-color-price"                // Base size price
            };

            // Try each selector and extract price with multiple text sources
            for (String selector : priceSelectors) {
                try {
                    List<WebElement> priceElements = productElement.findElements(By.cssSelector(selector));
                    for (WebElement priceElement : priceElements) {
                        if (priceElement != null && isElementDisplayed(priceElement)) {
                            // Try multiple ways to get text from element
                            String[] textSources = {
                                getText(priceElement),                           // Regular text
                                getAttribute(priceElement, "aria-label"),       // Aria label
                                getAttribute(priceElement, "data-a-price"),     // Data attribute
                                getAttribute(priceElement, "title"),            // Title attribute
                                getAttribute(priceElement, "textContent")       // Text content
                            };
                            
                            for (String priceText : textSources) {
                                if (priceText != null && !priceText.trim().isEmpty()) {
                                    logger.debug("Found price text with selector '{}': {}", selector, priceText);

                                    double price = CommonUtils.extractPriceFromText(priceText);
                                    if (price > 0) {
                                        logger.debug("Extracted price: ${} from text: {} using selector: {}", 
                                            price, priceText, selector);
                                        return price;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Selector '{}' failed: {}", selector, e.getMessage());
                }
            }

            // Fallback: Try to extract price from the entire product element text
            try {
                String entireText = getText(productElement);
                if (entireText != null && !entireText.trim().isEmpty()) {
                    double price = CommonUtils.extractPriceFromText(entireText);
                    if (price > 0) {
                        logger.debug("Extracted price ${} from entire element text", price);
                        return price;
                    }
                }
            } catch (Exception e) {
                logger.debug("Failed to extract price from entire element text: {}", e.getMessage());
            }

            // Last resort: Look for any element with dollar signs or price-like patterns
            try {
                List<WebElement> allElements = productElement.findElements(By.xpath(".//*[contains(text(), '$') or contains(text(), ',') or contains(@class, 'price')]"));
                for (WebElement element : allElements) {
                    if (isElementDisplayed(element)) {
                        String text = getText(element);
                        if (text != null && !text.trim().isEmpty()) {
                            double price = CommonUtils.extractPriceFromText(text);
                            if (price > 0) {
                                logger.debug("Extracted price ${} using last resort from: {}", price, text);
                                return price;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("Last resort price extraction failed: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.debug("Could not extract price from product element: {}", e.getMessage());
        }

        logger.warn("No valid price found for product element after trying all strategies");
        return 0.0;
    }

    private String getProductRatingFromElement(WebElement productElement) {
        try {
            WebElement ratingElement = productElement.findElement(By.cssSelector(".a-rating .a-icon-alt"));
            String ratingText = getAttribute(ratingElement, "textContent");
            return ratingText != null ? ratingText : "No rating";
        } catch (Exception e) {
            return "No rating";
        }
    }

    private int getReviewCountFromElement(WebElement productElement) {
        try {
            WebElement reviewElement = productElement.findElement(By.cssSelector(".a-size-base"));
            String reviewText = getText(reviewElement);
            String numbers = CommonUtils.extractNumbers(reviewText);
            return numbers.isEmpty() ? 0 : Integer.parseInt(numbers);
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isProductSponsored(WebElement productElement) {
        String sponsoredAttr = getAttribute(productElement, "data-sponsored");
        return "true".equals(sponsoredAttr);
    }

    /**
     * Çoklu strateji ile tıklanabilir ürün bağlantısını bulur.
     */
    private WebElement findProductLink(WebElement productElement) {
        String[] linkSelectors = {
            "h2 a",
            "h3 a", 
            ".a-link-normal",
            "a[href*='/dp/']",
            "a[href*='/gp/product/']",
            ".s-title-instructions-style a",
            ".a-size-base-plus a",
            ".s-link-style a"
        };
        
        for (String selector : linkSelectors) {
            try {
                WebElement link = productElement.findElement(By.cssSelector(selector));
                if (link != null && isElementDisplayed(link) && isElementEnabled(link)) {
                    logger.debug("Found product link with selector: {}", selector);
                    return link;
                }
            } catch (Exception e) {
                logger.debug("Link selector '{}' failed: {}", selector, e.getMessage());
            }
        }
        
        // Try to find any clickable link in the product element
        try {
            List<WebElement> allLinks = productElement.findElements(By.tagName("a"));
            for (WebElement link : allLinks) {
                if (isElementDisplayed(link) && isElementEnabled(link)) {
                    String href = getAttribute(link, "href");
                    if (href != null && (href.contains("/dp/") || href.contains("/gp/product/"))) {
                        logger.debug("Found product link by href pattern: {}", href);
                        return link;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Generic link search failed: {}", e.getMessage());
        }
        
        logger.warn("Could not find any clickable product link");
        return null;
    }

    // Inner Class for Product Information
    public static class ProductInfo {
        private final String title;
        private final double price;
        private final String rating;
        private final int reviewCount;
        private final boolean isSponsored;
        private final int index;

        public ProductInfo(String title, double price, String rating, int reviewCount, boolean isSponsored, int index) {
            this.title = title;
            this.price = price;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.isSponsored = isSponsored;
            this.index = index;
        }

        public String getTitle() { return title; }
        public double getPrice() { return price; }
        public String getRating() { return rating; }
        public int getReviewCount() { return reviewCount; }
        public boolean isSponsored() { return isSponsored; }
        public int getIndex() { return index; }

        @Override
        public String toString() {
            return String.format("ProductInfo{title='%s', price=%.2f, rating='%s', reviews=%d, sponsored=%s, index=%d}",
                    title, price, rating, reviewCount, isSponsored, index);
        }
    }
    public Product findMostExpensiveProduct(String productKeyword) {
        logger.info(productKeyword + " için en pahalı ürün aranıyor");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        // Yeni Amazon layout için güncellendi - screenshot'ta gördüğümüz yapı
        String[] productContainerSelectors = {
                ".s-result-item",  // Yeni layout
                "[data-component-type='s-search-result']",  // Eski layout fallback
                ".a-section.a-spacing-base"  // Alternative selector
        };
        
        List<WebElement> productElements = new ArrayList<>();
        for (String selector : productContainerSelectors) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                productElements = driver.findElements(By.cssSelector(selector));
                if (!productElements.isEmpty()) {
                    logger.debug("Using product container selector: " + selector);
                    break;
                }
            } catch (Exception e) {
                logger.debug("Product container selector failed: " + selector + " - " + e.getMessage());
            }
        }

        logger.info("Toplam " + productElements.size() + " arama sonucu bulundu");

        Product mostExpensive = null;
        double highestPrice = 0.0;
        int validProductsFound = 0;

        // İlk 20 ürünü kontrol et (performans için)
        int maxProducts = Math.min(productElements.size(), 20);
        for (int i = 0; i < maxProducts; i++) {
            try {
                WebElement productElement = productElements.get(i);
                logger.debug("İşleniyor ürün index: " + i);
                
                // Debug için ürün HTML'ini logla
                try {
                    String productHtml = productElement.getAttribute("outerHTML");
                    logger.debug("Ürün HTML (ilk 200 karakter): " + 
                        (productHtml.length() > 200 ? productHtml.substring(0, 200) + "..." : productHtml));
                } catch (Exception e) {
                    logger.debug("HTML extract edilemedi: " + e.getMessage());
                }
                
                Product product = extractProductInfo(productElement, productKeyword);

                if (product != null) {
                    validProductsFound++;
                    logger.info("Geçerli ürün bulundu #{}: {} - ${}", validProductsFound, product.getName(), product.getPrice());
                    
                    if (product.getPrice() > highestPrice) {
                        highestPrice = product.getPrice();
                        mostExpensive = product;
                        mostExpensive.setElementIndex(i);

                        logger.info("YENİ EN YÜKSEK FİYAT: " + product.getName() + " - $" + product.getPrice());
                    }
                } else {
                    logger.debug("Ürün index {} için geçerli bilgi çıkarılamadı", i);
                }

                // Her 5 üründe bir kısa mola
                if ((i + 1) % 5 == 0) {
                    WaitUtils.sleepMillis(1000);
                }

            } catch (Exception e) {
                logger.warn("Ürün işleme hatası (index " + i + "): " + e.getMessage());
            }
        }

        logger.info("Toplam {} geçerli {} ürünü bulundu", validProductsFound, productKeyword);

        if (mostExpensive == null) {
            logger.error("HİÇBİR GEÇERLİ {} ÜRÜNÜ BULUNAMADI!", productKeyword);
            
            // Debugging için ilk ürünün detaylı analizi
            if (!productElements.isEmpty()) {
                try {
                    WebElement firstProduct = productElements.get(0);
                    logger.error("İlk ürün debug bilgileri:");
                    logger.error("- Element tag: " + firstProduct.getTagName());
                    logger.error("- Element text (ilk 200 kar): " + 
                        (firstProduct.getText().length() > 200 ? firstProduct.getText().substring(0, 200) + "..." : firstProduct.getText()));
                    
                    // Title çıkarma denemesi
                    String debugTitle = getProductTitleFromElement(firstProduct);
                    logger.error("- Çıkarılan title: " + debugTitle);
                    
                    // Price çıkarma denemesi
                    double debugPrice = getProductPriceFromElement(firstProduct);
                    logger.error("- Çıkarılan fiyat: $" + debugPrice);
                    
                } catch (Exception e) {
                    logger.error("Debug bilgisi alınamadı: " + e.getMessage());
                }
            }
            
            throw new RuntimeException("Geçerli " + productKeyword + " ürünü bulunamadı");
        }

        logger.info("EN PAHALI ÜRÜN SEÇİLDİ: {} - ${}", mostExpensive.getName(), mostExpensive.getPrice());
        return mostExpensive;
    }

    private Product extractProductInfo(WebElement productElement, String keyword) {
        try {
            // Enhanced title extraction with multiple strategies
            String title = getProductTitleFromElement(productElement);
            
            if (title == null || title.equals("Unknown Product")) {
                logger.debug("Could not extract valid title for product element");
                return null;
            }

            // Anahtar kelime kontrolü - daha esnek
            if (!CommonUtils.containsAnyKeyword(title.toLowerCase(), keyword.toLowerCase(), "macbook", "mac book")) {
                logger.debug("Product title '{}' does not contain keyword '{}'", title, keyword);
                return null;
            }

            // Enhanced price extraction
            double price = getProductPriceFromElement(productElement);

            if (price > 0) {
                logger.debug("Extracted product: {} - ${}", title, price);
                return new Product(title, price, productElement);
            } else {
                logger.debug("No valid price found for product: {}", title);
            }

        } catch (Exception e) {
            logger.debug("Ürün bilgisi çıkarılamadı: " + e.getMessage());
        }

        return null;
    }

    public void navigateToProduct(Product product) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                // Ürün elementini tekrar bul (stale reference'dan kaçınmak için)
                List<WebElement> allProducts = driver.findElements(
                        By.cssSelector("[data-component-type='s-search-result']")
                );

                if (product.getElementIndex() < allProducts.size()) {
                    WebElement productElement = allProducts.get(product.getElementIndex());
                    WebElement linkElement = productElement.findElement(By.cssSelector(".a-link-normal"));

                    // JavaScript ile tıkla
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", linkElement);

                    // Navigasyonu bekle
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.not(
                            ExpectedConditions.urlContains("/s?k=")
                    ));

                    logger.info("Ürün sayfasına başarıyla gidildi (deneme " + attempt + ")");
                    return;
                }

            } catch (Exception e) {
                logger.warn("Navigasyon denemesi " + attempt + " başarısız: " + e.getMessage());

                if (attempt == 3) {
                    throw new RuntimeException("3 denemeden sonra ürün sayfasına giidilemedi", e);
                }

                WaitUtils.sleepMillis(2000);
            }
        }
    }
}
