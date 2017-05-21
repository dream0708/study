package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/7/13
 */
public enum  EventType implements IEnum{
    INDEX(1, "建索引");

    private int value;
    private String desc;

    EventType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
