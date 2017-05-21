package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年7月12日
 */
public enum EliteQuestionStatus implements IEnum
{	DRAFT(1, "草稿"),
	AUDITING(2, "发布待审核"),
	PUBLISHED(3, "已发布"),
	REJECTED(4, "驳回"),
	DEL(5, "删除"),
	SYSDEL(6, "系统删除"),
	PASSED(7, "发布审核通过");
	
	private int value;
	private String desc;
	
	EliteQuestionStatus(int value,String desc)
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