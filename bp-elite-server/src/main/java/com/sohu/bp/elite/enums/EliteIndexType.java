package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/8/29
 */
public enum EliteIndexType implements IEnum{
    Build_Index(1, "建索引"),
    Delete_Index(2, "删除索引");

    private int value;
    private String desc;

    EliteIndexType(int value, String desc){
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
