package com.sohu.bp.elite.service.web.impl;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.adapter.BpMediaArticleServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.enums.EliteAdTypeBySite;
import com.sohu.bp.elite.service.web.AdDisplayService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-11-01 12:16:35
 * TODO
 */
public class AdDisplayServiceImpl implements AdDisplayService
{
	private static Logger logger = LoggerFactory.getLogger(AdDisplayServiceImpl.class);
	private BpMediaArticleServiceAdapter articleServiceAdapter = BpDecorationServiceAdapterFactory.getBpMediaArticleServiceAdapter();
	private CacheManager redisCacheManager;
	private RedisCache adRedisBySite;
	private long areaCode = 0;
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}
	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	public long getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(long areaCode) {
		this.areaCode = areaCode;
	}
	public void init(){
		adRedisBySite = (RedisCache)redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_AD_BYSITE);
		
		for(Entry<Integer, EliteAdTypeBySite> codeAdTypeEntry : EliteAdTypeBySite.siteCodeMap.entrySet())
		{
			this.removeAd(codeAdTypeEntry.getValue());
		}
		
	}

	@Override
	public String getAd(EliteAdTypeBySite siteAdType) {
		String ad = "";
		if(null == siteAdType)
			return ad;
		ad = adRedisBySite.getString(siteAdType.getCacheKey());
		if(StringUtils.isBlank(ad))
		{
			try
			{
				String response = articleServiceAdapter.getPageFragDataWithArea(siteAdType.getSiteCode(), areaCode);
				if(StringUtils.isNotBlank(response))
				{
					JSONArray resArray = JSONArray.fromObject(response);
					if(null != resArray && resArray.size() > 0)
					{
						JSONObject resJSON = resArray.getJSONObject(0);
						if(null != resJSON && resJSON.containsKey("code"))
						{
							ad = resJSON.getString("code");
						}
					}
					
				}
			}catch(Exception e)
			{
				logger.error("", e);
			}
			
			if(StringUtils.isNotBlank(ad))
				adRedisBySite.putString(siteAdType.getCacheKey(), ad);
			
			if(null == ad)
				ad = "";
		}
		
		return ad;
	}

	@Override
	public void removeAd(EliteAdTypeBySite siteAdType) {
		if(null == siteAdType)
			return;
		adRedisBySite.remove(siteAdType.getCacheKey());
	}
	
}