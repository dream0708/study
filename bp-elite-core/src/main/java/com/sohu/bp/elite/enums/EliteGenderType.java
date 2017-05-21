package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/7/19
 */
public enum  EliteGenderType implements IEnum{
    Male(0, "男"),
    Female(1, "女");

    EliteGenderType(int value, String desc){
        this.value = value;
        this.desc = desc;
    }

    private int value;
    private String desc;


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
