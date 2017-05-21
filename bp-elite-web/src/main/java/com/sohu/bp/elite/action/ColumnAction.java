package com.sohu.bp.elite.action;

import java.text.SimpleDateFormat;
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
import org.springframework.beans.BeanUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.action.bean.feature.EliteColumnDisplayBean;
import com.sohu.bp.elite.action.bean.feature.EliteColumnListDetailBean;
import com.sohu.bp.elite.action.bean.feature.EliteColumnListDisplayBean;
import com.sohu.bp.elite.action.bean.feature.EliteColumnQuestionsDisplayBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.tag.TagDisplayBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TColumnListResult;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.ColumnService;
import com.sohu.bp.elite.service.web.WrapperPageService;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.ImageUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.util.ResponseJSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class ColumnAction {
	
	private static final Logger log = LoggerFactory.getLogger(ColumnAction.class);
	private BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
	private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
	
	@Resource
	private WrapperPageService wrapperPageService;
	@Resource
	private ColumnService columnService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private BpUserService bpUserService;

	@ResponseBody
	@RequestMapping(value = "fc/index.json")
	public String getColumnIndex(){
		JSONObject resJSON = ResponseJSON.getDefaultResJSON();
		TEliteColumn column = new TEliteColumn();
		try{
			 column = columnService.getIndexColumn();
		} catch (Exception e) {
			log.error("", e);
		}
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("title", column.getName());
		dataJSON.put("cover", ImageUtil.removeImgProtocol(column.getCover()));
		dataJSON.put("wapCover", ImageUtil.getWapFocusCover(column.getCover()));
		dataJSON.put("columnId", IDUtil.encodeId(column.getId()));
		resJSON.put("data", dataJSON);;
		return resJSON.toString();
	}

	@RequestMapping(value = "fc/{columnId}")
	public String getColumnDetail(@PathVariable("columnId")String columnId, HttpServletRequest request, HttpServletResponse response, HttpSession session, @ModelAttribute("bean")EliteColumnDisplayBean bean){
		Long id = IDUtil.decodeId(columnId);
		if(null == id || id <= 0) return Constants.PAGE_404;
		TEliteColumn column = columnService.getColumnById(id);
		UserInfo userInfo = null;
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null != bpId && bpId > 0)	{
		    userInfo = userInfoService.getUserInfoByBpid(bpId);
		    userInfo.setBpid(bpId);
		}
		convert2display(column, bean, userInfo, null);		
		return AgentUtil.getSource(request) == AgentSource.PC ? "pc/column/column" : "mobile/column/column";
	}
	
    @RequestMapping(value = "app/fc/{columnId}")
    public String appGetColumnDetail(@PathVariable("columnId")String columnId, ModelMap model, HttpServletRequest request, HttpServletResponse response, HttpSession session, @ModelAttribute("bean")EliteColumnDisplayBean bean){  
        /**
         * use for blank template
         */
//        if (StringUtils.isBlank(columnId)) return Constants.PAGE_404;
//        try {
//            long decodedId = IDUtil.decodeId(columnId);
//            TEliteColumn column = eliteAdapter.getEliteColumnById(decodedId);
//            model.put("id", columnId);
//        } catch (Exception e) {
//            log.error("", e);
//        }
//        return "app/column/column";
        
        /**
         * use for beetl render
         */
        Long id = IDUtil.decodeId(columnId);
        if(null == id || id <= 0) return Constants.APP_PAGE_404;
        TEliteColumn column = columnService.getColumnById(id);
        if (null == column) return Constants.APP_PAGE_404;
        model.put("id", columnId);
        UserInfo userInfo = null;
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if(null != bpId && bpId > 0)    {
            userInfo = userInfoService.getUserInfoByBpid(bpId);
            userInfo.setBpid(bpId);
        }
        convert2display(column, bean, userInfo, null);      
        return "app/column/column";
    }
	
    
    @RequestMapping(value = "fc/{columnId}.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getColumnDetailData(@PathVariable("columnId")String columnId, HttpServletRequest request, HttpServletResponse response, HttpSession session, EliteColumnDisplayBean bean) {
        return getColumnDetailDataMethod(columnId, session, bean);
    }
    
    @RequestMapping(value = "wx/fc/{columnId}.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String wxGetColumnDetailData(@PathVariable("columnId")String columnId, HttpServletRequest request, HttpServletResponse response, HttpSession session, EliteColumnDisplayBean bean) {
        return getColumnDetailDataMethod(columnId, session, bean);
    }
    
    @RequestMapping(value = "app/fc/{columnId}.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String appGetColumnDetailData(@PathVariable("columnId")String columnId, HttpServletRequest request, HttpServletResponse response, HttpSession session, EliteColumnDisplayBean bean) {
        return getColumnDetailDataMethod(columnId, session, bean);
    }
    
    public String getColumnDetailDataMethod(String columnId, HttpSession session, EliteColumnDisplayBean bean) {
        Long id = IDUtil.decodeId(columnId);
        if(null == id || id <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        TEliteColumn column = columnService.getColumnById(id);
        UserInfo userInfo = null;
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if(null != bpId && bpId > 0)    userInfo = userInfoService.getUserInfoByBpid(bpId);
        convert2display(column, bean, userInfo, AgentSource.APP);        
        JSONObject resJSON = ResponseJSON.getSucJSON();
        resJSON.put("data", bean);
        return resJSON.toString();
    }
    
    
	@RequestMapping(value = "fc")
	public String getColumnList(HttpServletRequest request, HttpServletResponse response, HttpSession session, @ModelAttribute("bean")EliteColumnListDisplayBean bean){
		getColumnList(bean);
		PageWrapperBean pageWrapper = new PageWrapperBean();
		UserInfo userInfo = null;
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null != bpId && bpId > 0)	userInfo = userInfoService.getUserInfoByBpid(bpId);
		pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, false));
		pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
		bean.setPageWrapper(pageWrapper);
		
		return AgentUtil.getSource(request) == AgentSource.PC ? "pc/column/column-list" : "mobile/column/column-list";
	}
	
    @ResponseBody
    @RequestMapping(value = "fc.json", produces = "application/json;charset=utf-8")
    public String getColumnListData(HttpServletRequest request, HttpSession session, EliteColumnListDisplayBean bean) {
        return getColumnListDataMethod(bean);
    }

    @ResponseBody
    @RequestMapping(value = "app/fc.json", produces = "application/json;charset=utf-8")
    public String appGetColumnListData(HttpServletRequest request, HttpSession session, EliteColumnListDisplayBean bean) {
        return getColumnListDataMethod(bean);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/fc.json", produces = "application/json;charset=utf-8")
    public String wxGetColumnListData(HttpServletRequest request, HttpSession session, EliteColumnListDisplayBean bean) {
        return getColumnListDataMethod(bean);
    }
	
    public String getColumnListDataMethod(EliteColumnListDisplayBean bean) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        getColumnList(bean);
        resJSON.put("data", bean);
        return resJSON.toString();
    }
		

	@RequestMapping(value = "fc/{columnId}/q")
	public String getColumnQuestions(HttpServletRequest request, HttpServletResponse response, HttpSession session, @PathVariable("columnId")String columnId, @ModelAttribute("bean")EliteColumnQuestionsDisplayBean bean){
		Long id = IDUtil.decodeId(columnId);
		if(null == id || id <= 0) return Constants.PAGE_404;
		TEliteColumn column = columnService.getColumnById(id);
		UserInfo userInfo = null;
		Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
		if(null != bpId && bpId > 0)	userInfo = userInfoService.getUserInfoByBpid(bpId);
		convert2questionDisplay(column, bean, userInfo);
		return AgentUtil.getSource(request) == AgentSource.PC ? "pc/column/column-question" : "mobile/column/column-question";
	}
	
	@RequestMapping(value = "fc/{columnId}/q.json", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getColumnQuestions(@PathVariable("columnId")String columnId, @ModelAttribute("bean")EliteColumnQuestionsDisplayBean bean, HttpServletRequest request){
		return getColumnQuestionsMethod(columnId, bean, request);
	}
	
	@RequestMapping(value = "wx/fc/{columnId}/q.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String wxGetColumnQuestions(@PathVariable("columnId")String columnId, @ModelAttribute("bean")EliteColumnQuestionsDisplayBean bean, HttpServletRequest request){
        return getColumnQuestionsMethod(columnId, bean, request);
    }

    @RequestMapping(value = "app/fc/{columnId}/q.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String appGetColumnQuestions(@PathVariable("columnId")String columnId, @ModelAttribute("bean")EliteColumnQuestionsDisplayBean bean, HttpServletRequest request){
        return getColumnQuestionsMethod(columnId, bean, request);
    }
    
    private String getColumnQuestionsMethod(String columnId, EliteColumnQuestionsDisplayBean bean, HttpServletRequest request) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long id = IDUtil.decodeId(columnId);
        if(null == id || id <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        bean.setId(columnId);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        convert2questionList(bean, source);
        resJSON.put("data", bean);
        return resJSON.toString();
    }
	
	public void getColumnList(EliteColumnListDisplayBean bean){
		int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
		int count = bean.getPageSize();
		TColumnListResult resultList = columnService.getColumns(start, count);
		bean.setTotal(resultList.getTotal());
		List<TEliteColumn> columns = resultList.getColumns();
		List<EliteColumnListDetailBean> columnList = new ArrayList<>();
		
		columns.forEach(column -> {
			EliteColumnListDetailBean detail = new EliteColumnListDetailBean();
			convert2list(column, detail);
			columnList.add(detail);
		});
		bean.setColumnList(columnList);
	}
	
	public void convert2display(TEliteColumn column, EliteColumnDisplayBean bean, UserInfo bpInfo, AgentSource source){
		BeanUtil.copyProperties(column, bean, "userInfo", "tags", "content", "publisTime", "questionList", "id");
		bean.setId(IDUtil.encodeId(column.getId()));
		
		String publishTime = new SimpleDateFormat("yyyy.MM.dd").format(new Date(column.getPublishTime()));
		bean.setPublishTime(publishTime);
		
		if (null != bpInfo) {
		    bean.setHasFavorited(InteractionUtil.hasFavorited(bpInfo.getBpid(), column.getId(), BpInteractionTargetType.ELITE_COLUMN));
		}
		
		bean.setDescription(column.getDescription());
		JSONObject userJSON = JSONObject.fromObject(column.getUserInfo());
		UserDetailDisplayBean userInfo = bpUserService.getUserDetailByBpId(userJSON.getLong("bpId"), null);
		userInfo.setDescription(userJSON.getString("description"));
		bean.setUserInfo(userInfo);
		
		String[] tags = column.getTags().split(Constants.TAG_IDS_SEPARATOR);
		List<TagDisplayBean> tagList = new ArrayList<>();
		if(StringUtils.isNotBlank(column.getTags()) && tags.length > 0 ){
			for(String tagString : tags){
				try{
					Tag tag = decorationAdapter.getTagById(Integer.valueOf(tagString));
					TagDisplayBean tagBean = new TagDisplayBean();
					tagBean.setId(IDUtil.encodeId((long)tag.getId()));
					tagBean.setName(tag.getName());
					tagList.add(tagBean);
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		bean.setTags(tagList);
		
		List<JSONObject> content = new ArrayList<>();
		JSONArray array = JSONArray.fromObject(column.getContent());
		array.forEach( contentJSON -> {
			JSONObject contentData = new JSONObject();
			contentData.put("title", JSONObject.fromObject(contentJSON).getString("title"));
			contentData.put("detail", ContentUtil.removeContentImageProtocol(JSONObject.fromObject(contentJSON).getString("detail")));
			contentData.put("wapDetail", ContentUtil.changeContentImgs(JSONObject.fromObject(contentJSON).getString("detail")));
			content.add(contentData);
		});
		bean.setContent(content);		
		
		TColumnListResult listResult = columnService.getColumns(0, Constants.COLUMN_LENGTH);
		List<TEliteColumn> columns = listResult.getColumns();
		List<JSONObject> columnJSONList = new ArrayList<>();
		columns.forEach(columnData -> {
			JSONObject columnJSON = new JSONObject();
			columnJSON.put("cover", ImageUtil.removeImgProtocol(columnData.getCover()));
			columnJSON.put("wapCover", ImageUtil.getWapFocusCover(columnData.getCover()));
			columnJSON.put("name", columnData.getName());
			columnJSON.put("publishTime", new SimpleDateFormat("yyyy.MM.dd").format(new Date(columnData.getPublishTime())));
			columnJSON.put("id", IDUtil.encodeId(columnData.getId()));
			columnJSONList.add(columnJSON);
		});
		bean.setRecentList(columnJSONList);
		Long num = columnService.getQuestionNum(column.getId());
		bean.setTotal(num);
		if (null != source && source == AgentSource.APP) {
		    bean.setQuestionList(columnService.getQuestionsByColumnId(column.getId(), 0, Constants.DEFAULT_PAGE_SIZE));
		} else {
            bean.setQuestionList(columnService.getQuestionsByColumnId(column.getId(), 0, Constants.COLUMN_QUESTION_NUM));
    		PageWrapperBean pageWrapper = new PageWrapperBean();
    		pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(bpInfo, false));
    		pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
    		bean.setPageWrapper(pageWrapper);
        }
	}
	
	public void convert2questionDisplay(TEliteColumn column, EliteColumnQuestionsDisplayBean bean, UserInfo bpInfo){
		BeanUtil.copyProperties(column, bean, "userInfo", "content", "publisTime", "feedItemList", "id");
		bean.setId(IDUtil.encodeId(column.getId()));
		
		bean.setDescription(column.getDescription());
		
		String publishTime = new SimpleDateFormat("yyyy.MM.dd").format(new Date(column.getPublishTime()));
		bean.setPublishTime(publishTime);
		
		JSONObject userJSON = JSONObject.fromObject(column.getUserInfo());
		UserDetailDisplayBean userInfo = bpUserService.getUserDetailByBpId(userJSON.getLong("bpId"), null);
		userInfo.setDescription(userJSON.getString("description"));
		bean.setUserInfo(userInfo);
		convert2questionList(bean, null);
		PageWrapperBean pageWrapper = new PageWrapperBean();
		pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(bpInfo, false));
		pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
		bean.setPageWrapper(pageWrapper);
	}
	
	public void convert2questionList(EliteColumnQuestionsDisplayBean bean, TEliteSourceType source){
		int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
		int count = bean.getPageSize();
		List<SimpleFeedItemBean> questionList = new ArrayList<>();
		try{
			TSearchQuestionCondition condition = new TSearchQuestionCondition();
			condition.setSpecialType(BpType.Elite_Column.getValue()).setSpecialId(IDUtil.decodeId(bean.getId())).setFrom(start).setCount(count)
			.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue())).setSortField("updateTime").setSortType(SortType.DESC);
			TQuestionListResult listResult = eliteAdapter.searchQuestion(condition);
			List<TEliteQuestion> questions = listResult.getQuestions();
			bean.setTotal(listResult.getTotal());
			questionList = ConvertUtil.convertQuestionList(questions, null, source);
		} catch (Exception e) {
			log.error("", e);
		}
		bean.setFeedItemList(questionList);
	}
	
	public void convert2list(TEliteColumn column, EliteColumnListDetailBean bean){
		BeanUtil.copyProperties(column, bean, "userInfo", "publisTime", "Id");
		bean.setId(IDUtil.encodeId(column.getId()));
		
		String publishTime = new SimpleDateFormat("yyyy.MM.dd").format(new Date(column.getPublishTime()));
		bean.setPublishTime(publishTime);
		
		JSONObject userJSON = JSONObject.fromObject(column.getUserInfo());
		UserDetailDisplayBean userInfo = bpUserService.getUserDetailByBpId(userJSON.getLong("bpId"), null);
		userInfo.setDescription(userJSON.getString("description"));
		bean.setUserInfo(userInfo);
	}
}
