package com.test.pages;

import com.test.pages.BasePage;
import com.test.utils.CommonUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.test.utils.CommonUtils.getSelectedDropdownOption;

/**
 * Amazon Ürün Detay Sayfası Page Object.
 * Ürün başlığı/fiyatı/özellikleri, stok ve Prime bilgisi, sepete ekleme akışları ve
 * görsel/karşılaştırma bileşenleriyle etkileşimleri kapsar.
 */
public class ProductDetailPage extends BasePage {
    
    // Page Elements using Page Factory
    
    // Product Information
    @FindBy(id = "productTitle")
    private WebElement productTitle;
    
    @FindBy(css = ".a-price.a-text-price.a-size-medium.apexPriceToPay")
    private WebElement productPrice;
    
    @FindBy(css = ".a-price-whole")
    private WebElement priceWhole;
    
    @FindBy(css = ".a-price-fraction")
    private WebElement priceFraction;
    
    @FindBy(css = ".a-offscreen")
    private WebElement priceOffscreen;
    
    @FindBy(css = "#priceblock_dealprice")
    private WebElement dealPrice;
    
    @FindBy(css = "#priceblock_ourprice")
    private WebElement ourPrice;
    
    // Product Images
    @FindBy(css = "#landingImage")
    private WebElement mainProductImage;
    
    @FindBy(css = ".a-button-thumbnail")
    private List<WebElement> thumbnailImages;
    
    @FindBy(css = "#altImages img")
    private List<WebElement> alternativeImages;
    
    // Product Details
    @FindBy(css = "#feature-bullets ul")
    private WebElement featureBullets;
    
    @FindBy(css = "#feature-bullets li")
    private List<WebElement> productFeatures;
    
    @FindBy(css = "#productDescription")
    private WebElement productDescription;
    
    @FindBy(css = "#aplus")
    private WebElement aPlusContent;
    
    // Ratings and Reviews
    @FindBy(css = ".a-icon.a-icon-star")
    private WebElement starRating;
    
    @FindBy(css = "#acrCustomerReviewText")
    private WebElement customerReviewText;
    
    @FindBy(css = "#averageCustomerReviews")
    private WebElement averageCustomerReviews;
    
    // Add to Cart Section
    @FindBy(id = "add-to-cart-button")
    private WebElement addToCartButton;
    
    @FindBy(id = "buy-now-button")
    private WebElement buyNowButton;
    
    @FindBy(css = "#addToCart")
    private WebElement addToCartSection;
    
    // Quantity Selection
    @FindBy(css = "#quantity")
    private WebElement quantityDropdown;
    
    @FindBy(css = "select[name='quantity']")
    private WebElement quantitySelect;
    
    // Availability
    @FindBy(css = "#availability span")
    private WebElement availabilityText;
    
    @FindBy(css = "#stockDisplay")
    private WebElement stockDisplay;
    
    // Product Options (Color, Size, Style, etc.)
    @FindBy(css = "#variation_color_name")
    private WebElement colorOptions;
    
    @FindBy(css = "#variation_size_name")
    private WebElement sizeOptions;
    
    @FindBy(css = "#variation_style_name")
    private WebElement styleOptions;
    
    @FindBy(css = ".a-button-group")
    private List<WebElement> variationButtons;
    
    // Shipping Information
    @FindBy(css = "#deliveryBlockMessage")
    private WebElement deliveryMessage;
    
    @FindBy(css = "#mir-layout-DELIVERY_BLOCK")
    private WebElement deliveryBlock;
    
    @FindBy(css = "#fast-track-message")
    private WebElement fastTrackMessage;
    
    // Prime Information
    @FindBy(css = ".a-icon-prime")
    private WebElement primeIcon;
    
    @FindBy(css = "#primeDeliveryMessage")
    private WebElement primeDeliveryMessage;
    
    // Success Messages
    @FindBy(css = "#attachDisplayAddBaseAlert")
    private WebElement addToCartSuccessMessage;
    
    @FindBy(css = "#sw-atc-details-single-container")
    private WebElement cartConfirmationDialog;
    
    @FindBy(css = "#attach-sidesheet-checkout-button")
    private WebElement proceedToCheckoutButton;
    
    @FindBy(css = "#attach-sidesheet-view-cart-button")
    private WebElement viewCartButton;
    
    // Product Comparison
    @FindBy(css = "#compare-product")
    private WebElement compareProduct;
    
