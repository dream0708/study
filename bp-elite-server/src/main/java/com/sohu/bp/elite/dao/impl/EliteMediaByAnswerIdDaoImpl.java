package com.sohu.bp.elite.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.cache.ssdb.SsdbCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.SsdbConstants;
import com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteMediaStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteMediaByanswerid;
import com.sohu.bp.elite.persistence.EliteMediaByquestionid;
import com.sohu.bp.util.CompareTool;

/**
 * 
 * @author nicholastang
 * 2016-08-16 20:12:28
 * TODO media by answerid 实现
 */
public class EliteMediaByAnswerIdDaoImpl extends JdbcDaoImpl implements EliteMediaByAnswerIdDao
{
	private Logger log = LoggerFactory.getLogger(EliteMediaByAnswerIdDaoImpl.class);
	private TablePartitioner partitioner;
	private CacheManager ssdbCacheManager;
	private CacheManager redisCacheManager;
    private SsdbCache mediaSsdb;
    private RedisCache mediaByAnswerCache;
    
	public TablePartitioner getPartitioner() {
		return partitioner;
	}

	public void setPartitioner(TablePartitioner partitioner) {
		this.partitioner = partitioner;
	}

	public CacheManager getSsdbCacheManager() {
		return ssdbCacheManager;
	}

	public void setSsdbCacheManager(CacheManager ssdbCacheManager) {
		this.ssdbCacheManager = ssdbCacheManager;
	}
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	public String getTableName(Long answerId)
	{
		return DbPartitionHelper.getTableName(partitioner, answerId);
	}
	
