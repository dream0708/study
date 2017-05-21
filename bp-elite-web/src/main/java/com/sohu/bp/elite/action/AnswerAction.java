package com.sohu.bp.elite.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.google.common.base.Objects;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.elite.action.bean.PageBean;
import com.sohu.bp.elite.action.bean.feedItem.SimpleFeedItemBean;
import com.sohu.bp.elite.action.bean.question.ELiteQuestionAndAnswerListBean;
import com.sohu.bp.elite.action.bean.question.EliteQuestionAnswerAndCommentListBean;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.bean.ContentBean;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.constants.SessionConstants;
import com.sohu.bp.elite.enums.AgentSource;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteAsyncTaskOper;
import com.sohu.bp.elite.enums.EliteMessageData;
import com.sohu.bp.elite.enums.EliteMessageTargetType;
import com.sohu.bp.elite.enums.EliteSendWechatTemplate;
import com.sohu.bp.elite.enums.EliteSquareOperationType;
import com.sohu.bp.elite.enums.FeedActionType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.enums.ProduceActionType;
import com.sohu.bp.elite.enums.SendCloudTemplate;
import com.sohu.bp.elite.filter.OverallDataFilter;
import com.sohu.bp.elite.model.SortType;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteMedia;
import com.sohu.bp.elite.model.TEliteMediaStatus;
import com.sohu.bp.elite.model.TEliteMediaType;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessageFrequenceType;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TEliteMessageTimePeriodType;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSourceType;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.BpUserService;
import com.sohu.bp.elite.service.web.EliteCacheService;
import com.sohu.bp.elite.service.web.ExpertTeamService;
import com.sohu.bp.elite.service.web.KafkaService;
import com.sohu.bp.elite.service.web.SquareService;
import com.sohu.bp.elite.service.web.UserRestrictService;
import com.sohu.bp.elite.service.web.WasherService;
import com.sohu.bp.elite.task.EliteAsyncTaskPool;
import com.sohu.bp.elite.task.EliteContentAsyncTask;
import com.sohu.bp.elite.task.EliteExpertTeamUpdateAsyncTask;
import com.sohu.bp.elite.task.EliteMessageDeliverAsyncTask;
import com.sohu.bp.elite.task.EliteRecAsyncTask;
import com.sohu.bp.elite.task.EliteSquareAsyncTask;
import com.sohu.bp.elite.task.TaskEvent;
import com.sohu.bp.elite.task.TaskEventCacheQueue;
import com.sohu.bp.elite.util.AgentUtil;
import com.sohu.bp.elite.util.BpUserUtil;
import com.sohu.bp.elite.util.ContentUtil;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.elite.util.EliteMessageUtil;
import com.sohu.bp.elite.util.EliteSpecialUtil;
import com.sohu.bp.elite.util.EliteStatusUtil;
import com.sohu.bp.elite.util.IDUtil;
import com.sohu.bp.elite.util.InteractionUtil;
import com.sohu.bp.elite.util.RequestUtil;
import com.sohu.bp.elite.util.SendCloudSmsUtil;
import com.sohu.bp.elite.util.ToolUtil;
import com.sohu.bp.elite.util.WechatUtil;
import com.sohu.bp.model.BpInteractionDetail;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.util.ResponseJSON;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import cn.focus.rec.collector.RecLogHelper;
import cn.focus.rec.enums.BehaviorType;
import cn.focus.rec.log.BehaviorLog;
import cn.focus.rec.model.ItemType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/9
 */
@Controller
//@RequestMapping({"answer", "a"})
public class AnswerAction {
    private static Logger log = LoggerFactory.getLogger(QuestionAction.class);

    private EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();

    @Resource
    private BpUserService bpUserService;
    @Resource
    private EliteCacheService eliteCacheService;
    @Resource
    private UserRestrictService userRestrictService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private SquareService squareService;
    @Resource
    private ExpertTeamService expertTeamService;
    @Resource
    private WasherService washerService;
    @Resource
    private KafkaService kafkaService;

