package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016-07-28 11:58:56
 * 访问来源枚举类型
 */
public enum AgentSource implements IEnum
{
	UNKNOWN(0, "未知"),
	PC(1, "PC端"),
	MOBILE(10, "移动端"),
	WECHAT(20, "微信小程序"),
	CRAWL(30, "抓取"),
    APP(40, "应用");
	
	private int value;
	private String desc;
	
	AgentSource(int value,String desc)
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