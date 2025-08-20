package com.test.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions", "com.test.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-html-report.html",
                "json:target/cucumber-json-report.json",
                "junit:target/cucumber-xml-report.xml",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true,
        publish = false
)
/**
 * Cucumber + TestNG koşucu sınıfı.
 * Özellik dosyaları, glue paketleri ve raporlayıcı eklentilerini konfigüre eder.
 */
public class TestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = false)
    /**
     * Senaryoların parametre sağlayıcısı (gerekirse paralel çalıştırma için ayarlanabilir).
     */
    public Object[][] scenarios() {
        return super.scenarios();
    }
}