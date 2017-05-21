package com.sohu.bp.elite.service.web.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagStatus;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.action.bean.FeedListDisplayBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureAppWrapperBean;
import com.sohu.bp.elite.action.bean.feature.EliteFeatureItemBean;
import com.sohu.bp.elite.action.bean.feature.EliteFocusBean;
import com.sohu.bp.elite.action.bean.feature.EliteHeadFragment;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.CacheConstants;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteFeatureStatus;
import com.sohu.bp.elite.enums.EliteRecomType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteFragment;
import com.sohu.bp.elite.model.TEliteFragmentType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSubjectListResult;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.ColumnService;
import com.sohu.bp.elite.service.web.FeatureService;
import com.sohu.bp.elite.service.web.IdentityService;
import com.sohu.bp.elite.util.CompositeIDUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.DateUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.HttpUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.IdentityUtil;
import com.sohu.bp.elite.util.ImageUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.elite.util.TagUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 
 * @author zhijungou
 * 2016年8月26日
 */
public class FeatureServiceImpl implements FeatureService {
	private static final Logger log = LoggerFactory.getLogger(FeatureServiceImpl.class);
	private String host;
	private EliteThriftServiceAdapter adapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	private BpDecorationServiceAdapter tagAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	private BpUserService bpUserService;
	private ColumnService columnService;
	private IdentityService identityService;
	
	private CacheManager redisCacheManager;
	private RedisCache redisCache;
	private RedisCache dataCache;
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setRedisCacheManager(CacheManager redisCacheManager)
	{
		this.redisCacheManager = redisCacheManager;
	}
	
	public BpUserService getBpUserService() {
		return bpUserService;
	}

	public void setBpUserService(BpUserService bpUserService) {
		this.bpUserService = bpUserService;
	}
	
	public void setColumnService(ColumnService columnService) {
		this.columnService = columnService;
	}
	
	public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

