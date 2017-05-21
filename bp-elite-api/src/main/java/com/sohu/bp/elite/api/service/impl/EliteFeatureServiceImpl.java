package com.sohu.bp.elite.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sohu.bp.elite.api.dao.EliteFeatureDao;
import com.sohu.bp.elite.api.service.EliteFeatureService;

public class EliteFeatureServiceImpl implements EliteFeatureService{

	private EliteFeatureDao eliteFeatureDao;
	
	
	public EliteFeatureDao getEliteFeatureDao() {
		return eliteFeatureDao;
	}


	public void setEliteFeatureDao(EliteFeatureDao eliteFeatureDao) {
		this.eliteFeatureDao = eliteFeatureDao;
	}


	@Override
	public Boolean updateCache(String key) {
		return eliteFeatureDao.updateCache(key);
	}


	@Override
	public String getAllUser() {
		return eliteFeatureDao.getAllUser();
	}


	@Override
	public List<Long> getInvitedList() {
		return eliteFeatureDao.getInvitedList();
	}


	@Override
	public Boolean removeInvited(Long id) {
		return eliteFeatureDao.removeInvited(id);
	}


	@Override
	public Boolean addInvited(Long id) {
		return eliteFeatureDao.addInvited(id);
	}


	@Override
	public List<Long> getEditList() {
		return eliteFeatureDao.getEditList();
	}


	@Override
	public Boolean addEditHistory(Long id) {
		return eliteFeatureDao.addEditHistory(id);
	}


    @Override
    public Boolean saveFocusOrder(Long objectId, Integer bpType, Integer order) {
        if (null == objectId || null == bpType || null == order || objectId <= 0) return false;
        return eliteFeatureDao.saveFocusOrder(objectId, bpType, order);
    }


    @Override
    public List<Integer> getOrdersByIds(List<Long> objectIds, Integer bpType) {
        List<Integer> orders = new ArrayList<Integer>();
        if (null == objectIds || objectIds.size() <= 0) return orders;
        return eliteFeatureDao.getOrdersByIds(objectIds, bpType);
    }
	
}
