package com.sohu.bp.elite.enums;

public enum EliteFeedAttitudeType implements IEnum{
    ADD(1, "增加"),
    REMOVE(2, "删除");
    
    
    private int value;
    private String desc;
    
    private EliteFeedAttitudeType(int value, String desc) {
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
