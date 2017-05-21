package com.sohu.bp.elite;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.elite.util.SpringUtil;

/**
 * create time: 2016年1月8日 下午3:06:47
 * @auther dexingyang
 */
public class BeanManagerFacade {

	public static Configuration getConfiguration(){
		return (Configuration)SpringUtil.getBean("configuration");
	}
	
//	public static UserInfoService getUserInfoService(){
//		return (UserInfoService)SpringUtil.getBean("userInfoService");
//	}
	
	public static CacheManager getRedisCacheManager(){
		return (CacheManager)SpringUtil.getBean("redisCacheManager");
	}
	
}
