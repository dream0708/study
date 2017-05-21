package com.sohu.bp.elite.api.service;

import java.util.List;

public interface IdentityService {
	public void insert(Long bpId);
	public void remove(Long bpId);
	public List<Long> getExpertsList(int start, int count);
	public long getNum();
}
