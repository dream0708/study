package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月11日
 */
public enum EliteStoryOriginal implements IEnum
{
	FROM_ELITE(1, "来自elite"),
	FROM_MP(2, "来自mp");
	
	private int value;
	private String desc;
	
	EliteStoryOriginal(int value,String desc)
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