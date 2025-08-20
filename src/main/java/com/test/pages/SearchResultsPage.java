package com.test.pages;

import com.test.utils.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    // Product Elements (within search results)
    @FindBy(css = "[data-component-type='s-search-result'] h2 a span")
    private List<WebElement> productTitles;

    @FindBy(css = "[data-component-type='s-search-result'] .a-price-whole")
    private List<WebElement> productPrices;

    @FindBy(css = "[data-component-type='s-search-result'] .a-price .a-offscreen")
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
        List<String> titles = productTitles.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .collect(Collectors.toList());

        logger.debug("Retrieved {} product titles", titles.size());
        return titles;
    }

    /**
     * Bu sayfadaki ürün fiyatlarını numerik liste olarak döner.
     */
    public List<Double> getProductPrices() {
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
     */
    public ProductInfo findMostExpensiveMacBookPro() {
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
                ProductInfo productInfo = getProductInfo(i);
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
            // Enhanced selector list with more variations
            String[] titleSelectors = {
                    "h2 a span",
                    "h2 a",
                    ".a-link-normal .a-size-medium",
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
            // Enhanced price selectors with more variations
            String[] priceSelectors = {
                    ".a-price .a-offscreen",
                    ".a-price-whole",
                    ".a-price .a-price-whole",
                    ".a-price-range .a-price .a-offscreen",
                    "[data-a-price]",
                    ".a-size-medium.a-color-price",
                    ".s-price .a-offscreen",
                    ".s-price"
            };

            // Try CSS selectors first
            for (String selector : priceSelectors) {
                try {
                    WebElement priceElement = productElement.findElement(By.cssSelector(selector));
                    if (priceElement != null && isElementDisplayed(priceElement)) {
                        String priceText = getText(priceElement);
                        if (priceText != null && !priceText.trim().isEmpty()) {
                            logger.debug("Found price text with selector '{}': {}", selector, priceText);

                            double price = CommonUtils.extractPriceFromText(priceText);
                            if (price > 0) {
                                logger.debug("Extracted price: ${} from text: {}", price, priceText);
                                return price;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }

        } catch (Exception e) {
            logger.debug("Could not extract price from product element: {}", e.getMessage());
        }

        logger.debug("No valid price found for product element");
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
}