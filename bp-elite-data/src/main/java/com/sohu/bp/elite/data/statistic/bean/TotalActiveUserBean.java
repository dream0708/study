package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class TotalActiveUserBean {
    private String link = "";
    private String name = "";
    private Integer growQuesD = 0;
    private Integer totalQues = 0;
    private Integer growAnsD = 0;
    private Integer totalAns = 0;
    private Integer fansNum = 0;

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

    public Integer getGrowQuesD() {
        return growQuesD;
    }

    public void setGrowQuesD(Integer growQuesD) {
        this.growQuesD = growQuesD;
    }

    public Integer getTotalQues() {
        return totalQues;
    }

    public void setTotalQues(Integer totalQues) {
        this.totalQues = totalQues;
    }

    public Integer getGrowAnsD() {
        return growAnsD;
    }

    public void setGrowAnsD(Integer growAnsD) {
        this.growAnsD = growAnsD;
    }

    public Integer getTotalAns() {
        return totalAns;
    }

    public void setTotalAns(Integer totalAns) {
        this.totalAns = totalAns;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public void setFansNum(Integer fansNum) {
        this.fansNum = fansNum;
    }
}
