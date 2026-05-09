package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemcongxetuyen")
public class DiemCong {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddiemcong;
    private String ts_cccd;
    private String manganh;
    private String matohop;
    private String phuongthuc;
    private BigDecimal diemCC;
    private BigDecimal diemUtxt;
    private BigDecimal diemTong;
    private String ghichu;

    @Column(unique = true)
    private String dc_keys;

    public Integer getIddiemcong() {
        return iddiemcong;
    }

    public void setIddiemcong(Integer iddiemcong) {
        this.iddiemcong = iddiemcong;
    }

    public String getTs_cccd() {
        return ts_cccd;
    }

    public void setTs_cccd(String ts_cccd) {
        this.ts_cccd = ts_cccd;
    }

    public String getManganh() {
        return manganh;
    }

    public void setManganh(String manganh) {
        this.manganh = manganh;
    }

    public String getMatohop() {
        return matohop;
    }

    public void setMatohop(String matohop) {
        this.matohop = matohop;
    }

    public String getPhuongthuc() {
        return phuongthuc;
    }

    public void setPhuongthuc(String phuongthuc) {
        this.phuongthuc = phuongthuc;
    }

    public BigDecimal getDiemCC() {
        return diemCC;
    }

    public void setDiemCC(BigDecimal diemCC) {
        this.diemCC = diemCC;
    }

    public BigDecimal getDiemUtxt() {
        return diemUtxt;
    }

    public void setDiemUtxt(BigDecimal diemUtxt) {
        this.diemUtxt = diemUtxt;
    }

    public BigDecimal getDiemTong() {
        return diemTong;
    }

    public void setDiemTong(BigDecimal diemTong) {
        this.diemTong = diemTong;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getDc_keys() {
        return dc_keys;
    }

    public void setDc_keys(String dc_keys) {
        this.dc_keys = dc_keys;
    }
}