package com.test.pages;

import com.test.pages.BasePage;
import com.test.utils.CommonUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static com.test.utils.CommonUtils.getSelectedDropdownOption;

/**
 * Amazon Shopping Cart Page Object Model
 * Contains elements and methods for shopping cart page interactions
 */
public class CartPage extends BasePage {
    
    // Page Elements using Page Factory
    
    // Cart Header
    @FindBy(css = "#sc-active-cart")
    private WebElement activeCartContainer;
    
    @FindBy(css = "#sc-subtotal-label-activecart")
    private WebElement subtotalLabel;
    
    @FindBy(css = "#sc-subtotal-amount-activecart")
    private WebElement subtotalAmount;
    
    @FindBy(css = "h1")
    private WebElement cartPageHeader;
    
    // Cart Items
    @FindBy(css = "[data-name='Active Items']")
    private WebElement activeItemsSection;
    
    @FindBy(css = "[data-itemtype='active']")
    private List<WebElement> cartItems;
    
    @FindBy(css = ".sc-list-item-content")
    private List<WebElement> itemContents;
    
    // Individual Item Elements (within cart items)
    @FindBy(css = ".sc-product-title")
    private List<WebElement> itemTitles;
    
    @FindBy(css = ".sc-product-price")
    private List<WebElement> itemPrices;
    
    @FindBy(css = ".sc-price")
    private List<WebElement> itemPriceElements;
    
    @FindBy(css = "[data-feature-id='quantity-selector']")
    private List<WebElement> quantitySelectors;
    
    @FindBy(css = ".sc-quantity-stepper")
    private List<WebElement> quantitySteppers;
    
    // Item Actions
    @FindBy(css = "[data-action='delete']")
    private List<WebElement> deleteButtons;
    
    @FindBy(css = "[data-action='save-for-later']")
    private List<WebElement> saveForLaterButtons;
    
    @FindBy(css = "[data-action='move-to-cart']")
    private List<WebElement> moveToCartButtons;
    
    // Proceed to Checkout
    @FindBy(name = "proceedToRetailCheckout")
    private WebElement proceedToCheckoutButton;
    
    @FindBy(css = "#attach-sidesheet-checkout-button")
    private WebElement sidesheetCheckoutButton;
    
    @FindBy(css = "#sc-buy-box-ptc-button")
    private WebElement checkoutButton;
    
    // Cart Summary
    @FindBy(css = "#sc-subtotal-label-buybox")
    private WebElement buyboxSubtotalLabel;
    
    @FindBy(css = "#sc-subtotal-amount-buybox")
    private WebElement buyboxSubtotalAmount;
    
    @FindBy(css = "#sc-subtotal-details-2")
    private WebElement subtotalDetails;
    
    // Empty Cart
    @FindBy(css = "#sc-empty-cart")
    private WebElement emptyCartSection;
    
    @FindBy(css = ".sc-empty-cart-header")
    private WebElement emptyCartHeader;
    
    @FindBy(css = "#continue-shopping")
    private WebElement continueShoppingButton;
    
    // Saved for Later
    @FindBy(css = "#sc-saved-cart")
    private WebElement savedItemsSection;
    
    @FindBy(css = "[data-name='Saved for later Items']")
    private WebElement savedForLaterSection;
    
    @FindBy(css = "[data-itemtype='saved']")
    private List<WebElement> savedItems;
    
    // Recently Added
    @FindBy(css = "#recently-added-wrapper")
    private WebElement recentlyAddedSection;
    
    // Recommendations
    @FindBy(css = "#rhf")
    private WebElement recommendationsSection;
    
    @FindBy(css = ".rhf-frame")
    private List<WebElement> recommendedItems;
    
    // Prime Benefits
    @FindBy(css = ".sc-badge-prime")
    private List<WebElement> primeBadges;
    
    @FindBy(css = "#sc-prime-upsell")
    private WebElement primeUpsell;
    
    // Delivery Information
    @FindBy(css = ".sc-delivery-messaging")
    private List<WebElement> deliveryMessages;
    
