package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class GrowQuestionBean {
    private String link = "";
    private String title = "";
    private Integer growAnsNumD = 0;
    private Integer growAnsNumW = 0;
    private Integer growAnsNumM = 0;
    private Integer totalAnsNum = 0;
    private Integer totalComNum = 0;

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

    public Integer getTotalComNum() {
        return totalComNum;
    }

    public void setTotalComNum(Integer totalComNum) {
        this.totalComNum = totalComNum;
    }
}
