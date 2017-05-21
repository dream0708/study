package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年4月18日
 */
public enum EliteSNSType implements IEnum
{
	TYPE_GOODED(1, "被点赞"),
	TYPE_BADED(2, "被点踩"),
	TYPE_FAVORITED(3, "被收藏"),
	TYPE_COMMENTED(4, "被评论"),
	TYPE_FAVORITE(5, "收藏"),
	TYPE_FOLLOW(6, "关注"),
	TYPE_FOLLOWED(7, "被关注");
	
	private int value;
	private String desc;

	EliteSNSType(int value,String desc)
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
