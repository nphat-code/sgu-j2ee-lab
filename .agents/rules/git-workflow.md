# Rule: Tự Động Commit & Push Lên Git Khi Hoàn Thành Mỗi Phần

## 📌 Mục đích
Quy định cơ chế tự động lưu vết và đồng bộ mã nguồn lên Git repository từ xa (remote git) ngay khi hoàn thành xong một bài tập, một phần thực hành hoặc một tính năng cụ thể.

---

## 🎯 Quy định thực thi

### 1. Khi nào thực hiện Commit & Push?
* Ngay khi hoàn thành một phần bài tập (Bài mẫu, Bài tập thêm, Fix lỗi hoàn chỉnh cho một bài tập).
* Khi code của bài tập đó đã được biên dịch thành công (build success) hoặc kiểm thử pass.
* Không dồn nhiều bài tập khác nhau vào một commit lớn; **xong phần nào commit và push ngay phần đó**.

### 2. Quy chuẩn Commit Message
* Áp dụng chuẩn **Conventional Commits**:
  - `feat: ...`: Hoàn thành tính năng mới hoặc bài tập mới (ví dụ: `feat: complete Chuong 1 Phan 1 Bai 3`).
  - `fix: ...`: Sửa lỗi cho một bài tập (ví dụ: `fix: resolve compilation errors and resource leak in Bai 3`).
  - `docs: ...`: Cập nhật ghi chú `NOTE.md`.
  - `refactor: ...`: Tối ưu cấu trúc code mà không đổi logic.
* Nội dung commit ngắn gọn, rõ ràng, nêu đúng bài tập / phần vừa hoàn thành.

### 3. Quy trình thực hiện
1. Kiểm tra trạng thái: `git status`.
2. Kiểm tra biên dịch / chạy thử bài tập để chắc chắn không push code hỏng.
3. Thêm các file liên quan: `git add <các file của phần đó>`.
4. Tạo commit: `git commit -m "<message>"`.
5. Đẩy lên nhánh đang làm việc: `git push origin <tên_nhánh>`.
6. Thông báo mã commit và trạng thái push cho người dùng.
