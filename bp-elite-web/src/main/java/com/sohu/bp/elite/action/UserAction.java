package com.sohu.bp.elite.action;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.person.UserAtDisplayBean;
import com.sohu.bp.elite.action.bean.person.UserInviteDisplayBean;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.InviteService;
import com.sohu.bp.thallo.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcraft.jsch.Session;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.outer.api.adapter.BpDecorationOuterApiAdapterFactory;
import com.sohu.bp.decoration.outer.api.adapter.BpInteractionServiceAdapter;
import com.sohu.bp.elite.action.bean.fans.FansUserDisplayBean;
import com.sohu.bp.elite.action.bean.favorite.FavoriteAnswerDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.person.UserStatusItemDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.SideBarPageType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.task.EliteRecAsyncTask;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.BpUserUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.elite.util.RequestUtil;
import com.sohu.bp.elite.util.ToolUtil;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.enums.ActionType;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import cn.focus.rec.collector.RecLogHelper;
import cn.focus.rec.enums.BehaviorType;
import cn.focus.rec.log.BehaviorLog;
import cn.focus.rec.model.ItemType;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/15
 */
@Controller
//@RequestMapping("user")
public class UserAction {

	private static Logger logger = LoggerFactory.getLogger(UserAction.class);
	
    private BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
	private BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpInteractionServiceAdapter bpInteractionServiceAdapter = BpDecorationOuterApiAdapterFactory.getBpInteractionServiceAdapter();
	private BpServiceAdapter serviceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();

    @Resource
    private BpUserService bpUserService;
    
    @Resource
	private WrapperPageService wrapperPageService;
    @Resource
    private UserInfoService userInfoService;
	@Resource
	private InviteService inviteService;
	@Resource
	private FeedService feedService;

