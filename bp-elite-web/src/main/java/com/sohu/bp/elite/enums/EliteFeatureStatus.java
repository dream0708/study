package com.sohu.bp.elite.enums;

public enum EliteFeatureStatus implements IEnum {
	VALID(1,"有效"),
	INVALID(2,"无效");

	int value;
	String desc;
	
	EliteFeatureStatus(int value, String desc)
	{
		this.value = value;
		this.desc = desc;
	}
	
	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
