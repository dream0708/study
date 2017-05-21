package com.sohu.bp.elite.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.kafka.util.SystemConfig;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.utils.crypt.AESUtil;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author nicholastang
 * 2016-07-27 16:26:15
 * 获取登录信息
 */
public final class LoginUtil {
	private static final Logger logger = LoggerFactory.getLogger(LoginUtil.class);

    public static final String BPINFO = "bpInfo";
    public static final String BPSIGN = "bpSign";
    public static final String BPID = "bpid";
    public static volatile EliteCacheService cacheService;
    public static volatile UserInfoService userInfoService;
    public static final long ExpireTimeMillSecs = 3600000;

//    static {
//        if (null == cacheService) {
//            synchronized (LoginUtil.class) {
//                cacheService = (EliteCacheService) SpringUtil.getBean("eliteCacheService");
//            }
//        }
//        if (null == userInfoService) {
//            synchronized (LoginUtil.class) {
//                userInfoService = (UserInfoService) SpringUtil.getBean("userInfoService");
//            }
//        }
//    }

    /**
     * BP服务
     */
    private static BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    /**
     * 种登录cookie
     *
     * @param bpInfo   bpInfo
     * @param bpSign   bpSign
     * @param keepTime cookie时间
     * @param response response对象
     */
    public static void setLoginCookie(String bpInfo, String bpSign, int keepTime, HttpServletResponse response) {
        if (Strings.isNullOrEmpty(bpInfo) || Strings.isNullOrEmpty(bpSign)) {
            return;
        }
        CookieUtil.setCookie(response, BPINFO, bpInfo, keepTime);
        CookieUtil.setCookieHttpOnly(response, BPSIGN, bpSign, keepTime);
    }

    /**
     * 获取登录的bpid
     *
     * @param request  request对象
     * @param response response对象
     * @return 登录的bpid, 如果登录失效则返回-1
     */
    public static long getLoginBpid(HttpServletRequest request, HttpServletResponse response) {
        String bpInfoCookieValue = CookieUtil.getCookieValue(request, BPINFO);
        String bpSignCookieValue = CookieUtil.getCookieValue(request, BPSIGN);
        if (Strings.isNullOrEmpty(bpInfoCookieValue) || Strings.isNullOrEmpty(bpSignCookieValue)) {
            return -1;
        }
        CodeMsgData getLoginUser = bpServiceAdapter.getLoginUser(bpInfoCookieValue, bpSignCookieValue);
        if (getLoginUser.getCode() == ResponseConstants.OK) {
            JSONObject data = JSONObject.parseObject(getLoginUser.getData());
            return data.getLongValue("bpid");
        } else if (getLoginUser.getCode() == ResponseConstants.INTERNALERROR || getLoginUser.getCode() == ResponseConstants.THRIFTEXCEPTION) {
            logger.error("BpServiceAdapter getLoginUser error!");
            return -1;
        } else {
            CookieUtil.deleteCookie(response, BPINFO);
            CookieUtil.deleteCookie(response, BPSIGN);
            return -1;
        }
        
    }
    /**
     * 获取微信登录bpid
     * @param request
     * @return 登录bpid, 如果登录失败则返回-1
     */
    public static long getWXLoginBpid(HttpServletRequest request){
    	String bpInfoHeaderValue = request.getHeader(StringUtils.lowerCase(BPINFO));
    	String bpSignHeaderValue = request.getHeader(StringUtils.lowerCase(BPSIGN));
    	if(StringUtils.isBlank(bpInfoHeaderValue) || StringUtils.isBlank(bpSignHeaderValue)) return -1;
    	CodeMsgData getLoginUser = bpServiceAdapter.getLoginUser(bpInfoHeaderValue, bpSignHeaderValue);
    	if(ResponseConstants.OK == getLoginUser.getCode()){
    		return JSONObject.parseObject(getLoginUser.getData()).getLongValue("bpid");
    	} else if (getLoginUser.getCode() == ResponseConstants.INTERNALERROR || getLoginUser.getCode() == ResponseConstants.THRIFTEXCEPTION){
    		logger.error("BpServiceAdapter getLoginUser error!");
    		return -1;
    	} else {
    		return -1;
    	}
    }
    /**
     * 返回App登录bpid
     * @param request
     * @return
     */
    public static long getAppLoginBpid(HttpServletRequest request, HttpServletResponse response) {
        Long bpId = getLoginBpid(request, response);
        if (null == bpId || bpId <= 0) bpId = getWXLoginBpid(request);
        return bpId;
    }

