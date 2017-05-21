package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
public enum  EliteUserStatus implements IEnum{
    VALID(1,"有效状态"),
    INVALID(2, "无效状态");

    private int value;
    private String desc;

    EliteUserStatus(int value, String desc) {
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
