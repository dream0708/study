package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016-08-16 11:46:47
 * TODO media 状态枚举类
 */
public enum EliteMediaStatus implements IEnum
{
	INVALID(1, "不可用"),
	WORK(2, "可用"),
	DELETE(3, "删除");
	
	private int value;
	private String desc;
	
	EliteMediaStatus(int value,String desc)
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