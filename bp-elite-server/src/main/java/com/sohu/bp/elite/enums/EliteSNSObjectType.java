package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年4月19日
 */
public enum EliteSNSObjectType implements IEnum
{
	TYPE_STORY(1, "故事"),
	TYPE_COMMENT(2, "评论"),
	TYPE_ANSWER(3, "回答"),
	TYPE_USER(4, "用户"),
	TYPE_ALBUM(5, "专辑"),
	TYPE_TAG(6, "标签"),
	TYPE_TOPIC(7, "话题");
	
	private int value;
	private String desc;

	EliteSNSObjectType(int value,String desc)
	{
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
