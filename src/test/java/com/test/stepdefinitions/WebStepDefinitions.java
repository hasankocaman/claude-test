package com.test.stepdefinitions;

import com.test.config.ConfigReader;
import com.test.pages.HomePage;
import com.test.pages.LoginPage;
import com.test.utils.DriverManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class WebStepDefinitions {
    private static final Logger logger = LogManager.getLogger(WebStepDefinitions.class);
    private HomePage homePage;
    private LoginPage loginPage;
    
    public WebStepDefinitions() {
        this.homePage = new HomePage();
        this.loginPage = new LoginPage();
    }
    
    @Given("the user navigates to the application")
    public void theUserNavigatesToTheApplication() {
        String baseUrl = ConfigReader.getBaseUrl();
        logger.info("Navigating to: " + baseUrl);
        DriverManager.getDriver().get(baseUrl);
    }
    
    @When("the user is on the home page")
    public void theUserIsOnTheHomePage() {
        logger.info("User is on the home page");
        String currentUrl = DriverManager.getDriver().getCurrentUrl();
        logger.info("Current URL: " + currentUrl);
    }
    
    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitle) {
        String actualTitle = homePage.getPageTitle();
        logger.info("Verifying page title contains: " + expectedTitle);
        Assert.assertTrue(actualTitle.toLowerCase().contains(expectedTitle.toLowerCase()),
                "Page title '" + actualTitle + "' does not contain '" + expectedTitle + "'");
    }
    
    @When("the user searches for {string}")
    public void theUserSearchesFor(String searchTerm) {
        logger.info("Performing search for: " + searchTerm);
        homePage.searchFor(searchTerm);
    }
    
    @Then("search results should be displayed")
    public void searchResultsShouldBeDisplayed() {
        logger.info("Verifying search results are displayed");
        Assert.assertTrue(homePage.areSearchResultsDisplayed(), "Search results are not displayed");
    }
    
    @And("results should contain {string} keyword")
    public void resultsShouldContainKeyword(String keyword) {
        String resultsText = homePage.getSearchResultsText();
        logger.info("Verifying search results contain keyword: " + keyword);
        Assert.assertTrue(resultsText.toLowerCase().contains(keyword.toLowerCase()),
                "Search results do not contain keyword: " + keyword);
    }
    
    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        theUserNavigatesToTheApplication();
        homePage.clickLoginLink();
        logger.info("User is on the login page");
    }
    
    @When("the user enters username {string} and password {string}")
    public void theUserEntersUsernameAndPassword(String username, String password) {
        logger.info("Entering login credentials");
        loginPage.loginWith(username, password);
    }
    
    @And("clicks the login button")
    public void clicksTheLoginButton() {
        logger.info("Login button already clicked in previous step");
    }
    
    @Then("the login result should be {string}")
    public void theLoginResultShouldBe(String expectedResult) {
        logger.info("Verifying login result: " + expectedResult);
        if ("success".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(loginPage.isLoginSuccessful(), "Login was not successful");
        } else if ("failure".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(loginPage.isLoginFailed(), "Login did not fail as expected");
        } else {
            throw new IllegalArgumentException("Invalid expected result: " + expectedResult);
        }
    }
}