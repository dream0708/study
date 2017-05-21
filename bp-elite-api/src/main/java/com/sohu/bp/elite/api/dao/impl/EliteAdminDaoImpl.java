package com.sohu.bp.elite.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.api.api.bean.AdminZombieBean;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.dao.EliteAdminDao;

public class EliteAdminDaoImpl implements EliteAdminDao{
	private static final Logger log = LoggerFactory.getLogger(EliteAdminDaoImpl.class);
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	public void init() {
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_ADMIN);
	}

	@Override
	public boolean zombieInsert(AdminZombieBean bean) {
		boolean result = redisCache.zAdd(CacheConstants.ADMIN_ZOMBIE_TASK_SET, bean.getCreateTime(), JSON.toJSONString(bean));
		log.info("insert task = {} into zombie task set, result = {}", new Object[]{JSON.toJSONString(bean), result});
		return result;
	}
	
	@Override
	public boolean zombieRemove(AdminZombieBean bean) {
		boolean result = redisCache.zRem(CacheConstants.ADMIN_ZOMBIE_TASK_SET, JSON.toJSONString(bean));
		log.info("remove task = {} from zombie task set, result = {}", new Object[]{JSON.toJSONString(bean), result});
		return result;
	}

	@Override
	public List<AdminZombieBean> zombieGetList(int start, int count) {
		List<AdminZombieBean> list = new ArrayList<>();
		Set<String> set = redisCache.zRevRange(CacheConstants.ADMIN_ZOMBIE_TASK_SET, start, start + count - 1);
		for (String ele : set) {
			AdminZombieBean bean = JSON.parseObject(ele, AdminZombieBean.class);
			list.add(bean);
		}
		return list;
	}

    @Override
    public void zombieFlush() {
        redisCache.remove(CacheConstants.ADMIN_ZOMBIE_TASK_SET);
        log.info("remove current zombie tasks record, it means the restart of admin");
    }

}
