package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_tohop_monthi")
public class ToHopMon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtohop;

    @Column(unique = true)
    private String matohop;

    private String mon1, mon2, mon3;
    private String tentohop;

    public Integer getIdtohop() {
        return idtohop;
    }

    public void setIdtohop(Integer idtohop) {
        this.idtohop = idtohop;
    }

    public String getMatohop() {
        return matohop;
    }

    public void setMatohop(String matohop) {
        this.matohop = matohop;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public String getTentohop() {
        return tentohop;
    }

    public void setTentohop(String tentohop) {
        this.tentohop = tentohop;
    }
}