	@ResponseBody
	@RequestMapping(value = {"u/userinfo-byid"}, produces = "application/json;charset=utf-8")
	public String getUserInfoByBpid(String bpId) {
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if (StringUtils.isBlank(bpId)) {
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		Long decodedBpId = IDUtil.decodeId(bpId);
		if (null == decodedBpId || decodedBpId.longValue() <= 0) {
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		List<Long> bpIdList = new ArrayList<>();
		bpIdList.add(decodedBpId);
		List<UserAtDisplayBean> users = this.getUsersAtBean(bpIdList);
		if (null != users && users.size() > 0) {
			resJSON.put("data", users.get(0));
			return resJSON.toString();
		}
		resJSON = ResponseJSON.getErrorInternalJSON();
		return resJSON.toString();
	}

	@ResponseBody
	@RequestMapping(value = {"u/default-at-users"}, produces = "application/json;charset=utf-8")
	public String getDefaultAtUsers(int count, HttpSession session) {
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if (count <= 0) {
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		final List<Long> bpIdList = new ArrayList<>();
		List<UserAtDisplayBean> users = new ArrayList<>();
		int start = 0;
		try {

			if (null == bpId) {
				Integer total = inviteService.getRecomNum();
				if (count > total) {
					count = total;
				}
				bpIdList.addAll(inviteService.getRecomInviteList(start, count));
			} else {
				UserObjectRelationListResult listResult = thalloServiceAdapter.getActiveListByObjectType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, start, count);
				if (listResult != null && listResult.getTotal() > 0) {
					List<UserObjectRelation> relations = listResult.getUserObjectRelationList();
					relations.forEach(relation -> bpIdList.add(relation.getObjectId()));
				} else {
				}
			}
			users = this.getUsersAtBean(bpIdList);
			resJSON.put("data", users);
		} catch (Exception e) {
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		return resJSON.toString();
	}

	//获取登录的用户信息
	@ResponseBody
	@RequestMapping(value = {"ask/user/getInfo"}, produces = "application/json;charset=utf-8")
	public String getUserInfo(HttpSession session){
		return getUserInfoMethod(session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/user/getInfo"}, produces = "application/json;charset=utf-8")
	public String wxGetUserInfo(HttpSession session){
		return getUserInfoMethod(session);
	}
	
	public String getUserInfoMethod(HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("get user info <bpId={}>", bpId);

		UserDetailDisplayBean user = bpUserService.getUserDetailByBpId(bpId, null);
		JSONObject jsonObject = ResponseJSON.getSucJSON();

		JSONObject jsonData = new JSONObject();
		jsonData.put("user", user);
		jsonObject.put("data", jsonData);

		return jsonObject.toString();
	}

	//查看用户是否有手机号
	@ResponseBody
	@RequestMapping(value = {"ask/user/checkHasPhoneNo"}, produces = "application/json;charset=utf-8")
	public String checkHasPhoneNo(HttpSession session){
		return checkHasPhoneNoMethod(session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/user/checkHasPhoneNo"}, produces = "application/json;charset=utf-8")
	public String wxCheckHasPhoneNo(HttpSession session){
		return checkHasPhoneNoMethod(session);
	}
	
	public String checkHasPhoneNoMethod(HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		JSONObject jsonObject = ResponseJSON.getSucJSON();
		JSONObject jsonData = new JSONObject();
		boolean hasPhoneNo = bpUserService.checkHasPhoneNo(bpId);
		logger.info("check if user <bpId={}> has phone no, result={}", bpId, hasPhoneNo);

		jsonData.put("hasPhoneNo", hasPhoneNo);
		jsonObject.put("data", jsonData);
		return jsonObject.toString();
	}

	//我的收藏列表页
	@RequestMapping(value = {"ask/user/favorites.html", "ca"})
	public String getMyFavorites(@ModelAttribute("bean")FavoriteAnswerDisplayBean bean, HttpSession session, 
			HttpServletRequest request, HttpServletResponse response){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			return Constants.PAGE_404;
		}
		logger.info("get favorite answer list for user <bpId={}>", bpId);

		List<TEliteAnswer> answers = getFavoriteAnswers(bpId, bean);
		logger.info("total favorite answer size=" + bean.getTotal());
		
		bean.setAnswerList(ConvertUtil.convertAnswerList(answers, bpId, null));
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
		
		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/collection/collection-answer";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			
			BpUserUtil.fillUserLoginInfo(bean.getUser());
			
			String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_FAVORITES);
			pageWrapper.setSidebarHtml(sideBarHtml);
			
			bean.setPageWrapper(pageWrapper);
			return "pc/collection/collection-answer";
		}else {
			return Constants.PAGE_404;
		}
	}

	//我的收藏列表json数据
	@ResponseBody
	@RequestMapping(value = {"ask/user/favorites.json", "ca.json"}, produces = "application/json;charset=utf-8")
	public String getMyFavoritesJsonData(FavoriteAnswerDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getMyFavoritesJsonDataMethod(bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ca.json"}, produces = "application/json;charset=utf-8")
	public String wxGetMyFavoritesJsonData(FavoriteAnswerDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getMyFavoritesJsonDataMethod(bean, session, request);
	}
	
	public String getMyFavoritesJsonDataMethod(FavoriteAnswerDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		logger.info("get favorite answer list for user <bpId={}>", bpId);

		List<TEliteAnswer> answers = getFavoriteAnswers(bpId, bean);
		logger.info("total favorite answer size=" + bean.getTotal());

		bean.setAnswerList(ConvertUtil.convertAnswerList(answers, bpId, null));
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
		
		resJSON.put("data", bean);
		return resJSON.toString();
	}

	//关注
	@ResponseBody
    @RequestMapping(value = {"ask/user/follow"}, produces = "application/json;charset=utf-8")
    public String follow(String bpId, HttpSession session, HttpServletRequest request){
		return followMethod(bpId, session, request);
	}
		
	@ResponseBody
    @RequestMapping(value = {"wx/ask/user/follow"}, produces = "application/json;charset=utf-8")
    public String wxFollow(String bpId, HttpSession session, HttpServletRequest request){
		return followMethod(bpId, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/ask/user/follow"}, produces = "application/json;charset=utf-8")
    public String appFollow(String bpId, HttpSession session, HttpServletRequest request){
        return followMethod(bpId, session, request);
    }

	public String followMethod(String bpId, HttpSession session, HttpServletRequest request){
		long decodedBpId = -1;
		try{
			decodedBpId = IDUtil.decodeId(bpId);
		}catch (Exception e){
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
		if(decodedBpId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long currBpId = (Long) session.getAttribute(SessionConstants.USER_ID);

		logger.info("invoke Follow user <currBpId={}, bpId={}>", currBpId, decodedBpId);
		try {
		    if (eliteAdapter.followPeople(decodedBpId, currBpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime(), true)) {
		        logger.info("follow user succeed!");
		        return ResponseJSON.getSucJSON().toString();
		    } else {
		        logger.info("follow user failed!");
		    }
		} catch (Exception e) {
		    logger.error("", e);
		}

        return ResponseJSON.getErrorInternalJSON().toString();
    }

	//取消关注
	@ResponseBody
	@RequestMapping(value = {"ask/user/unfollow"}, produces = "application/json;charset=utf-8")
	public String unFollow(String bpId, HttpSession session, HttpServletRequest request){
		return unFollowMethod(bpId, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/user/unfollow"}, produces = "application/json;charset=utf-8")
	public String wxUnFollow(String bpId, HttpSession session, HttpServletRequest request){
		return unFollowMethod(bpId, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/ask/user/unfollow"}, produces = "application/json;charset=utf-8")
    public String appUnFollow(String bpId, HttpSession session, HttpServletRequest request){
        return unFollowMethod(bpId, session, request);
    }
	
	public String unFollowMethod(String bpId, HttpSession session, HttpServletRequest request){
		long decodedBpId = -1;
		try{
			decodedBpId = IDUtil.decodeId(bpId);
		}catch (Exception e){
			logger.error("", e);
		}
		if(decodedBpId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long currBpId = (Long) session.getAttribute(SessionConstants.USER_ID);

		logger.info("invoke unFollow user <currBpId={}, bpId={}>", currBpId, decodedBpId);
		
		try {
		    if (eliteAdapter.unfollowPeople(decodedBpId, currBpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime())) {
		        logger.info("unfollow user succeed!");
				feedService.removeCacheBackwardFeeds(TimeLineUtil.getComplexId(currBpId, BpType.Elite_User.getValue()));
		        return ResponseJSON.getSucJSON().toString();
		    } else {
		        logger.error("unfollow user failed!");
		    }
		} catch (Exception e) {
		    logger.info("", e);
		}

		return ResponseJSON.getErrorInternalJSON().toString();
	}

	//获取未读消息数量
	@ResponseBody
	@RequestMapping(value = {"ask/user/getMsgCount"}, produces = "application/json;charset=utf-8")
	public String getMsgCount(HttpSession session){
		return getMsgCountMethod(session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/user/getMsgCount"}, produces = "application/json;charset=utf-8")
	public String wxGetMsgCount(HttpSession session){
		return getMsgCountMethod(session);
	}
	
	public String getMsgCountMethod(HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("get no read message for user <bpId={}>", bpId);

		CodeMsgData responseData = extendServiceAdapter.getBpNoReadMessages(bpId, 0, Constants.DEFAULT_NO_READ_MSG_SIZE);
		if(ResponseConstants.OK == responseData.getCode()){
			JSONObject jsonObject = ResponseJSON.getSucJSON();
			jsonObject.put("data", responseData.getData());
			return jsonObject.toString();
		}

		return ResponseJSON.getErrorInternalJSON().toString();
	}
	
	@RequestMapping(value = {"ask/user/fans.html", "mf"})
	public String myFans(@ModelAttribute("bean")FansUserDisplayBean bean, HttpSession session, 
			HttpServletRequest request, HttpServletResponse response)
	{
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
			 return Constants.PAGE_LOGIN + "?jumpUrl=/ask/user/fans.html";
		try{
			 logger.info("get fans list of user.userId={}", new String[]{String.valueOf(bpId)});
			 
			 bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					logger.info("userId={} total fans size={}", new String[]{String.valueOf(bpId),String.valueOf(listResult.getTotal())});
				}else {
					logger.info("not found user's fans");
				}
		
				bean.setUserList(userList);
			 
	        }catch (Exception e){
	            logger.error("", e);
	            return Constants.PAGE_503;
	     }
		 
		 if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			 return "mobile/center/center-fans";
			}else if(AgentUtil.getSource(request) == AgentSource.PC){
				PageWrapperBean pageWrapper = new PageWrapperBean();
				UserInfo userInfo = null;
				if(null != bpId && bpId.longValue() > 0)
					userInfo = userInfoService.getUserInfoByBpid(bpId);
				pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
				pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
				
				BpUserUtil.fillUserLoginInfo(bean.getUser());
				
				String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_FANS);
				pageWrapper.setSidebarHtml(sideBarHtml);
				
				bean.setPageWrapper(pageWrapper);
				return "pc/center/center-fans";
			}else {
				return Constants.PAGE_404;
			}
	}
	
	@ResponseBody
	@RequestMapping(value = {"ask/user/fans.json", "mf.json"})
	public String myFansJSON(FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
		return myFansJSONMethod(bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/mf.json"})
	public String wxMyFansJSON(FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
		return myFansJSONMethod(bean, session, request);
	}
	
	public String myFansJSONMethod(FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		try{
			 logger.info("get fans list of user.userId={}", new String[]{String.valueOf(bpId)});
			 
			 bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					logger.info("userId={} total fans size={}", new String[]{String.valueOf(bpId),String.valueOf(listResult.getTotal())});
				}else {
					logger.info("not found user's fans");
				}
		
				bean.setUserList(userList);
				
				resJSON.put("data", bean);
	        }catch (Exception e){
	            logger.error("", e);
	            resJSON = ResponseJSON.getErrorInternalJSON();
	            return resJSON.toString();
	     }
		
		return resJSON.toString();
	}

	//我的问题，回答列表
	//status 问题或回答的状态 pass,reject
	//item   问题或者回答     question,answer
	@RequestMapping(value = {"ask/user/{status}/{item}.html","p{item:[aq]{1}}{status:[pr]{1}}"})
	public String myQuestionOrAnswers(@PathVariable("status") String status, @PathVariable("item") String item, 
			@ModelAttribute("bean")UserStatusItemDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		if(StringUtils.isBlank(status) || StringUtils.isBlank(item) || !("pass".equals(status) || "reject".equals(status)||"p".equals(status) || "r".equals(status)) 
				|| !("question".equals(item) || "answer".equals(item) || "q".equals(item) || "a".equals(item))){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("get " + status + " " + item + " for user, bpId=" + bpId);
		 
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
		switch(item + status)
		{
		case "ap" : status = "pass";
		item = "answer";
		break;
		case "qp" : status = "pass";
		item = "question";
		break;
		case "ar" : status = "reject";
		item = "answer";
		break;
		case "qr" : status = "reject";
		item = "question";
		break;
		default : break;
		}
		fillSimpleItemBeanList(bpId, status, item, bean);
		
		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/center/center-"+status+"-"+item;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			
			BpUserUtil.fillUserLoginInfo(bean.getUser());
			
			String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_QUESTION);
			if(item.equalsIgnoreCase("answer"))
				sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_ANSWER);
			pageWrapper.setSidebarHtml(sideBarHtml);
			
			bean.setPageWrapper(pageWrapper);
			return "pc/center/center-"+status+"-"+item;
		}else {
			return Constants.PAGE_404;
		}
	}

	//我的问题，回答列表 Json数据
	//status 问题或回答的状态 pass,reject
	//item   问题或者回答     question,answer
	@ResponseBody
	@RequestMapping(value = {"ask/user/{status}/{item}.json", "p{item:[aq]{1}}{status:[pr]{1}}.json"}, produces = "application/json;charset=utf-8")
	public String myQuestionOrAnswersJsonData(@PathVariable("status") String status, @PathVariable("item") String item, UserStatusItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return myQuestionOrAnswersJsonDataMethod(status, item, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/p{item:[aq]{1}}{status:[pr]{1}}.json"}, produces = "application/json;charset=utf-8")
	public String wxMyQuestionOrAnswersJsonData(@PathVariable("status") String status, @PathVariable("item") String item, UserStatusItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return myQuestionOrAnswersJsonDataMethod(status, item, bean, session, request);
	}
	
	public String myQuestionOrAnswersJsonDataMethod(String status, String item, UserStatusItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		if(StringUtils.isBlank(status) || StringUtils.isBlank(item) || !("pass".equals(status) || "reject".equals(status)||"p".equals(status) || "r".equals(status)) 
				|| !("question".equals(item) || "answer".equals(item) || "q".equals(item) || "a".equals(item))){
			return ResponseJSON.getErrorParamsJSON().toString();
		}
		switch(item + status)
		{
		case "ap" : status = "pass";
		item = "answer";
		break;
		case "qp" : status = "pass";
		item = "question";
		break;
		case "ar" : status = "reject";
		item = "answer";
		break;
		case "qr" : status = "reject";
		item = "question";
		break;
		default : break;
		}
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("get " + status + " " + item + " for user, bpId=" + bpId);
		
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
		fillSimpleItemBeanList(bpId, status, item, bean);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);

		return jsonObject.toString();
	}
	
	@ResponseBody
	@RequestMapping("wx/p/change/nick")
	public String changeNick(@RequestParam(value = "nick", required = true) String nick, HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
		CodeMsgData codeMsgData = serviceAdapter.updateNick(bpId, nick);
		if(ResponseConstants.OK != codeMsgData.getCode()) return ResponseJSON.getErrorParamsJSON().toString();
		return ResponseJSON.getDefaultResJSON().toString();
	}
	
	@ResponseBody
	@RequestMapping("wx/p/change/avatar")
	public String changeAvatar(@RequestParam(value = "avatar", required = true) String avatar, HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
		CodeMsgData codeMsgData = serviceAdapter.updateAvatar(bpId, avatar);
		if(ResponseConstants.OK != codeMsgData.getCode()) return ResponseJSON.getErrorParamsJSON().toString();
		return ResponseJSON.getDefaultResJSON().toString();
	}
	
	//app接口用于查看userId是否关注了objectId
	@RequestMapping(value = "app/p/hasFollowed", produces="application/json;charset=utf-8")
	@ResponseBody
	public  String hasFollowed(@RequestParam(value = "userId", required = false) String encodedUserId, @RequestParam(value = "userType", required = false) Integer userType,
	        @RequestParam(value = "objectId") String encodedObjectId, @RequestParam(value = "objectType") Integer objectType, HttpSession session) {
	    Long objectId = IDUtil.decodeId(encodedObjectId);
	    if (objectId <= 0 || objectType <= 0) return ResponseJSON.getErrorParamsJSON().toString();
	    
	    userType = BpType.Bp.getValue();
	    Long userId = null;
	    if (StringUtils.isNotBlank(encodedUserId)) {
	        userId = IDUtil.decodeId(encodedUserId);
	    } else {
	        userId = (Long) session.getAttribute(SessionConstants.USER_ID);
	    }
	    if (null == userId || userId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
	    JSONObject resJSON = ResponseJSON.getDefaultResJSON();
	    boolean followedFlag = InteractionUtil.hasFollowed(userId, userType, objectId, objectType);
	    JSONObject dataJSON = new JSONObject();
	    dataJSON.put("hasFollowed", followedFlag);
	    resJSON.put("data", dataJSON);
	    
	    return resJSON.toString();
	}
	
    //用于一般的用户收藏targetId和targetType
    @RequestMapping(value = "/p/favorite", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String favoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpServletRequest request, HttpSession session) {
        return favoriteTargetMethod(targetType, targetId, request, session);
    }
    
    @RequestMapping(value = "wx/p/favorite", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxFavoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpServletRequest request, HttpSession session) {
        return favoriteTargetMethod(targetType, targetId, request, session);
    }
    
    @RequestMapping(value = "/app/p/favorite", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appFavoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpServletRequest request, HttpSession session) {
        return favoriteTargetMethod(targetType, targetId, request, session);
    }
    
    public String favoriteTargetMethod(Integer targetType, String targetId, HttpServletRequest request, HttpSession session) {
        if (null == targetType || StringUtils.isBlank(targetId) || (targetType != BpType.Answer.getValue() && targetType != BpType.Elite_Column.getValue()
                && targetType != BpType.Elite_Subject.getValue() && targetType != BpType.Elite_Vote.getValue())) return ResponseJSON.getErrorParamsJSON().toString();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        Long id = IDUtil.decodeId(targetId);
        boolean flag = false;
        try {
            flag = eliteAdapter.favoriteTargetItem(targetType, id, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime());
        } catch (Exception e) {
            logger.info("", e);
        }
        logger.info("bpId = {} favorite targetType = {}, targetId = {}, result = {}", new Object[]{bpId, targetType, id, flag});
        if (flag) {
            //发送到推荐
            if (targetType == BpType.Answer.getValue()) {
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(RecLogHelper.createBehaviorLog(BehaviorType.COLLECT, "FUV", id, ItemType.ANSWER, bpId, request)));
            } else if (targetType == BpType.Elite_Vote.getValue()) {
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(RecLogHelper.createBehaviorLog(BehaviorType.COLLECT, "FUV", id, ItemType.QUESTION, bpId, request)));
            }
            return ResponseJSON.getSucJSON().toString();
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    @RequestMapping(value = "/p/unfavorite", produces = "application/json;charset=utf-8")
 	@ResponseBody
 	public String unfavoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpSession session, HttpServletRequest request) {
         return unfavoriteTargetMethod(targetType, targetId, session, request);
     }
     
     @RequestMapping(value = "wx/p/unfavorite", produces = "application/json;charset=utf-8")
     @ResponseBody
     public String wxUnfavoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpSession session, HttpServletRequest request) {
         return unfavoriteTargetMethod(targetType, targetId, session, request);
     }
     
     @RequestMapping(value = "/app/p/unfavorite", produces = "application/json;charset=utf-8")
     @ResponseBody
     public String appUnFavoriteTarget(@RequestParam(value = "targetType") Integer targetType, @RequestParam(value = "targetId") String targetId, HttpSession session, HttpServletRequest request) {
         return unfavoriteTargetMethod(targetType, targetId, session, request);
     }
     
     public String unfavoriteTargetMethod(Integer targetType, String targetId, HttpSession session, HttpServletRequest request) {
    	 if (null == targetType || StringUtils.isBlank(targetId) || (targetType != BpType.Answer.getValue() && targetType != BpType.Elite_Column.getValue()
                 && targetType != BpType.Elite_Subject.getValue() && targetType != BpType.Elite_Vote.getValue())) return ResponseJSON.getErrorParamsJSON().toString();
         Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
         Long id = IDUtil.decodeId(targetId);
         boolean flag = false;
         try {
             flag = eliteAdapter.unfavoriteTargetItem(targetType, id, bpId);
         } catch (Exception e) {
             logger.info("", e);
         }
         logger.info("bpId = {} unfavorite targetType = {}, targetId = {}, result = {}", new Object[]{bpId, targetType, id, flag});
         if (flag) {
             //发送到推荐
             if (targetType == BpType.Answer.getValue()) {
                 EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(RecLogHelper.createBehaviorLog(BehaviorType.UNCOLLECT, "FUV", id, ItemType.ANSWER, bpId, request)));
             } else if (targetType == BpType.Elite_Vote.getValue()) {
                 EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(RecLogHelper.createBehaviorLog(BehaviorType.UNCOLLECT, "FUV", id, ItemType.QUESTION, bpId, request)));
             }
             return ResponseJSON.getSucJSON().toString();
         }
         return ResponseJSON.getErrorInternalJSON().toString();
     }
	
	
	private List<UserAtDisplayBean> getUsersAtBean(List<Long> bpIdList) {
		List<UserAtDisplayBean> users = new ArrayList<>();
		if (null != bpIdList) {
			for (Long bpId : bpIdList) {
				UserAtDisplayBean userAtDisplayBean = new UserAtDisplayBean();
				userAtDisplayBean.setBpId(IDUtil.encodeId(bpId));
				UserDetailDisplayBean userDetailBean = bpUserService.getUserSimpleByBpId(bpId);
				userAtDisplayBean.setAvatar(userDetailBean.getAvatar());
				userAtDisplayBean.setNick(userDetailBean.getNick());
				userAtDisplayBean.setHomeUrl(userDetailBean.getHomeUrl());
				userAtDisplayBean.setIdentity(userDetailBean.getIdentity());
				userAtDisplayBean.setIdentityString(userDetailBean.getIdentityString());
				userAtDisplayBean.setDescription(userDetailBean.getDescription());
				users.add(userAtDisplayBean);
			}
		}
		return users;
	}
	//item=question 问题
	//item=answer 回答
	//status=pass published和passed状态
	//status=reject  rejected状态
	private void fillSimpleItemBeanList(long bpId, String status, String item, UserStatusItemDisplayBean bean) {
		if("question".equals(item)){
			if("pass".equals(status)){
				fillSimpleItemBeanList(bpId, 1, 1, bean);
			}else if("reject".equals(status)){
				fillSimpleItemBeanList(bpId, 1, 2, bean);
			}
		}else if("answer".equals(item)) {
			if ("pass".equals(status)) {
				fillSimpleItemBeanList(bpId, 2, 1, bean);
			} else if ("reject".equals(status)) {
				fillSimpleItemBeanList(bpId, 2, 2, bean);
			}
		}
	}

	//type=1 问题
	//type=2 回答
	//status=1 published 和 passed 状态
	//status=2 rejected 状态
	private void fillSimpleItemBeanList(long bpId, int type, int status, UserStatusItemDisplayBean bean){
		List<SimpleFeedItemBean> feedItemList = new ArrayList<>();

		if(type == 1){
			TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
			searchQuestionCondition.setBpId(bpId)
								   .setSortField("updateTime")
								   .setSortType(SortType.DESC)
					               .setFrom((bean.getCurrPageNo()-1)*bean.getPageSize())
					               .setCount(bean.getPageSize());
			if(status == 1) {
				searchQuestionCondition.setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));
			}else if(status == 2){
				searchQuestionCondition.setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.REJECTED.getValue()));
			}

			try {
				TQuestionListResult listResult = eliteAdapter.searchQuestion(searchQuestionCondition);
				bean.setTotal(listResult.getTotal());
				if(listResult.getTotal() > 0){
					feedItemList = ConvertUtil.convertQuestionListUser(listResult.getQuestions(), bpId, null);
				}
			} catch (TException e) {
				logger.error("", e);
			}
		}else if(type == 2){
			TSearchAnswerCondition searchAnswerCondition = new TSearchAnswerCondition();
			searchAnswerCondition.setBpId(bpId)
								 .setSortField("updateTime")
			                      .setSortType(SortType.DESC)
					             .setFrom((bean.getCurrPageNo()-1)*bean.getPageSize())
					             .setCount(bean.getPageSize());
			if(status == 1) {
				searchAnswerCondition.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
			}else if(status == 2){
				searchAnswerCondition.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.REJECTED.getValue()));
			}

			try {
				TAnswerListResult listResult = eliteAdapter.searchAnswer(searchAnswerCondition);
				bean.setTotal(listResult.getTotal());
				if(listResult.getTotal() > 0){
					feedItemList = ConvertUtil.convertAnswerListUser(listResult.getAnswers(), bpId, null);
				}
			} catch (TException e) {
				logger.error("", e);
			}
		}

		bean.setFeedItemList(feedItemList);
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, null));
	}

	//获取用户收藏的回答列表
	private List<TEliteAnswer> getFavoriteAnswers(Long bpId, FavoriteAnswerDisplayBean bean){
		List<TEliteAnswer> answers = new ArrayList<>();
		
		if(bean.getCurrPageNo() < 1)
			bean.setCurrPageNo(1);
		if(bean.getPageSize() <= 0)
			bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		
		int start = (bean.getCurrPageNo()-1)*bean.getPageSize();
		int stop = start + bean.getPageSize();
		
		com.alibaba.fastjson.JSONObject jsonObject = bpInteractionServiceAdapter.getFavoriteTargets(bpId, BpInteractionTargetType.ELITE_ANSWER, start, stop);
		com.alibaba.fastjson.JSONArray ids = jsonObject.getJSONArray("ids");
		long total = jsonObject.getLongValue("total");
		for(int i = 0; i < ids.size(); i++){
			TEliteAnswer answer = null;
			try {
				answer = eliteAdapter.getAnswerById(ids.getLong(i));
			} catch (TException e) {
				logger.error("", e);
			}
			if(answer != null) {
				answers.add(answer);
			}
		}

		logger.info("get favorite answer size=" + answers.size());
		bean.setTotal(total);
		
		return answers;
	}
}
