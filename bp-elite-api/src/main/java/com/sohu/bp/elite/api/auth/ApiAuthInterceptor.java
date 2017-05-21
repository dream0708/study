package com.sohu.bp.elite.api.auth;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 内网接口调用权限验证
 * <p>
 * create time: 2015年4月3日 下午4:34:26
 *
 * @auther dexingyang
 */
public class ApiAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiAuthInterceptor.class);
    private static final String HEADER_TIMESTAMP = "Timestamp";
    private static final String HEADER_AUTH = "Auth";

    private static final String STIME = "stime";   //接口调用开始时间

    /**
     * 接口权限认证
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String output = "{\"code\":-1,\"msg\":\"auth not pass\"}";

        long startTime = System.currentTimeMillis();
        request.setAttribute(STIME, startTime);

        //获取基本参数
        String uri = request.getRequestURI();
        String method = request.getMethod().toUpperCase();
        if (StringUtils.isBlank(uri) || StringUtils.isBlank(method)) {
            String clientIP = getClientIP(request);
            log.warn("clientIP=[" + clientIP + "] uri or method is null");
            PrintWriter out = response.getWriter();
            out.print(output);
            return false;
        }

        //从Header获取auth值
        String auth = request.getHeader(HEADER_AUTH);
        if (StringUtils.isEmpty(auth)) {
            String clientIP = getClientIP(request);
            log.warn("clientIP=[" + clientIP + "] auth is null:uri=" + uri + ",method=" + method);
            PrintWriter out = response.getWriter();
            out.print(output);
            return false;
        }

        //获取时间戳
        String timestamp = request.getHeader(HEADER_TIMESTAMP);
        if (StringUtils.isEmpty(timestamp)) {
            String clientIP = getClientIP(request);
            log.warn("clientIP=[" + clientIP + "] timestamp is null");
            PrintWriter out = response.getWriter();
            out.print(output);
            return false;
        }
        long t = 0l;
        try {
            t = Long.parseLong(timestamp);
        } catch (Exception e) {
            String clientIP = getClientIP(request);
            log.warn("clientIP=[" + clientIP + "] timestamp is not number");
            PrintWriter out = response.getWriter();
            out.print(output);
            return false;
        }

        boolean ret = AuthenticationCenter.isPassAuthentication(uri, method, t, auth);
        if (ret) {
            return true;
        } else {
            String clientIP = getClientIP(request);
            log.warn("clientIP=[" + clientIP + "] auth not pass,auth=[" + auth + "]");
            PrintWriter out = response.getWriter();
            out.print(output);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Long sTime = (Long) request.getAttribute(STIME);
        if (sTime != null) {
            log.info("call uri=" + request.getRequestURI() + " time=" + (System.currentTimeMillis() - sTime.longValue()) + "ms");
        }
    }

    public String getClientIP(HttpServletRequest servletRequest) {
        // return servletRequest.getRemoteAddr();
        String ip = servletRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = servletRequest.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = servletRequest.getRemoteAddr();
        }
        // 解析IP, 第一个为真ip
        String[] ips = null;
        if (ip != null) {
            ips = ip.split(",");
            ip = ips[0];
        }
        return ip;
    }

}
