package com.sohu.bp.elite.dao;

import java.util.List;

import com.sohu.bp.elite.enums.EliteFragmentType;
import com.sohu.bp.elite.persistence.EliteFragment;

public interface EliteFragmentDao {
	public Long setEliteFragment(EliteFragment eliteFragment);
	public Boolean updateEliteFragment(EliteFragment eliteFragment);
	
	public Integer getFragmentCount();
	public Integer getFragmentCountByType(Integer type);
	
	public List<EliteFragment> getFragmentByType(Integer type);
	public List<EliteFragment> getAllFragment();
	public EliteFragment getFragmentById(Long id);
	public void removeCache();
	
	//设置互斥锁
	public Boolean setMutexLock(String id);
	public void removeMutexLock(String id);
}
