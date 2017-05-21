package com.sohu.bp.elite.enums;
/**
 * @author limingcai
 * 2016年3月14日 下午6:00:37
 *
 */
public enum EliteImageStatus implements IEnum {
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(0, "无效");
	
	private int value;
	private String desc;
	
	private EliteImageStatus(int value, String desc) {
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
