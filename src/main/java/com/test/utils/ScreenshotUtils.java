package com.test.utils;

import com.test.config.ConfigReader;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Ekran görüntüsü yardımcı sınıfı.
 * Hata durumlarında ve istenildiğinde ekran görüntüsü alıp dosyaya kaydetme ve
 * Allure raporuna ek olarak ekleme işlevlerini sağlar.
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    
    /**
     * Verilen test adıyla PNG ekran görüntüsü alır ve konfigüre edilen dizine kaydeder.
     * @param testName dosya adı için temel ad
     * @return tam dosya yolu; hata olursa null
     */
    public static String captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_" + timestamp + ".png";
            String screenshotPath = ConfigReader.getScreenshotPath();
            
            File directory = new File(screenshotPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fullPath = screenshotPath + File.separator + fileName;
            Files.write(Paths.get(fullPath), screenshot);
            
            logger.info("Screenshot captured: " + fullPath);
            return fullPath;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
    @Attachment(value = "Screenshot", type = "image/png")
    /**
     * Allure raporuna ekran görüntüsü ekler.
     * @return PNG byte dizisi; hata olursa boş dizi
     */
    public static byte[] attachScreenshotToAllure() {
        try {
            WebDriver driver = DriverManager.getDriver();
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: " + e.getMessage());
            return new byte[0];
        }
    }
    
    /**
     * Konfigürasyonda etkinse başarısızlık anında ekran görüntüsü alır ve Allure'a ekler.
     */
    public static void captureScreenshotOnFailure(String scenarioName) {
        if (ConfigReader.isScreenshotOnFailure()) {
            captureScreenshot(scenarioName);
            attachScreenshotToAllure();
        }
    }
}