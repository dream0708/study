package com.sohu.bp.elite.service.web.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.decoration.model.ExpertStatus;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.BpUserType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.util.BpUserUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.IdentityUtil;
import com.sohu.bp.elite.util.ImageUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.elite.util.TagUtil;

/**
 * 
 * @author nicholastang
 * 2016-08-17 17:57:03
 * TODO 和bp user 相关服务的实现类
 */
@Service
public class BpUserServiceImpl implements BpUserService
{
    private UserInfoService userInfoService;

	private Logger log = LoggerFactory.getLogger(BpUserServiceImpl.class);
	private BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
//    public static final String DesignerHome = "/ask/designer/";
//    public static final String ForemanHome = "/ask/foreman/";
//    public static final String SelfmediaHome = "/ask/self/";
//    public static final String CompanyHome = "/ask/company/";
//    public static final String AskUserHome = "/ask/user/";
//	改homeurl为短url
  public static final String DesignerHome = "/pd/";
  public static final String ForemanHome = "/pf/";
  public static final String SelfmediaHome = "/ps/";
  public static final String CompanyHome = "/pc/";
  public static final String AskUserHome = "/pu/";
    
    private CacheManager redisCacheManager;
    private RedisCache bpRedisCache;

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}
	
	public CacheManager getRedisCacheManager() {
		return redisCacheManager;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}
	
	public void init(){
		bpRedisCache = (RedisCache) redisCacheManager.getCache("");
	}
	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.web.BpUserService#getUserHomeUrl(java.lang.Long)
	 */
    @Override
	public String getUserHomeUrl(Long bpid) {
		String userHome = "http://bar.focus.cn/";
		
		BpUserType bpUserType = BpUserUtil.getBpUserType(bpid);
		if(null == bpUserType)
			return userHome;
		switch(bpUserType)
		{
		case NONE:
			userHome = Constants.PAGE_404;
			break;
		case NORMAL_USER:
//			userHome = AskUserHome + IDUtil.encodeId(bpid) + "/home.html";
			userHome = AskUserHome + IDUtil.encodeId(bpid);
			break;
		case SELF_MEDIA:
//			userHome = SelfmediaHome + IDUtil.encodeId(bpid) + "/home.html";
			userHome = SelfmediaHome + IDUtil.encodeId(bpid);
			break;
		case DESIGNER:
//			userHome = DesignerHome + IDUtil.encodeId(bpid) + "/home.html";
			userHome = DesignerHome + IDUtil.encodeId(bpid);
			break;
		case FOREMAN:
//			userHome = ForemanHome + IDUtil.encodeId(bpid) + "/home.html";
			userHome = ForemanHome + IDUtil.encodeId(bpid);
			break;
		case COMPANY:
			try
			{
				Expert expert = bpDecorationServiceAdapter.getExpertByIdRole(bpid, ExpertRole.SELF_MEDIA);
				if(null != expert && expert.getManageCompanyId() > 0)
//					userHome = CompanyHome + IDUtil.encodeId(expert.getManageCompanyId()) + "/home.html";
					userHome = CompanyHome + IDUtil.encodeId(expert.getManageCompanyId());
				else
					userHome = Constants.PAGE_404;
			}catch(Exception e)
			{
				log.error("", e);
				userHome = Constants.PAGE_503;
			}
			
			break;
		}
        return userHome;
	}
    
    @Override
    public UserDetailDisplayBean getUserDetailByBpId(long bpid, Long viewerId, boolean needTag) {
        UserDetailDisplayBean userDetail = new UserDetailDisplayBean();
        if(bpid <= 0)
            return userDetail;
        try
        {
            BpUserType bpUserType = BpUserUtil.getBpUserType(bpid);
            userDetail.setBpId(IDUtil.encodeId(bpid));
            userDetail.setHomeUrl(getUserHomeUrl(bpid));
            userDetail.setBpUserType(bpUserType.getValue());
            if(bpUserType == BpUserType.COMPANY)
            {

                List<Expert> expertList = bpDecorationServiceAdapter.getExpertListById(bpid);
                for(Expert expert:expertList)
                {
                    if(null != expert && expert.getStatus().equals(ExpertStatus.PASS) && expert.getManageCompanyId() > 0)
                    {
                        DecorationCompany company = bpDecorationServiceAdapter.getDecorationCompanyById(expert.getManageCompanyId());
                        userDetail.setAvatar(company.getLogo());
                        userDetail.setNick(company.getName());
                        userDetail.setCompanyId(IDUtil.encodeId(company.getId()));
                    }
                }
            }
            else
            {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpid);
                userDetail.setAvatar(userInfo.getAvatar());
                userDetail.setNick(userInfo.getNick());
            }
            int[] nums = InteractionUtil.getInteractionNumsForUser(bpid);
            userDetail.setFansNum(nums[0]);
            userDetail.setQuestionNum(nums[1]);
            userDetail.setAnswerNum(nums[2]);
            userDetail.setLikeNum(nums[3]);
            if(null == viewerId || viewerId <= 0)
            {
                userDetail.setHasFollowed(false);
            }
            else
            {
                userDetail.setHasFollowed(InteractionUtil.hasFollowed(viewerId, BpType.Elite_User.getValue(), bpid, BpType.Elite_User.getValue()));
                if(bpid == viewerId)
                    userDetail.setOwner(true);
            }
            
            if(null != bpUserType && 
                    (bpUserType == BpUserType.COMPANY || bpUserType == BpUserType.DESIGNER || bpUserType == BpUserType.FOREMAN || bpUserType == BpUserType.SELF_MEDIA))
                userDetail.setAuthenticated(true);
            try{
            TEliteUser user = eliteAdapter.getUserByBpId(bpid);
            userDetail.setIdentity(user.getIdentity());
            userDetail.setIdentityString(IdentityUtil.getIdentityString(user.getIdentity()));
            userDetail.setDescription(user.getDescription());
            if (needTag) {
                userDetail.setTags(TagUtil.getTagNamesByIds(user.getTagIds()));
            }
            }catch(Exception e){
                //ignore
            }
        }catch(Exception e)
        {
            log.error("", e);
        }
        return userDetail;
    }
    
    @Override
    public UserDetailDisplayBean getUserDetailByBpId(long bpid, Long viewerId) {
        return getUserDetailByBpId(bpid, viewerId, false);
    }

	@Override
	public boolean checkHasAnswered(Long bpId, Long questionId) {
		if(bpId == null || questionId == null)
			return false;

		TSearchAnswerCondition condition = new TSearchAnswerCondition();
		condition.setBpId(bpId)
				.setQuestionId(questionId)
				.setFrom(0)
				.setCount(1)
				.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue(), EliteAnswerStatus.REJECTED.getValue()));

		try {
			TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
			if(listResult != null && listResult.getTotal() > 0){
				return true;
			}
		} catch (TException e) {
			log.error("");
		}

		return false;
	}
	
    @Override
    public boolean checkHasChoosed(Long bpId, Long questionId) {
        if (null == bpId || null == questionId) return false;
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setQuestionId(questionId).setBpId(bpId).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.CHOOSE.getValue()))
        .setFrom(0).setCount(1);
        try {
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            if (null != listResult && listResult.getTotal() > 0) return true;
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }
    
    @Override
    public Integer getChoosedOption(Long bpId, Long questionId) {
        if (null == bpId || bpId <= 0 || null == questionId || questionId <= 0) return null;
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setQuestionId(questionId).setBpId(bpId).setFrom(0).setCount(1);
        try {
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            if (null != listResult && listResult.getTotal() > 0) {
                return (int)listResult.getAnswers().get(0).getSpecialId(); 
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

	@Override
	public boolean checkHasPhoneNo(Long bpId) {
//		if(bpId == null)
//			return false;
//
//		UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
//		return userInfo != null && StringUtils.isNotBlank(userInfo.getMobile());
		return true;
	}

	@Override
	public UserDetailDisplayBean getUserSimpleByBpId(Long bpid) {
        UserDetailDisplayBean userDetail = new UserDetailDisplayBean();
        
        try {
            boolean flag = true;
            BpUserType bpUserType = BpUserType.NORMAL_USER;
            List<Expert> experts = bpDecorationServiceAdapter.getExpertListById(bpid);
            if (null != experts && experts.size() > 0) {
                for (Expert expert : experts) {
                    if (expert.getStatus().equals(ExpertStatus.PASS)) {
                        if (expert.getRole().equals(ExpertRole.SELF_MEDIA)) {
                            if (expert.getManageCompanyId() > 0) {
                                bpUserType = BpUserType.COMPANY;
                                DecorationCompany company = bpDecorationServiceAdapter.getDecorationCompanyById(expert.getManageCompanyId());
                                userDetail.setAvatar(ImageUtil.removeImgProtocol(company.getLogo()));
                                userDetail.setCompanyId(IDUtil.encodeId(company.getId()));
                                String nick = company.getShortName();
                                if(StringUtils.isBlank(nick))
                                    nick = company.getName();
                                userDetail.setNick(nick);
                                flag = false;
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
            } 
	        userDetail.setBpId(IDUtil.encodeId(bpid));
	        userDetail.setHomeUrl(getUserHomeUrl(bpid));
	        userDetail.setBpUserType(bpUserType.getValue());
	        
//	        UserInfo userInfo = userInfoService.getDecorateUserInfoByBpid(bpid);
//	        userDetail.setAvatar(userInfo.getAvatar());
//	        userDetail.setNick(userInfo.getNick());
	        
            if (flag) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpid);
                if (null != userInfo) {
                    userDetail.setAvatar(userInfo.getAvatar());
                    userDetail.setNick(userInfo.getNick());
                }
            }
	        
	        if(null != bpUserType && 
	        		(bpUserType == BpUserType.COMPANY || bpUserType == BpUserType.DESIGNER || bpUserType == BpUserType.FOREMAN || bpUserType == BpUserType.SELF_MEDIA))
	        	userDetail.setAuthenticated(true);
	        try{
	        TEliteUser user = eliteAdapter.getUserByBpId(bpid);
	        userDetail.setIdentity(user.getIdentity());
	        userDetail.setIdentityString(IdentityUtil.getIdentityString(user.getIdentity()));
	        userDetail.setDescription(user.getDescription());
	        }catch(Exception e){
	        	//ignore
	        }
        }catch(Exception e)
        {
        	log.error("", e);
        }
        return userDetail;
	}

	@Override
	public boolean checkNotInBlackList(Long bpid) {
		boolean retVal = true;
		if(null == bpid) return retVal;
		UserInfo userInfo = userInfoService.getUserInfoByBpid(bpid);
		String mobile = userInfo.getMobile();
		if(StringUtils.isBlank(mobile)) return retVal;
		if(bpRedisCache.sIsMember("moblie.black.list", mobile)) retVal = false;
		return retVal;
	}

    @Override
    public UserDetailDisplayBean getUserBaseByBpId(Long bpid) {
        UserDetailDisplayBean userDetail = new UserDetailDisplayBean();
        if (null == bpid || bpid <= 0) return userDetail;
        BpUserType bpUserType = BpUserType.NORMAL_USER;
        try {
            boolean flag = true;
            userDetail.setBpId(IDUtil.encodeId(bpid));
            List<Expert> experts = bpDecorationServiceAdapter.getExpertListById(bpid);
            if (null != experts && experts.size() > 0) {
                for (Expert expert : experts) {
                    if (expert.getStatus().equals(ExpertStatus.PASS)) {
                        if (expert.getRole().equals(ExpertRole.SELF_MEDIA)) {
                            if (expert.getManageCompanyId() > 0) {
                                bpUserType = BpUserType.COMPANY;
                                userDetail.setBpIdOrigin(IDUtil.encodeIdOrigin(expert.getManageCompanyId()));
                                DecorationCompany company = bpDecorationServiceAdapter.getDecorationCompanyById(expert.getManageCompanyId());
                                userDetail.setAvatar(ImageUtil.removeImgProtocol(company.getLogo()));
                                userDetail.setCompanyId(IDUtil.encodeId(company.getId()));
                                String nick = company.getShortName();
                                if(StringUtils.isBlank(nick))
                                    nick = company.getName();
                                userDetail.setNick(nick);
                                flag = false;
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
            } 
            userDetail.setBpUserType(bpUserType.getValue());
            if (flag) {
                UserInfo userInfo = userInfoService.getUserInfoByBpid(bpid);
                if (null != userInfo) {
                    userDetail.setAvatar(userInfo.getAvatar());
                    userDetail.setNick(userInfo.getNick());
                }
            }
            
            TEliteUser user = eliteAdapter.getUserByBpId(bpid);
            userDetail.setIdentity(user.getIdentity());
            userDetail.setIdentityString(IdentityUtil.getIdentityString(user.getIdentity()));
            userDetail.setDescription(user.getDescription());
        } catch(Exception e) {
            log.error("", e);
        }
        return userDetail;
    }
	
}