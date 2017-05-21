package com.sohu.bp.elite.action.bean.feature;

import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.util.ImageUtil;

/**
 * 问答专栏的抽象基类
 * @author zhijungou
 * 2017年1月10日
 */
public abstract class AbstractEliteColumn extends PageBean{
	private String id;
	private String name;
	private String cover;
	private String wapCover;
	private String brief;
	private String description;
	private String publishTime;
	private UserDetailDisplayBean userInfo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = ImageUtil.removeImgProtocol(cover);
		setWapCover(ImageUtil.getWapFocusCover(cover));
	}
	public String getWapCover() {
		return wapCover;
	}
	public void setWapCover(String wapCover) {
		this.wapCover = wapCover;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UserDetailDisplayBean getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserDetailDisplayBean userInfo) {
		this.userInfo = userInfo;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
}
