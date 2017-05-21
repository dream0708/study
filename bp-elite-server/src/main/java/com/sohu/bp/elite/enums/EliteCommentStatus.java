package com.sohu.bp.elite.enums;
/**
 * @author limingcai
 * 2016年3月14日 下午6:19:02
 *
 */
public enum EliteCommentStatus implements IEnum {
	STATUS_WORK(1, "有效"),
	STATUS_INVALID(0, "无效");
	
	private int value;
	private String desc;
	
	EliteCommentStatus(int value,String desc)
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