    @FindBy(css = ".comparison-table")
    private WebElement comparisonTable;
    
    // Breadcrumb Navigation
    @FindBy(css = "#wayfinding-breadcrumbs_feature_div")
    private WebElement breadcrumbsSection;
    
    @FindBy(css = ".a-breadcrumb a")
    private List<WebElement> breadcrumbLinks;
    
    // Related Products
    @FindBy(css = "#similarities_feature_div")
    private WebElement similarItems;
    
    @FindBy(css = "#bundleV2_feature_div")
    private WebElement frequentlyBoughtTogether;
    
    // Constructor
    public ProductDetailPage(WebDriver driver) {
        super(driver);
        waitForProductPageToLoad();
        logger.info("ProductDetailPage initialized");
    }
    
    // Page Load Methods
    
    /**
     * Ürün detay sayfasının tamamen yüklenmesini bekler.
     */
    private void waitForProductPageToLoad() {
        waitUtils.waitForElementToBeVisible(productTitle);
        waitUtils.waitForElementToBeVisible(addToCartButton);
        waitUtils.waitForPageToLoad();
        logger.debug("Product detail page loaded");
    }
    
    // Product Information Methods
    
    /**
     * Ürün başlığını döner.
     */
    public String getProductTitle() {
        String title = getText(productTitle);
        logger.info("Product title: {}", title);
        return title;
    }
    
    /**
     * Ürün fiyatını numerik değer olarak döner.
     */
    public double getProductPrice() {
        double price = 0.0;
        
        // Try different price element selectors
        try {
            if (isElementDisplayed(priceOffscreen)) {
                String priceText = getText(priceOffscreen);
                price = CommonUtils.extractPriceFromText(priceText);
                logger.debug("Price from offscreen element: {}", price);
            }
            else if (isElementDisplayed(dealPrice)) {
                String priceText = getText(dealPrice);
                price = CommonUtils.extractPriceFromText(priceText);
                logger.debug("Price from deal price: {}", price);
            }
            else if (isElementDisplayed(ourPrice)) {
                String priceText = getText(ourPrice);
                price = CommonUtils.extractPriceFromText(priceText);
                logger.debug("Price from our price: {}", price);
            }
            else if (isElementDisplayed(priceWhole)) {
                String wholePrice = getText(priceWhole);
                String fractionPrice = isElementDisplayed(priceFraction) ? getText(priceFraction) : "00";
                String fullPrice = wholePrice + "." + fractionPrice;
                price = CommonUtils.extractPriceFromText(fullPrice);
                logger.debug("Price from whole/fraction: {}", price);
            }
        } catch (Exception e) {
            logger.warn("Could not extract product price: {}", e.getMessage());
        }
        
        logger.info("Product price: ${}", price);
        return price;
    }
    
    /**
     * Ürün fiyatını biçimlendirilmiş metin olarak döner.
     */
    public String getProductPriceText() {
        try {
            if (isElementDisplayed(priceOffscreen)) {
                return getText(priceOffscreen);
            }
            else if (isElementDisplayed(dealPrice)) {
                return getText(dealPrice);
            }
            else if (isElementDisplayed(ourPrice)) {
                return getText(ourPrice);
            }
        } catch (Exception e) {
            logger.debug("Could not get formatted price text: {}", e.getMessage());
        }
        
        return String.format("$%.2f", getProductPrice());
    }
    
    /**
     * Ürün stok/erişilebilirlik durumunu döner.
     */
    public String getAvailabilityStatus() {
        try {
            if (isElementDisplayed(availabilityText)) {
                String availability = getText(availabilityText);
                logger.debug("Availability: {}", availability);
                return availability;
            }
        } catch (Exception e) {
            logger.debug("Could not get availability status: {}", e.getMessage());
        }
        
        return "Availability unknown";
    }
    
    /**
     * Ürün stokta mı kontrol eder.
     */
    public boolean isProductInStock() {
        String availability = getAvailabilityStatus().toLowerCase();
        boolean inStock = !availability.contains("out of stock") && 
                         !availability.contains("unavailable") &&
                         !availability.contains("currently unavailable");
        
        logger.info("Product in stock: {}", inStock);
        return inStock;
    }
    
