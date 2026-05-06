package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemthixettuyen")
public class DiemThi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer iddiemthi;

    @Column(unique = true)
    private String cccd;

    private String sobaodanh;
    private String d_phuongthuc;

    @Column(name = "`TO`")
    private BigDecimal TO;

    private BigDecimal LI;
    private BigDecimal HO;
    private BigDecimal SI;
    private BigDecimal SU;
    private BigDecimal DI;
    private BigDecimal VA;
    private BigDecimal N1_THI, N1_CC, CNCN, CNNN, TI, KTPL;
    private BigDecimal NL1, NK1, NK2;

    public Integer getIddiemthi() {
        return iddiemthi;
    }

    public void setIddiemthi(Integer iddiemthi) {
        this.iddiemthi = iddiemthi;
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

    public String getD_phuongthuc() {
        return d_phuongthuc;
    }

    public void setD_phuongthuc(String d_phuongthuc) {
        this.d_phuongthuc = d_phuongthuc;
    }

    public BigDecimal getTO() {
        return TO;
    }

    public void setTO(BigDecimal TO) {
        this.TO = TO;
    }

    public BigDecimal getLI() {
        return LI;
    }

    public void setLI(BigDecimal LI) {
        this.LI = LI;
    }

    public BigDecimal getHO() {
        return HO;
    }

    public void setHO(BigDecimal HO) {
        this.HO = HO;
    }

    public BigDecimal getSI() {
        return SI;
    }

    public void setSI(BigDecimal SI) {
        this.SI = SI;
    }

    public BigDecimal getSU() {
        return SU;
    }

    public void setSU(BigDecimal SU) {
        this.SU = SU;
    }

    public BigDecimal getDI() {
        return DI;
    }

    public void setDI(BigDecimal DI) {
        this.DI = DI;
    }

    public BigDecimal getVA() {
        return VA;
    }

    public void setVA(BigDecimal VA) {
        this.VA = VA;
    }

    public BigDecimal getN1_THI() {
        return N1_THI;
    }

    public void setN1_THI(BigDecimal n1_THI) {
        N1_THI = n1_THI;
    }

    public BigDecimal getN1_CC() {
        return N1_CC;
    }

    public void setN1_CC(BigDecimal n1_CC) {
        N1_CC = n1_CC;
    }

    public BigDecimal getCNCN() {
        return CNCN;
    }

    public void setCNCN(BigDecimal CNCN) {
        this.CNCN = CNCN;
    }

    public BigDecimal getCNNN() {
        return CNNN;
    }

    public void setCNNN(BigDecimal CNNN) {
        this.CNNN = CNNN;
    }

    public BigDecimal getTI() {
        return TI;
    }

    public void setTI(BigDecimal TI) {
        this.TI = TI;
    }

    public BigDecimal getKTPL() {
        return KTPL;
    }

    public void setKTPL(BigDecimal KTPL) {
        this.KTPL = KTPL;
    }

    public BigDecimal getNL1() {
        return NL1;
    }

    public void setNL1(BigDecimal NL1) {
        this.NL1 = NL1;
    }

    public BigDecimal getNK1() {
        return NK1;
    }

    public void setNK1(BigDecimal NK1) {
        this.NK1 = NK1;
    }

    public BigDecimal getNK2() {
        return NK2;
    }

    public void setNK2(BigDecimal NK2) {
        this.NK2 = NK2;
    }
}