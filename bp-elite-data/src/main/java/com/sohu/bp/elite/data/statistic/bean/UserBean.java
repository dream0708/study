package com.sohu.bp.elite.data.statistic.bean;

import java.util.Date;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class UserBean {
    private String link = "";
    private String title = "";
    private int from = 0;
    private String registerTime = "";
    private Integer pv = 0;
    private Integer uv = 0;
    private Integer fansNum = 0;

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

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getUv() {
        return uv;
    }

    public void setUv(Integer uv) {
        this.uv = uv;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }

    public int getOriginFrom(){
        return from;
    }
    public String getFrom() {
        switch (from){
            case 1:
                return "PC";
            case 10:
                return "移动端";
            case 20:
                return "微信小程序";
            case 30:
                return "抓取";
            default:
                return "未知";
        }
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}
