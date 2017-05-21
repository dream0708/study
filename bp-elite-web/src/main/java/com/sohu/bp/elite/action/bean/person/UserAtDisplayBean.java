package com.sohu.bp.elite.action.bean.person;

/**
 * Created by nicholastang on 2017/3/28.
 */
public class UserAtDisplayBean {
    private String bpId;
    private String avatar;
    private String nick;
    private String homeUrl;
    private Integer identity;
    private String identityString;
    private String description;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBpId() {
        return bpId;
    }

    public void setBpId(String bpId) {
        this.bpId = bpId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getIdentityString() {
        return identityString;
    }

    public void setIdentityString(String identityString) {
        this.identityString = identityString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
