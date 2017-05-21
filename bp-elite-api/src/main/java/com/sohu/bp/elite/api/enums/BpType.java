package com.sohu.bp.elite.api.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhangzhihao
 *         2016/8/11
 */
public enum BpType implements IEnum{
    Elite_User(40, "问答用户"),
    Question(401, "问题"),
    Answer(450, "回答"),
    Tag(120, "标签");

    private int value;
    private String desc;

    BpType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
    
    public static final Map<Integer, IEnum> valueMap = new HashMap<Integer, IEnum>(){
		{
			put(40, Elite_User);
			put(401, Question);
			put(450, Answer);
			put(120, Tag);
		}
	};
	
	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){
		{
			put(40, "问答用户");
			put(401, "问题");
			put(450, "回答");
			put(120, "标签");
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
