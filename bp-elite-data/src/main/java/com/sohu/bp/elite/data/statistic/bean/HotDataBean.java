package com.sohu.bp.elite.data.statistic.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicholastang on 2016/11/14.
 * 热度访问数据
 */
public class HotDataBean {
    private String url = "";
    private Integer pv = 0;
    private Integer uv = 0;
    private Map<String, String> params = new HashMap<String, String>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
