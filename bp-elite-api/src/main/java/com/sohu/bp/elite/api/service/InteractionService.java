package com.sohu.bp.elite.api.service;

import com.sohu.bp.model.BpInteractionTargetType;

/**
 * 
 * @author nicholastang
 * 2016-08-31 19:33:44
 * TODO
 */
public interface InteractionService
{
	/**
	 * 点赞
	 * @param bpId
	 * @param targetId
	 * @param targetType
	 * @return
	 */
	public boolean like(Long bpId, Long targetId, BpInteractionTargetType targetType);
}