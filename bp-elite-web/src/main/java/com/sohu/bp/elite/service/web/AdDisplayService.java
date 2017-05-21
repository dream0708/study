package com.sohu.bp.elite.service.web;

import com.sohu.bp.elite.enums.EliteAdTypeBySite;

/**
 * 展示广告服务类
 * @author nicholastang
 * 2016-11-01 12:09:00
 * TODO
 */
public interface AdDisplayService
{
	public String getAd(EliteAdTypeBySite siteAdType);
	
	public void removeAd(EliteAdTypeBySite siteAdType);
}