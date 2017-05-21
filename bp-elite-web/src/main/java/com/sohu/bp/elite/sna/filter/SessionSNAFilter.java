package com.sohu.bp.elite.sna.filter;

import com.sohu.bp.App;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.filter.WXResponseWrapper;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.FirstFollowService;
import com.sohu.bp.elite.sna.SessionMap;
import com.sohu.bp.elite.sna.web.SNAHttpRequest;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteLoginUserAsyncTask;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ImageUtil;
import com.sohu.bp.elite.util.LoginUtil;
import com.sohu.bp.elite.util.SpringUtil;

import eu.bitwalker.useragentutils.UserAgent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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



/**
 * 
 * @author nicholastang
 * 2016年3月21日
 */
public class SessionSNAFilter implements Filter {
	
	private static final Logger log = LoggerFactory.getLogger(SessionSNAFilter.class);
	private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	public static final String[] IGNORE_SUFFIX = new String[]{".png", ".jpg", ".jpeg", ".gif", ".ico", ".css", ".js", ".swf", ".bmp", ".inc",};
	
	/*如果session没有变化，则5分钟更新一次cache*/
    private int expirationUpdateInterval = 5 * 60;
    
	private static final String EXCLUDES = "excludeRegex";
	private static final String CHECK_LOGIN = "checkLogin";  //只检查登录的链接
	private static final String SOFTLOGIN = "softLoginRegex";
	private static final String WXLOGIN = "wxLogin";
	private static final String APPLOGIN = "appLogin";
		
	private static String excludeRegex = "";
	private static Pattern pattern = null;
	
	private static String checkLogin = "";
	private static Pattern patternLogin = null;
	
	private static String softLoginRegex = "";
	private static Pattern patternSoftLogin = null;
	
	private static String wxLoginRegex = "";
	private static Pattern patternWxLogin = null;
	
	private static String appLoginRegex = "";
	private static Pattern patternAppLogin = null;
	
	private static RedisCache sessionCache;
	//private static String loginUrl = "/ask/login";
	
	private UserInfoService userInfoService;
	//private BpEliteServiceAdapter bpEliteServiceAdapter = BpEliteServiceAdapterFactory.getEliteServiceAdapter();
	private FirstFollowService firstFollowService;
	
	private Configuration configuration;
	public static String LoginUrl = "http://test.home.focus.cn/decoration/login.html";
	private String domain = "http://test.home.focus.cn";
	
	//add mutual locks
	private ConcurrentMap<Integer, Integer> locks = null;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		userInfoService = (UserInfoService)SpringUtil.getBean("userInfoService");
		firstFollowService = (FirstFollowService)SpringUtil.getBean("firstFollowService");
		
		excludeRegex = config.getInitParameter(EXCLUDES);
		checkLogin = config.getInitParameter(CHECK_LOGIN);
		softLoginRegex = config.getInitParameter(SOFTLOGIN);
		wxLoginRegex = config.getInitParameter(WXLOGIN);
		appLoginRegex = config.getInitParameter(APPLOGIN);
		
		
		excludeRegex = excludeRegex.replace(com.sohu.bp.elite.constants.Constants.SPACE, "").replace(com.sohu.bp.elite.constants.Constants.ENTER, "");
		checkLogin = checkLogin.replace(com.sohu.bp.elite.constants.Constants.SPACE, "").replace(com.sohu.bp.elite.constants.Constants.ENTER, "");
		softLoginRegex = softLoginRegex.replace(com.sohu.bp.elite.constants.Constants.SPACE, "").replace(com.sohu.bp.elite.constants.Constants.ENTER, "");
		wxLoginRegex = wxLoginRegex.replace(com.sohu.bp.elite.constants.Constants.SPACE, "").replace(com.sohu.bp.elite.constants.Constants.ENTER, "");
		appLoginRegex = appLoginRegex.replace(com.sohu.bp.elite.constants.Constants.SPACE, "").replace(com.sohu.bp.elite.constants.Constants.ENTER, "");
		