	public void init()
    {
    	mediaSsdb = (SsdbCache)ssdbCacheManager.getCache(SsdbConstants.ELITE_MEDIA_BYANSWER_ZSET);
    	mediaByAnswerCache = (RedisCache)redisCacheManager.getCache(CacheConstants.ELITE_MEDIA_BYANSWER);
    }
	
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#insert(com.sohu.bp.elite.persistence.EliteMediaByanswerid)
	 */
	@Override
	public Long insert(EliteMediaByanswerid eliteMediaByanswerid) {
		long retVal = -1;
		if(null == eliteMediaByanswerid || eliteMediaByanswerid.getAnswerId() == null 
				|| eliteMediaByanswerid.getMediaId() == null)
		{
			return retVal;
		}
		try
		{
			String tableName = getTableName(eliteMediaByanswerid.getAnswerId());
			if(StringUtils.isBlank(tableName))
			{
				log.error("get tablename faild.");
				return retVal;
			}
			eliteMediaByanswerid.setUpdateTime(new Date());
			eliteMediaByanswerid.setStatus(EliteMediaStatus.WORK.getValue());
			Long id = super.insert(eliteMediaByanswerid, tableName);
			if(null != id && id.longValue() > 0)
			{
				eliteMediaByanswerid.setId(id);
				
				String unionId = this.getUnionId(eliteMediaByanswerid.getAnswerId(), eliteMediaByanswerid.getMediaId());
				if(StringUtils.isBlank(unionId))
				{
					log.error("get union id faild.");
					return retVal;
				}
				
				mediaByAnswerCache.put(unionId, eliteMediaByanswerid);
				mediaSsdb.zAdd(eliteMediaByanswerid.getAnswerId().toString(), eliteMediaByanswerid.getType().intValue(), eliteMediaByanswerid.getMediaId().toString());
				retVal = id;
			}
			else
			{
				log.error("insert elite media by answer id faild.");
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = -1;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#update(com.sohu.bp.elite.persistence.EliteMediaByanswerid)
	 */
	@Override
	public boolean update(EliteMediaByanswerid eliteMediaByanswerid) {
		boolean retVal = false;
		if(null == eliteMediaByanswerid || eliteMediaByanswerid.getMediaId() == null || eliteMediaByanswerid.getAnswerId()== null)
		{
			return retVal;
		}
		
		try
		{
			String tableName = getTableName(eliteMediaByanswerid.getAnswerId());
			if(StringUtils.isBlank(tableName))
			{
				log.error("get table name failed");
				return retVal;
			}
			eliteMediaByanswerid.setUpdateTime(new Date());
			if(super.update(eliteMediaByanswerid, tableName) > 0)
			{
				String unionId = this.getUnionId(eliteMediaByanswerid.getAnswerId(), eliteMediaByanswerid.getMediaId());
				if(StringUtils.isBlank(unionId))
				{
					log.error("get union id faild.");
					return retVal;
				}
				if(!CompareTool.isEqual(eliteMediaByanswerid.getStatus(), EliteMediaStatus.WORK.getValue()))
				{
					mediaByAnswerCache.remove(unionId);
					mediaSsdb.zRem(eliteMediaByanswerid.getAnswerId().toString(), eliteMediaByanswerid.getMediaId().toString());
				}
				else
				{
					mediaByAnswerCache.put(unionId, eliteMediaByanswerid);
					mediaSsdb.zAdd(eliteMediaByanswerid.getAnswerId().toString(), eliteMediaByanswerid.getType().intValue(), eliteMediaByanswerid.getMediaId().toString());
				}
				
				retVal = true;
			}
			else
			{
				log.error("update elite media by answer id failed.mediaid={}", new String[]{eliteMediaByanswerid.getMediaId().toString()});
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = false;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#getById(java.lang.Long)
	 */
	@Deprecated
	public EliteMediaByanswerid getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#delete(java.lang.Long)
	 */
	@Deprecated
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#geMediaIdtListByAnswerId(java.lang.Long)
	 */
	@Override
	public List<Long> getMediaIdListByAnswerId(Long answerId) {
		return this.getMediaIdtListByAnswerId(answerId, true);
	}
	
	public List<Long> getMediaIdtListByAnswerId(Long answerId, boolean init) {
		if(null == answerId || answerId.longValue() <= 0)
			return null;
		if(!mediaSsdb.zExist(answerId.toString()))
		{
			if(init)
			{
				this.reloadAnswerMediaZset(answerId);
				return this.getMediaIdtListByAnswerId(answerId, false);
			}
			else
			{
				return null;
			}
		}
		
		List<Long> mediaIdList = new ArrayList<Long>();
		
		try
		{
			List<String> mediaIdStrList = mediaSsdb.zRevRangeByScore(answerId.toString(), "", 0, Long.MAX_VALUE, Integer.MAX_VALUE);
			
			if(null != mediaIdStrList && mediaIdStrList.size() > 0)
			{
				mediaIdStrList.forEach(mediaIdStr -> mediaIdList.add(Long.parseLong(mediaIdStr)));
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
		
		return mediaIdList;
	}
	
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#getMediaIdListByAnswerIdAndType(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public List<Long> getMediaIdListByAnswerIdAndType(Long answerId, Integer type) {
		return getMediaIdListByAnswerIdAndType(answerId, type, true);
	}
	
	public List<Long> getMediaIdListByAnswerIdAndType(Long answerId, Integer type, boolean init) {
		if(null == answerId || answerId.longValue() <= 0 || null == type || type.intValue() <= 0)
			return null;
		if(!mediaSsdb.zExist(answerId.toString()))
		{
			if(init)
			{
				this.reloadAnswerMediaZset(answerId);
				return this.getMediaIdListByAnswerIdAndType(answerId, type, false);
			}
			else
			{
				return null;
			}
		}
		
		List<Long> mediaIdList = new ArrayList<Long>();
		
		try
		{
			List<String> mediaIdStrList = mediaSsdb.zRevRangeByScore(answerId.toString(), "", type, type, Integer.MAX_VALUE);
			
			if(null != mediaIdStrList && mediaIdStrList.size() > 0)
			{
				mediaIdStrList.forEach(mediaIdStr -> mediaIdList.add(Long.parseLong(mediaIdStr)));
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
		
		return mediaIdList;
	}
	
	public void reloadAnswerMediaZset(Long answerId)
	{
		if(null == answerId || answerId.longValue() <= 0)
			return;
		
		mediaSsdb.zClear(answerId.toString());
		try
		{
			String tableName = getTableName(answerId);
			if(StringUtils.isBlank(tableName))
				return;
			Criteria criteria = Criteria.create(EliteMediaByanswerid.class).where("answerId", new Object[]{answerId}).and("status", new Object[]{EliteMediaStatus.WORK.getValue()});
			List<EliteMediaByanswerid> mediaByansweridList = super.queryList(criteria, tableName);
			if(null != mediaByansweridList && mediaByansweridList.size() > 0)
			{
				mediaByansweridList.forEach(mediaByAnswerid -> mediaSsdb.zAdd(mediaByAnswerid.getAnswerId().toString(), mediaByAnswerid.getType().intValue(), mediaByAnswerid.getMediaId().toString()));
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao#getByUnionId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public EliteMediaByanswerid getByUnionId(Long answerId, Long mediaId) {
		if(null == answerId || answerId.longValue() <= 0 
				|| null == mediaId || mediaId.longValue() <= 0)
			return null;
		
		String unionId = this.getUnionId(answerId, mediaId);
		if(StringUtils.isBlank(unionId))
			return null;
		EliteMediaByanswerid mediaByAnswerId = (EliteMediaByanswerid)mediaByAnswerCache.get(unionId);
		if(null == mediaByAnswerId)
		{
			String tableName = getTableName(answerId);
			if(StringUtils.isBlank(tableName))
				return null;
			Criteria criteria = Criteria.create(EliteMediaByquestionid.class)
					.where("answerId", new Object[]{answerId}).and("mediaId", new Object[]{mediaId});
			mediaByAnswerId = (EliteMediaByanswerid)super.querySingleResult(criteria, tableName);
			if(null != mediaByAnswerId)
			{
				mediaByAnswerCache.put(unionId, mediaByAnswerId);
			}
				
		}
		
		return mediaByAnswerId;
	}
	
	private String getUnionId(Long answerId, Long mediaId)
	{
		if(null == answerId || answerId.longValue() <= 0 
				|| null == mediaId || mediaId.longValue() <= 0)
			return "";
		return answerId.toString() + "_" + mediaId.toString();
	}

	
	
}