    @FindBy(css = ".sc-delivery-option")
    private List<WebElement> deliveryOptions;
    
    // Gift Options
    @FindBy(css = ".sc-gift-option")
    private List<WebElement> giftOptions;
    
    // Error Messages
    @FindBy(css = ".sc-list-item-error")
    private List<WebElement> errorMessages;
    
    @FindBy(css = ".a-alert-error")
    private List<WebElement> alertErrors;
    
    // Constructor
    public CartPage(WebDriver driver) {
        super(driver);
        waitForCartPageToLoad();
        logger.info("CartPage initialized");
    }
    
    // Page Load Methods
    
    /**
     * Wait for cart page to load completely
     */
    private void waitForCartPageToLoad() {
        // Wait for either active cart or empty cart section
        try {
            waitUtils.waitForElementToBeVisible(activeCartContainer);
            logger.debug("Active cart loaded");
        } catch (Exception e) {
            try {
                waitUtils.waitForElementToBeVisible(emptyCartSection);
                logger.debug("Empty cart loaded");
            } catch (Exception ex) {
                logger.warn("Cart page may not have loaded properly");
            }
        }
        
        waitUtils.waitForPageToLoad();
        logger.debug("Cart page loaded");
    }
    
    // Cart Status Methods
    
    /**
     * Check if cart is empty
     * @return true if cart has no items
     */
    public boolean isCartEmpty() {
        boolean isEmpty = cartItems.isEmpty() || isElementDisplayed(emptyCartSection);
        logger.info("Cart is empty: {}", isEmpty);
        return isEmpty;
    }
    
    /**
     * Get number of items in cart
     * @return Number of items in active cart
     */
    public int getCartItemCount() {
        int count = cartItems.size();
        logger.info("Cart item count: {}", count);
        return count;
    }
    
    /**
     * Get cart subtotal amount
     * @return Subtotal as double
     */
    public double getCartSubtotal() {
        double subtotal = 0.0;
        
        try {
            String subtotalText = "";
            
            // Try different subtotal element locations
            if (isElementDisplayed(subtotalAmount)) {
                subtotalText = getText(subtotalAmount);
            }
            else if (isElementDisplayed(buyboxSubtotalAmount)) {
                subtotalText = getText(buyboxSubtotalAmount);
            }
            
            if (!subtotalText.isEmpty()) {
                subtotal = CommonUtils.extractPriceFromText(subtotalText);
            }
            
        } catch (Exception e) {
            logger.warn("Could not get cart subtotal: {}", e.getMessage());
        }
        
        logger.info("Cart subtotal: ${}", subtotal);
        return subtotal;
    }
    
    /**
     * Get cart subtotal as formatted text
     * @return Formatted subtotal string
     */
    public String getCartSubtotalText() {
        try {
            if (isElementDisplayed(subtotalAmount)) {
                return getText(subtotalAmount);
            }
            else if (isElementDisplayed(buyboxSubtotalAmount)) {
                return getText(buyboxSubtotalAmount);
            }
        } catch (Exception e) {
            logger.debug("Could not get formatted subtotal text: {}", e.getMessage());
        }
        
        return String.format("$%.2f", getCartSubtotal());
    }
    
    // Item Verification Methods
    
    /**
     * Verify if specific product is in cart by title
     * @param productTitle Product title to search for
     * @return true if product found in cart
     */
    public boolean isProductInCart(String productTitle) {
        for (WebElement titleElement : itemTitles) {
            if (isElementDisplayed(titleElement)) {
                String itemTitle = getText(titleElement);
                if (CommonUtils.containsAnyKeyword(itemTitle.toLowerCase(), 
                        productTitle.toLowerCase().split("\\s+"))) {
                    logger.info("Product found in cart: {}", productTitle);
                    return true;
                }
            }
        }
        
        logger.info("Product not found in cart: {}", productTitle);
        return false;
    }
    
