package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.dao.EliteTopicDao;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.enums.EliteFeatureUpdateCacheType;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteTopic;




/**
 * 
 * @author zhijungou
 *	2016/8/15
 */
public class EliteTopicDaoImpl extends JdbcDaoImpl implements EliteTopicDao {

	private static final Logger log = LoggerFactory.getLogger(EliteAnswerDaoImpl.class);
	private CacheManager redisCacheManager;
	private RedisCache mutexLock;
	private RedisCache topicRedisCache;
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	public RedisCache getMutexLock() {
		return mutexLock;
	}

	public void setMutexLock(RedisCache mutexLock) {
		this.mutexLock = mutexLock;
	}
	
	public void init(){
		mutexLock = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_TOPIC_MUTEX_LOCK);
		topicRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_TOPIC);		
	}

	@Override
	public Long insert(EliteTopic eliteTopic) {
		Long retVal = 0l;
		if(null == eliteTopic) return retVal;
		if(null == eliteTopic.getCreateTime()) eliteTopic.setCreateTime(new Date());
		Long id = super.insert(eliteTopic);
		if(null == id && id.longValue() < 0) {
			log.error("insert failed. userId = {}, title = {}, brief = {}", new Object[]{eliteTopic.getUserId(),eliteTopic.getTitle(),eliteTopic.getBrief()});
			return retVal;
		}
		eliteTopic.setId(id);
		retVal = id;
		log.info("insert success. id = {}, userId = {}, title = {}, brief = {}", new Object[]{eliteTopic.getId(), eliteTopic.getUserId(), eliteTopic.getTitle(),eliteTopic.getBrief()});
		updateCache(eliteTopic, EnumSet.allOf(EliteFeatureUpdateCacheType.class));
		return retVal;		
	}

	@Override
	public Boolean update(EliteTopic eliteTopic) {
		if(null == eliteTopic || eliteTopic.getId() == null) return false;
		if(null == eliteTopic.getUpdateTime()) eliteTopic.setUpdateTime(new Date());
		if(super.update(eliteTopic) > 0){
			log.info("update success. id = {}, userId = {}, title = {} , brief = {}", new Object[]{eliteTopic.getId(), eliteTopic.getUserId(),eliteTopic.getTitle(),eliteTopic.getBrief()});
			updateCache(eliteTopic, EnumSet.allOf(EliteFeatureUpdateCacheType.class));
			return true;
		}
		else{
			log.error("update failed. id = {}, userId = {}, title = {} , brief = {}", new Object[]{eliteTopic.getId(), eliteTopic.getUserId(),eliteTopic.getTitle(),eliteTopic.getBrief()});
			return false;
		}
	}

	@Override
	public Long getTopicCount() {
		Long count = 0l;
		Long sqlCount = topicRedisCache.zCount(Constants.Elite_Topic_Key);
		if(null != sqlCount && sqlCount > 0) {
			count = sqlCount;
		} else{
			List<EliteTopic> topics = super.queryList(EliteTopic.class);
			if(null != topics && topics.size() > 0){
				count = (long) topics.size();
				//重新加载缓存
				topics.forEach(topic -> updateCache(topic, EnumSet.allOf(EliteFeatureUpdateCacheType.class)));
			}
			log.info("topic num is:" + Long.toString(count));
		}
		return count;
	}

	@Override
	public List<EliteTopic> getTopicHistory(Integer start, Integer count) {
		List<EliteTopic> eliteTopics = new ArrayList<>();
		Long num = getTopicCount();
		if(null != num && num > 0){
			Set<String> ids = topicRedisCache.zRevRange(Constants.Elite_Topic_Key, start.longValue(), start.longValue() + count.longValue() -1l);
			if(null != ids && ids.size() > 0){
				List<EliteTopic> temp = new ArrayList<EliteTopic>();
				ids.forEach(id -> temp.add(getTopicById(Long.valueOf(id))));
				eliteTopics = temp;
			} else {
				Criteria criteria = Criteria.create(EliteTopic.class);
				criteria.setStart(start);
				criteria.setCount(count);
				criteria.setLimit(true);
				criteria.desc("updateTime");
				eliteTopics = super.queryList(criteria);
			}
		}
		return eliteTopics;
	}

	@Override
	public Long getTopicCountByStatus(Integer status) {
		Long count = 0l;
		Long min,max;
		if(status.equals(EliteFeatureStatus.STATUS_WORK.getValue())) {
			min = 0l;
			max = new Date().getTime();
		} else {
			min = -new Date().getTime();
			max = 0l;
		}
		Set<String> ids = topicRedisCache.zRevRangeByScore(Constants.Elite_Topic_Status_Key, min.toString(), max.toString());
		if(null != ids && ids.size() > 0) {
			count = Long.valueOf(ids.size());
		} else {
			Criteria criteria = Criteria.create(EliteTopic.class).where("status", new Object[]{status});
			List<EliteTopic> topics = super.queryList(criteria);
			if(null != topics && topics.size() > 0) {
				count = (long) topics.size();
				//重新加载缓存
				topics.forEach(topic -> updateCache(topic, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)));
			}
		}
		return count;
	}

	@Override
	public List<EliteTopic> getTopicHistoryByStatus(Integer status, Integer start, Integer count) {
		List<EliteTopic> eliteTopics = new ArrayList<>();
		List<EliteTopic> sqlResult = new ArrayList<>();
		Long num = getTopicCount();
		if(null != num && num.longValue() > 0){	
			Long min,max;
			if(status.equals(EliteFeatureStatus.STATUS_WORK.getValue())) {
				min = 0l;
				max = new Date().getTime();
			} else {
				min = -new Date().getTime();
				max = 0l;
			}
			Set<String> ids = topicRedisCache.zRevRangeByScore(Constants.Elite_Topic_Status_Key, min.toString(), max.toString());
			if(null != ids && ids.size() > 0){
				List<String> idList = new ArrayList<>(ids);
				for(int index = start; index < start + count && index < ids.size(); index++){
					sqlResult.add(getTopicById(Long.parseLong(idList.get(index))));
				}
				if(null != sqlResult && sqlResult.size() > 0) eliteTopics = sqlResult;
			} else{
				Criteria criteria = Criteria.create(EliteTopic.class);
				criteria.where("status", new Object[]{status});
				criteria.setStart(start);
				criteria.setLimit(true);
				criteria.setCount(count);
				criteria.desc("updateTime");
				sqlResult = super.queryList(criteria);
				if(null != sqlResult && sqlResult.size() > 0) eliteTopics = sqlResult;
			}
		}
		return eliteTopics;
	}

	@Override
	public EliteTopic getTopicById(Long id) {
		if(null == id || id <= 0) return null;
		EliteTopic eliteTopic = new EliteTopic();
		EliteTopic sqlResult = (EliteTopic) topicRedisCache.get(id.toString());
		if(null != sqlResult && sqlResult.getId() > 0) {
			eliteTopic = sqlResult;
		} else {
			Criteria criteria = Criteria.create(EliteTopic.class);
			criteria.where("id", new Object[]{id});
			sqlResult = super.querySingleResult(criteria);
			if(null != sqlResult && sqlResult.getId() > 0) {
				eliteTopic = sqlResult;
				updateCache(eliteTopic, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE));
			}
		}
		return eliteTopic;		
	}

	@Override
	public boolean setMutexLock(String id) {
		if (null == id) return true;
		if (!mutexLock.setnx(id, Constants.CACHE_OCCUPY_VALUE)){
			log.warn("set redis mutex lock failed. id="+id);
			return false;
		}
		return true;
	}

	@Override
	public void removeMutexLock(String id) {
		if (null == id) return;
		mutexLock.remove(id);
	}
	
	@Override
	public void removeCache() {
		topicRedisCache.remove(Constants.Elite_Topic_Key);
		topicRedisCache.remove(Constants.Elite_Topic_Status_Key);
	}
	public void updateCache(EliteTopic eliteTopic, Set<EliteFeatureUpdateCacheType> types){
		if(null == eliteTopic || null == eliteTopic.getId() ||null == eliteTopic.getCreateTime()) return;
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SINGLE)) topicRedisCache.put(eliteTopic.getId().toString(), eliteTopic);
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SET))	topicRedisCache.zAdd(Constants.Elite_Topic_Key, eliteTopic.getUpdateTime().getTime(), eliteTopic.getId().toString());
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)){
			Long score = eliteTopic.getStatus().equals(EliteFeatureStatus.STATUS_WORK.getValue()) ? eliteTopic.getUpdateTime().getTime(): -eliteTopic.getUpdateTime().getTime();
			topicRedisCache.zAdd(Constants.Elite_Topic_Status_Key, score, eliteTopic.getId().toString());
		}
	}

}
