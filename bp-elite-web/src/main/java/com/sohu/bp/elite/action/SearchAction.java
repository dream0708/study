package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.SearchTagCondition;
import com.sohu.bp.decoration.model.TagListResult;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchAnswerDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchExpertDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchGlobalDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchQuestionDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchTagDisplayBean;
import com.sohu.bp.elite.action.bean.search.SearchUserDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchGlobalCondition;
import com.sohu.bp.elite.model.TSearchGlobalListResult;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TUserIdListResult;
import com.sohu.bp.elite.model.TUserListResult;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.InviteService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.task.EliteParallelPool;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteResponseJSON;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.SearchUtil;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/23
 */
@Controller
public class SearchAction {

    private Logger log = LoggerFactory.getLogger(SearchAction.class);

    private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private BpDecorationServiceAdapter decorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    
    @Resource
	private WrapperPageService wrapperPageService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
	private InviteService inviteService;
    @Resource
    private BpUserService bpUserService;

    //移动端搜索页
    @RequestMapping(value = "ask/search/index.html")
    public String showSearchPage(@ModelAttribute("bean")PageWrapperBean bean, HttpServletRequest request, HttpServletResponse response){
    	if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
    		return "mobile/search/search-home";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
            return "redirect:ask/search/global.html";
        }else {
            return Constants.PAGE_404;
        }
        
    }

    //PC端全局搜索页
    @RequestMapping(value = {"ask/search/global.html", "s"})
    public String showSearchGlobalResult(@ModelAttribute("bean")SearchGlobalDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    	
    	if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
     		return "mobile/search/search-home";
         }
    	log.info("invoke global search <keywords={}>", bean.getKeywords());
        if(StringUtils.isBlank(bean.getKeywords())){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchGlobalResult(bean, bean.getKeywords(), bpId);
        
        if(AgentUtil.getSource(request) == AgentSource.PC){
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = null;
			if(null != bpId && bpId > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return "pc/search/search-all";
        }else {
            return Constants.PAGE_404;
        }
    }

    //全局搜索
    @ResponseBody
    @RequestMapping(value = {"ask/search/global", "s.json"}, produces = "application/json;charset=utf-8")
    public String searchGlobal(String keywords, HttpSession session) {
    	return searchGlobalMethod(keywords, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/s.json"}, produces = "application/json;charset=utf-8")
    public String wxSearchGlobal(String keywords, HttpSession session) {
    	return searchGlobalMethod(keywords, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/s.json"}, produces = "application/json;charset=utf-8")
    public String appSearchGlobal(String keywords, HttpSession session) {
        return searchGlobalMethod(keywords, session);
    }
    
    public String searchGlobalMethod(String keywords, HttpSession session){
        log.info("invoke global search <keywords={}>", keywords);
        if(StringUtils.isBlank(keywords)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);

        SearchGlobalDisplayBean bean = SearchGlobalDisplayBean.getEmptyDisplayBean();
        fillSearchGlobalResult(bean, keywords, bpId);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    //PC端问题搜索
    @RequestMapping(value = {"ask/search/question.html", "sq"})
    public String searchQuestion(@ModelAttribute("bean")SearchQuestionDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        log.info("invoke question search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchQuestionResult(bean, bpId);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
    		return "redirect:/search/index.html";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = null;
			if(null != bpId && bpId > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return "pc/search/search-question";
        }else {
            return Constants.PAGE_404;
        }
    }
    
    //搜索问题
    @ResponseBody
    @RequestMapping(value = {"ask/search/question", "sq.json"}, produces = "application/json;charset=utf-8")
    public String searchQuestion(SearchQuestionDisplayBean bean, HttpSession session){
    	return searchQuestionMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/sq.json"}, produces = "application/json;charset=utf-8")
    public String wxSearchQuestion(SearchQuestionDisplayBean bean, HttpSession session){
    	return searchQuestionMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/sq.json"}, produces = "application/json;charset=utf-8")
    public String appSearchQuestion(SearchQuestionDisplayBean bean, HttpSession session){
        return searchQuestionMethod(bean, session);
    }
    
    public String searchQuestionMethod(SearchQuestionDisplayBean bean, HttpSession session){
        log.info("invoke question search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchQuestionResult(bean, bpId);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    //PC端回答搜索
    @RequestMapping(value = {"ask/search/answer.html", "sa"})
    public String searchAnswer(@ModelAttribute("bean")SearchAnswerDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        log.info("invoke answer search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchAnswerResult(bean, bpId);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
    		return "redirect:/search/index.html";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = null;
			if(null != bpId && bpId > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return "pc/search/search-answer";
        }else {
            return Constants.PAGE_404;
        }
    }

    
    //搜索回答
    @ResponseBody
    @RequestMapping(value = {"ask/search/answer", "sa.json"}, produces = "application/json;charset=utf-8")
    public String searchAnswer(SearchAnswerDisplayBean bean, HttpSession session){
    	return searchAnswerMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/sa.json"}, produces = "application/json;charset=utf-8")
    public String wsSearchAnswer(SearchAnswerDisplayBean bean, HttpSession session){
    	return searchAnswerMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/sa.json"}, produces = "application/json;charset=utf-8")
    public String appSearchAnswer(SearchAnswerDisplayBean bean, HttpSession session){
        return searchAnswerMethod(bean, session);
    }
    
    public String searchAnswerMethod(SearchAnswerDisplayBean bean, HttpSession session){
        log.info("invoke answer search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchAnswerResult(bean, bpId);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    //PC端搜索用户
    @RequestMapping(value = {"ask/search/user.html","su"})
    public String searchUser(@ModelAttribute("bean")SearchUserDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        log.info("invoke user search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchUserResult(bean, bpId);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
    		return "redirect:/search/index.html";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = null;
			if(null != bpId && bpId > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return "pc/search/search-user";
        }else {
            return Constants.PAGE_404;
        }
    }
    
    //搜索用户
    @ResponseBody
    @RequestMapping(value = {"ask/search/user", "su.json"}, produces = "application/json;charset=utf-8")
    public String searchUser(SearchUserDisplayBean bean, HttpSession session){
    	return searchUserMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/su.json"}, produces = "application/json;charset=utf-8")
    public String wxSearchUser(SearchUserDisplayBean bean, HttpSession session){
    	return searchUserMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/su.json"}, produces = "application/json;charset=utf-8")
    public String appSearchUser(SearchUserDisplayBean bean, HttpSession session){
        return searchUserMethod(bean, session);
    }
    
    public String searchUserMethod(SearchUserDisplayBean bean, HttpSession session){
        log.info("invoke user search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchUserResult(bean, bpId);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    //PC端搜索tag
    @RequestMapping(value = {"ask/search/tag.html", "st"})
    public String searchTag(@ModelAttribute("bean")SearchTagDisplayBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        log.info("invoke tag search <keywords={}, currPageNo={}, pageSize={}>", 
        		new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchTagResult(bean, bpId);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
    		return "redirect:/search/index.html";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = null;
			if(null != bpId && bpId > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return "pc/search/search-label";
        }else {
            return Constants.PAGE_404;
        }
    }
    
    //搜索tag
    @ResponseBody
    @RequestMapping(value = {"ask/search/tag", "st.json"}, produces = "application/json;charset=utf-8")
    public String searchTag(SearchTagDisplayBean bean, HttpSession session){
    	return searchTagMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/st.json"}, produces = "application/json;charset=utf-8")
    public String wxSearchTag(SearchTagDisplayBean bean, HttpSession session){
    	return searchTagMethod(bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/st.json"}, produces = "application/json;charset=utf-8")
    public String appSearchTag(SearchTagDisplayBean bean, HttpSession session){
        return searchTagMethod(bean, session);
    }
    
    public String searchTagMethod(SearchTagDisplayBean bean, HttpSession session){
        log.info("invoke tag search <keywords={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchTagResult(bean, bpId);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    //搜索专家
    @RequestMapping(value = "app/se.json", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appSearchExpert(SearchExpertDisplayBean bean, HttpSession session) {
        return searchExpertMethod(bean, session);
    }
    
    private String searchExpertMethod(SearchExpertDisplayBean bean, HttpSession session) {
        log.info("invoke expert search <tagId={}, currPageNo={}, pageSize={}>", new Object[]{bean.getTagId(), bean.getCurrPageNo(), bean.getPageSize()});
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        fillSearchExpertResult(bean, bpId);
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);
        return jsonObject.toString();
    }

    private void fillSearchGlobalResult(SearchGlobalDisplayBean bean, String keywords, Long bpId){
        TSearchGlobalCondition condition = new TSearchGlobalCondition();
        condition.setKeywords(keywords)
                .setFrom(0)
                .setCount(Constants.DEFAULT_SEARCH_COUNT);

        TSearchGlobalListResult listResult = null;
        Map<String, Integer> totalCounts = null;
        try {
            listResult = eliteThriftServiceAdapter.searchGlobal(condition);
            totalCounts = listResult.getTotalCounts();
        } catch (TException e) {
            log.error("", e);
        }
        if(listResult == null){
            totalCounts = new HashMap<>();
            totalCounts.put("question", 0);
            totalCounts.put("answer", 0);
            totalCounts.put("user", 0);
            listResult = new TSearchGlobalListResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), totalCounts);
        }

        bean.setQuestionList(ConvertUtil.convertQuestionList(listResult.getQuestions(), bpId, null));
        bean.setAnswerList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, null));
        List<UserDetailDisplayBean> users = new ArrayList<>();
        listResult.getUsers().forEach(u -> users.add(ConvertUtil.convert(u, bpId)));
        bean.setUserList(users);

        //单独搜索tag
        SearchTagCondition searchTagCondition = new SearchTagCondition();
        searchTagCondition.setKeywords(condition.getKeywords())
                    .setFrom(condition.getFrom())
                    .setCount(condition.getCount())
                    .setTypes(new ArrayList<TagType>(){{
                        add(TagType.ELITE_TAG);
                        add(TagType.ELITE_LOCATION_TAG);
                    }})
                    .setStatus(TagStatus.WORK);

        TagListResult tagListResult = null;
        try {
            tagListResult = decorationServiceAdapter.searchTag(searchTagCondition);
        } catch (TException e) {
            log.error("", e);
        }
        if(tagListResult == null){
            tagListResult = new TagListResult(new ArrayList<>(), 0);
        }

        if (tagListResult.getTotal() > 0) {
            bean.setTagList(ConvertUtil.convertToTagItemBeanList(tagListResult.getTags(), bpId, true));
            totalCounts.put("tag", (int)tagListResult.getTotal());
        }else {
            totalCounts.put("tag", 0);
        }

        bean.setTotalCounts(totalCounts);
    }

    private void fillSearchQuestionResult(SearchQuestionDisplayBean bean, Long bpId){
        TSearchQuestionCondition condition = new TSearchQuestionCondition();
        condition.setFrom((bean.getCurrPageNo() - 1)*bean.getPageSize())
                .setCount(bean.getPageSize())
                .setKeywords(bean.getKeywords())
                .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

        TQuestionListResult listResult = null;
        try {
            listResult = eliteThriftServiceAdapter.searchQuestion(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if(listResult == null){
            listResult = new TQuestionListResult(new ArrayList<>(), 0);
        }

        bean.setTotal(listResult.getTotal());
        bean.setQuestionList(ConvertUtil.convertQuestionList(listResult.getQuestions(), bpId, null));
    }

    private void fillSearchAnswerResult(SearchAnswerDisplayBean bean, Long bpId){
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize())
                .setCount(bean.getPageSize())
                .setKeywords(bean.getKeywords())
                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

        TAnswerListResult listResult = null;
        try {
            listResult = eliteThriftServiceAdapter.searchAnswer(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if(listResult == null){
            listResult = new TAnswerListResult(new ArrayList<>(), 0);
        }

        bean.setTotal(listResult.getTotal());
        bean.setAnswerList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, null));
    }

    private void fillSearchTagResult(SearchTagDisplayBean bean, Long bpId){
        SearchTagCondition condition = new SearchTagCondition();
        condition.setKeywords(bean.getKeywords())
                .setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize())
                .setCount(bean.getPageSize())
                .setTypes(new ArrayList<TagType>(){{
                    add(TagType.ELITE_TAG);
                    add(TagType.ELITE_LOCATION_TAG);
                }})
                .setStatus(TagStatus.WORK);

        TagListResult tagListResult = null;
        try {
            tagListResult = decorationServiceAdapter.searchTag(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if(tagListResult == null){
            tagListResult = new TagListResult(new ArrayList<>(), 0);
        }

        bean.setTotal(tagListResult.getTotal());
        bean.setTagList(ConvertUtil.convertToTagItemBeanList(tagListResult.getTags(), bpId, true));
    }

    private void fillSearchUserResult(SearchUserDisplayBean bean, Long bpId){
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setKeywords(bean.getKeywords())
                .setFrom((bean.getCurrPageNo() - 1)*bean.getPageSize())
                .setCount(bean.getPageSize());

        TUserListResult listResult = null;
        try {
            listResult = eliteThriftServiceAdapter.searchUser(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if(listResult == null){
            listResult = new TUserListResult(new ArrayList<>(), 0);
        }

        List<UserDetailDisplayBean> users = new ArrayList<>();
//        listResult.getUsers().forEach(u -> users.add(ConvertUtil.convert(u, bpId))); 
        final TUserListResult tempListResult = listResult;
        try {
            EliteParallelPool.getForkJoinPool().submit(() -> {
                tempListResult.getUsers().parallelStream().map(user -> {
                    return ConvertUtil.convert(user, bpId);
                }).forEachOrdered(users::add);
            }).get();
        } catch (Exception e) {
            log.error("", e);
        }
        bean.setUserList(users);
        bean.setTotal(listResult.getTotal());
    }
    
    private void fillSearchExpertResult(SearchExpertDisplayBean bean, Long bpId) {
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setStatus(EliteUserStatus.VALID.getValue()).setIdentity(EliteUserIdentity.EXPERT.getValue())
        .setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize()).setCount(bean.getPageSize());
        if (null != bean.getTagId() && bean.getTagId() > 0) {
            condition.setTagIds(bean.getTagId().toString());
        }
        TUserIdListResult listResult = null;
        try {
            listResult = eliteThriftServiceAdapter.searchUserId(condition);
        } catch (Exception e) {
            log.error("", e);
        }
        if(listResult == null){
            listResult = new TUserIdListResult(new ArrayList<>(), 0);
        }
        List<UserDetailDisplayBean> users = new ArrayList<>();
        final TUserIdListResult tempListResult = listResult;
//        listResult.getUserIds().forEach(userId -> users.add(bpUserService.getUserDetailByBpId(userId, bpId, true))); 
        try {
            EliteParallelPool.getForkJoinPool().submit(() -> {
                tempListResult.getUserIds().parallelStream().map(userId -> {
                    return bpUserService.getUserDetailByBpId(userId, bpId, true);
                }).forEachOrdered(users::add);
            }).get();
        } catch (Exception e) {
            log.error("", e);
        }
        bean.setUserList(users);
        bean.setTotal(listResult.getTotal());
    }
    
    //邀请搜索用户
    @ResponseBody
    @RequestMapping(value = {"ask/search/invite/user"}, produces = "application/json;charset=utf-8")
    public String searchInviteUser(@RequestParam(value = "questionId", required = true) String encodedQuestionId,SearchUserDisplayBean bean, HttpSession session){
    	return searchInviteUserMethod(encodedQuestionId, bean, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/search/invite/user"}, produces = "application/json;charset=utf-8")
    public String wxSearchInviteUser(@RequestParam(value = "questionId", required = true) String encodedQuestionId,SearchUserDisplayBean bean, HttpSession session){
    	return searchInviteUserMethod(encodedQuestionId, bean, session);
    }
    
    public String searchInviteUserMethod(String encodedQuestionId, SearchUserDisplayBean bean, HttpSession session){
        log.info("invoke invite user search <keywords={}, encodedQuestionId={}, currPageNo={}, pageSize={}>", new String[]{bean.getKeywords(), encodedQuestionId, String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
        if(StringUtils.isBlank(bean.getKeywords())){
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        Long questionId = IDUtil.decodeId(encodedQuestionId);
        if(null == questionId || questionId <= 0) return ResponseJSON.getErrorInternalJSON().toString();
        //提取手机号
        String[] result = SearchUtil.searchByTelNum(bean.getKeywords());
        bean.setKeywords(result[0]);
        String mobile = result[1];
        try{
	        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
	        if(null == bpId || bpId <= 0 ) return EliteResponseJSON.getNotLoginError().toString();
	        fillInviteSearchUserResult(bean, bpId, questionId, mobile);
        } catch (Exception e){
        	log.error("", e);
        }

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);

        return jsonObject.toString();
    }
    
    private void fillInviteSearchUserResult(SearchUserDisplayBean bean, Long inviteId, Long questionId, String mobile){
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setKeywords(bean.getKeywords())
                .setFrom((bean.getCurrPageNo() - 1)*bean.getPageSize())
                .setCount(bean.getPageSize());

        TUserListResult listResult = null;
        try {
            listResult = eliteThriftServiceAdapter.searchUser(condition);
        } catch (TException e) {
            log.error("", e);
        }
        if(listResult == null){
            listResult = new TUserListResult(new ArrayList<>(), 0);
        }

        List<UserDetailDisplayBean> users = new ArrayList<>();
        listResult.getUsers().forEach(u -> users.add(ConvertUtil.convert(u, inviteId))); 
        
        //邀请用户结果, 根据手机号来进行判断
        List<UserDetailDisplayBean> filterUsers = new ArrayList<>();
        for(UserDetailDisplayBean user : users){
        	Long invitedId = IDUtil.decodeId(user.getBpId());
        	if(StringUtils.isNotBlank(mobile)){
        		Long bpId = userInfoService.getBpidByMobile(mobile);
        		if (!invitedId.equals(bpId)) continue;
        	}
        	TEliteUser userElite = null;
        try{
        	userElite = eliteThriftServiceAdapter.getUserByBpId(invitedId);
        } catch(Exception e){
        	continue;
        }
        	user.setDescription(userElite.getDescription());
    		List<Long> idUserList = inviteService.getUserInviteList(questionId, inviteId);
    		user.setInvited(idUserList.contains(invitedId));
    		filterUsers.add(user);
        }
        bean.setUserList(filterUsers);
        bean.setTotal(listResult.getTotal());
    }
    
}

