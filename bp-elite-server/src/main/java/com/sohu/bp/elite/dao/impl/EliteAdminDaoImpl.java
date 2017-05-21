package com.sohu.bp.elite.dao.impl;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.dao.EliteAdminDao;
import com.sohu.bp.elite.enums.EliteAdminStatus;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by nicholastang on 2017/3/15.
 */
public class EliteAdminDaoImpl extends JdbcDaoImpl implements EliteAdminDao {
    private Logger log = LoggerFactory.getLogger(EliteAdminDaoImpl.class);
    private CacheManager redisCacheManager;

    private RedisCache adminStatusCache;
    private RedisCache adminCache;


    public RedisCache getAdminStatusCache() {
        return adminStatusCache;
    }

    public void setAdminStatusCache(RedisCache adminStatusCache) {
        this.adminStatusCache = adminStatusCache;
    }

    public RedisCache getAdminCache() {
        return adminCache;
    }

    public void setAdminCache(RedisCache adminCache) {
        this.adminCache = adminCache;
    }

    public CacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(CacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public void init(){
        adminStatusCache = (RedisCache)redisCacheManager.getCache(CacheConstants.ELITE_ADMIN_STATUS);
        adminCache = (RedisCache)redisCacheManager.getCache(CacheConstants.ElITE_ADMIN);
    }

    @Override
    public boolean save(EliteAdmin eliteAdmin) {
        if (super.save(eliteAdmin) <= 0) {
            return false;
        }
        adminCache.put(eliteAdmin.getId(), eliteAdmin);
        adminStatusCache.sRem(eliteAdmin.getStatus().toString(), String.valueOf(eliteAdmin.getId()));
        adminStatusCache.sAdd(eliteAdmin.getStatus().toString(), String.valueOf(eliteAdmin.getId()));
        return true;
    }

    //更新eliteAdmin
    @Override
    public boolean update(EliteAdmin eliteAdmin) {
        EliteAdmin oriEliteAdmin = this.get(eliteAdmin.getId());
        if (null != oriEliteAdmin) {
            super.update(eliteAdmin);
        }
        adminStatusCache.sRem(oriEliteAdmin.getStatus().toString(), String.valueOf(oriEliteAdmin.getId()));
        adminCache.put(eliteAdmin.getId(), eliteAdmin);
        return adminStatusCache.sAdd(eliteAdmin.getStatus().toString(), String.valueOf(eliteAdmin.getId()));
    }

    @Override
    public EliteAdmin get(long bpId) {
        EliteAdmin eliteAdmin = (EliteAdmin) adminCache.get(String.valueOf(bpId));
        if (eliteAdmin == null) {
            eliteAdmin = super.get(EliteAdmin.class, bpId);
            if (eliteAdmin != null) {
                adminCache.put(String.valueOf(eliteAdmin.getId()), eliteAdmin);
            }
        }
        return eliteAdmin;
    }

    @Override
    public Integer getAdminStatus(long bpId) {
        EliteAdmin eliteAdmin = this.get(bpId);
        if (null != eliteAdmin) {
            return eliteAdmin.getStatus();
        }
        return EliteAdminStatus.NONE.getValue();
    }

    @Override
    public boolean superAdmin(long bpId) {
        if (adminStatusCache.exist(String.valueOf(EliteAdminStatus.SUPER_ADMIN.getValue()))) {
            return adminStatusCache.sIsMember(String.valueOf(EliteAdminStatus.SUPER_ADMIN.getValue()), String.valueOf(bpId));
        }
        EliteAdmin eliteAdmin = this.get(bpId);
        if (eliteAdmin != null && eliteAdmin.getStatus() == EliteAdminStatus.SUPER_ADMIN.getValue()) {
        	adminStatusCache.sAdd(eliteAdmin.getStatus().toString(), String.valueOf(bpId));
        	return true;
        }
        return false;
    }

}