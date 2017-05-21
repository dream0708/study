package com.sohu.bp.elite.action.bean.person;

import java.io.Serializable;

import com.sohu.bp.elite.util.ImageUtil;

/**
 * 
 * @author zhijungou
 * 2016年10月24日
 * 用于用户邀请的展示
 */
public class UserInviteDisplayBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String bpId = "";
	private String description = "";
	private String nick = "";
	private String avatar = "";
	private Boolean authenticated = false;
	private Boolean invited = false;
	private String identityString = "";
	
	public String getBpId() {
		return bpId;
	}
	public void setBpId(String bpId) {
		this.bpId = bpId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = ImageUtil.removeImgProtocol(avatar);
	}
	public Boolean getAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(Boolean authenticated) {
		this.authenticated = authenticated;
	}
	public Boolean getInvited() {
		return invited;
	}
	public void setInvited(Boolean invited) {
		this.invited = invited;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    public String getIdentityString() {
        return identityString;
    }
    public void setIdentityString(String identityString) {
        this.identityString = identityString;
    }
	
}
