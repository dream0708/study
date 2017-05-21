package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpAreaMapServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.AreaMap;
import com.sohu.bp.decoration.model.DecorationCompany;
import com.sohu.bp.decoration.model.Expert;
import com.sohu.bp.decoration.model.ExpertRole;
import com.sohu.bp.elite.action.bean.home.CompanyHomeDisplayBean;
import com.sohu.bp.elite.action.bean.home.DesignerHomeDisplayBean;
import com.sohu.bp.elite.action.bean.home.ForemanHomeDisplayBean;
import com.sohu.bp.elite.action.bean.home.SelfHomeDisplayBean;
import com.sohu.bp.elite.action.bean.home.UserHomeDisplayBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * 
 * @author nicholastang
 * 2016-09-06 17:22:46
 * TODO 返回用户个人页
 */
@Controller
//@RequestMapping("")
public class UserHomeAction
{
	private static Logger logger = LoggerFactory.getLogger(UserHomeAction.class);
	
	private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	private BpAreaMapServiceAdapter areaMapServiceAdapter = BpDecorationServiceAdapterFactory.getBpAreaMapServiceAdapter();
	private TimelineService timelineService = AchelousTimeline.getService();
	@Resource
    private BpUserService bpUserService;
    @Resource
	private WrapperPageService wrapperPageService;
    @Resource
    private UserInfoService userInfoService;
	
    /**
     * “我”的问答个人页
     * @param bean
     * @param session
     * @param request
     * @return
     */
	@RequestMapping(value={"ask/user/home.html","pu"})
    public String myHome(@ModelAttribute("bean") UserHomeDisplayBean bean, HttpSession session, 
    		HttpServletRequest request, HttpServletResponse response){
    	Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_LOGIN + "?jumpUrl=/ask/user/home.html";
		
		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, bpId));
