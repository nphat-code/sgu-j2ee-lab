# 📘 Ghi Chú Kiến Thức - Bài 2: Khái Niệm IoC & Dependency Injection

> **Thư mục bài làm:** `Chuong1/Phan1/BaiMau/Bai2`  
> **Nội dung trọng tâm:** Cơ chế Spring IoC, Constructor Injection, Java Stream API và ApplicationRunner.

---

## 1. Spring IoC Container & `@Component`
* **IoC (Inversion of Control - Đảo ngược điều khiển):** Thay vì lập trình viên tự quản lý vòng đời đối tượng bằng từ khóa `new`, Spring Container sẽ chịu trách nhiệm tạo, cấu hình và quản lý các đối tượng này (gọi là các **Spring Beans**).
* **`@Component`:** Đánh dấu một class là một Spring Bean để Spring tự động phát hiện qua cơ chế Component Scanning.

---

## 2. Constructor Injection (Tiêm phụ thuộc qua hàm khởi tạo)
Trong class `Calculator`:
```java
private final Collection<Operation> operations;

public Calculator(Collection<Operation> operations) {
    this.operations = operations;
    System.out.println("OPERATION" + operations);
}
```

* **Quy trình khởi tạo:**
  1. Spring bắt buộc phải khởi tạo xong tất cả các đối tượng phụ thuộc (`Addition`, `Multiplication`, `Substraction`) trước.
  2. Spring gom tất cả các Bean cài đặt interface `Operation` vào một `Collection<Operation>`.
  3. Truyền (tiêm) danh sách này vào Constructor của `Calculator` thì đối tượng `Calculator` mới được tạo thành công.
* **Tại sao khuyên dùng Constructor Injection?**
  * Cho phép đặt thuộc tính dạng `final` (bất biến - Immutability).
  * Đảm bảo đối tượng luôn ở trạng thái toàn vẹn, không bao giờ bị `NullPointerException`.
  * Rất dễ viết Unit Test độc lập mà không cần khởi động Spring.
* **Nguyên lý Open/Closed (OCP trong SOLID):**
  * Khi cần thêm phép toán mới (như `Division`), chỉ cần tạo class mới `implements Operation` có `@Component`.
  * **Không cần sửa bất kỳ dòng code nào** trong class `Calculator`.

---

## 3. Java Stream API trong phương thức `calculate`
```java
operations.stream()
    .filter((operation) -> operation.handles(op))
    .map((operation) -> operation.apply(lhs, rhs))
    .peek((result) -> System.out.printf("%d %s %d = %s %n", lhs, op, rhs, result))
    .findFirst()
    .orElseThrow(() -> new IllegalArgumentException("Unknown operation " + op));
```

* **`stream()`**: Chuyển đổi danh sách sang luồng dữ liệu (Stream) để xử lý theo phong cách lập trình hàm.
* **`.filter(...)`**: Lọc phần tử thỏa mãn điều kiện (`operation.handles(op) == true`).
* **`.map(...)`**: Chuyển đổi đối tượng `Operation` thành kết quả tính toán (`apply(lhs, rhs)`).
* **`.peek(...)`**: Thực thi một hành động phụ (như in log ra console) mà không làm gián đoạn dòng chảy dữ liệu.
* **`.findFirst()`**: Lấy kết quả đầu tiên tìm thấy (trả về kiểu `Optional<Integer>`).
* **`.orElseThrow(...)`**: Ném ra ngoại lệ `IllegalArgumentException` nếu không tìm thấy phép toán nào phù hợp.

---

## 4. `ApplicationRunner` và bản chất cú pháp Lambda `args -> { ... }`
Trong class `CalculatorApplication`:
```java
@Bean
public ApplicationRunner calculationRunner(Calculator calculator) {
    return args -> {
        calculator.calculate(137, 21, '+');
        calculator.calculate(137, 21, '*');
        calculator.calculate(137, 21, '-');
    };
}
```

* **`ApplicationRunner`:** Là một functional interface của Spring Boot. Mọi Bean kiểu này sẽ **tự động được Spring kích hoạt chạy ngay sau khi ứng dụng khởi động hoàn tất**.
* **Bản chất của `return args -> { ... }`:**
  * Không phải là trả về biến `args`, mà là **trả về một đối tượng cài đặt interface `ApplicationRunner`**.
  * `args` là **tham số đầu vào** của phương thức `run(ApplicationArguments args)`.
  * Cú pháp rút gọn tương đương với viết lớp ẩn danh (Anonymous Class):
    ```java
    return new ApplicationRunner() {
        @Override
        public void run(ApplicationArguments args) throws Exception {
            calculator.calculate(137, 21, '+');
            // ...
        }
    };
    ```
