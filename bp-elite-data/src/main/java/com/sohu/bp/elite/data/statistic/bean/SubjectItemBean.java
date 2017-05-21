package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class SubjectItemBean {
    private String link = "";
    private String title = "";
    private Integer pc_pv = 0;
    private Integer pc_uv = 0;
    private Integer mobile_pv = 0;
    private Integer mobile_uv = 0;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPc_pv() {
        return pc_pv;
    }

    public void setPc_pv(Integer pc_pv) {
        this.pc_pv = pc_pv;
    }

    public Integer getPc_uv() {
        return pc_uv;
    }

    public void setPc_uv(Integer pc_uv) {
        this.pc_uv = pc_uv;
    }

    public Integer getMobile_pv() {
        return mobile_pv;
    }

    public void setMobile_pv(Integer mobile_pv) {
        this.mobile_pv = mobile_pv;
    }

    public Integer getMobile_uv() {
        return mobile_uv;
    }

    public void setMobile_uv(Integer mobile_uv) {
        this.mobile_uv = mobile_uv;
    }

}
