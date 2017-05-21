package com.sohu.bp.elite.consumer.enums;

/**
 * Created by nicholastang on 2017/3/27.
 */
public enum NotifyTopic {
    ELITE("zombie_elite", "僵尸程序向问吧发送消息的topic");

    private String value;
    private String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    NotifyTopic(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
