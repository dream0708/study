package com.sohu.bp.elite.action;

import com.sohu.achelous.model.Feed;
import com.sohu.achelous.timeline.AchelousTimeline;
import com.sohu.achelous.timeline.service.TimelineService;
import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.*;
import com.sohu.bp.elite.BeanManagerFacade;
import com.sohu.bp.elite.Configuration;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.tag.LabelPlazaDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.action.bean.tag.TagRelatedItemDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.elite.service.web.FeedService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.*;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.enums.ActionType;
import com.sohu.bp.thallo.model.RelationAction;
import com.sohu.bp.util.ResponseJSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sohu.bp.utils.RealIpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 
 * @author nicholastang
 * 2016-08-11 17:57:53
 * TODO 标签相关的后端处理action
 */
@Controller
//@RequestMapping("/tag")
public class TagAction
{
	private static Logger logger = LoggerFactory.getLogger(TagAction.class);

	private EliteThriftServiceAdapter eliteThriftServiceAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpDecorationServiceAdapter bpDecorationServiceAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	private TimelineService timlineService = AchelousTimeline.getService();
	private BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();

	@Resource
	private WrapperPageService wrapperPageService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private EliteCacheService eliteCacheService;
	@Resource
	private FeedService feedService;

	//PC端 搜索tag相关问题
	@RequestMapping(value = {"ask/tag/{tagId}/questions.html", "tq/{tagId}"})
	public String getTagQuestions(@PathVariable("tagId") String tagId, @ModelAttribute("bean") TagRelatedItemDisplayBean bean, HttpSession session, 
			HttpServletRequest request, ModelMap model, HttpServletResponse response){
		logger.info("invoke question search for tag, tagId=" + tagId);
		if(StringUtils.isBlank(tagId)){
			return Constants.PAGE_404;
		}
		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return Constants.PAGE_404;
		}
		if(decodedTagId == -1){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		fillTagRelatedQuestions(bean, decodedTagId, bpId, null);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			//model.put("bean", bean);
			//return "redirect:/ask/tag/" + tagId + "/index.html";
			return "redirect:/t/"+tagId;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, bean.getTag(), false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			model.put("bean", bean);
			return "pc/tag/label-plaza-question";
		}else {
			return Constants.PAGE_404;
		}
	}

	//PC端 搜索tag相关问题 json数据
	@ResponseBody
	@RequestMapping(value = {"ask/tag/{tagId}/questions", "tq/{tagId}.json"}, produces = "application/json;charset=utf-8")
	public String getTagQuestionsJsonData(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getTagQuestionsJsonDataMethod(tagId, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/tq/{tagId}.json"}, produces = "application/json;charset=utf-8")
	public String wxGetTagQuestionsJsonData(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getTagQuestionsJsonDataMethod(tagId, bean, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/tq/{tagId}.json"}, produces = "application/json;charset=utf-8")
    public String appGetTagQuestionsJsonData(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
        return getTagQuestionsJsonDataMethod(tagId, bean, session, request);
    }
	
	public String getTagQuestionsJsonDataMethod(String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		logger.info("invoke question search for tag <tagId={}, currPageNo={}, pageSize={}>", new String[]{tagId, String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
		if(StringUtils.isBlank(tagId)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return Constants.PAGE_404;
		}
		if(decodedTagId == -1){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TEliteSourceType source = AgentUtil.getFeedSourceType(request);
		fillTagRelatedQuestions(bean, decodedTagId, bpId, source);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);

		return jsonObject.toString();
	}

	//PC端 搜索tag相关回答
	@RequestMapping(value = {"ask/tag/{tagId}/answers.html", "ta/{tagId}"})
	public String searchAnswer(@PathVariable("tagId") String tagId, @ModelAttribute("bean") TagRelatedItemDisplayBean bean, 
			HttpSession session, HttpServletRequest request, ModelMap model, HttpServletResponse response){
		logger.info("invoke answer search for tag, tagId=" + tagId);
		if(StringUtils.isBlank(tagId)){
			return Constants.PAGE_404;
		}

		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return Constants.PAGE_404;
		}
		if(decodedTagId == -1){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		fillTagRelatedAnswers(bean, decodedTagId, bpId, null);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
//			model.put("bean", bean);
//			return "redirect:/ask/tag/" + tagId + "/index.html";
			return "redirect:/t/"+tagId;
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = null;
			if(null != bpId && bpId.longValue() > 0)
				userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, bean.getTag(), false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			model.put("bean", bean);
			return "pc/tag/label-plaza-answer";
		}else {
			return Constants.PAGE_404;
		}
	}

	//PC端 搜索tag相关回答 json数据
	@ResponseBody
	@RequestMapping(value = {"ask/tag/{tagId}/answers","ta/{tagId}.json"}, produces = "application/json;charset=utf-8")
	public String searchAnswer(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return searchAnswerMethod(tagId, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ta/{tagId}.json"}, produces = "application/json;charset=utf-8")
	public String wxSearchAnswer(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		return searchAnswerMethod(tagId, bean, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/ta/{tagId}.json"}, produces = "application/json;charset=utf-8")
    public String appSearchAnswer(@PathVariable("tagId") String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
        return searchAnswerMethod(tagId, bean, session, request);
    }
	
	public String searchAnswerMethod(String tagId, TagRelatedItemDisplayBean bean, HttpSession session, HttpServletRequest request){
		logger.info("invoke answer search for tag <tagId={}, currPageNo={}, pageSize={}>", new String[]{tagId, String.valueOf(bean.getCurrPageNo()), String.valueOf(bean.getPageSize())});
		if(StringUtils.isBlank(tagId)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return Constants.PAGE_404;
		}
		if(decodedTagId == -1){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TEliteSourceType source = AgentUtil.getFeedSourceType(request);
		fillTagRelatedAnswers(bean, decodedTagId, bpId, source);

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);

		return jsonObject.toString();
	}

	//关注
	@ResponseBody
	@RequestMapping(value = {"ask/tag/follow"}, produces = "application/json;charset=utf-8")
	public String follow(String tagId, HttpServletRequest request, HttpSession session){
		return followMethod(tagId, request, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/tag/follow"}, produces = "application/json;charset=utf-8")
	public String wxFollow(String tagId, HttpServletRequest request, HttpSession session){
		return followMethod(tagId, request, session);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/ask/tag/follow"}, produces = "application/json;charset=utf-8")
    public String appFollow(String tagId, HttpServletRequest request, HttpSession session){
        return followMethod(tagId, request, session);
    }
	
	public String followMethod(String tagId, HttpServletRequest request, HttpSession session){
		if(StringUtils.isBlank(tagId)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
		if(decodedTagId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("invoke Follow tag <bpId={}, tagId={}>", bpId, decodedTagId);

		RelationAction relationAction = new RelationAction();
		relationAction.setUserId(bpId)
				.setUserType(BpType.Elite_User.getValue())
				.setType(ActionType.TYPE_FOLLOW.getValue())
				.setObjectId(decodedTagId)
				.setObjectType(BpType.Tag.getValue())
				.setActTime(System.currentTimeMillis())
				.setActIp(RequestUtil.getClientIPLong(request))
				.setActPort(RequestUtil.getClientPort(request));
		try {
			boolean result = thalloServiceAdapter.doFollow(relationAction);
			if(result) {
				logger.info("Follow succeeded");
				return ResponseJSON.getSucJSON().toString();
			}
		} catch (TException e) {
			logger.error("", e);
		}

		return ResponseJSON.getErrorInternalJSON().toString();

	}

	//取消关注
	@ResponseBody
	@RequestMapping(value = {"ask/tag/unfollow"}, produces = "application/json;charset=utf-8")
	public String unFollow(String tagId, HttpServletRequest request, HttpSession session){
		return unFollowMethod(tagId, request, session);
	}
	
	@ResponseBody
	@RequestMapping(value = {"wx/ask/tag/unfollow"}, produces = "application/json;charset=utf-8")
	public String wxUnFollow(String tagId, HttpServletRequest request, HttpSession session){
		return unFollowMethod(tagId, request, session);
	}
	
	@ResponseBody
    @RequestMapping(value = {"app/ask/tag/unfollow"}, produces = "application/json;charset=utf-8")
    public String appUnFollow(String tagId, HttpServletRequest request, HttpSession session){
        return unFollowMethod(tagId, request, session);
    }
	
	public String unFollowMethod(String tagId, HttpServletRequest request, HttpSession session){
		if(StringUtils.isBlank(tagId)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedTagId = -1;
		try{
			decodedTagId = IDUtil.decodeId(tagId);
		}catch (Exception e){
			logger.error("", e);
			return ResponseJSON.getErrorInternalJSON().toString();
		}
		if(decodedTagId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		logger.info("invoke unFollow tag <bpId={}, tagId={}>", bpId, decodedTagId);

		RelationAction relationAction = new RelationAction();
		relationAction.setUserId(bpId)
				.setUserType(BpType.Elite_User.getValue())
				.setType(ActionType.TYPE_UNFOLLOW.getValue())
				.setObjectId(decodedTagId)
				.setObjectType(BpType.Tag.getValue())
				.setActTime(System.currentTimeMillis())
				.setActIp(RequestUtil.getClientIPLong(request))
				.setActPort(RequestUtil.getClientPort(request));
		try {
			boolean result = thalloServiceAdapter.doUnFollow(relationAction);
			if(result) {
				logger.info("unFollow succeeded");
				feedService.removeCacheBackwardFeeds(TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue()));
				return ResponseJSON.getSucJSON().toString();
			}
		} catch (TException e) {
			logger.error("", e);
		}

		return ResponseJSON.getErrorInternalJSON().toString();

	}


	//标签广场首页
	@RequestMapping(value={"ask/tag/{tagIdEncoded}/index.html", "t/{tagIdEncoded}"}, method=RequestMethod.GET)
	public String list(@PathVariable("tagIdEncoded") String tagIdEncoded, @ModelAttribute("bean") LabelPlazaDisplayBean bean, 
			HttpServletRequest request, HttpSession session, ModelMap model, HttpServletResponse response)
	{
		if(StringUtils.isBlank(tagIdEncoded)){
			return Constants.PAGE_404;
		}

		long decodedTagId = -1;
		Tag tag = null;
		try {
			decodedTagId = IDUtil.decodeId(tagIdEncoded);
			tag = bpDecorationServiceAdapter.getTagById((int)decodedTagId);
		} catch (TException e) {
			logger.error("", e);
			return Constants.PAGE_404;
		}
		if(tag == null){
			return Constants.PAGE_404;
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TagItemBean tagItemBean = ConvertUtil.convertToTagItemBean(tag, bpId, true);

		bean.setTag(tagItemBean);

		if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
			model.put("bean", bean);
			return "mobile/tag/label-plaza";
		}else if(AgentUtil.getSource(request) == AgentSource.PC){
			PageWrapperBean pageWrapper = new PageWrapperBean();
			UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, bean.getTag(), false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
			model.put("bean", bean);
			return "pc/tag/label-plaza";
		}else {
			return Constants.PAGE_404;
		}
	}

	//标签广场首页,获取json格式feed流
	@ResponseBody
	@RequestMapping(value={"ask/tag/{tagIdEncoded}/index.json", "t/{tagIdEncoded}.json"}, produces = "application/json;charset=utf-8")
	public String getTagFeedsJsonData(@PathVariable String tagIdEncoded, LabelPlazaDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getTagFeedsJsonDataMethod(tagIdEncoded, bean, session, request);
	}
	
	@ResponseBody
	@RequestMapping(value={"wx/t/{tagIdEncoded}.json"}, produces = "application/json;charset=utf-8")
	public String wxGetTagFeedsJsonData(@PathVariable String tagIdEncoded, LabelPlazaDisplayBean bean, HttpSession session, HttpServletRequest request){
		return getTagFeedsJsonDataMethod(tagIdEncoded, bean, session, request);
	}
	
	@ResponseBody
    @RequestMapping(value={"app/t/{tagIdEncoded}.json"}, produces = "application/json;charset=utf-8")
    public String appGetTagFeedsJsonData(@PathVariable String tagIdEncoded, LabelPlazaDisplayBean bean, HttpSession session, HttpServletRequest request){
        return getTagFeedsJsonDataMethod(tagIdEncoded, bean, session, request);
    }
	
	public String getTagFeedsJsonDataMethod(String tagIdEncoded, LabelPlazaDisplayBean bean, HttpSession session, HttpServletRequest request){
		if(StringUtils.isBlank(tagIdEncoded)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		long decodedTagId = IDUtil.decodeId(tagIdEncoded);
		if(decodedTagId == -1){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		TEliteSourceType source = AgentUtil.getFeedSourceType(request);

		try {
			//填充tag数据
			Integer tagId = (int)decodedTagId;
			Tag tag = bpDecorationServiceAdapter.getTagById(tagId);
			if(null == tag || tag.getId() <= 0)
				return ResponseJSON.getErrorParamsJSON().toString();

			TagItemBean tagItemBean = ConvertUtil.convertToTagItemBean(tag, bpId, true);

			//填充tag的feed流
			long oldestTime = bean.getOldestTime();
			if(oldestTime <= 0)
				oldestTime = System.currentTimeMillis();

			long accountId = TimeLineUtil.getComplexId(tagId, BpType.Tag.getValue());
			List<Feed> feeds = timlineService.queue(accountId, new Date(oldestTime-1), null);
			if (null != feeds && feeds.size() > 0) {
			    oldestTime = feeds.get(feeds.size() - 1).getTime().getTime();
			}
			List<SimpleFeedItemBean> feedItemList = ConvertUtil.convertFeedList(feeds, bpId, source);

			//在标签广场下，将produceText的文本从关注的#tag#直接变为新增
			if(null != feedItemList && feedItemList.size() > 0){
				feedItemList.forEach(item -> item.getProduceBean().setProduceText(ProduceUtil.tranProduceText(item.getProduceBean().getProduceText(), BpType.Tag)));
			}

			bean.setTag(tagItemBean);
			bean.setFeedItemList(feedItemList);
			bean.setOldestTime(oldestTime);
		}catch(Exception e)
		{
			logger.error("", e);
		}

		JSONObject jsonObject = ResponseJSON.getSucJSON();
		jsonObject.put("data", bean);
		return jsonObject.toString();
	}

	@ResponseBody
	@RequestMapping(value = "ask/tag/extract", produces = "application/json;charset=utf-8")
	public String extractTagsFromTxt(String content, final HttpServletRequest request){
		String ip = RealIpUtil.getRealIP(request);
		logger.info("[CLIENT_IP]"+ip);
		return extractTagsFromTxtMethod(content, ip);
	}
	
	@ResponseBody
	@RequestMapping(value = "wx/ask/tag/extract", produces = "application/json;charset=utf-8")
	public String wxExtractTagsFromTxt(String content){
		return extractTagsFromTxtMethod(content);
	}
	
	@ResponseBody
    @RequestMapping(value = "app/ask/tag/extract", produces = "application/json;charset=utf-8")
    public String appExtractTagsFromTxt(String content){
        return extractTagsFromTxtMethod(content);
    }

	public String extractTagsFromTxtMethod(String content) {
		return this.extractTagsFromTxtMethod(content, null);
	}

	public String extractTagsFromTxtMethod(String content, String ip){
		if(StringUtils.isBlank(content)){
			return ResponseJSON.getErrorParamsJSON().toString();
		}

		logger.info("extracting tags from content <{}>", content);
		Configuration configuration = BeanManagerFacade.getConfiguration();
		String url = configuration.get("algDomain") + "/tagging";
		Map<String, String> params = new HashMap<>();
		params.put("content", content);
		String response = HttpUtil.get(url, params, 5000, 2000);
		if(StringUtils.isNotBlank(response)){
			JSONObject jsonObj = JSONObject.fromObject(response);
			if(jsonObj.getBoolean("status")){
				List<TagDisplayBean> tags = new ArrayList<>();

				//添加地域标签
//				String existTagId = "";
//				TagDisplayBean locationTag = this.getCityTag(ip);
//				if(null != locationTag) {
//					tags.add(locationTag);
//					existTagId = locationTag.getId();
//				}

				JSONArray jsonArrayData = jsonObj.getJSONArray("data");
				if(jsonArrayData != null && jsonArrayData.size() > 0){
					JSONObject tagJsonObj;
					int count = 0;
					for(int i = 0; i < jsonArrayData.size(); i++){
						tagJsonObj = jsonArrayData.getJSONObject(i);
						TagDisplayBean tag = new TagDisplayBean();
						tag.setId(IDUtil.encodeId(tagJsonObj.getLong("id")));
						tag.setFatherId(IDUtil.encodeId(tagJsonObj.getLong("father_id")));
						tag.setName(StringEscapeUtils.unescapeJava(tagJsonObj.getString("name")));
						tag.setLevel(tagJsonObj.getInt("level"));
						tag.setWeight(tagJsonObj.getDouble("weight"));
						tags.add(tag);

						if(++count >= 3)
							break;
					}
				}

				JSONObject responseJSON = ResponseJSON.getSucJSON();
				responseJSON.put("data", tags);
				logger.info("extract tags succeeded, total tags=" + tags.size());
				return responseJSON.toString();
			}

		}else {
			logger.info("extract tags from content error");
		}

		return ResponseJSON.getErrorInternalJSON().toString();
	}

	private void fillTagRelatedQuestions(TagRelatedItemDisplayBean bean, Long decodedTagId, Long bpId, TEliteSourceType source){
		TSearchQuestionCondition condition = new TSearchQuestionCondition();
		condition.setFrom((bean.getCurrPageNo() - 1)*bean.getPageSize())
				.setCount(bean.getPageSize())
				.setTagIds(String.valueOf(decodedTagId))
				.setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

		TQuestionListResult listResult = null;
		long start = System.currentTimeMillis();
		try {
			listResult = eliteThriftServiceAdapter.searchQuestion(condition);
		} catch (TException e) {
			logger.error("", e);
		}
		if(listResult == null){
			listResult = new TQuestionListResult(new ArrayList<>(), 0);
		}
		long end = System.currentTimeMillis();
		logger.info("RelatedQuestion : Time cost test: Question search cost time : " + String.valueOf(end - start));
		
		start = System.currentTimeMillis();
		bean.setTotal(listResult.getTotal());
		bean.setFeedItemList(ConvertUtil.convertQuestionList(listResult.getQuestions(), bpId, source));
		bean.setTag(ConvertUtil.convertToTagItemBean(decodedTagId.intValue(), bpId, true));
		end = System.currentTimeMillis();
		logger.info("RelatedQuestion : Time cost test: Question fill cost time : " + String.valueOf(end - start));
	}

	private void fillTagRelatedAnswers(TagRelatedItemDisplayBean bean, Long decodedTagId, Long bpId, TEliteSourceType source){
		TSearchAnswerCondition condition = new TSearchAnswerCondition();
		condition.setFrom((bean.getCurrPageNo() - 1) * bean.getPageSize())
				.setCount(bean.getPageSize())
				.setTagIds(String.valueOf(decodedTagId))
				.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

		TAnswerListResult listResult = null;

		try {
			listResult = eliteThriftServiceAdapter.searchAnswer(condition);
		} catch (TException e) {
			logger.error("", e);
		}
		if(listResult == null){
			listResult = new TAnswerListResult(new ArrayList<>(), 0);
		}

		bean.setTotal(listResult.getTotal());
		bean.setFeedItemList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, source));
		bean.setTag(ConvertUtil.convertToTagItemBean(decodedTagId.intValue(), bpId, true));
	}
}