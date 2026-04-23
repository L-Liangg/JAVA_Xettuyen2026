package com.xettuyen.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nganh_tohop")
public class NganhToHop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String manganh;
    private String matohop;
    private String th_mon1;
    private Byte hsmon1;
    private String th_mon2;
    private Byte hsmon2;
    private String th_mon3;
    private Byte hsmon3;

    @Column(unique = true)
    private String tb_keys;

    @Column(name="`TO`")
    private Boolean TO;
    private Boolean N1, LI, HO, SI, VA, SU, DI, TI, KHAC, KTPL;
    private BigDecimal dolech;

    public Byte getHsmon1() {
        return hsmon1;
    }

    public void setHsmon1(Byte hsmon1) {
        this.hsmon1 = hsmon1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTh_mon1() {
        return th_mon1;
    }

    public void setTh_mon1(String th_mon1) {
        this.th_mon1 = th_mon1;
    }

    public String getTh_mon2() {
        return th_mon2;
    }

    public void setTh_mon2(String th_mon2) {
        this.th_mon2 = th_mon2;
    }

    public Byte getHsmon2() {
        return hsmon2;
    }

    public void setHsmon2(Byte hsmon2) {
        this.hsmon2 = hsmon2;
    }

    public String getTh_mon3() {
        return th_mon3;
    }

    public void setTh_mon3(String th_mon3) {
        this.th_mon3 = th_mon3;
    }

    public Byte getHsmon3() {
        return hsmon3;
    }

    public void setHsmon3(Byte hsmon3) {
        this.hsmon3 = hsmon3;
    }

    public String getTb_keys() {
        return tb_keys;
    }

    public void setTb_keys(String tb_keys) {
        this.tb_keys = tb_keys;
    }

    public Boolean getN1() {
        return N1;
    }

    public void setN1(Boolean n1) {
        N1 = n1;
    }

    public Boolean getTO() {
        return TO;
    }

    public void setTO(Boolean TO) {
        this.TO = TO;
    }

    public Boolean getLI() {
        return LI;
    }

    public void setLI(Boolean LI) {
        this.LI = LI;
    }

    public Boolean getHO() {
        return HO;
    }

    public void setHO(Boolean HO) {
        this.HO = HO;
    }

    public Boolean getSI() {
        return SI;
    }

    public void setSI(Boolean SI) {
        this.SI = SI;
    }

    public Boolean getVA() {
        return VA;
    }

    public void setVA(Boolean VA) {
        this.VA = VA;
    }

    public Boolean getSU() {
        return SU;
    }

    public void setSU(Boolean SU) {
        this.SU = SU;
    }

    public Boolean getDI() {
        return DI;
    }

    public void setDI(Boolean DI) {
        this.DI = DI;
    }

    public Boolean getTI() {
        return TI;
    }

    public void setTI(Boolean TI) {
        this.TI = TI;
    }

    public Boolean getKHAC() {
        return KHAC;
    }

    public void setKHAC(Boolean KHAC) {
        this.KHAC = KHAC;
    }

    public Boolean getKTPL() {
        return KTPL;
    }

    public void setKTPL(Boolean KTPL) {
        this.KTPL = KTPL;
    }

    public BigDecimal getDolech() {
        return dolech;
    }

    public void setDolech(BigDecimal dolech) {
        this.dolech = dolech;
    }
}