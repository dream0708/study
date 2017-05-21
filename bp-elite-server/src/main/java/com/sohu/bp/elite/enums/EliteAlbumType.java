package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月28日
 */
public enum EliteAlbumType implements IEnum
{
	TYPE_PUBLIC(1, "公开"),
	TYPE_PROTECT(2, "仅好友及关注的人可见"),
	TYPE_PRIVATE(3, "仅自己可见");
	
	private int value;
	private String desc;
	
	EliteAlbumType(int value,String desc)
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