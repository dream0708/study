package com.sohu.bp.elite.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author nicholastang
 * 2016-11-01 14:22:45
 * TODO 按页面位置划分的广告
 */
public enum EliteAdTypeBySite
{
	PC_BOTTOM_1(100, 16, "BOTTOM_1"),
	PC_RIGHTSIDE_1(101, 36, "RIGHTSIDE_1"),
	PC_HORIZONTAL_1(102, 35, "HORIZONTAL_1");
	
	int elitePageSite;
	int siteCode;
	String cacheKey;
	
	EliteAdTypeBySite(int elitePageSite, int siteCode, String cacheKey)
	{
		this.elitePageSite = elitePageSite;
		this.siteCode = siteCode;
		this.cacheKey = cacheKey;
	}

	public static final Map<Integer, EliteAdTypeBySite> posCodeMap = new HashMap<Integer, EliteAdTypeBySite>() {
		{
			put(100, PC_BOTTOM_1);
			put(101, PC_RIGHTSIDE_1);
			put(102, PC_HORIZONTAL_1);
		}
	};
	public static final Map<Integer, EliteAdTypeBySite> siteCodeMap = new HashMap<Integer, EliteAdTypeBySite>(){
		{
			put(16, PC_BOTTOM_1);
			put(15, PC_RIGHTSIDE_1);
			put(25, PC_HORIZONTAL_1);

		}
	};
	
	public static final Map<Integer, String> codeKeyMap = new HashMap<Integer, String>(){
		{
			put(16, "BOTTOM_1");
			put(15, "RIGHTSIDE_1");
			put(25, "HORIZONTAL_1");
		}
	};
	
	
	public int getElitePageSite() {
		return elitePageSite;
	}

	public void setElitePageSite(int elitePageSite) {
		this.elitePageSite = elitePageSite;
	}

	public int getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(int siteCode) {
		this.siteCode = siteCode;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}