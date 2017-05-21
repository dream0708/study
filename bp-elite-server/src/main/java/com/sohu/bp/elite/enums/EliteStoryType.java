package com.sohu.bp.elite.enums;
/**
 * @author limingcai
 * 2016年3月14日 下午4:57:42
 *
 */
public enum EliteStoryType implements IEnum {
	TYPE_NORMAL(0, "普通"),
	TYPE_ANSWER(1, "回答");
	
	private int value;
	private String desc;
	
	private EliteStoryType(int v, String d) {
		value = v;
		desc = d;
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
