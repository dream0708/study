package com.sohu.bp.elite.api.service;

import com.sohu.bp.elite.enums.EliteUserIdentity;

/**
 * 用于抓取用户入库的service类
 * @author zhijungou
 * 2016年12月30日
 */
public interface EliteUserService {
	public boolean isUserInDatabase(Long bpId);
	public void createUser(Long bpId);
	public boolean createUser(Long bpId, EliteUserIdentity identity);
	public void createUserIfNotExist(Long bpId);
	public boolean createUserIfNotExist(Long bpId, EliteUserIdentity identity);
}