//	    	
//	    	if(null != feedList && feedList.size() > 0)
//	    	{
//	    		oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//	    	}
	    	bean.setOldestTime(oldestTime);
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/person-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-home";
		}else {
			return Constants.PAGE_404;
		}
    }
	
	@ResponseBody
	@RequestMapping(value={"ask/user/home.json","pu.json"})
    public String myHomeJSON(UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		
    	Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, bpId, null));
	    	
	    	if(null != feedList && feedList.size() > 0)
	    	{
	    		oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
	    	}
	    	bean.setOldestTime(oldestTime);
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
    }
	
	@RequestMapping(value={"ask/user/question/home.html", "puq"})
	public String myQuestionHome(@ModelAttribute("bean") UserHomeDisplayBean bean, HttpSession session, 
			HttpServletRequest request, HttpServletResponse response){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_LOGIN + "?jumpUrl=/ask/user/question/home.html";
		
		try
		{
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), bpId, null));
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pu";
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-question-home";
		}else {
			return Constants.PAGE_404;
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"ask/user/question/home.json", "puq.json"})
	public String myQuestionHomeJSON(UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		try
		{
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), bpId, null));
            
            resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
	}
	
	@RequestMapping(value={"ask/user/answer/home.html", "pua"})
	public String myAnswerHome(@ModelAttribute("bean") UserHomeDisplayBean bean, HttpSession session, 
			HttpServletRequest request, HttpServletResponse response){
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_LOGIN + "?jumpUrl=/ask/user/answer/home.html";
		
		try
		{
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, null));
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pu";
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-answer-home";
		}else {
			return Constants.PAGE_404;
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"ask/user/answer/home.json","pua.json"})
	public String myAnswerHomeJSON(UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		try
		{
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, bpId);
	    	bean.setUser(userBean);
	    	
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, null));
            
            resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
	}
    
	/**
	 * 
	 * @param userIdEncoded
	 * @param bean
	 * @param session
	 * @param request
	 * @return
	 */
    @RequestMapping(value={"ask/user/{userIdEncoded}/home.html", "pu/{userIdEncoded}"})
    public String userHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") UserHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response){
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
//			if(null != feedList && feedList.size() > 0)
//			{
//				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//			}
			bean.setOldestTime(oldestTime);
	    	logger.info("total num="+bean.getUser().getWorkTotalNumHuman());
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/person-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/user/{userIdEncoded}/home.json", "pu/{userIdEncoded}.json"})
    public String userHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
    }
    
    //用于给app的用户下feed流
    @RequestMapping(value={"app/pu/{userIdEncoded}.json"}, produces="application/json;charset=utf-8")
    @ResponseBody
    public String getAppUserFeedItems(@PathVariable("userIdEncoded") String userIdEncoded, @RequestParam(value = "oldestTime", required = false) Long oldestTime, HttpSession session) {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if (StringUtils.isBlank(userIdEncoded)) {
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if (null == bpId || bpId.longValue() <= 0) {
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		if (null == oldestTime || oldestTime <= 0) oldestTime = new Date().getTime();
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try {
		    JSONObject dataJSON = new JSONObject();
	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	dataJSON.put("feedItemList", ConvertUtil.convertFeedList(feedList, viewerId, TEliteSourceType.WRITE_APP));
			if(null != feedList && feedList.size() > 0) {
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			dataJSON.put("oldestTime", oldestTime);
	    	resJSON.put("data", dataJSON);
		} catch(Exception e) {
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/user/{userIdEncoded}/question/home.html", "puq/{userIdEncoded}"})
    public String userQuestionHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") UserHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response){
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pu/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-question-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/user/{userIdEncoded}/question/home.json", "puq/{userIdEncoded}.json"})
    public String userQuestionHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
            
            resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/user/{userIdEncoded}/answer/home.html", "pua/{userIdEncoded}"})
    public String userAnswerHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") UserHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response){
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
		}catch(Exception e) {
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pu/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/person-answer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/user/{userIdEncoded}/answer/home.json", "pua/{userIdEncoded}.json"})
    public String userAnswserHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(bpId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
            resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/designer/{userIdEncoded}/home.html","pd/{userIdEncoded}"})
    public String designerHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") DesignerHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(designerId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
//			if(null != feedList && feedList.size() > 0)
//			{
//				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl(OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl(OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl(OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/designer-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/designer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/designer/{userIdEncoded}/home.json","pd/{userIdEncoded}.json"})
    public String designerHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, DesignerHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getDefaultResJSON();
    		return resJSON.toString();
    	}
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
				bean.setOldestTime(oldestTime);
			}
	    	long complexId = TimeLineUtil.getComplexId(designerId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/designer/{userIdEncoded}/question/home.html","pdq/{userIdEncoded}"})
    public String designerQuestionHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") DesignerHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(designerId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
            
            //设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pd/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo,false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/designer-question-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/designer/{userIdEncoded}/question/home.json", "pdq/{userIdEncoded}.json"})
    public String designerQuestionHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, DesignerHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(designerId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
            
            //设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	
            resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/designer/{userIdEncoded}/answer/home.html", "pda/{userIdEncoded}"})
    public String designerAnswerHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") DesignerHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(designerId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
            
            //设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pd/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/designer-answer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/designer/{userIdEncoded}/answer/home.json", "pda/{userIdEncoded}.json"})
    public String designerAnswerHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, DesignerHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long designerId = IDUtil.decodeId(userIdEncoded);
		if(null == designerId || designerId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(designerId, viewerId);
	    	
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(designerId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(designerId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
            //设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/designer/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(designerId, ExpertRole.DESIGNER);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/home.html", "pf/{userIdEncoded}"})
    public String foremanHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") ForemanHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(foremanId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
//			if(null != feedList && feedList.size() > 0)
//			{
//				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/foreman-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(foremanId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/foreman-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/home.json", "pf/{userIdEncoded}.json"})
    public String foremanHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, ForemanHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorInternalJSON();
			return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(foremanId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/question/home.html", "pfq/{userIdEncoded}"})
    public String foremanQuestionHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") ForemanHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(foremanId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pf/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(foremanId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/foreman-question-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/question/home.json", "pfq/{userIdEncoded}.json"})
    public String foremanQuestionHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, ForemanHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(foremanId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/answer/home.html", "pfa/{userIdEncoded}"})
    public String foremanAnswerHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") ForemanHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(foremanId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pf/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(foremanId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/foreman-answer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/foreman/{userIdEncoded}/answer/home.json", "pfa/{userIdEncoded}.json"})
    public String foremanAnswerHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, ForemanHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long foremanId = IDUtil.decodeId(userIdEncoded);
		if(null == foremanId || foremanId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(foremanId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(foremanId, ExpertRole.DESIGNER));
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(foremanId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/description/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/design/" + userBean.getBpIdOrigin() + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/foreman/comment/" + userBean.getBpIdOrigin() + ".html");
	    	//获取所属公司
	    	Expert expert = decorationServiceAdapter.getExpertByIdRole(foremanId, ExpertRole.FOREMAN);
	    	if(null != expert && expert.getCompany() != null)
	    	{
	    		bean.setCompany(ConvertUtil.convertCompany(expert.getCompany()));
	    	}
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/self/{userIdEncoded}/home.html", "ps/{userIdEncoded}"})
    public String selfHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") SelfHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(selfId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
//			if(null != feedList && feedList.size() > 0)
//			{
//				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/self-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(selfId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/self-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/self/{userIdEncoded}/home.json", "ps/{userIdEncoded}.json"})
    public String selfHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, SelfHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(selfId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/self/{userIdEncoded}/question/home.html", "psq/{userIdEncoded}"})
    public String selfQuestionHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") SelfHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(selfId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/ps/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(selfId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/self-question-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/self/{userIdEncoded}/question/home.json", "psq/{userIdEncoded}.json"})
    public String selfQuestionHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, SelfHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(selfId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/self/{userIdEncoded}/answer/home.html", "psa/{userIdEncoded}"})
    public String selfAnswerHome(@PathVariable("userIdEncoded") String userIdEncoded, @ModelAttribute("bean") SelfHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(userIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(selfId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/ps/"+userIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(selfId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/self-answer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/self/{userIdEncoded}/answer/home.json", "psa/{userIdEncoded}.json"})
    public String selfAnswerHomeJSON(@PathVariable("userIdEncoded") String userIdEncoded, SelfHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long selfId = IDUtil.decodeId(userIdEncoded);
		if(null == selfId || selfId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(selfId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(selfId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	//设置tab跳转链接
	    	bean.setSelfUrl("//"+OverallDataFilter.mainDomain+"/decoration/self/media/" + userBean.getBpIdOrigin() + ".html");
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/company/{companyIdEncoded}/home.html", "pc/{companyIdEncoded}"})
    public String companyHome(@PathVariable("companyIdEncoded") String companyIdEncoded, @ModelAttribute("bean") CompanyHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(companyIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
				return Constants.PAGE_404;
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
//	    	long complexId = TimeLineUtil.getComplexId(expertId, BpType.Elite_User.getValue());
//	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
//	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
//			if(null != feedList && feedList.size() > 0)
//			{
//				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
//			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/design/" + IDUtil.encodeIdOrigin(companyId)+ ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/instance/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setDesignerUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/designer/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setForemanUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/foreman/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/comment/" + IDUtil.encodeIdOrigin(companyId) + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "mobile/home/company-home";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/company-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/company/{companyIdEncoded}/home.json", "pc/{companyIdEncoded}.json"})
    public String companyHomeJSON(@PathVariable("companyIdEncoded") String companyIdEncoded, CompanyHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(companyIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
			{
	    		resJSON = ResponseJSON.getErrorParamsJSON();
	    		return resJSON.toString();
	    	}
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(expertId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	//设置tab跳转链接
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/company/{companyIdEncoded}/question/home.html", "pcq/{companyIdEncoded}"})
    public String companyQuestionHome(@PathVariable("companyIdEncoded") String companyIdEncoded, @ModelAttribute("bean") CompanyHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(companyIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
				return Constants.PAGE_404;
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(expertId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	//设置tab跳转链接
            bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/" + IDUtil.encodeIdOrigin(companyId)+ ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/design/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/instance/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setDesignerUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/designer/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setForemanUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/foreman/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/comment/" + IDUtil.encodeIdOrigin(companyId) + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pc/"+companyIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/company-question-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/company/{companyIdEncoded}/question/home.json", "pcq/{companyIdEncoded}.json"})
    public String companyQuestionHomeJSON(@PathVariable("companyIdEncoded") String companyIdEncoded, CompanyHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(companyIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
			{
	    		resJSON = ResponseJSON.getErrorParamsJSON();
	    		return resJSON.toString();
	    	}
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchQuestionCondition condition = new TSearchQuestionCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(expertId)
	                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

	        TQuestionListResult listResult;
	        listResult = eliteThriftServiceAdapter.searchQuestion(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), viewerId, null));
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    @RequestMapping(value={"ask/company/{companyIdEncoded}/answer/home.html", "pca/{companyIdEncoded}"})
    public String companyAnswerHome(@PathVariable("companyIdEncoded") String companyIdEncoded, @ModelAttribute("bean") CompanyHomeDisplayBean bean, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
    	if(StringUtils.isBlank(companyIdEncoded))
    		return Constants.PAGE_404;
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
			return Constants.PAGE_INDEX;
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
				return Constants.PAGE_404;
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(expertId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	//设置tab跳转链接
            bean.setDescHomeUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setDesignUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/design/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setInstanceUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/instance/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setDesignerUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/designer/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setForemanUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/foreman/" + IDUtil.encodeIdOrigin(companyId) + ".html");
	    	bean.setCommentUrl("//"+OverallDataFilter.mainDomain+"/decoration/company/comment/" + IDUtil.encodeIdOrigin(companyId) + ".html");
		}catch(Exception e)
		{
			logger.error("", e);
			return Constants.PAGE_503;
		}

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			return "redirect:/pc/"+companyIdEncoded;
			//return Constants.PAGE_INDEX;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != viewerId && viewerId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(viewerId);
			pageWrapper.setToolbarHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			return "pc/home/company-answer-home";
		}else {
			return Constants.PAGE_404;
		}
    }
    
    @ResponseBody
    @RequestMapping(value={"ask/company/{companyIdEncoded}/answer/home.json", "pca/{companyIdEncoded}.json"})
    public String companyAnswerHomeJSON(@PathVariable("companyIdEncoded") String companyIdEncoded, CompanyHomeDisplayBean bean, HttpSession session, HttpServletRequest request)
    {
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(companyIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long companyId = IDUtil.decodeId(companyIdEncoded);
		if(null == companyId || companyId.longValue() <= 0)
		{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);
		try
		{
			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(companyId.intValue());
			if(null == decorationCompany || decorationCompany.getExpertId() <= 0)
			{
	    		resJSON = ResponseJSON.getErrorParamsJSON();
	    		return resJSON.toString();
	    	}
			
			Long expertId = decorationCompany.getExpertId();
			
			UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(expertId, viewerId);
	    	//获取服务地区
	    	userBean.setServiceAreaList(this.getUserServiceArea(companyId, ExpertRole.NONE));
	    	bean.setUser(userBean);
	    	bean.setCompany(ConvertUtil.convertCompany(decorationCompany));
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	if(bean.getCurrPageNo() < 1)
	        	bean.setCurrPageNo(1);
	        if(bean.getPageSize() <= 0)
	        	bean.setPageSize(Constants.DEFAULT_PAGE_SIZE);
	        int from = (bean.getCurrPageNo() - 1)*bean.getPageSize();
	        
	        TSearchAnswerCondition condition = new TSearchAnswerCondition();
	        condition.setFrom(from)
	                .setCount(bean.getPageSize())
	                .setSortField("updateTime")
	                .setSortType(SortType.DESC)
	                .setBpId(expertId)
	                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

	        TAnswerListResult listResult;
	        
	        listResult = eliteThriftServiceAdapter.searchAnswer(condition);
            
            bean.setTotal(listResult.getTotal());
            bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, null));
	    	
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}

		return resJSON.toString();
    }
    
    /**
     * 获取服务地区列表
     * @param bpId
     * @param role
     * @return
     */
    public List<String> getUserServiceArea(Long bpId, ExpertRole role)
    {
    	List<String> serviceAreaList = new ArrayList<String>();
    	try
    	{
    		String serviceAreaCodes = "";
    		if(role == ExpertRole.NONE && bpId.longValue() > 0)
    		{
    			//在这种情况下bpId实际上是公司的id
    			DecorationCompany decorationCompany = decorationServiceAdapter.getDecorationCompanyById(bpId.intValue());
    			if(null != decorationCompany)
    				serviceAreaCodes = decorationCompany.getServiceArea();
    				
    		}
    		else
    		{
	    		Expert expert = decorationServiceAdapter.getExpertByIdRole(bpId, role);
	    		serviceAreaCodes = expert.getServiceArea();
    		}
    		
    		if(StringUtils.isNotBlank(serviceAreaCodes))
    		{
    			String[] serviceAreaCodeArray = serviceAreaCodes.split(Constants.TAG_IDS_SEPARATOR);
    			for(String serviceAreaCodeStr : serviceAreaCodeArray)
    			{
    				Long serviceAreaCode = Long.parseLong(serviceAreaCodeStr);
    				AreaMap areaMap = areaMapServiceAdapter.getAreaByCode(serviceAreaCode);
    				if(null != areaMap && StringUtils.isNotBlank(areaMap.getName()))
    					serviceAreaList.add(areaMap.getName());
    			}
    		}
    	}catch(Exception e)
    	{
    		logger.error("", e);
    	}
    	return serviceAreaList;
    }
    
    //微信的个人页，只有动态，没有提问或者回答
    @ResponseBody
	@RequestMapping(value = {"wx/pu/{userIdEncoded}.json"}, produces = "application/json;charset=utf-8")
    public String wxUserHomeJson(@PathVariable("userIdEncoded") String userIdEncoded, UserHomeDisplayBean bean, HttpSession session, HttpServletRequest request){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	if(StringUtils.isBlank(userIdEncoded))
    	{
    		resJSON = ResponseJSON.getErrorParamsJSON();
    		return resJSON.toString();
    	}
    	
    	Long bpId = IDUtil.decodeId(userIdEncoded);
		if(null == bpId || bpId.longValue() <= 0)
		{
			resJSON = ResponseJSON.getErrorParamsJSON();
			return resJSON.toString();
		}
		
		Long viewerId = (Long) session.getAttribute(SessionConstants.USER_ID);

		try
		{
	    	UserDetailDisplayBean userBean = bpUserService.getUserDetailByBpId(bpId, viewerId);
	    	bean.setUser(userBean);
	    	if(StringUtils.isBlank(userBean.getNick()) && StringUtils.isBlank(userBean.getAvatar())) return Constants.PAGE_404;
	    	long oldestTime = bean.getOldestTime();
	    	if(oldestTime <= 0) {
				oldestTime = System.currentTimeMillis();
			}
	    	long complexId = TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue());
	    	List<Feed> feedList = timelineService.queue(complexId, new Date(oldestTime-1), null);
	    	bean.setFeedItemList(ConvertUtil.convertFeedList(feedList, viewerId, null));
			if(null != feedList && feedList.size() > 0)
			{
				oldestTime = feedList.get(feedList.size() - 1).getTime().getTime();
			}
			bean.setOldestTime(oldestTime);
	    	resJSON.put("data", bean);
		}catch(Exception e)
		{
			logger.error("", e);
			resJSON = ResponseJSON.getErrorInternalJSON();
		}
		
		return resJSON.toString();
		}
}