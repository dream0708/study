package com.sohu.bp.elite.enums;

import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.model.BpMessageDetailType;
import com.sohu.bp.model.BpMessageSystemStatus;
import com.sohu.bp.model.BpMessageSystemType;

public enum EliteMessageData {
    
    //用于互动消息
    NEW_QUESTION_BY_PERSON(1, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您关注的用户提出了新的问题", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "提问《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_ANSWER_BY_PERSON(2, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您关注的用户产生了新的回答", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "回答了问题《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_ANSWER_BY_QUESTION(3, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您关注的问题收到新的回答", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "回答了问题《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_ANSWER_BY_MYQUESTION(4, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您收到了新的回答", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "回答了您的问题《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_FOLLOW_BY_MYQUESTION(5, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您的问题获得了新的关注", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "关注了您的提问《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_FOLLOW_BY_PERSON(6, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您在问吧获得了新的粉丝", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "关注了您</a>"),
    UPDATE_AUDIT_PASS_BY_MYQUESTION(7, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "系统通过了您的提问", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + "系统通过了您的提问《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    UPDATE_AUDIT_REJECT_BY_MYQUESTION(8, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "系统驳回了您提出的问题", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + "系统驳回了您的提问《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》，请修改后重新发布</a>"),
    NEW_COMMENT_BY_MYANSWER(9, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您的回答有新的评论", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "评论了您关于《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》的回答</a>"),
    NEW_LIKE_BY_MYANSWER(10, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您的回答被人点赞了", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "点赞了您关于《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》的回答</a>"),
    NEW_FAVORITE_BY_MYANSWER(11, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "你的回答被人收藏了", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "收藏了您关于《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》的回答</a>"),
    UPDATE_AUDIT_PASS_BY_MYANSWER(12, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "系统通过了您的回答", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + "系统通过了您关于《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》的回答</a>"),
    UPDATE_AUDIT_REJECT_BY_MYANSWER(13, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "系统驳回了您的回答", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + "系统驳回了您关于《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》的回答</a>"),
    UPDATE_QUESTION_BY_MYFOLLOW(14, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "您关注的问题更新了", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "修改了问题《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_INVITE_BY_PERSON(15, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "有用户邀请您回答问题","<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>" + Constants.MESSAGE_DATA_NICKNAME + "邀请您回答问题《" + Constants.MESSAGE_DATA_QUESTIONTITLE + "》</a>"),
    NEW_AT_TIP(16, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "有用户@您", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>有用户在" + Constants.MESSAGE_DATA_CONTENT_TYPE + "中@您</a>"),
    NEW_REPLY_TIP(17, BpMessageDetailType.INTERACTION, BpInteractionType.FOLLOW, null, null, "有用户回复了您的评论", "<a href='" + Constants.MESSAGE_DATA_JUMPURL + "'>有用户回复了您的评论</a>"),

    //用于专家申请
    EXPERT_REGISTER_PASS(40, BpMessageDetailType.SYSTEM, null, BpMessageSystemType.AUDITING, BpMessageSystemStatus.SUCCESS, "问吧专家认证成功", "恭喜您通过问吧专家认证。"),
    EXPERT_REGISTER_REJECT(41, BpMessageDetailType.SYSTEM, null, BpMessageSystemType.AUDITING, BpMessageSystemStatus.REJECT, "问吧专家认证失败", "抱歉，您未通过问吧专家审核，具体原因:" + Constants.MESSAGE_DATA_REASON);

    
    private int value;
    private BpMessageDetailType detailType;
    private BpInteractionType interactionType;
    private BpMessageSystemType systemType;
    private BpMessageSystemStatus systemStatus;
    private String topic;
    private String content;
    
    private EliteMessageData(int value, BpMessageDetailType detailType, BpInteractionType interactionType, BpMessageSystemType systemType, BpMessageSystemStatus systemStatus,
            String topic, String content) {
        this.value = value;
        this.detailType = detailType;
        this.interactionType = interactionType;
        this.systemType = systemType;
        this.systemStatus = systemStatus;
        this.topic = topic;
        this.content = content;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public BpMessageDetailType getDetailType() {
        return detailType;
    }

    public void setDetailType(BpMessageDetailType detailType) {
        this.detailType = detailType;
    }

    public BpInteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(BpInteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public BpMessageSystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(BpMessageSystemType systemType) {
        this.systemType = systemType;
    }

    public BpMessageSystemStatus getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(BpMessageSystemStatus systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public static EliteMessageData findByValue(int value) {
        switch (value) {
        case 1:
            return NEW_QUESTION_BY_PERSON;
        case 2:
            return NEW_ANSWER_BY_PERSON;
        case 3:
            return NEW_ANSWER_BY_QUESTION;
        case 4:
            return NEW_ANSWER_BY_MYQUESTION;
        case 5:
            return NEW_FOLLOW_BY_MYQUESTION;
        case 6:
            return NEW_FOLLOW_BY_PERSON;
        case 7:
            return UPDATE_AUDIT_PASS_BY_MYQUESTION;
        case 8:
            return UPDATE_AUDIT_REJECT_BY_MYQUESTION;
        case 9:
            return NEW_COMMENT_BY_MYANSWER;
        case 10:
            return NEW_LIKE_BY_MYANSWER;
        case 11:
            return NEW_FAVORITE_BY_MYANSWER;
        case 12:
            return UPDATE_AUDIT_PASS_BY_MYANSWER;
        case 13:
            return UPDATE_AUDIT_REJECT_BY_MYANSWER;
        case 14:
            return UPDATE_QUESTION_BY_MYFOLLOW;
        case 15:
            return NEW_INVITE_BY_PERSON;
        case 16:
            return NEW_AT_TIP;
        case 17:
            return NEW_REPLY_TIP;
        case 40:
            return EXPERT_REGISTER_PASS;
        case 41:
            return EXPERT_REGISTER_REJECT;
        default:
            return null;
        }
    }
    
}
