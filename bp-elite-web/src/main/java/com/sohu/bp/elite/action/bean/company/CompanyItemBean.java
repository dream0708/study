package com.sohu.bp.elite.action.bean.company;

import com.sohu.bp.elite.util.ImageUtil;

/**
 * 
 * @author nicholastang
 * 2016-09-06 19:02:06
 * TODO
 */
public class CompanyItemBean
{
	private String id = "";
	private String name = "";
	private String avatar = "";
	private String homeUrl = "";
	private String phone = "";
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = ImageUtil.removeImgProtocol(avatar);
	}
	public String getHomeUrl() {
		return homeUrl;
	}
	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}