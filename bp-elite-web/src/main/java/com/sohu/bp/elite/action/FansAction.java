package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;

import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.action.bean.fans.FansQuestionDisplayBean;
import com.sohu.bp.elite.action.bean.fans.FansTagDisplayBean;
import com.sohu.bp.elite.action.bean.fans.FansUserDisplayBean;

import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.ObjectUserRelation;
import com.sohu.bp.thallo.model.ObjectUserRelationListResult;
import com.sohu.bp.thallo.model.RelationStatus;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-09-02 12:19:12
 * TODO 请忽略调FollowUserDisplayBean的名字。todo:更正为UserDisplayBean
 */
@Controller
//@RequestMapping({"fans","/"})
public class FansAction
{
	private static Logger log = LoggerFactory.getLogger(FansAction.class);
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
	private BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	
	@Resource
	private BpUserService bpUserService;
	@Resource
	private WrapperPageService wrapperPageService;
	@Resource
	private UserInfoService userInfoService;
	
//	@RequestMapping(value = "list.html")
//	public String list(@ModelAttribute("bean")FansUserDisplayBean bean, HttpSession session, HttpServletRequest request)
//	{
//		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
//		if(null == bpId || bpId.longValue() <= 0)
//			return Constants.PAGE_LOGIN + "?jumpUrl=/ask/fans/list.html";
//		log.info("get fans list of user.userId={}", new String[]{bpId.toString()});
//		
//		try
//		{
//			bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
//			
//			if(bean.getCurrPageNo() < 1)
//	        	bean.setCurrPageNo(1);
//	        if(bean.getPageSize() <= 0)
//	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
//	        
//			List<UserDetailDisplayBean> userList = new ArrayList<>();
//			
//			ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
//			if(listResult != null && listResult.getTotal() > 0){
//				bean.setTotal(listResult.getTotal());
//	
//				for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
//					userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
//				}
//				log.info("userId={} total fans size={}", new String[]{bpId.toString(),String.valueOf(listResult.getTotal())});
//			}else {
//				log.info("not found user's fans");
//			}
//	
//			bean.setUserList(userList);
//		}catch(Exception e)
//		{
//			log.error("", e);
//			return Constants.PAGE_503;
//		}
//
//		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
//			return "mobile/fans/fanslist-man";
//		}else if(AgentUtil.getSource(request) == AgentSource.PC){
//			PageWrapperBean pageWrapper = new PageWrapperBean();
//			UserInfo userInfo = null;
//			if(null != bpId && bpId.longValue() > 0)
//				userInfo = userInfoService.getUserInfoByBpid(bpId);
//			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo));
//			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
//			bean.setPageWrapper(pageWrapper);
//			return "pc/center/center-fans";
//		}else {
//			return Constants.PAGE_404;
//		}
//	}
	
	
	@RequestMapping(value = {"ask/fans/question/{questionEncodedId}/list.html", "qf/{questionEncodedId}"})
	public String questionFansList(@PathVariable("questionEncodedId") String questionEncodedId, @ModelAttribute("bean")FansQuestionDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		if(StringUtils.isBlank(questionEncodedId))
			return Constants.PAGE_404;
		 Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
			 long questionId = IDUtil.decodeId(questionEncodedId);
			 if(questionId <= 0)
				 return Constants.PAGE_404;
			 
			 log.info("get fans list of question.questionId={}", new String[]{String.valueOf(questionId)});
			 
			 bean.setQuestion(ConvertUtil.convert(eliteAdapter.getQuestionById(questionId), bpId, AgentUtil.getFeedSourceType(request)));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(questionId, BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					log.info("questionId={} total fans size={}", new String[]{String.valueOf(questionId),String.valueOf(listResult.getTotal())});
				}else {
					log.info("not found question's fans");
				}
		
				bean.setUserList(userList);
			 
	        }catch (Exception e){
	            log.error("", e);
	            return Constants.PAGE_503;
	     }
		 
