package com.sohu.bp.elite.action.bean.feedItem;

import com.sohu.bp.elite.enums.BpUserType;

/**
 * Created by nicholastang on 2017/3/21.
 */
public class ProduceBean {
    private String produceText = "";
    private String produceLink = "";
    private String produceAvatar = "";
    private String bpIdOrigin = "";
    private String companyIdOrigin = "";
    private Integer bpUserType = BpUserType.NORMAL_USER.getValue();
    private Integer produceIdentity = 0;
    private String produceIdentityString = "00";
    public String getProduceText() {
        return produceText;
    }

    public void setProduceText(String produceText) {
        this.produceText = produceText;
    }

    public String getProduceAvatar() {
        return produceAvatar;
    }

    public void setProduceAvatar(String produceAvatar) {
        this.produceAvatar = produceAvatar;
    }

    public String getProduceLink() {
        return produceLink;
    }

    public void setProduceLink(String produceLink) {
        this.produceLink = produceLink;
    }

    public Integer getProduceIdentity() {
        return produceIdentity;
    }

    public void setProduceIdentity(Integer produceIdentity) {
        this.produceIdentity = produceIdentity;
    }

    public String getProduceIdentityString() {
        return produceIdentityString;
    }

    public void setProduceIdentityString(String produceIdentityString) {
        this.produceIdentityString = produceIdentityString;
    }

    public String getBpIdOrigin() {
        return bpIdOrigin;
    }

    public void setBpIdOrigin(String bpIdOrigin) {
        this.bpIdOrigin = bpIdOrigin;
    }

    public String getCompanyIdOrigin() {
        return companyIdOrigin;
    }

    public void setCompanyIdOrigin(String companyIdOrigin) {
        this.companyIdOrigin = companyIdOrigin;
    }

    public Integer getBpUserType() {
        return bpUserType;
    }

    public void setBpUserType(Integer bpUserType) {
        this.bpUserType = bpUserType;
    }
}
