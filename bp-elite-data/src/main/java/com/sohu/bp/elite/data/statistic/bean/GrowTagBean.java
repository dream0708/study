package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class GrowTagBean {
    private String link = "";
    private String name = "";
    private Integer growAnsNumD = 0;
    private Integer growAnsNumW = 0;
    private Integer growAnsNumM = 0;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGrowAnsNumD() {
        return growAnsNumD;
    }

    public void setGrowAnsNumD(Integer growAnsNumD) {
        this.growAnsNumD = growAnsNumD;
    }

    public Integer getGrowAnsNumW() {
        return growAnsNumW;
    }

    public void setGrowAnsNumW(Integer growAnsNumW) {
        this.growAnsNumW = growAnsNumW;
    }

    public Integer getGrowAnsNumM() {
        return growAnsNumM;
    }

    public void setGrowAnsNumM(Integer growAnsNumM) {
        this.growAnsNumM = growAnsNumM;
    }

    public Integer getTotalAnsNum() {
        return totalAnsNum;
    }

    public void setTotalAnsNum(Integer totalAnsNum) {
        this.totalAnsNum = totalAnsNum;
    }

    private Integer totalAnsNum;

}
