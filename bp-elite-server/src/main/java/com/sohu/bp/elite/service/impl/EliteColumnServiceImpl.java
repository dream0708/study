package com.sohu.bp.elite.service.impl;

import java.util.Date;
import java.util.List;

import com.sohu.bp.elite.dao.EliteColumnDao;
import com.sohu.bp.elite.persistence.EliteColumn;
import com.sohu.bp.elite.service.EliteColumnService;

public class EliteColumnServiceImpl implements EliteColumnService {

	private EliteColumnDao eliteColumnDao;
	
	public void setEliteColumnDao(EliteColumnDao eliteColumnDao) {
		this.eliteColumnDao = eliteColumnDao;
	}
	
	@Override
	public long insert(EliteColumn eliteColumn) {
		if(null == eliteColumn.getCreateTime()) eliteColumn.setCreateTime(new Date());
		if(null == eliteColumn.getUpdateTime()) eliteColumn.setUpdateTime(eliteColumn.getCreateTime());
		return eliteColumnDao.insert(eliteColumn);
	}

	@Override
	public EliteColumn getEliteColumnById(Long id) {
		return eliteColumnDao.getEliteColumnById(id);
	}

	@Override
	public Long getEliteColumnCount() {
		return eliteColumnDao.getEliteColumnCount();
	}

	@Override
	public Long getEliteColumnCountByStatus(int status) {
		return eliteColumnDao.getEliteColumnCountByStatus(status);
	}

	@Override
	public List<EliteColumn> getAllEliteColumnByStatus(int start, int count, int status) {
		if(-1 == count){
			count = getEliteColumnCountByStatus(status).intValue();
		}
		return eliteColumnDao.getAllEliteColumnByStatus(start, count, status);
	}

	@Override
	public void update(EliteColumn eliteColumn) {
		if(null == eliteColumn.getUpdateTime()) eliteColumn.setUpdateTime(new Date());
		eliteColumnDao.update(eliteColumn);
	}

	@Override
	public List<EliteColumn> getAllEliteColumn(int start, int count) {
		if(-1 == count){
			count = getEliteColumnCount().intValue();
		}
		return eliteColumnDao.getAllEliteColumn(start, count);
	}

}
