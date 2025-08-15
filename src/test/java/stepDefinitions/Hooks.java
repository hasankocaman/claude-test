package stepDefinitions;

import com.test.config.ConfigReader;
import com.test.utils.CommonUtils;
import com.test.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber Hooks for Amazon MacBook Pro Tests
 * Handles setup and teardown operations for each test scenario
 */
public class Hooks {
    
    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private static int scenarioCounter = 0;
    private long scenarioStartTime;
    
    /**
     * Before hook - executed before each scenario
     */
    @Before
    public void setUp(Scenario scenario) {
        scenarioCounter++;
        scenarioStartTime = System.currentTimeMillis();
        
        logger.info("┌─────────────────────────────────────────────────────────────────────");
        logger.info("│ SCENARIO #{}: {}", scenarioCounter, scenario.getName());
        logger.info("│ Tags: {}", scenario.getSourceTagNames());
        logger.info("└─────────────────────────────────────────────────────────────────────");
        
        try {
            // Driver is initialized when needed by page objects
            logger.info("✓ Scenario setup completed successfully");
            
        } catch (Exception e) {
            logger.error("✗ Failed to set up scenario: {}", e.getMessage(), e);
            takeScreenshotOnError(scenario, "setup_failed");
            throw new RuntimeException("Scenario setup failed", e);
        }
    }
    
    /**
     * After hook - executed after each scenario
     */
    @After
    public void tearDown(Scenario scenario) {
        long scenarioEndTime = System.currentTimeMillis();
        long executionTime = scenarioEndTime - scenarioStartTime;
        
        try {
            // Handle scenario result
            if (scenario.isFailed()) {
                logger.error("✗ SCENARIO FAILED: {}", scenario.getName());
                takeFailureScreenshot(scenario);
            } else {
                logger.info("✓ SCENARIO PASSED: {}", scenario.getName());
                if (shouldTakeSuccessScreenshot()) {
                    takeSuccessScreenshot(scenario);
                }
            }
            
            // Clean up WebDriver
            try {
                if (DriverManager.hasDriver()) {
                    DriverManager.quitDriver();
                }
            } catch (Exception e) {
                logger.warn("Warning during WebDriver cleanup: {}", e.getMessage());
            }
            
            // Log scenario completion
            logger.info("│ Execution Time: {} ms", executionTime);
            logger.info("│ Status: {}", scenario.getStatus());
            logger.info("└─────────────────────────────────────────────────────────────────────");
            
        } catch (Exception e) {
            logger.error("✗ Error during scenario cleanup: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Take screenshot on test failure
     */
    private void takeFailureScreenshot(Scenario scenario) {
        if (ConfigReader.isScreenshotOnFailure()) {
            try {
                String scenarioName = scenario.getName().replaceAll("\\s+", "_").toLowerCase();
                String fileName = "FAILED_" + scenarioName + "_" + System.currentTimeMillis();
                
                if (DriverManager.hasDriver()) {
                    CommonUtils.takeScreenshot(DriverManager.getDriver(), fileName);
                    logger.info("Failure screenshot saved: {}", fileName);
                }
            } catch (Exception e) {
                logger.warn("Could not take failure screenshot: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Take screenshot on test success (optional)
     */
    private void takeSuccessScreenshot(Scenario scenario) {
        try {
            String scenarioName = scenario.getName().replaceAll("\\s+", "_").toLowerCase();
            String fileName = "SUCCESS_" + scenarioName + "_" + System.currentTimeMillis();
            
            if (DriverManager.hasDriver()) {
                CommonUtils.takeScreenshot(DriverManager.getDriver(), fileName);
                logger.debug("Success screenshot saved: {}", fileName);
            }
        } catch (Exception e) {
            logger.debug("Could not take success screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Take screenshot on error
     */
    private void takeScreenshotOnError(Scenario scenario, String errorType) {
        try {
            String scenarioName = scenario.getName().replaceAll("\\s+", "_").toLowerCase();
            String fileName = "ERROR_" + errorType + "_" + scenarioName + "_" + System.currentTimeMillis();
            
            if (DriverManager.hasDriver()) {
                CommonUtils.takeScreenshot(DriverManager.getDriver(), fileName);
                logger.info("Error screenshot saved: {}", fileName);
            }
        } catch (Exception e) {
            logger.warn("Could not take error screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Check if success screenshots should be taken
     */
    private boolean shouldTakeSuccessScreenshot() {
        // Only take success screenshots if explicitly configured
        return false; // Simplified for now
    }
}