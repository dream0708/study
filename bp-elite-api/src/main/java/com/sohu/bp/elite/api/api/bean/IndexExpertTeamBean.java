package com.sohu.bp.elite.api.api.bean;

public class IndexExpertTeamBean {
    private Long bpId;
    private Integer pushNum;
    private Integer answeredNum;
    private Integer score;
    private Integer identity;
    private Long lastPushTime;
    private Long lastAnsweredTime;
    private Long team;
    
    public Long getBpId() {
        return bpId;
    }
    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }
    public Integer getPushNum() {
        return pushNum;
    }
    public void setPushNum(Integer pushNum) {
        this.pushNum = pushNum;
    }
    public Integer getAnsweredNum() {
        return answeredNum;
    }
    public void setAnsweredNum(Integer answeredNum) {
        this.answeredNum = answeredNum;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public Integer getIdentity() {
        return identity;
    }
    public void setIdentity(Integer identity) {
        this.identity = identity;
    }
    public Long getLastPushTime() {
        return lastPushTime;
    }
    public void setLastPushTime(Long lastPushTime) {
        this.lastPushTime = lastPushTime;
    }
    public Long getLastAnsweredTime() {
        return lastAnsweredTime;
    }
    public void setLastAnsweredTime(Long lastAnsweredTime) {
        this.lastAnsweredTime = lastAnsweredTime;
    }
    public Long getTeam() {
        return team;
    }
    public void setTeam(Long team) {
        this.team = team;
    }
    
    
}
