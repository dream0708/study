package com.sohu.bp.elite.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.elite.auth.AuthenticationCenter;
import com.sohu.bp.elite.util.ImageUtil;


/**
 * 用于微信小程序
 * @author zhijungou
 * 2016年12月5日
 */
public class WXFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(WXFilter.class);
	private static final String HEADER_TIMESTAMP = "elitetimestamp";
	private static final String HEADER_AUTH = "auth";
	
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest hrequest = (HttpServletRequest) request;
		final HttpServletResponse hresponse = (HttpServletResponse) response;
		
		String output = "{\"code\":-1,\"msg\":\"auth not pass\"}";
		
		String uri = hrequest.getRequestURI();
		String method = hrequest.getMethod();
		
		//获取Auth值
		String auth = hrequest.getHeader(HEADER_AUTH);
		if(StringUtils.isEmpty(auth)){
			log.warn("auth is null : uri = " + uri + "method = " + method);
			PrintWriter out = hresponse.getWriter();
			out.println(output);
			return;
		}
		//获取时间戳
		String timestamp = hrequest.getHeader(HEADER_TIMESTAMP);
		if(StringUtils.isEmpty(timestamp)){
			log.warn("auth is null : uri = " + uri + "method = " + method);
			PrintWriter out = hresponse.getWriter();
			out.println(output);
			return;
		}		
	    
		long t = 0l;
	    try {
	       t = Long.parseLong(timestamp);
	    } catch (Exception e) {
	       log.info("timestamp is not number");
	    }
		
	    boolean ret = AuthenticationCenter.isPassAuthentication(uri, method, t, auth);
	    if(ret){
	    	WXResponseWrapper wxResponse = new WXResponseWrapper(hresponse);
			chain.doFilter(hrequest, wxResponse);
			String content = new String(wxResponse.getContent(), "utf-8");
			String finalContent = ImageUtil.changeWechatImgDomain(content);
			hresponse.reset();
			hresponse.setContentType("application/json");
			hresponse.setCharacterEncoding("utf-8");
			PrintWriter out = hresponse.getWriter();
			out.println(finalContent);
			out.flush();
			out.close();
	    } else {
	    	log.warn("auth is null : uri = " + uri + "method = " + method);
			PrintWriter out = hresponse.getWriter();
			out.println(output);
			return;
	    }
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
