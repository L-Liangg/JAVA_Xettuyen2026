QUY TẮC TÍNH ĐIỂM THXT (Điểm tổ hợp xét tuyển)
1. Đầu vào cho mỗi nguyện vọng
CCCD thí sinh

Mã ngành

Thứ tự nguyện vọng

Phương thức (THPT / DGNL / VSAT)

2. Các bước tính ĐTHXT
Bước 1: Lấy điểm thi thô của thí sinh theo phương thức
Nếu phương thức THPT: Lấy từ bảng xt_diemthixettuyen (các cột TO, LI, HO, VA, SU, DI, TI, N1_THI, ...)

Nếu phương thức DGNL hoặc VSAT: Lấy từ bảng xt_diemthi_dgnl_vsat theo cccd và ma_mon

Bước 2: Xác định tổ hợp xét tuyển của ngành
Tra bảng xt_nganh_tohop theo manganh để biết:

matohop (A00, A01, D01, ...)

3 môn thi: th_mon1, th_mon2, th_mon3

Hệ số từng môn: hsmon1, hsmon2, hsmon3

Bước 3: Tính ĐTHXT theo phương thức (công thức chung)
Công thức gốc:

text
ĐTHXT = (d1 × hs1 + d2 × hs2 + d3 × hs3) / (hs1 + hs2 + hs3) × 3
Riêng từng phương thức:

Phương thức	Cách tính
THPT	d1, d2, d3 là điểm thi THPT (thang 10), áp dụng công thức trên
VSAT	- Quy đổi điểm VSAT (thang 150) về thang 10 (dùng bảng bách phân vị)
- Sau đó áp dụng công thức trên ×3
DGNL	- Quy đổi điểm ĐGNL (thang 150) về thang 30 (dùng bảng bách phân vị)
- Không nhân thêm hệ số (đã là thang 30)
Bước 4: Quy đổi về tổ hợp gốc (nếu cần)
Lấy n_tohopgoc từ bảng xt_nganh

Tra bảng độ lệch trong file cac cong thuc tinh.docx:

Nếu matohop = n_tohopgoc → không quy đổi

Nếu khác → lấy dolech từ bảng (hoặc tra từ file):

text
ĐTHXT_quy_đổi = ĐTHXT - dolech
Nếu tổ hợp không có trong bảng độ lệch → dolech = 0

Ví dụ bạn nêu: Ngành 7601 có tổ hợp gốc A00, thí sinh thi A01 được 24 điểm, độ lệch A01→A00 là -0,69 → điểm quy đổi = 24 - (-0,69) = 24,69 (chứ không phải 23,31). Bạn kiểm tra lại dấu nhé.

Bước 5: Chọn điểm cao nhất (nếu nhiều tổ hợp)
Một ngành có thể có nhiều tổ hợp xét tuyển

Hệ thống sẽ tự động tính ĐTHXT cho tất cả tổ hợp của ngành đó

Chọn ĐTHXT cao nhất (sau quy đổi về tổ hợp gốc) để lưu vào nguyện vọng

Lưu lại tt_thm = tên tổ hợp cho điểm cao nhất

Bước 6: Lưu vào bảng xt_nguyenvongxettuyen
diem_thxt = ĐTHXT cao nhất đã chọn

tt_thm = mã tổ hợp cho điểm cao nhất

📝 PROMPT HOÀN CHỈNH
markdown
Tôi có hệ thống cơ sở dữ liệu xét tuyển `xettuyen2026` với các bảng theo file schema.sql.

Tôi cần xây dựng **hệ thống tự động tính điểm THXT (Điểm tổ hợp xét tuyển)** cho từng nguyện vọng của thí sinh trong bảng `xt_nguyenvongxettuyen`, dựa trên:
1. Điểm thi thực tế của thí sinh (THPT, DGNL, VSAT)
2. Công thức tính điểm trong file `cac cong thuc tinh.docx`
3. Bảng độ lệch giữa các tổ hợp

## YÊU CẦU CHI TIẾT

