package com.sohu.bp.elite.dao.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao;
import com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao;
import com.sohu.bp.elite.dao.EliteMediaDao;
import com.sohu.bp.elite.db.DbPartitionHelper;
import com.sohu.bp.elite.db.Sequence;
import com.sohu.bp.elite.db.TablePartitioner;
import com.sohu.bp.elite.enums.EliteMediaStatus;
import com.sohu.bp.elite.jdbc.JdbcDaoImpl;
import com.sohu.bp.elite.persistence.EliteMedia;
import com.sohu.bp.elite.persistence.EliteMediaByanswerid;
import com.sohu.bp.elite.persistence.EliteMediaByquestionid;

/**
 * 
 * @author nicholastang
 * 2016-08-15 17:56:40
 * TODO media dao implements
 */
public class EliteMediaDaoImpl extends JdbcDaoImpl implements EliteMediaDao
{
	private Logger log = LoggerFactory.getLogger(EliteMediaDaoImpl.class);
	private TablePartitioner partitioner;
    private Sequence sequence;
    private CacheManager redisCacheManager;
    private RedisCache mediaCache;
    
    private EliteMediaByQuestionIdDao mediaByQuestionIdDao;
    private EliteMediaByAnswerIdDao mediaByAnswerIdDao;
    
	public TablePartitioner getPartitioner() {
		return partitioner;
	}