    //跳转到回答页
    @RequestMapping(value = {"ask/answer/{questionId}/go.html", "a/{questionId}/go"})
    public String showAnswerPage(@PathVariable("questionId") String questionId, ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(questionId)){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try {
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }
        if(EliteStatusUtil.isInvalidStatus(question)){
            return Constants.PAGE_404;
        }

        model.put("question", ConvertUtil.convert(question, bpId, AgentUtil.getFeedSourceType(request)));
        log.info("show answer page, questionId=" + decodedQuestionId);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return question.getSpecialType() == BpType.Elite_VS.getValue() ? "mobile/pub/pub-vs-answer" :"mobile/pub/pub-answer";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
            return Constants.PAGE_INDEX;
        }else {
            return Constants.PAGE_404;
        }
    }
  
    //返回需要修改的回答信息
    @ResponseBody
    @RequestMapping(value = {"ask/answer/{answerId}/update.json", "a/{answerId}/up.json"}, produces = "application/json;charset=utf-8")
    public String getUpdateAnswerData(@PathVariable("answerId") String answerId ){
    	return getUpdateAnswerDataMehod(answerId, null);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/a/{answerId}/up.json", produces = "application/json;charset=utf-8")
    public String wxGetUpdateAnswerData(@PathVariable("answerId") String answerId){
    	return getUpdateAnswerDataMehod(answerId, TEliteSourceType.WRITE_WX);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/a/{answerId}/up.json", produces = "application/json;charset=utf-8")
    public String appGetUpdateAnswerData(@PathVariable("answerId") String answerId){
        return getUpdateAnswerDataMehod(answerId, TEliteSourceType.WRITE_APP);
    }
    
    public String getUpdateAnswerDataMehod(String answerId, TEliteSourceType source){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(answer == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        log.info("go update answer, answerId={}", decodedAnswerId);

        try {
            JSONObject jsonObject = ResponseJSON.getSucJSON();

            JSONObject jsonData = new JSONObject();
            TEliteQuestion question = eliteAdapter.getQuestionById(answer.getQuestionId());
            jsonData.put("title", question.getTitle());

            jsonData.put("answerId", answerId);
            if (null == source){
            	jsonData.put("content", ContentUtil.removeContentImageProtocol(answer.getContent()));
            } else {
            	ContentBean contentBean = ContentUtil.parseContent(answer.getContent(), source);
            	jsonData.put("content", contentBean.getPlainText());
            	jsonData.put("imgList", contentBean.getImgList());
            }

            List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(decodedAnswerId, TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0) {
                jsonData.put("videoId", medias.get(0).getMediaGivenId());
                jsonData.put("vId", IDUtil.encodeId(medias.get(0).getId()));
            }else {
                jsonData.put("videoId", 0);
                jsonData.put("vId", "");
            }
            jsonObject.put("data", jsonData);
            return jsonObject.toString();
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }
    

    //跳转到回答页，更新回答
    @RequestMapping(value = {"ask/answer/{answerId}/update.html","a/{answerId}/up"})
    public String showUpdateAnswerPage(@PathVariable("answerId") String answerId, ModelMap model, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(answerId)){
            return Constants.PAGE_404;
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(answer == null){
            return Constants.PAGE_404;
        }

        log.info("go update answer, answerId={}", decodedAnswerId);

        try {
            TEliteQuestion question = eliteAdapter.getQuestionById(answer.getQuestionId());
            model.put("title", question.getTitle());

            model.put("answerId", answerId);
            model.put("content", ContentUtil.removeContentImageProtocol(answer.getContent()));

            List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(decodedAnswerId, TEliteMediaType.VIDEO);
            if(medias != null && medias.size() > 0) {
                model.put("videoId", medias.get(0).getMediaGivenId());
                model.put("vId", IDUtil.encodeId(medias.get(0).getId()));
            }else {
                model.put("videoId", 0);
                model.put("vId", "");
            }
        } catch (TException e) {
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return "mobile/pub/pub-answer-update";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
            return Constants.PAGE_INDEX;
        }else {
            return Constants.PAGE_404;
        }

    }

    //跳转到评论页
    @RequestMapping(value = {"ask/answer/{answerId}/comment/go.html","a/{answerId}/com/go"})
    public String showCommentPage(@PathVariable("answerId") String answerId, ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(answerId)){
            return Constants.PAGE_404;
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(EliteStatusUtil.isInvalidStatus(answer)){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("show comment page, answerId=" + decodedAnswerId);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        model.put("answer", ConvertUtil.convert(answer, bpId, source));

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return "mobile/home-comment";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
            return Constants.PAGE_INDEX;
        }else {
            return Constants.PAGE_404;
        }
    }

    //发布回答
    @ResponseBody
    @RequestMapping(value = {"ask/answer/publish"}, produces = "application/json;charset=utf-8")
    public String publish(String questionId, String content, @RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session){
    	return publishMethod(questionId, content, videoId, request, session);
    }

    @ResponseBody
    @RequestMapping(value="wx/ask/answer/publish", produces = "application/json;charset=utf-8")
    public String wxPublish(String questionId, String content, @RequestParam(value = "videoId", required = false) String videoId, 
    		@RequestParam(value = "imgList", required = false) String[] imgList, HttpServletRequest request, HttpSession session){
    	if(null != imgList && imgList.length > 0){
    		if (null == content) content = "";
    		for(String imgUrl : imgList){
    			StringBuilder img = new StringBuilder(Constants.CONTENT_IMG_PATTERN);
    			img.insert(Constants.CONTENT_IMG_PATTERN.indexOf("src=") + "src=\"".length(), imgUrl);
    			content += img.toString();
    		}
    	}
    	return publishMethod(questionId, content, videoId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/publish"}, produces = "application/json;charset=utf-8")
    public String appPublish(String questionId, String content, @RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session){
        return publishMethod(questionId, content, videoId, request, session);
    }
        
    public String publishMethod(String questionId, String content, String videoId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(questionId)){
        	return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        try {
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        String errorCode =   BpUserUtil.bpUserValid(question, content, bpId);
        if(StringUtils.isNotBlank(errorCode)){	
        	jsonObject = ResponseJSON.getErrorInternalJSON();
        	jsonObject.put("errorCode", errorCode);
        	return jsonObject.toString();
        }
        log.info("inserting answer<questionId={}, content={}>", new Object[]{decodedQuestionId, content});
        try {
            TEliteAnswer answer = new TEliteAnswer();
            boolean flag = false;
            boolean hasChoosed = false;
            TEliteSourceType source = AgentUtil.getFeedSourceType(request);
            Long time = new Date().getTime();
            //判断是否为投票站队贴
            TAnswerListResult listResult = null;
            if (EliteSpecialUtil.isChooseType(question)) {
                TSearchAnswerCondition condition = new TSearchAnswerCondition();
                condition.setQuestionId(decodedQuestionId).setBpId(bpId).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.CHOOSE.getValue())).setFrom(0).setCount(1);
                listResult = eliteAdapter.searchAnswer(condition);
            }
            if (null != listResult && listResult.getTotal() > 0) {
                answer = listResult.getAnswers().get(0);
                answer.setContent(ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Text_length), true, false))
                .setStatus(EliteAnswerStatus.PUBLISHED.getValue()).setUpdateTime(time);
                flag = eliteAdapter.updateAnswer(answer);
                hasChoosed = true;
            } else {
                answer.setBpId(bpId)
                    .setQuestionId(decodedQuestionId)
                    .setSource(source.getValue())
                    .setContent(ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Text_length), true, false))
                    .setTagIds(question.getTagIds())
                    .setCreateTime(time)
                    .setUpdateTime(time)
                    .setPublishTime(time)
                    .setCreateHost(RequestUtil.getClientIPLong(request))
                    .setCreatePort(RequestUtil.getClientPort(request))
                    .setSpecialType(question.getSpecialType());
    
                long id = eliteAdapter.insertAnswer(answer);
                log.info("insert answer result, id=" + id);
                answer.setId(id);
                flag = id > 0;
            }
            if (flag) {
                long id = answer.getId();

            	//转存图片
            	List<EliteAsyncTaskOper> operList = new ArrayList<EliteAsyncTaskOper>();
            	operList.add(EliteAsyncTaskOper.RESAVECONTENT);
            	EliteAsyncTaskPool.addTask(new EliteContentAsyncTask(operList, id, BpType.Answer, ""));
            	
            	TaskEvent event = new TaskEvent(FeedActionType.PUBLISH_ANSWER_WITH_TAGS, BpType.Answer, ProduceActionType.ANSWER, bpId, decodedQuestionId, id, question.getTagIds());
            	if (Objects.equal(question.getSpecialType(), BpType.Elite_Vote.getValue())) {
            	    if (hasChoosed) {
            	        event.setActionType(ProduceActionType.VOTE_AND_ANSWER);
            	    } else {
            	        event.setActionType(ProduceActionType.ANSWER);
            	    }
            	} else if (Objects.equal(question.getSpecialType(), BpType.Elite_VS.getValue())) {
            	    if (hasChoosed) {
            	        event.setActionType(ProduceActionType.VS_AND_COMMENT);
            	    } else {
            	        event.setActionType(ProduceActionType.COMMENT);
            	    }
            	}
            	
                TaskEventCacheQueue.getInstance().push(event);
                eliteCacheService.clearTotalAnswerNumCache();
            	
                if(StringUtils.isNotBlank(videoId)) {
                    log.info("inserting media for answer <answerId={}, mediaGivenId={}>", id, videoId);
                    TEliteMedia media = new TEliteMedia();
                    media.setAnswerId(id)
                            .setStatus(TEliteMediaStatus.WORK)
                            .setType(TEliteMediaType.VIDEO)
                            .setUpdateTime(System.currentTimeMillis())
                            .setUploadTime(System.currentTimeMillis())
                            .setUploadHost(RequestUtil.getClientIPLong(request))
                            .setUploadPort(RequestUtil.getClientPort(request))
                            .setMediaGivenId(Long.parseLong(videoId));
                    long mediaId = eliteAdapter.createMedia(media);
                    log.info("insert media result, id=" + mediaId);
                }
                
                userRestrictService.addAnswerNum(bpId);
                
                //发送消息
                UserInfo userInfo  = userInfoService.getDecorateUserInfoByBpid(bpId);
                final Long questionBpId = question.getBpId();
                String encodedAnswerId = IDUtil.encodeId(id);
            	
            	String inboxMessageContent = EliteMessageData.NEW_ANSWER_BY_QUESTION.getContent()
            			.replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                		.replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                		.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getAnswerUri(encodedAnswerId));
            	List<Long> sourceIdList2 = new ArrayList<>();
                sourceIdList2.add(question.getId());
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.INBOX, new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_ANSWER_BY_QUESTION.getValue()).setInboxMessageContent(inboxMessageContent), 
                		null, EliteMessageTargetType.FANS, BpType.Question, sourceIdList2));
            	
            	inboxMessageContent = EliteMessageData.NEW_ANSWER_BY_MYQUESTION.getContent()
            			.replace(Constants.MESSAGE_DATA_NICKNAME, userInfo.getNick())
                		.replace(Constants.MESSAGE_DATA_QUESTIONTITLE, question.getTitle())
                		.replace(Constants.MESSAGE_DATA_JUMPURL, "https://"+ OverallDataFilter.askDomain + ToolUtil.getAnswerUri(encodedAnswerId));
                TEliteMessageStrategy strategy = new TEliteMessageStrategy().setTimePeriodType(TEliteMessageTimePeriodType.DAY_TIME).setFrequenceType(TEliteMessageFrequenceType.DAY).setFrequenceValue(1)
                		.setIdentity(EliteMessageUtil.getMessageIdentity(questionBpId, question.getId()));
            	List<Long> sourceIdList3 = new ArrayList<>();
                sourceIdList3.add(questionBpId);
                TEliteMessageData messageData = new TEliteMessageData().setInboxMessageDataValue(EliteMessageData.NEW_ANSWER_BY_MYQUESTION.getValue()).setInboxMessageContent(inboxMessageContent)
                		.setSendCloudTemplate(SendCloudTemplate.NEW_ANSWER_BYQUESTION.getValue()).setSendCloudVariables(new HashMap<String, String>(){{put(SendCloudSmsUtil.PARAM_QUESTION_URI, ToolUtil.getLoginAnswerUrl(encodedAnswerId, questionBpId));}})
                		.setWechatTemplateId(EliteSendWechatTemplate.NEW_ANSWER_BY_MYQUESTION.getValue()).setWechatUrl(WechatUtil.getWechatAnswerUrl(id)).setWechatData(WechatUtil.getHasNewAnswerMessage(userInfo.getNick(), question.getTitle(), new Date().getTime()));
                EliteAsyncTaskPool.addTask(new EliteMessageDeliverAsyncTask(TEliteMessagePushType.MEDIUM, messageData,
                		strategy, EliteMessageTargetType.SINGLE, BpType.Elite_User, sourceIdList3));
            	