    /**
     * Ürün özelliklerini liste olarak döner.
     */
    public List<String> getProductFeatures() {
        List<String> features = productFeatures.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .collect(java.util.stream.Collectors.toList());
        
        logger.debug("Product features count: {}", features.size());
        return features;
    }
    
    // Add to Cart Methods
    
    /**
     * Ürünü sepete ekler. Diyalog açılırsa sayfada kalır, yoksa sepete gidişi döner.
     */
    public Object addToCart() {
        logger.info("Adding product to cart");
        
        // Scroll to add to cart button
        scrollToElement(addToCartButton);
        
        // Ensure button is clickable
        waitUtils.waitForElementToBeClickable(addToCartButton);
        
        // Click add to cart button
        click(addToCartButton);
        
        // Wait for response (either redirect to cart or confirmation dialog)
        CommonUtils.waitFor(2);
        
        // Check if confirmation dialog appears
        if (isAddToCartDialogDisplayed()) {
            logger.info("Add to cart confirmation dialog appeared");
            return this; // Stay on product page with dialog
        }
        else {
            // If no dialog, we might have been redirected to cart
            logger.info("Redirected to cart page");
            waitUtils.waitForPageToLoad();
            return new CartPage(driver);
        }
    }
    
    /**
     * Ürünü sepete ekler ve ödeme adımına ilerler.
     */
    public CartPage addToCartAndProceedToCheckout() {
        addToCart();
        
        if (isAddToCartDialogDisplayed()) {
            click(proceedToCheckoutButton);
            waitUtils.waitForPageToLoad();
        }
        
        logger.info("Proceeding to checkout");
        return new CartPage(driver);
    }
    
    /**
     * Ürünü sepete ekler ve sepet sayfasına gider.
     */
    public CartPage addToCartAndViewCart() {
        addToCart();
        
        if (isAddToCartDialogDisplayed()) {
            click(viewCartButton);
            waitUtils.waitForPageToLoad();
        }
        
        logger.info("Viewing cart");
        return new CartPage(driver);
    }
    
    /**
     * Sepete ekleme onay diyalogu görünür mü kontrol eder.
     */
    public boolean isAddToCartDialogDisplayed() {
        boolean dialogVisible = isElementDisplayed(cartConfirmationDialog) || 
                               isElementDisplayed(addToCartSuccessMessage);
        logger.debug("Add to cart dialog displayed: {}", dialogVisible);
        return dialogVisible;
    }
    
    /**
     * Ürünün sepete başarıyla eklendiğini doğrular.
     */
    public boolean isProductAddedToCartSuccessfully() {
        boolean success = isElementDisplayed(addToCartSuccessMessage) ||
                         isAddToCartDialogDisplayed();
        logger.info("Product added to cart successfully: {}", success);
        return success;
    }
    
    // Quantity Methods
    
    /**
     * Ürün adedini günceller.
     */
    public void setQuantity(int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }
        
        logger.info("Setting quantity to: {}", quantity);
        
