package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月28日
 */
public enum EliteReferenceType implements IEnum
{
	TYPE_USER(1, "用户"),
	TYPE_ALBUM(2, "专辑"),
	TYPE_TAG(3, "标签"),
	TYPE_TOPIC(4, "话题"),
	TYPE_STORY(5, "小短文");
	
	private int value;
	private String desc;
	
	EliteReferenceType(int value,String desc)
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