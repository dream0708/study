package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author nicholastang
 * 2016-09-22 17:09:03
 * TODO sidebar 所属的页面类型
 */
public enum SideBarPageType implements IEnum{
    MY_QUESTION(0, "我的提问"),
    MY_ANSWER(1, "我的回答"),
    MY_FANS(2, "我的粉丝"),
    MY_FOLLOW(3, "我的关注"),
    MY_FAVORITES(4, "我的收藏");

    private int value;
    private String desc;

    SideBarPageType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
    
    public static final Map<Integer, IEnum> valueMap = new HashMap<Integer, IEnum>(){
		{
			put(0, MY_QUESTION);
			put(1, MY_ANSWER);
			put(2, MY_FANS);
			put(3, MY_FOLLOW);
			put(4, MY_FAVORITES);
		}
	};
	
	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){
		{
			put(0, "我的提问");
			put(1, "我的回答");
			put(2, "我的粉丝");
			put(3, "我的关注");
			put(4, "我的收藏");
		}
	};

    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
