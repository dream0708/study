package com.sohu.bp.elite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteFragmentDao;
import com.sohu.bp.elite.persistence.EliteFragment;
import com.sohu.bp.elite.service.EliteFragmentService;

public class EliteFragmentServiceImpl implements EliteFragmentService{
	private static final Logger log = LoggerFactory.getLogger(EliteFragmentServiceImpl.class);
	private EliteFragmentDao eliteFragmentDao;
	
	public EliteFragmentDao getEliteFragmentDao() {
		return eliteFragmentDao;
	}

	public void setEliteFragmentDao(EliteFragmentDao eliteFragmentDao) {
		this.eliteFragmentDao = eliteFragmentDao;
	}

	@Override
	public Boolean setEliteFragmentHistory(EliteFragment eliteFragment) {
		Boolean retVal = false;
		if(null != eliteFragment)
		{
			if(null != eliteFragment.getId() && eliteFragment.getId() > 0)
			{	if(!eliteFragmentDao.setMutexLock(Long.toString(eliteFragment.getId()))) return retVal;
				retVal = eliteFragmentDao.updateEliteFragment(eliteFragment);
				eliteFragmentDao.removeMutexLock(Long.toString(eliteFragment.getId()));
			}
			else{
				Long id = eliteFragmentDao.setEliteFragment(eliteFragment);
				retVal = (id > 0);
			}
		}
		return retVal;
	}

	@Override
	public Integer getFragmentCount() {
		return eliteFragmentDao.getFragmentCount();
	}

	@Override
	public Integer getFragmentCountByType(Integer type) {
		return eliteFragmentDao.getFragmentCountByType(type);
	}

	@Override
	public List<EliteFragment> getFragmentByType(Integer type) {
		return eliteFragmentDao.getFragmentByType(type);
	}

	@Override
	public EliteFragment getFragmentById(Long id) {
		return eliteFragmentDao.getFragmentById(id);
	}

	@Override
	public List<EliteFragment> getAllFragment() {
		return eliteFragmentDao.getAllFragment();
	}

}
