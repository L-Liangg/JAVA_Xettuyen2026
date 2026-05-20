
  DROP DATABASE IF EXISTS `xettuyen2026`;
  CREATE DATABASE `xettuyen2026` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
  USE `xettuyen2026`;

  SET FOREIGN_KEY_CHECKS = 0;

  -- =============================================
  -- 1. BẢNG QUYỀN & NGƯỜI DÙNG
  -- =============================================
  DROP TABLE IF EXISTS `xt_roles`;
  CREATE TABLE `xt_roles` (
    `idrole` int NOT NULL AUTO_INCREMENT,
    `role_name` varchar(50) NOT NULL,
    `description` varchar(200) DEFAULT NULL,
    PRIMARY KEY (`idrole`),
    UNIQUE KEY `role_name_UNIQUE` (`role_name`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_users`;
  CREATE TABLE `xt_users` (
    `iduser` int NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(255) NOT NULL,
    `full_name` varchar(100) DEFAULT NULL,
    `email` varchar(100) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `role_id` int NOT NULL,
    `is_active` tinyint(1) DEFAULT 1,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`iduser`),
    UNIQUE KEY `username_UNIQUE` (`username`),
    KEY `fk_users_role_idx` (`role_id`),
    CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `xt_roles` (`idrole`) ON DELETE RESTRICT ON UPDATE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  -- =============================================
  -- CÁC BẢNG GỐC (ĐÃ CHUẨN HÓA CHARSET)
  -- =============================================

  DROP TABLE IF EXISTS `xt_bangquydoi`;
  CREATE TABLE `xt_bangquydoi` (
    `idqd` int NOT NULL AUTO_INCREMENT,
    `d_phuongthuc` varchar(45) DEFAULT NULL,
    `d_tohop` varchar(45) DEFAULT NULL,
    `d_mon` varchar(45) DEFAULT NULL,
    `d_diema` decimal(6,2) DEFAULT NULL,
    `d_diemb` decimal(6,2) DEFAULT NULL,
    `d_diemc` decimal(6,2) DEFAULT NULL,
    `d_diemd` decimal(6,2) DEFAULT NULL,
    `d_maquydoi` varchar(45) DEFAULT NULL,
    `d_phanvi` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`idqd`),
    UNIQUE KEY `d_maquydoi_UNIQUE` (`d_maquydoi`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

<<<<<<< HEAD
DROP TABLE IF EXISTS `xt_diemcongxetuyen`;
CREATE TABLE `xt_diemcongxetuyen` (
  `iddiemcong` int unsigned NOT NULL AUTO_INCREMENT,
  `ts_cccd` varchar(45) NOT NULL,
  `manganh` varchar(20) DEFAULT '0.00',
  `matohop` varchar(10) DEFAULT '0.00',
  `phuongthuc` varchar(45) DEFAULT NULL,
  `diemCC` decimal(6,2) DEFAULT NULL,
  `diemUtxt` decimal(6,2) DEFAULT NULL,
  `diemTong` decimal(6,2) DEFAULT '0.00',
  `ghichu` text,
  `dc_keys` varchar(45) NOT NULL,
  PRIMARY KEY (`iddiemcong`),
  UNIQUE KEY `dc_keys_UNIQUE` (`dc_keys`)
<<<<<<< HEAD
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
=======
) ENGINE=InnoDB DEFAULT CHARSET=u
tf8mb4 COLLATE=utf8mb4_general_ci;
>>>>>>> origin/annguyen
=======
  DROP TABLE IF EXISTS `xt_diemcongxetuyen`;
  CREATE TABLE `xt_diemcongxetuyen` (
    `iddiemcong` int unsigned NOT NULL AUTO_INCREMENT,
    `ts_cccd` varchar(45) NOT NULL,
    `manganh` varchar(20) DEFAULT '0.00',
    `matohop` varchar(10) DEFAULT '0.00',
    `phuongthuc` varchar(45) DEFAULT NULL,
    `diemCC` decimal(6,2) DEFAULT NULL,
    `diemUtxt` decimal(6,2) DEFAULT NULL,
    `diemTong` decimal(6,2) DEFAULT '0.00',
    `ghichu` text,
    `dc_keys` varchar(45) NOT NULL,
    PRIMARY KEY (`iddiemcong`),
    UNIQUE KEY `dc_keys_UNIQUE` (`dc_keys`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
>>>>>>> origin/feature

  DROP TABLE IF EXISTS `xt_diemthixettuyen`;
  CREATE TABLE `xt_diemthixettuyen` (
    `iddiemthi` int NOT NULL AUTO_INCREMENT,
    `cccd` varchar(20) NOT NULL,
    `sobaodanh` varchar(45) DEFAULT NULL,
    `d_phuongthuc` varchar(10) DEFAULT NULL,
    `TO` decimal(8,2) DEFAULT '0.00',
    `LI` decimal(8,2) DEFAULT '0.00',
    `HO` decimal(8,2) DEFAULT '0.00',
    `SI` decimal(8,2) DEFAULT '0.00',
    `SU` decimal(8,2) DEFAULT '0.00',
    `DI` decimal(8,2) DEFAULT '0.00',
    `VA` decimal(8,2) DEFAULT '0.00',
    `N1_THI` decimal(8,2) DEFAULT NULL COMMENT 'Điểm thi gốc',
    `N1_CC` decimal(8,2) DEFAULT '0.00' COMMENT 'max(N1_Thi, N1_QD)',
    `CNCN` decimal(8,2) DEFAULT '0.00',
    `CNNN` decimal(8,2) DEFAULT '0.00',
    `TI` decimal(8,2) DEFAULT '0.00',
    `KTPL` decimal(8,2) DEFAULT '0.00',
    `NL1` decimal(8,2) DEFAULT NULL,
    `NK1` decimal(8,2) DEFAULT NULL,
    `NK2` decimal(8,2) DEFAULT NULL,
    PRIMARY KEY (`iddiemthi`),
    UNIQUE KEY `cccd_UNIQUE` (`cccd`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_nganh`;
  CREATE TABLE `xt_nganh` (
    `idnganh` int NOT NULL AUTO_INCREMENT,
    `manganh` varchar(45) NOT NULL,
    `tennganh` varchar(100) NOT NULL,
    `n_tohopgoc` varchar(3) DEFAULT NULL,
    `n_chitieu` int NOT NULL DEFAULT '0',
    `n_diemsan` decimal(10,2) DEFAULT NULL,
    `n_diemtrungtuyen` decimal(10,2) DEFAULT NULL,
    `n_tuyenthang` varchar(1) DEFAULT NULL,
    `n_dgnl` varchar(1) DEFAULT NULL,
    `n_thpt` varchar(1) DEFAULT NULL,
    `n_vsat` varchar(1) DEFAULT NULL,
    `sl_xtt` int DEFAULT NULL,
    `sl_dgnl` int DEFAULT NULL,
    `sl_vsat` int DEFAULT NULL,
    `sl_thpt` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`idnganh`),
    UNIQUE KEY `manganh_UNIQUE` (`manganh`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_tohop_monthi`;
  CREATE TABLE `xt_tohop_monthi` (
    `idtohop` int NOT NULL AUTO_INCREMENT,
    `matohop` varchar(45) NOT NULL,
    `mon1` varchar(10) NOT NULL,
    `mon2` varchar(10) NOT NULL,
    `mon3` varchar(10) NOT NULL,
    `tentohop` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`idtohop`),
    UNIQUE KEY `matohop_UNIQUE` (`matohop`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_nganh_tohop`;
  CREATE TABLE `xt_nganh_tohop` (
    `id` int NOT NULL AUTO_INCREMENT,
    `manganh` varchar(45) NOT NULL,
    `matohop` varchar(45) NOT NULL,
    `th_mon1` varchar(10) DEFAULT NULL,
    `hsmon1` tinyint DEFAULT NULL,
    `th_mon2` varchar(10) DEFAULT NULL,
    `hsmon2` tinyint DEFAULT NULL,
    `th_mon3` varchar(10) DEFAULT NULL,
    `hsmon3` tinyint DEFAULT NULL,
    `tb_keys` varchar(45) DEFAULT NULL COMMENT 'manganh_matohop',
    `N1` tinyint(1) DEFAULT NULL,
    `TO` tinyint(1) DEFAULT NULL,
    `LI` tinyint(1) DEFAULT NULL,
    `HO` tinyint(1) DEFAULT NULL,
    `SI` tinyint(1) DEFAULT NULL,
    `VA` tinyint(1) DEFAULT NULL,
    `SU` tinyint(1) DEFAULT NULL,
    `DI` tinyint(1) DEFAULT NULL,
    `TI` tinyint(1) DEFAULT NULL,
    `KHAC` tinyint(1) DEFAULT NULL,
    `KTPL` tinyint(1) DEFAULT NULL,
    `dolech` decimal(6,2) DEFAULT '0.00',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_UNIQUE` (`tb_keys`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_thisinhxettuyen25`;
  CREATE TABLE `xt_thisinhxettuyen25` (
    `idthisinh` int NOT NULL AUTO_INCREMENT,
    `cccd` varchar(20) DEFAULT NULL,
    `sobaodanh` varchar(45) DEFAULT NULL,
    `ho` varchar(100) DEFAULT NULL,
    `ten` varchar(100) DEFAULT NULL,
    `ngay_sinh` varchar(45) DEFAULT NULL,
    `dien_thoai` varchar(20) DEFAULT NULL,
    `password` varchar(100) DEFAULT NULL,
    `gioi_tinh` varchar(10) DEFAULT NULL,
    `email` varchar(100) DEFAULT NULL,
    `noi_sinh` varchar(80) DEFAULT NULL,
    `updated_at` date DEFAULT NULL,
    `doi_tuong` varchar(45) DEFAULT NULL,
    `khu_vuc` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`idthisinh`),
    UNIQUE KEY `cccd_UNIQUE` (`cccd`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

  DROP TABLE IF EXISTS `xt_nguyenvongxettuyen`;
  CREATE TABLE `xt_nguyenvongxettuyen` (
    `idnv` int NOT NULL AUTO_INCREMENT,
    `nn_cccd` varchar(45) NOT NULL,
    `nv_manganh` varchar(45) NOT NULL,
    `nv_tt` int NOT NULL,
    `diem_thxt` decimal(10,5) DEFAULT NULL,
    `diem_utqd` decimal(10,5) DEFAULT NULL,
    `diem_cong` decimal(6,2) DEFAULT NULL,
    `diem_xettuyen` decimal(10,5) DEFAULT NULL,
    `nv_ketqua` varchar(45) DEFAULT NULL,
    `nv_keys` varchar(45) DEFAULT NULL,
    `tt_phuongthuc` varchar(45) DEFAULT NULL,
    `tt_thm` varchar(45) DEFAULT NULL,
    PRIMARY KEY (`idnv`),
    UNIQUE KEY `nv_keys_UNIQUE` (`nv_keys`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `xt_diemthi_dgnl_vsat`;
CREATE TABLE xettuyen2026.xt_diemthi_dgnl_vsat (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    cccd        VARCHAR(20) NOT NULL,
    dot_thi     VARCHAR(45),
    ma_dot_thi  VARCHAR(45) NOT NULL,
    ngay_thi    VARCHAR(45),
    nam         INT,
    ma_mon      VARCHAR(20) NOT NULL,
    ten_mon     VARCHAR(100),
    diem        DECIMAL(7,2),
    thang_diem  VARCHAR(20),
    ma_dvtctdl  VARCHAR(45),
    ten_dvtctdl VARCHAR(100),
    dv_keys     VARCHAR(100) NOT NULL,
    UNIQUE KEY uk_dv_keys (dv_keys)
);

  -- =============================================
  -- FOREIGN KEYS
  -- =============================================

  ALTER TABLE `xt_diemcongxetuyen`
    ADD CONSTRAINT `fk_diemcong_thisinh` FOREIGN KEY (`ts_cccd`) REFERENCES `xt_thisinhxettuyen25` (`cccd`) ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE `xt_diemthixettuyen`
    ADD CONSTRAINT `fk_diemthi_thisinh` FOREIGN KEY (`cccd`) REFERENCES `xt_thisinhxettuyen25` (`cccd`) ON DELETE CASCADE ON UPDATE CASCADE;


  ALTER TABLE `xt_diemthi_dgnl_vsat`
    ADD CONSTRAINT `fk_diemthidgnl&vsat_thisinh` FOREIGN KEY (`cccd`) REFERENCES `xt_thisinhxettuyen25` (`cccd`) ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE `xt_nganh_tohop`
    ADD CONSTRAINT `fk_nganh_tohop_nganh` FOREIGN KEY (`manganh`) REFERENCES `xt_nganh` (`manganh`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_nganh_tohop_tohop` FOREIGN KEY (`matohop`) REFERENCES `xt_tohop_monthi` (`matohop`) ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE `xt_nguyenvongxettuyen`
    ADD CONSTRAINT `fk_nguyenvong_thisinh` FOREIGN KEY (`nn_cccd`) REFERENCES `xt_thisinhxettuyen25` (`cccd`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_nguyenvong_nganh` FOREIGN KEY (`nv_manganh`) REFERENCES `xt_nganh` (`manganh`) ON DELETE RESTRICT ON UPDATE CASCADE;

  -- =============================================
  -- DỮ LIỆU MẪU
  -- =============================================

  INSERT INTO `xt_roles` (`role_name`, `description`) VALUES
  ('admin', 'Quản trị viên hệ thống'),
  ('teacher', 'Giáo viên / Cán bộ xét tuyển');


  INSERT INTO `xt_users` (`username`, `password`, `full_name`, `email`, `phone`, `role_id`, `is_active`) VALUES
  ('admin', 'admin123', 'Nguyễn Văn Admin', 'admin@truong.edu.vn', '0912345678', 1, 1),
  ('teacher1', 'teacher123', 'Trần Thị Giáo Viên', 'teacher1@truong.edu.vn', '0987654321', 2, 1),
  ('canbo01', 'canbo0123', 'Lê Văn Cán Bộ', 'canbo01@truong.edu.vn', '0978123456', 2, 1);

  INSERT INTO `xt_tohop_monthi` (`matohop`, `mon1`, `mon2`, `mon3`, `tentohop`) VALUES
  ('A00', 'TO', 'LI', 'HO', 'Toán - Lý - Hóa'),
  ('A01', 'TO', 'LI', 'VA', 'Toán - Lý - Văn'),
  ('C00', 'VA', 'SU', 'DI', 'Văn - Sử - Địa'),
  ('D01', 'TO', 'VA', 'TI', 'Toán - Văn - Anh');

  INSERT INTO `xt_nganh` (`manganh`, `tennganh`, `n_tohopgoc`, `n_chitieu`, `n_diemsan`, `n_dgnl`, `n_thpt`, `n_vsat`) VALUES
  ('CNTT', 'Công nghệ Thông tin', 'A00', 120, 24.00, '1', '1', '1'),
  ('QTKD', 'Quản trị Kinh doanh', 'D01', 150, 22.00, '1', '1', '1'),
  ('SPTOAN', 'Sư phạm Toán học', 'A00', 60, 25.00, '1', '1', '1');

  INSERT INTO `xt_nganh_tohop` (`manganh`, `matohop`, `th_mon1`, `hsmon1`, `th_mon2`, `hsmon2`, `th_mon3`, `hsmon3`, `tb_keys`) VALUES
  ('CNTT', 'A00', 'TO', 1, 'LI', 1, 'HO', 1, 'CNTT_A00'),
  ('QTKD', 'D01', 'TO', 1, 'VA', 1, 'TI', 1, 'QTKD_D01');

  INSERT INTO `xt_bangquydoi` (`d_phuongthuc`, `d_tohop`, `d_mon`, `d_diema`, `d_diemb`, `d_diemc`, `d_diemd`, `d_maquydoi`) VALUES
  ('THPT', 'A00', 'TO', 9.0, 8.0, 7.0, 6.0, 'QD_THPT_A00_TO');

  INSERT INTO `xt_thisinhxettuyen25` (`cccd`, `sobaodanh`, `ho`, `ten`, `ngay_sinh`, `dien_thoai`, `gioi_tinh`, `email`, `noi_sinh`, `doi_tuong`, `khu_vuc`) VALUES
  ('001234567890', 'TS001', 'Nguyễn', 'Văn A', '2005-03-15', '0911111111', 'Nam', 'vana@gmail.com', 'Hà Nội', '1', 'KV1');

  INSERT INTO `xt_diemthixettuyen` (`cccd`, `sobaodanh`, `d_phuongthuc`, `TO`, `LI`, `HO`, `VA`, `TI`) VALUES
  ('001234567890', 'TS001', 'THPT', 9.2, 8.5, 8.8, 7.5, 8.0);

  INSERT INTO `xt_nguyenvongxettuyen` (`nn_cccd`, `nv_manganh`, `nv_tt`, `diem_thxt`, `diem_xettuyen`, `nv_ketqua`, `nv_keys`) VALUES
  ('001234567890', 'CNTT', 1, 26.50, 27.00, 'Chờ', '001234567890_CNTT_1');

  SET FOREIGN_KEY_CHECKS = 1;