	public void setPartitioner(TablePartitioner partitioner) {
		this.partitioner = partitioner;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	public EliteMediaByQuestionIdDao getMediaByQuestionIdDao() {
		return mediaByQuestionIdDao;
	}

	public void setMediaByQuestionIdDao(EliteMediaByQuestionIdDao mediaByQuestionIdDao) {
		this.mediaByQuestionIdDao = mediaByQuestionIdDao;
	}

	public EliteMediaByAnswerIdDao getMediaByAnswerIdDao() {
		return mediaByAnswerIdDao;
	}

	public void setMediaByAnswerIdDao(EliteMediaByAnswerIdDao mediaByAnswerIdDao) {
		this.mediaByAnswerIdDao = mediaByAnswerIdDao;
	}

	public String getTableName(Long id){
		return DbPartitionHelper.getTableName(partitioner, id);
	}
	public void init(){
		mediaCache = (RedisCache)redisCacheManager.getCache(CacheConstants.ELITE_MEDIA);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaDao#save(com.sohu.bp.elite.persistence.EliteMedia)
	 */
	@Override
	public Long save(EliteMedia eliteMedia) {
		long retVal = -1;
		if(null == eliteMedia || eliteMedia.getType() == null || (eliteMedia.getQuestionId() == null && eliteMedia.getAnswerId() == null))
			return retVal;
		Long id = sequence.nextSequence();
		if(null == id || id.longValue() <= 0)
		{
			log.error("get id of media failed.");
			return retVal;
		}
		String tableName = getTableName(id);
		if(StringUtils.isBlank(tableName))
		{
			log.error("");
			return retVal;
		}
		eliteMedia.setId(id);
		eliteMedia.setStatus(EliteMediaStatus.WORK.getValue());
		eliteMedia.setUploadTime(new Date());
		eliteMedia.setUpdateTime(new Date());
		try
		{
			if(super.save(eliteMedia, tableName) > 0)
			{

				//插入byquestionid表
				if(eliteMedia.getQuestionId() != null && eliteMedia.getQuestionId().longValue() > 0)
				{
					EliteMediaByquestionid mediaByQuestionId = new EliteMediaByquestionid();
					mediaByQuestionId.setMediaId(eliteMedia.getId());
					mediaByQuestionId.setQuestionId(eliteMedia.getQuestionId());
					mediaByQuestionId.setStatus(eliteMedia.getStatus());
					mediaByQuestionId.setType(eliteMedia.getType());
					if(mediaByQuestionIdDao.insert(mediaByQuestionId) <= 0)
					{
						log.info("insert media by question id record faild.mediaId={}, questionId={}", 
								new String[]{eliteMedia.getId().toString(), eliteMedia.getQuestionId().toString()});
						return retVal;
					}
				}
				
				//插入byanswerid表
				if(eliteMedia.getAnswerId() != null && eliteMedia.getAnswerId().longValue() > 0)
				{
					EliteMediaByanswerid mediaByAnswerId = new EliteMediaByanswerid();
					mediaByAnswerId.setMediaId(eliteMedia.getId());
					mediaByAnswerId.setAnswerId(eliteMedia.getAnswerId());
					mediaByAnswerId.setStatus(eliteMedia.getStatus());
					mediaByAnswerId.setType(eliteMedia.getType());
					if(mediaByAnswerIdDao.insert(mediaByAnswerId) <= 0)
					{
						log.info("insert media by answer id record faild.mediaId={}, answerId={}", 
								new String[]{eliteMedia.getId().toString(), eliteMedia.getAnswerId().toString()});
						return retVal;
					}
				}
				
				mediaCache.put(id.toString(), eliteMedia);
				retVal = id;
			}
			else
			{
				log.error("save elite media failed. id={}", new String[]{id.toString()});
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = -1;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaDao#update(com.sohu.bp.elite.persistence.EliteMedia)
	 */
	@Override
	public boolean update(EliteMedia eliteMedia) {
		boolean retVal = false;
		if(null == eliteMedia || eliteMedia.getId() == null || eliteMedia.getId().longValue() <= 0)
		{
			return retVal;
		}

		String tableName = getTableName(eliteMedia.getId());
		if(StringUtils.isBlank(tableName))
		{
			log.error("get table name failed.");
			return retVal;
		}
		
		eliteMedia.setUpdateTime(new Date());
		try
		{
			if(super.update(eliteMedia, tableName) > 0)
			{
				if(eliteMedia.getQuestionId() != null && eliteMedia.getQuestionId().longValue() > 0)
				{
					EliteMediaByquestionid mediaByQuestionId = mediaByQuestionIdDao.getByUnionId(eliteMedia.getQuestionId(), eliteMedia.getId());
					if(null != mediaByQuestionId)
					{
						mediaByQuestionId.setStatus(eliteMedia.getStatus());
						if(!mediaByQuestionIdDao.update(mediaByQuestionId))
						{
							log.error("update media by question id faild.questionId={}, mediaId={}", 
									new String[]{eliteMedia.getQuestionId().toString(), eliteMedia.getId().toString()});
							return false;
						}
					}
					else
					{
						mediaByQuestionId = new EliteMediaByquestionid();
						mediaByQuestionId.setQuestionId(eliteMedia.getQuestionId());
						mediaByQuestionId.setMediaId(eliteMedia.getId());
						mediaByQuestionId.setStatus(eliteMedia.getStatus());
						if(mediaByQuestionIdDao.insert(mediaByQuestionId) <= 0)
						{
							log.error("insert media by question id faild.questionId={}, mediaId={}", 
									new String[]{eliteMedia.getQuestionId().toString(), eliteMedia.getId().toString()});
							return false;
						}
					}
				}
				
				if(eliteMedia.getAnswerId() != null && eliteMedia.getAnswerId().longValue() > 0)
				{
					EliteMediaByanswerid mediaByAnswerId = mediaByAnswerIdDao.getByUnionId(eliteMedia.getAnswerId(), eliteMedia.getId());
					if(null != mediaByAnswerId)
					{
						mediaByAnswerId.setStatus(eliteMedia.getStatus());
						if(!mediaByAnswerIdDao.update(mediaByAnswerId))
						{
							log.error("update media by answer id faild.answerId={}, mediaId={}", 
									new String[]{eliteMedia.getAnswerId().toString(), eliteMedia.getId().toString()});
							return false;
						}
					}
					else
					{
						mediaByAnswerId = new EliteMediaByanswerid();
						mediaByAnswerId.setAnswerId(eliteMedia.getAnswerId());
						mediaByAnswerId.setMediaId(eliteMedia.getId());
						mediaByAnswerId.setStatus(eliteMedia.getStatus());
						if(mediaByAnswerIdDao.insert(mediaByAnswerId) <= 0)
						{
							log.error("insert media by answer id faild.answerId={}, mediaId={}", 
									new String[]{eliteMedia.getAnswerId().toString(), eliteMedia.getId().toString()});
							return false;
						}
					}
				}
				
				mediaCache.put(eliteMedia.getId().toString(), eliteMedia);
				retVal = true;
			}
			else
			{
				log.error("update elite media failed.id={}", new String[]{eliteMedia.getId().toString()});
			}
		}catch(Exception e)
		{
			log.error("", e);
			retVal = false;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaDao#getById(java.lang.Long)
	 */
	@Override
	public EliteMedia getById(Long id) {
		if(null == id || id.longValue() <= 0)
			return null;
		EliteMedia eliteMedia = (EliteMedia)mediaCache.get(id.toString());
		if(null == eliteMedia)
		{
			try
			{
				String tableName = getTableName(id);
				eliteMedia = super.get(EliteMedia.class, id, tableName);
				if(null != eliteMedia)
				{
					mediaCache.put(eliteMedia.getId().toString(), eliteMedia);
				}
			}catch(Exception e)
			{
				log.error("", e);
			}
		}
		return eliteMedia;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.dao.EliteMediaDao#delete(java.lang.Long)
	 */
	@Deprecated
	public boolean delete(Long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
}