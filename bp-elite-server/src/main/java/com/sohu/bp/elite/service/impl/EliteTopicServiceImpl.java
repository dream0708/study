package com.sohu.bp.elite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteTopicDao;

import com.sohu.bp.elite.persistence.EliteTopic;
import com.sohu.bp.elite.service.EliteTopicService;

public class EliteTopicServiceImpl implements EliteTopicService{
	
	private Logger log = LoggerFactory.getLogger(EliteTopicServiceImpl.class);
	private EliteTopicDao eliteTopicDao;
	
	
	public EliteTopicDao getEliteTopicDao() {
		return eliteTopicDao;
	}

	public void setEliteTopicDao(EliteTopicDao eliteTopicDao) {
		this.eliteTopicDao = eliteTopicDao;
	}

	@Override
	public Boolean setEliteTopicHistory(EliteTopic eliteTopic) {
		Boolean retVal = false;
		if(null == eliteTopic) return retVal;
		if(null != eliteTopic.getId() && eliteTopic.getId() > 0)
		{
			if(!eliteTopicDao.setMutexLock(eliteTopic.getId().toString())) return retVal;
			retVal = eliteTopicDao.update(eliteTopic);
			eliteTopicDao.removeMutexLock(eliteTopic.getId().toString());
			return retVal;
		}
		else {
			Long id = eliteTopicDao.insert(eliteTopic);
			if(null != id && id > 0) return true;
		}
		return false;
	}

	@Override
	public EliteTopic getEliteTopicById(Long id) {
		return eliteTopicDao.getTopicById(id);
	}

	@Override
	public Long getEliteTopicCount() {
		return eliteTopicDao.getTopicCount();
	}

	@Override
	public Long getEliteTopicCountByStatus(Integer status) {
		return eliteTopicDao.getTopicCountByStatus(status);
	}

	@Override
	public List<EliteTopic> getAllEliteTopic(Integer start, Integer count) {
		return eliteTopicDao.getTopicHistory(start, count);
	}

	@Override
	public List<EliteTopic> getAllEliteTopicByStatus(Integer status, Integer start, Integer count) {
		return eliteTopicDao.getTopicHistoryByStatus(status, start, count);
	}

}