    /**
     * Verify if MacBook Pro is in cart
     * @return true if any MacBook Pro product is in cart
     */
    public boolean isMacBookProInCart() {
        for (WebElement titleElement : itemTitles) {
            if (isElementDisplayed(titleElement)) {
                String itemTitle = getText(titleElement).toLowerCase();
                if (CommonUtils.containsAnyKeyword(itemTitle, "macbook pro", "macbook")) {
                    logger.info("MacBook Pro found in cart: {}", itemTitle);
                    return true;
                }
            }
        }
        
        logger.info("MacBook Pro not found in cart");
        return false;
    }
    
    /**
     * Get all item titles in cart
     * @return List of product titles in cart
     */
    public List<String> getCartItemTitles() {
        List<String> titles = itemTitles.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .collect(java.util.stream.Collectors.toList());
        
        logger.debug("Cart item titles: {}", titles);
        return titles;
    }
    
    /**
     * Get all item prices in cart
     * @return List of product prices in cart
     */
    public List<Double> getCartItemPrices() {
        List<Double> prices = new java.util.ArrayList<>();
        
        for (WebElement priceElement : itemPriceElements) {
            if (isElementDisplayed(priceElement)) {
                String priceText = getText(priceElement);
                double price = CommonUtils.extractPriceFromText(priceText);
                if (price > 0) {
                    prices.add(price);
                }
            }
        }
        
        logger.debug("Cart item prices: {}", prices);
        return prices;
    }
    
    /**
     * Get details of first item in cart
     * @return CartItemInfo object with item details
     */
    public CartItemInfo getFirstCartItem() {
        if (cartItems.isEmpty()) {
            logger.warn("No items in cart");
            return null;
        }
        
        return getCartItem(0);
    }
    
    /**
     * Get details of cart item by index
     * @param index Item index (0-based)
     * @return CartItemInfo object
     */
    public CartItemInfo getCartItem(int index) {
        if (index < 0 || index >= cartItems.size()) {
            throw new IndexOutOfBoundsException("Cart item index out of range: " + index);
        }
        
        String title = index < itemTitles.size() && isElementDisplayed(itemTitles.get(index)) 
                ? getText(itemTitles.get(index)) : "Unknown Product";
        
        double price = 0.0;
        if (index < itemPriceElements.size() && isElementDisplayed(itemPriceElements.get(index))) {
            String priceText = getText(itemPriceElements.get(index));
            price = CommonUtils.extractPriceFromText(priceText);
        }
        
        int quantity = getItemQuantity(index);
        
        CartItemInfo itemInfo = new CartItemInfo(title, price, quantity, index);
        logger.debug("Cart item {}: {}", index, itemInfo);
        
        return itemInfo;
    }
    
    // Quantity Management Methods
    
    /**
     * Get quantity of specific cart item
     * @param itemIndex Item index
     * @return Item quantity
     */
    public int getItemQuantity(int itemIndex) {
        try {
            if (itemIndex >= 0 && itemIndex < quantitySelectors.size()) {
                WebElement quantityElement = quantitySelectors.get(itemIndex);
                if (isElementDisplayed(quantityElement)) {
                    String quantity = getSelectedDropdownOption(quantityElement);
                    return Integer.parseInt(quantity);
                }
            }
        } catch (Exception e) {
            logger.debug("Could not get item quantity for index {}: {}", itemIndex, e.getMessage());
        }
        
        return 1; // Default quantity
    }
    
