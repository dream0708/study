package com.sohu.bp.elite.enums;

public enum EliteFragmentType implements IEnum {
	NAVLABEL(1,"导航标签");
	
	private Integer value;
	private String desc;
	
	EliteFragmentType(Integer value, String desc) {
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
