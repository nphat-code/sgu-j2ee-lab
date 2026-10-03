# 📘 Ghi Chú Kiến Thức - Bài 2: Xây Dựng Ứng Dụng Website Sử Dụng Spring Boot và Thymeleaf

> **Thư mục bài làm:** `Chuong1/Phan2/BaiMau/Bai2`  
> **Nội dung trọng tâm:** Mô hình Spring MVC (Model - View - Controller), Template Engine Thymeleaf, Java Record, xử lý file dữ liệu CSV với Stream API và cơ chế Đa ngôn ngữ (i18n).

---

## 1. Cấu Trúc Tổng Thể Của Dự Án

```text
Chuong1/Phan2/BaiMau/Bai2/demo/
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java        # Điểm khởi chạy ứng dụng Spring Boot
│   ├── ServletInitializer.java     # Cấu hình khởi tạo triển khai WAR
│   ├── model/
│   │   └── Book.java               # Java Record đại diện cho thực thể sách
│   ├── repository/
│   │   └── BookGenerator.java      # Tiện ích đọc và nạp dữ liệu từ books.csv
│   ├── service/
│   │   ├── BookService.java        # Interface nghiệp vụ sách
│   │   └── InMemoryBookService.java# Cài đặt lưu trữ sách trong bộ nhớ (ConcurrentHashMap)
│   └── controller/
│       └── BookController.java     # Spring MVC Controller điều hướng trang web
└── src/main/resources/
    ├── books.csv                   # File dữ liệu sách mẫu (50 đầu sách)
    ├── messages.properties         # File cấu hình đa ngôn ngữ mặc định (Tiếng Anh)
    ├── messages_nl.properties      # File cấu hình tiếng Hà Lan
    └── templates/                  # Thư mục chứa các giao diện Thymeleaf
        ├── index.html              # Trang chủ lựa chọn ngôn ngữ và liên kết
        ├── books/
        │   ├── list.html           # Trang hiển thị danh sách sách (dạng bảng)
        │   └── details.html        # Trang xem thông tin chi tiết một cuốn sách
        └── error.html              # Trang hiển thị thông báo lỗi hệ thống
```

---

## 2. Java Record (`Book.java`)

Từ **Java 14/16**, `record` được giới thiệu nhằm tạo ra các lớp mang dữ liệu bất biến (Immutable Data Carrier) một cách ngắn gọn:

```java
public record Book(String isbn, String title, List<String> authors) {
    @Override
    public boolean equals(Object o) {
        if (o instanceof Book other) {
            return this.isbn != null && Objects.equals(this.isbn, other.isbn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isbn);
    }
}
```

### Đặc tính quan trọng:
* **Tự động sinh mã:** Trình biên dịch tự động sinh constructor đầy đủ tham số, các hàm truy cập thuộc tính (như `book.isbn()`, `book.title()`, `book.authors()`), `toString()`, `equals()` và `hashCode()`.
* **Không có tiền tố `get`:** Thuộc tính được truy cập trực tiếp qua tên hàm (ví dụ: `book.isbn()` thay vì `book.getIsbn()`). Tuy nhiên trong Thymeleaf expression `${book.isbn}`, Thymeleaf vẫn tự động hiểu và gọi phương thức tương ứng.
* **Bất biến (Immutability):** Các trường trong record mặc định là `final`, đảm bảo an toàn đa luồng (Thread-safe).

---

## 3. Đọc File CSV Trong Classpath Với Java Stream API

Trong `BookGenerator.java`:
```java
var books = new ClassPathResource("books.csv").getInputStream();
try (var lines = new BufferedReader(new InputStreamReader(books)).lines()) {
    return lines
        .skip(1)                                    // Bỏ qua dòng tiêu đề
        .map(BookGenerator::parseLine)              // Xử lý từng dòng dữ liệu
        .filter(Objects::nonNull)
        .toList();
}
```

### Xử lý định dạng CSV linh hoạt (Dấu phẩy `,` và Dấu gạch đứng `|`):
Trong thực tế, file `books.csv` có thể phân tách bởi:
1. **Dấu gạch đứng (`|`):** Cần escape bằng `line.split("\\|")` vì `|` là ký tự đặc biệt trong Regex.
2. **Dấu phẩy (`,`) tiêu chuẩn:** Một cuốn sách có thể có nhiều tác giả (ví dụ: `"Fiona Garcia, Michael Johnson"`), phần tên tác giả được đặt trong dấu ngoặc kép `""`.
   - Cần sử dụng Regex Lookahead để tránh tách nhầm dấu phẩy bên trong dấu nháy kép:
     ```java
     line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 3);
     ```
   - Sau đó loại bỏ dấu nháy kép và `trim()` khoảng trắng trước khi gom vào danh sách tác giả.

