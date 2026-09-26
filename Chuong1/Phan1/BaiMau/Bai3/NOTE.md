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
