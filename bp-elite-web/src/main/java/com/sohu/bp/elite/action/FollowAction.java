package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.follow.FollowTagDisplayBean;
import com.sohu.bp.elite.action.bean.follow.FollowUserDisplayBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionListDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.SideBarPageType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.task.EliteParallelPool;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.BpUserUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.RelationStatus;
import com.sohu.bp.thallo.model.UserObjectRelationListResult;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;


/**
 * 
 * @author nicholastang
 * 2016-08-12 11:48:32
 * TODO “我关注的xxx“逻辑
 */
@Controller
//@RequestMapping({"follow", "/"})
public class FollowAction
{
	private static Logger log = LoggerFactory.getLogger(FollowAction.class);

	private BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();

	@Resource
	private BpUserService bpUserService;
	@Resource
	private WrapperPageService wrapperPageService;
	@Resource
	private UserInfoService userInfoService;

	//我关注的tag列表
	@RequestMapping(value = {"ask/follow/tag.html","ct"})
	public String followTagList(@ModelAttribute("bean")FollowTagDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow tags for user<bpId={}>", bpId);
		
		fillMyFollowTags(bean, bpId);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/attention/attention-label";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());

			BpUserUtil.fillUserLoginInfo(bean.getUser());
			
			String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_FOLLOW);
			pageWrapper.setSidebarHtml(sideBarHtml);
			bean.setPageWrapper(pageWrapper);
			return "pc/attention/center-attenlabel";
		}else {
			return Constants.PAGE_404;
		}
	}

	//获取我关注tag json数据
	@ResponseBody
	@RequestMapping(value = {"ask/follow/tag.json","ct.json"}, produces = "application/json;charset=utf-8")
	public String getFollowTagJsonData(FollowTagDisplayBean bean, HttpSession session) {
		return getFollowTagJsonDataMethod(bean, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ct.json"}, produces = "application/json;charset=utf-8")
	public String wxGetFollowTagJsonData(FollowTagDisplayBean bean, HttpSession session) {
		return getFollowTagJsonDataMethod(bean, session);
	}
	
    @ResponseBody
    @RequestMapping(value = {"app/ct.json"}, produces = "application/json;charset=utf-8")
    public String appGetFollowTagJsonData(FollowTagDisplayBean bean, HttpSession session) {
        return getFollowTagJsonDataMethod(bean, session);
    }
	
	public String getFollowTagJsonDataMethod(FollowTagDisplayBean bean, HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow tags json data for user<bpId={}>", bpId);

		fillMyFollowTags(bean, bpId);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);
		return jsonObject.toString();
	}

	//我关注的用户列表
	@RequestMapping(value = {"ask/follow/person.html", "cu"})
	public String followPersonList(@ModelAttribute("bean")FollowUserDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow persons for user<bpId={}>", bpId);
		
		fillMyFollowUsers(bean, bpId);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/attention/attention-man";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());

			BpUserUtil.fillUserLoginInfo(bean.getUser());
			
			String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_FOLLOW);
			pageWrapper.setSidebarHtml(sideBarHtml);
			bean.setPageWrapper(pageWrapper);
			return "pc/attention/center-attenman";
		}else {
			return Constants.PAGE_404;
		}
	}

	//获取我关注用户json数据
	@ResponseBody
	@RequestMapping(value = {"ask/follow/person.json", "cu.json"}, produces = "application/json;charset=utf-8")
	public String getFollowPersonJsonData(FollowUserDisplayBean bean, HttpSession session) {
		return getFollowPersonJsonDataMethod(bean, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/cu.json"}, produces = "application/json;charset=utf-8")
	public String wxGetFollowPersonJsonData(FollowUserDisplayBean bean, HttpSession session) {
		return getFollowPersonJsonDataMethod(bean, session);
	}
	
    @ResponseBody
    @RequestMapping(value = {"app/cu.json"}, produces = "application/json;charset=utf-8")
    public String appGetFollowPersonJsonData(FollowUserDisplayBean bean, HttpSession session) {
        return getFollowPersonJsonDataMethod(bean, session);
    }
	
	public String getFollowPersonJsonDataMethod(FollowUserDisplayBean bean, HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow persons json data for user<bpId={}>", bpId);

		fillMyFollowUsers(bean, bpId);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);
		return jsonObject.toString();
	}

	//我关注的问题列表
	@RequestMapping(value = {"ask/follow/question.html", "cq"})
	public String followQuestionList(@ModelAttribute("bean") EliteQuestionListDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow questions for user<bpId={}>", bpId);
		
		fillMyFollowQuestions(bean, bpId);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/attention/attention-question";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
					
			BpUserUtil.fillUserLoginInfo(bean.getUser());
			
			String sideBarHtml = wrapperPageService.getSidebarHtml(bean.getUser(), SideBarPageType.MY_FOLLOW);
			pageWrapper.setSidebarHtml(sideBarHtml);
			bean.setPageWrapper(pageWrapper);
			return "pc/attention/center-attenques";
		}else {
			return Constants.PAGE_404;
		}
	}

	//获取我关注问题json数据
	@ResponseBody
	@RequestMapping(value = {"ask/follow/question.json", "cq.json"}, produces = "application/json;charset=utf-8")
	public String getFollowQuestionJsonData(EliteQuestionListDisplayBean bean, HttpSession session) {
		return getFollowQuestionJsonDataMethod(bean, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/cq.json"}, produces = "application/json;charset=utf-8")
	public String wxGetFollowQuestionJsonData(EliteQuestionListDisplayBean bean, HttpSession session) {
		return getFollowQuestionJsonDataMethod(bean, session);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/cq.json"}, produces = "application/json;charset=utf-8")
    public String appGetFollowQuestionJsonData(EliteQuestionListDisplayBean bean, HttpSession session) {
        return getFollowQuestionJsonDataMethod(bean, session);
    }
	
	public String getFollowQuestionJsonDataMethod(EliteQuestionListDisplayBean bean, HttpSession session){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		log.info("get follow questions json data for user<bpId={}>", bpId);

		fillMyFollowQuestions(bean, bpId);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);
		return jsonObject.toString();
	}

	private void fillMyFollowQuestions(EliteQuestionListDisplayBean bean, Long bpId){
		List<TEliteQuestion> questionList = new ArrayList<>();
		try {
			int from = (bean.getCurrPageNo()-1)*bean.getPageSize();
			UserObjectRelationListResult relationListResult = thalloServiceAdapter.getActiveListByObjectType(bpId, BpType.Elite_User.getValue(), BpType.Question.getValue(), RelationStatus.FOLLOW, from, bean.getPageSize());
			if (relationListResult != null && relationListResult.getTotal() > 0) {
				bean.setTotal(relationListResult.getTotal());

//				for (UserObjectRelation relation : relationListResult.getUserObjectRelationList()) {
//					questionList.add(eliteAdapter.getQuestionById(relation.getObjectId()));
//				}
				relationListResult.getUserObjectRelationList().parallelStream().map(relation -> {
				    TEliteQuestion question = null;
				    try {
				        question = eliteAdapter.getQuestionById(relation.getObjectId());
				    } catch (Exception e) {
				        log.error("", e);
				    }
				    return question;
				}).filter(Objects::nonNull).forEachOrdered(questionList::add);
				log.info("total follow questions=" + relationListResult.getTotal());
			}else {
				log.info("follow questions not found");
			}
		} catch (Exception e) {
			log.error("", e);
		}
		bean.setQuestionList(ConvertUtil.convertQuestionList(questionList, bpId, null));
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
	}

	private void fillMyFollowUsers(FollowUserDisplayBean bean, Long bpId){
		List<UserDetailDisplayBean> userList = new ArrayList<>();
		try {
			UserObjectRelationListResult listResult = thalloServiceAdapter.getActiveListByObjectType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, (bean.getCurrPageNo()-1)*bean.getPageSize(), bean.getPageSize());
			if(listResult != null && listResult.getTotal() > 0){
				bean.setTotal(listResult.getTotal());

//				for(UserObjectRelation relation : listResult.getUserObjectRelationList()){
//					userList.add(bpUserService.getUserDetailByBpId(relation.getObjectId(), bpId));
//				}
				EliteParallelPool.getForkJoinPool().submit(() -> {
    				listResult.getUserObjectRelationList().parallelStream().map(relation -> {
    				    return bpUserService.getUserDetailByBpId(relation.getObjectId(), bpId);
    				}).forEachOrdered(userList::add);
				}).get();
				log.info("total follow persons=" + listResult.getTotal());
			}else {
				log.info("follow persons not found");
			}

		}catch (Exception e){
			log.error("", e);
		}
		bean.setUserList(userList);
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, null));
	}

	private void fillMyFollowTags(FollowTagDisplayBean bean, Long bpId){
		List<TagItemBean> tagList = new ArrayList<>();
		try {
			UserObjectRelationListResult relationListResult = thalloServiceAdapter.getActiveListByObjectType(bpId, BpType.Elite_User.getValue(), BpType.Tag.getValue(), RelationStatus.FOLLOW, (bean.getCurrPageNo()-1)*bean.getPageSize(), bean.getPageSize());
			if (relationListResult != null && relationListResult.getTotal() > 0) {
				bean.setTotal(relationListResult.getTotal());
//				for (UserObjectRelation relation : relationListResult.getUserObjectRelationList()) {
//					tagList.add(ConvertUtil.convertToTagItemBean(decorationServiceAdapter.getTagById((int) relation.getObjectId()), bpId));
//				}
				EliteParallelPool.getForkJoinPool().submit(() -> {
    				relationListResult.getUserObjectRelationList().parallelStream().map(relation -> {
    				    Tag tag = null;
    				    try{
    				        tag = decorationServiceAdapter.getTagById((int) relation.getObjectId());
    				    } catch (Exception e) {
    				        log.error("", e);
    				    }
    				    return ConvertUtil.convertToTagItemBean(tag, bpId, true);
    				}).filter(Objects::nonNull).forEachOrdered(tagList::add);
				}).get();
				log.info("total follow tags=" + relationListResult.getTotal());
			} else {
				log.info("follow tags not found");
			}
		}catch (Exception e){
			log.error("", e);
		}
		bean.setTagList(tagList);
		bean.setUser(bpUserService.getUserDetailByBpId(bpId, bpId));
	}
}