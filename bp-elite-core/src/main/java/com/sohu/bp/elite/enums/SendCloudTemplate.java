package com.sohu.bp.elite.enums;

public enum SendCloudTemplate implements IEnum{

    NEW_ANSWER_BYQUESTION(2876, "问答系统提示新回答短信"),
    NEW_INVITE_BY_PERSON(2997, "问答系统提示被邀请回答"),
    EXPERT_AUDIT_PASS(4568, "专家认证通过"),
    EXPERT_AUDIT_REJECT(4569, "专家认证不通过");
    
    private int value;
    private String desc;
    
    private SendCloudTemplate(int value, String desc) {
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
