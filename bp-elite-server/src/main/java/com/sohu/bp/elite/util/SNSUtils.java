package com.sohu.bp.elite.util;

import com.sohu.bp.elite.enums.EliteSNSObjectType;
import com.sohu.bp.elite.enums.EliteSNSType;

/**
 * 
 * @author nicholastang
 * 2016年4月19日
 */
public class SNSUtils
{
	public static int getNewSNSType(EliteSNSType snsType, EliteSNSObjectType objectType)
	{
		if(null == snsType || null == objectType)
			return 0;
		return snsType.getValue() *100 + objectType.getValue();
	}
}