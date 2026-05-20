package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class NguyenVong {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idnv;

    private String nn_cccd;
    private String nv_manganh;
    private Integer nv_tt;
    private BigDecimal diem_thxt;
    private BigDecimal diem_utqd;
    private BigDecimal diem_cong;
    private BigDecimal diem_xettuyen;
    private String nv_ketqua;

    @Column(unique = true)
    private String nv_keys;

    private String tt_phuongthuc;
    private String tt_thm;

    public Integer getIdnv() {
        return idnv;
    }

    public void setIdnv(Integer idnv) {
        this.idnv = idnv;
    }

    public String getNn_cccd() {
        return nn_cccd;
    }

    public void setNn_cccd(String nn_cccd) {
        this.nn_cccd = nn_cccd;
    }

    public String getNv_manganh() {
        return nv_manganh;
    }

    public void setNv_manganh(String nv_manganh) {
        this.nv_manganh = nv_manganh;
    }

    public Integer getNv_tt() {
        return nv_tt;
    }

    public void setNv_tt(Integer nv_tt) {
        this.nv_tt = nv_tt;
    }

    public BigDecimal getDiem_thxt() {
        return diem_thxt;
    }

    public void setDiem_thxt(BigDecimal diem_thxt) {
        this.diem_thxt = diem_thxt;
    }

    public BigDecimal getDiem_utqd() {
        return diem_utqd;
    }

    public void setDiem_utqd(BigDecimal diem_utqd) {
        this.diem_utqd = diem_utqd;
    }

    public BigDecimal getDiem_cong() {
        return diem_cong;
    }

    public void setDiem_cong(BigDecimal diem_cong) {
        this.diem_cong = diem_cong;
    }

    public BigDecimal getDiem_xettuyen() {
        return diem_xettuyen;
    }

    public void setDiem_xettuyen(BigDecimal diem_xettuyen) {
        this.diem_xettuyen = diem_xettuyen;
    }

    public String getNv_ketqua() {
        return nv_ketqua;
    }

    public void setNv_ketqua(String nv_ketqua) {
        this.nv_ketqua = nv_ketqua;
    }

    public String getNv_keys() {
        return nv_keys;
    }

    public void setNv_keys(String nv_keys) {
        this.nv_keys = nv_keys;
    }

    public String getTt_phuongthuc() {
        return tt_phuongthuc;
    }

    public void setTt_phuongthuc(String tt_phuongthuc) {
        this.tt_phuongthuc = tt_phuongthuc;
    }

    public String getTt_thm() {
        return tt_thm;
    }

    public void setTt_thm(String tt_thm) {
        this.tt_thm = tt_thm;
    }
}