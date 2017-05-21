package com.sohu.bp.elite.enums;

public enum EliteVSType implements IEnum{
    
    ELITE_VS_POS(1, "晒图安利"),
    ELITE_VS_NEG(2, "晒图吐槽");
    
    private int value;
    private String desc;
    private EliteVSType(int value, String desc) {
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
