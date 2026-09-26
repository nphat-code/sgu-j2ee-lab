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

---

## 5. Cẩm Nang Toàn Diện Về Java Stream API

### 5.1. Stream API là gì?
* Được giới thiệu từ **Java 8**, Stream API cho phép xử lý tập hợp dữ liệu (`List`, `Set`, `Map`, `Array`) theo phong cách **khai báo (Declarative / Functional Programming)** thay vì dùng các vòng lặp `for`/`while` truyền thống.
* **3 đặc tính cốt lõi:**
  1. **Không lưu trữ dữ liệu:** Stream chỉ là một luồng vận chuyển dữ liệu qua các bước xử lý.
  2. **Không làm biến đổi nguồn gốc:** Dữ liệu gốc trong List/Set ban đầu được giữ nguyên vẹn (Immutable).
  3. **Thực thi lười biếng (Lazy Evaluation):** Các thao tác trung gian sẽ **không chạy** cho đến khi gặp một thao tác kết thúc (Terminal Operation).

---

### 5.2. Cấu trúc 3 giai đoạn của một Stream Pipeline
Một chuỗi xử lý Stream luôn gồm 3 giai đoạn:

```text
[Nguồn dữ liệu (Source)] ➔ [Các thao tác trung gian (Intermediate)] ➔ [Thao tác kết thúc (Terminal)]
```

#### Giai đoạn 1: Tạo Stream (Source)
```java
List<String> names = List.of("Java", "Spring", "Hibernate", "React");

// Từ Collection:
Stream<String> stream1 = names.stream();

// Từ Mảng:
int[] numbers = {1, 2, 3, 4, 5};
IntStream stream2 = Arrays.stream(numbers);

// Khởi tạo trực tiếp:
Stream<String> stream3 = Stream.of("A", "B", "C");
```

#### Giai đoạn 2: Thao tác trung gian (Intermediate Operations)
*Đặc điểm: Trả về một `Stream` mới, có thể gọi nối tiếp nhau (Chaining).*

| Phương thức | Chức năng | Ví dụ |
| :--- | :--- | :--- |
| **`filter(Predicate)`** | Lọc các phần tử thỏa mãn điều kiện (`true`) | `.filter(n -> n % 2 == 0)` *(lấy số chẵn)* |
| **`map(Function)`** | Biến đổi mỗi phần tử thành một giá trị/kiểu khác | `.map(String::toUpperCase)` *(viết hoa)* |
| **`sorted()`** | Sắp xếp các phần tử | `.sorted()` hoặc `.sorted(Comparator.reverseOrder())` |
| **`distinct()`** | Loại bỏ các phần tử trùng lặp (dựa trên `equals()`) | `.distinct()` |
| **`limit(n)`** | Giữ lại tối đa `n` phần tử đầu tiên | `.limit(5)` |
| **`skip(n)`** | Bỏ qua `n` phần tử đầu tiên | `.skip(2)` |
| **`peek(Consumer)`** | Xem/in giá trị trong quá trình xử lý (dùng để debug) | `.peek(System.out::println)` |

#### Giai đoạn 3: Thao tác kết thúc (Terminal Operations)
*Đặc điểm: Đóng Stream lại, kích hoạt toàn bộ pipeline chạy và trả về kết quả cụ thể (List, số, boolean,...).*

| Phương thức | Chức năng | Ví dụ |
| :--- | :--- | :--- |
| **`toList()`** (Java 16+) | Gom các phần tử thành `List` (bất biến) | `.toList()` |
| **`collect(...)`** | Gom kết quả thành Collection linh hoạt | `.collect(Collectors.joining(", "))` |
| **`forEach(Consumer)`** | Lặp qua từng phần tử cuối cùng để thực hiện hành động | `.forEach(System.out::println)` |
| **`count()`** | Đếm tổng số lượng phần tử | `long total = stream.count();` |
| **`findFirst()` / `findAny()`** | Lấy phần tử đầu tiên / bất kỳ (trả về `Optional<T>`) | `.findFirst().orElse(null)` |
| **`anyMatch()` / `allMatch()`** | Kiểm tra có ít nhất 1 / tất cả thỏa điều kiện hay không | `.anyMatch(s -> s.startsWith("S"))` |
| **`reduce(...)`** | Kết hợp các phần tử thành một giá trị duy nhất (tính tổng, tích) | `.reduce(0, (a, b) -> a + b)` |

---

### 5.3. Các ví dụ thực tế thường dùng trong Spring Boot

#### Ví dụ 1: Lọc và trích xuất danh sách tên sinh viên qua môn (điểm >= 5.0)
```java
List<Student> students = studentRepository.findAll();

// Lấy danh sách tên viết hoa của các sinh viên qua môn
List<String> passedStudentNames = students.stream()
    .filter(s -> s.getScore() >= 5.0)
    .map(s -> s.getName().toUpperCase())
    .toList();
```

