package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author nicholastang
 * 2016-08-15 14:19:40
 * TODO 描述用户行为的枚举类
 */
public enum ProduceActionType implements IEnum{
    FOLLOW(1, "关注了"),
    LIKE(2, "点赞了"),
    FAVORITE(3, "收藏了"),
    COMMENT(4, "评论了"),
    SHARE(5, "分享了"),
    ASK(6, "提问了"),
    ANSWER(7, "回答了"),
    ADD(8, "新增了"),
    UPDATE(9, "更新了"),
    PUBLISH_VOTE(10, "发起了投票"),
    VOTE(11, "投票了"),
    VOTE_AND_ANSWER(12, "投票并回答了"),
    PUBLISH_VS(13, "发起了晒图"),
    VS(14, "站队了"),
    VS_AND_COMMENT(15, "站队并评论了");
    

    private int value;
    private String desc;

    ProduceActionType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }
    
    public static final Map<Integer, IEnum> valueMap = new HashMap<Integer, IEnum>(){
		{
			put(1, FOLLOW);
			put(2, LIKE);
			put(3, FAVORITE);
			put(4, COMMENT);
			put(5, SHARE);
			put(6, ASK);
			put(7, ANSWER);
			put(8, ADD);
			put(9, UPDATE);
		    put(10, PUBLISH_VOTE);
		    put(11, VOTE);
		    put(12, VOTE_AND_ANSWER);
		    put(13, PUBLISH_VS);
		    put(14, VS);
		    put(15, VS_AND_COMMENT);
		}
	};
	
	public static final Map<Integer, String> valueDescMap = new HashMap<Integer, String>(){
		{
			put(1, "关注");
			put(2, "点赞");
			put(3, "收藏");
			put(4, "评论");
			put(5, "分享");
			put(6, "提问");
			put(7, "回答");
			put(8, "新增");
			put(9, "更新");
		    put(10, "发起了投票");
		    put(11, "投票了");
		    put(12, "投票并回答了");
		    put(13, "发起了晒图");
		    put(14, "站队了");
		    put(15, "站队并评论");
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
