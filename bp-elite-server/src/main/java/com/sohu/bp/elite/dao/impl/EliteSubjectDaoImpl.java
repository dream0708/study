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
import com.sohu.bp.elite.dao.EliteSubjectDao;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.enums.EliteFeatureUpdateCacheType;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteSubject;



/**
 * 
 * @author zhijungou
 * 2016年8月9日
 */
public class EliteSubjectDaoImpl extends JdbcDaoImpl implements EliteSubjectDao{

	private static final  Logger log = LoggerFactory.getLogger(EliteSubjectDaoImpl.class);
	private CacheManager redisCacheManager;
	private RedisCache mutexLock;
	private RedisCache subjectRedisCache;
	
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
		mutexLock = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_SUBJECT_MUTEX_LOCK);
		subjectRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_SUBJECT);
	}
	
	@Override
	public Long insert(EliteSubject eliteSubject){
		Long retVal = (long) -1;
		if(null == eliteSubject) return retVal;
		eliteSubject.setCreateTime(new Date());

		Long id =super.insert(eliteSubject);
		if(null == id || id.longValue() <= 0) {
			log.error("insert eliteSubject failed, id={}",eliteSubject.getId());
			return retVal;
		}
		
		eliteSubject.setId(id);
		retVal = id;
		updateCache(eliteSubject, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET, EliteFeatureUpdateCacheType.UPDATE_SET));
		log.info("updated eliteSubject(id={}, userId={}, title={}, brief={}, detail={})",new Object[]{
				eliteSubject.getId(), eliteSubject.getUserId(), eliteSubject.getName(), eliteSubject.getBrief(), eliteSubject.getDetail()});
		return retVal;
	}
	
	@Override
	public Long update(EliteSubject eliteSubject){
		if(null == eliteSubject || eliteSubject.getId() == null) return null;
		eliteSubject.setUpdateTime(new Date());
		log.info("updating eliteSubject(id={}, userId={}, title={}, brief={}, detail={})",new Object[]{
			eliteSubject.getId(), eliteSubject.getUserId(), eliteSubject.getName(), eliteSubject.getBrief(), eliteSubject.getDetail()});
		
		if(super.update(eliteSubject) > 0) {
			log.info("updated eliteSubject(id={}, userId={}, title={}, brief={}, detail={})",new Object[]{
					eliteSubject.getId(), eliteSubject.getUserId(), eliteSubject.getName(), eliteSubject.getBrief(), eliteSubject.getDetail()});
			updateCache(eliteSubject, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET, EliteFeatureUpdateCacheType.UPDATE_SET));
			}
		else
			{log.error("update eliteSubject failed, id={}",eliteSubject.getId());}
		return eliteSubject.getId();
	}
	
	@Override
	public Long getHistoryCountByStatus(final Integer status){
		Long count = 0l;
		if(null == status) return count;
		Long min,max;
		if(status.equals(EliteFeatureStatus.STATUS_WORK.getValue())) {
			min = 0l;
			max = new Date().getTime();
		} else {
			min = -new Date().getTime();
			max = 0l;
		}
		Set<String> ids = subjectRedisCache.zRevRangeByScore(Constants.Elite_Subject_Status_Key, min.toString(), max.toString());
		if(null != ids && ids.size() > 0) count = Long.valueOf(ids.size()); 
		else{
			Criteria sql = new Criteria(EliteSubject.class);
			sql.where("status", new Object[]{status});
			List<EliteSubject> subjects = super.queryList(sql);
			if(null != subjects && subjects.size() > 0) {
				count = (long) subjects.size();
				//重新加载缓存
				subjects.forEach(subject -> updateCache(subject, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)));
			}
		}
		return count;
	}
	
	@Override
	public List<EliteSubject> getHistoryByStatus(final Integer status, final Integer start, final Integer count){
		List<EliteSubject> eliteSubjectList = new ArrayList<EliteSubject>();
		Long total = getHistoryCountByStatus(status);
		if(total.longValue() > 0){	
			List<EliteSubject> sqlResultList = new ArrayList<>();
			Long min,max;
			if(status.equals(EliteFeatureStatus.STATUS_WORK.getValue())) {
				min = 0l;
				max = new Date().getTime();
			} else {
				min = -new Date().getTime();
				max = 0l;
			}
			Set<String> ids = subjectRedisCache.zRevRangeByScore(Constants.Elite_Subject_Status_Key, min.toString(), max.toString());
			if(null != ids && ids.size() > 0){	
				List<String> idList = new ArrayList<String>(ids);
				for(int i = start; i < start + count && i < ids.size(); i++){
					sqlResultList.add(getHistoryById(Long.parseLong(idList.get(i))));
				}
				if(null != sqlResultList &&sqlResultList.size() > 0) eliteSubjectList = sqlResultList;
			} else{
				Criteria sql = new Criteria(EliteSubject.class);
				sql.where("status", new Object[]{status});
				sql.desc("updateTime");
				sql.setLimit(true);
				sql.setStart(start);
				sql.setCount(count);
				sqlResultList = super.queryList(sql);
				if (null != sqlResultList && sqlResultList.size() > 0) eliteSubjectList = sqlResultList;
			}
		}
		return eliteSubjectList;
	}
	
	@Override
	public EliteSubject getHistoryById(Long id){
		EliteSubject eliteSubject = new EliteSubject();
		if(null == id || id.longValue() <= 0) return null;
		eliteSubject = (EliteSubject) subjectRedisCache.get(id.toString());
		if(null != eliteSubject && eliteSubject.getId() > 0) return eliteSubject;
		else{
			eliteSubject = super.get(EliteSubject.class, id);
			updateCache(eliteSubject, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE));
		}
		return eliteSubject;
	}
	
	@Override
	public Long getAllHistoryCount(){
		Long count = 0l;
		Long sqlCount = subjectRedisCache.zCount(Constants.Elite_Subject_Key);
		if(sqlCount > 0) count = sqlCount;
		else{
			List<EliteSubject> subjects = super.queryList(EliteSubject.class);
			if(null != subjects && subjects.size() > 0){
				count = (long) subjects.size();
			//重新加载缓存
				subjects.forEach(subject -> updateCache(subject, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_SET, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)));
			}
		}
		return count;
	}
	
	@Override
	public List<EliteSubject> getAllHistory(final Integer start, final Integer count){	
			List<EliteSubject> eliteSubjectList = new ArrayList<EliteSubject>();
			Long num = getAllHistoryCount();
			if(num.longValue() > 0){	
				List<EliteSubject> sqlResultList = new ArrayList<>();
				Set<String> ids = subjectRedisCache.zRevRange(Constants.Elite_Subject_Key, start, start+count-1);
				if(null !=ids && ids.size() > 0){
					Iterator<String> it = ids.iterator();
					while(it.hasNext()){
						String id = it.next();
						sqlResultList.add(getHistoryById(Long.parseLong(id)));
					}
					if(null != sqlResultList && sqlResultList.size() > 0) eliteSubjectList = sqlResultList;			
				}
				else{
					Criteria sql = new Criteria(EliteSubject.class);
					sql.setStart(start);
					sql.setCount(count);
					sql.setLimit(true);
					sql.desc("updateTime");
					sqlResultList = super.queryList(sql);
					if(null != sqlResultList && sqlResultList.size() > 0) eliteSubjectList = sqlResultList;
				}
			}
			
			return eliteSubjectList;
	}
	
	@Override
	public boolean setMutexLock(final String ids){	
		if(null == ids) return true;
		if(!mutexLock.setnx(ids, Constants.CACHE_OCCUPY_VALUE)){
			log.warn("set redis mutex lock failed. id="+ids);
			return false;
		}
		return true;
	}
	
	@Override
	public void removeMutexLock(final String ids){
		if(null == ids) return;
		mutexLock.remove(ids);
	}
	
	@Override
	public void removeCache() {
		subjectRedisCache.remove(Constants.Elite_Subject_Key);
		subjectRedisCache.remove(Constants.Elite_Subject_Status_Key);
	}
	
	public void updateCache(EliteSubject eliteSubject, Set<EliteFeatureUpdateCacheType> types){
		if(null == eliteSubject || null == eliteSubject.getId() || null == eliteSubject.getCreateTime()) return;
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SINGLE)) subjectRedisCache.put(eliteSubject.getId().toString(), eliteSubject);
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SET))
			subjectRedisCache.zAdd(Constants.Elite_Subject_Key, eliteSubject.getUpdateTime().getTime(), eliteSubject.getId().toString());
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)){
			Long score = eliteSubject.getStatus().equals(EliteFeatureStatus.STATUS_WORK.getValue()) ? eliteSubject.getUpdateTime().getTime(): -eliteSubject.getUpdateTime().getTime();
			subjectRedisCache.zAdd(Constants.Elite_Subject_Status_Key, score, eliteSubject.getId().toString());
		}
		
	}
}