#### Ví dụ 2: Tìm kiếm một đối tượng theo điều kiện (Trả về `Optional`)
```java
// Giống cách làm trong class Calculator
Optional<Student> topStudent = students.stream()
    .filter(s -> s.getScore() == 10.0)
    .findFirst();

// Nếu không thấy thì ném ngoại lệ
Student student = topStudent.orElseThrow(() -> new NotFoundException("Không có sinh viên điểm 10"));
```

#### Ví dụ 3: Gom nhóm theo phòng ban (Grouping By)
```java
Map<String, List<Employee>> employeesByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));
```

---

## 6. Unit Test trong Spring Boot (JUnit 5 & AssertJ)

### 6.1. Phương thức `assertThat` là gì?
* `assertThat` là một phương thức static thuộc thư viện **AssertJ** (`org.assertj.core.api.Assertions.assertThat`). Thư viện này được tích hợp sẵn trong dependency `spring-boot-starter-test`.
* Cú pháp theo phong cách **Fluent Assertions** (viết nối tiếp như câu nói tiếng Anh):
  ```java
  assertThat(addition.apply(2, 2)).isEqualTo(4);
  assertThat(addition.handles('+')).isTrue();
  ```

### 6.2. Static Import (`import static ...`)
* Trong Java thông thường, bạn phải gọi: `Assertions.assertThat(...)`.
* Để gọi trực tiếp ngắn gọn `assertThat(...)`, bạn phải dùng cú pháp **`import static`**:
  ```java
  import static org.assertj.core.api.Assertions.assertThat;
  ```

---

## 7. Kiểm Thử Đơn Vị Với Mockito (`mock`, `when`, `verify`, `times`)

Trong class `CalculatorTest`:
```java
@BeforeEach
public void setup() {
    mockOperation = Mockito.mock(Operation.class);
    calculator = new Calculator(Collections.singletonList(mockOperation));
}
```

### 7.1. Tại sao phải dùng Mockito?
* **Mục tiêu của Unit Test:** Chỉ kiểm tra logic của riêng lớp `Calculator`, không phụ thuộc vào việc các lớp `Addition`, `Multiplication` có chạy đúng hay không.
* **`mock(Operation.class)`:** Tạo ra một đối tượng giả lập để thay thế các phụ thuộc thật.

### 7.2. Các phương thức cốt lõi của Mockito
1. **`when(...).thenReturn(...)` (Stubbing - Dàn dựng hành vi):**
   ```java
   when(mockOperation.handles('*')).thenReturn(true);
   when(mockOperation.apply(2, 2)).thenReturn(4);
   ```
   * *Ý nghĩa:* Dạy cho đối tượng giả biết nó phải trả về giá trị gì khi được gọi với tham số nhất định.
2. **`verify(mock, times(n)).method(...)` (Verification - Xác thực tương tác):**
   ```java
   verify(mockOperation, times(1)).apply(2, 2);
   ```
   * *Ý nghĩa:* Kiểm tra và khẳng định rằng phương thức `apply(2, 2)` trên đối tượng `mockOperation` đã **được gọi đúng 1 lần (`times(1)`)**. Nếu không được gọi hoặc bị gọi từ 2 lần trở lên, bài test sẽ báo lỗi (Fail).
3. **`times(n)`:**
   * Là một hàm static của Mockito quy định số lần mong muốn phương thức được gọi.
   * Để dùng được `times`, `verify`, `when`, cách gọn nhất là dùng:
     ```java
     import static org.mockito.Mockito.*;
     ```

---

## 8. Vòng Đời Thực Thi Kiểm Thử Khi Bấm Run (JUnit 5 & Mockito)

Khi bạn bấm **Run** tại file `CalculatorTest`, bộ máy JUnit Engine (Jupiter) thực hiện tuần tự theo quy trình sau:

```text
[Bấm Run]
   │
   ├─► Tìm thấy 2 phương thức @Test
   │
   ├─► [Test Case 1]
   │     ├─► Chạy @BeforeEach setup() (Tạo mockOperation và calculator mới)
   │     ├─► Chạy throwExceptionWhenNoSuitableOperationFound()
   │     └─► Kết luận: PASS (Bắt đúng ngoại lệ IllegalArgumentException)
   │
   ├─► [Test Case 2]
   │     ├─► Chạy lại @BeforeEach setup() (Reset hoàn toàn đối tượng, tránh phụ thuộc)
   │     ├─► Chạy shouldCallApplyMethodWhenSuitableOperationFound()
   │     └─► Kết luận: PASS (Xác thực hàm apply đã được gọi đúng 1 lần)
   │
   └─► Tổng kết: 2/2 Tests Passed (Màu xanh)
```

### Chi tiết từng giai đoạn:
1. **Khởi tạo môi trường độc lập:** JUnit luôn tạo mới một instance của `CalculatorTest` cho mỗi test case.
2. **`@BeforeEach setup()`:**
   - Tạo đối tượng giả `mockOperation = Mockito.mock(Operation.class)`.
   - Tạo `calculator = new Calculator(Collections.singletonList(mockOperation))`.
   - Mục đích: Đảm bảo dữ liệu test ở bài trước không làm sai lệch bài sau (Test Isolation).
