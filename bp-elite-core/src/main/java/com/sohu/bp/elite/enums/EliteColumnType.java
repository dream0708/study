package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

//专栏的样式枚举类
public enum EliteColumnType implements IEnum{
	COLUMN_DESIGNER(0, "设计师专栏");
	
	private int value;
	private String desc;
	
	public static final Map<Integer, IEnum> valueMap = new HashMap<Integer, IEnum>(){{
		put(0, COLUMN_DESIGNER);
	}};

	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){{
		put(0, "设计师专栏");
	}};
	
	private EliteColumnType(int value, String desc) {
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
