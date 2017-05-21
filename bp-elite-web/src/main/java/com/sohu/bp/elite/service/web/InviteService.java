package com.sohu.bp.elite.service.web;

import java.util.List;

public interface InviteService {
	public Integer saveInviteStatus(Long questionId, Long inviteId, Long invitedId);
	public List<Long> getRecomInviteList(Integer start, Integer count);
	public List<Long> getUserInviteList(Long questionId, Long inviteId);
	public Integer getRecomNum();
}
