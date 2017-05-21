package com.sohu.bp.elite.data.statistic.bean;

/**
 * Created by nicholastang on 2016/12/7.
 */
public class SourceBean {
    private String desc = "";
    private String source = "";
    private String pos = "";
    private Integer pv = 0;
    private Integer uv = 0;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
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
}