3. **Chạy kịch bản 1 (Kiểm tra lỗi):**
   - Giả lập `handles(bất kỳ)` ➔ trả về `false`.
   - Gọi `calculate(2, 2, '*')` ➔ Stream ném ra `IllegalArgumentException`.
   - `assertThrows(...)` bắt được ngoại lệ này ➔ Khẳng định hàm xử lý lỗi chính xác.
4. **Chạy kịch bản 2 (Kiểm tra tương tác thành công):**
   - Giả lập `handles('*') = true`, `apply(2, 2) = 4`.
   - Gọi `calculate(2, 2, '*')`.
   - `verify(mockOperation, times(1)).apply(2, 2)`: Khẳng định phương thức `apply` được gọi đúng 1 lần với tham số `(2, 2)`.

---

## 9. Mô Hình AAA trong Unit Test & Vai Trò Của `calculator.calculate(...)`

Trong phương thức test:
```java
@Test
void shouldCallApplyMethodWhenSuitableOperationFound() {
    // 1. Arrange (Chuẩn bị / Dàn dựng)
    when(mockOperation.handles('*')).thenReturn(true);
    when(mockOperation.apply(2, 2)).thenReturn(4);

    // 2. Act (Hành động / Thực thi)
    calculator.calculate(2, 2, '*');

    // 3. Assert / Verify (Xác thực kết quả)
    verify(mockOperation, times(1)).apply(2, 2);
}
```

### 9.1. Lệnh `calculator.calculate(2, 2, '*')` để làm gì?
* **Về mặt nghiệp vụ (Business logic):** Yêu cầu đối tượng `calculator` thực hiện phép toán `2 * 2` bằng cách duyệt Stream tìm phép toán hỗ trợ ký tự `'*'`, sau đó gọi `apply(2, 2)` và in kết quả ra console (`2 * 2 = 4`).
* **Về mặt kiểm thử (Unit Test):** Đây là bước **Act** (kích hoạt phương thức cần kiểm thử - Method Under Test).
  * Lớp `Calculator` chỉ thực hiện logic (lọc Stream và gọi `apply`) khi phương thức `calculate` này được kích hoạt.
  * Nếu không gọi dòng này, bước `verify(mockOperation, times(1)).apply(2, 2)` ở phía sau chắc chắn sẽ thất bại (**Fail**) vì phương thức `apply` chưa từng được gọi lần nào.

### 9.2. Chuẩn 3 bước AAA (Arrange - Act - Assert) trong kiểm thử phần mềm
| Bước | Tên gọi | Nhiệm vụ trong bài test | Dòng code tương ứng |
| :--- | :--- | :--- | :--- |
| **1** | **Arrange** *(Chuẩn bị)* | Thiết lập dữ liệu đầu vào và dàn dựng trước hành vi của Mock (`when...thenReturn`). | Dòng 33, 34 |
| **2** | **Act** *(Hành động)* | Gọi chính xác phương thức đang muốn kiểm thử với các tham số cụ thể. | Dòng 35 (`calculator.calculate(2, 2, '*')`) |
| **3** | **Assert / Verify** *(Kiểm tra)* | Đối chiếu kết quả trả về hoặc xác thực các tương tác phụ thuộc (`verify`). | Dòng 36 |

---

## 10. Kiểm Thử Ngoại Lệ Với `assertThrows` và `anyChar()`

Trong phương thức test:
```java
@Test
void throwExceptionWhenNoSuitableOperationFound() {
    when(mockOperation.handles(anyChar())).thenReturn(false);
    Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(2, 2, '*'));
}
```

### 10.1. Mục đích của bài test
Kiểm tra xem hệ thống có **ném ra đúng ngoại lệ `IllegalArgumentException`** khi người dùng truyền vào một phép toán không được hỗ trợ hay không.

### 10.2. Chi tiết các dòng lệnh
1. **`when(mockOperation.handles(anyChar())).thenReturn(false);`**
   - **`anyChar()`** (Argument Matcher của Mockito): Đại diện cho **bất kỳ ký tự nào**.
   - Ý nghĩa: Giả lập kịch bản mọi phép toán đều trả về `false` (không có phép toán nào hỗ trợ ký tự được truyền vào).
2. **`Assertions.assertThrows(IllegalArgumentException.class, () -> calculator.calculate(2, 2, '*'));`**
   - **`assertThrows`** (JUnit 5): Dùng để bắt và kiểm tra ngoại lệ.
   - Nhận vào 2 tham số:
     1. Loại ngoại lệ mong muốn xảy ra: `IllegalArgumentException.class`.
     2. Khối lệnh thực thi (dưới dạng Lambda `() -> ...`): `calculator.calculate(2, 2, '*')`.
   - **Đánh giá kết quả:**
     - Nếu khi chạy hàm `calculate(...)` mà **có ném ra** đúng lỗi `IllegalArgumentException` (từ `.orElseThrow(...)` trong Stream) ➔ Test **PASS** (đúng như mong đợi).
     - Nếu hàm chạy bình thường (không có lỗi) hoặc ném ra lỗi khác ➔ Test **FAIL**.

