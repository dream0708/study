package com.sohu.bp.elite.action.bean.expert;

import java.util.List;

public class ExpertApplyBean {
    private String nick;
    private String avatar;
    private Integer provinceId;
    private Integer locationId;
    private String description;
    private List<Long> tag;
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    public Integer getProvinceId() {
        return provinceId;
    }
    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }
    public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Long> getTag() {
        return tag;
    }
    public void setTag(List<Long> tag) {
        this.tag = tag;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    } 
}
