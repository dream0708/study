package com.sohu.bp.elite.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet.P;

import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.model.TEliteSourceType;

/**
 * 
 * @author nicholastang
 * 2016-07-28 11:32:10
 * 使用http head 中agent获得的信息工具类
 */
public class AgentUtil
{
	public static String[] MOBILE_DEVICE_ARRAY = new String[]{"android", "iphone", "windows phone", "mobile", "meego"};
	public static String IOS_APP_AGENT = "homeiosapp";
	public static String WECHAT_PREFIX = "^/wx";
	public static String APP_PREFIX = "^/app/";
	
	public static AgentSource getSource(HttpServletRequest request) {	
		Matcher matcher = Pattern.compile(WECHAT_PREFIX).matcher(request.getRequestURI().toLowerCase());
		if(matcher.find()){
			return AgentSource.WECHAT;
		}
		matcher = Pattern.compile(APP_PREFIX).matcher(request.getRequestURI().toLowerCase());
		if (matcher.find()) {
		    return AgentSource.APP;
		}
		String userAgent = request.getHeader("User-Agent");
		if(StringUtils.isBlank(userAgent))
			return AgentSource.UNKNOWN;
		userAgent = userAgent.toLowerCase();
//		if (userAgent.indexOf(IOS_APP_AGENT) >= 0) return AgentSource.APP;
		for(int i=0; i<MOBILE_DEVICE_ARRAY.length;i++)
		{
			if(userAgent.indexOf(MOBILE_DEVICE_ARRAY[i]) >= 0)
			{
				return AgentSource.MOBILE;
			}
		}
		
		return AgentSource.PC;
	}
	
	public static TEliteSourceType getFeedSourceType(HttpServletRequest request) {
	       Matcher matcher = Pattern.compile(WECHAT_PREFIX).matcher(request.getRequestURI().toLowerCase());
	        if(matcher.find()){
	            return TEliteSourceType.WRITE_WX;
	        }
	        matcher = Pattern.compile(APP_PREFIX).matcher(request.getRequestURI().toLowerCase());
	        if (matcher.find()) {
	            return TEliteSourceType.WRITE_APP;
	        }
	        String userAgent = request.getHeader("User-Agent");
	        if(StringUtils.isBlank(userAgent))
	            return TEliteSourceType.CRAWL;
	        userAgent = userAgent.toLowerCase();
//	      if (userAgent.indexOf(IOS_APP_AGENT) >= 0) return AgentSource.APP;
	        for(int i=0; i<MOBILE_DEVICE_ARRAY.length;i++) {
	            if(userAgent.indexOf(MOBILE_DEVICE_ARRAY[i]) >= 0) {
	                return TEliteSourceType.WRITE_MOBILE;
	            }
	        }
	        
	        return TEliteSourceType.WRITE_PC;
	}
}