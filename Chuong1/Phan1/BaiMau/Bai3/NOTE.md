# 📘 Ghi Chú Kiến Thức - Bài 3: Tổ Chức Dự Án Spring & Tìm Hiểu Về Bean Scope

> **Thư mục bài làm:** `Chuong1/Phan1/BaiMau/Bai3`  
> **Nội dung trọng tâm:** Phân chia kiến trúc dự án Spring, quản lý vòng đời và phạm vi Bean (Bean Scopes: Singleton vs Prototype), giải quyết đa hình phụ thuộc với `@Primary`.

---

## 1. Cấu Trúc Tổ Chức Dự Án Chuẩn Theo Giáo Trình

Bài tập phân chia các lớp theo các package chức năng rõ ràng:

```text
com.example/
├── interfaces/         # Khai báo các interface trừu tượng (Contract)
│   ├── Speakers.java
│   └── Tyres.java
├── implementation/     # Cài đặt chi tiết cho các interface
│   ├── BoseSpeakers.java
│   ├── SonySpeakers.java       (@Primary)
│   ├── BridgeStoneTyres.java
│   └── MichelinTyres.java      (@Primary)
├── beans/              # Các Bean nghiệp vụ chính
│   ├── Person.java
│   └── Vehicle.java
├── services/           # Lớp dịch vụ thực thi tính năng
│   └── VehicleServices.java    (@Scope: PROTOTYPE)
├── config/             # Cấu hình Spring Context
│   └── ProjectConfig.java
└── main/               # Lớp thực thi chạy ứng dụng
    └── Example16.java
```

---

## 2. Tìm Hiểu Về Bean Scope (Phạm Vi Của Bean)

Trong Spring Framework, **Scope** định nghĩa vòng đời và cách Spring Container tạo mới hay tái sử dụng các instance của Bean.

| Bean Scope | Cú pháp khai báo | Hành vi tạo đối tượng | Trường hợp áp dụng |
| :--- | :--- | :--- | :--- |
| **Singleton** *(Mặc định)* | Không cần ghi hoặc `@Scope("singleton")` / `@Scope(BeanDefinition.SCOPE_SINGLETON)` | Spring chỉ tạo **duy nhất 1 instance** trong suốt vòng đời của ApplicationContext. Mọi nơi gọi `getBean()` hoặc inject đều dùng chung 1 đối tượng này (cùng `hashCode`). | Các Bean không lưu trạng thái (Stateless): Service, Repository, Controller, Utility. |
| **Prototype** | `@Scope("prototype")` hoặc `@Scope(BeanDefinition.SCOPE_PROTOTYPE)` | Mỗi lần gọi `getBean()` hoặc mỗi lần được inject, Spring sẽ **tạo mới một instance hoàn toàn khác** (khác `hashCode`). | Các Bean có lưu trạng thái riêng biệt cho từng người dùng/tác vụ (Stateful object). |

### Phân tích trong Bài 3:
* Lớp `VehicleServices` được đánh dấu:
  ```java
  @Component
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public class VehicleServices { ... }
  ```
* Trong phương thức `main` của `Example16`:
  ```java
  VehicleServices vehicleServices1 = context.getBean(VehicleServices.class);
  VehicleServices vehicleServices2 = context.getBean("vehicleServices", VehicleServices.class);
  
  // Kết quả: vehicleServices1 != vehicleServices2 (khác hashCode)
  ```
  ➔ Spring xác nhận đây là **Prototype Scoped Bean**.

### 2.1. Chuyên sâu: Tại sao khi lấy 2 Bean Singleton thì 2 đối tượng lại giống hệt nhau?
Khi một Bean là **Singleton** (mặc định trong Spring), cơ chế hoạt động của Spring IoC Container như sau:

1. **Bộ nhớ đệm Singleton (Singleton Cache / Registry):**
   - Spring Container duy trì một bảng băm nội bộ (thực chất là một `Map<String, Object>`) để lưu trữ tất cả các instance Singleton.
2. **Quy trình khi gọi `context.getBean(...)`:**
   - **Lần gọi đầu tiên (hoặc lúc khởi động Container):** Spring kiểm tra Cache, thấy chưa có bean ➔ Spring gọi Constructor (`new VehicleServices()`) ➔ in ra console dòng `"VehicleServices object is created"` ➔ đưa đối tượng vừa tạo vào Cache ➔ trả về địa chỉ ô nhớ gán cho biến thứ nhất.
   - **Lần gọi thứ hai:** Spring kiểm tra Cache ➔ phát hiện đối tượng **đã tồn tại** ➔ Spring **không tạo mới** (không gọi lại constructor) ➔ trả về ngay chính địa chỉ ô nhớ của đối tượng đã tạo trước đó gán cho biến thứ hai.
3. **Bản chất của phép so sánh `==` và `hashCode()`:**
   - Trong Java, phép so sánh `==` giữa 2 biến tham chiếu là **so sánh địa chỉ vùng nhớ trên RAM (Heap Memory)**.
   - Do cả `vehicleServices1` và `vehicleServices2` cùng trỏ vào **duy nhất 1 địa chỉ ô nhớ** mà Spring quản lý trong Cache, nên:
     - `hashCode()` của chúng trùng khớp 100%.
     - `vehicleServices1 == vehicleServices2` trả về `true`.
4. **Mục đích thiết kế Singleton của Spring:**
   - **Tối ưu hiệu năng & RAM:** Các lớp Service/Repository/Controller thường là **Stateless** (chỉ chứa logic xử lý, không lưu trạng thái riêng biệt của từng người dùng). Tái sử dụng 1 instance duy nhất giúp tiết kiệm bộ nhớ và giảm tải áp lực dọn rác cho Garbage Collector (GC).

---

## 3. Xử Lý Xung Đột Nhiều Bean Triển Khai Cùng Interface Với `@Primary`

### Vấn đề:
Khi một interface có nhiều class cùng cài đặt và đều đánh dấu `@Component`:
* `Speakers` có 2 class con: `BoseSpeakers` và `SonySpeakers`.
* Khi một class khác (như `VehicleServices`) yêu cầu `@Autowired private Speakers speakers;`, Spring sẽ không biết phải tiêm class nào và ném ngoại lệ:
  `NoUniqueBeanDefinitionException: expected single matching bean but found 2: boseSpeakers, sonySpeakers`.

### Giải pháp với `@Primary`:
* Đánh dấu `@Primary` lên lớp ưu tiên:
  ```java
  @Component
  @Primary
  public class SonySpeakers implements Speakers { ... }
  ```
* Spring sẽ tự động chọn `SonySpeakers` làm lựa chọn mặc định khi tiêm vào `VehicleServices` mà không bị xung đột.

---

## 4. Cấu Hình Quét Component Với `@ComponentScan`

Trong `ProjectConfig`:
```java
@Configuration
@ComponentScan(basePackages = {"com.example.implementation", "com.example.services"})
@ComponentScan(basePackageClasses = {com.example.beans.Vehicle.class, com.example.beans.Person.class})
public class ProjectConfig {
}
```

* **`basePackages`:** Quét toàn bộ các lớp có `@Component` trong package chỉ định theo chuỗi String.
* **`basePackageClasses`:** Quét theo package chứa các class chỉ định (Type-safe, tránh gõ nhầm tên package).
