package com.sohu.bp.elite.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.service.EliteExpertTeamService;
import com.sohu.bp.elite.api.service.EliteUserService;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TUserListResult;

public class EliteExpertTeamServiceImpl implements EliteExpertTeamService {
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamServiceImpl.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    
    private EliteUserService eliteUserService;
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_EXPERT_TEAM);
    }
    
    @Override
    public List<String> getExpertTeamList() {
        List<String> list = new ArrayList<String>();
        Set<String> set = redisCache.zRevRange(CacheConstants.EXPERT_TEAM_SET, 0, -1);
        if (null != set && set.size() > 0) {
            list.addAll(set);
        } else {
            getExpertTeamListByAdapter(list);
        }
        return list;
    }

    @Override
    public boolean addExpert(Long bpId) {
       if (eliteUserService.createUserIfNotExist(bpId, EliteUserIdentity.EXPERT)) {
           addCache(bpId.toString());
           return true;
       } else {
           log.info("add into expert team failed : bpId = {}", bpId);
           return false;
       }
       
    }

    @Override
    public boolean removeExpert(Long bpId) {
        try {
            TEliteUser user = eliteAdapter.getUserByBpId(bpId);
            user.setIdentity(EliteUserIdentity.NORMAL.getValue());
            eliteAdapter.updateUser(user);
            redisCache.zRem(CacheConstants.EXPERT_TEAM_SET, bpId.toString());
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    @Override
    public void updateCache() {
        redisCache.remove(CacheConstants.EXPERT_TEAM_SET);
        log.info("remove cache EXPERT_TEAM_SET");
        List<String> list = new ArrayList<>();
        getExpertTeamListByAdapter(list);
        list.forEach(this::addCache);
    }
    
    private void getExpertTeamListByAdapter(List<String> list) {
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setStatus(EliteUserStatus.VALID.getValue()).setIdentity(EliteUserIdentity.EXPERT.getValue());
        try {
            TUserListResult listResult = eliteAdapter.searchUser(condition);
            List<TEliteUser> users = listResult.getUsers();
            users.forEach(user -> list.add(String.valueOf(user.getBpId())));
        } catch (TException e) {
            log.error("", e);
        }
    }
    
    private void addCache(String bpId) {
        redisCache.zAdd(CacheConstants.EXPERT_TEAM_SET, new Date().getTime(), bpId);
        log.info("add into expert team : bpId = {}", bpId);
    }
    
    public CacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public EliteUserService getEliteUserService() {
        return eliteUserService;
    }

    public void setEliteUserService(EliteUserService eliteUserService) {
        this.eliteUserService = eliteUserService;
    }
    
}
