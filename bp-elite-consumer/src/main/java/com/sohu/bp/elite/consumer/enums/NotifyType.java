package com.sohu.bp.elite.consumer.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicholastang on 2017/3/27.
 */
public enum  NotifyType {
    ADD_COMMENT(1, "添加评论"),
    ADD_FAN_FOR_PERSON(2, "给用户添加粉丝"),
    ADD_FAN_FOR_QUESITON(3, "给问题添加关注人"),
    ADD_LIKE_FOR_ANSWER(4, "给回答增加赞"),
    ADD_LIKE_FOR_COMMENT(5, "给评论增加咱"),
    ADD_FAVORITE_FOR_ANSWER(6, "给回答增加收藏");

    private int value;
    private String desc;

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

    NotifyType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static final Map<Integer, Enum> valueMap = new HashMap<Integer, Enum>(){
        {
            put(1, ADD_COMMENT);
            put(2, ADD_FAN_FOR_PERSON);
            put(3, ADD_FAN_FOR_QUESITON);
            put(4, ADD_LIKE_FOR_ANSWER);
            put(5, ADD_LIKE_FOR_COMMENT);
            put(6, ADD_FAVORITE_FOR_ANSWER);
        }
    };
}
