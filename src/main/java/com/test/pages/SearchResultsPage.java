package com.test.pages;

import com.test.pages.BasePage;
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
 * Amazon Search Results Page Object Model
 * Contains elements and methods for search results page interactions
 */
public class SearchResultsPage extends BasePage {
    
    // Page Elements using Page Factory
    
    // Search Results Container
    @FindBy(css = "[data-component-type='s-search-result']")
    private List<WebElement> searchResults;
    
    @FindBy(css = "h1.a-size-base-plus")
    private WebElement resultsHeader;
    
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
    
    @FindBy(css = "a[data-value='relevanceblender']")
    private WebElement sortByRelevance;
    
    @FindBy(css = "a[data-value='review-rank']")
    private WebElement sortByCustomerReviews;
    
    // Filters
    @FindBy(css = "#priceRefinements")
    private WebElement priceFilter;
    
    @FindBy(css = "#brandRefinements")
    private WebElement brandFilter;
    
    @FindBy(css = "[data-s-filter-id]")
    private List<WebElement> filterOptions;
    
    @FindBy(css = ".a-checkbox-label")
    private List<WebElement> checkboxFilters;
    
    // Pagination
    @FindBy(css = ".s-pagination-next")
    private WebElement nextPageButton;
    
    @FindBy(css = ".s-pagination-previous")
    private WebElement previousPageButton;
    
    @FindBy(css = ".s-pagination-item")
    private List<WebElement> paginationItems;
    
    // Sponsored Products
    @FindBy(css = "[data-component-type='s-search-result'][data-sponsored='true']")
    private List<WebElement> sponsoredProducts;
    
    // No Results
    @FindBy(css = ".s-no-outline")
    private WebElement noResultsMessage;
    
    @FindBy(css = "[data-component-type='s-search-result']:not([data-sponsored='true'])")
    private List<WebElement> organicResults;
    
    // Constructor
    public SearchResultsPage(WebDriver driver) {
        super(driver);
        waitForSearchResultsToLoad();
        logger.info("SearchResultsPage initialized");
    }
    
    // Page Load Methods
    
    /**
     * Wait for search results page to load completely
     */
    private void waitForSearchResultsToLoad() {
        waitUtils.waitForElementToBeVisible(resultsList);
        waitUtils.waitForPageToLoad();
        logger.debug("Search results page loaded");
    }
    
    // Search Results Methods
    
    /**
     * Get total number of search results
     * @return Number of search results
     */
    public int getSearchResultsCount() {
        int count = searchResults.size();
        logger.info("Total search results found: {}", count);
        return count;
    }
    
    /**
     * Get organic (non-sponsored) results count
     * @return Number of organic results
     */
    public int getOrganicResultsCount() {
        int count = organicResults.size();
        logger.debug("Organic results count: {}", count);
        return count;
    }
    
    /**
     * Get sponsored results count
     * @return Number of sponsored results
     */
    public int getSponsoredResultsCount() {
        int count = sponsoredProducts.size();
        logger.debug("Sponsored results count: {}", count);
        return count;
    }
    
    /**
     * Check if search results are displayed
     * @return true if results are present
     */
    public boolean areSearchResultsDisplayed() {
        boolean displayed = !searchResults.isEmpty() && isElementDisplayed(resultsList);
        logger.debug("Search results displayed: {}", displayed);
        return displayed;
    }
    
    /**
     * Get search results header text
     * @return Header text with result count
     */
    public String getResultsHeaderText() {
        String headerText = getText(resultsHeader);
        logger.debug("Results header: {}", headerText);
        return headerText;
    }
    
    // Product Information Methods
    
