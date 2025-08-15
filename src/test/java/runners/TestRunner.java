package runners;

import com.test.config.ConfigReader;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG Test Runner for Amazon MacBook Pro Tests
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions", "com.test.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/html-report",
                "json:target/cucumber-reports/json-report/cucumber.json",
                "junit:target/cucumber-reports/xml-report/cucumber.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "@smoke or @macbook",
        monochrome = true,
        dryRun = false,
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
    
    private static final Logger logger = LogManager.getLogger(TestRunner.class);
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Starting Amazon MacBook Pro Test Execution");
        logger.info("Base URL: {}", ConfigReader.getBaseUrl());
        logger.info("Browser: {}", ConfigReader.getBrowser());
        logger.info("Headless Mode: {}", ConfigReader.isHeadless());
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Amazon MacBook Pro Test Execution Completed");
    }
    
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}