package com.sohu.bp.elite.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.EliteSNSObjectType;
import com.sohu.bp.elite.enums.EliteSNSType;

/**
 * 
 * @author nicholastang
 * 2016年3月11日
 */
public class FollowUtils
{
	private static final Logger log = LoggerFactory.getLogger(FollowUtils.class);
	
	public static final String USERID = "USERID";
	public static final String OBJECTID = "OBJECTID";
	public static final String OBJECTTYPE = "OBJECTTYPE";
	
	/**
	 * 
	 * @param UserId
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public static String getMixId(Long userId, Long objectId, Integer objectType)
	{
		return userId.toString()+Constants.CACHE_DEFAULT_SPLIT_CHAR+objectId.toString()+Constants.CACHE_DEFAULT_SPLIT_CHAR+objectType.toString();
	}
	/**
	 * 获取关注对象复合id
	 * @param objectType
	 * @param objectId
	 * @return
	 */
	public static String getMixId(Long objectId, Integer objectType)
	{
		return objectId.toString()+Constants.CACHE_DEFAULT_SPLIT_CHAR+objectType.toString();
	}
	
	/**
	 * 获取关注对象原始id
	 * @param mixId
	 * @return
	 */
	public static Map<String, Object> parseMixId(String mixId)
	{
		if(StringUtils.isBlank(mixId))
			return null;
		String[] dataArray = mixId.split(Constants.CACHE_DEFAULT_SPLIT_CHAR+"");
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try
		{
			if(dataArray.length == 2)
			{
				dataMap.put(OBJECTID, Long.parseLong(dataArray[0]));
				dataMap.put(OBJECTTYPE, Integer.parseInt(dataArray[1]));
			}
			else if(dataArray.length == 3)
			{
				dataMap.put(USERID, Long.parseLong(dataArray[0]));
				dataMap.put(OBJECTID, Long.parseLong(dataArray[1]));
				dataMap.put(OBJECTTYPE, Integer.parseInt(dataArray[2]));
			}
			else
			{
				return null;
			}
		}catch(Exception e)
		{
			log.error("", e);
			return null;
		}
		return dataMap;
	}
}