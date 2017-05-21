package com.sohu.bp.elite.api.enums;

public enum EliteMessageType implements IEnum
{
	INBOX(1, "站内信"),
	CELLPHONE(2, "手机"),
	EMAIL(3, "电子邮箱");
	
	int value;
	String desc;
	
	EliteMessageType(int value, String desc)
	{
		this.value = value;
		this.desc = desc;
	}
	
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}
}