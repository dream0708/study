package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aspectj.apache.bcel.classfile.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.support.logging.Log;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.dao.EliteFollowDao;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.enums.EliteFollowType;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteFollow;



public class EliteFollowDaoImpl extends JdbcDaoImpl implements EliteFollowDao{
	
	private static final Logger log = LoggerFactory.getLogger(EliteFollowDaoImpl.class);
	private CacheManager redisCacheManager;
	private CacheManager ssdbCacheManager;
	private RedisCache redisCache;
	private SsdbCache ssdbCache;
	
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	
	public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
		this.ssdbCacheManager = ssdbCacheManager;
	}

	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_FOLLOW);
		ssdbCache = (SsdbCache) ssdbCacheManager.getCache(SsdbConstants.CACHE_FIRST_FOLLOW);
	}
	
	@Override
	public Long setEliteFollow(EliteFollow eliteFollow) {
		Long retVal = -1l;
		if(null != eliteFollow){
			Long eliteFollowId = super.insert(eliteFollow);
			if(null != eliteFollowId && eliteFollowId > 0){	
				retVal = eliteFollowId;
				log.info("insert eliteFollow successfully. eliteFollow id = " + retVal.toString());
				eliteFollow.setId(eliteFollowId);
				updateCache(eliteFollow);
			}
		}
		
		return retVal;
	}

	@Override
	public Boolean updateEliteFollow(EliteFollow eliteFollow) {
		Boolean retVal = false;
		if(null != eliteFollow){
			int result = super.update(eliteFollow);
			retVal = (result > 0);
			log.info("update eliteFollow successfully. eliteFollow id = " + eliteFollow.getId().toString());
			updateCache(eliteFollow);
		}
		return retVal;
	}

	@Override
	public Integer getEliteFollowCountByType(EliteFollowType eliteFollowType) {
		Integer retVal = 0;
		Integer result = redisCache.zRangeByScore(Constants.Elite_Follow_Key, String.valueOf(eliteFollowType.getValue()), String.valueOf(eliteFollowType.getValue())).size();
		if(null == result ||  result <= 0){		
			Criteria criteria = Criteria.create(EliteFollow.class)
					.where("type", new Object[]{eliteFollowType.getValue()})
					.and("status", new Object[]{EliteFeatureStatus.STATUS_WORK.getValue()});
			List<EliteFollow> follows = super.queryList(criteria);
			if(null != follows && follows.size() > 0){
				result = follows.size();
				//更新缓存
				follows.forEach(this::updateCache);
			}
		}
		if(null != result) retVal = result;
		return retVal;
	}

	@Override
	public List<EliteFollow> getEliteFollowByType(EliteFollowType eliteFollowType, Integer start, Integer count) {
		List<EliteFollow> eliteFollows = new ArrayList<>();
		Integer num = getEliteFollowCountByType(eliteFollowType);
		if(null != num && num > 0){
			Set<String> idSet = redisCache.zRangeByScore(Constants.Elite_Follow_Key, String.valueOf(eliteFollowType.getValue()), String.valueOf(eliteFollowType.getValue()));
			if(null != idSet && idSet.size() > 0){
				List<EliteFollow> temp = new ArrayList<EliteFollow>();
				idSet.forEach(id -> temp.add(getEliteFollowById(Long.valueOf(id))));
				int end = (temp.size() > start + count) ? start + count : temp.size();
				eliteFollows = temp.subList(start, end);			
			} else {
				Criteria criteria = Criteria.create(EliteFollow.class)
											.where("type", new Object[]{eliteFollowType.getValue()})
											.and("status", new Object[]{EliteFeatureStatus.STATUS_WORK.getValue()});
				criteria.setStart(start);
				criteria.setCount(count);
				criteria.setLimit(true);
				eliteFollows = super.queryList(criteria);
			}
		}
		return eliteFollows != null ? eliteFollows : new ArrayList<EliteFollow>();
	}

	public EliteFollow getEliteFollowById(Long id){
		EliteFollow eliteFollow = new EliteFollow();
		eliteFollow = (EliteFollow) redisCache.get(id.toString());
		if(null == eliteFollow || eliteFollow.getId() <=0 ){
			Criteria criteria = Criteria.create(EliteFollow.class).where("id", new Object[]{id});
			EliteFollow result = super.querySingleResult(criteria);
			if(null != result && result.getId() > 0) eliteFollow = result;
		}
		return eliteFollow;
	}
	
	public void removeCache() {
		redisCache.remove(Constants.Elite_Follow_Key);
	}
	
	private void updateCache(EliteFollow eliteFollow) {
		if(EliteFeatureStatus.STATUS_WORK.getValue() == eliteFollow.getStatus()){
			redisCache.zAdd(Constants.Elite_Follow_Key, eliteFollow.getType().longValue(),eliteFollow.getId().toString());
			redisCache.put(eliteFollow.getId().toString(), eliteFollow);
			if(EliteFollowType.ELITE_USER.getValue() == eliteFollow.getType()){
				ssdbCache.zAdd(Constants.ELITE_FOLLOW_USER_LOGIN, Constants.ELITE_FOLLOW_USER_LOGIN_SCORE, eliteFollow.getBpId().toString());
				redisCache.remove(Constants.ELITE_FOLLOW_USER_KEY);
			}
			else{
				redisCache.remove(Constants.ELITE_FOLLOW_TAG_KEY);
			}
								
		} else {
			redisCache.zRem(Constants.Elite_Follow_Key, eliteFollow.getId().toString());
			redisCache.remove(eliteFollow.getId().toString());
			if(EliteFollowType.ELITE_USER.getValue() == eliteFollow.getType()){
				ssdbCache.zRem(Constants.ELITE_FOLLOW_USER_LOGIN, eliteFollow.getBpId().toString());
				redisCache.remove(Constants.ELITE_FOLLOW_USER_KEY);
			}
			else{
				redisCache.remove(Constants.ELITE_FOLLOW_TAG_KEY);
			}
		}
	}
}
