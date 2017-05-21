package com.sohu.bp.elite.enums;

/**
 * Created by nicholastang on 2017/2/28.
 */
public enum SendMessageType {
    INBOX(1, "站内信"),
    CELL_MESSAGE(2, "手机短信");

    private int msgType;
    private String desc;

    SendMessageType(int msgType, String desc) {
        this.msgType = msgType;
        this.desc = desc;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getDesc() {
        return desc;
    }
}
