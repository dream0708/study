package com.sohu.bp.elite.enums;

public enum EliteFragmentStatus implements IEnum {
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(2, "无效");
	
	private int value;
	private String desc;
	
	EliteFragmentStatus(int value, String desc)
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
	public void setValue(int value) {
		this.value = value;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

}
