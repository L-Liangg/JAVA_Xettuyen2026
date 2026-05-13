package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nganh")
public class Nganh {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idnganh;

    private String manganh;
    private String tennganh;
    private String n_tohopgoc;
    private Integer n_chitieu;
    private BigDecimal n_diemsan;
    private BigDecimal n_diemtrungtuyen;
    private String n_tuyenthang;
    private String n_dgnl;
    private String n_thpt;
    private String n_vsat;
    private Integer sl_xtt, sl_dgnl, sl_vsat, sl_thpt;

    public Integer getIdnganh() {
        return idnganh;
    }

    public void setIdnganh(Integer idnganh) {
        this.idnganh = idnganh;
    }

    public String getManganh() {
        return manganh;
    }

    public void setManganh(String manganh) {
        this.manganh = manganh;
    }

    public String getTennganh() {
        return tennganh;
    }

    public void setTennganh(String tennganh) {
        this.tennganh = tennganh;
    }

    public String getN_tohopgoc() {
        return n_tohopgoc;
    }

    public void setN_tohopgoc(String n_tohopgoc) {
        this.n_tohopgoc = n_tohopgoc;
    }

    public Integer getN_chitieu() {
        return n_chitieu;
    }

    public void setN_chitieu(Integer n_chitieu) {
        this.n_chitieu = n_chitieu;
    }

    public BigDecimal getN_diemsan() {
        return n_diemsan;
    }

    public void setN_diemsan(BigDecimal n_diemsan) {
        this.n_diemsan = n_diemsan;
    }

    public BigDecimal getN_diemtrungtuyen() {
        return n_diemtrungtuyen;
    }

    public void setN_diemtrungtuyen(BigDecimal n_diemtrungtuyen) {
        this.n_diemtrungtuyen = n_diemtrungtuyen;
    }

    public String getN_tuyenthang() {
        return n_tuyenthang;
    }

    public void setN_tuyenthang(String n_tuyenthang) {
        this.n_tuyenthang = n_tuyenthang;
    }

    public String getN_dgnl() {
        return n_dgnl;
    }

    public void setN_dgnl(String n_dgnl) {
        this.n_dgnl = n_dgnl;
    }

    public String getN_thpt() {
        return n_thpt;
    }

    public void setN_thpt(String n_thpt) {
        this.n_thpt = n_thpt;
    }

    public String getN_vsat() {
        return n_vsat;
    }

    public void setN_vsat(String n_vsat) {
        this.n_vsat = n_vsat;
    }

    public Integer getSl_xtt() {
        return sl_xtt;
    }

    public void setSl_xtt(Integer sl_xtt) {
        this.sl_xtt = sl_xtt;
    }

    public Integer getSl_dgnl() {
        return sl_dgnl;
    }

    public void setSl_dgnl(Integer sl_dgnl) {
        this.sl_dgnl = sl_dgnl;
    }

    public Integer getSl_vsat() {
        return sl_vsat;
    }

    public void setSl_vsat(Integer sl_vsat) {
        this.sl_vsat = sl_vsat;
    }

    public Integer getSl_thpt() {
        return sl_thpt;
    }

    public void setSl_thpt(Integer sl_thpt) {
        this.sl_thpt = sl_thpt;
    }
}