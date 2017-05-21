package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.dao.EliteExpertTeamDao;
import com.sohu.bp.elite.dao.EliteUserDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.persistence.EliteUser;

import freemarker.core.ReturnInstruction.Return;

/**
 * 
 * @author zhijungou
 * 2016年10月20日
 */
public class EliteUserDaoImpl extends JdbcDaoImpl implements EliteUserDao {

    private Logger log = LoggerFactory.getLogger(EliteQuestionDaoImpl.class);
    
    private EliteExpertTeamDao expertTeamDao;
    private TablePartitioner partitioner;
    private CacheManager redisCacheManager;
    private CacheManager ssdbCacheManager;
    private RedisCache userRedisCache;
    private SsdbCache ssdbCache;

    public void setExpertTeamDao(EliteExpertTeamDao expertTeamDao) {
        this.expertTeamDao = expertTeamDao;
    }
    
    public void setPartitioner(TablePartitioner partitioner) {
        this.partitioner = partitioner;
    }

    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
    
    public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
        this.ssdbCacheManager = ssdbCacheManager;
    }

    public void init(){
        userRedisCache = (RedisCache)redisCacheManager.getCache(CacheConstants.ELITE_USER);
        ssdbCache = (SsdbCache) ssdbCacheManager.getCache(SsdbConstants.ELITE_AUDITING_USER_LIST);
    }

    public String getTableName(Long bpId){
        return DbPartitionHelper.getTableName(partitioner, bpId);
    }

    @Override
    public Long save(EliteUser user) {
        Long id = -1L;
        if(user == null || user.getId() == null)
            return id;
        id = user.getId();
        if(super.save(user, getTableName(user.getId())) > 0){
            userRedisCache.put(user.getId(), user);
        }else {
            id = -1L;
            log.error("save user error, bpId=" + user.getId());
        }

        return id;
    }

    @Override
    public boolean update(EliteUser user) {
        if(user == null || user.getId() == null)
            return false;
        EliteUser originUser = get(user.getId());
        if(originUser == null)
            return false;

        if(super.update(user, getTableName(user.getId())) > 0) {
            userRedisCache.put(user.getId().toString(), user);
            if (!Objects.equals(user.getIdentity(), originUser.getIdentity())) {
                updateCache(user);
            }
        }else {
            log.error("update user error, bpId=" + user.getId());
            return false;
        }
        return true;
    }
    
    @Override
    public EliteUser get(Long bpId) {
        EliteUser user = (EliteUser) userRedisCache.get(bpId.toString());
        if(user == null){
            user = super.get(EliteUser.class, bpId, getTableName(bpId));
            if(user != null){
                userRedisCache.put(user.getId().toString(), user);
            }
        }
        return user;
    }

    @Override
    public ListResult getAuditingExperts(Integer start, Integer count) {
        long total = ssdbCache.zCount(SsdbConstants.ELITE_AUDITING_USER_KEY);
        List<EliteUser> users = new ArrayList<EliteUser>();
        if (total > 0) {
            List<String> ids = ssdbCache.zRange(SsdbConstants.ELITE_AUDITING_USER_KEY, start, count);
            if (null != ids) {
                ids.forEach(id -> users.add(get(Long.valueOf(id))));
            }
        }
        ListResult listResult = new ListResult(total, users);
        return listResult; 
    }

    @Override
    public ListResult getExperts(Integer start, Integer count) {
        long total = userRedisCache.zCount(CacheConstants.ELITE_EXPERT_SET);
        List<EliteUser> users = new ArrayList<EliteUser>(); 
        if (total > 0) {
            Set<String> ids = userRedisCache.zRange(CacheConstants.ELITE_EXPERT_SET, start, start + count - 1);
            if (null != ids) {
                ids.forEach(id -> users.add(get(Long.valueOf(id))));  
            }            
        }
        ListResult listResult = new ListResult(total, users);
        return listResult;
    }
    
    @Override
    public void removeExpertsCache() {
        userRedisCache.remove(CacheConstants.ELITE_EXPERT_SET);
    }
    
    @Deprecated
    @Override
    public Boolean addExpertAuditing(Long bpId) {
        return true;
    }
    
    @Override
    public void addExpertCache(Long bpId) { 
        userRedisCache.zAdd(CacheConstants.ELITE_EXPERT_SET, new Date().getTime(), bpId.toString());
    }   
    
    //更新专家缓存
    private void updateCache(EliteUser user) {
        switch (EliteUserIdentity.valueMap.get(user.getIdentity())) {
        case EXPERT:
            userRedisCache.zAdd(CacheConstants.ELITE_EXPERT_SET, new Date().getTime(), user.getId().toString());
            ssdbCache.zRem(SsdbConstants.ELITE_AUDITING_USER_KEY, user.getId().toString());
            break;
        case EXPERT_AUDITING:
            userRedisCache.zRem(CacheConstants.ELITE_EXPERT_SET, user.getId().toString());
            ssdbCache.zAdd(SsdbConstants.ELITE_AUDITING_USER_KEY, new Date().getTime(), user.getId().toString());
            break;
        case NORMAL:
            userRedisCache.zRem(CacheConstants.ELITE_EXPERT_SET, user.getId().toString());
            ssdbCache.zRem(SsdbConstants.ELITE_AUDITING_USER_KEY, user.getId().toString());
        default:
            break;
        }
    }

    @Override
    public Long getExpertsNum() {
        return  userRedisCache.zCount(CacheConstants.ELITE_EXPERT_SET);
    }

    @Override
    public EliteUser getExpert(Long bpId) {
        EliteUser user = get(bpId);
        if (Objects.equals(user.getIdentity(), EliteUserIdentity.EXPERT.getValue())) return user;
        else return null;
    } 
}
