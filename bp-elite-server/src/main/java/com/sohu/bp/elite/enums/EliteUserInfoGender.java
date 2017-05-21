package com.sohu.bp.elite.enums;

/**
 * Created by linezhao on 2016/4/11 10:26.
 */
public enum EliteUserInfoGender implements IEnum{
    MALE(0, "男性"),
    FEMALE(1, "女性"),
    OTHER(2, "其他");

    private int value;
    private String desc;

    EliteUserInfoGender(int value, String desc) {
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
