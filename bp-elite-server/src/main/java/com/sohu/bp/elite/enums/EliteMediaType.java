package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016-08-16 11:37:47
 * TODO media 类型枚举类
 */
public enum EliteMediaType implements IEnum
{
	IMAGE(1, "图片"),
	VIDEO(2, "视频");
	
	private int value;
	private String desc;
	
	EliteMediaType(int value,String desc)
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