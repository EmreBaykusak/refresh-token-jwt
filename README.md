# Refresh Token Mimarisi - JWT Kimlik Doğrulama Sistemi

Bu proje, Spring Boot ve JWT (JSON Web Token) kullanılarak geliştirilmiş bir kimlik doğrulama (authentication) mekanizmasıdır. Temel hedef; Access Token ve Refresh Token mimarisini uygulayarak gerçek dünya uygulamalarında kullanılan güvenli ve sürdürülebilir bir backend altyapısı oluşturmaktır.

## 📌 Proje Amacı

Bu proje kapsamında aşağıdaki adımlar gerçekleştirilmiştir:

- JWT tabanlı kimlik doğrulama (authentication) yapısının kurulması
- **Access Token** ve **Refresh Token** kavramlarının uygulanması
- Mevcut projenin Refresh Token mimarisine uygun şekilde yeniden tasarlanması
- Token yenileme (refresh) mekanizmasının implement edilmesi
- Güvenli ve sürdürülebilir bir authentication altyapısının oluşturulması

## 🚀 Teknolojiler

Java 26 · Spring Boot · Spring Security · jjwt 0.12.6 · Maven

## ⚙️ Kurulum ve Çalıştırma

### 1. Projeyi klonlayın

```bash
git clone https://github.com/EmreBaykusak/refresh-token-jwt.git
cd refresh-token-jwt
```

### 2. `.env` dosyası oluşturun

Proje dizininde bir `.env` dosyası oluşturup aşağıdaki değişkeni tanımlayın:

```env
JWT_SECRET=buraya-gizli-ve-uzun-bir-anahtar-yazin
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000
```

> ⚠️ `.env` dosyasını asla Git'e commit etmeyin. `.gitignore` dosyanızda `.env` satırının bulunduğundan emin olun.

Bu değişkenler `application.properties` içinde şu şekilde referans alınmaktadır:

```properties
jwt.secret=${JWT_SECRET}
jwt.access.expiration=${JWT_ACCESS_EXPIRATION:3600000}
jwt.refresh.expiration=${JWT_REFRESH_EXPIRATION:604800000}
```

| Değişken | Açıklama | Varsayılan |
| :--- | :--- | :--- |
| `JWT_SECRET` | JWT imzalamak için kullanılan gizli anahtar | — (zorunlu) |
| `JWT_ACCESS_EXPIRATION` | Access Token geçerlilik süresi (ms) | `3600000` (1 saat) |
| `JWT_REFRESH_EXPIRATION` | Refresh Token geçerlilik süresi (ms) | `604800000` (7 gün) |

### 3. Projeyi derleyin ve çalıştırın

```bash
mvn clean install
mvn spring-boot:run
```

## 🧪 Testler

```bash
mvn test
```

Proje üç ana test senaryosu içermektedir:

- **TokenManagerTest** — Token oluşturma, imza doğrulama ve claims çıkarma
- **AuthenticationTest** — Başarılı/başarısız kullanıcı giriş denemeleri
- **RefreshMechanismTest** — Geçerli Refresh Token ile yeni Access Token alma

## 🔐 Mimari Akış

```
1. Login     →  Kullanıcı doğrulanır → Access Token + Refresh Token döner
2. Access    →  İstemci, Access Token ile korunan kaynaklara erişir
3. Expire    →  Access Token süresi dolar → 401 Unauthorized
4. Refresh   →  İstemci /refresh endpoint'ine Refresh Token gönderir
5. Renew     →  Yeni bir Access Token üretilir ve döner
```