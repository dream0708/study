package com.sohu.bp.elite.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;

/**
 * create time: 2015年11月24日 下午12:18:36
 * 
 * @auther dexingyang
 */
public class CookieUtil {
	private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);

	public static final String DEFAULT_DOMAIN = ".focus.cn";

	public static final String DEFAULT_PATH = "/";

	public CookieUtil() {
	}

	/**
	 * 设置cookie值 注意: 此方法对value没有进行URLEncode
	 * 
	 * @param response
	 *            response对象
	 * @param cookieName
	 *            cookie名称
	 * @param cookieValue
	 *            cookie值
	 * @param maxAge
	 *            cookie的存放时间，以秒为单位
	 */
	public static void setCookie(HttpServletResponse response,
			String cookieName, String cookieValue, int maxAge) {
		StringBuilder stringBuilder = new StringBuilder(128);
		stringBuilder.append(cookieName).append("=").append(cookieValue)
				.append("; ");
		stringBuilder.append("domain=" + DEFAULT_DOMAIN + "; path="
				+ DEFAULT_PATH + "; ");
		if (maxAge > 0) {
			long expireTime = System.currentTimeMillis() / 1000 + maxAge;
			String gmtDate = getGMTDate(new Date(expireTime * 1000));
			stringBuilder.append("expires=").append(gmtDate).append("; ");
		}
		String cookie = stringBuilder.toString();
		response.addHeader("Set-Cookie", cookie);
	}

	/**
	 * 设置HttpOnly的Cookie
	 * 
	 * @param response
	 *            response对象
	 * @param name
	 *            cookie名称
	 * @param value
	 *            cookie值
	 * @param maxAge
	 *            cookie的存放时间，以秒为单位
	 */
	public static void setCookieHttpOnly(HttpServletResponse response,
			String name, String value, int maxAge) {
		StringBuilder stringBuilder = new StringBuilder(128);
		stringBuilder.append(name).append("=").append(value).append("; ");
		stringBuilder.append("domain=" + DEFAULT_DOMAIN + "; path="
				+ DEFAULT_PATH + "; ");
		if (maxAge > 0) {
			long expireTime = System.currentTimeMillis() / 1000 + maxAge;
			String gmtDate = getGMTDate(new Date(expireTime * 1000));
			stringBuilder.append("expires=").append(gmtDate).append("; ");
		}
		stringBuilder.append("HttpOnly");
		String cookie = stringBuilder.toString();
		response.addHeader("Set-Cookie", cookie);
	}

	private static String getGMTDate(java.util.Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"E,d-MMM-yyyy HH:mm:ss 'GMT'", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(date);
	}

	private static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;
		if (cookies == null) {
			return returnCookie;
		} else {
			for (int i = 0; i < cookies.length; ++i) {
				Cookie thisCookie = cookies[i];
				if (thisCookie.getName().equals(name)
						&& !thisCookie.getValue().equals("")) {
					returnCookie = thisCookie;
					break;
				}
			}
			return returnCookie;
		}
	}

	/**
	 * 根据name获取对应的cookie值, 已经进行URLDecode
	 * 
	 * @param request
	 *            request对象
	 * @param name
	 *            cookie名称
	 * @return cookie的值
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie == null) {
			return null;
		}
		try {
			return URLDecoder.decode(cookie.getValue(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return cookie.getValue();
		}
	}

	/**
	 * 根据name删除对应的cookie值,
	 * 
	 * @param response
	 *            response对象
	 * @param cookieName
	 *            cookie的名称
	 */
	public static void deleteCookie(HttpServletResponse response,
			String cookieName) {
		if (!Strings.isNullOrEmpty(cookieName)) {
			Cookie cookie = new Cookie(cookieName, "");
			cookie.setMaxAge(0);
			cookie.setDomain(DEFAULT_DOMAIN);
			cookie.setValue("");
			cookie.setPath(DEFAULT_PATH);
			response.addCookie(cookie);
		}
	}

	/**
	 * 获取所有的cookie值 将所有的cookie组成一个JSON对象
	 * 
	 * @param request
	 *            request对象
	 * @return JSON对象
	 */
	public static JSONObject getAllRequestCookies(HttpServletRequest request) {
		JSONObject cookies = new JSONObject();
		Cookie[] requestCookies = request.getCookies();
		if (requestCookies == null) {
			return cookies;
		}
		for (Cookie cookie : requestCookies) {
			String cookieValue = "";
			try {
				cookieValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
				cookieValue = cookie.getValue();
			}
			cookies.put(cookie.getName(), cookieValue);
		}
		return cookies;
	}

}
