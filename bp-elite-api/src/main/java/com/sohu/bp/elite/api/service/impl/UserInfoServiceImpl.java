package com.sohu.bp.elite.api.service.impl;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.decoration.model.ExpertStatus;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.service.UserInfoService;
import com.sohu.bp.elite.api.util.HttpUtil;
import com.sohu.bp.elite.api.util.LifeSignatureUtil;
import com.sohu.bp.elite.enums.BpUserType;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create time: 2015年12月3日 上午10:12:25
 * @auther dexingyang
 */

public class UserInfoServiceImpl implements UserInfoService {

    private static Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    private BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
    private BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();;

    private String apiHost;
	
	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}
    
    public UserInfo getUserInfoByBpid(Long bpid) {
    	UserInfo userInfo = new UserInfo();
    	userInfo.setAvatar(Constants.DEFAULT_AVATAR);
    	userInfo.setNick(Constants.DEFAULT_NICK);
    	if(null == bpid || bpid.longValue() <= 0)
    		return userInfo;
    	CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpid);
    	if(null == codeMsgData)
    		log.error("get userinfo failed. bpid={}", new String[]{bpid.toString()});
    	else if(codeMsgData.getCode() != ResponseConstants.OK)
    		log.error("code is not right, code message={}, bpid={}", new String[]{codeMsgData.getMessage(), bpid.toString()});
    	else
    	{
    		String infoJSONStr = codeMsgData.getData();
    		if(StringUtils.isNotBlank(infoJSONStr))
    		try
    		{
    			JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
    			if(null != infoJSON)
    			{
    				userInfo.setAvatar(infoJSON.getString("avatar"));
    				userInfo.setNick(infoJSON.getString("nick"));
    				userInfo.setMobile(infoJSON.getString("mobile"));
    			}
    		}catch(Exception e)
    		{
    			log.error("", e);
    		}
    	}
    	return userInfo;
    	
    	/*
        String realURL = apiHost+"/bpapi/bpuserinfo/getbybpid/" + String.valueOf(bpid);
        Map<String, String> params = new HashMap<String, String>();
        params.put("so_sig", LifeSignatureUtil.generateSig(params));
        String result = HttpUtil.get(realURL, params);

        try{
        	JSONObject jsonObject = JSONObject.fromObject(result);
        	if (jsonObject.getInt("code") == 0) {
        		UserInfo userInfo = new UserInfo();
        		JSONObject userInfoObject = jsonObject.getJSONObject("data");
        		userInfo.setBpid(bpid);
        		userInfo.setAvatar(userInfoObject.getString("avatar"));
        		userInfo.setEmail(userInfoObject.getString("email"));
        		userInfo.setMobile(userInfoObject.getString("mobile"));
        		userInfo.setNick(userInfoObject.getString("nick"));
        		return userInfo;
        	} else {
        		log.error("get userinfo failed: " + jsonObject.getString("msg"));
        		return null;
        	}
        }catch(Exception e){
        	log.error("get userinfo failed, result="+result);
        }
        return null;
        */
    }

    public UserInfo getUserInfoByMobile(String mobile) {

        String realURL = apiHost+"/bpapi/bpuserinfo/getbymobile/" + mobile;
        Map<String, String> map = new HashMap<String, String>();
        map.put("so_sig", LifeSignatureUtil.generateSig(map));
        String result = HttpUtil.get(realURL, map);
        try{
        	JSONObject jsonObject = JSONObject.fromObject(result);
        	if (jsonObject.getInt("code") == 0) {
        		UserInfo userInfo = new UserInfo();
        		JSONObject userInfoObject = jsonObject.getJSONObject("data");
        		userInfo.setBpid(userInfoObject.getLong("bpid"));
        		userInfo.setAvatar(userInfoObject.getString("avatar"));
        		userInfo.setEmail(userInfoObject.getString("email"));
        		userInfo.setMobile(userInfoObject.getString("mobile"));
        		userInfo.setNick(userInfoObject.getString("nick"));
        		return userInfo;
        	} else {
        		log.error("get userinfo failed: " + jsonObject.getString("msg"));
        		return null;
        	}
        }catch(Exception e){
        	log.error("get userinfo failed, result="+result);
        }
        return null;
    }

    public boolean hasUser(String mobile) {
        String realURL = apiHost+"/bpapi/bpuserinfo/getbymobile/" + mobile;
        Map<String, String> map = new HashMap<String, String>();
        map.put("so_sig", LifeSignatureUtil.generateSig(map));
        String result = HttpUtil.get(realURL, map);
        try{
        	JSONObject jsonObject = JSONObject.fromObject(result);
        	return jsonObject.getInt("code") == 0;
        }catch(Exception e){
        }
        return false;
    }

	@Override
	public long getBpidByMobile(String mobile) {
		long retVal = -1;
		if(StringUtils.isBlank(mobile))
			return retVal;
		
		CodeMsgData codeMsgData = bpServiceAdapter.getBpidByMobile(mobile);
    	if(null == codeMsgData)
    		log.error("get bpid failed. mobile={}", new String[]{mobile});
    	else if(codeMsgData.getCode() != ResponseConstants.OK)
    		log.error("code is not right, code message={}, mobile={}", new String[]{codeMsgData.getMessage(), mobile});
    	else
    	{
    		String infoJSONStr = codeMsgData.getData();
    		if(StringUtils.isNotBlank(infoJSONStr))
    		try
    		{
    			JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
    			if(null != infoJSON)
    			{
    				retVal = infoJSON.getLong("bpid");
    			}
    		}catch(Exception e)
    		{
    			log.error("", e);
    		}
    	}
    	
    	return retVal;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.api.service.UserInfoService#addBpUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public long addBpUser(String mobile, String nick, String avatar, String ip, Integer port) {
		Long bpId = 0L;
		try
		{
			CodeMsgData codeMsgData = bpServiceAdapter.addBpUser(mobile, nick, avatar, ip.toString(), port);
			if(null != codeMsgData && codeMsgData.getCode() == ResponseConstants.OK)
			{
				String data = codeMsgData.getData();
				if(StringUtils.isNotBlank(data))
				{
					JSONObject dataJSON = JSONObject.fromObject(data);
					if(dataJSON.containsKey("bpId") && dataJSON.getLong("bpId") > 0)
						bpId = dataJSON.getLong("bpId");
				}
			}
			else
			{
				log.warn("code={}, msg={}", new String[]{codeMsgData.getCode().toString(), codeMsgData.getMessage()});
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
		return bpId;
	}

	@Override
	public UserInfo getDecorateUserInfoByBpid(Long bpid) {
		if(null == bpid || bpid.longValue() <= 0)
			return null;
		UserInfo userInfo = new UserInfo();
		userInfo.setBpid(bpid);
		BpUserType bpUserType = this.getBpUserType(bpid);
		switch(bpUserType)
		{
		case COMPANY:
			try
			{
				List<Expert> expertList = bpDecorationServiceAdapter.getExpertListById(bpid);
	        	for(Expert expert:expertList)
	        	{
	        		if(null != expert && expert.getStatus().equals(ExpertStatus.PASS) && expert.getManageCompanyId() > 0)
	        		{
	        			DecorationCompany company = bpDecorationServiceAdapter.getDecorationCompanyById(expert.getManageCompanyId());
	        			userInfo.setAvatar(company.getLogo());
	        			String nick = company.getShortName();
	        			if(StringUtils.isBlank(nick))
	        				nick = company.getName();
	        			userInfo.setNick(nick);
	        			
	        			UserInfo oriUserInfo = this.getUserInfoByBpid(bpid);
	        			userInfo.setMobile(oriUserInfo.getNick());
	        		}
	        	}
			}catch(Exception e)
			{
				log.error("", e);
			}
        	break;
        default:
        	userInfo = this.getUserInfoByBpid(bpid);
        	break;
		}
		return userInfo;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.api.service.UserInfoService#getBpUserType(java.lang.Long)
	 */
	@Override
	public BpUserType getBpUserType(Long bpid) {
		BpUserType bpUserType = BpUserType.NONE;
		if(null == bpid || bpid.longValue() <= 0)
			return bpUserType;
		try {
            List<Expert> expertList = bpDecorationServiceAdapter.getExpertListById(bpid);
            boolean passed = false;
            for (Expert expert : expertList) {
                if (expert.getStatus().equals(ExpertStatus.PASS)) {
                	passed = true;
                	
                    if (expert.getRole().equals(ExpertRole.SELF_MEDIA)) {
                        if(expert.getManageCompanyId() > 0)
                        {
                        	bpUserType = BpUserType.COMPANY;
                        	break;
                        }
                        else
                        	bpUserType = BpUserType.SELF_MEDIA;
                    }
                    if (expert.getRole().equals(ExpertRole.FOREMAN)) {
                        bpUserType = BpUserType.FOREMAN;
                        break;
                    }
                    if (expert.getRole().equals(ExpertRole.DESIGNER)) {
                        bpUserType = BpUserType.DESIGNER;
                        break;
                    }
                }
            }
            
            if(bpUserType == BpUserType.NONE)
            	bpUserType = BpUserType.NORMAL_USER;
        } catch (Exception e) {
            log.error("", e);
        }
		
		return bpUserType;
	}

	public static void main(String[] args){
		BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
		CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(8995610L);
		System.out.println(codeMsgData);
	}
}