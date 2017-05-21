package com.sohu.bp.elite.service.web.impl;



import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.action.bean.person.UserRestrictBean;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.service.web.UserRestrictService;


public class UserRestrictServiceImpl implements UserRestrictService {
	
	private CacheManager cacheManager;
	private RedisCache redisCache;
	private Integer cacheTimeSeconds = 60 * 60 * 24; //设置缓存过期时间为1天
	
	public void init(){
		redisCache = (RedisCache) cacheManager.getCache(CacheConstants.CACHE_ELITE_RESTRICT);
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public boolean addQuestionNum(Long bpId) {
		Boolean retVal = false;
		String cacheName = getCacheName(bpId);
		UserRestrictBean  userRestrict = new UserRestrictBean(0, 1);
		if(redisCache.exist(cacheName)){ 
			userRestrict = JSON.toJavaObject((JSONObject) redisCache.get(cacheName), UserRestrictBean.class);
			Integer questionNum = userRestrict.getQuestionNum() + 1;
			userRestrict.setQuestionNum(questionNum);
			redisCache.put(cacheName, JSON.toJSON(userRestrict));
			retVal = true;
		} else {
		
			redisCache.put(cacheName, cacheTimeSeconds, JSON.toJSON(userRestrict));	
			retVal = true;
		}
		return retVal;
	}

	@Override
	public boolean addAnswerNum(Long bpId) {
		Boolean retVal = false;
		String cacheName = getCacheName(bpId);
		UserRestrictBean  userRestrict = new UserRestrictBean(1, 0);
		if(redisCache.exist(cacheName)){ 
			userRestrict = JSON.toJavaObject((JSONObject) redisCache.get(cacheName), UserRestrictBean.class);
			Integer answerNum = userRestrict.getAnswerNum() + 1;
			userRestrict.setAnswerNum(answerNum);
			redisCache.put(cacheName, JSON.toJSON(userRestrict));
			retVal = true;
		} else {
		
			redisCache.put(cacheName, cacheTimeSeconds, JSON.toJSON(userRestrict));	
			retVal = true;
		}
		return retVal;
	}

	@Override
	public boolean isQuestionRestrict(Long bpId) {
		Boolean retVal = false;
		String cacheName = getCacheName(bpId);
		if(redisCache.exist(cacheName)){
			UserRestrictBean userRestrict = JSON.toJavaObject((JSONObject) redisCache.get(cacheName), UserRestrictBean.class);
			retVal = (userRestrict.getQuestionNum() >= Constants.QUESTION_RESTRICT_NUM) ? true : false;
		} else {
			retVal = false;
		}
		return retVal;
	}

	@Override
	public boolean isAnswerRestrict(Long bpId) {
		Boolean retVal = false;
		String cacheName = getCacheName(bpId);
		if(redisCache.exist(cacheName)){
			UserRestrictBean userRestrict = JSON.toJavaObject((JSONObject) redisCache.get(cacheName), UserRestrictBean.class);
			retVal = (userRestrict.getAnswerNum() >= Constants.ANSWER_RESTRICT_NUM) ? true : false;
		} else {
			retVal = false;
		}
		return retVal;
	}
	
	public String getCacheName(Long bpId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String nowDate = sdf.format(date);
		String cacheName = nowDate + "-" + bpId.toString();
		return cacheName;
	}

}
