package com.sohu.bp.elite.enums;

public enum EliteAsyncTaskOper implements IEnum
{
	RESAVECONTENT(1,"对内容进行异步处理（包括存储图片和提取@）");

	int value;
	String desc;
	
	EliteAsyncTaskOper(int value, String desc)
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