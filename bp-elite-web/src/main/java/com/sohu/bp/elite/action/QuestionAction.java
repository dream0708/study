package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sohu.achelous.timeline.util.TimeLineUtil;
import com.sohu.bp.elite.service.web.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.action.bean.person.UserDetailDisplayBean;
import com.sohu.bp.elite.action.bean.question.ELiteQuestionAndAnswerListBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionInviteBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionUpdateBean;
import com.sohu.bp.elite.action.bean.tag.TagItemBean;
import com.sohu.bp.elite.action.bean.wrapper.PageWrapperBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.bean.ContentBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAdTypeBySite;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.enums.EliteSquareOperationType;
import com.sohu.bp.elite.enums.EliteVSType;
import com.sohu.bp.elite.enums.FeedActionType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteMedia;
import com.sohu.bp.elite.model.TEliteMediaStatus;
import com.sohu.bp.elite.model.TEliteMediaType;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteContentAsyncTask;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTask;
import com.sohu.bp.elite.task.EliteExpertTeamAsyncTaskPool;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.task.EliteSquareAsyncTask;
import com.sohu.bp.elite.task.TaskEvent;
import com.sohu.bp.elite.task.TaskEventCacheQueue;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.Bean2UrlUtil;
import com.sohu.bp.elite.util.BpUserUtil;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteMessageUtil;
import com.sohu.bp.elite.util.EliteResponseJSON;
import com.sohu.bp.elite.util.EliteSpecialUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.RequestUtil;
import com.sohu.bp.elite.util.SecurityUtil;
import com.sohu.bp.elite.util.TagUtil;
import com.sohu.bp.elite.util.ToolUtil;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.bp.utils.RealIpUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
@Controller
//@RequestMapping({"question","q"})
public class QuestionAction {

    private static Logger log = LoggerFactory.getLogger(QuestionAction.class);

    private static EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();

    @Resource
    private BpUserService bpUserService;
    @Resource
	private WrapperPageService wrapperPageService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserRestrictService userRestrictService;
    @Resource
    private SquareService squareService;
    @Resource
    private AdDisplayService adDisplayService;
    @Resource
    private InviteService InviteService;
    @Resource
    private KafkaService kafkaService;
    @Resource
    private FeedService feedService;
    