* **`ClassPathResource("books.csv")`:** Tìm và tải tài nguyên nằm trong thư mục `src/main/resources` mà không cần quan tâm đến đường dẫn tuyệt đối trên đĩa cứng.


---

## 4. Mô Hình Spring MVC & `@Controller`

Khác với kiến trúc REST API trả về JSON bằng `@RestController`, kiến trúc Web truyền thống sử dụng **`@Controller`**:

```java
@Controller
public class BookController {
    private final BookService bookService;

    @GetMapping("/books.html")
    public String all(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list"; // Trả về đường dẫn file templates/books/list.html
    }

    @GetMapping(value = "/books.html", params = "isbn")
    public String get(@RequestParam("isbn") String isbn, Model model) {
        bookService.find(isbn)
            .ifPresent(book -> model.addAttribute("book", book));
        return "books/details"; // Trả về templates/books/details.html
    }
}
```

* **Vai trò của `Model`:** Là đối tượng trung gian dùng để truyền dữ liệu từ Controller sang View. Khi gọi `model.addAttribute("books", ...)`, giao diện HTML có thể truy xuất dữ liệu thông qua biến `${books}`.
* **Tên view trả về:** Chuỗi String trả về (ví dụ `"books/list"`) sẽ được Spring Boot ViewResolver tự động ghép với tiền tố `classpath:/templates/` và hậu tố `.html` để render ra file `templates/books/list.html`.
* **Phân biệt request qua tham số (`params = "isbn"`):** Spring MVC cho phép map cùng một URL (`/books.html`) nhưng xử lý các logic khác nhau tùy thuộc vào sự hiện diện của tham số URL (Query Parameter).

---

## 5. Cẩm Nang Cú Pháp Thymeleaf Cơ Bản

Thymeleaf là một Server-Side Java Template Engine cho phép hiển thị dữ liệu trực tiếp vào HTML mà vẫn giữ được tính toàn vẹn của mã giao diện (Natural Templating).

| Cú pháp Thymeleaf | Ý nghĩa | Ví dụ trong bài |
| :--- | :--- | :--- |
| **`th:text="${...}"`** | Đổ dữ liệu từ Model vào nội dung thẻ HTML, tự động mã hóa ký tự đặc biệt (XSS protection). | `<td th:text="${book.title}">Title</td>` |
| **`th:each="item : ${list}"`** | Lặp qua tập hợp để render lặp lại thẻ HTML. | `<tr th:each="book : ${books}">` |
| **`th:if="${condition}"`** | Hiển thị thẻ HTML nếu điều kiện là `true`. | `<div th:if="${book != null}">` |
| **`th:href="@{...}"`** | Tạo liên kết URL an toàn theo ngữ cảnh của ứng dụng, hỗ trợ truyền query params linh hoạt. | `<a th:href="@{/books.html(isbn=${book.isbn})}">` |
| **`th:text="#{...}"`** | Lấy chuỗi ký tự theo mã từ file tài nguyên đa ngôn ngữ (`messages.properties`). | `<h1 th:text="#{books.list.title}"></h1>` |

---

## 6. Hướng Dẫn Chạy & Kiểm Tra Ứng Dụng

