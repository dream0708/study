package com.sohu.bp.elite.enums;

/**
 * @author zhangzhihao
 *         2016/7/14
 */
public enum  EliteAnswerStatus implements IEnum {
    DRAFT(1, "草稿"),
    AUDITING(2, "发布待审核"),
    PUBLISHED(3, "已发布"),
    REJECTED(4, "驳回"),
    DEL(5, "删除"),
    SYSDEL(6, "系统删除"),
    PASSED(7, "发布审核通过"),
    //用于站队投票等功能，表示仅选择
    CHOOSE(8, "处于选择状态");
    

    private int value;
    private String desc;

    EliteAnswerStatus(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
