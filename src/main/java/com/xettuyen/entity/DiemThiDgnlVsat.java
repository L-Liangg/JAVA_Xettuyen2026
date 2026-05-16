package com.xettuyen.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemthi_dgnl_vsat")
public class DiemThiDgnlVsat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cccd;
    @Column(nullable = false)
    private String dot_thi;
    @Column(nullable = false)
    private String ma_dot_thi;
    private String ngay_thi;
    private Integer nam;
    private String ma_mon;
    private String ten_mon;
    private BigDecimal diem;
    private String thang_diem;
    private String ma_dvtctdl;
    private String ten_dvtctdl;

    @Column(unique = true)
    private String dv_keys;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getDot_thi() {
        return dot_thi;
    }

    public void setDot_thi(String dot_thi) {
        this.dot_thi = dot_thi;
    }

    public String getMa_dot_thi() {
        return ma_dot_thi;
    }

    public void setMa_dot_thi(String ma_dot_thi) {
        this.ma_dot_thi = ma_dot_thi;
    }

    public String getNgay_thi() {
        return ngay_thi;
    }

    public void setNgay_thi(String ngay_thi) {
        this.ngay_thi = ngay_thi;
    }

    public Integer getNam() {
        return nam;
    }

    public void setNam(Integer nam) {
        this.nam = nam;
    }

    public String getMa_mon() {
        return ma_mon;
    }

    public void setMa_mon(String ma_mon) {
        this.ma_mon = ma_mon;
    }

    public String getTen_mon() {
        return ten_mon;
    }

    public void setTen_mon(String ten_mon) {
        this.ten_mon = ten_mon;
    }

    public BigDecimal getDiem() {
        return diem;
    }

    public void setDiem(BigDecimal diem) {
        this.diem = diem;
    }

    public String getThang_diem() {
        return thang_diem;
    }

    public void setThang_diem(String thang_diem) {
        this.thang_diem = thang_diem;
    }

    public String getMa_dvtctdl() {
        return ma_dvtctdl;
    }

    public void setMa_dvtctdl(String ma_dvtctdl) {
        this.ma_dvtctdl = ma_dvtctdl;
    }

    public String getTen_dvtctdl() {
        return ten_dvtctdl;
    }

    public void setTen_dvtctdl(String ten_dvtctdl) {
        this.ten_dvtctdl = ten_dvtctdl;
    }

    public String getDv_keys() {
        return dv_keys;
    }

    public void setDv_keys(String dv_keys) {
        this.dv_keys = dv_keys;
    }
}
