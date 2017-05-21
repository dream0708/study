package com.sohu.bp.elite.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.sohu.bp.util.HostTool;

public class RequestUtil {
	
	public static Map<String, String> getRequestInfo(HttpServletRequest servletRequest){
		Map<String,String> retMap = new HashMap<String, String>();
		retMap.put("ip", RequestUtil.getClientIP(servletRequest));
		retMap.put("agent", RequestUtil.getAgent(servletRequest));
		retMap.put("referer",RequestUtil.getReferer(servletRequest));
		retMap.put("port", RequestUtil.getClientPort(servletRequest).toString());
/*		Enumeration rnames=servletRequest.getHeaderNames();

		for (Enumeration e = rnames ; e.hasMoreElements() ;) {
		        String thisName=e.nextElement().toString();
		        String thisValue=servletRequest.getHeader(thisName);
		        retMap.put(thisName, thisValue);
		}*/
		return retMap;
	}
	
	public static String getRemoteIpAndPort(HttpServletRequest servletRequest){
		String ip = RequestUtil.getClientIP(servletRequest);
		long port = RequestUtil.getClientPort(servletRequest);
		
		if(StringUtils.isEmpty(ip))
			ip = "";
		return ip+":"+port;
	}
	
	public static String getClientIP(HttpServletRequest servletRequest) {
		// return servletRequest.getRemoteAddr();
		String ip = servletRequest.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = servletRequest.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = servletRequest.getRemoteAddr();
		}
		//有可能有多级代理，获取第一个
		String[] ips = null;
		if (ip != null) {
			ips = ip.split(",");
			ip = ips[0];
		}
		return ip;
	}

	public static Long getClientIPLong(final HttpServletRequest servletRequest) {
		if(servletRequest == null)
			return 0L;
		else
			return HostTool.ipToLong(RequestUtil.getClientIP(servletRequest));
	}
	
	/**
     * 获取客户端源端口
     * @param request
     * @return
     */
    public static Integer getClientPort(final HttpServletRequest request){
    	if(request == null)
    		return 0;
    	
        try{
        	int port = request.getRemotePort();
        	return port;
        }catch(Exception e){
            return 0;
        }
    }

	public static String getAgent(HttpServletRequest servletRequest) {
		String agentHeader = servletRequest.getHeader("user-agent");
		if(org.apache.commons.lang.StringUtils.isNotBlank(agentHeader)){
			return agentHeader.split(" ")[0];
		}
		return "UNKNOWN";
	}

	public static String getReferer(HttpServletRequest servletRequest) {
		String referer = servletRequest.getHeader("referer");
		if(org.apache.commons.lang.StringUtils.isBlank(referer)){
			return "";
		}
		return referer;
	}

/*	private final static String IE9 = "MSIE 9.0";
	private final static String IE8 = "MSIE 8.0";
	private final static String IE7 = "MSIE 7.0";
	private final static String IE6 = "MSIE 6.0";
	private final static String MAXTHON = "Maxthon";
	private final static String QQ = "QQBrowser";
	private final static String GREEN = "GreenBrowser";
	private final static String SE360 = "360SE";
	private final static String FIREFOX = "Firefox";
	private final static String OPERA = "Opera";
	private final static String CHROME = "Chrome";
	private final static String SAFARI = "Safari";
	private final static String OTHER = "����";

	private static String checkBrowse(String userAgent) {
		if (regex(OPERA, userAgent))
			return OPERA;
		if (regex(CHROME, userAgent))
			return CHROME;
		if (regex(FIREFOX, userAgent))
			return FIREFOX;
		if (regex(SAFARI, userAgent))
			return SAFARI;
		if (regex(SE360, userAgent))
			return SE360;
		if (regex(GREEN, userAgent))
			return GREEN;
		if (regex(QQ, userAgent))
			return QQ;
		if (regex(MAXTHON, userAgent))
			return MAXTHON;
		if (regex(IE9, userAgent))
			return IE9;
		if (regex(IE8, userAgent))
			return IE8;
		if (regex(IE7, userAgent))
			return IE7;
		if (regex(IE6, userAgent))
			return IE6;
		return OTHER;
	}

	private static boolean regex(String regex, String str) {
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(str);
		return m.find();
	}*/
}