		sessionCache = (RedisCache)((CacheManager)SpringUtil.getBean("redisCacheManager")).getCache(CacheConstants.CACHE_BP_ELITE_SESSION);
		if(excludeRegex != null && !"".equals(excludeRegex)){
			excludeRegex = excludeRegex.toLowerCase();
			pattern = Pattern.compile(excludeRegex);
		}
		
		if(checkLogin != null && !"".equals(checkLogin)){
			patternLogin = Pattern.compile(checkLogin);
		}
		
		if(StringUtils.isNotBlank(softLoginRegex)){
			patternSoftLogin = Pattern.compile(softLoginRegex);
		}
		
		if(StringUtils.isNotBlank(wxLoginRegex)){
			patternWxLogin = Pattern.compile(wxLoginRegex);
		}
		if(StringUtils.isNotBlank(appLoginRegex)){
		    patternAppLogin = Pattern.compile(appLoginRegex);
		}
		
		configuration = (Configuration)SpringUtil.getBean("configuration");
		domain = configuration.get("frontDomain", domain);
		LoginUrl = configuration.get("loginUrl", LoginUrl);
		
		locks = new ConcurrentHashMap<Integer, Integer>();
	}
	
	@Override
	public void destroy() {
	}
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {		

		final HttpServletRequest hrequest = (HttpServletRequest) request;
		final HttpServletResponse hresponse = (HttpServletResponse) response;
		
		//检测是否是exclude的地址
		int ret = checkUri(hrequest);
		if(ret == 1){
			chain.doFilter(hrequest, hresponse);
			return;
		}
		
		//从request中取userId
		long cookieUserId = -1;
		AgentSource userAgent = AgentUtil.getSource(hrequest);
		if(ret == 4){
			cookieUserId = LoginUtil.getWXLoginBpid(hrequest);
		} else if (ret == 5) {
		    cookieUserId = LoginUtil.getAppLoginBpid(hrequest, hresponse);
		} else {
			cookieUserId = LoginUtil.getLoginBpid(hrequest, hresponse);
		}
		
		//for test
//		cookieUserId = 142668;
		
		if(cookieUserId <= 0){
			String uri = hrequest.getRequestURI().toLowerCase();
			if(ret == 3)
			{   
				chain.doFilter(hrequest, hresponse);
				return;
			} else if (ret == 4 || ret == 5){
				//利用登录的规则重新来进行判断，如果不为excludeRegex和softRegex则返回错误
			    switch (ret) {
			    case 4 :
			        uri = hrequest.getRequestURI().substring("/wx".length());
			        break;
			    case 5 :
			        uri = hrequest.getRequestURI().substring("/app".length());
			        break;
			    default :
			        break;
			    }
				if(uri.equalsIgnoreCase("")) uri = "/";
				ret = checkUri(uri);
				if(ret == 1 || ret == 3) {
					chain.doFilter(hrequest, hresponse);
				} else {
				log.info("get wx or app bpId info failed");
				String output = "{\"code\":-1,\"msg\":\"get user info failed\"}";
				hresponse.setContentType("applicaton/json");
				hresponse.setCharacterEncoding("utf-8");
				PrintWriter out = hresponse.getWriter();
				out.print(output);
				return;
				}
			}
			else
			{
				hresponse.sendRedirect(LoginUrl+"?ru="+ domain + uri);//跳转到登陆首页
			}
			return;
		}

		EliteAsyncTaskPool.addTask(new EliteLoginUserAsyncTask(cookieUserId));
		//判断是否是只判断checkLogin的地址
		if(ret == 2){
			hrequest.setAttribute(SessionConstants.USER_ID, cookieUserId);
//			chain.doFilter(hrequest, hresponse);
//			return;
		}
		
		Long userId = new Long(cookieUserId);
		
		SessionMap session = null;
		String sessionKey = userId.toString();
		session = (SessionMap)sessionCache.get(sessionKey);
		if(null == session)
		{
			session = new SessionMap();
			session.setSessionId(sessionKey);
			session.put(SessionConstants.USER_ID, userId);
			UserInfo userInfo = userInfoService.getUserInfoByBpid(userId);

			if(null != userInfo)
			{
				session.put(SessionConstants.USER_NAME, userInfo.getNick());
		    	session.put(SessionConstants.USER_AVATAR, userInfo.getAvatar());
			}
			//初次建立session时进行更新用户索引的行为
			Integer lock = (Integer) getCacheSyncObject(userId.intValue());
			if (null == lock){
				firstFollowService.firstLoginFollow(cookieUserId, hrequest);
			}
			if (ret == 2 && userAgent == AgentSource.MOBILE) {
				try {
					TEliteUser user = eliteAdapter.getUserByBpId(userId);
				} catch (Exception e) {
					log.error("", e);
					hresponse.sendRedirect("/wait"+"?ru=" + hrequest.getRequestURI() + "&waitSec=5");//跳转到登陆首页
				}
			}
		}
		
		
		int expireTime = hrequest.getSession().getMaxInactiveInterval();
		SNAHttpRequest snaRequest = new SNAHttpRequest(hrequest,userId.toString(),session);
		

		try {
			chain.doFilter(snaRequest, hresponse);
		} finally {
			
			int updateInterval = (int) ((System.currentTimeMillis() - session.getLastAccessedTime()) / 1000);
			
			if(session.isNew() && session.isEmpty()){
				return;
			}
			
			if(session.isClear() || session.isEmpty()){
				sessionCache.remove(userId.toString());
				return;
			}
			
			//非新创建session，数据未更改且未到更新间隔，则不更新cache
			if(!session.isNew() && !session.isModified() && updateInterval < expirationUpdateInterval){
				return;
			}
			
			//对于memacached，get操作不会更新过期时间的开始计算时间为访问时间，而update操作可以
			//而对于session，访问时需要更新时间。所以对于memached，即时session没有更新，也需要写缓存,加了一个5分钟的判断
			//redis:对于设置了expire的key，当重新set该key时，expire就变成了永久存储
			//      而且当进行get时，expire的时间也不会重新计算，仍然是从重设置expire的时候开始计算。
			//      所以session没有变化时，只进行expire操作即可，减少一些网络传输。这种方式就用不了5分钟的更新策略,因为accesstime没有更新
			{
				session.setLastAccessedTime(System.currentTimeMillis());
				String id = session.getSessionId();
				if(id != null && userId.toString().equals(id)){ //两者一致时才更新缓存，防止串号
					session.clearStatus();
					sessionCache.put(sessionKey,expireTime,session);
					removeCacheSyncObject(Integer.valueOf(id));
				}
			}
		}
	}
	
	/**
	 * 1-满足exclude正则, 2-满足checkLogin正则, 0-都不满足
	 * @param request
	 * @return
	 */
	private int checkUri(HttpServletRequest request){				
		//获取请求资源的后缀
		String uri = request.getRequestURI().toLowerCase();
		return checkUri(uri);
	}
	
	private int checkUri(String uri){
		//判断静态资源后缀
		for(String suffix:IGNORE_SUFFIX){
			if (uri.endsWith(suffix)){
            	return 1;
            }
		}
		
		//判断是否满足exclude
		if(StringUtils.isNotBlank(excludeRegex)){
			Matcher matcher = pattern.matcher(uri);
        	if(matcher.matches())
        		return 1;
        }
		
		//判断是否满足checkLogin
		if(StringUtils.isNotBlank(checkLogin)){
			Matcher matcher = patternLogin.matcher(uri);
			if(matcher.matches())
				return 2;
		}
		
		//判断是否满足软登陆
		if(StringUtils.isNotBlank(softLoginRegex)){
			Matcher matcher = patternSoftLogin.matcher(uri);
			if(matcher.matches())
				return 3;
		}
		
		//判断是否满足wx登录
		if(StringUtils.isNotBlank(wxLoginRegex)){
			Matcher matcher = patternWxLogin.matcher(uri);
			if(matcher.matches())
				return 4;
		}
		
		//判断是否满足app登录
		if (StringUtils.isNotBlank(appLoginRegex)) {
		    Matcher matcher = patternAppLogin.matcher(uri);
		    if (matcher.matches()) {
		        return 5;
		    }
		}
		return 0;
	}
	
	private Object getCacheSyncObject(final Integer id){
		return locks.putIfAbsent(id, id);
	}
	private void removeCacheSyncObject(final Integer id){
		locks.remove(id);
	}
}
