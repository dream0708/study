package com.sohu.bp.elite.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.dao.IdentityDao;

public class IdentityDaoImpl implements IdentityDao {
	
	private static final Logger log = LoggerFactory.getLogger(IdentityDaoImpl.class);
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	public RedisCache getRedisCache() {
		return redisCache;
	}
	public void setRedisCache(RedisCache redisCache) {
		this.redisCache = redisCache;
	}
	
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_IDENTITY);
	}
	@Override
	public void insert(Long bpId) {
		if(null == bpId || bpId <= 0) return;
		boolean result = redisCache.zAdd(CacheConstants.IDENTITY_SET_KEY, new Date().getTime(), bpId.toString());
		log.info("insert bpId = {} into identity cache result = {}", new Object[]{ bpId, result });
	}

	@Override
	public void remove(Long bpId) {
		if(null == bpId || bpId <=0 ) return;
		boolean result = redisCache.zRem(CacheConstants.IDENTITY_SET_KEY, bpId.toString());
		log.info("remove bpId = {} from identity cache result = {}", new Object[]{ bpId, result});
	}
	@Override
	public List<Long> getExpertsList(int start, int count) {
		Set<String> ids = redisCache.zRevRange(CacheConstants.IDENTITY_SET_KEY, start, start + count - 1);
		List<Long> idList = new ArrayList<>();
		if(null == ids || ids.size() <= 0) return idList;
		ids.forEach(id -> idList.add(Long.valueOf(id)));
		return idList;
	}
	@Override
	public long getNum() {
		return redisCache.zCount(CacheConstants.IDENTITY_SET_KEY);
	}
	

}