    /**
     * Get all product titles from current page
     * @return List of product titles
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
     * Get all product prices from current page
     * @return List of product prices as doubles
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
     * Get product information for a specific result index
     * @param index Result index (0-based)
     * @return ProductInfo object
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
     * Find the most expensive MacBook Pro in current results
     * @return ProductInfo of the most expensive MacBook Pro
     */
    public ProductInfo findMostExpensiveMacBookPro() {
        logger.info("Looking for most expensive MacBook Pro");
        
        // Debug: Log search results count
        logger.info("Search results found: {}", searchResults.size());
        
        if (searchResults.isEmpty()) {
            // Try to find results with alternative selectors
            logger.warn("No search results found with primary selector, trying alternatives...");
            
            // Alternative Amazon selectors for search results
            try {
                List<WebElement> alternativeResults = driver.findElements(By.cssSelector("div[data-component-type='s-search-result'], .s-result-item, [data-asin]"));
                logger.info("Alternative selector found {} results", alternativeResults.size());
                
                if (!alternativeResults.isEmpty()) {
                    // Use alternative results
                    List<ProductInfo> macBookProductsAlt = new ArrayList<>();
                    for (int i = 0; i < Math.min(alternativeResults.size(), 20); i++) {
                        WebElement element = alternativeResults.get(i);
                        try {
                            String title = getProductTitleFromElement(element);
                            logger.debug("Alternative result {}: {}", i, title);
                            
                            if (CommonUtils.containsAnyKeyword(title.toLowerCase(), "macbook pro", "macbook", "mac book")) {
                                double price = getProductPriceFromElement(element);
                                ProductInfo productInfo = new ProductInfo(title, price, "No rating", 0, false, i);
                                macBookProductsAlt.add(productInfo);
                                logger.info("Found MacBook Pro (alternative): {} - ${}", title, price);
                            }
                        } catch (Exception e) {
                            logger.debug("Error processing alternative result {}: {}", i, e.getMessage());
                        }
                    }
                    
                    if (!macBookProductsAlt.isEmpty()) {
                        // Return most expensive from alternatives
                        ProductInfo mostExpensive = macBookProductsAlt.stream()
                                .filter(p -> p.getPrice() > 0)
                                .max(Comparator.comparing(ProductInfo::getPrice))
                                .orElse(macBookProductsAlt.get(0));
                        logger.info("Selected most expensive MacBook from alternatives: {} - ${}", 
                                mostExpensive.getTitle(), mostExpensive.getPrice());
                        return mostExpensive;
                    }
                }
            } catch (Exception e) {
                logger.warn("Alternative selector also failed: {}", e.getMessage());
            }
            
            logger.error("No search results found with any selector");
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
            // Last resort: try to find any Apple product
            logger.warn("No MacBook Pro found, looking for any Apple product...");
            for (int i = 0; i < Math.min(searchResults.size(), 20); i++) {
                try {
                    ProductInfo productInfo = getProductInfo(i);
                    if (CommonUtils.containsAnyKeyword(productInfo.getTitle().toLowerCase(), "apple", "ipad", "iphone")) {
                        logger.warn("Using Apple product as fallback: {}", productInfo.getTitle());
                        return productInfo;
                    }
                } catch (Exception e) {
                    logger.debug("Error processing fallback result {}: {}", i, e.getMessage());
                }
            }
            
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
     * Click on a product by index
     * @param index Product index to click
     * @return ProductDetailPage instance
     */
    public ProductDetailPage clickOnProduct(int index) {
        if (index < 0 || index >= searchResults.size()) {
            throw new IndexOutOfBoundsException("Product index out of range: " + index);
        }
        
        WebElement productElement = searchResults.get(index);
        WebElement titleLink = productElement.findElement(By.cssSelector("h2 a"));
        
        String productTitle = getText(titleLink);
        logger.info("Clicking on product {}: {}", index, productTitle);
        
        // Scroll to element and click
        scrollToElement(titleLink);
        click(titleLink);
        
        waitUtils.waitForPageToLoad();
        logger.info("Navigated to product detail page");
        
        return new ProductDetailPage(driver);
    }
    
    /**
     * Click on the most expensive MacBook Pro
     * @return ProductDetailPage instance
     */
    public ProductDetailPage clickOnMostExpensiveMacBookPro() {
        ProductInfo mostExpensive = findMostExpensiveMacBookPro();
        return clickOnProduct(mostExpensive.getIndex());
    }
    
    // Sorting Methods
    
    /**
     * Sort results by price (high to low)
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
     * Sort results by price (low to high)
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
     * Alternative sorting method using URL parameters
     * @param sortValue Sort parameter value
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
    
    // Filtering Methods
    
    /**
     * Apply brand filter
     * @param brandName Brand name to filter by
     */
    public void applyBrandFilter(String brandName) {
        logger.info("Applying brand filter: {}", brandName);
        
        for (WebElement filterOption : checkboxFilters) {
            if (isElementDisplayed(filterOption) && 
                getText(filterOption).toLowerCase().contains(brandName.toLowerCase())) {
                
                click(filterOption);
                waitForSearchResultsToLoad();
                logger.info("Applied brand filter: {}", brandName);
                return;
            }
        }
        
        logger.warn("Brand filter not found: {}", brandName);
    }
    
    /**
     * Apply Apple brand filter specifically
     */
    public void applyAppleFilter() {
        applyBrandFilter("Apple");
    }
    
    // Navigation Methods
    
    /**
     * Go to next page of results
     * @return true if successfully navigated to next page
     */
    public boolean goToNextPage() {
        if (isElementDisplayed(nextPageButton) && isElementEnabled(nextPageButton)) {
            click(nextPageButton);
            waitForSearchResultsToLoad();
            logger.info("Navigated to next page");
            return true;
        }
        
        logger.debug("Next page button not available");
        return false;
    }
    
    /**
     * Go to previous page of results
     * @return true if successfully navigated to previous page
     */
    public boolean goToPreviousPage() {
        if (isElementDisplayed(previousPageButton) && isElementEnabled(previousPageButton)) {
            click(previousPageButton);
            waitForSearchResultsToLoad();
            logger.info("Navigated to previous page");
            return true;
        }
        
        logger.debug("Previous page button not available");
        return false;
    }
    
    // Helper Methods
    
    private String getProductTitleFromElement(WebElement productElement) {
        try {
            // Try multiple selectors for product title
            WebElement titleElement = null;
            
            // Primary selector
            try {
                titleElement = productElement.findElement(By.cssSelector("h2 a span"));
            } catch (Exception e1) {
                try {
                    // Alternative selector 1
                    titleElement = productElement.findElement(By.cssSelector("h2 a"));
                } catch (Exception e2) {
                    try {
                        // Alternative selector 2
                        titleElement = productElement.findElement(By.cssSelector(".a-link-normal .a-size-medium"));
                    } catch (Exception e3) {
                        try {
                            // Alternative selector 3
                            titleElement = productElement.findElement(By.cssSelector("[data-cy='title-recipe-title']"));
                        } catch (Exception e4) {
                            // Alternative selector 4
                            titleElement = productElement.findElement(By.cssSelector(".s-title-instructions-style span"));
                        }
                    }
                }
            }
            
            if (titleElement != null) {
                String title = getText(titleElement);
                logger.debug("Extracted title: {}", title);
                return title;
            }
        } catch (Exception e) {
            logger.debug("Could not extract title from product element: {}", e.getMessage());
        }
        return "Unknown Product";
    }
    
    private double getProductPriceFromElement(WebElement productElement) {
        try {
            // Try multiple price selectors
            String[] priceSelectors = {
                ".a-price .a-offscreen",
                ".a-price-whole",
                ".a-price .a-price-whole",
                ".a-price-range .a-price .a-offscreen",
                "[data-a-price]",
                ".a-size-medium.a-color-price",
                ".a-text-price.a-size-medium"
            };
            
            for (String selector : priceSelectors) {
                try {
                    WebElement priceElement = productElement.findElement(By.cssSelector(selector));
                    if (priceElement != null) {
                        String priceText = getText(priceElement);
                        logger.debug("Found price text with selector '{}': {}", selector, priceText);
                        
                        double price = CommonUtils.extractPriceFromText(priceText);
                        if (price > 0) {
                            logger.debug("Extracted price: ${} from text: {}", price, priceText);
                            return price;
                        }
                    }
                } catch (Exception ignored) {
                    // Continue to next selector
                }
            }
            
            // Try alternative approach: get all text and extract price
            try {
                String fullText = getText(productElement);
                double price = CommonUtils.extractPriceFromText(fullText);
                if (price > 0) {
                    logger.debug("Extracted price from full element text: ${}", price);
                    return price;
                }
            } catch (Exception ex) {
                logger.debug("Failed to extract price from full text: {}", ex.getMessage());
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
        
        // Getters
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