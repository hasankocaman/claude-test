# Amazon MacBook Pro Test Automation

Selenium WebDriver, Cucumber BDD ve TestNG kullanarak Amazon.com'da MacBook Pro arama ve sepete ekleme sürecini test eden otomasyon projesi.

## 🚀 Hızlı Başlangıç

### Ön Koşullar

- **Java JDK 11+** (Oracle JDK veya OpenJDK)
- **Maven 3.6+**
- **Chrome Browser** (otomatik olarak yönetilir)
- **Git** (proje klonlamak için)

### Kurulum Talimatları

1. **Projeyi klonlayın**
```bash
git clone <repository-url>
cd amazon-macbook-test
```

2. **Dependencies yükleyin**
```bash
mvn clean install
```

3. **Java ve Maven versiyonlarını kontrol edin**
```bash
java -version
mvn -version
```

### Konfigürasyon

Test ayarlarını `src/test/resources/config/config.properties` dosyasından yapabilirsiniz:

```properties
# Browser ayarları
browser=chrome
headless=false
window.maximize=true

# Timeout ayarları
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Test URL'leri
base.url=https://www.amazon.com/
```

## 🧪 Test Çalıştırma Komutları

### Tüm Testleri Çalıştırma
```bash
mvn test
```

### Belirli Tag'li Testleri Çalıştırma
```bash
# Sadece smoke testleri
mvn test -Dcucumber.filter.tags="@smoke"

# Sadece regression testleri  
mvn test -Dcucumber.filter.tags="@regression"

# MacBook testleri
mvn test -Dcucumber.filter.tags="@macbook"
```

### Paralel Test Çalıştırma
```bash
mvn test -Dparallel.thread.count=3
```

### Headless Mode'da Çalıştırma
```bash
mvn test -Dheadless=true
```

### Farklı Browser ile Çalıştırma
```bash
# Firefox ile
mvn test -Dbrowser=firefox

# Edge ile  
mvn test -Dbrowser=edge
```

### Clean ve Test
```bash
mvn clean test
```

## 📊 Raporlama

### Allure Reports

1. **Allure sonuçlarını oluştur**
```bash
mvn allure:serve
```

2. **Allure raporu indir**
```bash
mvn allure:report
```
Rapor `target/site/allure-maven-plugin/` dizininde oluşturulur.

### Cucumber Reports

Testler çalıştırıldıktan sonra aşağıdaki rapor dosyaları oluşturulur:

- **HTML Report**: `target/cucumber-html-report.html`
- **JSON Report**: `target/cucumber-json-report.json`  
- **XML Report**: `target/cucumber-xml-report.xml`

### TestNG Reports

- **Test Results**: `target/surefire-reports/`
- **TestNG HTML**: `test-output/index.html`

### Loglar

- **Ana Log Dosyası**: `target/logs/application.log`
- **Test Execution Log**: `target/logs/test-execution.log`
- **Error Log**: `target/logs/errors.log`

## 🔧 Maven Profil Kullanımı

### Smoke Test Profili
```bash
mvn test -Psmoke
```

### Regression Test Profili  
```bash
mvn test -Pregression
```

### CI/CD Profili
```bash
mvn test -Pci
```

## 📁 Önemli Dosyalar

| Dosya | Açıklama |
|-------|----------|
| `pom.xml` | Maven dependencies ve plugin konfigürasyonu |
| `src/test/resources/testng.xml` | TestNG suite konfigürasyonu |
| `src/test/resources/config/config.properties` | Test ayarları |
| `src/test/resources/features/` | Cucumber feature dosyaları |
| `src/test/resources/log4j2.xml` | Logging konfigürasyonu |

## 🐛 Sorun Giderme

### Yaygın Problemler

1. **"WebDriver not found" hatası**
   - WebDriverManager otomatik olarak driver'ları yönetir
   - Internet bağlantınızı kontrol edin

2. **"Element not found" hatası**
   - Timeout değerlerini artırın
   - `config.properties`'de `explicit.wait` değerini yükseltin

3. **"Port already in use" hatası**  
   - Çalışan Chrome instance'larını kapatın
   - `mvn clean` komutunu çalıştırın

### Debug Modu
```bash
mvn test -X -Ddebug=true
```

### Verbose Logging
```bash
mvn test -Dlog.level=DEBUG
```

## 🚀 CI/CD Entegrasyonu

### GitHub Actions
```yaml
- name: Run Tests
  run: mvn clean test -Pci -Dheadless=true
  
- name: Generate Allure Report
  run: mvn allure:report
```

### Jenkins Pipeline
```groovy
stage('Test') {
    steps {
        sh 'mvn clean test -Pci'
    }
    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])
        }
    }
}
```

## 📈 Test Metrics

Proje aşağıdaki test metriklerini sağlar:

- **Test Coverage**: Feature bazlı coverage
- **Execution Time**: Test süre analizi  
- **Success Rate**: Başarı oranı
- **Browser Compatibility**: Çoklu tarayıcı desteği
- **Performance**: Sayfa yükleme süreleri

## 🤝 Katkıda Bulunma

1. Repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📝 Sürüm Notları

### v1.0.0
- ✅ Amazon MacBook Pro arama testi
- ✅ Sepete ekleme fonksiyonalitesi
- ✅ Page Object Model implementasyonu
- ✅ Allure reporting entegrasyonu
- ✅ Cross-browser support
- ✅ Parallel test execution

## 📞 Destek

Sorularınız için:
- Issue açın GitHub'da
- Dokümantasyonu kontrol edin (`CLAUDE.md`)
- Log dosyalarını inceleyin