//            	squareService.insertSquare(id, FeedType.ANSWER);
                EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(id, FeedType.ANSWER, EliteSquareOperationType.INSERT));
            	JSONObject dataJSON = new JSONObject();
            	dataJSON.put("answerId", id);
            	jsonObject.put("data", dataJSON);
            	
            	//更新专家团信息
            	if (expertTeamService.isExpert(bpId)) EliteAsyncTaskPool.addTask(new EliteExpertTeamUpdateAsyncTask(bpId, decodedQuestionId));
            	
            	//推送到推荐,当做评论来进行
                BehaviorLog bhv = RecLogHelper.commentBehaviorLog("FUV", answer.getQuestionId(), ItemType.QUESTION, answer.getBpId(), answer.getContent(), request);
            	EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
            	
                return jsonObject.toString();
            }
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    //用于投票，站队等特殊回答形式
    @RequestMapping(value = {"ask/answer/choose"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String choose(String questionId, HttpServletRequest request, HttpSession session, 
            @RequestParam(value = "specialId", required = false) Long specialId) {
        return chooseMethod(questionId, request, session, specialId);
    }
    
    @RequestMapping(value = {"wx/ask/answer/choose"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxChoose(String questionId, HttpServletRequest request, HttpSession session, Long specialId) {
        return chooseMethod(questionId, request, session, specialId);
    }
    
    @RequestMapping(value = {"app/ask/answer/choose"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appChoose(String questionId, HttpServletRequest request, HttpSession session, Long specialId) {
        return chooseMethod(questionId, request, session, specialId);
    }
    
    public String chooseMethod(String questionId, HttpServletRequest request, HttpSession session, Long specialId) {
        if (StringUtils.isBlank(questionId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        
        if (null == specialId) return ResponseJSON.getErrorParamsJSON().toString();
        long decodedQuestionId = -1;
        TEliteQuestion question = null;
        int specialType = 0;
        try {
            decodedQuestionId = IDUtil.decodeId(questionId);
            question = eliteAdapter.getQuestionById(decodedQuestionId);
            specialType = question.getSpecialType();
        } catch (Exception e) {
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        String errorCode = BpUserUtil.bpUserChooseValid(question, bpId);
        if(StringUtils.isNotBlank(errorCode)){  
            jsonObject = ResponseJSON.getErrorInternalJSON();
            jsonObject.put("errorCode", errorCode);
            return jsonObject.toString();
        }
        
        try {
            boolean hasAnswered = false;
            boolean result = false;
            long id = 0;
            TEliteAnswer answer = new TEliteAnswer();
            TSearchAnswerCondition condition = new TSearchAnswerCondition();
            condition.setBpId(bpId).setQuestionId(decodedQuestionId).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue(), EliteAnswerStatus.REJECTED.getValue())).setFrom(0).setCount(1);
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            if (null != listResult && listResult.getTotal() > 0) {
                answer = listResult.getAnswers().get(0);
                answer.setSpecialId(specialId).setSpecialType(specialType);
                result = eliteAdapter.updateAnswer(answer);
                id = answer.getId();
                hasAnswered = true;
                log.info("[CHOOSE] complete choice, questionId = {}, speicalType = {}, specialId = {}, answerId = {}, result = {}", 
                        new Object[]{decodedQuestionId, specialType, specialId, answer.getId(), result});
            } else {
                TEliteSourceType source = AgentUtil.getFeedSourceType(request);
                    answer.setBpId(bpId)
                        .setQuestionId(decodedQuestionId)
                        .setSource(source.getValue())
                        .setTagIds(question.getTagIds())
                        .setCreateTime(System.currentTimeMillis())
                        .setUpdateTime(System.currentTimeMillis())
                        .setPublishTime(System.currentTimeMillis())
                        .setCreateHost(RequestUtil.getClientIPLong(request))
                        .setCreatePort(RequestUtil.getClientPort(request))
                        .setStatus(EliteAnswerStatus.CHOOSE.getValue())
                        .setSpecialType(specialType).setSpecialId(specialId);
        
                    id = eliteAdapter.insertAnswer(answer);
                    log.info("[CHOOSE] insert choice, questionId = {}, specialType = {}, specialId = {}, answerId = {}" ,
                            new Object[]{decodedQuestionId, specialType, specialId, id});
                    result = id > 0;
            }
            
            if (result && id > 0) {
                JSONObject dataJSON = new JSONObject();
                String avatar = StringUtils.isBlank((String)session.getAttribute(SessionConstants.USER_AVATAR)) ? Constants.DEFAULT_AVATAR : (String)session.getAttribute(SessionConstants.USER_AVATAR);
                dataJSON.put("avatar", avatar);
                dataJSON.put("answerId", id);
                jsonObject.put("data", dataJSON);
                TaskEvent event = new TaskEvent(null, BpType.Answer, null, bpId, decodedQuestionId, id, question.getTagIds());
                if (Objects.equal(specialType, BpType.Elite_Vote.getValue())) {
                    if (hasAnswered) {
                        event.setType(FeedActionType.PUBLISH_ANSWER_WITH_TAGS).setActionType(ProduceActionType.VOTE_AND_ANSWER);
                    } else {
                        event.setType(FeedActionType.PUBLISH_ANSWER).setActionType(ProduceActionType.VOTE);
                    }
                } else {
                    if (hasAnswered) {
                        event.setType(FeedActionType.PUBLISH_ANSWER_WITH_TAGS).setActionType(ProduceActionType.VS_AND_COMMENT);
                    } else {
                        event.setType(FeedActionType.PUBLISH_ANSWER).setActionType(ProduceActionType.VS);
                    }
                }
                TaskEventCacheQueue.getInstance().push(event);
            }
            return jsonObject.toString();
        } catch (TException e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }
    //删除回答
    @ResponseBody
    @RequestMapping(value = {"ask/answer/delete"}, produces = "application/json;charset=utf-8")
    public String delete(String answerId, HttpServletRequest request, HttpSession session){
    	return deleteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/delete", produces = "application/json;charset=utf-8")
    public String wxDelete(String answerId, HttpServletRequest request, HttpSession session){
    	return deleteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/ask/answer/delete", produces = "application/json;charset=utf-8")
    public String appDelete(String answerId, HttpServletRequest request, HttpSession session){
        return deleteMethod(answerId, request, session);
    }
    
    public String deleteMethod(String answerId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(answerId))
            return ResponseJSON.getErrorParamsJSON().toString();

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(answer == null) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke deleting answer, answerId={}, bpId={}", decodedAnswerId, bpId);
        answer.setStatus(EliteAnswerStatus.DEL.getValue())
                .setUpdateTime(System.currentTimeMillis())
                .setUpdateHost(RequestUtil.getClientIPLong(request))
                .setUpdatePort(RequestUtil.getClientPort(request));

        try {
            boolean updateResult = eliteAdapter.updateAnswer(answer);
            log.info("delete answer result=" + updateResult);
            if(updateResult){
                TaskEvent event = new TaskEvent(FeedActionType.DELETE_ANSWER, BpType.Answer, null, bpId, 0, decodedAnswerId, "");
                TaskEventCacheQueue.getInstance().push(event);

                List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(decodedAnswerId, TEliteMediaType.VIDEO);
                if(medias != null && medias.size() > 0){
                    log.info("starting delete answer media");
                    boolean deleteMediaResult = false;
                    for(TEliteMedia media : medias){
                         deleteMediaResult = eliteAdapter.removeMedia(media);
                    }
                    log.info("delete answer media result=" + deleteMediaResult);
                }
//                squareService.reduceObject(decodedAnswerId, FeedType.ANSWER);
                EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(decodedAnswerId, FeedType.ANSWER, EliteSquareOperationType.REMOVE));
                
                //推送到推荐
//                ItemLog itemLog = RecLogHelper.createItemLog(ItemType.ANSWER, decodedAnswerId);
//                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(itemLog));
                return ResponseJSON.getSucJSON().toString();
            }
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //更新回答
    @ResponseBody
    @RequestMapping(value = {"ask/answer/update"}, produces = "application/json;charset=utf-8")
    public String update(String answerId, String content, @RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session){
    	return updateMethod(answerId, content, videoId, request, session, null);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/update", produces = "application/json;charset=utf-8")
    public String wxUpdate(String answerId, String content, @RequestParam(value = "videoId", required = false) String videoId, @RequestParam(value = "imgList", required = false) String[] imgList, HttpServletRequest request, HttpSession session){
    	if(null != imgList && imgList.length > 0){
    		if (null == content) content = "";
    		for(String imgUrl : imgList){
    			StringBuilder img = new StringBuilder(Constants.CONTENT_IMG_PATTERN);
    			img.insert(Constants.CONTENT_IMG_PATTERN.indexOf("src=") + "src=\"".length(), imgUrl);
    			content += img.toString();
    		}
    	}
    	return updateMethod(answerId, content, videoId, request, session, TEliteSourceType.WRITE_WX);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/update"}, produces = "application/json;charset=utf-8")
    public String appUpdate(String answerId, String content, @RequestParam(value = "videoId", required = false) String videoId, HttpServletRequest request, HttpSession session){
        return updateMethod(answerId, content, videoId, request, session, null);
    }
    
    
    public String updateMethod(String answerId, String content, String videoId, HttpServletRequest request, HttpSession session, TEliteSourceType source){
        if(StringUtils.isBlank(answerId) || StringUtils.isBlank(content)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(answer == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        log.info("updating answer <id={}, content={}, videoId={}>", new Object[]{decodedAnswerId, content, videoId});
        try {
            Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
            answer.setBpId(bpId)
                    .setContent(ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Text_length), true, false))
                    .setSource(source == null ? ((AgentUtil.getSource(request).getValue() == AgentSource.MOBILE.getValue()) ? TEliteSourceType.WRITE_MOBILE.getValue() : TEliteSourceType.WRITE_PC.getValue()) : source.getValue())
                    .setUpdateTime(System.currentTimeMillis())
                    .setUpdateHost(RequestUtil.getClientIPLong(request))
                    .setUpdatePort(RequestUtil.getClientPort(request))
                    .setStatus(EliteAnswerStatus.PUBLISHED.getValue());

            boolean updateResult = eliteAdapter.updateAnswer(answer);
            log.info("update answer result=" + updateResult);
            if(updateResult) {
            	List<EliteAsyncTaskOper> operList = new ArrayList<EliteAsyncTaskOper>();
            	operList.add(EliteAsyncTaskOper.RESAVECONTENT);
            	EliteAsyncTaskPool.addTask(new EliteContentAsyncTask(operList, answer.getId(), BpType.Answer, ""));
            	
                TaskEvent event = new TaskEvent(FeedActionType.UPDATE_ANSWER, BpType.Answer, ProduceActionType.UPDATE, bpId, answer.getQuestionId(), decodedAnswerId, "");
                TaskEventCacheQueue.getInstance().push(event);

                if(StringUtils.isNotBlank(videoId)) {
                    List<TEliteMedia> medias = eliteAdapter.getMediaListByAnswerIdAndType(decodedAnswerId, TEliteMediaType.VIDEO);
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
                        log.info("inserting media for answer <answerId={}, mediaGivenId={}>", new Object[]{decodedAnswerId, videoId});
                        TEliteMedia media = new TEliteMedia();
                        media.setAnswerId(decodedAnswerId)
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
                }
//                squareService.insertSquare(decodedAnswerId, FeedType.ANSWER);
                EliteAsyncTaskPool.addTask(new EliteSquareAsyncTask(decodedAnswerId, FeedType.ANSWER, EliteSquareOperationType.INSERT));
                
                //推送到推荐
//                ItemLog itemLog = RecLogHelper.createItemLog(ItemType.ANSWER, decodedAnswerId);
//                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(itemLog));
                return ResponseJSON.getSucJSON().toString();
            }
        } catch (TException e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //更新回答-删除回答的视频
    @ResponseBody
    @RequestMapping(value = {"ask/answer/deleteVideo"}, produces = "application/json;charset=utf-8")
    public String deleteVideo(String vId){
    	return deleteVideoMethod(vId);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/deleteVideo", produces = "application/json;charset=utf-8")
    public String wxDeleteVideo(String vId){
    	return deleteVideoMethod(vId);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/ask/answer/deleteVideo", produces = "application/json;charset=utf-8")
    public String appDeleteVideo(String vId){
        return deleteVideoMethod(vId);
    }
    
    public String deleteVideoMethod(String vId){
        if(StringUtils.isBlank(vId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long id = -1;
        try {
            id = IDUtil.decodeId(vId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(id == -1){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        log.info("deleting video for answer, id=" + id);
        TEliteMedia media = new TEliteMedia();
        media.setId(id);
        try {
            boolean result = eliteAdapter.removeMedia(media);
            if(result) {
                log.info("delete answer video succeeded");
                return ResponseJSON.getSucJSON().toString();
            }else {
                log.info("delete answer video error");
            }
        } catch (TException e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //获取回答和评论列表
    @RequestMapping(value = {"ask/answer/{answerId}/comments.html", "a/{answerId}"})
    public String getQuestionAnswerComments(@PathVariable("answerId") String answerId, @ModelAttribute("bean") EliteQuestionAnswerAndCommentListBean bean, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isBlank(answerId)){
            return Constants.PAGE_404;
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        TEliteQuestion question = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
            question = eliteAdapter.getQuestionById(answer.getQuestionId());
        }catch (Exception e){
            log.error("", e);
            return Constants.PAGE_404;
        }

        if(EliteStatusUtil.isInvalidStatus(answer)){
            return Constants.PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("get comments for answer <id={}>", decodedAnswerId);
        if (null != bpId && bpId.longValue() > 0) {
            try {
                bean.setAdmin(eliteAdapter.getEliteAdminStatus(bpId));
            } catch (Exception e) {
                log.error("", e);
            }
        } else {
            bpId = 0L;
        }
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        fillAnswerComments(answer, bean, bpId, question, source);

        if(AgentUtil.getSource(request) == AgentSource.MOBILE) {
            return "mobile/answer";
        }else if(AgentUtil.getSource(request) == AgentSource.PC){
        	return "redirect:http://" + OverallDataFilter.askDomain + "/q/" + IDUtil.encodeId(answer.getQuestionId())+"#"+IDUtil.encodeId(decodedAnswerId);
        }else {
            return Constants.PAGE_404;
        }
    }
    
    //获取App回答列表
    @RequestMapping(value = {"app/a/{answerId}"})
    public String getAppQuestionAnswerComments(@PathVariable("answerId") String answerId, @ModelAttribute("bean") EliteQuestionAnswerAndCommentListBean bean, HttpSession session, ModelMap model, HttpServletRequest request){
        /**
         * use for blankTemplate
         */
//        if(StringUtils.isBlank(answerId)) {
//            return Constants.PAGE_404;
//        }
//        try {
//            Long decodedAnswerId = IDUtil.decodeId(answerId);
//            TEliteAnswer answer = eliteAdapter.getAnswerById(decodedAnswerId);
//            model.put("id", answerId);
//        }catch (Exception e){
//            log.error("", e);
//            return Constants.PAGE_404;
//        }
//        
//        return "app/answer/answer";
        /**
         * use for beetl render
         */
        if(StringUtils.isBlank(answerId)) {
            return Constants.APP_PAGE_404;
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        TEliteQuestion question = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            model.put("id", answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
            question = eliteAdapter.getQuestionById(answer.getQuestionId());
        }catch (Exception e){
            log.error("", e);
            return Constants.APP_PAGE_404;
        }

        if(EliteStatusUtil.isInvalidStatus(answer)){
            return Constants.APP_PAGE_404;
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        fillAnswerComments(answer, bean, bpId, question, source);
        return "app/answer/answer";
    }
    
    //获取回答和评论列表的Json数据
    @ResponseBody
    @RequestMapping(value = {"ask/answer/{answerId}/comments.json", "a/{answerId}.json"}, produces = "application/json;charset=utf-8")
    public String getQuestionAnswerCommentsJsonData(@PathVariable("answerId") String answerId, @ModelAttribute("bean") EliteQuestionAnswerAndCommentListBean bean, HttpSession session, HttpServletRequest request){
        return getQuestionAnswerCommentsJsonDataMethod(answerId, bean, session, request);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/a/{answerId}.json"}, produces = "application/json;charset=utf-8")
    public String appGetQuestionAnswerCommentsJsonData(@PathVariable("answerId") String answerId, @ModelAttribute("bean") EliteQuestionAnswerAndCommentListBean bean, HttpSession session, HttpServletRequest request){
       return getQuestionAnswerCommentsJsonDataMethod(answerId, bean, session, request);
    }

    public String getQuestionAnswerCommentsJsonDataMethod(String answerId, EliteQuestionAnswerAndCommentListBean bean, HttpSession session, HttpServletRequest request){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        TEliteQuestion question = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
            question = eliteAdapter.getQuestionById(answer.getQuestionId());
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        if(EliteStatusUtil.isInvalidStatus(answer)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("get comments for answer <id={}>", decodedAnswerId);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        fillAnswerComments(answer, bean, bpId, question, source);

        JSONObject jsonObject = ResponseJSON.getSucJSON();
        jsonObject.put("data", bean);
        return jsonObject.toString();
    }

    //PC端 获取评论列表的Json数据
    @ResponseBody
    @RequestMapping(value = "ask/answer/{answerId}/commentsOnly.json", produces = "application/json;charset=utf-8")
    public String getCommentsJsonData(@PathVariable("answerId") String answerId, PageBean pageBean, HttpSession session){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("get comments data <answerId={}>", decodedAnswerId);
        List<SimpleFeedItemBean> comments = getCommentsData(decodedAnswerId, bpId, pageBean);
        JSONObject jsonObject = ResponseJSON.getSucJSON();
        JSONObject jsonData = new JSONObject();
        jsonData.put("commentList", comments);
        jsonData.putAll(JSONObject.fromObject(pageBean));
        jsonObject.put("data", jsonData);

        return jsonObject.toString();

    }
    
    //wx小程序，获取评论列表
    @ResponseBody
    @RequestMapping( value = "wx/{questionId}/commentsOnly.json", produces = "application/json;charset=utf-8")
    public String wxGetCommentsJsonData(@PathVariable("questionId") String questionId, PageBean page){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	Long decodedId = IDUtil.decodeId(questionId);
    	if(null == decodedId || decodedId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
    	JSONArray array = new JSONArray();
    	int start = (page.getCurrPageNo() - 1) * page.getPageSize();
    	int count  = page.getPageSize();
    	TSearchAnswerCondition condtion = new TSearchAnswerCondition().setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()))
    			.setSortField("updateTime").setSortType(SortType.DESC).setQuestionId(decodedId).setFrom(start).setCount(count);
    	try{
    		TAnswerListResult listResult = eliteAdapter.searchAnswer(condtion);
    		JSONObject commentJSON = new JSONObject();
    		List<TEliteAnswer> answers = listResult.getAnswers();
    		for(TEliteAnswer answer : answers){
    			commentJSON.put("answerId", IDUtil.encodeId(answer.getId()));
    			List<SimpleFeedItemBean> commentList = ConvertUtil.getWXCommentLists(answer.getId(), null);
    			commentJSON.put("commentList", commentList);
    			array.add(commentJSON);
    		}
    	} catch (Exception e){
    		log.error("", e);
    	}
    	resJSON.put("data", array);
    	return resJSON.toString();
    }
    

    //点赞
    @ResponseBody
    @RequestMapping(value = {"ask/answer/like"}, produces = "application/json;charset=utf-8")
    public String like(String answerId, HttpServletRequest request, HttpSession session){
    	return likeMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/like", produces = "application/json;charset=utf-8")
    public String wxLike(String answerId, HttpServletRequest request, HttpSession session){
    	return likeMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/ask/answer/like", produces = "application/json;charset=utf-8")
    public String appLike(String answerId, HttpServletRequest request, HttpSession session){
        return likeMethod(answerId, request, session);
    }
   
    public String likeMethod(String answerId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if (EliteStatusUtil.isInvalidStatus(answer)) {
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke Like answer <bpId={}, answerId={}>", bpId, decodedAnswerId);
        try {
            if (eliteAdapter.likeAnswer(decodedAnswerId, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime(), true, true)) {
//                TaskEvent event = new TaskEvent(FeedActionType.LIKE, bpId, -1, decodedAnswerId, null);
//                TaskEventCacheQueue.getInstance().push(event);
                log.info("Like succeeded");
                //推送到推荐
                BehaviorLog bhv = RecLogHelper.createBehaviorLog(BehaviorType.LIKE, "FUV", decodedAnswerId, ItemType.ANSWER, bpId, request);
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
                return ResponseJSON.getSucJSON().toString();
            } else {
                log.info("Like error");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //取消点赞
    @ResponseBody
    @RequestMapping(value = "ask/answer/unlike", produces = "application/json;charset=utf-8")
    public String unLike(String answerId, HttpServletRequest request, HttpSession session){
    	return unLikeMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/unlike", produces = "application/json;charset=utf-8")
    public String wxUnLike(String answerId, HttpServletRequest request, HttpSession session){
    	return unLikeMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/ask/answer/unlike", produces = "application/json;charset=utf-8")
    public String appUnLike(String answerId, HttpServletRequest request, HttpSession session){
        return unLikeMethod(answerId, request, session);
    }
    
    public String unLikeMethod(String answerId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(answer == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke unLike answer <bpId={}, answerId={}>", bpId, decodedAnswerId);
        
        try {
            if (eliteAdapter.unlikeAnswer(decodedAnswerId, bpId)) {
//                TaskEvent event = new TaskEvent(FeedActionType.UNLIKE, bpId, -1, decodedAnswerId, null);
//                TaskEventCacheQueue.getInstance().push(event);

                log.info("unLike succeeded");
                return ResponseJSON.getSucJSON().toString();
            }else {
                log.info("unLike error");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    //踩
    @ResponseBody
    @RequestMapping(value = {"ask/answer/tread"}, produces = "application/json;charset=utf-8")
    public String treadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return TreadAnswerMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/answer/tread"}, produces = "application/json;charset=utf-8")
    public String wxTreadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return TreadAnswerMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/tread"}, produces = "application/json;charset=utf-8")
    public String appTreadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return TreadAnswerMethod(answerId, request, session);
    }
    
    private  String TreadAnswerMethod(String answerId, HttpServletRequest request, HttpSession session) {
        if (StringUtils.isBlank(answerId)) return ResponseJSON.getErrorParamsJSON().toString();
        long decodedAnswerId = -1;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        try {
            boolean flag = eliteAdapter.treadAnswer(decodedAnswerId, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime());
            log.info("bpId = {} tread answerId= {}, result = {}", new Object[]{bpId, decodedAnswerId, flag});
            if (flag) {
                //推送到推荐
                BehaviorLog bhv = RecLogHelper.createBehaviorLog(BehaviorType.DISLIKE, "FUV", decodedAnswerId, ItemType.ANSWER, bpId, request);
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
                return ResponseJSON.getSucJSON().toString();
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }
    
    //取消踩
    @ResponseBody
    @RequestMapping(value = {"ask/answer/untread"}, produces = "application/json;charset=utf-8")
    public String untreadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return untreadAnswerMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/answer/untread"}, produces = "application/json;charset=utf-8")
    public String wxUntreadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return untreadAnswerMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/untread"}, produces = "application/json;charset=utf-8")
    public String appUntreadAnswer(String answerId, HttpServletRequest request, HttpSession session) {
        return untreadAnswerMethod(answerId, request, session);
    }
    
    private  String untreadAnswerMethod(String answerId, HttpServletRequest request, HttpSession session) {
        if (StringUtils.isBlank(answerId)) return ResponseJSON.getErrorParamsJSON().toString();
        long decodedAnswerId = -1;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        if (null == bpId || bpId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        try {
            boolean flag = eliteAdapter.untreadAnswer(decodedAnswerId, bpId);
            log.info("bpId = {} untread answerId= {}, result = {}", new Object[]{bpId, decodedAnswerId, flag});
            if (flag) return ResponseJSON.getSucJSON().toString();
        } catch (Exception e) {
            log.error("", e);
        }
        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //评论
    @ResponseBody
    @RequestMapping(value = {"ask/answer/comment"}, produces = "application/json;charset=utf-8")
    public String comment(String answerId, String content, HttpServletRequest request, HttpSession session){
    	return commentMethod(answerId, content, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "wx/ask/answer/comment", produces = "application/json;charset=utf-8")
    public String wxComment(String answerId, String content, HttpServletRequest request, HttpSession session){
    	return commentMethod(answerId, content, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = "app/ask/answer/comment", produces = "application/json;charset=utf-8")
    public String appComment(String answerId, String content, HttpServletRequest request, HttpSession session){
        return commentMethod(answerId, content, request, session);
    }
    
    public String commentMethod(String answerId, String content, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(answerId) || StringUtils.isBlank(content)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(EliteStatusUtil.isInvalidStatus(answer)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke Comment answer <bpId={}, answerId={}, content={}>", new Object[]{bpId, decodedAnswerId, content});

        String comment = ContentUtil.preDealContent(ContentUtil.subString(content, Constants.Comment_Length), false, false);
        StringBuilder commentSb = new StringBuilder(comment);
        washerService.resaveContent(commentSb, decodedAnswerId, BpType.Comment);

        try {
	        if (eliteAdapter.commentAnswer(decodedAnswerId, bpId, commentSb.toString(), RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), new Date().getTime(), true, true)) {
	//            TaskEvent event = new TaskEvent(FeedActionType.COMMENT, bpId, -1, decodedAnswerId, null);
	//            TaskEventCacheQueue.getInstance().push(event);
	            log.info("Comment succeeded");
	            UserInfo userInfo  = userInfoService.getDecorateUserInfoByBpid(bpId);
	
	            JSONObject data = new JSONObject();
	            data.put("user", userInfo);
	            JSONObject resJSON = ResponseJSON.getSucJSON();
	            resJSON.put("data", data);
	            
	            //推送到推荐
	            BehaviorLog bhv = RecLogHelper.commentBehaviorLog("FUV", decodedAnswerId, ItemType.ANSWER, bpId, commentSb.toString(), request);
	            EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
	 
	            return resJSON.toString();
	        } else {
	            log.info("Comment error");
	        }
        } catch (Exception e) {
        	log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //收藏
    @ResponseBody
    @RequestMapping(value = {"ask/answer/favorite"}, produces = "application/json;charset=utf-8")
    public String favorite(String answerId, HttpServletRequest request, HttpSession session){
    	return favoriteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/answer/favorite"}, produces = "application/json;charset=utf-8")
    public String wxFavorite(String answerId, HttpServletRequest request, HttpSession session){
    	return favoriteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/favorite"}, produces = "application/json;charset=utf-8")
    public String appFavorite(String answerId, HttpServletRequest request, HttpSession session){
        return favoriteMethod(answerId, request, session);
    }
  
    public String favoriteMethod(String answerId, HttpServletRequest request, HttpSession session){
  
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(EliteStatusUtil.isInvalidStatus(answer)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke Favorite answer <bpId={}, answerId={}>", bpId, decodedAnswerId);

        try {
            if (eliteAdapter.favoriteAnswer(decodedAnswerId, bpId, RequestUtil.getClientIPLong(request), RequestUtil.getClientPort(request), System.currentTimeMillis(), true, true)) {
//                TaskEvent event = new TaskEvent(FeedActionType.FAVORITE, bpId, -1, decodedAnswerId, null);
//                TaskEventCacheQueue.getInstance().push(event);
                
                log.info("Favorite succeeded");
                
                //发送到推荐
                BehaviorLog bhv = RecLogHelper.createBehaviorLog(BehaviorType.COLLECT, "FUV", decodedAnswerId, ItemType.ANSWER, bpId, request);
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
                return ResponseJSON.getSucJSON().toString();
            } else {
                log.info("Favorite error");
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    //取消收藏
    @ResponseBody
    @RequestMapping(value = {"ask/answer/unfavorite"}, produces = "application/json;charset=utf-8")
    public String unFavorite(String answerId, HttpServletRequest request, HttpSession session){
    	return unFavoriteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"wx/ask/answer/unfavorite"}, produces = "application/json;charset=utf-8")
    public String wxUnFavorite(String answerId, HttpServletRequest request, HttpSession session){
    	return unFavoriteMethod(answerId, request, session);
    }
    
    @ResponseBody
    @RequestMapping(value = {"app/ask/answer/unfavorite"}, produces = "application/json;charset=utf-8")
    public String appUnFavorite(String answerId, HttpServletRequest request, HttpSession session){
        return unFavoriteMethod(answerId, request, session);
    }
    
    public String unFavoriteMethod(String answerId, HttpServletRequest request, HttpSession session){
        if(StringUtils.isBlank(answerId)){
            return ResponseJSON.getErrorParamsJSON().toString();
        }

        long decodedAnswerId = -1;
        TEliteAnswer answer = null;
        try {
            decodedAnswerId = IDUtil.decodeId(answerId);
            answer = eliteAdapter.getAnswerById(decodedAnswerId);
        }catch (Exception e){
            log.error("", e);
            return ResponseJSON.getErrorInternalJSON().toString();
        }
        if(answer == null){
            return ResponseJSON.getErrorParamsJSON().toString();
        }
        
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        log.info("invoke unFavorite answer <bpId={}, answerId={}>", bpId, decodedAnswerId);
        
        try {
            if (eliteAdapter.unfavoriteAnswer(decodedAnswerId, bpId)) {
//                TaskEvent event = new TaskEvent(FeedActionType.UNFAVORITE, bpId, -1, decodedAnswerId, null);
//                TaskEventCacheQueue.getInstance().push(event);

                log.info("unFavorite succeeded");
                //推送到推荐
                BehaviorLog bhv = RecLogHelper.createBehaviorLog(BehaviorType.UNCOLLECT, "FUV", decodedAnswerId, ItemType.ANSWER, bpId, request);
                EliteAsyncTaskPool.addTask(new EliteRecAsyncTask(bhv));
                return ResponseJSON.getSucJSON().toString();
            } else {
                log.info("unFavorite error");
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return ResponseJSON.getErrorInternalJSON().toString();
    }

    private void fillAnswerComments(TEliteAnswer answer, EliteQuestionAnswerAndCommentListBean bean, Long bpId, TEliteQuestion question, TEliteSourceType source){
        List<SimpleFeedItemBean> comments = getCommentsData(answer.getId(), bpId, bean);
//        bean.setQuestion(ConvertUtil.convert(question, bpId, source));
        bean.setQuestion(ConvertUtil.convertSimple(question, source));
        bean.setAnswer(ConvertUtil.convert(answer, bpId, source));
        bean.setCommentList(comments);
    }

    private List<SimpleFeedItemBean> getCommentsData(Long answerId, Long bpId, PageBean bean){
        List<SimpleFeedItemBean> comments = new ArrayList<>();

        int total = InteractionUtil.getInteractionNumsForAnswer(answerId)[2];
        bean.setTotal(total);

        int start = (bean.getCurrPageNo()-1)*bean.getPageSize();
        int stop = start + bean.getPageSize();

        CodeMsgData responseData = extendServiceAdapter.getBpInteractionByTargetInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, BpInteractionType.COMMENT, start, stop);
        if(ResponseConstants.OK == responseData.getCode()){
            JSONArray data = JSONArray.fromObject(responseData.getData());
            log.info("comments data={}", data);
            if(data != null && data.size() > 0) {
                log.info("get comment size={}", data.size());

                for (int i = 0; i < data.size(); i++) {
                    long interactionId = data.getLong(i);
                    responseData = extendServiceAdapter.getBpInteraction(interactionId);
                    if(ResponseConstants.OK == responseData.getCode()){
                        BpInteractionDetail interactionDetail = JSON.parseObject(responseData.getData(), BpInteractionDetail.class);
                        SimpleFeedItemBean comment = new SimpleFeedItemBean();
                        comment.setItemId(IDUtil.encodeId(interactionId));
                        comment.setPublishTime(new Date(interactionDetail.getCreateTime()));
                        comment.setUpdateTime(interactionDetail.getUpdateTime());
                        comment.setContent(JSONObject.fromObject(interactionDetail.getExtra()).getString("data"));
//                        comment.setUser(bpUserService.getUserDetailByBpId(interactionDetail.getBpid(), bpId));
                        comment.setUser(bpUserService.getUserSimpleByBpId(interactionDetail.getBpid()));
                        comments.add(comment);
                    }
                }
            }
        }
        return comments;
    }

    //判断用户是否达到提问上限
    @RequestMapping(value = {"ask/answer/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String isRestrict(HttpSession session){
    	return isRestrictMethod(session);
    }
    
    @RequestMapping(value = {"wx/ask/answer/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxIsRestrict(HttpSession session){
    	return isRestrictMethod(session);
    }
    
    @RequestMapping(value = {"app/ask/answer/isRestrict"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appIsRestrict(HttpSession session){
        return isRestrictMethod(session);
    }
    
    public String isRestrictMethod(HttpSession session){
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
    	Boolean isRestrict = userRestrictService.isAnswerRestrict(bpId);
    	JSONObject data = new JSONObject();
    	data.put("isRestrict", isRestrict);
    	resJSON.put("data", data);
    	return resJSON.toString();
    }
    //给wap端接口，查看同一问题的回答数
    @RequestMapping(value = {"ask/answer/answerNum"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String answerNum(@RequestParam(value = "questionId", required = true)String questionEncodedId){
    	return answerNumMethod(questionEncodedId);
    }
    
    @RequestMapping(value = {"wx/ask/answer/answerNum"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxAnswerNum(@RequestParam(value = "questionId", required = true)String questionEncodedId){
    	return answerNumMethod(questionEncodedId);
    }
    
    @RequestMapping(value = {"app/ask/answer/answerNum"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appAnswerNum(@RequestParam(value = "questionId", required = true)String questionEncodedId){
        return answerNumMethod(questionEncodedId);
    }
    
    public String answerNumMethod(String questionEncodedId){
    	Long id = IDUtil.decodeId(questionEncodedId);
    	JSONObject resJSON = ResponseJSON.getDefaultResJSON();
    	int[] nums = InteractionUtil.getInteractionNumsForQuestion(id);
    	JSONObject dataJSON = new JSONObject();
    	dataJSON.put("answerNum", nums[1]);
    	resJSON.put("data", dataJSON);
    	return resJSON.toString();
    }
    
    //获取晒图等特殊问题的不同态度回答
    @RequestMapping(value = {"ask/question/{questionId}/options.json"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getOptionAnswers(@PathVariable("questionId") String id, @RequestParam(value = "type", required = false) Integer type, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request) {
        return getOptionAnswersMethod(id, type, bean, session, request);
    }
    
    @RequestMapping(value = {"wx/ask/question/{questionId}/options.json"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxGetOptionAnswers(@PathVariable("questionId") String id, @RequestParam(value = "type", required = false) Integer type, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request) {
        return getOptionAnswersMethod(id, type, bean, session, request);
    }
    
    @RequestMapping(value = {"app/ask/question/{questionId}/options.json"}, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String appGetOptionAnswers(@PathVariable("questionId") String id, @RequestParam(value = "type", required = false) Integer type, @ModelAttribute("bean") ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request) {
        return getOptionAnswersMethod(id, type, bean, session, request);
    }
    
    private String getOptionAnswersMethod(String id, Integer type, ELiteQuestionAndAnswerListBean bean, HttpSession session, HttpServletRequest request) {
        JSONObject resJSON = ResponseJSON.getDefaultResJSON();
        Long questionId = IDUtil.decodeId(id);
        Long bpId = (Long) session.getAttribute(SessionConstants.USER_ID);
        TEliteSourceType source = AgentUtil.getFeedSourceType(request);
        if (null == questionId || questionId <= 0) return ResponseJSON.getErrorParamsJSON().toString();
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        int start = (bean.getCurrPageNo() - 1) * bean.getPageSize();
        condition.setQuestionId(questionId).setFrom(start).setCount(bean.getPageSize()).setSortField("updateTime").setSortType(SortType.DESC)
        .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
        if (null != type && type >= 0) {
            condition.setSpecialId(type);
        }
        try {
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            bean.setTotal(listResult.getTotal());
            bean.setAnswerList(ConvertUtil.convertAnswerList(listResult.getAnswers(), bpId, source));
        } catch (Exception e) {
            log.error("", e);
        }
        resJSON.put("data", bean);
        return resJSON.toString();
    }
}
