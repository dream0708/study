package com.sohu.bp.elite.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.decoration.model.ExpertStatus;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.enums.BpUserType;
import com.sohu.bp.elite.enums.EliteQAErrorCode;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.UserRestrictService;
import com.sohu.bp.elite.service.web.impl.BpUserServiceImpl;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 
 * @author nicholastang
 * 2016-08-26 11:37:19
 * TODO 关于bpuser的工具类
 */
public class BpUserUtil
{
	private static Logger log = LoggerFactory.getLogger(BpUserUtil.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static BpDecorationServiceAdapter bpDecorationServiceAdapter;
	private static BpServiceAdapter bpServiceAdapter;
	static{
		bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
		bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();
	}
	
	public static BpUserType getBpUserType(Long bpid)
	{
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
	
	/**
	 * 填充用户登录安全信息
	 * @param user
	 */
	public static void fillUserLoginInfo(UserDetailDisplayBean user)
	{
		try
		{
			if(null != user && StringUtils.isNotBlank(user.getBpId()))
			{
				Long decodedBpId = IDUtil.decodeId(user.getBpId());
				if(null == decodedBpId || decodedBpId.longValue() <= 0)
					return;
				
				CodeMsgData codeMsgData = bpServiceAdapter.getLoginHistory(decodedBpId);
				if(codeMsgData.getCode() == ResponseConstants.OK)
				{
					StringBuilder sb = new StringBuilder("");
					String loginHistory = codeMsgData.getData();
					JSONArray loginHistoryArray = JSONArray.fromObject(loginHistory);
					if(null != loginHistoryArray && loginHistoryArray.size() > 0)
					{
						JSONObject loginHistoryJSON = loginHistoryArray.getJSONObject(0);
						Long ct = loginHistoryJSON.getLong("ct");
						if(null != ct)
							sb.append(sdf.format(new Date(ct))).append(" ");
						
						String location = loginHistoryJSON.getString("province") + " " + loginHistoryJSON.getString("city");
						if(StringUtils.isNotBlank(location))
							sb.append(location);
					}
					
					user.setLastLoginInfo(sb.toString());
				}
				else
				{
					log.warn("get login history by bpId={} failed. code={}, msg={}", new String[]{decodedBpId.toString(), codeMsgData.getCode().toString(), codeMsgData.getMessage()});
				}
				
				codeMsgData = bpServiceAdapter.getBpUserShadowStatus(decodedBpId);
				if(codeMsgData.getCode() == ResponseConstants.OK)
				{
					String accountSafeInfo= codeMsgData.getData();
					JSONObject accountSafeJSON = JSONObject.fromObject(accountSafeInfo);
					Integer securityStatus = accountSafeJSON.getInt("bpUserPasswordSecurityStatus");
					if(null == securityStatus || securityStatus == 1)
						user.setAccountSafe(false);
				}
				else
				{
					log.warn("get account safe status by bpId={} failed. code={}, msg={}", new String[]{decodedBpId.toString(), codeMsgData.getCode().toString(), codeMsgData.getMessage()});
				}
			}
		}catch(Exception e)
		{
			log.error("", e);
		}
	}		
	
	//用于判断用户提问和回答的权限
	public static String bpUserValid(TEliteQuestion question, String content, Long bpid){
		String result = "";
		BpUserService bpUserService = (BpUserService) SpringUtil.getBean("bpUserService");
		UserRestrictService userRestrictService = (UserRestrictService) SpringUtil.getBean("userRestrictService");
		if(EliteStatusUtil.isInvalidStatus(question)){
			result = JSONObject.fromObject(EliteQAErrorCode.ANSWER_QUESTION_INVALID.toString()).toString();
		} else if (bpUserService.checkHasAnswered(bpid, question.getId())){
			result = JSONObject.fromObject(EliteQAErrorCode.ANSWER_ALREADY_ANSWERD.toString()).toString();
		} else if (!bpUserService.checkHasPhoneNo(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_PHONE_UNBOUND.toString()).toString();
		} else if (userRestrictService.isAnswerRestrict(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_RESTRICT_ANSWER.toString()).toString();
		} else if (!bpUserService.checkNotInBlackList(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_INBLACKLIST.toString()).toString();
		} else if (StringUtils.isBlank(content)){
			result = JSONObject.fromObject(EliteQAErrorCode.ANSWER_CONTENT_BLANK.toString()).toString();
		}
		return result;
		
	}
	
	//用于判断用户投票和站队的权限
	public static String bpUserChooseValid(TEliteQuestion question, Long bpid) {
	    String result = "";
	    BpUserService bpUserService = (BpUserService) SpringUtil.getBean("bpUserService");
        if(EliteStatusUtil.isInvalidStatus(question)){
            result = JSONObject.fromObject(EliteQAErrorCode.ANSWER_QUESTION_INVALID.toString()).toString();
        } else if (bpUserService.checkHasChoosed(bpid, question.getId())){
            result = JSONObject.fromObject(EliteQAErrorCode.ANSWER_ALREADY_CHOOSED.toString()).toString();
        } else if (!bpUserService.checkHasPhoneNo(bpid)){
            result = JSONObject.fromObject(EliteQAErrorCode.USER_PHONE_UNBOUND.toString()).toString();
        } else if (!bpUserService.checkNotInBlackList(bpid)){
            result = JSONObject.fromObject(EliteQAErrorCode.USER_INBLACKLIST.toString()).toString();
        }
        return result;
	}
	
	public static String bpUserValid(String title, Long bpid){
		String result = "";
		BpUserService bpUserService = (BpUserService) SpringUtil.getBean("bpUserService");
		UserRestrictService userRestrictService = (UserRestrictService) SpringUtil.getBean("userRestrictService");
		if(StringUtils.isBlank(title)){
			result = JSONObject.fromObject(EliteQAErrorCode.QUESTION_TITLE_BLANK.toString()).toString();
		} else if(!bpUserService.checkHasPhoneNo(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_PHONE_UNBOUND.toString()).toString();
		} else if(userRestrictService.isQuestionRestrict(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_RESTRICT_QUESION.toString()).toString();
		} else if(!bpUserService.checkNotInBlackList(bpid)){
			result = JSONObject.fromObject(EliteQAErrorCode.USER_INBLACKLIST.toString()).toString();
		}
		return result;
	}
}