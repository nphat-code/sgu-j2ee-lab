# 📘 Ghi Chú Kiến Thức - Dự Án 1.5: Dependency Injection Với @Autowired Và @Qualifier

> **Thư mục bài làm:** `Chuong1/Phan1/BaiThem/DuAn1.5`  
> **Nội dung trọng tâm:** Cơ chế Dependency Injection (DI) trong Spring, 3 hình thức tiêm phụ thuộc (Constructor, Setter, Field), xử lý xung đột nhiều Bean với ngoại lệ `NoUniqueBeanDefinitionException`, và kỹ thuật chỉ định Bean bằng `@Qualifier` và `@Primary`.

---

## 1. Tổng Quan Về Dependency Injection & `@Autowired`

* **Dependency Injection (DI):** Là một mẫu thiết kế triển khai nguyên lý Inversion of Control (IoC). Thay vì một đối tượng tự khởi tạo các đối tượng mà nó phụ thuộc bằng từ khóa `new`, Spring IoC Container sẽ chịu trách nhiệm tạo ra đối tượng và "tiêm" (bơm) các phụ thuộc đó vào đối tượng cần nó.
* **`@Autowired`:** Annotation của Spring dùng để kích hoạt tính năng tự động liên kết (autowiring) các bean phụ thuộc vào điểm tiêm chỉ định.

---

## 2. Ba Hình Thức Tiêm Phụ Thuộc (Dependency Injection)

### 2.1. Constructor Injection (Khuyên dùng hàng đầu 🌟)
Tiêm phụ thuộc thông qua hàm khởi tạo (constructor) của class:
```java
@Component
public class PizzaController {
    private final Pizza pizza;

    @Autowired
    public PizzaController(@Qualifier("vegPizza") Pizza pizza) {
        this.pizza = pizza;
    }
}
```
> [!TIP]
> **Tại sao Constructor Injection là Best Practice?**
> 1. **Bất biến (Immutability):** Cho phép khai báo biến phụ thuộc là `final`, ngăn chặn việc thay đổi tham chiếu sau khi đối tượng được tạo.
> 2. **Tránh `NullPointerException`:** Đối tượng không bao giờ tồn tại ở trạng thái "nửa vời" (chưa tiêm phụ thuộc).
> 3. **Dễ Unit Test:** Dễ dàng viết test độc lập (POJO test) bằng cách `new PizzaController(mockPizza)` mà không cần khởi động Spring context hay dùng Reflection.
> 4. **Tùy chọn `@Autowired`:** Từ Spring Framework 4.3 trở đi, nếu class chỉ có **duy nhất một constructor**, ta có thể bỏ qua annotation `@Autowired`, Spring vẫn tự động tiêm.

### 2.2. Setter Injection
Tiêm phụ thuộc thông qua các phương thức setter:
```java
@Component
public class PizzaController {
    private Pizza pizza;

    @Autowired
    @Qualifier("vegPizza")
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
}
```
* **Khi nào dùng?** Dùng cho các phụ thuộc tùy chọn (Optional Dependencies) hoặc khi phụ thuộc có thể thay đổi/cấu hình lại trong suốt vòng đời ứng dụng.

### 2.3. Field Injection (Tiêm trực tiếp vào thuộc tính)
Đặt `@Autowired` trực tiếp lên field của class:
```java
@Component
public class PizzaController {
    @Autowired
    @Qualifier("vegPizza")
    private Pizza pizza;
}
```
* **Nhược điểm:**
  - Vi phạm tính đóng gói (encapsulation), buộc phải dùng Reflection để can thiệp biến `private`.
  - Khó khăn khi viết Unit Test độc lập vì không có constructor hay setter để mock đối tượng.
  - Dễ dẫn đến phụ thuộc vòng (Circular Dependency) mà không bị phát hiện lúc biên dịch.

---

## 3. Vấn Đề Xung Đột Nhiều Bean & Ngoại Lệ `NoUniqueBeanDefinitionException`

### 3.1. Nguyên nhân
Khi bạn có một Interface:
```java
public interface Pizza {
    String getPizza();
}
```
Và có **nhiều hơn 1 class triển khai (implement)** cùng được đánh dấu là Spring Bean:
- `VegPizza` (`@Component`)
- `NonVegPizza` (`@Component`)

Nếu bạn tiêm theo kiểu phụ thuộc chung:
```java
@Autowired
public PizzaController(Pizza pizza) { // ❌ LỖI!
    this.pizza = pizza;
}
```
Spring IoC Container sẽ quét thấy 2 Bean thỏa mãn kiểu `Pizza` và không thể tự quyết định nên chọn Bean nào. Ứng dụng sẽ dừng khởi động và ném ra lỗi:
```text
org.springframework.beans.factory.NoUniqueBeanDefinitionException: 
No qualifying bean of type 'net.javaguides.springboot.service.Pizza' available: 
expected single matching bean but found 2: nonVegPizza, vegPizza
```

