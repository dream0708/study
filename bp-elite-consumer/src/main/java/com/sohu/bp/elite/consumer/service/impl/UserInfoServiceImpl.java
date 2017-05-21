package com.sohu.bp.elite.consumer.service.impl;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.decoration.model.ExpertStatus;
import com.sohu.bp.elite.consumer.service.UserInfoService;
import com.sohu.bp.elite.enums.BpUserType;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create time: 2015年12月3日 上午10:12:25
 * @auther dexingyang
 */

public class UserInfoServiceImpl implements UserInfoService {

    private static Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    private BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	
    public UserInfo getUserInfoByBpid(Long bpid) {
    	UserInfo userInfo = null;
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
    				userInfo = new UserInfo();
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

}