	public void init()
	{
		redisCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_BP_ELITE_FEATURE);
		dataCache = (RedisCache) redisCacheManager.getCache(CacheConstants.CACHE_DATA);
	}
	
	@Override
	public List<TEliteSubject> getEliteSubject(Integer start, Integer count) {
		
		List<TEliteSubject> tEliteSubjects = new ArrayList<TEliteSubject>();
		
		if(start == 0 && count == 1) {
			TEliteSubject tEliteSubject = (TEliteSubject) redisCache.get(Constants.ELITE_SUBJECT_KEY);
			if(null != tEliteSubject && tEliteSubject.getId() > 0) {
				tEliteSubjects.add(tEliteSubject);
				return tEliteSubjects;
			}			
		}
		try {
			TSubjectListResult listResult = adapter.getHistoryByStatus(EliteFeatureStatus.VALID.getValue(), start, count);
			if(null != listResult && listResult.getTotal() > 0) {
				tEliteSubjects = listResult.getSubjects();
				if (start == 0) redisCache.put(Constants.ELITE_SUBJECT_KEY, tEliteSubjects.get(0));
				}
		} catch (Exception e) {
			log.error("", e);
		}
		return tEliteSubjects;
	}

	@Override
	public Long getEliteCount() {
		Long num = 0l;
		try {
			TSubjectListResult listResult = adapter.getHistoryByStatus(EliteFeatureStatus.VALID.getValue(), 0, 1);
			if(null != listResult && listResult.getTotal() > 0)
				{
					num = listResult.getTotal();
				}
		} catch (Exception e) {
			log.error("", e);
		}
		return num;
	}
	@Override
	public List<TEliteTopic> getEliteTopic(Integer start, Integer count) {
		List<TEliteTopic> tEliteTopics = new ArrayList<TEliteTopic>();
		if (0 == start && 1 == count) {
			TEliteTopic tEliteTopic = (TEliteTopic) redisCache.get(Constants.ELITE_TOPIC_KEY);
			if (null != tEliteTopic && tEliteTopic.getId() > 0) {
				tEliteTopics.add(tEliteTopic);
				return tEliteTopics;
			}
		}
		
		try {
			List<TEliteTopic> listResult = adapter.getAllEliteTopicByStatus(EliteFeatureStatus.VALID.getValue(),start, count);
			if(null != listResult && listResult.size() > 0) {
				tEliteTopics = listResult;
				if (start == 0) redisCache.put(Constants.ELITE_TOPIC_KEY, tEliteTopics.get(0));
				}
		} catch (Exception e){
			log.error("", e);
		}
		return tEliteTopics;
	}
	
	@Override
	public long getTopicCount() {
		long num = 0;
		try {
			num = adapter.getEliteTopicCountByStatus(EliteFeatureStatus.VALID.getValue());
		} catch (Exception e) {
			log.error("", e);
		}
		return num;
	}

	@Override
	public JSONArray getTagNav() {
		JSONArray retVal = new JSONArray();
		retVal = (JSONArray) redisCache.get(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_SPLIT_CHAR + TEliteFragmentType.NAVLABEL.getValue());
		if (null != retVal && !retVal.isEmpty())
			return retVal;

		JSONArray tagNav = new JSONArray();
		try {
			List<TEliteFragment> tEliteFragments = adapter.getAllFragment();
			for (TEliteFragment tEliteFragment : tEliteFragments) {
				if (tEliteFragment.getType() == TEliteFragmentType.NAVLABEL
						&& tEliteFragment.getStatus() == EliteFeatureStatus.VALID.getValue()) {
					JSONObject detail = JSONObject.fromObject(tEliteFragment.getDetail());
					List<Integer> keys = new ArrayList<>();
					Set<String> keySets = detail.keySet();
					for (String keySet : keySets)
						keys.add(Integer.parseInt(keySet));

					List<Tag> oldTags = tagAdapter.getSubTagListByParentId(0, TagType.ELITE_TAG, TagStatus.WORK);
					List<Integer> ids = new ArrayList<>();
					for (Tag oldTag : oldTags) {
						Integer id = oldTag.getId();
						JSONObject tagDetail = (JSONObject) detail.get(id.toString());
						if (keys.contains(id) && tagDetail.getBoolean("isNavTag")) {
							ids.add(id);
							JSONObject tag = new JSONObject();
							tag.put("id", IDUtil.encodeId(id));
							tag.put("name", tagDetail.get("name"));
							tag.put("pic", ImageUtil.removeImgProtocol(tagDetail.getString("pic")));
							tag.put("sub_tag", new JSONArray());
							tagNav.add(tag);
						}
					}
					for (Integer key : keys) {
						JSONObject subTagDetail = (JSONObject) detail.get(key.toString());
						if (!ids.contains(key) && subTagDetail.getBoolean("isNavTag")) {
							Tag subTag = tagAdapter.getTagById(key);
							if (subTag.status == TagStatus.WORK) {
    							Integer tagParentId = subTag.getParentId();
    							Iterator it = tagNav.iterator();
    							int index = 0;
    							while (it.hasNext()) {
    								JSONObject tagJSON = (JSONObject) it.next();
    								if (tagParentId.intValue() ==  IDUtil.decodeId(tagJSON.getString("id"))) {
    									JSONObject subTagJSON = new JSONObject();
    									subTagJSON.put("id", IDUtil.encodeId(key.longValue()));
    									subTagJSON.put("name", subTagDetail.get("name"));
    									tagJSON.accumulate("sub_tag", subTagJSON);
    									tagNav.set(index, tagJSON);
    								}
    								index++;
    							}
							}
						}
					}

					break;
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		retVal = tagNav;
		redisCache.put(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_SPLIT_CHAR + TEliteFragmentType.NAVLABEL.getValue(), retVal);
		return retVal;
	}

	@Override
	public String getSlogan() {
		String slogan = Constants.DEFAULT_SLOGAN;
		JSONObject sloganJSON = (JSONObject)redisCache.get(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_SPLIT_CHAR + TEliteFragmentType.SLOGAN.getValue());
		if (null != sloganJSON && sloganJSON.containsKey("slogan")) {
			slogan = sloganJSON.getString("slogan");
			if (StringUtils.isBlank(slogan)) {
				slogan = Constants.DEFAULT_SLOGAN;
			}
			return slogan;
		}
		try {
			List<TEliteFragment> fragmentList = adapter.getFragmentByType(TEliteFragmentType.SLOGAN);
			if (null != fragmentList) {
				for (TEliteFragment tEliteFragment : fragmentList) {
					if (null != tEliteFragment && tEliteFragment.getStatus() == EliteFeatureStatus.VALID.getValue()) {
						sloganJSON = JSONObject.fromObject(tEliteFragment.getDetail());
						break;
					}
				}
			}
			if (null != sloganJSON && sloganJSON.containsKey("slogan")) {
				slogan = sloganJSON.getString("slogan");
				if (StringUtils.isBlank(slogan)) {
					slogan = Constants.DEFAULT_SLOGAN;
				}
				redisCache.put(Constants.ELITE_FRAGMENT_KEY + Constants.DEFAULT_SPLIT_CHAR + TEliteFragmentType.SLOGAN.getValue(), sloganJSON);
				return slogan;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return slogan;
	}

	@Override
	public List<Integer> getTagSquare(Integer tagId) {
		if(null == tagId) return null;
		List<Integer> tagSquare = new ArrayList<Integer>();
		
		try{
			Tag tag = tagAdapter.getTagById(tagId);
			Integer parentTagId = tag.getParentId();
			if(null != parentTagId) 
			{	
				if(0 != parentTagId) tagSquare.add(parentTagId);
				List<Tag> siblings = tagAdapter.getSubTagListByParentId(parentTagId, TagType.ELITE_TAG, TagStatus.WORK);
				for(Tag sibling : siblings)
				{
					if(tagId != sibling.getId() && sibling.getId() > 0) tagSquare.add(sibling.getId());
				}
			}
		} catch (Exception e){
			log.error("", e);
		}
		return tagSquare;
	}
	
	@Override
	public TEliteSubject getEliteSubjectById(Long id)
	{
		if(null == id || id <= 0) return null;
		TEliteSubject tEliteSubject = (TEliteSubject) redisCache.get(Constants.ELITE_SUBJECT_KEY);
		if(null == tEliteSubject || tEliteSubject.getId() < 0 || id != tEliteSubject.getId())
		{
		try{
			TEliteSubject result = adapter.getHistoryById(id);
			if(null != result && result.getId() > 0) tEliteSubject = result;
		} catch (Exception e){
			log.error("", e);
		}
		}
		return tEliteSubject;
	}
	
	@Override
	public TEliteTopic getEliteTopicById(Long id)
	{
		if(null == id || id <= 0) return null;
		TEliteTopic tEliteTopic = (TEliteTopic) redisCache.get(Constants.ELITE_TOPIC_KEY);
		if(null == tEliteTopic || tEliteTopic.getId() < 0)
		{
		try{
			TEliteTopic result = adapter.getEliteTopicById(id);
			if(null != result && result.getId() > 0) tEliteTopic = result;
		} catch (Exception e){
			log.error(" ",e);
		}
		}
		return tEliteTopic;
	}
	
	@Deprecated
	@Override
	public FeedListDisplayBean getTopFeeds(FeedListDisplayBean bean, Long bpId, Integer start, Integer count)
	{
		try{
			if(0 == start){
				if(redisCache.exist(CacheConstants.CACHE_ELITE_TOPFEEDS)){
					String JSONString = (String) redisCache.get(CacheConstants.CACHE_ELITE_TOPFEEDS);
					bean = JSON.parseObject(JSONString, FeedListDisplayBean.class);
					return bean;
				}
			}
			Integer num = start + count;
			List<String> idAllList = getRecomList(EliteRecomType.RECOM_ANSWER.getValue(), num + 1);
			bean.setTotal(idAllList.size());
			//bean.setTotalPage((int) bean.getTotal()/bean.getPageSize() + 1);
			List<String> idList = null;
			if(idAllList.size() > (start + num)) {
				idList = idAllList.subList(start, num);
				bean.setNextPageNo(bean.getCurrPageNo() + 1);
			}
			else {
				idList = idAllList.subList(start, idAllList.size());
				bean.setNextPageNo(-1);
			}
			List<TEliteAnswer> tEliteAnswers = new ArrayList<>();
			for(String id : idList)
			{
				TEliteAnswer answer = adapter.getAnswerById(Long.valueOf(id));
				if(null != answer) tEliteAnswers.add(answer);
			}
			if(null != tEliteAnswers && tEliteAnswers.size() > 0) bean.setFeedItemList(ConvertUtil.convertAnswerList(tEliteAnswers, bpId, null));
			if(0 == start){ 
				redisCache.put(CacheConstants.CACHE_ELITE_TOPFEEDS,Constants.TOP_FEEDS_OVERDUE, JSON.toJSONString(bean));
			}
		} catch (Exception e)
		{
			log.error("" , e);
		}
		return bean;
	}
	
	@Override
	public List<TEliteQuestion> getRelevantQuestion(Long bpId , Long questionId)
	{
		List<TEliteQuestion> tEliteQuestions = new ArrayList<>();
		try{
			List<Long> idList = getRelevantQuestionApi(questionId);
			//刨去问题本身的id
			idList.remove(questionId);
			if(null != idList && idList.size() > 0)
			{
				for(Long id : idList)
				{
					TEliteQuestion tEliteQuestion = adapter.getQuestionById(id);
					if(null != tEliteQuestion) tEliteQuestions.add(tEliteQuestion);
				}
					
			}
		} catch(Exception e){
			log.error("", e);
		}
		return tEliteQuestions;
	}
	
	@Override
	public List<Tag> getQuestionRecomTags(Long questionId)
	{
		List<Tag> tags = new ArrayList<>();
		try{
			if(null == questionId) return null;
			List<Long> idList = getRelevantTagApi(questionId);
			//刨去问题本身的标签
			TEliteQuestion question = adapter.getQuestionById(questionId);
			String[] ids = question.getTagIds().split(Constants.TAG_IDS_SEPARATOR);
			for(String id : ids){
				if(StringUtils.isBlank(id)) continue;
				idList.remove(Long.valueOf(id));
			}
			if(null != idList && idList.size() > 0){
				for(Long id : idList)
				{
					Tag tag = tagAdapter.getTagById(id.intValue());
					tags.add(tag);
				}
			}
		} catch (Exception e){
			log.error("", e);
		}
		return tags;
	}
	
	@Override
	public List<UserDetailDisplayBean> getRecomUsers(Long viewerId, Integer tag)
	{	
		final Integer showNum = 6;
		List<UserDetailDisplayBean> retVal = new ArrayList<>();
		List<String> idTagList = new ArrayList<>();
		try{
			List<String> idList = getRecomList(EliteRecomType.RECOM_USER.getValue(), Integer.MAX_VALUE);
			if(null != idList && idList.size() > 0)
			{
				Integer size = idList.size();
				if(size <= showNum) idTagList = idList;
				else{
					
					Integer start = tag * showNum - ((int)((tag * showNum) / size)) * size;
					if(start + showNum <= size) idTagList = idList.subList(start, start + showNum);
					else{
						idTagList = idList.subList(start, size);
						idTagList.addAll(idList.subList(0, showNum - size +start));
					}				
				}
				for(String id : idTagList)
				{
					UserDetailDisplayBean userInfo = bpUserService.getUserDetailByBpId(Long.valueOf(id), viewerId);
					retVal.add(userInfo);
				}
			}
			
		} catch(Exception e){
			log.error("", e);
		}
		
		return retVal;
	}
	
	@Override
	public List<Tag> getRecomTags(Long bpId) {
		List<Tag> tags = new ArrayList<>();
		List<String> idList = getRecomList(EliteRecomType.RECOM_TAG.getValue(), Integer.MAX_VALUE);
		try{
			if(null != idList && idList.size() > 0){
				for(String id : idList){
					//判断是否已关注
					if(InteractionUtil.hasFollowed(bpId, BpType.Elite_User.getValue(), Long.valueOf(id), BpType.Tag.getValue())){
						continue;
					}
					Tag tag = tagAdapter.getTagById(Integer.valueOf(id));
					tags.add(tag);			
				}
			}
		} catch (Exception e){
			log.error("", e);
		}
		return tags;
	}
	
	@Override
	public List<String> getRecomList(Integer type, Integer count){
		List<String> retVal = new ArrayList<>();
		String uri = "/decoration/search/innerapi/elite/getRecomList.action";
		Map<String, String> params = new HashMap<>();
		params.put("type", type.toString());
		params.put("size", count.toString());
		String result= HttpUtil.post(host + uri, params); 
		JSONObject resJSON = JSONObject.fromObject(result);
		if(0 == resJSON.getInt("code"))
		{
			JSONObject data = resJSON.getJSONObject("data");
			JSONArray dataArray = data.getJSONArray("ids");
			Iterator it = dataArray.iterator();
			while(it.hasNext()){
				retVal.add(String.valueOf(it.next()));
			}
		}
		return retVal;
	}
	
	public List<Long> getRelevantQuestionApi(Long questionId){
		List<Long> retVal = new ArrayList<>();
		String uri = "/decoration/search/innerapi/search/question?";
		String keywords = "keywords=";
		String count = "count=10";
		Map<String, String> params = new HashMap<>();
		try{
		TEliteQuestion question = adapter.getQuestionById(questionId);
		keywords += HtmlUtils.htmlUnescape(question.getTitle());
		} catch (Exception e) {
			log.error("", e);
		}
		keywords = keywords.replaceAll("\\s+", "%20");
		String url = host + uri + keywords + "&" + count;
		String result = HttpUtil.post(url, params);
		JSONObject resJSON = JSONObject.fromObject(result);
		{
			JSONObject data = resJSON.getJSONObject("data");
			JSONArray dataArray = data.getJSONArray("idArray");
			for(int index = 0; index < dataArray.size(); index ++){
				Long id = dataArray.getLong(index);
				retVal.add(id);
			}
		}
		return retVal;
	}

	public List<Long> getRelevantTagApi(Long questionId){
		List<Long> retVal = new ArrayList<>();
		String uri = "/decoration/search/innerapi/elite/getRecomTagsForQuestion.action?";
		String id = "id=" + questionId.toString();
		Map<String, String> params = new HashMap<>();
		String url = host + uri + id;
		String result = HttpUtil.post(url, params);
		JSONObject resJSON = JSONObject.fromObject(result);
		if(0 == resJSON.getInt("code")){
			JSONArray resArray = resJSON.getJSONObject("data").getJSONArray("tags");
			for(int index = 0; index < resJSON.getJSONObject("data").getJSONArray("tags").size(); index++){
				retVal.add(resArray.getLong(index));
			}
		}
		return retVal;
	}
	
	public Long getAnswerNum(Long questionId){
		Long num = 0l;
		try{
			TSearchAnswerCondition condition = new TSearchAnswerCondition();
			condition.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()))
						.setQuestionId(questionId)
						.setFrom(0)
						.setCount(1);
			TAnswerListResult answerListResult = adapter.searchAnswer(condition);
			num = answerListResult.getTotal();
		} catch (Exception e){
			log.error("" ,e);
		}
		return num;
	}

    @Override
    public List<EliteFocusBean> getFocus() {
        List<EliteFocusBean> list = new ArrayList<EliteFocusBean>();
        String cacheText = (String)redisCache.get(Constants.ELITE_FOCUS_ORDER_KEY);
        if (null == cacheText) {
            Set<String> sets = redisCache.zRange(Constants.ELITE_FOCUS_ORDER_SET_KEY, 0, -1);
            for (String idString : sets) {
                try {
                    Long compositeId = Long.valueOf(idString);
                    Long id = CompositeIDUtil.getObjectIdByCompositeId(compositeId);
                    BpType bpType = CompositeIDUtil.getBpTypeByCompositeId(compositeId);
                    EliteFocusBean bean = new EliteFocusBean();
                    boolean statusFlag = true;
                    switch (bpType) {
                    case Elite_Subject :
                        TEliteSubject subject = adapter.getHistoryById(id);
                        if (statusFlag = (subject.getStatus() == EliteFeatureStatus.VALID.getValue())) {
                            bean.setBpType(BpType.Elite_Subject.getValue()).setId(IDUtil.encodeId(id)).setTitle(HtmlUtils.htmlUnescape(subject.getName()))
                            .setCover(ImageUtil.removeImgProtocol(subject.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(subject.getCover()));
                        }
                        break;
                    case Elite_Topic :
                        TEliteTopic topic = adapter.getEliteTopicById(id);
                        if (statusFlag = (topic.getStatus() == EliteFeatureStatus.VALID.getValue())) {
                            bean.setBpType(BpType.Elite_Topic.getValue()).setId(IDUtil.encodeId(topic.getQuestionId())).setTitle(HtmlUtils.htmlUnescape(topic.getTitle()))
                            .setCover(ImageUtil.removeImgProtocol(topic.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(topic.getCover()));
                        }
                        break;
                    case Elite_Column :
                        TEliteColumn column = adapter.getEliteColumnById(id);
                        if (statusFlag = (column.getStatus() == EliteFeatureStatus.VALID.getValue())) {
                            bean.setBpType(BpType.Elite_Column.getValue()).setId(IDUtil.encodeId(id)).setTitle(HtmlUtils.htmlUnescape(column.getName()))
                            .setCover(ImageUtil.removeImgProtocol(column.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(column.getCover()));
                        }
                       break;
                    default:
                       break;
                    }
                    if (!statusFlag) {
                        redisCache.zRem(Constants.ELITE_FOCUS_ORDER_SET_KEY, idString);
                        log.info("get focus, id = {}, bpType = {} doesn't status is invalid, remove from order cache", new Object[]{id, bpType});
                        continue;
                    }
                    list.add(bean);
                } catch (Exception e) {
                    log.error("", e);
                    redisCache.zRem(Constants.ELITE_FOCUS_ORDER_SET_KEY, idString);
                    log.info("get focus, compositeId = {}  doesn't exist in db, remove from order cache", new Object[]{idString});
                }
            }
            String content = com.alibaba.fastjson.JSONObject.toJSONString(list);
            redisCache.put(Constants.ELITE_FOCUS_ORDER_KEY, content);
            log.info("focus content doesn't exist in cache, save content = {} in cache.", new Object[]{});
        } else {
            list = com.alibaba.fastjson.JSONArray.parseArray(cacheText, EliteFocusBean.class);
        }
        //如果没有读取到数值，则获取默认的焦点图
        if (list.size() == 0) {
        	try {
        		List<TEliteSubject> subjects = this.getEliteSubject(0, 1);
        		if (null != subjects && subjects.size() > 0) {
        			EliteFocusBean bean = new EliteFocusBean();
        			TEliteSubject subject = subjects.get(0);
        			bean.setBpType(BpType.Elite_Subject.getValue()).setId(IDUtil.encodeId(subject.getId())).setTitle(HtmlUtils.htmlUnescape(subject.getName()))
                    .setCover(ImageUtil.removeImgProtocol(subject.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(subject.getCover()));
        			list.add(bean);
        		}
        		List<TEliteTopic> topics = this.getEliteTopic(0, 1);
        		if (null != topics && topics.size() > 0) {
        			EliteFocusBean bean = new EliteFocusBean();
        			TEliteTopic topic = topics.get(0);
        			bean.setBpType(BpType.Elite_Topic.getValue()).setId(IDUtil.encodeId(topic.getQuestionId())).setTitle(HtmlUtils.htmlUnescape(topic.getTitle()))
                    .setCover(ImageUtil.removeImgProtocol(topic.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(topic.getCover()));
        			list.add(bean);
        		}
        		TEliteColumn column = columnService.getIndexColumn();
        		if (null != column && column.getId() > 0) {
        			EliteFocusBean bean = new EliteFocusBean();
        			bean.setBpType(BpType.Elite_Column.getValue()).setId(IDUtil.encodeId(column.getId())).setTitle(HtmlUtils.htmlUnescape(column.getName()))
                    .setCover(ImageUtil.removeImgProtocol(column.getCover())).setCoverSmall(ImageUtil.getWapFocusCover(column.getCover()));
        			list.add(bean);
        		}
        	} catch (Exception e) {
        		log.error("", e);
        	}
        }
        return list;
    }

    @Override
    public EliteHeadFragment getAppHeader() {
        EliteHeadFragment head = new EliteHeadFragment();
        String appHeaderString = (String)dataCache.get(CacheConstants.DATA_APP_FOCUS);
        if (null != appHeaderString) {
            head = (EliteHeadFragment)JSONObject.toBean(JSONObject.fromObject(appHeaderString), EliteHeadFragment.class);
        } else {
            List<TEliteSubject> list = getEliteSubject(0, 1);
            if (null != list && list.size() > 0) {
                TEliteSubject subject = list.get(0);
                head.setTitle(HtmlUtils.htmlUnescape(subject.getName()));
                head.setUrl("https://" + OverallDataFilter.askDomain + "fs/" + IDUtil.encodeId(subject.getId()));
                head.setPic(ImageUtil.getWapFocusCover(subject.getCover()));
            }
        }
        return head;
    }

    @Override
    public EliteFeatureAppWrapperBean getAppWrapper() {
        EliteFeatureAppWrapperBean featureWrapper = (EliteFeatureAppWrapperBean) redisCache.get(Constants.ELITE_FEATURE_WRAPPER_KEY);
        if (null == featureWrapper) {
            featureWrapper = new EliteFeatureAppWrapperBean();
            // 获取头图
            EliteHeadFragment headFragment = getAppHeader();
            // 获取专题
            List<EliteFeatureItemBean> subjects = new ArrayList<EliteFeatureItemBean>();
            try {
                List<TEliteSubject> subjectList = getEliteSubject(0, Constants.APP_SIZE_LITTLE);
                subjectList.stream().map(ConvertUtil::convertToFeatureItemBean).forEach(subjects::add);
            } catch (Exception e) {
                log.error("", e);
            }
            // 获取专栏
            List<EliteFeatureItemBean> columns = new ArrayList<EliteFeatureItemBean>();
            try {
                List<TEliteColumn> columnList = adapter.getAllEliteColumn(0, Constants.APP_SIZE_LITTLE).getColumns();
                columnList.stream().map(ConvertUtil::convertToFeatureItemBean).forEach(columns::add);
            } catch (Exception e) {
                log.error("", e);
            }
            // 获取热门讨论
            List<EliteFeatureItemBean> topics = new ArrayList<EliteFeatureItemBean>();
            try {
                List<TEliteTopic> topicList = getEliteTopic(0, Constants.DEFAULT_APP_SIZE);
                topicList.stream().map(ConvertUtil::convertToFeatureItemBean).forEach(topics::add);
            } catch (Exception e) {
                log.error("", e);
            }
    
            // 获取专家团列表
            List<UserDetailDisplayBean> users = new ArrayList<UserDetailDisplayBean>();
            try {
                List<Long> ids = identityService.getRecomExperts(0, Constants.DEFAULT_APP_SIZE);
                if (null != ids) {
                    ids.forEach(id -> users.add(bpUserService.getUserDetailByBpId(id, null, true)));
                }
            } catch (Exception e) {
                log.error("", e);
            }
            featureWrapper.setHeadFragment(headFragment).setSubjects(subjects).setColumns(columns).setTopics(topics)
                    .setUsers(users);
            redisCache.put(Constants.ELITE_FEATURE_WRAPPER_KEY, 5 * 60, featureWrapper);
        }
        return featureWrapper;
    }
}
