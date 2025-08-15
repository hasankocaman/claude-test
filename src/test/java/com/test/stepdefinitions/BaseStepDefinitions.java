package com.test.stepdefinitions;

import com.test.config.ConfigReader;
import com.test.utils.DriverManager;
import com.test.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(BaseStepDefinitions.class);
    
    @Before
    public void setUp(Scenario scenario) {
        logger.info("Starting scenario: " + scenario.getName());
        DriverManager.initializeDriver();
        logger.info("WebDriver initialized successfully");
    }
    
    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                logger.error("Scenario failed: " + scenario.getName());
                // Take screenshot before quitting driver
                if (DriverManager.hasDriver()) {
                    ScreenshotUtils.captureScreenshotOnFailure(scenario.getName());
                }
            } else {
                logger.info("Scenario passed: " + scenario.getName());
            }
        } catch (Exception e) {
            logger.warn("Error during teardown screenshot: " + e.getMessage());
        } finally {
            // Always quit driver
            DriverManager.quitDriver();
            logger.info("WebDriver quit successfully");
        }
    }
}