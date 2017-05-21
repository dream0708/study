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
import com.sohu.bp.elite.dao.EliteFragmentDao;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.enums.EliteFeatureUpdateCacheType;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteFragment;

public class EliteFragmentDaoImpl extends JdbcDaoImpl implements EliteFragmentDao{
	private static final Logger log = LoggerFactory.getLogger(EliteFragmentDaoImpl.class);
	private CacheManager redisCacheManager;
	private RedisCache mutexLock;
	private RedisCache fragmentRedisCache;
	
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	public void init()
	{
		mutexLock = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_FRAGMENT_MUTEX_LOCK);
		fragmentRedisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_FRAGMENT);
	}

	@Override
	public Long setEliteFragment(EliteFragment eliteFragment) {
		Long retVal = -1l;
		if(null != eliteFragment){
			Long id = super.insert(eliteFragment);
			if(null != id && id > 0) {
				retVal = id;
				eliteFragment.setId(id);
				updateCache(eliteFragment, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_SET));
			}
		}
		return retVal;
	}

	@Override
	public Boolean updateEliteFragment(EliteFragment eliteFragment) {
		Boolean retVal = false;
		if(null != eliteFragment){
			int result = super.update(eliteFragment);
			retVal = (result > 0);
			updateCache(eliteFragment, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_SET));
		}
		return retVal;
	}

	@Override
	public Integer getFragmentCount() {
		Integer retVal = 0;
		Integer sqlCount = new Long(fragmentRedisCache.zCount(Constants.Elite_Fragment_Key)).intValue();
		if(null != sqlCount && sqlCount > 0) {
			retVal = sqlCount;
		} else {
			List<EliteFragment> fragments = super.queryList(EliteFragment.class);
			if(null != fragments && fragments.size() > 0){
				retVal = fragments.size();
				//更新缓存
				fragments.forEach(fragment -> updateCache(fragment, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_SET)));
			}
		}
		return retVal;
	}

	@Override
	public Integer getFragmentCountByType(Integer type) {
		Integer retVal = 0;
		Criteria criteria = Criteria.create(EliteFragment.class).where("type", new Object[]{type});
		Integer sqlCount = super.queryCount(criteria);
		if(null != sqlCount && sqlCount > 0) retVal = sqlCount;
		return retVal;
	}

	@Override
	public List<EliteFragment> getAllFragment() {
		List<EliteFragment> fragments = new ArrayList<>();
		Long count = getFragmentCount().longValue();
		Set<String> ids = fragmentRedisCache.zRevRange(Constants.Elite_Fragment_Key, 0l, count-1l);
		if(null !=ids && ids.size() >0){	
			List<EliteFragment> temp = new ArrayList<>();
			ids.forEach(id -> temp.add(getFragmentById(Long.valueOf(id))));
			fragments = temp;
		} else {
			fragments = super.queryList(EliteFragment.class);
		}
		return fragments != null ? fragments : new ArrayList<EliteFragment>();
	}
	
	
	@Override
	public List<EliteFragment> getFragmentByType(Integer type) {
		List<EliteFragment> eliteFragments = new ArrayList<>();
		Criteria criteria = Criteria.create(EliteFragment.class).where("type", new Object[]{type});
		List<EliteFragment> sqlResult = super.queryList(criteria);
		if(null != sqlResult && sqlResult.size() > 0) eliteFragments = sqlResult;
		return eliteFragments;
	}

	@Override
	public EliteFragment getFragmentById(Long id) {
		EliteFragment eliteFragment = new EliteFragment();
		eliteFragment = (EliteFragment) fragmentRedisCache.get(id);
		if(null == eliteFragment){
			Criteria criteria =	Criteria.create(EliteFragment.class).where("id", new Object[]{id});
			EliteFragment sqlResult = super.querySingleResult(criteria);
			if(null != sqlResult && sqlResult.getId() > 0) {
				eliteFragment = sqlResult;
				updateCache(eliteFragment, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE));
			}
		}
		return eliteFragment;
	}

	@Override
	public Boolean setMutexLock(String id) {
		if(null == id) return true;
		if(!mutexLock.setnx(id, Constants.CACHE_OCCUPY_VALUE)){
			log.warn("set redis mutex lock failed. id="+id);
			return false;
		}
		return true;
	}

	@Override
	public void removeMutexLock(String id) {
		if(null == id) return;
		try{
			mutexLock.remove(id);
		}catch(Exception e)
		{
			log.error("remove redis mutex lock failed. id="+id);
		}
	}
	
	@Override
	public void removeCache() {
		fragmentRedisCache.remove(Constants.Elite_Fragment_Key);
	}

	public void updateCache(EliteFragment eliteFragment, Set<EliteFeatureUpdateCacheType> types){
		if(null == eliteFragment || null == eliteFragment.getId() || null == eliteFragment.getCreateTime()) return;
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SINGLE))
			fragmentRedisCache.put(eliteFragment.getId().toString(), eliteFragment);
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SET))
			fragmentRedisCache.zAdd(Constants.Elite_Fragment_Key, eliteFragment.getUpdateTime().getTime(), eliteFragment.getId().toString());
	}

}
