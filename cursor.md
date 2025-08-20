## Proje Özeti ve Mimarisi

Bu proje, Selenium WebDriver + Cucumber + TestNG stack kullanarak Amazon üzerinde temel arama ve sepet senaryolarını yürüten bir UI test otomasyon çatısıdır. Page Object Model (POM) benimsenmiştir.

### OOP Kullanımı
- **Abstraction**: Tüm sayfa sınıfları `BasePage` üzerinden ortak işlevleri devralır. Beklemeler, tıklama/yazma, gezinme, dropdown seçimi, retry mekanizmaları gibi karmaşıklıklar `BasePage` içinde soyutlanır.
- **Inheritance**: `HomePage`, `SearchResultsPage`, `ProductDetailPage`, `CartPage`, `LoginPage` sınıfları `BasePage`'den türeyerek ortak davranışları miras alır.
- **Encapsulation**: Web öğeleri `@FindBy` ile private alanlar olarak tanımlanır; dış dünya yalnızca public yöntemler üzerinden etkileşir.
- **Polymorphism**: `BasePage` içindeki overloaded `executeWithRetry` metotları (Runnable/Supplier), tıklama/yazma/okuma işlemlerinde farklı davranışlar sunar. Farklı sayfa sınıfları aynı temel API'leri kendi bağlamlarında kullanır.
- **Single Responsibility Principle (SRP)**: 
  - `ConfigReader`: yapılandırma okuma
  - `DriverManager` / `WebDriverFactory`: sürücü yaşam döngüsü ve oluşturma
  - `WaitUtils`: bekleme stratejileri
  - `CommonUtils`: tekrar kullanılabilir yardımcılar (fiyat çıkarımı, JS işlemleri, ekran görüntüleri vb.)
  - Sayfa sınıfları: yalnızca sayfa etkileşimleri

### Ana Bileşenler
- `com.test.pages.BasePage`: Ortak sürücü, bekleme, logger ve güvenilir etkileşim yardımcılarını sunan soyut temel sınıf.
- `com.test.pages.HomePage`: Arama kutusu ve butonu ile arama akışı, bot/captcha toleransı.
- `com.test.pages.SearchResultsPage`: Sonuçları bekleme, başlık/fiyat/puan çıkarımı, sıralama, en pahalı MacBook Pro’yu bulma ve tıklama.
- `com.test.pages.ProductDetailPage`: Ürün başlığı/fiyatı/özellikleri, adet, Prime/teslimat bilgisi, sepete ekleme akışları.
- `com.test.pages.CartPage`: Sepet durum/doğrulama, miktar güncelleme, silme, ödeme adımına ilerleme.
- `com.test.pages.LoginPage`: Basit login akışı doğrulama.
- `com.test.config.ConfigReader`: Konfigürasyon dosyasını yükler ve tipli getter’lar sağlar.
- `com.test.utils.DriverManager`: Thread-safe WebDriver yönetimi, sağlık kontrolü ve yeniden başlatma.
- `com.test.utils.WebDriverFactory`: Farklı tarayıcılar için sürücü üretimi.
- `com.test.utils.WaitUtils`: Gelişmiş bekleme yardımcıları (DOM, AJAX, jQuery, frame, alert, metin, URL).
- `com.test.utils.CommonUtils`: Fiyat/numara çıkarımı, ekran görüntüsü, JS, liste/string yardımcıları.
- `com.test.utils.ScreenshotUtils`: Ekran görüntüsü alımı ve Allure’a ekleme.

### Proje Nasıl Çalışır?
1. Cucumber senaryoları `TestRunner` (TestNG) ile çalıştırılır. Özellik dosyaları `src/test/resources/features` altındadır.
2. `BaseStepDefinitions` sınıfı `@Before` hook'unda `DriverManager.initializeDriver()` çağırarak sürücüyü hazırlar; `@After` hook'unda senaryo durumuna göre ekran görüntüsü alır ve sürücüyü kapatır.
3. Step Definitions (`WebStepDefinitions`) Page Object’leri kullanarak akışları yürütür:
   - Uygulama URL’sine gitme (`ConfigReader.getBaseUrl()`).
   - `HomePage.searchFor` ile arama.
   - `SearchResultsPage` üzerinde sıralama/ürün seçimi.
   - `ProductDetailPage` üzerinden sepete ekleme.
   - `CartPage` üzerinde doğrulamalar ve ödeme adımına ilerleme.
4. Raporlama Cucumber HTML/JSON/XML ve Allure entegrasyonları ile sağlanır.

### Çalıştırma
- Maven: `mvn clean test`
- Tarayıcı, beklemeler ve ekran görüntüsü ayarları `src/test/resources/config/config.properties` dosyasından yönetilir.

### Güvenilirlik ve Dayanıklılık
- Retry mekanizmaları (`BasePage.executeWithRetry`, `clickWithRetry`, `sendKeysWithRetry`).
- Gelişmiş beklemeler (`WaitUtils.waitForFullPageReady`, AJAX/jQuery kontrolleri).
- `DriverManager.isBrowserHealthy()` ve gerekirse otomatik yeniden başlatma.

Bu yapı sayesinde testler, dinamik ve değişken içerikli sayfalarda daha stabil ve bakımı kolay bir mimari ile yürütülür.


