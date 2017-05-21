package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 问答用户身份标识枚举类
 * @author zhijungou
 * 2016年11月10日
 */
public enum EliteUserIdentity implements IEnum {
	NORMAL(0, "用户"),
	EXPERT(1, "专家"),
    EXPERT_AUDITING(10, "待审核专家"),
	SUPER_ADMIN(50, "超级管理员");
	
	private int value;
	private String desc;
	
	private EliteUserIdentity(int value, String desc) {
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
	
	public static final Map<Integer, EliteUserIdentity> valueMap = new HashMap<Integer, EliteUserIdentity>(){{
		put(0, NORMAL);
		put(1, EXPERT);
		put(10, EXPERT_AUDITING);
		put(50, SUPER_ADMIN);
	}};

	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){{
		put(0, "用户");
		put(1, "专家");
		put(10, "待审核专家");
		put(50, "超级管理员");
	}};
}
