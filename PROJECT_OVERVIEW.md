# PROJECT_OVERVIEW

## Proje Tipi, Java Sürümü ve Giriş Noktaları
- Maven tabanlı yapı `com.test:selenium-cucumber-testng` olarak tanımlı (`pom.xml:6`, `pom.xml:8`).
- Derleme hedefi Java 11; Maven compiler plugin aynı kaynak/bytecode seviyesini kullanıyor (`pom.xml:15`, `pom.xml:115`).
- Test yürütmesi `testng.xml` üzerinden `com.test.runners.TestRunner` sınıfını çağırarak başlıyor (`testng.xml:15`, `src/test/java/com/test/runners/TestRunner.java:7`).
- Surefire eklentisi TestNG suite dosyasını, paralel metod çalıştırmayı ve Cucumber tag filtrelerini sistem özellikleriyle tetikliyor (`pom.xml:121`, `pom.xml:132`, `pom.xml:178`).

## pom.xml Bağımlılıkları
- Selenium WebDriver 4.25.0 UI otomasyon altyapısını sağlıyor (`pom.xml:45`, `pom.xml:50`).
- WebDriverManager 5.6.2 sürücüyü otomatik indirip kuruyor (`pom.xml:53`, `pom.xml:57`).
- Cucumber Java ve Cucumber TestNG bağlayıcıları BDD adım tanımlarını TestNG ile entegre ediyor (`pom.xml:61`, `pom.xml:70`).
- TestNG 7.8.0 test yaşam döngüsünü ve raporlamayı yönetiyor (`pom.xml:74`, `pom.xml:77`).
- Allure Cucumber7 JVM ve Allure TestNG eklentileri birleşik raporlama üretiyor (`pom.xml:80`, `pom.xml:90`).
- Log4j2 (core/api) ayrıntılı günlük kaydı için kullanılıyor (`pom.xml:93`, `pom.xml:103`).
- Surefire plugin bağımlılığı olarak AspectJ weaver Allure enstrümantasyonunu etkinleştiriyor (`pom.xml:232`, `pom.xml:238`).

## src/main/java Paket Yapısı ve Page Object Model
- `config` paketi konfigürasyon çözümleyiciyi içeriyor; `ConfigReader` dosya, sistem özelliği ve çevresel değişkenleri harmanlıyor (`src/main/java/com/test/config/ConfigReader.java:18`, `src/main/java/com/test/config/ConfigReader.java:37`).
- `core` paketi `TestContext` ile senaryolar arası durum paylaşımını yönetiyor (`src/main/java/com/test/core/TestContext.java:11`).
- `model` paketi `Product` veri modelini barındırıyor (`src/main/java/com/test/model/Product.java:5`).
- `pages` paketi Base Page + özel Amazon sayfalarını (Home, SearchResults, ProductDetail, Cart, Login) sunuyor; tümü `BasePage`’i genişletiyor (`src/main/java/com/test/pages/BasePage.java:18`, `src/main/java/com/test/pages/HomePage.java:12`, `src/main/java/com/test/pages/SearchResultsPage.java:22`, `src/main/java/com/test/pages/ProductDetailPage.java:13`, `src/main/java/com/test/pages/CartPage.java:13`, `src/main/java/com/test/pages/LoginPage.java:12`).
- `utils` paketi sürücü yönetimi, beklemeler, ortak yardımcılar, WebDriverFactory gibi çapraz kullanımlı bileşenleri topluyor (`src/main/java/com/test/utils/DriverManager.java:12`, `src/main/java/com/test/utils/CommonUtils.java:13`, `src/main/java/com/test/utils/WaitUtils.java:11`, `src/main/java/com/test/utils/WebDriverFactory.java:21`).

## Test Çerçevesi, Runner, Feature ve Step’ler
- Cucumber + TestNG entegrasyonu `TestRunner` üzerinden yönetiliyor, glue ayarı iki ayrı step paketi içeriyor (`src/test/java/com/test/runners/TestRunner.java:7`, `src/test/java/com/test/runners/TestRunner.java:9`).
- Fixture yönetimi hem `com.test.stepdefinitions.BaseStepDefinitions` hem de `stepDefinitions.Hooks` içinde tanımlı; her ikisi de @Before/@After kancalarıyla DriverManager çağırıyor (`src/test/java/com/test/stepdefinitions/BaseStepDefinitions.java:19`, `src/test/java/stepDefinitions/Hooks.java:25`).
- BDD senaryoları `sample.feature` ve kapsamlı `amazon_macbook_test.feature` dosyalarında toplanmış (`src/test/resources/features/sample.feature:1`, `src/test/resources/features/amazon_macbook_test.feature:1`).
- Basit arama/login adımları `WebStepDefinitions`’ta, detaylı Amazon akışları `AmazonStepDefinitions`’ta uygulanmış (`src/test/java/com/test/stepdefinitions/WebStepDefinitions.java:27`, `src/test/java/stepDefinitions/AmazonStepDefinitions.java:32`).
- TestNG çalıştırması `testng.xml` parametreleriyle (browser, environment, headless) kontrol ediliyor (`testng.xml:9`, `testng.xml:11`).

