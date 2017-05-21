package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/7/20
 */
public enum EliteSearchType implements IEnum{
    Question(8, "问题索引"),
    Answer(9, "回答索引"),
    User(10, "用户索引");

    private int value;
    private String desc;

    EliteSearchType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }

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
