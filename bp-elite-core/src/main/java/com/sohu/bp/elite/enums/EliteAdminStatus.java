package com.sohu.bp.elite.enums;

/**
 * Created by nicholastang on 2017/3/15.
 */
public enum  EliteAdminStatus implements IEnum{
    NONE(0, "非管理员"),
    SUPER_ADMIN(10, "超级管理员");

    private int value;
    private String desc;

    EliteAdminStatus(int value, String desc) {
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
