package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月11日
 */
public enum EliteUserInfoStatus implements IEnum
{	STATUS_INIT(2, "初始化"),
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(0, "无效");
	
	private int value;
	private String desc;
	
	EliteUserInfoStatus(int value,String desc)
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