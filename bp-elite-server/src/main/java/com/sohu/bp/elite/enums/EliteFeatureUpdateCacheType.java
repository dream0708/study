package com.sohu.bp.elite.enums;

/**
 * DAO层更新缓存的种类枚举
 * @author zhijungou
 * 2017年1月13日
 */
public enum EliteFeatureUpdateCacheType {
	UPDATE_SINGLE(0, "更新单独缓存"),
	UPDATE_SET(1, "更新无状态队列"),
	UPDATE_STATUS_SET(2, "更新有状态队列");

	private final int value;
	private final String desc;
	
	private EliteFeatureUpdateCacheType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getDesc(){
		return this.desc;
	}
}
