package com.sohu.bp.elite.enums;

import net.sf.json.JSONObject;

//问答用户限制枚举类
public enum EliteQAErrorCode implements IEnum{
	USER_PHONE_UNBOUND(1, "您未绑定手机号"),
	USER_RESTRICT_QUESION(2, "您已达问题上限"),
	USER_RESTRICT_ANSWER(3, "您已达回答上限"),
	USER_INBLACKLIST(4, "您已被屏蔽"),
	QUESTION_TITLE_BLANK(5, "您的问题标题为空"),
	ANSWER_QUESTION_INVALID(6, "该问题已关闭"),
	ANSWER_CONTENT_BLANK(7, "您的回答内容为空"),
	ANSWER_ALREADY_ANSWERD(8, "您已回答过该问题"),
    ANSWER_ALREADY_CHOOSED(9, "您已经进行了选择");
	
	public int value;
	public String desc;
	
	private EliteQAErrorCode(int value, String desc) {
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
	}
	@Override
	public String toString(){
		return "{\"value\":" + this.value + ",\"desc\":\"" + this.desc +"\"}";
	}
}