    /**
     * 获取登录的user
     *
     * @param request  request对象
     * @param response response对象
     * @return 登录的user, 如果登录失效则返回"", 返回值形如MOBILE_1111111111或者WECHAT_123123123
     */
    public static String getLoginUser(HttpServletRequest request, HttpServletResponse response) {
        String bpInfoCookieValue = CookieUtil.getCookieValue(request, BPINFO);
        String bpSignCookieValue = CookieUtil.getCookieValue(request, BPSIGN);
        if (Strings.isNullOrEmpty(bpInfoCookieValue) || Strings.isNullOrEmpty(bpSignCookieValue)) {
            return "";
        }
        CodeMsgData getLoginUser = bpServiceAdapter.getLoginUser(bpInfoCookieValue, bpSignCookieValue);
        if (getLoginUser.getCode() == ResponseConstants.OK) {
            JSONObject data = JSONObject.parseObject(getLoginUser.getData());
            return data.getString("user");
        } else if (getLoginUser.getCode() == ResponseConstants.INTERNALERROR || getLoginUser.getCode() == ResponseConstants.THRIFTEXCEPTION) {
            logger.error("BpServiceAdapter getLoginUser error!");
            return "";
        } else {
            CookieUtil.deleteCookie(response, BPINFO);
            CookieUtil.deleteCookie(response, BPSIGN);
            return "";
        }
    }

    public static String getLoginCode(long bpId) {
        if (bpId <= 0) {
            return null;
        }
        String mobile = "";
        UserInfo userInfo =  userInfoService.getUserInfoByBpid(bpId);
        if (null != userInfo) {
            mobile = userInfo.getMobile();
        }
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        String token = cacheService.getLoginToken();
        if (StringUtils.isBlank(token)) {
            return null;
        }
//        String mobile="18511878337";
//        String token="rjk21wIlTnN4YvzoubN6mxAOm145vOaH";
        String encodeToken = AESUtil.encryptStringV2(new StringBuilder(String.valueOf(bpId))
                .append(Constants.ID_SPLIT_CHAR).append(mobile).append(Constants.ID_SPLIT_CHAR).append(System.currentTimeMillis())
                .append(Constants.ID_SPLIT_CHAR).append(token).toString());
        return encodeToken;
    }

    public static boolean loginByCode(String encodeToken) {
        if (StringUtils.isBlank(encodeToken)) {
            return false;
        }
        String decodedToken = AESUtil.decryptStringV2(encodeToken);
        if (StringUtils.isBlank(decodedToken)) {
            return false;
        }
        String[] tokenArray = decodedToken.split("\\" + Constants.ID_SPLIT_CHAR);
        if (null == tokenArray || tokenArray.length != 4) {
            return false;
        }
        try {
            if (!tokenArray[3].equalsIgnoreCase(cacheService.getLoginToken())) {
                logger.info("login token expires. encodedToken={}", new String[]{encodeToken});
                return false;
            }
            if (System.currentTimeMillis() - Long.parseLong(tokenArray[2]) > ExpireTimeMillSecs) {
                logger.info("login token time expires. encodedToken={}", new String[]{encodeToken});
                return false;
            }
            UserInfo userInfo = userInfoService.getUserInfoByBpid(Long.parseLong(tokenArray[0]));
            if (null == userInfo || StringUtils.isBlank(userInfo.getMobile())) {
                logger.info("bpId is illegal. token={}", new String[]{encodeToken});
                return false;
            }
            if (!userInfo.getMobile().equalsIgnoreCase(tokenArray[1])) {
                logger.info("mobile is illegal. token={}", new String[]{encodeToken});
                return false;
            }
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
        return false;
    }
}
