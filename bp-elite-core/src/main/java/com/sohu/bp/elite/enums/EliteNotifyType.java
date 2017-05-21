package com.sohu.bp.elite.enums;

//用于更新索引的说明
public enum EliteNotifyType implements IEnum{
	ELITE_NOTIFY_INSERT(1, "插入"),
	ELITE_NOTIFY_UPDATE(2, "更新"),
	ELITE_NOTIFY_DELETE(3, "删除");
	
	private int value;
	private String desc;
	private EliteNotifyType(int value, String desc){
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
