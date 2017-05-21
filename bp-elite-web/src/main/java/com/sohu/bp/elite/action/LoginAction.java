package com.sohu.bp.elite.action;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sohu.bp.elite.OverallData;
import com.sohu.bp.elite.action.bean.JumpPageBean;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.util.LoginUtil;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.utils.RealIpUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.SpringUtil;

import io.netty.handler.codec.http.HttpResponse;

/**
 * 
 * @author nicholastang
 * 2016-08-23 18:48:03
 * TODO 登录类
 */
@Controller
//@RequestMapping("ask")
public class LoginAction
{
	private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);
	private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	public static volatile String LoginUrl = "http://test.home.focus.cn/decoration/login.html";
	public static volatile String PCLoginUrl = "http://test.home.focus.cn/login";
	public static volatile String Logout = "http://test.home.focus.cn/logout";

	private static final int PERSISTENT_COOKIE_TIME = 86400 * 365 * 3;
	
	private String domain = "http://test.home.focus.cn";
	
	private Configuration configuration;
	
	@PostConstruct
	public void init()
	{
		configuration = (Configuration)SpringUtil.getBean("configuration");
		domain = configuration.get("frontDomain", domain);
		LoginUrl = configuration.get("loginUrl", LoginUrl);
		PCLoginUrl = configuration.get("PCLoginUrl", PCLoginUrl);
		Logout = configuration.get("logout", Logout);
	}
	
	@RequestMapping(value = {"ask/login", "login"})
	public String login(String jumpUrl, HttpSession session, HttpServletRequest request, RedirectAttributes model)
	{	
		Map<String, String[]> params = request.getParameterMap();
		model.addAllAttributes(params);
		if(StringUtils.isBlank(jumpUrl))
			jumpUrl = "/";
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null != bpId && bpId.longValue() > 0)
			return "redirect:"+jumpUrl;
		
		return "redirect:" + (AgentUtil.getSource(request).getValue() == AgentSource.PC.getValue() ? PCLoginUrl : LoginUrl) +"?ru="+ domain + jumpUrl;
	}

	@RequestMapping(value = {"tlogin"})
	public String loginByToken(String token, String ru, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String ip = RealIpUtil.getRealIP(request);
		int port = request.getRemotePort();
		if (StringUtils.isBlank(ru)) {
			ru = "/";
		}
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if (null != bpId && bpId.longValue() > 0) {
			return "redirect:" + ru;
		}
		try {
			CodeMsgData codeMsgData = bpServiceAdapter.loginByToken(token, ip, port);
			if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
				JSONObject data = JSONObject.fromObject(codeMsgData.getData());
				if (null != data && data.containsKey("bpInfo") && data.containsKey("bpSign") && data.containsKey("bpid")) {
					String bpInfo = data.getString(LoginUtil.BPINFO);
					String bpSign = data.getString(LoginUtil.BPSIGN);
					LoginUtil.setLoginCookie(bpInfo, bpSign, PERSISTENT_COOKIE_TIME, response);//设置cookie
					return "redirect:" + ru;
				}
			} else {
				logger.error("login by token failed. codeMsgData="+codeMsgData);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return "redirect:" + LoginUrl + "?ru=https://" + OverallDataFilter.askDomain + ru;
	}

	public String codeLogin(String jumpUrl, String code,  HttpSession session, HttpServletRequest request) {
		if (StringUtils.isBlank(jumpUrl)) {
			jumpUrl = "/";
		}
		return null;
	}
	
	@RequestMapping(value = {"ask/logout", "logout"})
	public String logout(String jumpUrl, HttpSession session, HttpServletRequest request, RedirectAttributes model){
		Map<String, String[]> params = request.getParameterMap();
		model.addAllAttributes(params);
		if(StringUtils.isBlank(jumpUrl)) jumpUrl = "/";
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0) return "redirect:" + jumpUrl;
		return "redirect:" + Logout + "?ru=" + domain + jumpUrl;
	}

	@RequestMapping(value = {"wait"})
	public String wait(@ModelAttribute("bean")JumpPageBean jumpPageBean, HttpServletRequest request) {
		if (jumpPageBean.getWaitSec() <= 0 || jumpPageBean.getWaitSec() > 30) {
			jumpPageBean.setWaitSec(5);
		}
		return "/common/jump-page";
	}
}