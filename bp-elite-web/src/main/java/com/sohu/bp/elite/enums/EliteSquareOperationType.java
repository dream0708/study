package com.sohu.bp.elite.enums;

public enum EliteSquareOperationType implements IEnum{
    INSERT(1, "插入广场"),
    REMOVE(2, "删除广场");

    private int value;
    private String desc;
    
    private EliteSquareOperationType(int value, String desc) {
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
