package com.sohu.bp.elite.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.dao.EliteFollowDao;
import com.sohu.bp.elite.enums.EliteFollowType;
import com.sohu.bp.elite.persistence.EliteFollow;
import com.sohu.bp.elite.service.EliteFollowService;

public class EliteFollowServiceImpl implements EliteFollowService {

	private static final Logger log = LoggerFactory.getLogger(EliteFollowServiceImpl.class);
	private EliteFollowDao eliteFollowDao;
	
	public void setEliteFollowDao(EliteFollowDao eliteFollowDao) {
		this.eliteFollowDao = eliteFollowDao;
	}
	
	@Override
	public Long setEliteFollow(EliteFollow eliteFollow) {
		return eliteFollowDao.setEliteFollow(eliteFollow);
	}

	@Override
	public Boolean updateEliteFollow(EliteFollow eliteFollow) {
		return eliteFollowDao.updateEliteFollow(eliteFollow);
	}

	@Override
	public Integer getEliteFollowCountByType(EliteFollowType eliteFollowType) {
		return eliteFollowDao.getEliteFollowCountByType(eliteFollowType);
	}

	@Override
	public List<EliteFollow> getEliteFollowBtType(EliteFollowType eliteFollowType, Integer start, Integer count) {
		return eliteFollowDao.getEliteFollowByType(eliteFollowType, start, count);
	}

	@Override
	public EliteFollow getEliteFollowById(Long id) {
		return eliteFollowDao.getEliteFollowById(id);
	}

}
