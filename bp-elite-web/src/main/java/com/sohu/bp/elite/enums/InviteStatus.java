package com.sohu.bp.elite.enums;

public enum InviteStatus {
	INVITE_ERROR(0, "邀请失败"),
	INVITE_ALREADY(1, "已邀请"),
	INVITE_NOT_YET(2, "未邀请");
	
	int value;
	String desc;
	
	private InviteStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public int getValue(){
		return this.value;
	}
}