1. **Khởi động ứng dụng:**
   - Chạy class [DemoApplication.java](file:///c:/Study/HK1Nam3/J2EE/Lab/Chuong1/Phan2/BaiMau/Bai2/demo/src/main/java/com/example/demo/DemoApplication.java) hoặc mở terminal tại thư mục bài làm:
   ```bash
   ./mvnw spring-boot:run
   ```
2. **Kiểm tra trên trình duyệt:**
   - **Trang chủ:** Truy cập `http://localhost:8080/`
   - **Danh sách sách:** Truy cập `http://localhost:8080/books.html`
   - **Xem chi tiết sách:** Click vào mã ISBN bất kỳ hoặc gõ `http://localhost:8080/books.html?isbn=9780451524935`
   - **Đổi ngôn ngữ (i18n):** Click `NL` hoặc gõ `http://localhost:8080/?locale=nl` để xem giao diện tiếng Hà Lan.

---

## 7. Cơ Chế Chuyển Đổi Ngôn Ngữ Động Với `LocaleChangeInterceptor`

Mặc định, Spring Boot sử dụng `AcceptHeaderLocaleResolver` (chỉ đọc ngôn ngữ từ cài đặt trình duyệt thông qua header `Accept-Language` và bỏ qua tham số `?locale=...`).

Để các liên kết như `?locale=nl` hoặc `?locale=en` trên giao diện có tác dụng thay đổi ngôn ngữ ngay lập tức:
* Cần đăng ký `LocaleResolver` (như `SessionLocaleResolver`) để lưu ngôn ngữ người dùng vào Session.
* Đăng ký `LocaleChangeInterceptor` vào `WebMvcConfigurer` để lắng nghe tham số `locale` trên URL:
  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Bean
      public LocaleResolver localeResolver() {
          SessionLocaleResolver slr = new SessionLocaleResolver();
          slr.setDefaultLocale(Locale.ENGLISH);
          return slr;
      }

      @Bean
      public LocaleChangeInterceptor localeChangeInterceptor() {
          LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
          lci.setParamName("locale");
          return lci;
      }

      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(localeChangeInterceptor());
      }
  }
  ```

---

## 8. Xử Lý Sự Cố Thường Gặp Khi Khởi Chạy (Troubleshooting)

### 1. IDE chạy nhầm class `main()` của bài khác (ví dụ: `CalculatorApplication`)
* **Nguyên nhân gốc rễ (Root Cause):**
  1. Trong file `pom.xml` gốc của workspace đa module, tất cả các bài tập con (`c1-p1-b1`, `c1-p1-b2`, `c1-p1-b3`, `c1-p2-b2`) ban đầu đều mang mặc định `<artifactId>demo</artifactId>`.
  2. Điều này dẫn tới lỗi Maven `DuplicateProjectException`. Bộ Extension Java (Red Hat Java / Eclipse JDT LS) trong IDE không thể nạp các module bị trùng tên, mà chỉ nạp duy nhất project `demo` đầu tiên (`CalculatorApplication`).
  3. Khi bạn mở file `DemoApplication.java` ở Phần 2 Bài 2 và bấm nút Run, IDE không tìm thấy project Java của file này nên tự động chuyển hướng chạy project `demo` duy nhất mà nó ghi nhớ.
* **Giải pháp đã xử lý:**
  1. Đã phân biệt `artifactId` độc lập cho từng module: `c1-p1-b1-demo`, `c1-p1-b2-demo`, `c1-p1-b3-demo`, `c1-p2-b2-demo`.
  2. Đã tạo file cấu hình [`.vscode/launch.json`](file:///c:/Study/HK1Nam3/J2EE/Lab/.vscode/launch.json) liệt kê cụ thể từng bài.
* **Cách chạy chuẩn xác nhất từ IDE:**
  - Nhìn sang thanh bên trái, chọn tab **Run and Debug** (hoặc nhấn `Ctrl + Shift + D`).
  - Ở ô chọn cấu hình trên cùng, chọn: **"Phần 2 - Bài 2: Web Sách (DemoApplication)"** hoặc **"Chạy file đang mở (Current File)"**.
  - Bấm nút Play ▶️ màu xanh lá cây bên cạnh để chạy.
  - Hoặc mở terminal gõ trực tiếp:
    ```powershell
    cd 'c:\Study\HK1Nam3\J2EE\Lab\Chuong1\Phan2\BaiMau\Bai2\demo'
    .\mvnw spring-boot:run
    ```

### 2. Lỗi cổng 8080 bị chiếm dụng (`Port 8080 was already in use`)
* **Nguyên nhân:** Tiến trình chạy trước đó chưa được dừng hẳn (vẫn đang chạy ngầm trên cổng `8080`).
* **Cách khắc phục:**
  * Bấm nút **Stop ⏹️** (màu đỏ) trên thanh công cụ Debug của IDE.
  * Hoặc trong Terminal PowerShell, tìm và tắt tiến trình chiếm cổng:
    ```powershell
    netstat -ano | findstr 8080
    # Lấy PID ở cột cuối cùng rồi tắt tiến trình:
    Stop-Process -Id <PID> -Force
    ```

