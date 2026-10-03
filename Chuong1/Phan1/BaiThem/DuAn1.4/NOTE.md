# 📘 Ghi Chú Kiến Thức - Dự Án 1.4: Đọc Tập Tin Cấu Hình Với @PropertySource Và @Value

> **Thư mục bài làm:** `Chuong1/Phan1/BaiThem/DuAn1.4`  
> **Nội dung trọng tâm:** Cơ chế Externalized Configuration, nạp tài nguyên cấu hình với `@PropertySource`, tiêm giá trị cấu hình bằng `@Value`, truy xuất động qua `Environment`, và thời điểm khởi tạo với `InitializingBean`.

---

## 1. Vấn Đề "Hard-coding" & Khái Niệm Externalized Configuration

### 1.1. Tác hại của Hard-coding
* Nếu viết cứng các thông tin nhạy cảm hoặc dễ thay đổi (như URL cơ sở dữ liệu, username, password, port, secret key) trực tiếp trong mã nguồn Java:
  - Khi cần chuyển môi trường (Development, Testing, Staging, Production), lập trình viên phải sửa code và biên dịch (recompile), đóng gói (repackage) lại toàn bộ ứng dụng.
  - Nguy cơ lộ lọt thông tin bảo mật khi đẩy mã nguồn lên kho lưu trữ (GitHub, GitLab,...).
* **Giải pháp:** Áp dụng nguyên lý **Externalized Configuration** (tách biệt mã nguồn và cấu hình môi trường), lưu cấu hình trong các tập tin cấu hình riêng biệt (`.properties`, `.yml`).

---

## 2. Tìm Hiểu Annotation `@PropertySource`

`@PropertySource` cung cấp cơ chế khai báo tường minh để nạp (load) một tập tin `.properties` vào Spring `Environment`.

### 2.1. Cú pháp cơ bản
```java
@Configuration
@PropertySource("classpath:config.properties")
public class PropertySourceDemo {
    // ...
}
```

### 2.2. Các tính năng nâng cao của `@PropertySource`

#### a. Nạp nhiều tập tin cấu hình
Có thể dùng `@PropertySources` hoặc khai báo mảng chuỗi trực tiếp:
```java
@Configuration
@PropertySources({
    @PropertySource("classpath:config.properties"),
    @PropertySource("classpath:db.properties")
})
public class AppConfig { }
```
Hoặc:
```java
@PropertySource({"classpath:config.properties", "classpath:db.properties"})
```

#### b. Bỏ qua nếu không tìm thấy file (`ignoreResourceNotFound`)
Hữu ích khi cấu hình file override tùy chọn (nếu có thì nạp, không có thì bỏ qua không quăng ngoại lệ):
```java
@PropertySource(value = "classpath:optional-config.properties", ignoreResourceNotFound = true)
```

#### c. Dùng placeholder trong đường dẫn file
Spring cho phép dùng biến môi trường để trỏ linh hoạt tới file cấu hình:
```java
@PropertySource("classpath:config-${spring.profiles.active}.properties")
```

---

## 3. Hai Cách Lấy Giá Trị Cấu Hình Trong Spring

### 3.1. Dùng `@Value` (Khai báo trực tiếp trên trường)
* Cú pháp: `@Value("${tên_khóa:giá_trị_mặc_định}")`
* Ví dụ:
  ```java
  @Value("${jdbc.driver}")
  private String driver;

  @Value("${jdbc.port:3306}") // Có giá trị mặc định là 3306 nếu không tìm thấy key
  private int port;
  ```
* **Ưu điểm:** Ngắn gọn, tường minh, tự động ép kiểu dữ liệu cơ bản (String, int, boolean,...).

### 3.2. Dùng `Environment` (Truy xuất linh hoạt theo lập trình)
* `org.springframework.core.env.Environment` là Bean đại diện cho toàn bộ môi trường thực thi của ứng dụng.
* Ví dụ:
  ```java
  @Autowired
  private Environment env;

  public void someMethod() {
      String url = env.getProperty("jdbc.url");
      Integer port = env.getProperty("jdbc.port", Integer.class, 3306);
  }
  ```
* **Ưu điểm:** Cho phép kiểm tra sự tồn tại của key bằng `env.containsProperty("...")`, lấy giá trị động theo tên biến lúc runtime.

---

## 4. Vòng Đời Khởi Tạo & `InitializingBean`

Trong dự án:
```java
@Configuration
@PropertySource("classpath:config.properties")
public class PropertySourceDemo implements InitializingBean {

    @Value("${jdbc.driver}")
    private String driver;

    @Autowired
    private Environment env;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info(driver);
        setDatabaseConfig();
    }
}
```

> [!NOTE]
> **Tại sao không đọc `@Value` trong Constructor?**
> * Khi Constructor được gọi, Spring IoC Container mới chỉ vừa cấp phát vùng nhớ đối tượng. Các Dependency Injection (`@Autowired`) và gán giá trị `@Value` **chưa diễn ra** (giá trị lúc này là `null`).
> * `InitializingBean.afterPropertiesSet()` (hoặc `@PostConstruct`) được kích hoạt **ngay sau khi** tất cả các thuộc tính bean và dependencies đã được Spring tiêm hoàn tất. Đây là vị trí lý tưởng để đọc dữ liệu cấu hình hoặc khởi tạo kết nối ngoại vi.

---

## 5. Bảng Đối Chiếu: `@Value` vs `Environment`

| Tiêu chí | `@Value` | `Environment` |
| :--- | :--- | :--- |
| **Cách tiêm** | Annotation trực tiếp trên field / method parameter | Inject bean `Environment` qua `@Autowired` |
| **Hỗ trợ giá trị mặc định** | Có cú pháp `${key:defaultValue}` | Có overload `getProperty(key, defaultValue)` |
| **Thời điểm phân giải** | Lúc khởi tạo Bean (Bean Post Processor) | Bất cứ lúc nào trong runtime khi gọi method |
| **Khả năng kiểm tra key** | Báo lỗi lúc khởi động nếu thiếu key và không có default | Kiểm tra linh hoạt qua `containsProperty(key)` |
| **Trường hợp khuyên dùng** | Hầu hết các nhu cầu gán cấu hình vào field cố định | Các tình huống cần duyệt động cấu hình, profile động |
