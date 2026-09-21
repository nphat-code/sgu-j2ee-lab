# Rule: Tự Động Ghi Chú Kiến Thức Cốt Lõi (Learning Notes)

## 📌 Mục đích
Quy định cách ghi lại kiến thức, giải thích khái niệm kỹ thuật và lưu vết học tập cho học phần Chuyên đề J2EE theo yêu cầu của người dùng.

---

## 🎯 Quy định thực thi

### 1. Khi nào ghi chú?
* Mỗi khi người dùng hỏi về:
  - Giải thích một file code, một đoạn mã hoặc một cơ chế kỹ thuật (như IoC, Dependency Injection, Bean Lifecycle, Stream API, Validation, HATEOAS, Exception Handling,...).
  - Giải thích các Annotation (`@Component`, `@Autowired`, `@Bean`, `@RestController`, `@Transactional`,...).
  - Giải thích nguyên nhân của lỗi và cách tư duy sửa lỗi.
  - Khi người dùng trực tiếp yêu cầu "note lại", "lưu lại kiến thức này".

### 2. Vị trí lưu file ghi chú
* **Luôn đặt file `NOTE.md` ngay bên trong thư mục bài tập đang làm việc**, ví dụ:
  - `Chuong1/Phan1/BaiMau/Bai2/NOTE.md`
  - `Chuong1/Phan2/BaiMau/Bai1/NOTE.md`
  - Hoặc thư mục bài thêm: `ChuongX/.../BaiThem/.../NOTE.md`
* Nếu file `NOTE.md` đã tồn tại trong bài tập đó, hãy **bổ sung thêm mục mới** vào file thay vì ghi đè làm mất kiến thức cũ.

### 3. Tiêu chuẩn định dạng `NOTE.md`
* **Ngôn ngữ:** Tiếng Việt, rõ ràng, sư phạm, chuẩn học thuật môn học.
* **Cấu trúc:**
  - Tiêu đề mục rõ ràng (sử dụng H2, H3).
  - Trích dẫn đoạn code liên quan ngắn gọn.
  - Giải thích: **Bản chất hoạt động** ➔ **Tại sao lại viết như vậy?** ➔ **Lưu ý / Lỗi thường gặp**.
* Sử dụng định dạng GitHub Markdown chuẩn đẹp (bảng, danh sách, callout alerts `> [!NOTE]`, `> [!TIP]`).

### 4. Giới hạn phạm vi (Scope Boundary)
* Khi giải thích và note kiến thức, **TUYỆT ĐỐI KHÔNG tự ý viết thêm mã nguồn vào các file code `.java`** của người dùng trừ khi người dùng yêu cầu sửa trực tiếp. Hãy để người dùng tự tay thực hành viết code.
