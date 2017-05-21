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
import com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteMediaStatus;
import com.sohu.bp.elite.jdbc.Criteria;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteMediaByquestionid;
import com.sohu.bp.util.CompareTool;

/**
 * 
 * @author nicholastang
 * 2016-08-16 11:01:13
 * TODO media 问题id关联表的dao实现
 */
public class EliteMediaByQuestionIdDaoImpl extends JdbcDaoImpl implements EliteMediaByQuestionIdDao
{
	private Logger log = LoggerFactory.getLogger(EliteMediaByQuestionIdDaoImpl.class);
	private TablePartitioner partitioner;
	private CacheManager ssdbCacheManager;
	private CacheManager redisCacheManager;
    private SsdbCache mediaSsdb;
    private RedisCache mediaByQuestionCache;
    
    
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

	public String getTableName(Long questionId)
    {
		return DbPartitionHelper.getTableName(partitioner, questionId);
    }
    public void init()
    {
    	mediaSsdb = (SsdbCache)ssdbCacheManager.getCache(SsdbConstants.ELITE_MEDIA_BYQUESTION_ZSET);
    	mediaByQuestionCache =(RedisCache)redisCacheManager.getCache(CacheConstants.ELITE_MEDIA_BYQUESTION);
    }
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#insert(com.sohu.bp.elite.persistence.EliteMediaByquestionid)
	 */
	@Override
	public Long insert(EliteMediaByquestionid eliteMediaByquestionid) {
		long retVal = -1;
		if(null == eliteMediaByquestionid || eliteMediaByquestionid.getQuestionId() == null 
				|| eliteMediaByquestionid.getMediaId() == null)
		{
			return retVal;
		}
		try
		{
			String tableName = getTableName(eliteMediaByquestionid.getQuestionId());
			if(StringUtils.isBlank(tableName))
			{
				log.error("get tablename faild.");
				return retVal;
			}
			eliteMediaByquestionid.setUpdateTime(new Date());
			eliteMediaByquestionid.setStatus(EliteMediaStatus.WORK.getValue());
			Long id = super.insert(eliteMediaByquestionid, tableName);
			if(null != id && id.longValue() > 0)
			{
				eliteMediaByquestionid.setId(id);
				
				String unionId = this.getUnionId(eliteMediaByquestionid.getQuestionId(), eliteMediaByquestionid.getMediaId());
				if(StringUtils.isBlank(unionId))
				{
					log.error("get union id faild.");
					return retVal;
				}
				
				mediaByQuestionCache.put(unionId, eliteMediaByquestionid);
				mediaSsdb.zAdd(eliteMediaByquestionid.getQuestionId().toString(), eliteMediaByquestionid.getType().intValue(), eliteMediaByquestionid.getMediaId().toString());
				retVal = id;
			}
			else
			{
				log.error("insert elite media by question id faild.");
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = -1;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#update(com.sohu.bp.elite.persistence.EliteMediaByquestionid)
	 */
	@Override
	public boolean update(EliteMediaByquestionid eliteMediaByquestionid) {
		boolean retVal = false;
		if(null == eliteMediaByquestionid || eliteMediaByquestionid.getMediaId() == null || eliteMediaByquestionid.getQuestionId() == null)
		{
			return retVal;
		}
		
		try
		{
			String tableName = getTableName(eliteMediaByquestionid.getQuestionId());
			if(StringUtils.isBlank(tableName))
			{
				log.error("get table name failed");
				return retVal;
			}
			eliteMediaByquestionid.setUpdateTime(new Date());
			if(super.update(eliteMediaByquestionid, tableName) > 0)
			{
				String unionId = this.getUnionId(eliteMediaByquestionid.getQuestionId(), eliteMediaByquestionid.getMediaId());
				if(StringUtils.isBlank(unionId))
				{
					log.error("get union id faild.");
					return retVal;
				}
				if(!CompareTool.isEqual(eliteMediaByquestionid.getStatus(), EliteMediaStatus.WORK.getValue()))
				{
					mediaByQuestionCache.put(unionId, eliteMediaByquestionid);
					mediaSsdb.zRem(eliteMediaByquestionid.getQuestionId().toString(), eliteMediaByquestionid.getMediaId().toString());
				}
				else
				{
					mediaByQuestionCache.remove(unionId);
					mediaSsdb.zAdd(eliteMediaByquestionid.getQuestionId().toString(), eliteMediaByquestionid.getType().intValue(), eliteMediaByquestionid.getMediaId().toString());
				}
				
				retVal = true;
			}
			else
			{
				log.error("update elite media by questionid failed.mediaid={}", new String[]{eliteMediaByquestionid.getMediaId().toString()});
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = false;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#getById(java.lang.Long)
	 */
	@Deprecated
	public EliteMediaByquestionid getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#getListByQuestionId(java.lang.Long)
	 */
	@Override
	public List<Long> getMediaIdListByQuestionId(Long questionId) {
		return this.getMediaIdListByQuestionId(questionId, true);
	}
	
	public List<Long> getMediaIdListByQuestionId(Long questionId, boolean init) {
		if(null == questionId || questionId.longValue() <= 0)
			return null;
		if(!mediaSsdb.zExist(questionId.toString()))
		{
			if(init)
			{
				this.reloadQuestionMediaZset(questionId);
				return this.getMediaIdListByQuestionId(questionId, false);
			}
			else
			{
				return null;
			}
		}
		
		List<Long> mediaIdList = new ArrayList<Long>();
		try
		{
			List<String> mediaIdStrList = mediaSsdb.zRevRangeByScore(questionId.toString(), "", 0, Long.MAX_VALUE, Integer.MAX_VALUE);
			
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
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#getMediaIdListByQuestionIdAndType(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public List<Long> getMediaIdListByQuestionIdAndType(Long questionId, Integer type) {
		return this.getMediaIdListByQuestionIdAndType(questionId, type, true);
	}
	
	public List<Long> getMediaIdListByQuestionIdAndType(Long questionId, Integer type, boolean init) {
		if(null == questionId || questionId.longValue() <= 0 || null == type || type.intValue() <= 0)
			return null;
		if(!mediaSsdb.zExist(questionId.toString()))
		{
			if(init)
			{
				this.reloadQuestionMediaZset(questionId);
				return this.getMediaIdListByQuestionIdAndType(questionId, type, false);
			}
			else
			{
				return null;
			}
		}
		
		List<Long> mediaIdList = new ArrayList<Long>();
		try
		{
			List<String> mediaIdStrList = mediaSsdb.zRevRangeByScore(questionId.toString(), "", type, type, Integer.MAX_VALUE);
			
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
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#delete(java.lang.Long)
	 */
	@Deprecated
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void reloadQuestionMediaZset(Long questionId)
	{
		if(null == questionId || questionId.longValue() <= 0)
			return;
		
		mediaSsdb.zClear(questionId.toString());
		try
		{
			String tableName = getTableName(questionId);
			if(StringUtils.isBlank(tableName))
				return;
			Criteria criteria = Criteria.create(EliteMediaByquestionid.class).where("questionId", new Object[]{questionId}).and("status", new Object[]{EliteMediaStatus.WORK.getValue()});
			List<EliteMediaByquestionid> mediaByquestionidList = super.queryList(criteria, tableName);
			if(null != mediaByquestionidList && mediaByquestionidList.size() > 0)
			{
				mediaByquestionidList.forEach(mediaByQuestionid -> mediaSsdb.zAdd(mediaByQuestionid.getQuestionId().toString(), mediaByQuestionid.getType().intValue(), mediaByQuestionid.getMediaId().toString()));
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao#getByUnionId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public EliteMediaByquestionid getByUnionId(Long questionId, Long mediaId) {
		if(null == questionId || questionId.longValue() <= 0 
				|| null == mediaId || mediaId.longValue() <= 0)
			return null;
		
		String unionId = this.getUnionId(questionId, mediaId);
		if(StringUtils.isBlank(unionId))
			return null;
		EliteMediaByquestionid mediaByQuestionId = (EliteMediaByquestionid)mediaByQuestionCache.get(unionId);
		if(null == mediaByQuestionId)
		{
			String tableName = getTableName(questionId);
			if(StringUtils.isBlank(tableName))
				return null;
			Criteria criteria = Criteria.create(EliteMediaByquestionid.class)
					.where("questionId", new Object[]{questionId}).and("mediaId", new Object[]{mediaId});
			mediaByQuestionId = (EliteMediaByquestionid)super.querySingleResult(criteria, tableName);
			if(null != mediaByQuestionId)
			{
				mediaByQuestionCache.put(unionId, mediaByQuestionId);
			}
				
		}
		
		return mediaByQuestionId;
	}
	
	
	private String getUnionId(Long questionId, Long mediaId)
	{
		if(null == questionId || questionId.longValue() <= 0 
				|| null == mediaId || mediaId.longValue() <= 0)
			return "";
		return questionId.toString() + "_" + mediaId.toString();
	}

	
	
}