package com.sohu.bp.elite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteSubjectDao;
import com.sohu.bp.elite.persistence.EliteSubject;
import com.sohu.bp.elite.service.EliteSubjectService;



/**
 * 
 * @author zhijungou
 * 2016年8月9日
 */
public class EliteSubjectServiceImpl implements EliteSubjectService{
	private Logger log = LoggerFactory.getLogger(EliteSubjectServiceImpl.class);
	private EliteSubjectDao eliteSubjectDao;
	
	public EliteSubjectDao getEliteSubjectDao() {
		return eliteSubjectDao;
	}
	public void setEliteSubjectDao(EliteSubjectDao eliteSubjectDao) {
		this.eliteSubjectDao = eliteSubjectDao;
	}
	
	@Override
	public Long setEliteSubjectHistory(EliteSubject eliteSubject)
	{
		Long retVal = -1l;
		if(null == eliteSubject) return retVal;
		//先处理更新
		if(null != eliteSubject.getId() && eliteSubject.getId().longValue() > 0)
		{ 	if(!eliteSubjectDao.setMutexLock(eliteSubject.getId().toString())) return retVal;
			retVal = eliteSubjectDao.update(eliteSubject);
			eliteSubjectDao.removeMutexLock(eliteSubject.getId().toString());
			return retVal;
		}
		//处理新信息的插入
		else
		{
			Long id = eliteSubjectDao.insert(eliteSubject);
			if(null == id || id.longValue() <= 0) return retVal;
			return id;
		}
	}
	
	@Override
	public List<EliteSubject> getHistoryByStatus(Integer status, Integer start, Integer count)
	{
		return eliteSubjectDao.getHistoryByStatus(status, start, count);
	}
	
	@Override
	public List<EliteSubject> getAllHistory(Integer start, Integer count)
	{
		return eliteSubjectDao.getAllHistory(start, count);
	}
	
	@Override
	public Long getAllHistoryCount()
	{
		return eliteSubjectDao.getAllHistoryCount();
	}
	
	@Override
	public EliteSubject getHistoryById(Long id)
	{
		return eliteSubjectDao.getHistoryById(id);
	}
	@Override
	public Long getSubjectCountByStatus(Integer status) {
		return eliteSubjectDao.getHistoryCountByStatus(status);
	}
}
