package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhangzhihao
 *         2016/8/11
 */
public enum BpType implements IEnum{
    Bp(1, "用户"),
    Comment(150, "评论"),
    Elite_User(40, "问答用户"),
    FUV(45, "FUV"),
    Question(401, "问题"),
    Elite_Vote(402, "问答投票贴"),
    Elite_VS(403, "问答晒图"),
    Answer(450, "回答"),
    Tag(120, "标签"),
	Elite_Tag(121, "问答标签"),
	Elite_Subject(470, "问答专题"),
	Elite_Column(471, "问答专栏"),
	Elite_Topic(472, "问答话题"),
	Elite_Expert_Team(480, "问答专家团");
	
	

    private int value;
    private String desc;

    BpType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
    
    public static final Map<Integer, BpType> valueMap = new HashMap<Integer, BpType>(){
		{
		    put(1, Bp);
            put(150, Comment);
            put(40, Elite_User);
            put(45, FUV);
            put(401, Question);
            put(402, Elite_Vote);
            put(403, Elite_VS);
            put(450, Answer);
            put(120, Tag);
            put(121, Elite_Tag);
            put(470, Elite_Subject);
            put(471, Elite_Column);
            put(472, Elite_Topic);
            put(480, Elite_Expert_Team);
		}
	};
	
	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){
		{
		    put(1, "用户");
            put(150, "评论");
            put(40, "问答用户");
            put(45, "FUV");
            put(401, "问题");
            put(402, "问答投票贴");
            put(403, "问答晒图");
            put(450, "回答");
            put(120, "标签");
            put(121, "问答标签");
            put(470, "问答专题");
            put(471, "问答专栏");
            put(472, "问答话题");
            put(480, "问答专家团");
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
