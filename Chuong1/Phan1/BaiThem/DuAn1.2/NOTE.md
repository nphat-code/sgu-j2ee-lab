# 📘 Ghi Chú Kiến Thức - Dự Án 1.2: Tìm Hiểu Về Spring Scope Với Prototype

> **Thư mục bài làm:** `Chuong1/Phan1/BaiThem/DuAn1.2`  
> **Nội dung trọng tâm:** Cơ chế Bean Scope, khai báo `@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)`, phân tích nguyên nhân tại sao lần gọi thứ hai in ra `null`.

---

## 1. Khái Niệm Spring Bean Scope

Trong Spring, **Scope (Phạm vi)** quy định:
1. Số lượng thể hiện (instance) của một Bean được tạo ra.
2. Vòng đời (Lifecycle) tồn tại của Bean đó trong Spring IoC Container.
3. Cách thức mà Spring trả về Bean khi có yêu cầu tiêm phụ thuộc (`@Autowired`) hoặc gọi `context.getBean(...)`.

Spring hỗ trợ 6 phạm vi cốt lõi (2 phạm vi cơ bản cho Core Spring và 4 phạm vi cho Web-aware context):
* **Singleton** *(Mặc định)*
* **Prototype**
* **Request** *(Web)*
* **Session** *(Web)*
* **Application** *(Web)*
* **WebSocket** *(Web)*

---

## 2. Prototype Scope Là Gì?

* Khi một Bean được đánh dấu là **Prototype Scope**:
  - Mỗi khi ứng dụng gọi `context.getBean(...)` hoặc mỗi khi Bean đó được tiêm vào một lớp khác, **Spring IoC Container sẽ luôn khởi tạo một instance hoàn toàn mới**.
  - Spring **không lưu giữ** instance Prototype trong Singleton Cache (Registry).
  - Spring chịu trách nhiệm khởi tạo, cấu hình và ráp nối phụ thuộc cho Prototype Bean, sau đó bàn giao trực tiếp cho client code mà không tiếp tục quản lý vòng đời tiêu hủy (`@PreDestroy` sẽ không được tự động gọi khi đóng container).

### Cú pháp khai báo:
```java
package net.javaguides.spring.scope;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TwitterMessageService implements MessageService {
    private String message;
    // getter và setter...
}
```

> [!TIP]
> Sử dụng hằng số `ConfigurableBeanFactory.SCOPE_PROTOTYPE` thay vì chuỗi `"prototype"` giúp tránh lỗi gõ sai chính tả (Type-safe).

---

## 3. Phân Tích Mã Nguồn Và Kết Quả Thực Thi

Trong lớp `Application`:
```java
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

// Lần gọi 1
MessageService messageService = context.getBean(MessageService.class);
messageService.setMessage("TwitterMessageService Implementation");
System.out.println(messageService.getMessage());

// Lần gọi 2
MessageService messageService1 = context.getBean(MessageService.class);
System.out.println(messageService1.getMessage());

context.close();
```

### Kết quả console xuất ra:
```text
TwitterMessageService Implementation
null
```

### 🔍 Tại sao lần 2 lại in ra `null`?

1. **Lần 1 (`messageService`):**
   - Spring tạo mới đối tượng `TwitterMessageService` (giả sử tại ô nhớ `@001`).
   - Lệnh `messageService.setMessage("TwitterMessageService Implementation")` gán chuỗi vào biến `message` của instance `@001`.
   - In ra console: `TwitterMessageService Implementation`.

2. **Lần 2 (`messageService1`):**
   - Vì lớp có `@Scope(SCOPE_PROTOTYPE)`, Spring **không lấy lại** đối tượng tại ô nhớ `@001`.
   - Spring gọi constructor tạo ra **một đối tượng hoàn toàn mới** (tại ô nhớ `@002`).
   - Biến `message` trong đối tượng `@002` lúc này là một thuộc tính mới khởi tạo, **chưa được gán giá trị**, nên có giá trị mặc định là `null`.
   - Lệnh `messageService1.getMessage()` do đó trả về `null`.

> [!NOTE]
> Nếu lớp `TwitterMessageService` là **Singleton** (mặc định), lần 2 sẽ dùng lại chính đối tượng `@001` và kết quả in ra sẽ là:
> ```text
> TwitterMessageService Implementation
> TwitterMessageService Implementation
> ```

---

## 4. So Sánh Chi Tiết: Singleton vs Prototype

| Tiêu chí | Singleton Scope | Prototype Scope |
| :--- | :--- | :--- |
| **Số lượng thể hiện** | Duy nhất **1 instance** cho toàn bộ Spring Container. | **Nhiều instance** (mỗi lần yêu cầu tạo một instance mới). |
| **Lưu trữ Cache** | Lưu vĩnh viễn trong **Singleton Cache**. | **Không lưu** trong cache sau khi tạo xong. |
| **Trạng thái (State)** | Phù hợp cho đối tượng **Stateless** (không lưu trạng thái riêng). | Phù hợp cho đối tượng **Stateful** (lưu trạng thái riêng cho từng tác vụ). |
| **Vòng đời (Lifecycle)** | Spring quản lý trọn vẹn từ lúc tạo (`@PostConstruct`) đến lúc hủy (`@PreDestroy`). | Spring chỉ quản lý khâu tạo, client tự chịu trách nhiệm quản lý/hủy đối tượng. |
| **Ví dụ áp dụng** | Service, Repository, Controller, Utility, Config. | User Session, Shopping Cart, Upload Task, Form DTO tạm thời. |
