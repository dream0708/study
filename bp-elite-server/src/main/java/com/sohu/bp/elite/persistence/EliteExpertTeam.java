package com.sohu.bp.elite.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@Table(name="elite_expert_team")
public class EliteExpertTeam extends AbstractEntity{
    /**
     * 
     */
    private static final long serialVersionUID = -6570886151165519194L;
    private Long id;
    private Integer pushNum;
    private Integer answeredNum;
    private Integer score;
    private Integer identity;
    private String unansweredId;
    private String answeredId;
    private Date lastPushTime;
    private Date lastAnsweredTime;
    private Long team;
    
    @Id
    public Long getId() {
        return id;
    }


    public EliteExpertTeam setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getAnsweredNum() {
        return answeredNum;
    }


    public EliteExpertTeam setAnsweredNum(Integer answeredNum) {
        this.answeredNum = answeredNum;
        return this;
    }


    public Integer getScore() {
        return score;
    }


    public EliteExpertTeam setScore(Integer score) {
        this.score = score;
        return this;
    }

    public String getUnansweredId() {
        return unansweredId;
    }


    public EliteExpertTeam setUnansweredId(String unansweredId) {
        this.unansweredId = unansweredId;
        return this;
    }
    
    public String getAnsweredId() {
        return answeredId;
    }

    public EliteExpertTeam setAnsweredId(String answeredId) {
        this.answeredId = answeredId;
        return this;
    }
    
    public Integer getIdentity() {
        return identity;
    }


    public EliteExpertTeam setIdentity(Integer identity) {
        this.identity = identity;
        return this;
    }


    public Integer getPushNum() {
        return pushNum;
    }


    public EliteExpertTeam setPushNum(Integer pushNum) {
        this.pushNum = pushNum;
        return this;
    }


    public Date getLastPushTime() {
        return lastPushTime;
    }


    public EliteExpertTeam setLastPushTime(Date lastPushTime) {
        this.lastPushTime = lastPushTime;
        return this;
    }


    public Date getLastAnsweredTime() {
        return lastAnsweredTime;
    }


    public EliteExpertTeam setLastAnsweredTime(Date lastAnsweredTime) {
        this.lastAnsweredTime = lastAnsweredTime;
        return this;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    @Transient
    @Override
    public Serializable getInternalId() {
       return id;
    }
    
    @Override
    public String toString() {
        return String.format("bpId : %d push_num : %d answerd_num : %d score : %d ", this.id, this.pushNum, this.answeredNum, this.score);
    }


    public Long getTeam() {
        return team;
    }

    public EliteExpertTeam setTeam(Long team) {
        this.team = team;
        return this;
    }
}
