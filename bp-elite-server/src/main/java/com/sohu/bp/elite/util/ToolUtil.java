package com.sohu.bp.elite.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-10-18 20:10:01
 * TODO 问答工具类
 */
public class ToolUtil
{
    private static final Logger logger = LoggerFactory.getLogger(ToolUtil.class);
    private static final BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    //生成短地址的新浪服务接口及分配的sourceCode
    public static String shortUrlGeneratorApi = "http://api.t.sina.com.cn/short_url/shorten.json";
    public static String sourceCode = "3271760578";
    
    public static String getQuestionUri(String encodedQuestionId)
    {
        return "/q/" + encodedQuestionId;
    }

    public static String getLoginQuestionUrl(String askDomain, String encodedQuestionId, long bpId) {
        String url = ToolUtil.getShortUrl("https://" + askDomain + getQuestionUri(encodedQuestionId));
        try {
            CodeMsgData codeMsgData = bpServiceAdapter.getLoginToken(bpId);
            if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
                JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
                if (null != dataJSON && dataJSON.containsKey("token")) {
                    String token = dataJSON.getString("token");
                    logger.info("login token is {} with bpid={}", new String[]{token, String.valueOf(bpId)});
                    url = ToolUtil.getShortUrl(new StringBuilder("https://")
                            .append(askDomain)
                            .append("/tlogin?ru=").append(getQuestionUri(encodedQuestionId))
                            .append("&token=").append(token).toString());
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return url;
    }
    
    public static String getAnswerUri(String encodedAnswerId)
    {
        return "/a/" + encodedAnswerId;
    }

    public static String getLoginAnswerUrl(String askDomain, String encodedAnswerId, long bpId) {
        String url = ToolUtil.getShortUrl("https://" + askDomain + getQuestionUri(encodedAnswerId));
        try {
            CodeMsgData codeMsgData = bpServiceAdapter.getLoginToken(bpId);
            if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
                JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
                if (null != dataJSON && dataJSON.containsKey("token")) {
                    String token = dataJSON.getString("token");
                    logger.info("login token is {} with bpid={}", new String[]{token, String.valueOf(bpId)});
                    url = ToolUtil.getShortUrl(new StringBuilder("https://")
                            .append(askDomain)
                            .append("/tlogin?ru=").append(getAnswerUri(encodedAnswerId))
                            .append("&token=").append(token).toString());
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return url;
    }
    
    public static String getMyFansUri()
    {
        return "/mf";
    }

    public static String getLoginMyFansUrl(String askDomain, long bpId) {
        String url = ToolUtil.getShortUrl("https://" + askDomain + getMyFansUri());
        try {
            CodeMsgData codeMsgData = bpServiceAdapter.getLoginToken(bpId);
            if (null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK) {
                JSONObject dataJSON = JSONObject.fromObject(codeMsgData.getData());
                if (null != dataJSON && dataJSON.containsKey("token")) {
                    String token = dataJSON.getString("token");
                    logger.info("login token is {} with bpid={}", new String[]{token, String.valueOf(bpId)});
                    url = ToolUtil.getShortUrl(new StringBuilder("https://")
                            .append(askDomain)
                            .append("/tlogin?ru=").append(getMyFansUri())
                            .append("&token=").append(token).toString());
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return url;
    }

    public static String getTagUri(String encodedTagId) {
        return "/t/" + encodedTagId;
    }
    
    public static String getShortUrl(String oriUrl)
    {
        String shortUrl = "";
        if(StringUtils.isBlank(oriUrl))
            return shortUrl;
        Map<String, String> params = new HashMap<String, String>();
        params.put("source", sourceCode);
        params.put("url_long", oriUrl);
        String response = HttpUtil.get(shortUrlGeneratorApi, params);
        if(StringUtils.isBlank(response) || response.contains("\"code\":-1"))
        {
            logger.error("get short url failed.oriUrl={}, response={}", new String[]{oriUrl, response});
        }
        else
        {
            JSONArray responseJSONArray = JSONArray.fromObject(response);
            if(null != responseJSONArray && responseJSONArray.size() > 0)
            {
                JSONObject responseJSON = responseJSONArray.getJSONObject(0);
                if(null != responseJSON && responseJSON.containsKey("url_short"))
                {
                    shortUrl = responseJSON.getString("url_short");
                    shortUrl = shortUrl.substring(7);
                    logger.info("get short url success.shortUrl=http://{}, oriUrl={}", new String[]{shortUrl, oriUrl});
                }
            }
        }
        return shortUrl;
    }
    
    public static void main(String[] args)
    {
        System.out.println(ToolUtil.getShortUrl("http://bar.focus.cn/login/token?token=a1ffe42db44ed1b92a75b34b14bfcac98fa524d6a26fbb83e5c45ee7c82f7ac7ca4f8441aa7b721ed4299632bed0dbd9c4dc53acf1e46ea2169febad4df835441629c9b4adde25217a687ea2a543f0c2"));
    }
}