package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/11/10.
 */
public class ActiveUserBean {
    private String link = "";
    private String name = "";
    private Integer growQAD = 0;
    private Integer growQAW = 0;
    private Integer growQAM = 0;
    private Integer totalQA = 0;
    private Integer growLikedD = 0;
    private Integer growSharedD = 0;

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

    public Integer getGrowQAD() {
        return growQAD;
    }

    public void setGrowQAD(Integer growQAD) {
        this.growQAD = growQAD;
    }

    public Integer getGrowQAW() {
        return growQAW;
    }

    public void setGrowQAW(Integer growQAW) {
        this.growQAW = growQAW;
    }

    public Integer getGrowQAM() {
        return growQAM;
    }

    public void setGrowQAM(Integer growQAM) {
        this.growQAM = growQAM;
    }

    public Integer getTotalQA() {
        return totalQA;
    }

    public void setTotalQA(Integer totalQA) {
        this.totalQA = totalQA;
    }

    public Integer getGrowLikedD() {
        return growLikedD;
    }

    public void setGrowLikedD(Integer growLikedD) {
        this.growLikedD = growLikedD;
    }

    public Integer getGrowSharedD() {
        return growSharedD;
    }

    public void setGrowSharedD(Integer growSharedD) {
        this.growSharedD = growSharedD;
    }
}
