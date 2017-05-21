package com.sohu.bp.elite.service;

import java.util.List;

import com.sohu.bp.elite.enums.EliteFollowType;
import com.sohu.bp.elite.persistence.EliteFollow;

public interface EliteFollowService {
	
	public Long setEliteFollow(EliteFollow eliteFollow);
	public Boolean updateEliteFollow(EliteFollow eliteFollow);
	
	public Integer getEliteFollowCountByType(EliteFollowType eliteFollowType);
	
	public EliteFollow getEliteFollowById(Long id);
	public List<EliteFollow> getEliteFollowBtType(EliteFollowType eliteFollowType, Integer start, Integer count);

}