        try {
            if (isElementDisplayed(quantityDropdown)) {
                selectDropdownByValue(quantityDropdown, String.valueOf(quantity));
            }
            else if (isElementDisplayed(quantitySelect)) {
                selectDropdownByValue(quantitySelect, String.valueOf(quantity));
            }
            
            CommonUtils.waitFor(1); // Wait for any price updates
            logger.info("Quantity set to: {}", quantity);
            
        } catch (Exception e) {
            logger.warn("Could not set quantity: {}", e.getMessage());
        }
    }
    
    /**
     * Seçili adet değerini döner.
     */
    public int getSelectedQuantity() {
        try {
            if (isElementDisplayed(quantityDropdown)) {
                String qty = getSelectedDropdownOption(quantityDropdown);
                return Integer.parseInt(qty);
            }
            else if (isElementDisplayed(quantitySelect)) {
                String qty = getSelectedDropdownOption(quantitySelect);
                return Integer.parseInt(qty);
            }
        } catch (Exception e) {
            logger.debug("Could not get selected quantity: {}", e.getMessage());
        }
        
        return 1; // Default quantity
    }
    
    // Product Options Methods
    
    /**
     * Renk seçeneğini (varsa) seçer.
     */
    public void selectColor(String color) {
        if (isElementDisplayed(colorOptions)) {
            logger.info("Selecting color: {}", color);
            selectDropdownByText(colorOptions, color);
            CommonUtils.waitFor(2); // Wait for page to update with new selection
        } else {
            logger.debug("Color options not available for this product");
        }
    }
    
    /**
     * Beden seçeneğini (varsa) seçer.
     */
    public void selectSize(String size) {
        if (isElementDisplayed(sizeOptions)) {
            logger.info("Selecting size: {}", size);
            selectDropdownByText(sizeOptions, size);
            CommonUtils.waitFor(2); // Wait for page to update with new selection
        } else {
            logger.debug("Size options not available for this product");
        }
    }
    
    // Rating and Review Methods
    
    /**
     * Ürün puanlamasını metin olarak döner.
     */
    public String getProductRating() {
        try {
            if (isElementDisplayed(starRating)) {
                String ratingText = getAttribute(starRating, "title");
                if (ratingText == null) {
                    ratingText = getText(starRating);
                }
                logger.debug("Product rating: {}", ratingText);
                return ratingText;
            }
        } catch (Exception e) {
            logger.debug("Could not get product rating: {}", e.getMessage());
        }
        
        return "No rating available";
    }
    
    /**
     * Müşteri değerlendirme sayısını döner.
     */
    public int getReviewCount() {
        try {
            if (isElementDisplayed(customerReviewText)) {
                String reviewText = getText(customerReviewText);
                String numbers = CommonUtils.extractNumbers(reviewText);
                return numbers.isEmpty() ? 0 : Integer.parseInt(numbers.replace(",", ""));
            }
        } catch (Exception e) {
            logger.debug("Could not get review count: {}", e.getMessage());
        }
        
        return 0;
    }
    
    // Prime and Shipping Methods
    
    /**
     * Ürün Prime kargo uygunluğuna sahip mi kontrol eder.
     */
    public boolean isPrimeEligible() {
        boolean primeEligible = isElementDisplayed(primeIcon) || 
                               isElementDisplayed(primeDeliveryMessage);
        logger.debug("Prime eligible: {}", primeEligible);
        return primeEligible;
    }
    
    /**
     * Teslimat bilgisi metnini döner.
     */
    public String getDeliveryInfo() {
        try {
            if (isElementDisplayed(deliveryMessage)) {
                return getText(deliveryMessage);
            }
            else if (isElementDisplayed(fastTrackMessage)) {
                return getText(fastTrackMessage);
            }
        } catch (Exception e) {
            logger.debug("Could not get delivery info: {}", e.getMessage());
        }
        
        return "Delivery information not available";
    }
    
    // Validation Methods
    
    /**
     * Bu sayfanın bir MacBook Pro ürününe ait olup olmadığını kontrol eder.
     */
    public boolean isMacBookProProduct() {
        String title = getProductTitle().toLowerCase();
        boolean isMacBookPro = CommonUtils.containsAnyKeyword(title, "macbook pro", "macbook");
        logger.info("Is MacBook Pro product: {}", isMacBookPro);
        return isMacBookPro;
    }
    
    /**
     * Ürün detay sayfasının yüklendiğini doğrular.
     */
    public boolean isProductPageLoaded() {
        boolean loaded = isElementDisplayed(productTitle) && 
                        isElementDisplayed(addToCartButton) && 
                        !getProductTitle().isEmpty();
        logger.debug("Product page loaded: {}", loaded);
        return loaded;
    }
    
    // Navigation Methods
    
    /**
     * Arama sonuçlarına geri döner.
     */
    public SearchResultsPage goBackToSearchResults() {
        navigateBack();
        logger.info("Navigated back to search results");
        return new SearchResultsPage(driver);
    }
    
    // Utility Methods
    
    /**
     * Ürün sayfasının ekran görüntüsünü alır.
     */
    public void takeProductPageScreenshot(String fileName) {
        CommonUtils.takeScreenshot(driver, "product_" + fileName);
    }
    
    /**
     * Ürün özet bilgilerini metin olarak döner.
     */
    public String getProductSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Product: ").append(getProductTitle()).append("\n");
        summary.append("Price: $").append(getProductPrice()).append("\n");
        summary.append("Rating: ").append(getProductRating()).append("\n");
        summary.append("Reviews: ").append(getReviewCount()).append("\n");
        summary.append("Availability: ").append(getAvailabilityStatus()).append("\n");
        summary.append("Prime Eligible: ").append(isPrimeEligible()).append("\n");
        
        String productSummary = summary.toString();
        logger.info("Product Summary:\n{}", productSummary);
        return productSummary;
    }
}