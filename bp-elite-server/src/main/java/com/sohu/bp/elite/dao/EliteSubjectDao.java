package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteSubject;


/**
 * 
 * @author zhijungou
 * 2016年8月9日
 */
public interface EliteSubjectDao {

	public Long insert(EliteSubject eliteSubject);
	public Long update(EliteSubject eliteSubject);
	public Long getHistoryCountByStatus(Integer status);
	public List<EliteSubject> getHistoryByStatus(Integer status, Integer start, Integer count);
	public EliteSubject getHistoryById(Long id);
	public Long getAllHistoryCount();
	public List<EliteSubject> getAllHistory(Integer start, Integer count);
	public void removeCache();
	
	//设置互斥锁
	public boolean setMutexLock(String ids);
	public void removeMutexLock(String ids);
}

