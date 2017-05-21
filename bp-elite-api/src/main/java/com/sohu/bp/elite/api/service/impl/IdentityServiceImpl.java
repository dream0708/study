package com.sohu.bp.elite.api.service.impl;

import java.util.List;

import com.sohu.bp.elite.api.dao.IdentityDao;
import com.sohu.bp.elite.api.service.IdentityService;

public class IdentityServiceImpl implements IdentityService {
	
	private IdentityDao identityDao;
	
	
	public IdentityDao getIdentityDao() {
		return identityDao;
	}

	public void setIdentityDao(IdentityDao identityDao) {
		this.identityDao = identityDao;
	}

	@Override
	public void insert(Long bpId) {
		identityDao.insert(bpId);
	}

	@Override
	public void remove(Long bpId) {
		identityDao.remove(bpId);
	}

	@Override
	public List<Long> getExpertsList(int start, int count) {
		return identityDao.getExpertsList(start, count);
	}

	@Override
	public long getNum() {
		return identityDao.getNum();
	}

}
