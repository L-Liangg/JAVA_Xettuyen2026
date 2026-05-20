package com.xettuyen.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "xt_thisinhxettuyen25")
public class ThiSinh {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idthisinh;

    @Column(unique = true)
    private String cccd;

    private String sobaodanh;
    private String ho;
    private String ten;
    private String ngay_sinh;
    private String dien_thoai;
    private String password;
    private String gioi_tinh;
    private String email;
    private String noi_sinh;
    private LocalDate updated_at;
    private String doi_tuong;
    private String khu_vuc;

    public Integer getIdthisinh() {
        return idthisinh;
    }

    public void setIdthisinh(Integer idthisinh) {
        this.idthisinh = idthisinh;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSobaodanh() {
        return sobaodanh;
    }

    public void setSobaodanh(String sobaodanh) {
        this.sobaodanh = sobaodanh;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(String ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }

    public String getDien_thoai() {
        return dien_thoai;
    }

    public void setDien_thoai(String dien_thoai) {
        this.dien_thoai = dien_thoai;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoi_sinh() {
        return noi_sinh;
    }

    public void setNoi_sinh(String noi_sinh) {
        this.noi_sinh = noi_sinh;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    public String getDoi_tuong() {
        return doi_tuong;
    }

    public void setDoi_tuong(String doi_tuong) {
        this.doi_tuong = doi_tuong;
    }

    public String getKhu_vuc() {
        return khu_vuc;
    }

    public void setKhu_vuc(String khu_vuc) {
        this.khu_vuc = khu_vuc;
    }
}