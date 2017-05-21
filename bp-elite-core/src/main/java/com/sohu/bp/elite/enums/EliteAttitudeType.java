package com.sohu.bp.elite.enums;

public enum EliteAttitudeType implements IEnum{
    POSITIVE(1, "支持"),
    NEGATIVE(2, "反对"),
    NEUTRAL(3, "中立");
    
    private int value;
    private String desc;
    
    private EliteAttitudeType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

}
