package com.test.tests;

import com.test.config.ConfigReader;
import com.test.pages.CartPage;
import com.test.pages.HomePage;
import com.test.pages.ProductDetailPage;
import com.test.pages.SearchResultsPage;
import com.test.utils.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Smoke test: Searches MacBook Pro on Amazon, sorts by Price High to Low,
 * prints the top product price to console, and adds it to the cart.
 */
public class TopPriceAddToCartTest {

    private static final Logger logger = LogManager.getLogger(TopPriceAddToCartTest.class);
    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.initializeDriver();
        driver = DriverManager.getDriver();
        driver.get(ConfigReader.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    @Test(groups = {"smoke", "macbook"})
    public void printTopPriceAndAddToCart() {
        // Go to home and search
        HomePage homePage = new HomePage(driver);
        homePage.searchFor("MacBook Pro");

        // Sort results and get the first (top) product info
        SearchResultsPage resultsPage = new SearchResultsPage(driver);
        resultsPage.sortByPriceHighToLow();

        SearchResultsPage.ProductInfo topProduct = resultsPage.getProductInfo(0);
        logger.info("Top product after sorting High->Low: {} - ${}", topProduct.getTitle(), topProduct.getPrice());
        System.out.println("TOP PRICE: $" + String.format("%.2f", topProduct.getPrice()));

        // Click top product and add to cart
        ProductDetailPage productDetailPage = resultsPage.clickOnProduct(topProduct.getIndex());
        Assert.assertTrue(productDetailPage.isProductPageLoaded(), "Product details page did not load");

        CartPage cartPage = productDetailPage.addToCartAndViewCart();
        Assert.assertTrue(cartPage.verifyCartIsNotEmpty(), "Cart should not be empty after adding product");
    }
}