    /**
     * Update quantity of cart item
     * @param itemIndex Item index
     * @param newQuantity New quantity
     */
    public void updateItemQuantity(int itemIndex, int newQuantity) {
        if (newQuantity < 0) newQuantity = 0;
        if (newQuantity > 30) newQuantity = 30; // Amazon typical limit
        
        logger.info("Updating item {} quantity to: {}", itemIndex, newQuantity);
        
        try {
            if (itemIndex >= 0 && itemIndex < quantitySelectors.size()) {
                WebElement quantityElement = quantitySelectors.get(itemIndex);
                if (isElementDisplayed(quantityElement)) {
                    selectDropdownByValue(quantityElement, String.valueOf(newQuantity));
                    CommonUtils.waitFor(2); // Wait for price update
                    logger.info("Updated item quantity successfully");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to update item quantity: {}", e.getMessage());
        }
    }
    
    // Item Actions Methods
    
    /**
     * Remove item from cart
     * @param itemIndex Item index to remove
     */
    public void removeItem(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= deleteButtons.size()) {
            throw new IndexOutOfBoundsException("Delete button index out of range: " + itemIndex);
        }
        
        logger.info("Removing item from cart: index {}", itemIndex);
        
        WebElement deleteButton = deleteButtons.get(itemIndex);
        click(deleteButton);
        
        CommonUtils.waitFor(2); // Wait for item removal
        waitUtils.waitForPageToLoad();
        
        logger.info("Item removed from cart successfully");
    }
    
    /**
     * Save item for later
     * @param itemIndex Item index to save
     */
    public void saveItemForLater(int itemIndex) {
        if (itemIndex < 0 || itemIndex >= saveForLaterButtons.size()) {
            throw new IndexOutOfBoundsException("Save for later button index out of range: " + itemIndex);
        }
        
        logger.info("Saving item for later: index {}", itemIndex);
        
        WebElement saveButton = saveForLaterButtons.get(itemIndex);
        click(saveButton);
        
        CommonUtils.waitFor(2); // Wait for action to complete
        logger.info("Item saved for later successfully");
    }
    
    // Checkout Methods
    
    /**
     * Proceed to checkout
     * @return true if successfully navigated to checkout
     */
    public boolean proceedToCheckout() {
        logger.info("Proceeding to checkout");
        
        try {
            // Try different checkout button locations
            if (isElementDisplayed(proceedToCheckoutButton) && isElementEnabled(proceedToCheckoutButton)) {
                scrollToElement(proceedToCheckoutButton);
                click(proceedToCheckoutButton);
            }
            else if (isElementDisplayed(checkoutButton) && isElementEnabled(checkoutButton)) {
                scrollToElement(checkoutButton);
                click(checkoutButton);
            }
            else if (isElementDisplayed(sidesheetCheckoutButton) && isElementEnabled(sidesheetCheckoutButton)) {
                click(sidesheetCheckoutButton);
            }
            else {
                logger.error("No checkout button found or enabled");
                return false;
            }
            
            waitUtils.waitForPageToLoad();
            logger.info("Successfully proceeded to checkout");
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to proceed to checkout: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if checkout button is available
     * @return true if checkout can be initiated
     */
    public boolean isCheckoutAvailable() {
        boolean available = (isElementDisplayed(proceedToCheckoutButton) && isElementEnabled(proceedToCheckoutButton)) ||
                           (isElementDisplayed(checkoutButton) && isElementEnabled(checkoutButton)) ||
                           (isElementDisplayed(sidesheetCheckoutButton) && isElementEnabled(sidesheetCheckoutButton));
        
        logger.debug("Checkout available: {}", available);
        return available;
    }
    
    // Navigation Methods
    
    /**
     * Continue shopping (go back to homepage)
     * @return HomePage instance
     */
    public HomePage continueShopping() {
        if (isElementDisplayed(continueShoppingButton)) {
            click(continueShoppingButton);
        } else {
            // Navigate to homepage directly
            driver.get(com.test.config.ConfigReader.getBaseUrl());
        }
        
        waitUtils.waitForPageToLoad();
        logger.info("Continued shopping - returned to homepage");
        return new HomePage(driver);
    }
    
    // Validation Methods
    
    /**
     * Verify cart page is loaded
     * @return true if cart page elements are present
     */
    public boolean isCartPageLoaded() {
        boolean loaded = isElementDisplayed(activeCartContainer) || isElementDisplayed(emptyCartSection);
        logger.debug("Cart page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Verify cart has expected item count
     * @param expectedCount Expected number of items
     * @return true if count matches
     */
    public boolean verifyCartItemCount(int expectedCount) {
        int actualCount = getCartItemCount();
        boolean matches = actualCount == expectedCount;
        logger.info("Cart item count verification - Expected: {}, Actual: {}, Matches: {}", 
                expectedCount, actualCount, matches);
        return matches;
    }
    
    /**
     * Verify cart is not empty
     * @return true if cart has items
     */
    public boolean verifyCartIsNotEmpty() {
        boolean notEmpty = !isCartEmpty();
        logger.info("Cart is not empty: {}", notEmpty);
        return notEmpty;
    }
    
    // Prime and Delivery Methods
    
    /**
     * Check if any items have Prime shipping
     * @return true if Prime items present
     */
    public boolean hassPrimeItems() {
        boolean hasPrime = !primeBadges.isEmpty() && 
                          primeBadges.stream().anyMatch(this::isElementDisplayed);
        logger.debug("Cart has Prime items: {}", hasPrime);
        return hasPrime;
    }
    
    /**
     * Get delivery messages for cart items
     * @return List of delivery message texts
     */
    public List<String> getDeliveryMessages() {
        List<String> messages = deliveryMessages.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .collect(java.util.stream.Collectors.toList());
        
        logger.debug("Delivery messages: {}", messages);
        return messages;
    }
    
    // Error Handling Methods
    
    /**
     * Check if there are any error messages
     * @return true if errors present
     */
    public boolean hasErrors() {
        boolean hasErrors = (!errorMessages.isEmpty() && errorMessages.stream().anyMatch(this::isElementDisplayed)) ||
                           (!alertErrors.isEmpty() && alertErrors.stream().anyMatch(this::isElementDisplayed));
        
        logger.debug("Cart has errors: {}", hasErrors);
        return hasErrors;
    }
    
    /**
     * Get all error messages
     * @return List of error message texts
     */
    public List<String> getErrorMessages() {
        List<String> errors = new java.util.ArrayList<>();
        
        errorMessages.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .forEach(errors::add);
        
        alertErrors.stream()
                .filter(this::isElementDisplayed)
                .map(this::getText)
                .forEach(errors::add);
        
        logger.debug("Error messages: {}", errors);
        return errors;
    }
    
    // Utility Methods
    
    /**
     * Take screenshot of cart page
     * @param fileName Screenshot file name
     */
    public void takeCartPageScreenshot(String fileName) {
        CommonUtils.takeScreenshot(driver, "cart_" + fileName);
    }
    
    /**
     * Get cart summary as formatted string
     * @return Formatted cart summary
     */
    public String getCartSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== CART SUMMARY ===\n");
        summary.append("Items in cart: ").append(getCartItemCount()).append("\n");
        summary.append("Cart subtotal: $").append(getCartSubtotal()).append("\n");
        summary.append("Cart is empty: ").append(isCartEmpty()).append("\n");
        summary.append("Has Prime items: ").append(hassPrimeItems()).append("\n");
        summary.append("Checkout available: ").append(isCheckoutAvailable()).append("\n");
        
        if (!isCartEmpty()) {
            summary.append("\nItems:\n");
            List<String> titles = getCartItemTitles();
            List<Double> prices = getCartItemPrices();
            
            for (int i = 0; i < Math.min(titles.size(), prices.size()); i++) {
                summary.append(String.format("%d. %s - $%.2f\n", i + 1, titles.get(i), prices.get(i)));
            }
        }
        
        summary.append("==================");
        
        String cartSummary = summary.toString();
        logger.info("Cart Summary:\n{}", cartSummary);
        return cartSummary;
    }
    
    // Inner Class for Cart Item Information
    public static class CartItemInfo {
        private final String title;
        private final double price;
        private final int quantity;
        private final int index;
        
        public CartItemInfo(String title, double price, int quantity, int index) {
            this.title = title;
            this.price = price;
            this.quantity = quantity;
            this.index = index;
        }
        
        // Getters
        public String getTitle() { return title; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public int getIndex() { return index; }
        public double getTotalPrice() { return price * quantity; }
        
        @Override
        public String toString() {
            return String.format("CartItemInfo{title='%s', price=%.2f, quantity=%d, totalPrice=%.2f, index=%d}",
                    title, price, quantity, getTotalPrice(), index);
        }
    }
}