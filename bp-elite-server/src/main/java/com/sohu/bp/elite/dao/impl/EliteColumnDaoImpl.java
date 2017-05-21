package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.dao.EliteColumnDao;
import com.sohu.bp.elite.enums.EliteFeatureUpdateCacheType;
import com.sohu.bp.elite.enums.EliteTopicStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteColumn;

public class EliteColumnDaoImpl extends JdbcDaoImpl implements EliteColumnDao{
	private static final Logger log = LoggerFactory.getLogger(EliteColumnDaoImpl.class);
	
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	
	
	public void init(){
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.ELITE_COLUMN);
	}
	
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	@Override
	public Long insert(EliteColumn eliteColumn) {
		if(null == eliteColumn) return null;
		Long retVal = -1l;
		if(null == eliteColumn.getCreateTime()) eliteColumn.setCreateTime(new Date());
		if(null == eliteColumn.getUpdateTime()) eliteColumn.setUpdateTime(new Date());
		retVal = super.insert(eliteColumn);
		if(null == retVal || retVal <= 0){
			log.error("insert eliteColumn failed, id = {} and title = {}", new Object[]{retVal, eliteColumn.getName()});
			return retVal;
		}
		eliteColumn.setId(retVal);
		log.info("insert eliteColumn succeed, {}", eliteColumn);
		updateCache(eliteColumn, EnumSet.allOf(EliteFeatureUpdateCacheType.class));
		return retVal;
	}

	@Override
	public EliteColumn getEliteColumnById(Long id) {
		if(null == id || id <=0 ) return null;
		EliteColumn eliteColumn = new EliteColumn();
		eliteColumn = (EliteColumn) redisCache.get(id.toString());
		if(null == eliteColumn || null == eliteColumn.getId() || eliteColumn.getId() <= 0){
			eliteColumn = super.get(EliteColumn.class, id);
		}
		updateCache(eliteColumn, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE));
		return eliteColumn;
	}

	@Override
	public Long getEliteColumnCount() {
		Long num = redisCache.zCount(CacheConstants.ELITE_COLUMN_KEY);
		if(null == num || num <= 0){
			List<EliteColumn> columns = super.queryList(EliteColumn.class);
			if(null != columns && columns.size() > 0){
				num = (long) columns.size();
				//重新加载缓存
				columns.forEach(column -> updateCache(column, EnumSet.allOf(EliteFeatureUpdateCacheType.class)));
			}
		}
		return num;
	}

	@Override
	public Long getEliteColumnCountByStatus(int status) {		
		Long num = 0l;
		Criteria criteria = new Criteria(EliteColumn.class).where("status", new Object[]{status});
		if(status == EliteTopicStatus.STATUS_WORK.getValue()) {
			num = redisCache.zCount(CacheConstants.ELITE_COLUMN_STATUS_KEY);
			if(null == num || num <= 0){
				List<EliteColumn> columns = super.queryList(criteria);
				if(null != columns && columns.size() > 0){
					num = (long) columns.size();
					//重新加载缓存
					columns.forEach(column -> updateCache(column, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET)));
				}
			}
		}
		if(null == num || num <= 0){
			num = (long) super.queryCount(criteria);
		}
		return num;
	}

	@Override
	public List<EliteColumn> getAllEliteColumn(int start, int count) {
		List<EliteColumn> eliteColumns = new ArrayList<>();
		Long num = getEliteColumnCount();
		if(null != num && num > 0){
			Set<String> ids = redisCache.zRevRange(CacheConstants.ELITE_COLUMN_KEY, start, start + count -1);
			if(null != ids && ids.size() > 0){
				List<EliteColumn> temp = new ArrayList<>();
				ids.forEach(id -> temp.add(getEliteColumnById(Long.valueOf(id))));
				eliteColumns = temp;
			} else {
				Criteria criteria = new Criteria(EliteColumn.class).desc("updateTime");
				criteria.setLimit(true);
				criteria.setStart(start);
				criteria.setCount(count);
				eliteColumns = super.queryList(criteria);
			}
		}
		return eliteColumns;
	}

	@Override
	public List<EliteColumn> getAllEliteColumnByStatus(int start, int count, int status) {
		List<EliteColumn> eliteColumns = new ArrayList<>();
		Long num = getEliteColumnCountByStatus(status);
		if(null != num && num > 0){
			Set<String> ids = new HashSet<>();
			if(status == EliteTopicStatus.STATUS_WORK.getValue()) ids = redisCache.zRevRange(CacheConstants.ELITE_COLUMN_STATUS_KEY, start, start + count -1);
			if(null != ids && ids.size() > 0){
				List<EliteColumn> temp = new ArrayList<>();
				ids.forEach(id -> temp.add(getEliteColumnById(Long.valueOf(id))));
				eliteColumns = temp;
			} else {
				Criteria criteria = new Criteria(EliteColumn.class).desc("updateTime").where("status", new Object[]{status});
				criteria.setLimit(true);
				criteria.setStart(start);
				criteria.setCount(count);
				eliteColumns = super.queryList(criteria);
			}
		}
		return eliteColumns;
	}

	@Override
	public void update(EliteColumn eliteColumn) {
		if(null == eliteColumn || null == eliteColumn.getId() || eliteColumn.getId() <=0) return;
		if(null == eliteColumn.getUpdateTime()) eliteColumn.setUpdateTime(new Date());
		if(super.update(eliteColumn) > 0){
			updateCache(eliteColumn, EnumSet.of(EliteFeatureUpdateCacheType.UPDATE_SET, EliteFeatureUpdateCacheType.UPDATE_SINGLE, EliteFeatureUpdateCacheType.UPDATE_STATUS_SET));
			log.info("update eliteColumn {} succeed!", new Object[]{eliteColumn});
		}
	}
	
	@Override
	public void removeCache() {
		redisCache.remove(CacheConstants.ELITE_COLUMN_KEY);
		redisCache.remove(CacheConstants.ELITE_COLUMN_STATUS_KEY);
	}

	private void updateCache(EliteColumn eliteColumn, Set<EliteFeatureUpdateCacheType> types){
		if(null == eliteColumn || null == eliteColumn.getId() || null == eliteColumn.getUpdateTime()) return;
		if(eliteColumn.getPublishTime() == null) eliteColumn.setPublishTime(new Date());
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SINGLE))	redisCache.put(eliteColumn.getId(), eliteColumn);
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_STATUS_SET))
			if(eliteColumn.getStatus().equals(EliteTopicStatus.STATUS_WORK.getValue())){
				redisCache.zAdd(CacheConstants.ELITE_COLUMN_STATUS_KEY, eliteColumn.getPublishTime().getTime(), eliteColumn.getId().toString());
			} else {
				redisCache.zRem(CacheConstants.ELITE_COLUMN_STATUS_KEY, eliteColumn.getId().toString());
			}
		if(types.contains(EliteFeatureUpdateCacheType.UPDATE_SET))
			redisCache.zAdd(CacheConstants.ELITE_COLUMN_KEY, eliteColumn.getPublishTime().getTime(), eliteColumn.getId().toString());
	}
}
