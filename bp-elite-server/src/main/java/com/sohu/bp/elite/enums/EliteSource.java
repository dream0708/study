package com.sohu.bp.elite.enums;

public enum EliteSource implements IEnum
{
	SOURCE_PC_WEB(1, "pc"),
	SOURCE_MOBILE_WEB_BP(11, "移动web-bp"),
	SOURCE_MOBILE_CLIENT_BP(21, "bp移动客户端"),
	SOURCE_MOBILE_CLIENT_SOHUNEWS(22, "搜狐新闻客户端");
	
	
	private int value;
	private String desc;
	
	EliteSource(int value,String desc)
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