package com.sohu.bp.elite.enums;

public enum EliteVSAttitudeType implements IEnum{
	POS(1, "赞成"),
	NEG(2, "反对");
	
	private int value;
	private String desc;
	
	private EliteVSAttitudeType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	@Override
	public int getValue() {
		return this.value;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

}
