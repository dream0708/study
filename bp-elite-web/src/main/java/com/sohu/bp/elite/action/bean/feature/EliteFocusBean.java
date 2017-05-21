package com.sohu.bp.elite.action.bean.feature;

/**
 * 用于头图展示
 * 
 * @author zhijungou 2017年3月30日
 */
public class EliteFocusBean {
    private Integer bpType;
    private String id;
    private String cover;
    private String coverSmall;
    private String title;

    public Integer getBpType() {
        return bpType;
    }

    public EliteFocusBean setBpType(Integer bpType) {
        this.bpType = bpType;
        return this;
    }

    public String getId() {
        return id;
    }

    public EliteFocusBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public EliteFocusBean setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public EliteFocusBean setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EliteFocusBean setTitle(String title) {
        this.title = title;
        return this;
    }

}
