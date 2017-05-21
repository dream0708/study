package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.InviteStatus;
import com.sohu.bp.elite.service.web.InviteService;
import com.sohu.bp.elite.util.InviteUtil;

public class InviteServiceImpl implements InviteService {
	
	private static final Logger log = LoggerFactory.getLogger(InviteServiceImpl.class);
	
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	private RedisCache adminRedisCache;
	
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_INVITE);
		adminRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_FEATURE);
	}

	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}


	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}


	@Override
	public Integer saveInviteStatus(Long questionId, Long inviteId, Long invitedId) {
		String cacheKey = InviteUtil.getInviteCacheKey(questionId, inviteId);
		List<Long> inviteList = getUserInviteList(questionId, inviteId);
		if(inviteList.contains(invitedId)) return InviteStatus.INVITE_ALREADY.getValue();
		Boolean result = redisCache.zAdd(cacheKey, new Date().getTime(), invitedId.toString());
		redisCache.expire(cacheKey, Constants.INVITE_EXPIRE_TIME_SECOND);
		log.info("save invite status succeed; question Id = {}, inviteId = {}, invitedId = {}", new Object[]{questionId, inviteId, invitedId});
		return result ? InviteStatus.INVITE_NOT_YET.getValue() : InviteStatus.INVITE_ERROR.getValue();
	}

	@Override
	public List<Long> getRecomInviteList(Integer start, Integer count) {
		List<Long> idList = new ArrayList<>();
		Set<String> ids = adminRedisCache.zRevRange(CacheConstants.INVITED_USER, start, start + count - 1);
		ids.forEach((String id) -> idList.add(Long.valueOf(id)));
		return idList;
	}

	@Override
	public List<Long> getUserInviteList(Long questionId, Long inviteId) {
		String key = InviteUtil.getInviteCacheKey(questionId, inviteId);
		Long max = new Date().getTime();
		Long min = getExpireTime();
		Set<String> ids = redisCache.zRevRangeByScore(key, min.toString(), max.toString());
		List<Long> idList = new ArrayList<>();
		ids.forEach((String id) -> idList.add(Long.valueOf(id)));
		return idList;
	}
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -2);
		Date date2 = calendar.getTime();
		System.out.println("now is : " + date);
		System.out.println("two days ago is " + date2);
	}
	
	public Long getExpireTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, Constants.INVITE_EXPIRE_TIME);
		Date date = calendar.getTime();
		return date.getTime();
	}

	@Override
	public Integer getRecomNum() {
		return (int) adminRedisCache.zCount(CacheConstants.INVITED_USER);
	}

}