		 if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
				return "mobile/fans/fanslist-question";
			}else if(AgentUtil.getSource(request) == AgentSource.PC){
				PageWrapperBean pageWrapper = new PageWrapperBean();
				UserInfo userInfo = null;
				if(null != bpId && bpId.longValue() > 0)
					userInfo = userInfoService.getUserInfoByBpid(bpId);
				pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
				pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
				bean.setPageWrapper(pageWrapper);
				return "pc/fans/fanslist-question";
			}else {
				return Constants.PAGE_404;
			}
		
	}
	
	@ResponseBody
	@RequestMapping(value = {"ask/fans/question/{questionEncodedId}/list.json", "qf/{questionEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String questionFansListJSON(@PathVariable("questionEncodedId") String questionEncodedId, FansQuestionDisplayBean bean, HttpSession session, HttpServletRequest request){
		return questionFansListJSONMethod(questionEncodedId, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/qf/{questionEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String wxQuestionFansListJSON(@PathVariable("questionEncodedId") String questionEncodedId, FansQuestionDisplayBean bean, HttpSession session, HttpServletRequest request){
		return questionFansListJSONMethod(questionEncodedId, bean, session, request);
	}
	
    @ResponseBody
    @RequestMapping(value = {"app/qf/{questionEncodedId}.json"}, produces = "application/json;charset=utf-8")
    public String appQuestionFansListJSON(@PathVariable("questionEncodedId") String questionEncodedId, FansQuestionDisplayBean bean, HttpSession session, HttpServletRequest request){
        return questionFansListJSONMethod(questionEncodedId, bean, session, request);
    }
	
	public String questionFansListJSONMethod(String questionEncodedId, FansQuestionDisplayBean bean, HttpSession session, HttpServletRequest request)
	{
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(StringUtils.isBlank(questionEncodedId))
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		 Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
				 long questionId = IDUtil.decodeId(questionEncodedId);
				 if(questionId <= 0)
				 {
					 resJSON = ResponseJSON.getErrorParamsJSON();
					 return resJSON.toString();
				 }
				 
				 log.info("get fans list of question.questionId={}", new String[]{String.valueOf(questionId)});
				 
				 bean.setQuestion(ConvertUtil.convert(eliteAdapter.getQuestionById(questionId), bpId, AgentUtil.getFeedSourceType(request)));
				 
				 if(bean.getCurrPageNo() < 1)
			        	bean.setCurrPageNo(1);
			        if(bean.getPageSize() <= 0)
			        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
			        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
			        
					List<UserDetailDisplayBean> userList = new ArrayList<>();
					
					ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(questionId, BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
					if(listResult != null && listResult.getTotal() > 0){
						bean.setTotal(listResult.getTotal());
			
						for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
							userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
						}
						log.info("questionId={} total fans size={}", new String[]{String.valueOf(questionId),String.valueOf(listResult.getTotal())});
					}else {
						log.info("not found question's fans");
					}
			
				  bean.setUserList(userList);
					
				  resJSON.put("data", bean);
	        }catch (Exception e){
	            log.error("", e);
	            resJSON = ResponseJSON.getErrorInternalJSON();
	            return resJSON.toString();
	        }
		 
		 return resJSON.toString();
		
	}
	
	@RequestMapping(value = "ask/fans/answer/{answerEncodedId}/list.html")
	public String answerFansList() {
		return Constants.PAGE_404;
	}
	
	@ResponseBody
	@RequestMapping(value = "ask/fans/answer/{answerEncodedId}/list.json", produces = "application/json;charset=utf-8")
	public String answerFansJSON() {
		return ResponseJSON.getErrorParamsJSON().toString();
	}
	
	@RequestMapping(value = {"ask/fans/tag/{tagEncodedId}/list.html", "tf/{tagEncodedId}"})
	public String tagFansList(@PathVariable("tagEncodedId") String tagEncodedId, @ModelAttribute("bean")FansTagDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		if(StringUtils.isBlank(tagEncodedId))
			return Constants.PAGE_404;
		 Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
			 int tagId = (int)IDUtil.decodeId(tagEncodedId);
			 if(tagId <= 0)
				 return Constants.PAGE_404;
			 
			 log.info("get fans list of tag.tagId={}", new String[]{String.valueOf(tagId)});
			 
			
			 bean.setTag(ConvertUtil.convertToTagItemBean(bpDecorationServiceAdapter.getTagById(tagId), bpId, true));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(tagId, BpType.Tag.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					log.info("tagId={} total fans size={}", new String[]{String.valueOf(tagId),String.valueOf(listResult.getTotal())});
				}else {
					log.info("not found tag's fans");
				}
		
				bean.setUserList(userList);
			 
	        }catch (Exception e){
	            log.error("", e);
	            return Constants.PAGE_503;
	     }
		 
		 if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
				return "mobile/fans/fanslist-tag";
			}else if(AgentUtil.getSource(request) == AgentSource.PC){
				PageWrapperBean pageWrapper = new PageWrapperBean();
				UserInfo userInfo = null;
				if(null != bpId && bpId.longValue() > 0)
					userInfo = userInfoService.getUserInfoByBpid(bpId);
				pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
				pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
				bean.setPageWrapper(pageWrapper);
				return "pc/fans/fanslist-tag";
			}else {
				return Constants.PAGE_404;
			}
	}
	
	@ResponseBody
	@RequestMapping(value = {"ask/fans/tag/{tagEncodedId}/list.json", "tf/{tagEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String tagFansListJSON(@PathVariable("tagEncodedId") String tagEncodedId, FansTagDisplayBean bean, HttpSession session, HttpServletRequest request){
		return tagFanListJSONMethod(tagEncodedId, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/tf/{tagEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String wxTagFansListJSON(@PathVariable("tagEncodedId") String tagEncodedId, FansTagDisplayBean bean, HttpSession session, HttpServletRequest request){
		return tagFanListJSONMethod(tagEncodedId, bean, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/tf/{tagEncodedId}.json"}, produces = "application/json;charset=utf-8")
    public String appTagFansListJSON(@PathVariable("tagEncodedId") String tagEncodedId, FansTagDisplayBean bean, HttpSession session, HttpServletRequest request){
        return tagFanListJSONMethod(tagEncodedId, bean, session, request);
    }
	
	public String tagFanListJSONMethod(String tagEncodedId, FansTagDisplayBean bean, HttpSession session, HttpServletRequest request)
	{
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(StringUtils.isBlank(tagEncodedId))
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		 Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
			 int tagId = (int)IDUtil.decodeId(tagEncodedId);
			 if(tagId <= 0)
			 {
					resJSON = ResponseJSON.getErrorParamsJSON();
					return resJSON.toString();
			 }
			 
			 log.info("get fans list of tag.tagId={}", new String[]{String.valueOf(tagId)});
			 
			
			 bean.setTag(ConvertUtil.convertToTagItemBean(bpDecorationServiceAdapter.getTagById(tagId), bpId, true));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(tagId, BpType.Tag.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					log.info("tagId={} total fans size={}", new String[]{String.valueOf(tagId),String.valueOf(listResult.getTotal())});
				}else {
					log.info("not found tag's fans");
				}
		
				bean.setUserList(userList);
				resJSON.put("data", bean);
	        }catch (Exception e){
	            log.error("", e);
	            resJSON = ResponseJSON.getErrorInternalJSON();
	            return resJSON.toString();
	     }
		 
		 return resJSON.toString();
	}
	
	@RequestMapping(value = {"ask/fans/user/{userEncodedId}/list.html", "uf/{userEncodedId}"})
	public String userFansList(@PathVariable("userEncodedId") String userEncodedId, @ModelAttribute("bean")FansUserDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		if(StringUtils.isBlank(userEncodedId))
			return Constants.PAGE_404;
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
			 long userId = (int)IDUtil.decodeId(userEncodedId);
			 if(userId <= 0)
				 return Constants.PAGE_404;
			 
			 log.info("get fans list of user.userId={}", new String[]{String.valueOf(userId)});
			 
			 bean.setUser(bpUserService.getUserDetailByBpId(userId, bpId));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(userId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					log.info("userId={} total fans size={}", new String[]{String.valueOf(userId),String.valueOf(listResult.getTotal())});
				}else {
					log.info("not found user's fans");
				}
		
				bean.setUserList(userList);
			 
	        }catch (Exception e){
	            log.error("", e);
	            return Constants.PAGE_503;
	     }
		 
		 if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
				return "mobile/fans/fanslist-man";
			}else if(AgentUtil.getSource(request) == AgentSource.PC){
				PageWrapperBean pageWrapper = new PageWrapperBean();
				UserInfo userInfo = null;
				if(null != bpId && bpId.longValue() > 0)
					userInfo = userInfoService.getUserInfoByBpid(bpId);
				pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
				pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
				bean.setPageWrapper(pageWrapper);
				return "pc/fans/fanslist-man";
			}else {
				return Constants.PAGE_404;
			}
	}
	
	@ResponseBody
	@RequestMapping(value = {"ask/fans/user/{userEncodedId}/list.json", "uf/{userEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String userFansListJSON(@PathVariable("userEncodedId") String userEncodedId, FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
		return userFansListJSONMethod(userEncodedId, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/uf/{userEncodedId}.json"}, produces = "application/json;charset=utf-8")
	public String wxUserFansListJSON(@PathVariable("userEncodedId") String userEncodedId, FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
		return userFansListJSONMethod(userEncodedId, bean, session, request);
	}
	
    @ResponseBody
    @RequestMapping(value = {"app/uf/{userEncodedId}.json"}, produces = "application/json;charset=utf-8")
    public String appUserFansListJSON(@PathVariable("userEncodedId") String userEncodedId, FansUserDisplayBean bean, HttpSession session, HttpServletRequest request){
        return userFansListJSONMethod(userEncodedId, bean, session, request);
    }
	
	public String userFansListJSONMethod(String userEncodedId, FansUserDisplayBean bean, HttpSession session, HttpServletRequest request)
	{
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		if(StringUtils.isBlank(userEncodedId))
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		 try{
			 long userId = (int)IDUtil.decodeId(userEncodedId);
			 if(userId <= 0)
			 {
				 resJSON = ResponseJSON.getErrorParamsJSON();
				 return resJSON.toString();
			 }
			 
			 log.info("get fans list of user.userId={}", new String[]{String.valueOf(userId)});
			 
			 bean.setUser(bpUserService.getUserDetailByBpId(userId, bpId));
			 
			 if(bean.getCurrPageNo() < 1)
		        	bean.setCurrPageNo(1);
		        if(bean.getPageSize() <= 0)
		        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
		        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
		        
				List<UserDetailDisplayBean> userList = new ArrayList<>();
				
				ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(userId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
				if(listResult != null && listResult.getTotal() > 0){
					bean.setTotal(listResult.getTotal());
		
					for(ObjectUserRelation relation : listResult.getObjectUserRelationList()){
						userList.add(bpUserService.getUserDetailByBpId(relation.getUserId(), bpId));
					}
					log.info("userId={} total fans size={}", new String[]{String.valueOf(userId),String.valueOf(listResult.getTotal())});
				}else {
					log.info("not found user's fans");
				}
		
				bean.setUserList(userList);
				
				resJSON.put("data", bean);
	        }catch (Exception e){
	            log.error("", e);
	            resJSON = ResponseJSON.getErrorInternalJSON();
				return resJSON.toString();
	     }
		 return resJSON.toString();
	}
}