package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.persistence.EliteColumn;

public interface EliteColumnService {
	public long insert(EliteColumn eliteColumn);
	public EliteColumn getEliteColumnById(Long id);
	public Long getEliteColumnCount();
	public Long getEliteColumnCountByStatus(int status);
	public List<EliteColumn> getAllEliteColumn(int start, int count);
	public List<EliteColumn> getAllEliteColumnByStatus(int start, int count, int status);
	public void update(EliteColumn eliteColumn);
}
