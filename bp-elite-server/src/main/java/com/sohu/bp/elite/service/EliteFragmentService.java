package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteFragment;

public interface EliteFragmentService {
	public	Boolean setEliteFragmentHistory(EliteFragment eliteFragment);
	public Integer getFragmentCount();
	public Integer getFragmentCountByType(Integer type);
	public List<EliteFragment> getFragmentByType(Integer type);
	public List<EliteFragment> getAllFragment();
	public EliteFragment getFragmentById(Long id);
}
