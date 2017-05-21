package com.sohu.bp.elite.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteMediaByAnswerIdDao;
import com.sohu.bp.elite.dao.EliteMediaByQuestionIdDao;
import com.sohu.bp.elite.dao.EliteMediaDao;
import com.sohu.bp.elite.dao.impl.EliteMediaDaoImpl;
import com.sohu.bp.elite.enums.EliteMediaStatus;
import com.sohu.bp.elite.persistence.EliteMedia;
import com.sohu.bp.elite.service.EliteMediaService;

/**
 * 
 * @author nicholastang
 * 2016-08-17 14:44:46
 * TODO elite media service 具体逻辑实现
 */
public class EliteMediaServiceImpl implements EliteMediaService
{
	private Logger log = LoggerFactory.getLogger(EliteMediaServiceImpl.class);
	
	private EliteMediaDao mediaDao;
	private EliteMediaByQuestionIdDao mediaByQuestionIdDao;
	private EliteMediaByAnswerIdDao mediaByAnswerIdDao;
	
	public EliteMediaDao getMediaDao() {
		return mediaDao;
	}

	public void setMediaDao(EliteMediaDao mediaDao) {
		this.mediaDao = mediaDao;
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

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#create(com.sohu.bp.elite.persistence.EliteMedia)
	 */
	@Override
	public Long create(EliteMedia eliteMedia) {
		return mediaDao.save(eliteMedia);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#update(com.sohu.bp.elite.persistence.EliteMedia)
	 */
	@Override
	public boolean update(EliteMedia eliteMedia) {
		return mediaDao.update(eliteMedia);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#remove(com.sohu.bp.elite.persistence.EliteMedia)
	 */
	@Override
	public boolean remove(EliteMedia eliteMedia) {
		if(null == eliteMedia)
			return false;
		eliteMedia.setStatus(EliteMediaStatus.DELETE.getValue());
		return mediaDao.update(eliteMedia);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#getById(java.lang.Long)
	 */
	@Override
	public EliteMedia getById(Long id) {
		return mediaDao.getById(id);
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#getMediaListByQuestionId(java.lang.Long)
	 */
	@Override
	public List<EliteMedia> getMediaListByQuestionId(Long questionId) {
		List<EliteMedia> eliteMediaList = new ArrayList<EliteMedia>();
		List<Long> mediaIdList = mediaByQuestionIdDao.getMediaIdListByQuestionId(questionId);
		if(null != mediaIdList && mediaIdList.size() > 0)
		{
			mediaIdList.forEach(mediaId -> eliteMediaList.add(mediaDao.getById(mediaId)));
		}
		return eliteMediaList;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#getMediaListByAnswerId(java.lang.Long)
	 */
	@Override
	public List<EliteMedia> getMediaListByAnswerId(Long answerId) {
		List<EliteMedia> eliteMediaList = new ArrayList<EliteMedia>();
		List<Long> mediaIdList = mediaByAnswerIdDao.getMediaIdListByAnswerId(answerId);
		if(null != mediaIdList && mediaIdList.size() > 0)
		{
			mediaIdList.forEach(mediaId -> eliteMediaList.add(mediaDao.getById(mediaId)));
		}
		return eliteMediaList;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#getMediaListByQuestionIdAndType(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public List<EliteMedia> getMediaListByQuestionIdAndType(Long questionId, Integer type) {
		List<EliteMedia> eliteMediaList = new ArrayList<EliteMedia>();
		List<Long> mediaIdList = mediaByQuestionIdDao.getMediaIdListByQuestionIdAndType(questionId, type);
		if(null != mediaIdList && mediaIdList.size() > 0)
		{
			mediaIdList.forEach(mediaId -> eliteMediaList.add(mediaDao.getById(mediaId)));
		}
		return eliteMediaList;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteMediaService#getMediaListByAnswerIdAndType(java.lang.Long, java.lang.Integer)
	 */
	@Override
	public List<EliteMedia> getMediaListByAnswerIdAndType(Long answerId, Integer type) {
		List<EliteMedia> eliteMediaList = new ArrayList<EliteMedia>();
		List<Long> mediaIdList = mediaByAnswerIdDao.getMediaIdListByAnswerIdAndType(answerId, type);
		if(null != mediaIdList && mediaIdList.size() > 0)
		{
			mediaIdList.forEach(mediaId -> eliteMediaList.add(mediaDao.getById(mediaId)));
		}
		return eliteMediaList;
	}
	
}