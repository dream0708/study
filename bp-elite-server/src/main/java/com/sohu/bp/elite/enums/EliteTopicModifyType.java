package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016年3月16日
 */
public enum EliteTopicModifyType implements IEnum
{
	MODIFY_INIT(0, "初始创建"),
	MODIFY_TITLE(1, "修改题目"),
	MODIFY_TAG(2, "修改标签"),
	MODIFY_TITLE_TAG(3, "修改标题及标签"),
	MODIFY_DESC(4, "修改描述"),
	MODIFY_TITLE_DESC(5, "修改标题及描述"),
	MODIFY_TAG_DESC(6, "修改标签及描述"),
	MODIFY_TITLE_TAG_DESC(7, "修改所有信息");
	
	private int value;
	private String desc;
	
	EliteTopicModifyType(int value,String desc)
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