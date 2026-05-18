package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_map_mon")
public class MapMon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mon_thpt", unique = true)
    private String mon_thpt;

    @Column(name = "mon_vsat")
    private String mon_vsat;

    @Column(name = "mon_dgnl")
    private String mon_dgnl;

    @Column(name = "ten_mon")
    private String ten_mon;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMon_thpt() {
        return mon_thpt;
    }

    public void setMon_thpt(String mon_thpt) {
        this.mon_thpt = mon_thpt;
    }

    public String getMon_vsat() {
        return mon_vsat;
    }

    public void setMon_vsat(String mon_vsat) {
        this.mon_vsat = mon_vsat;
    }

    public String getMon_dgnl() {
        return mon_dgnl;
    }

    public void setMon_dgnl(String mon_dgnl) {
        this.mon_dgnl = mon_dgnl;
    }

    public String getTen_mon() {
        return ten_mon;
    }

    public void setTen_mon(String ten_mon) {
        this.ten_mon = ten_mon;
    }
}
