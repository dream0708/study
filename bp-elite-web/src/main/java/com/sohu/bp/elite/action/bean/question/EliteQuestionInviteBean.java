package com.sohu.bp.elite.action.bean.question;

/**
 * 用于各种场景下邀请回答的javabean
 * @author zhijungou
 * 2016年12月21日
 */
public class EliteQuestionInviteBean {
	private String invitedUserId;
	private String specialId;
	private Integer specialType;
	
	public String getInvitedUserId() {
		return invitedUserId;
	}
	public void setInvitedUserId(String invitedUserId) {
		this.invitedUserId = invitedUserId;
	}
	public String getSpecialId() {
		return specialId;
	}
	public void setSpecialId(String specialId) {
		this.specialId = specialId;
	}
	public Integer getSpecialType() {
		return specialType;
	}
	public void setSpecialType(Integer specialType) {
		this.specialType = specialType;
	}
}
