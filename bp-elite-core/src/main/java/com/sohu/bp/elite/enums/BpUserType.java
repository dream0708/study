package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author nicholastang
 * 2016-08-26 11:40:33
 * TODO
 */
public enum BpUserType implements IEnum{
	NONE(0, "不识别"),
    NORMAL_USER(1, "普通用户"),
    SELF_MEDIA(11, "自媒体"),
    DESIGNER(21, "设计师"),
    FOREMAN(22, "工长"),
    COMPANY(60, "公司");

    private int value;
    private String desc;

    BpUserType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
    
    public static final Map<Integer, IEnum> valueMap = new HashMap<Integer, IEnum>(){
		{
			put(0, NONE);
			put(1, NORMAL_USER);
			put(11, SELF_MEDIA);
			put(21, DESIGNER);
			put(22, FOREMAN);
			put(60, COMPANY);
		}
	};
	
	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){
		{
			put(0, "不识别");
			put(1, "普通用户");
			put(11, "自媒体");
			put(21, "设计师");
			put(22, "工长");
			put(60, "公司");
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