---

## 4. Giải Quyết Xung Đột: `@Qualifier` vs `@Primary`

Spring cung cấp hai annotation chính để giải quyết tính mơ hồ (ambiguity) khi có nhiều Bean cùng kiểu:

### 4.1. Annotation `@Qualifier`
* Dùng để **chỉ định chính xác tên bean** mà bạn muốn tiêm vào một điểm cụ thể.
* Tên bean mặc định là tên class viết thường chữ cái đầu (camelCase): `VegPizza` ➔ `"vegPizza"`.
* Ví dụ:
  ```java
  @Autowired
  public PizzaController(@Qualifier("vegPizza") Pizza pizza) {
      this.pizza = pizza;
  }
  ```

### 4.2. Annotation `@Primary`
* Đặt trực tiếp lên một class triển khai để biến class đó thành **lựa chọn mặc định ưu tiên**:
  ```java
  @Component
  @Primary
  public class VegPizza implements Pizza { ... }
  ```
* Nếu có nhiều bean cùng kiểu và không có `@Qualifier` chỉ định, Spring sẽ tự động chọn bean có `@Primary`.

### 4.3. Bảng so sánh `@Qualifier` vs `@Primary`

| Tiêu chí | `@Qualifier` | `@Primary` |
| :--- | :--- | :--- |
| **Vị trí đặt** | Tại điểm tiêm (Constructor param, Setter param, Field) | Tại class định nghĩa Bean (`@Component`) hoặc `@Bean` method |
| **Phạm vi hiệu lực** | Chỉ có tác dụng tại chính điểm tiêm đó (Cục bộ) | Tác dụng trên toàn bộ ứng dụng ở mọi điểm tiêm (Toàn cục) |
| **Mức độ ưu tiên** | **Cao hơn** (Nếu cả 2 cùng xuất hiện, `@Qualifier` sẽ ghi đè `@Primary`) | Thấp hơn `@Qualifier` |
| **Mục đích sử dụng** | Khi cần kiểm soát linh hoạt từng điểm tiêm nhận Bean nào | Khi muốn có 1 Bean mặc định dùng cho 90% trường hợp |

---

## 5. Tổng Kết: Bài 1.5 Làm Gì & Luồng Chạy Chi Tiết

### 5.1. Mục đích bài toán
Thực hành và hiểu rõ:
1. Cơ chế tiêm phụ thuộc tự động qua `@Autowired` theo chuẩn Spring Boot.
2. Xử lý trường hợp đa hình (polymorphism): một interface có nhiều class implement cùng tồn tại trong Spring Container.
3. Sử dụng `@Qualifier("vegPizza")` để Spring tiêm chính xác instance `VegPizza` vào `PizzaController`.

### 5.2. Luồng thực thi
1. Ứng dụng khởi động từ [`Application.java`](file:///c:/Study/HK1Nam3/J2EE/Lab/Chuong1/Phan1/BaiThem/DuAn1.5/demo/src/main/java/net/javaguides/springboot/Application.java).
2. Spring Component Scan quét package `net.javaguides.springboot`:
   - Tạo bean `vegPizza` (từ [`VegPizza`](file:///c:/Study/HK1Nam3/J2EE/Lab/Chuong1/Phan1/BaiThem/DuAn1.5/demo/src/main/java/net/javaguides/springboot/service/VegPizza.java)).
   - Tạo bean `nonVegPizza` (từ [`NonVegPizza`](file:///c:/Study/HK1Nam3/J2EE/Lab/Chuong1/Phan1/BaiThem/DuAn1.5/demo/src/main/java/net/javaguides/springboot/service/NonVegPizza.java)).
3. Spring khởi tạo [`PizzaController`](file:///c:/Study/HK1Nam3/J2EE/Lab/Chuong1/Phan1/BaiThem/DuAn1.5/demo/src/main/java/net/javaguides/springboot/controller/PizzaController.java):
   - Thấy constructor cần đối tượng `Pizza` với `@Qualifier("vegPizza")`.
   - Lấy bean `vegPizza` tiêm vào constructor.
4. Trong hàm `main`:
   - Lấy bean `PizzaController` từ context: `context.getBean(PizzaController.class)`.
   - Gọi `pizzaController.getPizza()` ➔ nhận về chuỗi `"Veg Pizza"` và in ra console.
