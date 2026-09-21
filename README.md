# Chuyên đề Java Enterprise Edition (J2EE) - SGU

> **Học phần:** Chuyên đề J2EE (Mã HP: `841468`)  
> **Trường:** Đại học Sài Gòn (SGU)  
> **Tài liệu học tập:** *Hướng dẫn tự học và nghiên cứu học phần Chuyên đề J2EE* (Mã số: CK2025 - 03)  
> **Chủ biên:** ThS. Nguyễn Thanh Phước  

---

## 📌 Giới thiệu Repo
Kho lưu trữ mã nguồn các bài tập thực hành, bài mẫu và bài tập làm thêm (dự án tự luyện) theo đề cương học phần **Chuyên đề J2EE**.

### Công nghệ & Môi trường sử dụng
* **Ngôn ngữ:** Java 17+ (hoặc Java 21)
* **Framework:** Spring Boot 3.x / Spring Framework 6.x
* **Build Tool:** Maven / Gradle
* **IDE:** IntelliJ IDEA Community / Ultimate
* **Database:** MySQL 8.x, H2 Database
* **Thư viện chính:** Spring Web, Spring Data JPA, Spring Validation, Spring HATEOAS, Lombok, MapStruct, SpringDoc OpenAPI (Swagger UI), SLF4J / Logback.

---

## 📂 Cấu trúc thư mục học phần

```text
Lab/
├── Chuong1/          # KIẾN TRÚC SPRING BOOT
│   ├── Phan1/        # Spring Framework (IoC, DI, Bean Scopes)
│   │   ├── BaiMau/   # Bài 1 (Spring Initializr), Bài 2 (IoC/DI Calculator), Bài 3 (Bean Scope), Bài 4 (Dựng dự án mẫu)
│   │   └── BaiThem/  # Dự án 1.1 -> Dự án 1.6
│   ├── Phan2/        # Spring Boot (REST API cơ bản, Thymeleaf)
│   │   ├── BaiMau/   # Bài 1 (HelloWorld API), Bài 2 (Thymeleaf Library)
│   │   └── BaiThem/  # Dự án 1.7 -> Dự án 1.10
│   └── Phan3/        # Spring Project (Kiến trúc nhiều tầng: Eazyschool)
│       ├── BaiMau/   # Bài 1 (Eazyschool Architecture)
│       └── BaiThem/  # Dự án 1.11 -> Dự án 1.14
│
├── Chuong2/          # RESTFUL API
│   ├── Phan1/        # RESTful API CRUD (Player, Book)
│   ├── Phan2/        # Swagger & OpenAPI (SpringDoc UI)
│   └── Phan3/        # HATEOAS (Hypermedia as the Engine of Application State)
│
├── Chuong3/          # VALIDATE DỮ LIỆU - ENUM VÀ RESPONSE
│   ├── Phan1/        # Bean Validation (JSR-380) & Enum
│   └── Phan2/        # Chuẩn hóa Response & ResponseEntity
│
├── Chuong4/          # XỬ LÝ NGOẠI LỆ & LOGGING
│   ├── Phan1/        # Global Exception Handling (@ControllerAdvice, @ExceptionHandler)
│   └── Phan2/        # Hệ thống Logging (Logback, Rolling File, Log levels)
│
├── Chuong5/          # LOMBOK VÀ MAPSTRUCT
│   ├── Phan1/        # Lombok (Loại bỏ Boilerplate code)
│   └── Phan2/        # MapStruct (Ánh xạ Entity <-> DTO ở compile-time)
│
└── Chuong6/          # JPA & HIBERNATE
    ├── Phan1/        # JPA cơ bản & Entity Mapping
    └── Phan2/        # Spring Data JPA & Transaction Management (@Transactional)
```

---

## 🚀 Hướng dẫn chạy dự án Spring Boot

1. **Mở dự án:**
   * Khởi động IntelliJ IDEA -> Chọn `Open` -> Trỏ đến thư mục bài tập tương ứng (thư mục chứa `pom.xml`).
2. **Build Maven:**
   ```bash
   ./mvnw clean install
   # hoặc
   mvn clean install
   ```
3. **Chạy ứng dụng:**
   * Chạy trực tiếp từ class `@SpringBootApplication` trong IDE hoặc:
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Kiểm tra API / Giao diện:**
   * Web UI: `http://localhost:8080/`
   * Swagger UI: `http://localhost:8080/swagger-ui/index.html`
