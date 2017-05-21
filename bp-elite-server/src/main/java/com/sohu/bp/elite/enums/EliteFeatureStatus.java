package com.sohu.bp.elite.enums;

public enum EliteFeatureStatus implements IEnum{
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(2, "无效");
	
	private int value;
	private String desc;
	
	private EliteFeatureStatus(int value, String desc) {
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
