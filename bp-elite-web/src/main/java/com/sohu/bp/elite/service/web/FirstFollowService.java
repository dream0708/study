package com.sohu.bp.elite.service.web;

import javax.servlet.http.HttpServletRequest;

public interface FirstFollowService {
	
	public Boolean isFirstLogin(Long bpId);
	
	public void userFollow(Long bpId, Long actIp, Integer actPort);
	
	public void tagFollow(Long bpId, Long actIp, Integer actPort);
	
	public void firstLoginFollow(final Long bpId, final HttpServletRequest request);
	
	public void rebuildUserIndex();
	
	public void rebuildUserIndexTest();
	
}
