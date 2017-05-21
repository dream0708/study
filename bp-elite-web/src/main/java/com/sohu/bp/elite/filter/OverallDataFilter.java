package com.sohu.bp.elite.filter;

import java.io.IOException;

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

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.OverallData;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.zookeeper.AppStaticVCService;
import com.sohu.bp.elite.zookeeper.StaticVCService;
import com.sohu.bp.elite.util.SpringUtil;


public class OverallDataFilter implements Filter
{
	private static final Logger log = LoggerFactory.getLogger(OverallDataFilter.class);
	private static StaticVCService staticVCService;
	private static AppStaticVCService appStaticVCService;
	public static String uxDomain = "s4.bp.sohu.com";
	public static String mainDomain = "//home.focus.cn";
	public static String askDomain = "bar.focus.cn";
	public static String innerDomain = "test.life.sohuno.com";
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		Configuration configuration = (Configuration)SpringUtil.getBean("configuration");
		uxDomain = configuration.get("uxDomain", uxDomain);
		mainDomain = configuration.get("mainDomain", mainDomain);
		askDomain = configuration.get("askDomain", askDomain);
		innerDomain = configuration.get("innerDomain", innerDomain);
		staticVCService = (StaticVCService)SpringUtil.getBean("staticVCService");
		appStaticVCService = (AppStaticVCService)SpringUtil.getBean("appStaticVCService");
		String staticVC = staticVCService.getNodeData();
		String appStaticVC = appStaticVCService.getNodeData();
		if(StringUtils.isNotBlank(staticVC)){
			OverallData.setStaticVerCode(staticVC);
			//log.info("[Overall_FILTER]static version code="+staticVC);
		}
	    if(StringUtils.isNotBlank(appStaticVC)){
            OverallData.setAppStaticVerCode(appStaticVC);
        }
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		final HttpServletRequest hrequest = (HttpServletRequest) request;
		final HttpServletResponse hresponse = (HttpServletResponse) response;
		//处理静态资源版本号
		String staticVC = OverallData.getStaticVerCode();
		String appStaticVC = OverallData.getAppStaticVerCode();
		if (StringUtils.isEmpty(staticVC)) {
			log.info("[Overall_FILTER] first empty");
			staticVC = staticVCService.getNodeData();
			if(StringUtils.isEmpty(staticVC))
			{
				log.info("[Overall_FILTER] second empty");
				hresponse.sendRedirect("http://home.focus.cn");
				return;
			}
			OverallData.setStaticVerCode(staticVC);
			//log.info("[Overall_FILTER]static version code="+staticVC);
		}
        if (StringUtils.isEmpty(appStaticVC)) {
            log.info("[Overall_FILTER] appStaticVC first empty");
            appStaticVC = appStaticVCService.getNodeData();
            if (StringUtils.isEmpty(appStaticVC)) {
                log.info("[Overall_FILTER] appStaticVC second empty");
                return;
            }
            OverallData.setAppStaticVerCode(appStaticVC);
        }
		hrequest.setAttribute(Constants.STATIC_VERSION_CODE_NAME, staticVC);
		hrequest.setAttribute(Constants.APP_STATIC_VERSION_CODE_NAME, appStaticVC);
		hrequest.setAttribute(Constants.MAIN_DOMAIN, mainDomain);
		hrequest.setAttribute(Constants.ASK_DOMAIN, askDomain);
		hrequest.setAttribute(Constants.UX_DOMAIN, uxDomain);
		hrequest.setAttribute(Constants.INNER_DOMAIN, innerDomain);
		chain.doFilter(hrequest, hresponse);
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}