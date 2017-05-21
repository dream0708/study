package com.sohu.bp.elite.consumer.service;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.enums.BpUserType;

/**
 * create time: 2015年12月21日 下午2:46:14
 * @auther dexingyang
 */
public interface UserInfoService {
	public UserInfo getUserInfoByBpid(Long bpid);
	
	public long getBpidByMobile(String mobile);
}
