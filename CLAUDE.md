# Amazon MacBook Pro Test Automation Project

## Proje Amacı

Bu proje, Amazon.com'da MacBook Pro arama yaparak en yüksek fiyatlı modeli sepete ekleme sürecini test etmek için geliştirilmiş bir otomasyon test suite'idir. BDD (Behavior Driven Development) yaklaşımı ile Cucumber framework'ü kullanılarak yazılmıştır.

## Teknoloji Stack

- **Java 11+**: Ana programlama dili
- **Maven 3.x**: Dependency management ve build tool
- **Selenium WebDriver 4.25.0**: Web browser automation
- **Cucumber 7.15.0**: BDD framework ve test runner
- **TestNG 7.8.0**: Test execution framework
- **WebDriverManager 5.6.2**: Otomatik WebDriver yönetimi
- **Allure 2.24.0**: Test reporting framework
- **Log4j2 2.22.0**: Logging framework

## Page Object Model Kullanımı

Proje, sürdürülebilirlik ve okunabilirlik için Page Object Model (POM) design pattern'ini kullanmaktadır:

### BasePage Sınıfı
- Tüm page class'larının miras aldığı temel sınıf
- Ortak WebDriver işlemleri (click, sendKeys, waitFor vb.)
- Logging ve exception handling

### Sayfa Sınıfları
- **HomePage**: Amazon ana sayfa işlemleri
- **SearchResultsPage**: Arama sonuçları sayfası işlemleri
- **ProductDetailPage**: Ürün detay sayfası işlemleri
- **CartPage**: Sepet sayfası işlemleri

### Utility Sınıfları
- **DriverManager**: WebDriver instance yönetimi
- **ConfigReader**: Configuration dosyası okuma
- **ScreenshotUtils**: Ekran görüntüsü alma ve Allure entegrasyonu

## Test Senaryoları

### Ana Test Senaryosu: MacBook Pro Sepete Ekleme
```gherkin
Feature: Amazon MacBook Pro Search and Cart
  
  Scenario: Find most expensive MacBook Pro and add to cart
    Given the user is on Amazon homepage
    When the user searches for "MacBook Pro"
    Then search results should be displayed
    When the user sorts results by "Price: High to Low"
    And selects the most expensive MacBook Pro
    And adds the product to cart
    Then the product should be successfully added to cart
```

### Test Senaryoları Kategorileri:
1. **Search Functionality Tests**: Arama fonksiyonlarını test eder
2. **Product Listing Tests**: Ürün listeleme ve sıralama testleri
3. **Cart Operations Tests**: Sepet işlemleri testleri
4. **Navigation Tests**: Sayfa navigasyon testleri

## Klasör Yapısı Açıklaması

```
src/
├── main/java/com/test/
│   ├── config/
│   │   └── ConfigReader.java          # Configuration yönetimi
│   ├── pages/
│   │   ├── BasePage.java              # Temel page sınıfı
│   │   ├── HomePage.java              # Amazon ana sayfa
│   │   ├── SearchResultsPage.java     # Arama sonuçları sayfası
│   │   ├── ProductDetailPage.java     # Ürün detay sayfası
│   │   └── CartPage.java              # Sepet sayfası
│   └── utils/
│       ├── DriverManager.java         # WebDriver yönetimi
│       └── ScreenshotUtils.java       # Ekran görüntüsü utilities
├── test/java/com/test/
│   ├── runners/
│   │   └── TestRunner.java            # Cucumber TestNG runner
│   └── stepdefinitions/
│       ├── BaseStepDefinitions.java   # Temel step definitions
│       ├── HomePageSteps.java         # Ana sayfa step definitions
│       ├── SearchSteps.java           # Arama step definitions
│       ├── ProductSteps.java          # Ürün step definitions
│       └── CartSteps.java             # Sepet step definitions
└── test/resources/
    ├── config/
    │   └── config.properties           # Test konfigürasyonu
    ├── features/
    │   ├── amazon_search.feature       # Ana test senaryoları
    │   └── navigation.feature          # Navigasyon testleri
    ├── testdata/
    │   └── testdata.json              # Test verileri
    ├── log4j2.xml                     # Logging konfigürasyonu
    └── testng.xml                     # TestNG suite konfigürasyonu
```

## Test Konfigürasyonu

### Browser Ayarları
- **Default Browser**: Chrome
- **Headless Mode**: Configurable (default: false)
- **Window Management**: Maximize on startup
- **Timeouts**: Implicit (10s), Explicit (20s), Page Load (30s)

### Test Verileri
- Arama terimleri: "MacBook Pro", "MacBook Pro 16", "MacBook Pro M3"
- Sıralama seçenekleri: "Price: High to Low", "Price: Low to High"
- Test kullanıcı bilgileri (isteğe bağlı login testleri için)

### Reporting
- **Allure Reports**: Detaylı test raporları ve ekran görüntüleri
- **Cucumber Reports**: HTML, JSON, XML formatlarında
- **TestNG Reports**: Test execution sonuçları
- **Logging**: Detaylı sistem ve test logları

## Test Stratejisi

### Test Seviyeleri
1. **Smoke Tests**: Temel fonksiyonalite kontrolü
2. **Regression Tests**: Tam test coverage
3. **Cross-Browser Tests**: Chrome, Firefox, Edge support

### Test Data Management
- Properties dosyalarında environment-specific veriler
- JSON dosyalarında test verileri
- Parameterized testler için Cucumber Examples

### Error Handling
- Timeout durumlarında otomatik retry
- Test başarısızlığında ekran görüntüsü alma
- Detaylı hata mesajları ve stack trace logging

## Environment Support

- **Local**: Development ortamı
- **CI/CD**: Jenkins/GitHub Actions entegrasyonu
- **Docker**: Containerized test execution (opsiyonel)
- **Parallel Execution**: TestNG ile paralel test çalıştırma