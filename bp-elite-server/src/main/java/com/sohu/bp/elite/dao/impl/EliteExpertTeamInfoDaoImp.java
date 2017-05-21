package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.dao.EliteExpertTeamInfoDao;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;

public class EliteExpertTeamInfoDaoImp extends JdbcDaoImpl implements EliteExpertTeamInfoDao {
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamInfoDaoImp.class);
    
    private CacheManager redisCacheManager;
    private RedisCache redisCache;
    
    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_EXPERT_TEAM_INFO);
    }
    @Override
    public Long insert(EliteExpertTeamInfo expertTeamInfo) {
        Long retVal = super.insert(expertTeamInfo);
        if (null == retVal || retVal <= 0 ) {
            log.info("insert expert team info = {} failed!, id = {}", new Object[]{expertTeamInfo, retVal});
            return retVal;
        }
        expertTeamInfo.setId(retVal);
        log.info("insert expert team info = {} succeed!", new Object[]{expertTeamInfo});
        return retVal;
    }

    @Override
    public boolean update(EliteExpertTeamInfo expertTeamInfo) {
        boolean retVal = false;
        if (super.update(expertTeamInfo) > 0) {
            updateCache(expertTeamInfo);
            retVal = true;
        }
        log.info("update expertTeamInfo = {}, result = {}", new Object[]{expertTeamInfo, retVal});
        return retVal;
    }

    @Override
    public EliteExpertTeamInfo getById(Long id) {
        EliteExpertTeamInfo teamInfo = (EliteExpertTeamInfo) redisCache.get(id);
        if (null == teamInfo || null == teamInfo.getId() || teamInfo.getId() <= 0) {
            teamInfo = super.get(EliteExpertTeamInfo.class, id);
        }
        updateCache(teamInfo);
        return teamInfo;
    }


    
    private void updateCache(EliteExpertTeamInfo teamInfo) {
        if (null == teamInfo || null == teamInfo.getId() || teamInfo.getId() <= 0) return;
        if (Objects.equals(teamInfo.getStatus(), EliteFeatureStatus.STATUS_WORK.getValue())) {
            redisCache.put(teamInfo.getId(), teamInfo);
            redisCache.sAdd(CacheConstants.ELITE_EXPERT_TEAM_INFO_SET, teamInfo.getId().toString());
        } else {
            redisCache.remove(teamInfo.getId().toString());
            redisCache.sRem(CacheConstants.ELITE_EXPERT_TEAM_INFO_SET, teamInfo.getId().toString());
        }
    }

    @Override
    public List<EliteExpertTeamInfo> getList() {
        List<EliteExpertTeamInfo> list = new ArrayList<>();
        Set<String> set = redisCache.sMembers(CacheConstants.ELITE_EXPERT_TEAM_INFO_SET);
        if (null != set && list.size() > 0) {
            for (String id : set) {
                list.add(getById(Long.valueOf(id)));
            }
        } else {
            Criteria criteria = Criteria.create(EliteExpertTeamInfo.class).where("status", new Object[]{EliteFeatureStatus.STATUS_WORK.getValue()});
            list = super.queryList(criteria);
            list.forEach(this::updateCache);
        }
        return list;
    }

}