### Input:
- Bảng `xt_nguyenvongxettuyen` đã có: `nn_cccd`, `nv_manganh`, `nv_tt`, `tt_phuongthuc` (THPT/DGNL/VSAT), các cột điểm đang để trống
- Bảng `xt_diemthixettuyen`: điểm thi THPT của thí sinh (thang 10)
- Bảng `xt_diemthi_dgnl_vsat`: điểm thi DGNL và VSAT (thang 150)
- Bảng `xt_nganh`: thông tin ngành, đặc biệt `n_tohopgoc` (tổ hợp gốc)
- Bảng `xt_nganh_tohop`: danh sách tổ hợp xét tuyển của từng ngành, kèm môn thi và hệ số
- Bảng `xt_tohop_monthi`: định nghĩa tổ hợp môn
- File `cac cong thuc tinh.docx` (được nhúng trong context) chứa:
  - Bảng độ lệch giữa các tổ hợp (A00, A01, B00, C00, C01, D01, D07)
  - Công thức quy đổi điểm VSAT → thang 10 (dùng bảng bách phân vị)
  - Công thức quy đổi điểm ĐGNL → thang 30 (dùng bảng bách phân vị)

### Xử lý cho từng nguyện vọng:

#### Bước 1: Xác định dữ liệu điểm thi
- Nếu `tt_phuongthuc = 'THPT'`: lấy điểm từ `xt_diemthixettuyen` theo `cccd`
- Nếu `tt_phuongthuc = 'DGNL' hoặc 'VSAT'`: lấy điểm từ `xt_diemthi_dgnl_vsat` theo `cccd` và `ma_mon` tương ứng

#### Bước 2: Tính ĐTHXT cho từng tổ hợp của ngành
Với mỗi `matohop` trong `xt_nganh_tohop` của `nv_manganh`:
- Lấy 3 môn thi và hệ số
- Tính điểm theo công thức:
dt = (d1*hs1 + d2*hs2 + d3*hs3) / (hs1+hs2+hs3)

text
- **Nếu phương thức THPT**: ĐTHXT = dt × 3
- **Nếu phương thức VSAT**: 
- Quy đổi từng môn từ thang 150 → thang 10 (dùng bảng bách phân vị - giả sử có sẵn)
- Sau đó ĐTHXT = dt × 3
- **Nếu phương thức DGNL**:
- Quy đổi tổng điểm từ thang 150 → thang 30 (dùng bảng bách phân vị)
- ĐTHXT = điểm đã quy đổi (không nhân thêm)

#### Bước 3: Quy đổi về tổ hợp gốc
- Lấy `n_tohopgoc` từ `xt_nganh`
- Nếu `matohop` == `n_tohopgoc`: ĐTHXT_quydoi = ĐTHXT
- Nếu khác: tra bảng độ lệch:
- Tìm `dolech` tại giao của hàng = `n_tohopgoc`, cột = `matohop`
- ĐTHXT_quydoi = ĐTHXT - dolech
- Nếu không tìm thấy → dolech = 0

#### Bước 4: Chọn điểm cao nhất
- Lấy `max(ĐTHXT_quydoi)` trong tất cả tổ hợp của ngành
- Ghi nhận `tt_thm` = `matohop` tương ứng với điểm cao nhất

#### Bước 5: Cập nhật vào `xt_nguyenvongxettuyen`
- `diem_thxt` = ĐTHXT cao nhất đã quy đổi về tổ hợp gốc
- `tt_thm` = mã tổ hợp cho điểm cao nhất

### Yêu cầu đặc biệt (theo ví dụ của bạn):
> "Ngành 7601 có điểm chuẩn 23, tổ hợp gốc A00. Thí sinh thi A00 được 22 (không đủ), thi A01 được 24, tính độ lệch A01→A00 = -0,69 → quy về A00 được 24,69. Hệ thống phải tự động tìm ra điểm cao nhất này và lưu vào `xt_nguyenvong` cùng `tt_thm`."

Hệ thống cần **duyệt hết tất cả tổ hợp** của ngành, không chỉ tổ hợp thí sinh đăng ký, để tìm ra phương án có lợi nhất cho thí sinh.

### Output:
- Cập nhật trực tiếp vào bảng `xt_nguyenvongxettuyen`
- Log các trường hợp lỗi: thiếu điểm, sai CCCD, sai mã ngành, không tìm thấy tổ hợp