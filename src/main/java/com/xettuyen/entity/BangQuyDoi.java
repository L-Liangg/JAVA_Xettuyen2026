package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_bangquydoi")
public class BangQuyDoi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idqd;

    private String d_phuongthuc;
    private String d_tohop;
    private String d_mon;
    private BigDecimal d_diema;
    private BigDecimal d_diemb;
    private BigDecimal d_diemc;
    private BigDecimal d_diemd;

    @Column(unique = true)
    private String d_maquydoi;

    private String d_phanvi;

    public Integer getIdqd() {
        return idqd;
    }

    public void setIdqd(Integer idqd) {
        this.idqd = idqd;
    }

    public String getD_phuongthuc() {
        return d_phuongthuc;
    }

    public void setD_phuongthuc(String d_phuongthuc) {
        this.d_phuongthuc = d_phuongthuc;
    }

    public String getD_tohop() {
        return d_tohop;
    }

    public void setD_tohop(String d_tohop) {
        this.d_tohop = d_tohop;
    }

    public String getD_mon() {
        return d_mon;
    }

    public void setD_mon(String d_mon) {
        this.d_mon = d_mon;
    }

    public BigDecimal getD_diema() {
        return d_diema;
    }

    public void setD_diema(BigDecimal d_diema) {
        this.d_diema = d_diema;
    }

    public BigDecimal getD_diemb() {
        return d_diemb;
    }

    public void setD_diemb(BigDecimal d_diemb) {
        this.d_diemb = d_diemb;
    }

    public BigDecimal getD_diemc() {
        return d_diemc;
    }

    public void setD_diemc(BigDecimal d_diemc) {
        this.d_diemc = d_diemc;
    }

    public BigDecimal getD_diemd() {
        return d_diemd;
    }

    public void setD_diemd(BigDecimal d_diemd) {
        this.d_diemd = d_diemd;
    }

    public String getD_maquydoi() {
        return d_maquydoi;
    }

    public void setD_maquydoi(String d_maquydoi) {
        this.d_maquydoi = d_maquydoi;
    }

    public String getD_phanvi() {
        return d_phanvi;
    }

    public void setD_phanvi(String d_phanvi) {
        this.d_phanvi = d_phanvi;
    }
}