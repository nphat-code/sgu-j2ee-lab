# 📘 Ghi Chú Kiến Thức - Dự Án 1.1: Spring IoC Container & Cấu Hình Bean Bằng Java

> **Thư mục bài làm:** `Chuong1/Phan1/BaiThem/DuAn1.1`  
> **Nội dung trọng tâm:** Cơ chế Spring IoC Container, cấu hình Bean hoàn toàn bằng Java (`@Configuration`, `@Bean`), quy trình khởi tạo `ApplicationContext` và truy vấn Bean.

---

## 1. Spring IoC Container Là Gì?

**Inversion of Control (IoC - Đảo ngược điều khiển)** là nguyên lý cốt lõi của Spring Framework. 
* Thay vì lập trình viên chủ động dùng từ khóa `new` để tạo và quản lý các đối tượng phụ thuộc (phụ thuộc chặt chẽ - Tightly Coupled), quyền điều khiển này được chuyển giao hoàn toàn cho **Spring IoC Container**.
* **Nhiệm vụ của Spring IoC Container:**
  1. Đọc và phân tích thông tin cấu hình (Metadata).
  2. Khởi tạo đối tượng (Bean).
  3. Quản lý vòng đời (Lifecycle) và cấu hình các thuộc tính.
  4. Ráp nối (Wire/Inject) các phụ thuộc giữa các Bean lại với nhau.

```text
[Java Classes] + [Configuration Metadata (@Configuration)]
                     │
                     ▼
           [Spring IoC Container]
                     │
                     ▼
      [Fully Configured System (Spring Beans)]
```

---

## 2. Hai Dạng Container Cốt Lõi: `BeanFactory` vs `ApplicationContext`

Trong Spring có hai tầng giao diện đại diện cho IoC Container:

| Tiêu chí | `BeanFactory` (`org.springframework.beans`) | `ApplicationContext` (`org.springframework.context`) |
| :--- | :--- | :--- |
| **Bản chất** | Container mức cơ sở (cấp thấp nhất). | Container cấp cao, kế thừa toàn bộ từ `BeanFactory`. |
| **Cơ chế nạp Bean** | **Lazy-loading:** Chỉ khởi tạo Bean khi có lời gọi `getBean()`. | **Eager-loading:** Mặc định khởi tạo toàn bộ các Singleton Bean ngay khi ứng dụng khởi động. |
| **Tính năng mở rộng** | Rất hạn chế, chỉ đủ để DI cơ bản. | Hỗ trợ đầy đủ: i18n (MessageSource), xử lý sự kiện (Event publication), tích hợp AOP, Web Application. |
| **Ứng dụng thực tế** | Dùng cho các thiết bị nhúng cực kỳ hạn chế bộ nhớ. | **Tiêu chuẩn mặc định** cho hầu hết các ứng dụng Spring / Spring Boot hiện đại. |

---

## 3. Cấu Hình Bean Bằng Java (Java-based Configuration)

Thay vì dùng file XML cổ điển (`beans.xml`), từ Spring 3.0+ lập trình viên chuyển sang dùng **Java Configuration**:

### 3.1. `@Configuration`
* Đánh dấu một class là lớp cấu hình Spring (nguồn metadata).
* Cho phép Spring nhận diện các phương thức bên trong có gắn `@Bean`.

### 3.2. `@Bean`
* Đặt trên các phương thức của lớp `@Configuration`.
* **Cơ chế hoạt động:** Giá trị trả về của phương thức này sẽ được Spring Container quản lý như một Spring Bean.
* **Tên Bean mặc định:** Chính là tên của phương thức (ví dụ: `greetingService`).

```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.service.GreetingService;

@Configuration
public class AppConf {

    @Bean
    public GreetingService greetingService() {
        GreetingService service = new GreetingService();
        service.setMessage("Hello from Spring IoC Container with Java-based Configuration!");
        return service;
    }
}
```

---

## 4. Lớp Dịch Vụ Nghiệp Vụ `GreetingService` (POJO)

```java
package com.example.service;

public class GreetingService {
    private String message;

    public GreetingService() {}

    public GreetingService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void sayGreeting() {
        System.out.println("Greeting message: " + message);
    }
}
```

* `GreetingService` hoàn toàn là một **Plain Old Java Object (POJO)** sạch, không hề bị gắn các annotation của Spring.
* Điều này giúp lớp nghiệp vụ giữ được tính độc lập cao, dễ dàng tái sử dụng hoặc kiểm thử đơn vị độc lập.

---

## 5. Khởi Tạo Container & Truy Vấn Bean Trong `SpringApplication`

```java
package com.example.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.example.config.AppConf;
import com.example.service.GreetingService;

public class SpringApplication {
    public static void main(String[] args) {
        // 1. Khởi tạo IoC Container từ lớp cấu hình Java
        var context = new AnnotationConfigApplicationContext(AppConf.class);

        // 2. Truy vấn Bean theo kiểu dữ liệu (Type-safe)
        GreetingService greetingService = context.getBean(GreetingService.class);

        // 3. Sử dụng Bean
        greetingService.sayGreeting();

        // 4. Giải phóng tài nguyên
        context.close();
    }
}
```

### Quy trình 4 bước diễn ra:
1. `AnnotationConfigApplicationContext(AppConf.class)` đọc lớp `AppConf`.
2. Spring thực thi phương thức `greetingService()`, nhận về đối tượng `GreetingService` và lưu vào Singleton Cache.
3. Khi gọi `context.getBean(GreetingService.class)`, Spring trích xuất instance đó ra từ Cache.
4. Lời gọi `greetingService.sayGreeting()` in thông điệp ra console.
5. `context.close()` giải phóng toàn bộ tài nguyên của container trước khi thoát chương trình.

---

## 6. Chuyên Sâu: File Cấu Hình `AppConf` Chạy Khi Nào?

Lớp cấu hình `AppConf` được Spring IoC Container nạp và thực thi **ngay tại thời điểm khởi tạo Container**, cụ thể là khi dòng lệnh sau chạy:

```java
var context = new AnnotationConfigApplicationContext(AppConf.class);
```

### Chi tiết các giai đoạn diễn ra bên dưới:
1. **Đăng ký Metadata (Configuration Registration):**
   - `AnnotationConfigApplicationContext` nhận tham số `AppConf.class` và đăng ký nó như một Bean cấu hình nguồn.
2. **Tạo CGLIB Proxy:**
   - Spring nhận thấy annotation `@Configuration`. Nó dùng thư viện CGLIB để tạo ra một lớp con proxy bọc quanh `AppConf`.
   - Mục đích: Đảm bảo mọi lời gọi đến phương thức `@Bean` đều trả về cùng một thể hiện duy nhất (Singleton Semantics).
3. **Thực thi các phương thức `@Bean` (Bean Instantiation):**
   - Ngay trong quá trình khởi động Container (Eager initialization), Spring tự động gọi phương thức `greetingService()` trong `AppConf`.
   - Lệnh `new GreetingService()` và `service.setMessage(...)` được thực thi tại đây.
   - Đối tượng vừa tạo được đưa ngay vào bộ nhớ đệm **Singleton Cache (Registry)** của Spring.
4. **Khi gọi `context.getBean(GreetingService.class)`:**
   - Lúc này Spring **KHÔNG** chạy lại phương thức trong `AppConf` nữa, mà chỉ lấy đối tượng đã được khởi tạo sẵn ở bước 3 trong Cache ra sử dụng.
