# 📘 Ghi Chú Kiến Thức - Dự Án 1.3: Tìm Hiểu Về Spring Scope Với Singleton

> **Thư mục bài làm:** `Chuong1/Phan1/BaiThem/DuAn1.3`  
> **Nội dung trọng tâm:** Cơ chế Singleton Scope, khai báo `@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)`, phân tích hành vi chia sẻ trạng thái (Shared State) giữa các lần gọi Bean.

---

## 1. Khái Niệm Singleton Scope Trong Spring

* **Singleton** là phạm vi mặc định của mọi Spring Bean nếu không khai báo phạm vi nào khác.
* Khi một Bean là **Singleton**:
  - Spring IoC Container chỉ tạo **duy nhất một thể hiện (single instance)** của class đó trong toàn bộ vòng đời của `ApplicationContext`.
  - Mọi lời gọi `context.getBean(...)` hoặc các điểm tiêm phụ thuộc (`@Autowired`) tại các class khác nhau đều được Spring trả về **chính xác cùng một tham chiếu đối tượng** (cùng địa chỉ vùng nhớ Heap và cùng `hashCode`).

### Cú pháp khai báo:
```java
package net.javaguides.spring.scope;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TwitterMessageService implements MessageService {
    private String message;
    // getter và setter...
}
```

> [!NOTE]
> Mặc dù `@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)` là tùy chọn (vì Spring mặc định là Singleton), việc khai báo tường minh giúp mã nguồn thể hiện rõ ràng chủ đích thiết kế của lập trình viên.

---

## 2. Phân Tích Thực Nghiệm: Đối Chiếu Dự Án 1.2 (Prototype) vs Dự Án 1.3 (Singleton)

Trong hàm `main` của `Application`:
```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

// Lấy Bean lần 1 và đặt giá trị
MessageService messageService = context.getBean(MessageService.class);
messageService.setMessage("TwitterMessageService Implementation");
System.out.println(messageService.getMessage());

// Lấy Bean lần 2 và in giá trị
MessageService messageService1 = context.getBean(MessageService.class);
System.out.println(messageService1.getMessage());

context.close();
```

### So sánh kết quả in ra màn hình:

| Dự án | Khai báo Scope | Kết quả in ra lần 1 | Kết quả in ra lần 2 | Bản chất vùng nhớ |
| :--- | :--- | :--- | :--- | :--- |
| **Dự án 1.2** | `SCOPE_PROTOTYPE` | `TwitterMessageService Implementation` | **`null`** | Tạo 2 đối tượng tách biệt (`@001` và `@002`). Biến `message` ở `@002` chưa được gán. |
| **Dự án 1.3** | `SCOPE_SINGLETON` | `TwitterMessageService Implementation` | **`TwitterMessageService Implementation`** | Dùng chung 1 đối tượng `@001`. Thay đổi ở lần 1 được lưu lại và phản ánh sang lần 2. |

---

## 3. Bản Chất Của Việc Chia Sẻ Trạng Thái (Shared State) & Lưu Ý Về Thread-Safety

### 3.1. Hiện tượng Chia Sẻ Trạng Thái (Shared State)
* Ở dòng code:
  ```java
  messageService.setMessage("TwitterMessageService Implementation");
  ```
  Bạn đang làm biến đổi trường dữ liệu nội tại (`private String message`) của đối tượng Singleton nằm trong RAM.
* Khi biến `messageService1` được lấy ra, vì nó trỏ cùng vào đối tượng này, nên gọi `messageService1.getMessage()` sẽ nhận được ngay giá trị mà `messageService` vừa gán.

### 3.2. Cảnh báo an toàn đa luồng (Thread-Safety)
> [!WARNING]
> Trong các ứng dụng Web thực tế (Spring MVC / REST API), nhiều HTTP Request được xử lý song song bởi nhiều luồng (Threads) khác nhau:
> - Nếu một Singleton Bean là **Stateful** (chứa các biến dữ liệu thay đổi như `message` ở trên), các request đồng thời có thể ghi đè dữ liệu của nhau, gây ra lỗi **Race Condition**.
> - Do đó, quy tắc vàng trong Spring là: **Singleton Bean nên luôn là Stateless (phi trạng thái)** — chỉ chứa các phương thức nghiệp vụ, không lưu biến trạng thái dùng chung có thể thay đổi.
