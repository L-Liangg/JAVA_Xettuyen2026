package com.xettuyen.service.imports;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelColumnMapping {

    public static final Map<String, String> THI_SINH = new LinkedHashMap<>() {{
        put("cccd", "cccd");
        put("số báo danh", "sobaodanh");
        put("họ", "ho");
        put("tên", "ten");
        put("ngày sinh", "ngay_sinh");
        put("điện thoại", "dien_thoai");
        put("mật khẩu", "password");
        put("giới tính", "gioi_tinh");
        put("email", "email");
        put("mật khẩu", "mat_khau");
        put("nơi sinh", "noi_sinh");
        put("đối tượng", "doi_tuong");
        put("khu vực", "khu_vuc");
    }};

    public static final Map<String, String> NGANH = new LinkedHashMap<>() {{
        put("mã ngành", "manganh");
        put("tên ngành", "tennganh");
        put("tổ hợp gốc", "n_tohopgoc");
        put("chỉ tiêu", "n_chitieu");
        put("điểm sàn", "n_diemsan");
        put("điểm trúng tuyển", "n_diemtrungtuyen");
        put("tuyển thẳng", "n_tuyenthang");
        put("đgnl", "n_dgnl");
        put("thpt", "n_thpt");
        put("v-sat", "n_vsat");
    }};

    public static final Map<String, String> TOHOP_MON = new LinkedHashMap<>() {{
        put("mã tổ hợp", "matohop");
        put("môn 1", "mon1");
        put("môn 2", "mon2");
        put("môn 3", "mon3");
        put("tên tổ hợp", "tentohop");
    }};

    public static final Map<String, String> NGANH_TOHOP = new LinkedHashMap<>() {{
        put("mã ngành", "manganh");
        put("mã tổ hợp", "matohop");
        put("môn 1", "th_mon1");
        put("hs môn 1", "hsmon1");
        put("môn 2", "th_mon2");
        put("hs môn 2", "hsmon2");
        put("môn 3", "th_mon3");
        put("hs môn 3", "hsmon3");
        put("n1", "N1");
        put("to", "TO");
        put("li", "LI");
        put("ho", "HO");
        put("si", "SI");
        put("va", "VA");
        put("su", "SU");
        put("di", "DI");
        put("ti", "TI");
        put("khac", "KHAC");
        put("ktpl", "KTPL");
        put("độ lệch", "dolech");
    }};

    public static final Map<String, String> DIEM_THI = new LinkedHashMap<>() {{
        put("cccd", "cccd");
        put("số báo danh", "sobaodanh");
        put("toán", "TO");
        put("văn", "VA");
        put("lí", "LI");
        put("hóa", "HO");
        put("sinh", "SI");
        put("sử", "SU");
        put("địa", "DI");
        put("ktpl", "KTPL");
        put("tin", "TI");
        put("cncn", "CNCN");
        put("cnnn", "CNNN");
        put("n1 thi", "N1_THI");
        put("nk1", "NK1");
        put("nk2", "NK2");
        put("nl1", "NL1");
    }};

    public static final Map<String, String> DIEM_THI_DGNL_VSAT = new LinkedHashMap<>() {{
        put("cccd", "cccd");
        put("đợt thi", "dot_thi");
        put("mã đợt thi", "ma_dot_thi");
        put("ngày thi", "ngay_thi");
        put("năm thi", "nam");
        put("mã môn thi", "ma_mon");
        put("tên môn thi", "ten_mon");
        put("điểm", "diem");
        put("thang điểm", "thang_diem");
        put("mã dvtctdl", "ma_dvtctdl");
        put("tên dvtctdl", "ten_dvtctdl");
    }};

    public static final Map<String, String> NGUYEN_VONG = new LinkedHashMap<>() {{
        put("cccd", "nn_cccd");
        put("mã ngành", "nv_manganh");
        put("thứ tự nv", "nv_tt");
        put("phương thức", "tt_phuongthuc");
    }};

    public static final Map<String, String> DIEM_CONG = new LinkedHashMap<>() {{
        put("cccd", "ts_cccd");
        put("mã ngành", "manganh");
        put("tổ hợp", "matohop");
        put("phương thức", "phuongthuc");
        put("điểm cc", "diemCC");
        put("điểm utxt", "diemUtxt");
        put("điểm tổng", "diemTong");
        put("ghi chú", "ghichu");
        put("khu vực", "ghichu");
        put("khu vuc", "ghichu");
    }};

    public static final Map<String, String> BANG_QUY_DOI = new LinkedHashMap<>() {{
        put("mã quy đổi", "d_maquydoi");
        put("phương thức", "d_phuongthuc");
        put("tổ hợp", "d_tohop");
        put("môn", "d_mon");
        put("điểm a", "d_diema");
        put("điểm b", "d_diemb");
        put("điểm c", "d_diemc");
        put("điểm d", "d_diemd");
        put("phân vị", "d_phanvi");
    }};
}
