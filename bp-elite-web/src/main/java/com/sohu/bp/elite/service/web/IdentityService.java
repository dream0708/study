package com.sohu.bp.elite.service.web;

import java.util.List;

public interface IdentityService {
	public List<Long> getRecomExperts(int start, int count);
	public List<Long> getAllRecomExperts();
	public Long getRecomNum();
}
