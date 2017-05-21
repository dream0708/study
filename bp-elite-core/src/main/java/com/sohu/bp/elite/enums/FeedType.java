package com.sohu.bp.elite.enums;

/**
 * 
 * @author nicholastang
 * 2016-08-11 17:38:28
 * TODO
 */
public enum FeedType implements IEnum
{
	QUESTION(1, "问题"),
	ANSWER(2, "回答");
	
	private int value;
	private String desc;
	
	FeedType(int value,String desc)
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
	
	public static FeedType getFeedTypeByValue(int value){
		switch (value)	{
		case 1 : return QUESTION; 
		case 2 : return ANSWER; 
		default : return QUESTION; 
		}
	}
	
	public static BpType getBpTypeByFeedType(FeedType feedType) {
	    switch (feedType) {
	    case QUESTION : 
	        return BpType.Question;
	    case ANSWER : 
	        return BpType.Answer;
	    default : return BpType.Question;
	    }
	}
}