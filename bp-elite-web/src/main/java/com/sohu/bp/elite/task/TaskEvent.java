package com.sohu.bp.elite.task;

import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.FeedActionType;
import com.sohu.bp.elite.enums.ProduceActionType;

/**
 * @author zhangzhihao
 *         2016/8/24
 */
public class TaskEvent {
    private FeedActionType type;
    private BpType bpType;
    private ProduceActionType actionType;
    private long bpId;
    private long questionId;
    private long answerId;
    private String tagIds;

    public TaskEvent(){

    }

    public TaskEvent(FeedActionType type, BpType bpType, ProduceActionType actionType, long bpId, long questionId, long answerId, String tagIds) {
        this.type = type;
        this.bpType = bpType;
        this.actionType = actionType;
        this.bpId = bpId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.tagIds = tagIds;
    }

    public FeedActionType getType() {
        return type;
    }

    public TaskEvent setType(FeedActionType type) {
        this.type = type;
        return this;
    }

    public long getBpId() {
        return bpId;
    }

    public TaskEvent setBpId(long bpId) {
        this.bpId = bpId;
        return this;
    }

    public long getQuestionId() {
        return questionId;
    }

    public TaskEvent setQuestionId(long questionId) {
        this.questionId = questionId;
        return this;
    }

    public long getAnswerId() {
        return answerId;
    }

    public TaskEvent setAnswerId(long answerId) {
        this.answerId = answerId;
        return this;
    }

    public String getTagIds() {
        return tagIds;
    }

    public TaskEvent setTagIds(String tagIds) {
        this.tagIds = tagIds;
        return this;
    }

    public BpType getBpType() {
        return bpType;
    }

    public TaskEvent setBpType(BpType bpType) {
        this.bpType = bpType;
        return this;
    }

    public ProduceActionType getActionType() {
        return actionType;
    }

    public TaskEvent setActionType(ProduceActionType actionType) {
        this.actionType = actionType;
        return this;
    }
}
