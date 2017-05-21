package com.sohu.bp.elite.consumer.enums;

import com.sohu.bp.elite.enums.IEnum;

/**
 * 
 * @author nicholastang
 * 2016-10-17 19:27:14
 * TODO
 */
public enum EliteMessageTargetType implements IEnum
{
	SINGLE(1,"定向单人"),
	BATCH(2, "定向多人"),
	FANS(3, "粉丝");
	
	int value;
	String desc;
	
	EliteMessageTargetType(int value, String desc)
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
	
}