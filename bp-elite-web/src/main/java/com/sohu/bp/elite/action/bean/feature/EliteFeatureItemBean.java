package com.sohu.bp.elite.action.bean.feature;

import java.io.Serializable;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.util.ImageUtil;

public class EliteFeatureItemBean implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -8355867512379405529L;
    private String id;
    private String name;
    private String brief;
    private String date;
    private Long updateTime;
    private String cover;
    private String smallCover;
    private String mediumCover;
    private String questionId;
    private Integer num;
    private Integer type;
    private UserDetailDisplayBean user;
    
    
    public String getCover() {
		return cover;
	}
	public EliteFeatureItemBean setCover(String cover) {
		this.cover = ImageUtil.removeImgProtocol(cover);
		this.smallCover = ImageUtil.getSmallImage(cover);
		this.mediumCover = ImageUtil.getSmallImage(cover, null, Constants.MEDIUM_IMAGE_RATIO);
		return this;
	}
	public String getSmallCover() {
		return smallCover;
	}
	public EliteFeatureItemBean setSmallCover(String smallCover) {
		this.smallCover = smallCover;
		return this;
	}
	public String getId() {
        return id;
    }
    public EliteFeatureItemBean setId(String id) {
        this.id = id;
        return this;
    }
    public String getName() {
        return name;
    }
    public EliteFeatureItemBean setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getBrief() {
        return brief;
    }
    public EliteFeatureItemBean setBrief(String brief) {
        this.brief = brief;
        return this;
    }
    public String getDate() {
        return date;
    }
    public EliteFeatureItemBean setDate(String date) {
        this.date = date;
        return this;
    }
    public Integer getNum() {
        return num;
    }
    public EliteFeatureItemBean setNum(Integer num) {
        this.num = num;
        return this;
    }
    public String getQuestionId() {
        return questionId;
    }
    public EliteFeatureItemBean setQuestionId(String questionId) {
        this.questionId = questionId;
        return this;
    }
    public String getMediumCover() {
        return mediumCover;
    }
    public EliteFeatureItemBean setMediumCover(String mediumCover) {
        this.mediumCover = mediumCover;
        return this;
    }
    public Long getUpdateTime() {
        return updateTime;
    }
    public EliteFeatureItemBean setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    public Integer getType() {
        return type;
    }
    public EliteFeatureItemBean setType(Integer type) {
        this.type = type;
        return this;
    }
    public UserDetailDisplayBean getUser() {
        return user;
    }
    public EliteFeatureItemBean setUser(UserDetailDisplayBean user) {
        this.user = user;
        return this;
    }
}
