package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月29日
 */
public enum EliteSpecialTopicStatus implements IEnum
{
	STATUS_EXPIRE(3, "过期"),
	STATUS_PRE_WORK(2, "预发布"),
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(0, "无效");
	
	private int value;
	private String desc;
	
	EliteSpecialTopicStatus(int value,String desc)
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