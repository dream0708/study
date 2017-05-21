package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.service.web.IdentityService;

public class IdentityServiceImpl implements IdentityService {

	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_IDENTITY);
	}
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	@Override
	public List<Long> getRecomExperts(int start, int count) {
		Set<String> ids = redisCache.zRevRange(CacheConstants.IDENTITY_SET_KEY, start, start + count - 1);
		if(null == ids || ids.size() <= 0 ) return null;
		List<Long> idList = new ArrayList<>();
		ids.forEach(id -> idList.add(Long.valueOf(id)));
		return idList;
	}

	@Override
	public List<Long> getAllRecomExperts() {
		Set<String> ids = redisCache.zRevRange(CacheConstants.IDENTITY_SET_KEY, 0, -1);
		if(null == ids || ids.size() <= 0) return null;
		List<Long> idList = new ArrayList<>();
		ids.forEach(id -> idList.add(Long.valueOf(id)));
		return idList;
	}
	
	@Override
	public Long getRecomNum() {
		return redisCache.zCount(CacheConstants.IDENTITY_SET_KEY);
	}

}