    //跳转到发布问题页
    @RequestMapping(value = {"ask/question/go.html", "q/go"})
    public String showPublishQuestionPage(@ModelAttribute("bean") EliteQuestionInviteBean bean, Integer specialType, HttpServletRequest request, HttpSession session, ModelMap model, HttpServletResponse response){
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
//        if(!bpUserService.checkHasPhoneNo(bpId)){
//            return Constants.PAGE_INDEX;
//        }
        
        log.info("show publish question page");
        
	        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
//	        	if(null == bpId || bpId <= 0 ||!bpUserService.checkHasPhoneNo(bpId)) {
//	        		Configuration configuration = (Configuration)SpringUtil.getBean("configuration");
//	        		String domain = configuration.get("frontDomain");	
//	        		String LoginUrl = configuration.get("loginUrl");
//	        		String uri = request.getRequestURI().toLowerCase(); 
//	        		try {
//						response.sendRedirect(LoginUrl+"?ru="+ domain + uri + "?invitedUserId=" + encodedInviteId);
//					} catch (IOException e) {
//						log.error("", e);
//					}
//	        	}
	            return (null != specialType && specialType == BpType.Elite_VS.getValue()) ? "mobile/pub/pub-vs" : "mobile/pub/pub-question";
	        } else if(AgentUtil.getSource(request) == AgentSource.PC){
	        	if(null == bean.getInvitedUserId()) return Constants.PAGE_INDEX;
				else{
					try {
						response.sendRedirect("/" + Bean2UrlUtil.bean2GetMethodUrl(bean));
					} catch (Exception e) {
						log.error("", e);
                        return Constants.PAGE_404;
					}
				}
	        }else {
	            return Constants.PAGE_404;
	        }
	        return Constants.PAGE_404;
    }

    //获取要修改的问题信息
    @ResponseBody
    @RequestMapping(value = {"ask/question/{questionId}/update.json", "q/{questionId}/up.json"}, produces = "application/json;charset=utf-8")
    public String getUpdateQuestionData(@PathVariable("questionId") String questionId, HttpSession session){
    	return getUpdateQuestionDataMethod(questionId, session, null);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/q/{questionId}/up.json"}, produces = "application/json;charset=utf-8")
    public String wxGetUpdateQuestionData(@PathVariable("questionId") String questionId, HttpSession session){
    	return getUpdateQuestionDataMethod(questionId, session, TEliteSourceType.WRITE_WX);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/q/{questionId}/up.json"}, produces = "application/json;charset=utf-8")
    public String appGetUpdateQuestionData(@PathVariable("questionId") String questionId, HttpSession session){
        return getUpdateQuestionDataMethod(questionId, session, TEliteSourceType.WRITE_APP);
    }
    
    public String getUpdateQuestionDataMethod(String questionId, HttpSession session, TEliteSourceType source){
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(question == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("go update question <bpId={}, questionId={}>", bpId, decodedQuestionId);
        try {
            JSONObject jsonObject = ResponseJSON.getSucJSON();

            EliteQuestionUpdateBean bean = new EliteQuestionUpdateBean();
            bean.setQuestionId(questionId).setTitle(question.getTitle());
            ContentBean contentBean = ContentUtil.parseContent(question.getDetail(), source);
            if (null == source) {
                bean.setContent(ContentUtil.removeContentImageProtocol(question.getDetail()));
            } else {
                bean.setContent(contentBean.getPlainText());
            }
            bean.setPlainText(contentBean.getPlainText()).setImageList(contentBean.getImgList())
            .setTagList(ConvertUtil.convertToTagItemBeanList(question.getTagIds(), bpId, false, false));
            
            bean.setSpecialId(String.valueOf(question.getSpecialId())).setSpecialType(question.getSpecialType())
            .setOptions(ContentUtil.removeOptionsImageProtocol(question.getOptions()));
            List<TEliteMedia> medias = eliteAdapter.getMediaListByQuestionIdAndType(decodedQuestionId, TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0) {
                bean.setVideoId(medias.get(0).getMediaGivenId());
                bean.setvId(IDUtil.encodeId(medias.get(0).getId()));
            }else {
                bean.setVideoId(0L);
                bean.setvId("");
            }

            jsonObject.put("data", bean);
            return jsonObject.toString();
        } catch (TException e) {
            log.error("", e);
        }

       return ResponseJSON.getErrorInternalJSON().toString();
    }

    //调转到发布问题页，修改问题
    @RequestMapping(value = {"ask/question/{questionId}/update.html", "q/{questionId}/up"})
    public String showUpdateQuestionPage(@PathVariable("questionId") String questionId, Integer specialType, ModelMap model, HttpServletRequest request, HttpSession session, HttpServletResponse response){
        if(StringUtils.isBlank(questionId)){
            return Constants.PAGE_404;
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(EliteStatusUtil.isDelStatus(question)){
            return Constants.PAGE_404;
        }
        
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("go update question <bpId={}, questionId={}>", bpId, decodedQuestionId);
        EliteQuestionUpdateBean bean = new EliteQuestionUpdateBean();
        try {
            bean.setQuestionId(questionId).setTitle(question.getTitle());
            ContentBean contentBean = ContentUtil.parseContent(question.getDetail());
            bean.setContent(ContentUtil.removeContentImageProtocol(question.getDetail())).setPlainText(contentBean.getPlainText()).setImageList(contentBean.getImgList())
            .setTagList(ConvertUtil.convertToTagItemBeanList(question.getTagIds(), bpId, false, false));
            
            bean.setSpecialId(String.valueOf(question.getSpecialId())).setSpecialType(question.getSpecialType())
            .setOptions(ContentUtil.removeOptionsImageProtocol(question.getOptions()));
            List<TEliteMedia> medias = eliteAdapter.getMediaListByQuestionIdAndType(decodedQuestionId, TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0) {
                bean.setVideoId(medias.get(0).getMediaGivenId());
                bean.setvId(IDUtil.encodeId(medias.get(0).getId()));
            }else {
                bean.setVideoId(0L);
                bean.setvId("");
            }
            
        } catch (Exception e) {
            log.error("", e);
            return Constants.PAGE_404;
        }
       
        model.putAll(JSON.parseObject(JSON.toJSONString(bean)));

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return (null != specialType && specialType == BpType.Elite_VS.getValue()) ? "mobile/pub/pub-vs" : "mobile/pub/pub-question-update";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
            return Constants.PAGE_INDEX;
        }else {
            return Constants.PAGE_404;
        }
    }

    //发布问题
    @ResponseBody
    @RequestMapping(value = {"ask/question/publish"}, produces = "application/json;charset=utf-8")
    public String publish(String title, @RequestParam(value = "tagIds", required = false) String tagIds, @RequestParam(value = "content", required = false) String content, 
    		@RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session, @RequestParam(value = "invitedUserId", required = false)String encodedInvitedId,
    		@RequestParam(value = "specialId", required = false) String specialId, @RequestParam(value = "specialType", required = false) Integer specialType,
    		@RequestParam(value = "options", required = false) String options){
    	return publishMethod(title, tagIds, content, videoId, request, session, encodedInvitedId, specialType, specialId, options);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/question/publish", produces = "application/json;charset=utf-8")
    public String wxPublish(String title, @RequestParam(value = "tagIds", required = false) String tagIds, @RequestParam(value = "content", required = false) String content, @RequestParam(value = "imgList", required = false)String[] imgList,
    		@RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session, @RequestParam(value = "invitedUserId", required = false)String encodedInvitedId,
    		@RequestParam(value = "specialId", required = false) String specialId, @RequestParam(value = "specialType", required = false) Integer specialType,
    		@RequestParam(value = "options", required = false) String options){
    	if(null != imgList && imgList.length > 0){
    		if (null == content) content = "";
    		for(String imgUrl : imgList){
    			StringBuilder img = new StringBuilder(Constants.CONTENT_IMG_PATTERN);
    			img.insert(Constants.CONTENT_IMG_PATTERN.indexOf("src=") + "src=\"".length(), imgUrl);
    			content += img.toString();
    		}
    	}
    	return publishMethod(title, tagIds, content, videoId, request, session, encodedInvitedId, specialType, specialId, options);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/question/publish"}, produces = "application/json;charset=utf-8")
    public String appPublish(String title, @RequestParam(value = "tagIds", required = false) String tagIds, @RequestParam(value = "content", required = false) String content, 
            @RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session, @RequestParam(value = "invitedUserId", required = false)String encodedInvitedId,
            @RequestParam(value = "specialId", required = false) String specialId, @RequestParam(value = "specialType", required = false) Integer specialType,
            @RequestParam(value = "options", required = false) String options){
        return publishMethod(title, tagIds, content, videoId, request, session, encodedInvitedId, specialType, specialId, options);
    }

    public String publishMethod(String title, String tagIds, String content,
    		String videoId, HttpServletRequest request, HttpSession session, String encodedInvitedId, Integer specialType, String specialId, String options){
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        String errorCode = BpUserUtil.bpUserValid(title, bpId);
        if(StringUtils.isNotBlank(errorCode)){
        	jsonObject = ResponseJSON.getErrorInternalJSON();
            jsonObject.put("errorCode", errorCode);
            return jsonObject.toString();
        }
        
        log.info("inserting question <title={}, content={}, videoId={}, tagIds={}>", new Object[]{title, content, videoId, tagIds});
        try {
            TEliteSourceType source = AgentUtil.getFeedSourceType(request);
            UserDetailDisplayBean user = bpUserService.getUserDetailByBpId(bpId, null);
            String decodedTagIds = TagUtil.decodeTagIds(tagIds);
            
            TEliteQuestion question = new TEliteQuestion();
            question.setBpId(IDUtil.decodeId(user.getBpId()))
                    .setTitle(ContentUtil.preDealContent(ContentUtil.subString(title, Constants.Title_length), false, true))
                    .setDetail(ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Text_length), true, false))
                    .setTagIds(decodedTagIds)
                    .setSource(source.getValue())
                    .setCreateTime(System.currentTimeMillis())
                    .setUpdateTime(System.currentTimeMillis())
                    .setPublishTime(System.currentTimeMillis())
                    .setCreateHost(RequestUtil.getClientIPLong(request))
                    .setCreatePort(RequestUtil.getClientPort(request));
            if (null != specialType) question.setSpecialType(specialType);
            if (StringUtils.isNotBlank(specialId) && IDUtil.decodeId(specialId) > 0)  {
                Long decodeSpecialId = IDUtil.decodeId(specialId);
                if (Objects.equals(specialType, BpType.Elite_VS.getValue()) && decodeSpecialId != EliteVSType.ELITE_VS_POS.getValue() && decodeSpecialId != EliteVSType.ELITE_VS_NEG.getValue()) {
                    return ResponseJSON.getErrorParamsJSON().toString();
                }
                question.setSpecialId(decodeSpecialId);
            }
            if (StringUtils.isNotBlank(options) && JSONArray.fromObject(options).size() > 0) {
                question.setOptions(SecurityUtil.filterOptions(options));
            }
            
            long id = eliteAdapter.insertQuestion(question);
            log.info("insert question result, id=" + id);
            if(id > 0) {
            	question.setId(id);

            	//转存外域图片
                String ip = RealIpUtil.getRealIP(request);
            	List<EliteAsyncTaskOper> operList = new ArrayList<EliteAsyncTaskOper>();
            	operList.add(EliteAsyncTaskOper.RESAVECONTENT);
//            	EliteAsyncTaskPool.addTask(new EliteContentAsyncTask(operList, id, BpType.Question, ip));
                Future<Boolean> contentTaskResult = EliteAsyncTaskPool.submitTask(new EliteContentAsyncTask(operList, id, BpType.Question, ip));
            	
            	TaskEvent event = new TaskEvent(FeedActionType.PUBLISH_QUESTION, BpType.Question, ProduceActionType.ASK, bpId, id, 0, decodedTagIds);
                if (Objects.equals(specialType, BpType.Elite_Vote.getValue())) {
                    event.setActionType(ProduceActionType.PUBLISH_VOTE);
                } else if (Objects.equals(specialType, BpType.Elite_VS.getValue())) {
                    event.setActionType(ProduceActionType.PUBLISH_VS);
                }
                TaskEventCacheQueue.getInstance().push(event);

                if(StringUtils.isNotBlank(videoId)) {
                    log.info("inserting media for question <questionId={}, mediaGivenId={}>", id, videoId);
                    TEliteMedia media = new TEliteMedia();
                    media.setQuestionId(id)
                            .setMediaGivenId(Long.parseLong(videoId))
                            .setStatus(TEliteMediaStatus.WORK)
                            .setType(TEliteMediaType.VIDEO)
                            .setUpdateTime(System.currentTimeMillis())
                            .setUploadTime(System.currentTimeMillis())
                            .setUploadHost(RequestUtil.getClientIPLong(request))
                            .setUploadPort(RequestUtil.getClientPort(request));
                    long mediaId = eliteAdapter.createMedia(media);
                    log.info("insert media result, id=" + mediaId);
                }
                //将用户问题限制数量缓存+1
                userRestrictService.addQuestionNum(bpId);   
                
                JSONObject data = new JSONObject();
                Boolean isInviteQuestion = false;
                //判断是否为向他提问
                if(StringUtils.isNotBlank(encodedInvitedId)) {
                	Long invitedId = IDUtil.decodeId(encodedInvitedId);
                	Integer saveStatus = InviteService.saveInviteStatus(id, bpId, invitedId);
                	EliteMessageUtil.postInviteMessage(user.getNick(), invitedId, id);
                	isInviteQuestion = true;
                }
                data.put("isInviteQuestion", isInviteQuestion);
                data.put("id", IDUtil.encodeId(id));
                jsonObject.put("data", data);

                //推送到推荐
//                ItemLog itemLog = RecLogHelper.createItemLog(ItemType.QUESTION, id);
//                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(itemLog));
            	
//            	squareService.insertSquare(id, FeedType.QUESTION);
            	EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(id, FeedType.QUESTION, EliteSquareOperationType.INSERT));
            	
            	EliteExpertTeamAsyncTaskPool.addTask(new EliteExpertTeamAsyncTask(question.getId(), question.getBpId(), contentTaskResult));
            		
                return jsonObject.toString();
            }
        } catch (TException e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //删除问题
    @ResponseBody
    @RequestMapping(value = {"ask/question/delete"}, produces = "application/json;charset=utf-8")
    public String delete(String questionId, HttpServletRequest request, HttpSession session){
    	return deleteMethod(questionId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/question/delete"}, produces = "application/json;charset=utf-8")
    public String wxDelete(String questionId, HttpServletRequest request, HttpSession session){
    	return deleteMethod(questionId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/question/delete"}, produces = "application/json;charset=utf-8")
    public String appDelete(String questionId, HttpServletRequest request, HttpSession session){
        return deleteMethod(questionId, request, session);
    }
    
    public String deleteMethod(String questionId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(questionId)) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;

        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(question == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke deleting question, questionId={}, bpId={}", decodedQuestionId, bpId);
        question.setStatus(EliteQuestionStatus.DEL.getValue())
                .setUpdateTime(System.currentTimeMillis())
                .setUpdateHost(RequestUtil.getClientIPLong(request))
                .setUpdatePort(RequestUtil.getClientPort(request));

        try {
            boolean updateResult = eliteAdapter.updateQuestion(question);
            log.info("delete question result=" + updateResult);
            if(updateResult){

                TaskEvent event = new TaskEvent(FeedActionType.DELETE_QUESTION, BpType.Question, null, bpId, decodedQuestionId, 0, "");
                TaskEventCacheQueue.getInstance().push(event);

                List<TEliteMedia> medias = eliteAdapter.getMediaListByQuestionIdAndType(decodedQuestionId, TEliteMediaType.VIDEO);
                if(medias != null && medias.size() > 0){
                    log.info("starting delete question media");
                    boolean deleteMediaResult = false;
                    for(TEliteMedia media : medias){
                        deleteMediaResult = eliteAdapter.removeMedia(media);
                    }
                    log.info("delete question media result=" + deleteMediaResult);
                }
                
//            	squareService.reduceObject(decodedQuestionId, FeedType.QUESTION);
                EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(decodedQuestionId, FeedType.QUESTION, EliteSquareOperationType.REMOVE));
            	
            	//推送到推荐
//            	ItemLog itemLog = RecLogHelper.createItemLog(ItemType.QUESTION, decodedQuestionId);
//                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(itemLog));
                
                return ResponseJSON.getSucJSON().toString();
            }
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //更新问题
    @ResponseBody
    @RequestMapping(value = {"ask/question/update"}, produces = "application/json;charset=utf-8")
    public String update(String questionId,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content,
                         @RequestParam(value = "videoId", required = false) String videoId,
                         @RequestParam(value = "tagIds", required = false) String tagIds,
                         HttpSession session, HttpServletRequest request){
    	return updateMethod(questionId, title, content, videoId, tagIds, session, request, null);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/question/update"}, produces = "application/json;charset=utf-8")
    public String wxUpdate(String questionId,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content,
                         @RequestParam(value = "videoId", required = false) String videoId,
                         @RequestParam(value = "tagIds", required = false) String tagIds,
                         @RequestParam(value = "imgList", required = false) String[] imgList,
                         HttpSession session, HttpServletRequest request){
    	if(null != imgList && imgList.length > 0){
    		if (null == content) content = "";
    		for(String imgUrl : imgList){
    			StringBuilder img = new StringBuilder(Constants.CONTENT_IMG_PATTERN);
    			img.insert(Constants.CONTENT_IMG_PATTERN.indexOf("src=") + "src=\"".length(), imgUrl);
    			content += img.toString();
    		}
    	}
    	return updateMethod(questionId, title, content, videoId, tagIds, session, request, TEliteSourceType.WRITE_WX);
    }

    @ResponseBody
    @RequestMapping(value = {"app/ask/question/update"}, produces = "application/json;charset=utf-8")
    public String appUpdate(String questionId,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content,
                         @RequestParam(value = "videoId", required = false) String videoId,
                         @RequestParam(value = "tagIds", required = false) String tagIds,
                         HttpSession session, HttpServletRequest request){
        return updateMethod(questionId, title, content, videoId, tagIds, session, request, null);
    }
    
    public String updateMethod(String questionId, String title, String content, String videoId, String tagIds, HttpSession session, HttpServletRequest request, TEliteSourceType source){
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(question == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("updating question <id={}, title={}, content={}, videoId={}, tagIds={}>, bpId={}", new Object[]{decodedQuestionId, title, content, videoId, tagIds, bpId});
        try {
            UserDetailDisplayBean user = bpUserService.getUserDetailByBpId(bpId, null);
            String decodedTagIds = TagUtil.decodeTagIds(tagIds);
            question.setBpId(IDUtil.decodeId(user.getBpId()))
                    .setTitle(ContentUtil.preDealContent(ContentUtil.subString(title, Constants.Title_length), false, true))
                    .setDetail(ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Text_length), true, false))
                    .setTagIds(decodedTagIds)
                    .setSource(null == source ? ((AgentUtil.getSource(request).getValue() == AgentSource.MOBILE.getValue()) ? TEliteSourceType.WRITE_MOBILE.getValue() : TEliteSourceType.WRITE_PC.getValue()) : source.getValue())
                    .setUpdateTime(System.currentTimeMillis())
                    .setUpdateHost(RequestUtil.getClientIPLong(request))
                    .setUpdatePort(RequestUtil.getClientPort(request))
                    .setStatus(EliteQuestionStatus.PUBLISHED.getValue());

            boolean updateResult = eliteAdapter.updateQuestion(question);
            log.info("update question result=" + updateResult);
            if(updateResult) {
                String ip = RealIpUtil.getRealIP(request);
            	List<EliteAsyncTaskOper> operList = new ArrayList<EliteAsyncTaskOper>();
            	operList.add(EliteAsyncTaskOper.RESAVECONTENT);
            	EliteAsyncTaskPool.addTask(new EliteContentAsyncTask(operList, question.getId(), BpType.Question, ip));
            	
                TaskEvent event = new TaskEvent(FeedActionType.UPDATE_QUESTION, BpType.Question, ProduceActionType.UPDATE, bpId, decodedQuestionId, 0, decodedTagIds);
                TaskEventCacheQueue.getInstance().push(event);

                if(StringUtils.isNotBlank(videoId)) {
                    List<TEliteMedia> medias = eliteAdapter.getMediaListByQuestionIdAndType(decodedQuestionId, TEliteMediaType.VIDEO);
                    boolean mediaExist = false;
                    if(medias != null && medias.size() > 0){
                        for(TEliteMedia media : medias){
                            if(media.getMediaGivenId() == Long.parseLong(videoId)){
                                mediaExist = true;
                                break;
                            }
                        }
                    }

                    if(medias == null || medias.size() == 0 || !mediaExist) {
                        log.info("inserting media for question <questionId={}, mediaGivenId={}>", IDUtil.decodeId(questionId), videoId);
                        TEliteMedia media = new TEliteMedia();
                        media.setQuestionId(decodedQuestionId)
                                .setMediaGivenId(Long.parseLong(videoId))
                                .setStatus(TEliteMediaStatus.WORK)
                                .setType(TEliteMediaType.VIDEO)
                                .setUploadTime(System.currentTimeMillis())
                                .setUploadHost(RequestUtil.getClientIPLong(request))
                                .setUploadPort(RequestUtil.getClientPort(request));
                        long mediaId = eliteAdapter.createMedia(media);
                        log.info("insert media result, id=" + mediaId);
                    }
                }
                
                //发送消息
                String inboxMessageContent = EliteMessageData.UPDATE_QUESTION_BY_MYFOLLOW.getContent()
                		.replace(Constants.MESSAGE_DATA_NICKNAME, user.getNick())
                		.replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                		.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getQuestionUri(questionId));
                List<Long> sourceIdList = new ArrayList<Long>();
                sourceIdList.add(question.getId());
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.UPDATE_QUESTION_BY_MYFOLLOW.getValue()).setInboxMessageContent(inboxMessageContent), 
                		null, EliteMessageTargetType.FANS, BpType.Question, sourceIdList));
//            	EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(EliteMessageType.INBOX, 
//                		messageTopic, messageContent, EliteMessageTargetType.FANS, BpType.Question, sourceIdList, null, null));
            	
            	JSONObject jsonObject = ResponseJSON.getSucJSON();
                JSONObject data = new JSONObject();
                data.put("id", questionId);
                jsonObject.put("data", data);
//                squareService.insertSquare(decodedQuestionId, FeedType.QUESTION);
                EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(decodedQuestionId, FeedType.QUESTION, EliteSquareOperationType.INSERT));
                //推送到推荐
//                ItemLog itemLog = RecLogHelper.createItemLog(ItemType.QUESTION, decodedQuestionId);
//                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(itemLog));
                return jsonObject.toString();
                
            }
        } catch (TException e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //更新问题-删除问题的视频
    @ResponseBody
    @RequestMapping(value = {"ask/question/deleteVideo"}, produces = "application/json;charset=utf-8")
    public String deleteVideo(String vId){
    	return deleteVideoMethod(vId);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/question/deleteVideo"}, produces = "application/json;charset=utf-8")
    public String wxDeleteVideo(String vId){
    	return deleteVideoMethod(vId);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/question/deleteVideo"}, produces = "application/json;charset=utf-8")
    public String appDeleteVideo(String vId){
        return deleteVideoMethod(vId);
    }
    
    public String deleteVideoMethod(String vId){
        if(StringUtils.isBlank(vId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long id = -1;
        try{
            id = IDUtil.decodeId(vId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(id == -1){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        log.info("deleting video for question, id=" + id);
        TEliteMedia media = new TEliteMedia();
        media.setId(id);
        try {
            boolean result = eliteAdapter.removeMedia(media);
            if(result) {
                log.info("delete question video succeeded");
                return ResponseJSON.getSucJSON().toString();
            }else {
                log.info("delete question video error");
            }
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //获取问题的回答列表
    @RequestMapping(value = {"ask/question/{questionId}/answers.html", "q/{questionId}"})
    public String getQuestionWithAnswers(@PathVariable("questionId") String questionId, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(questionId)){
            return Constants.PAGE_404;
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }
        if(EliteStatusUtil.isDelStatus(question)){
            return Constants.PAGE_404;
        }
        log.info("get answers for question(id={})", decodedQuestionId);
        
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (null != bpId && bpId > 0) {
            try {
                bean.setAdmin(eliteAdapter.getEliteAdminStatus(bpId));
            } catch (Exception e) {
                log.error("", e);
            }
        } else {
            bpId = 0L;
        }
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        fillQuestionAnswers(question, bean, bpId, source);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return EliteSpecialUtil.isVSType(question) == true ? "mobile/question/question-vs" : "mobile/question/question-answer";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	//填充广告素材
        	Map<Integer, String> adMap = new HashMap<Integer, String>();
        	adMap.put(EliteAdTypeBySite.PC_BOTTOM_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_BOTTOM_1));
        	adMap.put(EliteAdTypeBySite.PC_RIGHTSIDE_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_RIGHTSIDE_1));
            adMap.put(EliteAdTypeBySite.PC_HORIZONTAL_1.getElitePageSite(), adDisplayService.getAd(EliteAdTypeBySite.PC_HORIZONTAL_1));
        	bean.setAdMap(adMap);
        	
        	PageWrapperBean pageWrapper = new PageWrapperBean();
        	UserInfo userInfo = userInfoService.getUserInfoByBpid(bpId);
			pageWrapper.setHeaderHtml(wrapperPageService.getIndexHeaderHtml(userInfo, bean.getQuestion(), false));
			pageWrapper.setFooterHtml(wrapperPageService.getIndexFooterHtml());
			bean.setPageWrapper(pageWrapper);
            return EliteSpecialUtil.isVSType(question) == true ? "pc/question/question-vs" : "pc/question/question-answer";
        }else {
            return Constants.PAGE_404;
        }
    }
    
    //app获取问题详情页
    @RequestMapping("app/q/{questionId}")
    public String getAppQuestionsWithAnswer(@PathVariable("questionId") String questionId, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpSession session, ModelMap model, HttpServletRequest request) {
        /**
         * use blank template
         */
//        if(StringUtils.isBlank(questionId)){
//            return Constants.PAGE_404;
//        }
//
//        long decodedQuestionId = -1;
//        TEliteQuestion question = null;
//        try{
//            decodedQuestionId = IDUtil.decodeId(questionId);
//            question = eliteAdapter.getQuestionById(decodedQuestionId);
//            model.put("id", questionId);
//        }catch (Exception e){
//            log.error("", e);
//            return Constants.PAGE_404;
//        }
//        
//        if(StringUtils.isBlank(questionId)){
//            return Constants.PAGE_404;
//        }
//        return EliteSpecialUtil.isVSType(question) == true ? "app/question/question-vs" : "app/question/question";
        /**
         * use beetl render
         */
        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            model.put("id", questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return Constants.APP_PAGE_404;
        }
        if(EliteStatusUtil.isDelStatus(question)){
            return Constants.APP_PAGE_404;
        }
        log.info("get answers for question(id={})", decodedQuestionId);
        
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        fillQuestionAnswers(question, bean, bpId, source);
        
        return EliteSpecialUtil.isVSType(question) == true ? "app/question/question-vs" : "app/question/question";
    }

    //获取问题的回答列表Json数据
    @ResponseBody
    @RequestMapping(value = {"ask/question/{questionId}/answers.json", "q/{questionId}.json"}, produces = "application/json;charset=utf-8")
    public String getQuestionWithAnswersJsonData(@PathVariable("questionId") String questionId, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpServletRequest request, HttpSession session){
    	return getQuestionWithAnswerJsonDataMethod(questionId, bean, session, request);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/q/{questionId}.json"}, produces = "application/json;charset=utf-8")
    public String wxGetQuestionWithAnswersJsonData(@PathVariable("questionId") String questionId, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpServletRequest request, HttpSession session){
    	return getQuestionWithAnswerJsonDataMethod(questionId, bean, session, request);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/q/{questionId}.json"}, produces = "application/json;charset=utf-8")
    public String appGetQuestionWithAnswersJsonData(@PathVariable("questionId") String questionId, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpServletRequest request, HttpSession session){
        return getQuestionWithAnswerJsonDataMethod(questionId, bean, session, request);
    }
    
    public String getQuestionWithAnswerJsonDataMethod(String questionId, ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request){
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        log.info("get answers json data for question(id={})", decodedQuestionId);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        if(source == TEliteSourceType.WRITE_WX && EliteStatusUtil.isInvalidStatus(question)) return EliteResponseJSON.getQuestionNotExistError().toString();
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if(null == bpId || bpId <= 0)
        	bpId = 0L;
        boolean result = fillQuestionAnswers(question, bean, bpId, source);
        if(result) {
            JSONObject jsonObject = ResponseJSON.getSucJSON();
            jsonObject.put("data", bean);
            return jsonObject.toString();
        }else {
            return ResponseJSON.getErrorInternalJSON().toString();
        }
    }
    
    //关注
    @ResponseBody
    @RequestMapping(value = {"ask/question/follow"}, produces = "application/json;charset=utf-8")
    public String follow(String questionId, HttpServletRequest request, HttpSession session){
    	return followMethod(questionId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/question/follow"}, produces = "application/json;charset=utf-8")
    public String wxFollow(String questionId, HttpServletRequest request, HttpSession session){
    	return followMethod(questionId, request, session);
    }

    @ResponseBody
    @RequestMapping(value = {"app/ask/question/follow"}, produces = "application/json;charset=utf-8")
    public String appFollow(String questionId, HttpServletRequest request, HttpSession session){
        return followMethod(questionId, request, session);
    }
    
    public String followMethod(String questionId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
        }
        if(EliteStatusUtil.isInvalidStatus(question)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke Follow question <bpId={}, questionId={}>", bpId, decodedQuestionId);
        try {
            if (eliteAdapter.followQuestion(decodedQuestionId, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime(), true, true)) {
//                TaskEvent event = new TaskEvent(FeedActionType.FOLLOW, bpId, decodedQuestionId, -1, null);
//                TaskEventCacheQueue.getInstance().push(event);

                log.info("Follow succeeded");
                UserDetailDisplayBean user = bpUserService.getUserSimpleByBpId(bpId);
                JSONObject resJSON = ResponseJSON.getSucJSON();
                JSONObject dataJSON = new JSONObject();
                dataJSON.put("user", user);
                resJSON.put("data", dataJSON);
                return resJSON.toString();
            } else {
                log.info("Follow error");
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //取消关注
    @ResponseBody
    @RequestMapping(value = {"ask/question/unfollow"}, produces = "application/json;charset=utf-8")
    public String unFollow(String questionId, HttpServletRequest request, HttpSession session){
    	return unFollowMethod(questionId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/question/unfollow"}, produces = "application/json;charset=utf-8")
    public String wxUnFollow(String questionId, HttpServletRequest request, HttpSession session){
    	return unFollowMethod(questionId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/question/unfollow"}, produces = "application/json;charset=utf-8")
    public String appUnFollow(String questionId, HttpServletRequest request, HttpSession session){
        return unFollowMethod(questionId, request, session);
    }
    
    public String unFollowMethod(String questionId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(question == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke unFollow question <bpId={}, questionId={}>", bpId, decodedQuestionId);
        
        try {
            if (eliteAdapter.unfollowQuestion(decodedQuestionId, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime())) {
//                TaskEvent event = new TaskEvent(FeedActionType.UNFOLLOW, bpId, decodedQuestionId, -1, null);
//                TaskEventCacheQueue.getInstance().push(event);

                log.info("unFollow succeeded");
                feedService.removeCacheBackwardFeeds(TimeLineUtil.getComplexId(bpId, BpType.Elite_User.getValue()));
                return ResponseJSON.getSucJSON().toString();
            } else {
                log.info("unFollow error");
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //获取问题的tag
    @RequestMapping(value = {"ask/question/{questionId}/tags.html","q/{questionId}/tags"})
    public String getQuestionTags(@PathVariable("questionId") String questionId, HttpSession session, ModelMap model, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(questionId)){
            return Constants.PAGE_404;
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(EliteStatusUtil.isInvalidStatus(question)){
            return Constants.PAGE_404;
        }

        log.info("get tags for question, id=" + decodedQuestionId);
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        List<TagItemBean> tagList = ConvertUtil.convertToTagItemBeanList(question.getTagIds(), bpId);
        log.info("question tag size=" + tagList.size());

        model.put("tagList", tagList);
        return "mobile/tag/question-label";
    }
    
    //微信小程序获取问题tag的api
    @RequestMapping(value="wx/q/{questionId}/tags.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String wxGetQuestionTagsJSON(@PathVariable("questionId") String questionId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        return getQuestionTagsMethod(questionId, session);
    }
    
    @RequestMapping(value="app/q/{questionId}/tags.json", produces="application/json;charset=utf-8")
    @ResponseBody
    public String appGetQuestionTagsJSON(@PathVariable("questionId") String questionId, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        return getQuestionTagsMethod(questionId, session);
    }
    
    private String getQuestionTagsMethod(String questionId, HttpSession session) {
        if(StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try{
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        if(EliteStatusUtil.isInvalidStatus(question)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        log.info("get tags for question, id=" + decodedQuestionId);
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        List<TagItemBean> tagList = ConvertUtil.convertToTagItemBeanList(question.getTagIds(), bpId);
        log.info("question tag size=" + tagList.size());
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        JSONObject dataJSON = new JSONObject();
        dataJSON.put("tagList", tagList);
        resJSON.put("data", dataJSON);
        return resJSON.toString();
    }

    private boolean fillQuestionAnswers(TEliteQuestion question, ELiteQuestionAndAnswerListBean bean, Long viewerId, TEliteSourceType source){
        try {
            TSearchAnswerCondition condition = new TSearchAnswerCondition();
            condition.setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()))
                        .setSortField("updateTime")
                        .setSortType(SortType.DESC)
                        .setQuestionId(question.getId())
                        .setFrom((bean.getCurrPageNo()-1)*bean.getPageSize())
                        .setCount(bean.getPageSize());
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            log.info("total answers=" + listResult.getTotal());
            bean.setTotal(listResult.getTotal());
            bean.setQuestion(ConvertUtil.convert(question, viewerId, source));
            bean.setAnswered(bpUserService.checkHasAnswered(viewerId, question.getId()));
            int questionType = question.getSpecialType();
            bean.setSpecialType(questionType);
            if (questionType == BpType.Elite_Vote.getValue() || questionType == BpType.Elite_VS.getValue()) {
                Integer optionId = bpUserService.getChoosedOption(viewerId, question.getId());
                bean.setChoosedOption(null == optionId ? 0 : optionId);
            }
            bean.setHasPhoneNo(bpUserService.checkHasPhoneNo(viewerId));
            bean.setAnswerList(ConvertUtil.convertAnswerList(listResult.getAnswers(), viewerId, source));
            //添加默认头像
//            UserDetailDisplayBean user = bpUserService.getUserDetailByBpId(viewerId, null);
            if (TEliteSourceType.WRITE_PC == source || TEliteSourceType.WRITE_WX == source) {
                UserDetailDisplayBean user = new UserDetailDisplayBean();
                UserInfo userInfo = userInfoService.getUserInfoByBpid(viewerId);
                if (null == userInfo || StringUtils.isBlank(userInfo.getAvatar())) {
                    user.setAvatar(Constants.DEFAULT_AVATAR);
                    user.setNick(Constants.DEFAULT_NICK);
                } else {
                    user.setAvatar(userInfo.getAvatar());
                    user.setNick(userInfo.getNick());
                    user.setBpId(IDUtil.encodeId(viewerId));
                }
                bean.setUser(user);
            }
//            bean.setIsAnswerRestrict(userRestrictService.isAnswerRestrict(viewerId));
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
        return true;
    }
    
    //判断用户是否达到提问上限
    @RequestMapping(value = {"ask/question/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String isRestrict(HttpSession session){
    	return isRestrictMethod(session);
    }
    
    @RequestMapping(value = {"wx/ask/question/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxIsRestrict(HttpSession session){
    	return isRestrictMethod(session);
    }
    
    @RequestMapping(value = {"app/ask/question/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appIsRestrict(HttpSession session){
        return isRestrictMethod(session);
    }
    
    public String isRestrictMethod(HttpSession session){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
    	Boolean isRestrict = userRestrictService.isQuestionRestrict(bpId);
    	JSONObject data = new JSONObject();
    	data.put("isRestrict", isRestrict);
    	resJSON.put("data", data);
    	return resJSON.toString();
    }
}