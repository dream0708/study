package com.sohu.bp.elite.persistence;

import java.io.Serializable;

import javax.persistence.Transient;

import com.alibaba.fastjson.JSON;

/**
 * 用于记录专家分组信息
 * @author zhijungou
 * 2017年4月12日
 */
public class EliteExpertTeamInfo extends AbstractEntity{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String teamName;
    private Long creatorId;
    private String authorizatedIds;
    private Integer status;
    
    public Long getId() {
        return id;
    }
    public EliteExpertTeamInfo setId(Long id) {
        this.id = id;
        return this;
    }
    public String getTeamName() {
        return teamName;
    }
    public EliteExpertTeamInfo setTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }
    public Long getCreatorId() {
        return creatorId;
    }
    public EliteExpertTeamInfo setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
        return this;
    }
    public String getAuthorizatedIds() {
        return authorizatedIds;
    }
    public EliteExpertTeamInfo setAuthorizatedIds(String authorizatedIds) {
        this.authorizatedIds = authorizatedIds;
        return this;
    }
    public Integer getStatus() {
        return status;
    }
    public EliteExpertTeamInfo setStatus(Integer status) {
        this.status = status;
        return this;
    }
    
    @Transient
    @Override
    public Serializable getInternalId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this); 
    }
    
    
}
