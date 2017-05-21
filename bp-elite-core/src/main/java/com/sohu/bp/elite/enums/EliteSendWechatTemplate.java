package com.sohu.bp.elite.enums;

public enum EliteSendWechatTemplate implements IEnum{
	 NEW_INVITE_BY_PERSON(5, "邀请回答"),
	 NEW_ANSWER_BY_MYQUESTION(6, "您的问题有了新的回答");
	 
	 private int value;
	 private String desc;
	 
	 private EliteSendWechatTemplate(int value, String desc) {
		 this.value = value;
		 this.desc = desc;
	 }

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getDesc() {
		return desc;
	};
}