## Önemli Akışlar ve Sınıflar Arası İlişkiler
- Ana senaryo “Arama → En pahalı ürünü seç → Sepete ekle” `AmazonStepDefinitions` içindeki `findMostExpensiveProduct` çağrılı SearchResults → ProductDetail → Cart navigasyonu ile yürütülüyor (`src/test/java/stepDefinitions/AmazonStepDefinitions.java:690`, `src/main/java/com/test/pages/ProductDetailPage.java:78`, `src/main/java/com/test/pages/CartPage.java:72`).
- Arama öncesi bot kontrolü ve çoklu locator stratejisi `HomePage.searchFor` içinde ele alınıyor (`src/main/java/com/test/pages/HomePage.java:131`).
- Seçilen ürün bilgisi `TestContext` aracılığıyla paylaşılmakta; `AmazonStepDefinitions` sepet doğrulamalarında bu veriyi yeniden kullanıyor (`src/main/java/com/test/core/TestContext.java:18`, `src/test/java/stepDefinitions/AmazonStepDefinitions.java:343`).
- Login akışı `HomePage.clickLoginLink` + `LoginPage.loginWith` kombinasyonuyla sağlanıyor (`src/main/java/com/test/pages/HomePage.java:188`, `src/main/java/com/test/pages/LoginPage.java:52`).

## Potansiyel Riskler / Anti-Pattern’ler
- Bekleme yönetimi artık `WaitUtils.sleep*` yardımcıları üzerinden merkezileştirildi; uzun süreli beklemeler yine de performansı etkileyebileceğinden gerçek koşullarda optimizasyon devam etmeli (`src/main/java/com/test/utils/WaitUtils.java:23`).
- WebDriver yaşam döngüsü `DriverManager` içinde parametreli hale getirildi; yine de paralel senaryolarda ek dayanıklılık kontrolleri planlanabilir (`src/main/java/com/test/utils/DriverManager.java:20`).
- `Hooks` sınıfı kaldırıldı ve yaşam döngüsü tek yerde toplandı; `TestRunner` hâlâ iki glue paketini yüklüyor, ileride step sınıfları da tek pakette birleştirilebilir (`src/test/java/com/test/runners/TestRunner.java:9`).
- Paralel çalıştırma Surefire tarafında açıkken DriverManager tek ThreadLocal örneğiyle Chrome’u sabit başlatıyor, farklı tarayıcılar veya thread güvenliği sınırlı (`pom.xml:132`, `src/main/java/com/test/utils/DriverManager.java:15`).
- AmazonStepDefinitions kapsamına rağmen bazı feature adımları placeholder olarak bırakılmış (örn. marka filtresi) ve başarısız olduğunda genel RuntimeException fırlatıyor (`src/main/java/com/test/pages/SearchResultsPage.java:445`, `src/test/java/stepDefinitions/AmazonStepDefinitions.java:760`).

## İyileştirme Önerileri ve Öncelikli Yapılacaklar
- `amazon_macbook_test.feature` içindeki kapsamlı senaryolar için eksik step implementasyonlarını tamamlayıp gereksiz senaryoları sadeleştirin (`src/test/resources/features/amazon_macbook_test.feature:17`).

## Son İyileştirmeler
- Bekleme stratejisi `WaitUtils.sleep*` yardımcıları ve `CommonUtils.waitFor*` üzerinden `Thread.sleep` bağımlılığından arındırıldı; `SearchResultsPage` ve `BotDetectionHandler` da bu mekanizmayı kullanıyor (`src/main/java/com/test/utils/CommonUtils.java:56`, `src/main/java/com/test/pages/SearchResultsPage.java:757`, `src/main/java/com/test/utils/BotDetectionHandler.java:427`).
- WebDriver yaşam döngüsü `DriverManager` içine konsolide edildi; `WebDriverFactory` kaldırıldı ve tarayıcı seçimleri konfigürasyonla parametreleniyor (`src/main/java/com/test/utils/DriverManager.java:20`).
- Cucumber hook’ları `BaseStepDefinitions` altında birleştirildi; senaryo süresi ve hata ekran görüntüsü yönetimi tek noktadan sağlanıyor (`src/test/java/com/test/stepdefinitions/BaseStepDefinitions.java:18`).
- `AmazonStepDefinitions` senaryoları seçilen ürünü `TestContext` içinde saklayarak sepet doğrulamasında yeniden kullanıyor (`src/test/java/stepDefinitions/AmazonStepDefinitions.java:343`).

## Çalıştırma Adımları (IntelliJ & Komut Satırı)
- **IntelliJ IDEA**: Projeyi Maven olarak içe aktarın, `Lifecycle > clean` ve `test` hedeflerini çalıştırın; veya `testng.xml` dosyasını sağ tıklayıp “Run” deyin (`testng.xml:15`). Gerekirse `Run/Debug Configurations` içinde VM seçeneklerine `-Dcucumber.filter.tags="@smoke"` ekleyin.
- **Komut Satırı**: `mvn clean test` varsayılan tüm senaryoları koşturur (`pom.xml:41`). Belirli tag’ler için `mvn clean test -Dcucumber.filter.tags="@macbook"` kullanın (`pom.xml:178`). Headless çalışma için `-Dheadless=true` veya `-Dconfig.headless=true` argümanlarını iletin (`pom.xml:30`, `src/main/java/com/test/config/ConfigReader.java:118`).
