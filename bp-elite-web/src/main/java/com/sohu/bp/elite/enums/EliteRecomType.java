package com.sohu.bp.elite.enums;

/**
 * 
 * @author zhijungou
 * 2016年9月10日
 */
public enum EliteRecomType {
	RECOM_ANSWER(1,"推荐回答"),
	RECOM_USER(2,"推荐用户"),
	RECOM_TAG(3,"推荐标签");
	
	Integer value;
	String desc;
	
	private EliteRecomType(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public Integer getValue(){
		return value;
	}
	
}
