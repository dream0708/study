package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

public enum EliteFollowType implements IEnum {
	ELITE_USER(1, "问答用户"),
	ELITE_TAG(2,"问答标签");
	
	private Integer value;
	private String desc;
	
	public static final Map<Integer, EliteFollowType> valueMap = new HashMap<Integer, EliteFollowType>(){{
		put(1,ELITE_USER);
		put(2,ELITE_TAG);
	}};
	
	private EliteFollowType(Integer value, String desc) {
		this.desc = desc;
		this.value = value;
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
