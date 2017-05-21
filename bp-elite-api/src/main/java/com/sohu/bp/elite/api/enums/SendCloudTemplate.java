package com.sohu.bp.elite.api.enums;

public enum SendCloudTemplate implements IEnum
{
	NEW_ANSWER_BYQUESTION(2876, "问答系统提示新回答短信"),
	NEW_INVITE_BY_PERSON(2997, "问答系统提示被邀请回答");
	
	Integer value;
	String desc;
	
	SendCloudTemplate(Integer value, String desc)
	{
		this.value = value;
		this.desc = desc;
	}
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.enums.IEnum#getValue()
	 */
	@Override
	public int getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.enums.IEnum#getDesc()
	 */
	@Override
	public String getDesc() {
		return this.desc;
	}
	
}