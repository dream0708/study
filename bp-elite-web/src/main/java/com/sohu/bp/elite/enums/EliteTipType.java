package com.sohu.bp.elite.enums;

public enum EliteTipType {
    TIP_SUCCESS("icon-success", "成功提示"),
    TIP_WARNING("icon-warning", "失败提示"),
    TIP_WAIT("icon-wait", "等待提示");
    
    String value;
    String desc;
    
    private EliteTipType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDesc() {
        return desc;
    }
}
