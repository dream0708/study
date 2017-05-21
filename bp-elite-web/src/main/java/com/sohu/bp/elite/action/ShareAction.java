package com.sohu.bp.elite.action;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.util.ResponseJSON;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapter;
import com.sohu.bp.wechat.adapter.BpWechatServiceAdapterFactory;
import com.sohu.bp.wechat.model.BpWechatPlatform;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-09-09 19:46:02
 * TODO
 */
@Controller
@RequestMapping("ask/share")
public class ShareAction
{
	private static Logger logger = LoggerFactory.getLogger(ShareAction.class);
	private static final String token = "askbar2016";
	private static final String aesKey = "rVKsmq97h4ofIy5YWD1OAM6iTEPABL9m6mBeyFpUZw2";
	private BpWechatServiceAdapter wechatAdapter = BpWechatServiceAdapterFactory.getWechatServiceAdapter();
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	@RequestMapping("/wechat")
	@ResponseBody
	public String share(String url)
	{
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(StringUtils.isBlank(url))
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		BpWechatPlatform platform;
		try {
			platform = wechatAdapter.getWechatPlatform(5);
			String nonce = wechatAdapter.generateNonce();
			String timestamp = wechatAdapter.generateTimestamp();
			String signature = wechatAdapter.getJsApiSignature(platform, url, timestamp, nonce);
			String appId = platform.getAppId();
			JSONObject dataJSON = new JSONObject();
			dataJSON.put("appId", appId);
			dataJSON.put("url", url);
			dataJSON.put("timestamp", timestamp);
			dataJSON.put("nonce", nonce);
			dataJSON.put("signature", signature);
			resJSON.put("data", dataJSON);
		} catch (Exception e) {
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		return resJSON.toString();
	}

	/**
	 * 微信认证handler
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@RequestMapping("/certify")
	@ResponseBody
	public String certify(String signature, long timestamp, String nonce, String echostr){
		return echostr;
	}
}