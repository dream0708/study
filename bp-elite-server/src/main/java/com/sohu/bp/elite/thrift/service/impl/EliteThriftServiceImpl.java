package com.sohu.bp.elite.thrift.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtil;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.elite.bean.EliteSearchResultBean;
import com.sohu.bp.elite.bean.SearchGlobalListResult;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteFollowType;
import com.sohu.bp.elite.enums.EliteNotifyType;
import com.sohu.bp.elite.enums.FeedType;
import com.sohu.bp.elite.model.TAnswerIdListResult;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TColumnListResult;
import com.sohu.bp.elite.model.TEliteAdmin;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TEliteColumn;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteExpertTeamInfo;
import com.sohu.bp.elite.model.TEliteFollow;
import com.sohu.bp.elite.model.TEliteFollowType;
import com.sohu.bp.elite.model.TEliteFragment;
import com.sohu.bp.elite.model.TEliteFragmentType;
import com.sohu.bp.elite.model.TEliteMedia;
import com.sohu.bp.elite.model.TEliteMediaType;
import com.sohu.bp.elite.model.TEliteMessageData;
import com.sohu.bp.elite.model.TEliteMessagePushType;
import com.sohu.bp.elite.model.TEliteMessageStrategy;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TEliteSquareItem;
import com.sohu.bp.elite.model.TEliteSubject;
import com.sohu.bp.elite.model.TEliteTopic;
import com.sohu.bp.elite.model.TEliteUser;
import com.sohu.bp.elite.model.TExpertTeamListResult;
import com.sohu.bp.elite.model.TQuestionIdListResult;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchExpertTeamCondition;
import com.sohu.bp.elite.model.TSearchGlobalCondition;
import com.sohu.bp.elite.model.TSearchGlobalListResult;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TSubjectListResult;
import com.sohu.bp.elite.model.TUserIdListResult;
import com.sohu.bp.elite.model.TUserListResult;
import com.sohu.bp.elite.persistence.EliteAdmin;
import com.sohu.bp.elite.persistence.EliteAnswer;
import com.sohu.bp.elite.persistence.EliteColumn;
import com.sohu.bp.elite.persistence.EliteExpertTeam;
import com.sohu.bp.elite.persistence.EliteExpertTeamInfo;
import com.sohu.bp.elite.persistence.EliteFollow;
import com.sohu.bp.elite.persistence.EliteFragment;
import com.sohu.bp.elite.persistence.EliteMedia;
import com.sohu.bp.elite.persistence.EliteQuestion;
import com.sohu.bp.elite.persistence.EliteSubject;
import com.sohu.bp.elite.persistence.EliteTopic;
import com.sohu.bp.elite.persistence.EliteUser;
import com.sohu.bp.elite.service.EliteAdminService;
import com.sohu.bp.elite.service.EliteAnswerService;
import com.sohu.bp.elite.service.EliteColumnService;
import com.sohu.bp.elite.service.EliteExpertTeamInfoService;
import com.sohu.bp.elite.service.EliteExpertTeamService;
import com.sohu.bp.elite.service.EliteFollowService;
import com.sohu.bp.elite.service.EliteFragmentService;
import com.sohu.bp.elite.service.EliteMediaService;
import com.sohu.bp.elite.service.EliteQuestionService;
import com.sohu.bp.elite.service.EliteSearchService;
import com.sohu.bp.elite.service.EliteSquareService;
import com.sohu.bp.elite.service.EliteSubjectService;
import com.sohu.bp.elite.service.EliteThriftService;
import com.sohu.bp.elite.service.EliteTopicService;
import com.sohu.bp.elite.service.EliteUserActionService;
import com.sohu.bp.elite.service.EliteUserService;
import com.sohu.bp.elite.service.NotifyService;
import com.sohu.bp.elite.util.ConvertUtil;
import com.sohu.bp.service.adapter.BpServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;

import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/6/3
 */
public class EliteThriftServiceImpl implements EliteThriftService.Iface {

    private Logger log = LoggerFactory.getLogger(EliteThriftServiceImpl.class);
    private BpServiceAdapter bpServiceAdapter = BpServiceAdapterFactory.getBpServiceAdapter();

    private EliteQuestionService questionService;
    private EliteAnswerService answerService;
    private EliteUserService userService;
    private EliteSearchService searchService;
    private EliteSubjectService subjectService;
    private EliteTopicService topicService;
    private EliteMediaService mediaService;
    private EliteFragmentService eliteFragmentService;
    private EliteFollowService eliteFollowService;
    private NotifyService notifyService;
    private EliteColumnService columnService;
    private EliteExpertTeamService expertTeamService;
    private EliteExpertTeamInfoService expertTeamInfoService;
    private EliteAdminService eliteAdminService;
    private EliteUserActionService userActionService;
    private EliteSquareService squareService;
    
	public NotifyService getNotifyService() {
		return notifyService;
	}
	
	public void setColumnService(EliteColumnService columnService) {
		this.columnService = columnService;
	}
	
	public void setNotifyService(NotifyService notifyService) {
		this.notifyService = notifyService;
	}
	
    public void setQuestionService(EliteQuestionService questionService) {
        this.questionService = questionService;
    }

    public void setAnswerService(EliteAnswerService answerService) {
        this.answerService = answerService;
    }

    public void setUserService(EliteUserService userService) {
        this.userService = userService;
    }

    public void setSearchService(EliteSearchService searchService) {
        this.searchService = searchService;
    }
    
    public void setSubjectService(EliteSubjectService subjectService) {
        this.subjectService = subjectService;
    }
    
    public void setTopicService(EliteTopicService topicService) {
        this.topicService = topicService;
    }
    
    public void setEliteFollowService(EliteFollowService eliteFollowService) {
		this.eliteFollowService = eliteFollowService;
	}
    
    public void setExpertTeamInfoService(EliteExpertTeamInfoService expertTeamInfoService) {
        this.expertTeamInfoService = expertTeamInfoService;
    }
    
    public void setExpertTeamService(EliteExpertTeamService expertTeamService) {
        this.expertTeamService = expertTeamService;
    }

	public EliteMediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(EliteMediaService mediaService) {
		this.mediaService = mediaService;
	}
	
	public EliteFragmentService getEliteFragmentService() {
		return eliteFragmentService;
	}

	public void setEliteFragmentService(EliteFragmentService eliteFragmentService) {
		this.eliteFragmentService = eliteFragmentService;
	}

    public EliteAdminService getEliteAdminService() {
        return eliteAdminService;
    }

    public void setEliteAdminService(EliteAdminService eliteAdminService) {
        this.eliteAdminService = eliteAdminService;
    }
    
    public void setUserActionService(EliteUserActionService userActionService) {
        this.userActionService = userActionService;
    }
    
    public void setSquareService(EliteSquareService squareService) {
        this.squareService = squareService;
    }

    @Override
    public long insertQuestion(TEliteQuestion question) throws TException {
        return questionService.insert(ConvertUtil.convert(question), true);
    }
    
    @Override
    public long insertQuestionWithOptions(TEliteQuestion question, boolean securityFlag) throws TException {
        return questionService.insert(ConvertUtil.convert(question), securityFlag);
    }

    @Override
    public boolean updateQuestion(TEliteQuestion question) throws TException {
        return questionService.update(ConvertUtil.convert(question));
    }

    @Override
    public TEliteQuestion getQuestionById(long questionId) throws TException {
        return ConvertUtil.convert(questionService.getById(questionId));
    }

    @Override
    public List<TEliteQuestion> getQuestionsByIds(List<Long> questionIds) throws TException {
        ListResult listResult = questionService.getByIds(questionIds);
        List<TEliteQuestion> questions =  new ArrayList<>();
        if(listResult != null && listResult.getTotal() > 0){
            for(Object obj : listResult.getEntities()){
                questions.add(ConvertUtil.convert((EliteQuestion) obj));
            }
        }
        return questions;
    }

    @Override
    public boolean batchAuditQuestion(List<Long> passQuestionIds, List<Long> rejectedQuestionIds) throws TException {
        return questionService.batchAudit(passQuestionIds, rejectedQuestionIds);
    }

    @Override
    public boolean passOneQuestion(long questionId) throws TException {
        return questionService.pass(questionId);
    }

    @Override
    public boolean rejectOneQuestion(long questionId) throws TException {
        return questionService.reject(questionId);
    }

    @Override
    public boolean deleteOneQuestion(long questionId) throws TException {
        return questionService.userDelete(questionId);
    }

    @Override
    public boolean sysDeleteOneQuestion(long questionId) throws TException {
        return questionService.sysDelete(questionId);
    }

    @Override
    public TQuestionListResult getAuditingQuestions(int start, int count) throws TException {
        TQuestionListResult tQuestionListResult = new TQuestionListResult(new ArrayList<>(), 0);
        ListResult listResult = questionService.getAuditingQuestions(start, count);
        if(listResult != null && listResult.getTotal() > 0){
            tQuestionListResult.setTotal(listResult.getTotal());
            List<TEliteQuestion> tEliteQuestions = new ArrayList<>();
            for(Object obj : listResult.getEntities()){
                tEliteQuestions.add(ConvertUtil.convert((EliteQuestion) obj));
            }
            tQuestionListResult.setQuestions(tEliteQuestions);
        }
        return tQuestionListResult;
    }

    @Override
    public List<TEliteQuestion> getQuestionsByBpId(long bpId) throws TException {
        List<EliteQuestion>  questions = questionService.getQuestionsByBpId(bpId);
        List<TEliteQuestion> tEliteQuestions = new ArrayList<>();
        if(questions != null && questions.size() > 0){
            for(EliteQuestion obj : questions){
                tEliteQuestions.add(ConvertUtil.convert(obj));
            }
        }
        return tEliteQuestions;
    }

    @Override
    public long insertAnswer(TEliteAnswer answer) throws TException {
        return answerService.insert(ConvertUtil.convert(answer), true);
    }
    
    @Override
    public long insertAnswerWithOptions(TEliteAnswer answer, boolean securityFlag) throws TException {
        return answerService.insert(ConvertUtil.convert(answer), securityFlag);
    }

    @Override
    public boolean updateAnswer(TEliteAnswer answer) throws TException {
        return answerService.update(ConvertUtil.convert(answer));
    }

    @Override
    public TEliteAnswer getAnswerById(long answerId) throws TException {
        return ConvertUtil.convert(answerService.getById(answerId));
    }

    @Override
    public List<TEliteAnswer> getAnswersByQuestionId(long questionId) throws TException {
        List<EliteAnswer> answers = answerService.getAnswersByQuestionId(questionId);
        List<TEliteAnswer> tEliteAnswers = new ArrayList<>();
        if(answers != null && answers.size() > 0){
            for(EliteAnswer obj : answers){
                tEliteAnswers.add(ConvertUtil.convert(obj));
            }
        }
        return tEliteAnswers;
    }

    @Override
    public List<TEliteAnswer> getAnswersByBpId(long bpId) throws TException {
        List<EliteAnswer> answers = answerService.getAnswersByBpId(bpId);
        List<TEliteAnswer> tEliteAnswers = new ArrayList<>();
        if(answers != null && answers.size() > 0){
            for(EliteAnswer obj : answers){
                tEliteAnswers.add(ConvertUtil.convert(obj));
            }
        }
        return tEliteAnswers;
    }

    @Override
    public int getUserAnswerNumByStatus(long bpId, List<Integer> statusList) throws TException {
        return answerService.getUserAnswerNumByStatus(bpId, statusList);
    }

    @Override
    public int getQuestionAnswerNumByStatus(long questionId, List<Integer> statusList) throws TException {
        return answerService.getQuestionAnswerNumByStatus(questionId, statusList);
    }

    @Override
    public boolean batchAuditAnswer(List<Long> passAnswerIds, List<Long> rejectedAnswerIds) throws TException {
        return answerService.batchAudit(passAnswerIds, rejectedAnswerIds);
    }

    @Override
    public boolean passOneAnswer(long answerId) throws TException {
        return answerService.pass(answerId);
    }

    @Override
    public boolean rejectOneAnswer(long answerId) throws TException {
        return answerService.reject(answerId);
    }

    @Override
    public boolean deleteOneAnswer(long answerId) throws TException {
        return answerService.userDelete(answerId);
    }

    @Override
    public boolean sysDeleteOneAnswer(long answerId) throws TException {
        return answerService.sysDelete(answerId);
    }

    @Override
    public TAnswerListResult getAuditingAnswers(int start, int count) throws TException {
        TAnswerListResult tAnswerListResult = new TAnswerListResult(new ArrayList<>(), 0);
        ListResult listResult = answerService.getAuditingAnswers(start, count);
        if(listResult != null && listResult.getTotal() > 0){
            List<TEliteAnswer> tEliteAnswers = new ArrayList<>();
            for(Object obj : listResult.getEntities()){
                tEliteAnswers.add(ConvertUtil.convert((EliteAnswer) obj));
            }
            tAnswerListResult.setTotal(listResult.getTotal());
            tAnswerListResult.setAnswers(tEliteAnswers);
        }
        return tAnswerListResult;
    }

    @Override
    public long insertUser(TEliteUser user) throws TException {
        return userService.save(ConvertUtil.convert(user));
    }

    @Override
    public boolean updateUser(TEliteUser user) throws TException {
        return userService.update(ConvertUtil.convert(user));
    }

    @Override
    public TEliteUser getUserByBpId(long bpId) throws TException {
        return ConvertUtil.convert(userService.get(bpId));
    }

    @Override
    public TQuestionListResult searchQuestion(TSearchQuestionCondition condition) throws TException {
        List<TEliteQuestion> questions = new ArrayList<TEliteQuestion>();
        TQuestionListResult questionListResult = new TQuestionListResult(questions, 0);
        ListResult listResult = searchService.searchQuestion(condition);
        if (listResult != null && listResult.getTotal() > 0) {
            EliteQuestion question;
            Long id = 0L;
            for (Object ele : listResult.getEntities()) {
                if (ele instanceof EliteSearchResultBean) {
                    EliteSearchResultBean bean = (EliteSearchResultBean) ele;
                    id = bean.getId();
                    question = questionService.getById(id);
                    if (question != null) {
                        TEliteQuestion tquestion = ConvertUtil.convert(question);
                        tquestion.setHighlightText(bean.getHighlightText());
                        tquestion.setHighlightWords(bean.getHighlightWord());
                        questions.add(tquestion);
                    } else {
                        log.error("question exists in ES, but not in DB, questionId=" + id);
                        notifyService.notify2Statistic(id, BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                    }
                } else if (ele instanceof Long) {
                    id = (Long) ele;
                    question = questionService.getById(id);
                    if (question != null) {
                        TEliteQuestion tquestion = ConvertUtil.convert(question);
                        questions.add(tquestion);
                    } else {
                        log.error("question exists in ES, but not in DB, questionId=" + id);
                        notifyService.notify2Statistic(id, BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                    }
                }
            }
            questionListResult.setTotal(listResult.getTotal());
            questionListResult.setQuestions(questions);
        }
        return questionListResult;
    }

    @Override
    public TQuestionIdListResult searchQuestionId(TSearchQuestionCondition condition) throws TException {
        ListResult list = searchService.searchQuestionId(condition);
        List<Long> ids = new ArrayList<Long>();
        if (null != list.getEntities() && list.getEntities().size() > 0) {
            list.getEntities().forEach(object -> ids.add((Long)object));
        }
        TQuestionIdListResult listResult = new TQuestionIdListResult(ids, list.getTotal());
        return listResult;
    }

    @Override
    public TAnswerListResult searchAnswer(TSearchAnswerCondition condition) throws TException {
        List<TEliteAnswer> answers = new ArrayList<TEliteAnswer>();
        TAnswerListResult answerListResult = new TAnswerListResult(answers, 0);
        ListResult listResult = searchService.searchAnswer(condition);
        if(listResult != null && listResult.getTotal() > 0){
            EliteAnswer answer;
            Long id = 0L;
            for (Object ele : listResult.getEntities()) {
                if (ele instanceof EliteSearchResultBean) {
                    EliteSearchResultBean bean = (EliteSearchResultBean) ele;
                    id = bean.getId();
                    answer = answerService.getById(id);
                    if (answer != null) {
                        TEliteAnswer tanswer = ConvertUtil.convert(answer);
                        tanswer.setHighlightText(bean.getHighlightText());
                        tanswer.setHighlightWords(bean.getHighlightWord());
                        answers.add(tanswer);
                    } else {
                        log.error("answer exists in ES, but not in DB, questionId=" + id);
                        notifyService.notify2Statistic(id, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                    }
                } else if (ele instanceof Long) {
                    id = (Long) ele;
                    answer = answerService.getById(id);
                    if (answer != null) {
                        TEliteAnswer tanswer = ConvertUtil.convert(answer);
                        answers.add(tanswer);
                    } else {
                        log.error("answer exists in ES, but not in DB, questionId=" + id);
                        notifyService.notify2Statistic(id, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                    }
                }
            }
            answerListResult.setTotal(listResult.getTotal());
            answerListResult.setAnswers(answers);
        }

        return answerListResult;
    }
    
    @Override
    public TAnswerIdListResult searchAnswerId(TSearchAnswerCondition condition) throws TException {
        ListResult listResult = searchService.searchAnswerId(condition);
        TAnswerIdListResult answerIdListResult = new TAnswerIdListResult(new ArrayList<Long>(), 0);
        if (null != listResult && listResult.getTotal() > 0) {
            List<Long> ids = new ArrayList<Long>();
            listResult.getEntities().forEach(object -> ids.add((Long)object));
            answerIdListResult.setAnswerIds(ids);
            answerIdListResult.setTotal(listResult.getTotal());
        }
        return answerIdListResult;
    }

    @Override
    public TUserListResult searchUser(TSearchUserCondition condition) throws TException {
        List<TEliteUser> users = new ArrayList<>();
        TUserListResult userListResult = new TUserListResult(users, 0);
        ListResult listResult = searchService.searchUser(condition);
        if(listResult != null && listResult.getTotal() > 0){
            for(Object ele : listResult.getEntities()){
                TEliteUser user = new TEliteUser();
                Long bpId = 0L;
                if (ele instanceof EliteSearchResultBean) {
                    EliteSearchResultBean bean = (EliteSearchResultBean) ele;
                    bpId = bean.getId();
                    user.setHighlightText(bean.getHighlightText()).setHighlightWords(bean.getHighlightWord());
                } else if (ele instanceof Long) {
                    bpId = (Long) ele;
                }
                try {
                    CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
                    if(codeMsgData.getCode() == ResponseConstants.OK){
                        log.info("get userInfo for search <bpId={}>", bpId);
                        String infoJSONStr = codeMsgData.getData();
                        if(StringUtils.isNotBlank(infoJSONStr)) {
                            JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
                            if (null != infoJSON) {
                                user.setBpId(bpId).setNick(infoJSON.getString("nick"));
                                try{
                                EliteUser eliteUser = userService.get(bpId);
                                if(null != eliteUser){
	                                BeanUtil.copyProperties(eliteUser, user, "nick","bpId","firstLoginTime","lastLoginTime","birthday");
	                                if(null != eliteUser.getBirthday()) user.setBirthday(eliteUser.getBirthday().getTime());
	                                if(null != eliteUser.getFirstLoginTime()) user.setFirstLoginTime(eliteUser.getFirstLoginTime().getTime());
	                                if(null != eliteUser.getLastLoginTime()) user.setLastLoginTime(eliteUser.getLastLoginTime().getTime());
                                } else {
                                	notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                                	continue;
                                }
                                } catch (Exception e){
                                	log.error("", e);
                                	continue;
                                }
                                users.add(user);
                            }
                        }
                    }
                    
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            userListResult.setTotal(listResult.getTotal());
            userListResult.setUsers(users);
        }

        return userListResult;
    }
    
    @Override
    public TUserIdListResult searchUserId(TSearchUserCondition condition) throws TException {
        ListResult list = searchService.searchUserId(condition);
        List<Long> ids = new ArrayList<Long>();
        if (null != list.getEntities() && list.getEntities().size() > 0) {
            list.getEntities().forEach(object -> ids.add((Long)object));
        }
        TUserIdListResult listResult = new TUserIdListResult(ids, list.getTotal());
        return listResult;
    }
    
    @Override
    public TExpertTeamListResult searchExpertTeam(TSearchExpertTeamCondition condition) throws TException {
       List<TEliteExpertTeam> experts = new ArrayList<TEliteExpertTeam>();
       TExpertTeamListResult expertListResult = new TExpertTeamListResult(experts, 0);
       ListResult listResult = searchService.searchExpertTeam(condition);
       if (null != listResult && listResult.getTotal() > 0) {
           for (Object id : listResult.getEntities()) {
               Long bpId = (Long) id;
               EliteExpertTeam expertTeam = expertTeamService.get(bpId);
               if (null != expertTeam) {
                   experts.add(ConvertUtil.convert(expertTeam));
               } else {
                   log.error("expertTeam bpId = {} is not in database, remove from es.", bpId);
                   notifyService.notify2Statistic(bpId, BpType.Elite_Expert_Team.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
               }
               
           }
       }
       expertListResult.setTotal(listResult.getTotal());
       return expertListResult;
    }

    @Override
    public TSearchGlobalListResult searchGlobal(TSearchGlobalCondition condition) throws TException {
        Map<String, Integer> initTotalCounts = new HashMap<>();
        initTotalCounts.put("question", 0);
        initTotalCounts.put("answer", 0);
        initTotalCounts.put("user", 0);
        TSearchGlobalListResult listResult = new TSearchGlobalListResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), initTotalCounts);

        SearchGlobalListResult searchResult = searchService.searchGlobal(condition);
        if(searchResult != null){
            List<TEliteQuestion> questions = new ArrayList<TEliteQuestion>();
            List<TEliteAnswer> answers = new ArrayList<TEliteAnswer>();
            List<TEliteUser> users = new ArrayList<TEliteUser>();
            
            for(EliteSearchResultBean ele : searchResult.getQuestions()){
                Long id = ele.getId();
                EliteQuestion question = questionService.getById(id);
                if(question != null) {
                    TEliteQuestion tQuestion = ConvertUtil.convert(question);
                    tQuestion.setHighlightText(ele.getHighlightText()).setHighlightWords(ele.getHighlightWord());
                    questions.add(tQuestion);
                } else {
                    log.error("question exists in ES, but not in DB, questionId=" + id);
                    notifyService.notify2Statistic(id, BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                }
            }

            for(EliteSearchResultBean ele : searchResult.getAnswers()) {
                Long id = ele.getId();
                EliteAnswer answer = answerService.getById(id);
                if(answer != null) {
                    TEliteAnswer tAnswer = ConvertUtil.convert(answer);
                    tAnswer.setHighlightText(ele.getHighlightText()).setHighlightWords(ele.getHighlightWord());
                    answers.add(tAnswer);
                } else {
                    log.error("answer exists in ES, but not in DB, answerId=" + id);
                    notifyService.notify2Statistic(id, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                }
            }

            for(EliteSearchResultBean ele : searchResult.getUsers()){
                try {
                    Long bpId = ele.getId();
                    CodeMsgData codeMsgData = bpServiceAdapter.getBpUserBasic(bpId);
                    if(codeMsgData.getCode() == ResponseConstants.OK){
                        log.info("get userInfo for global search <bpId={}>", bpId);
                        String infoJSONStr = codeMsgData.getData();
                        if(StringUtils.isNotBlank(infoJSONStr)) {
                            JSONObject infoJSON = JSONObject.fromObject(infoJSONStr);
                            if (null != infoJSON) {
                                TEliteUser tUser = new TEliteUser();
                                tUser.setBpId(bpId);
                                tUser.setNick(infoJSON.getString("nick"));
                                tUser.setHighlightText(ele.getHighlightText()).setHighlightWords(ele.getHighlightWord());
                                users.add(tUser);
                            }
                        }
                    }
                    EliteUser eliteUser = userService.get(bpId);
                    if(null == eliteUser){
                    	log.error("user exists in ES, but not in DB, bpId=" + bpId);
                    	notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), EliteNotifyType.ELITE_NOTIFY_DELETE.getValue());
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            listResult.setQuestions(questions);
            listResult.setAnswers(answers);
            listResult.setUsers(users);
            listResult.setTotalCounts(searchResult.getTotalCounts());
            listResult.setRank(searchResult.getRank());
        }

        return listResult;
    }

    @Override
    public long setEliteSubjectHistory(TEliteSubject subject){
    	return subjectService.setEliteSubjectHistory(ConvertUtil.convert(subject));
    }
    
    @Override
    public TSubjectListResult getHistoryByStatus(int status, int start, int count){
    	List<TEliteSubject> tEliteSubject = new ArrayList<>();
    	TSubjectListResult subjectListResult = new TSubjectListResult(tEliteSubject, 0);
    	Long num = subjectService.getSubjectCountByStatus(status);
    	List<EliteSubject> eliteSubject = subjectService.getHistoryByStatus(status, start, count);
    	if(null != eliteSubject && eliteSubject.size() > 0){
    		for(Object obj : eliteSubject){
    			tEliteSubject.add(ConvertUtil.convert((EliteSubject) obj));
    		}
    		subjectListResult.setSubjects(tEliteSubject);
    		subjectListResult.setTotal(num);
    	}
    	return subjectListResult;
    }
    
    @Override
    public TSubjectListResult getAllHistory(int start, int count){
    	List<TEliteSubject> tEliteSubjects = new ArrayList<>();
    	TSubjectListResult tSubjectListResult = new TSubjectListResult(tEliteSubjects, 0);
    	List<EliteSubject> eliteSubjects = subjectService.getAllHistory(start, count);
    	if(null != eliteSubjects && eliteSubjects.size() > 0){
    		for(Object obj : eliteSubjects){
    			tEliteSubjects.add(ConvertUtil.convert((EliteSubject)obj));
    		}
    		tSubjectListResult.setSubjects(tEliteSubjects);
    		tSubjectListResult.setTotal((long)eliteSubjects.size());
    	}
    	return tSubjectListResult;
    }
    
    @Override
    public long getAllHistoryCount(){
    	return subjectService.getAllHistoryCount();
    }
    
    @Override
    public TEliteSubject getHistoryById(long id){
    	return ConvertUtil.convert((EliteSubject)subjectService.getHistoryById(id));
    }

	@Override
	public boolean setEliteTopicHistory(TEliteTopic topic) throws TException {
		return topicService.setEliteTopicHistory(ConvertUtil.convert(topic));
	}

	@Override
	public TEliteTopic getEliteTopicById(long id) throws TException {
		return ConvertUtil.convert(topicService.getEliteTopicById(id));
	}

	@Override
	public long getEliteTopicCount() throws TException {
		return topicService.getEliteTopicCount();
	}

	@Override
	public long getEliteTopicCountByStatus(int status) throws TException {
		return topicService.getEliteTopicCountByStatus(status);
	}

	@Override
	public List<TEliteTopic> getAllEliteTopic(int start, int count) throws TException {
		List<TEliteTopic> tEliteTopics = new ArrayList<>();
		List<EliteTopic> eliteTopics = topicService.getAllEliteTopic(start, count);
		for(Object obj : eliteTopics)
		{
			TEliteTopic tEliteTopic = ConvertUtil.convert((EliteTopic) obj);
			tEliteTopics.add(tEliteTopic);
		}
		return tEliteTopics;
	}

	@Override
	public List<TEliteTopic> getAllEliteTopicByStatus(int status, int start, int count) throws TException {
		List<TEliteTopic> tEliteTopics = new ArrayList<>();
		List<EliteTopic> eliteTopics = topicService.getAllEliteTopicByStatus(status, start, count);
		for(Object obj : eliteTopics)
		{
			TEliteTopic tEliteTopic = ConvertUtil.convert((EliteTopic) obj);
			tEliteTopics.add(tEliteTopic);
		}
		return tEliteTopics;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#createMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public long createMedia(TEliteMedia media) throws TException {
		return mediaService.create(ConvertUtil.convert(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#updateMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public boolean updateMedia(TEliteMedia media) throws TException {
		return mediaService.update(ConvertUtil.convert(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#removeMedia(com.sohu.bp.elite.model.TEliteMedia)
	 */
	@Override
	public boolean removeMedia(TEliteMedia media) throws TException {
		return mediaService.remove(ConvertUtil.convert(media));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getById(long)
	 */
	@Override
	public TEliteMedia getById(long id) throws TException {
		return ConvertUtil.convert(mediaService.getById(id));
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getMediaListByQuestionIdAndType(long, com.sohu.bp.elite.model.TEliteMediaType)
	 */
	@Override
	public List<TEliteMedia> getMediaListByQuestionIdAndType(long questionId, TEliteMediaType type) throws TException {
		List<EliteMedia> eliteMediaList = mediaService.getMediaListByQuestionIdAndType(questionId, type.getValue());
		List<TEliteMedia> tEliteMediaList = new ArrayList<TEliteMedia>();
		if(null != eliteMediaList && eliteMediaList.size() > 0)
		{
			eliteMediaList.forEach(eliteMedia -> tEliteMediaList.add(ConvertUtil.convert(eliteMedia)));
		}
		
		return tEliteMediaList;
	}

	/* (non-Javadoc)
	 * @see com.sohu.bp.elite.service.EliteThriftService.Iface#getMediaListByAnswerIdAndType(long, com.sohu.bp.elite.model.TEliteMediaType)
	 */
	@Override
	public List<TEliteMedia> getMediaListByAnswerIdAndType(long answerId, TEliteMediaType type) throws TException {
		List<EliteMedia> eliteMediaList = mediaService.getMediaListByAnswerIdAndType(answerId, type.getValue());
		List<TEliteMedia> tEliteMediaList = new ArrayList<TEliteMedia>();
		if(null != eliteMediaList && eliteMediaList.size() > 0)
		{
			eliteMediaList.forEach(eliteMedia -> tEliteMediaList.add(ConvertUtil.convert(eliteMedia)));
		}
		
		return tEliteMediaList;
	}

    @Override
    public boolean rebuildQuestion(long questionId) throws TException {
//        searchService.indexData(questionId, EliteSearchType.Question.getValue(), null, true);
    	notifyService.notify2Statistic(questionId, BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        return true;
    }

    @Override
    public boolean rebuildAnswer(long answerId) throws TException {
//        searchService.indexData(answerId, EliteSearchType.Answer.getValue(), null, true);
    	notifyService.notify2Statistic(answerId, BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        return true;
    }

    @Override
    public boolean rebuildUser(long bpId) throws TException {
//        searchService.indexData(bpId, EliteSearchType.User.getValue(), null, true);
    	notifyService.notify2Statistic(bpId, BpType.Elite_User.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        return true;
    }
    
    @Override
    public boolean rebuildExpertTeam(long bpId) throws TException {
        if (bpId > 0) notifyService.notify2Statistic(bpId, BpType.Elite_Expert_Team.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue());
        return false;
    }

    @Override
    public boolean rebuildQuestionsForUser(long bpId) throws TException {
//        searchService.indexData(bpId, EliteSearchType.Question.getValue(), "1", true);
        List<EliteQuestion> questions = questionService.getQuestionsByBpId(bpId);
        if (null != questions) {
            questions.forEach(question -> notifyService.notify2Statistic(question.getId(), BpType.Question.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue()));
        }
        return true;
    }

    @Override
    public boolean rebuildAnswersForUser(long bpId) throws TException {
//        searchService.indexData(bpId, EliteSearchType.Answer.getValue(), "1", true);
        List<EliteAnswer> answers = answerService.getAnswersByBpId(bpId);
        if (null != answers) {
            answers.forEach(answer -> notifyService.notify2Statistic(answer.getId(), BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue()));
        }
        return true;
    }

    @Override
    public boolean rebuildAnswersForQuestion(long questionId) throws TException {
//        searchService.indexData(questionId, EliteSearchType.Answer.getValue(), "2", true);
        List<EliteAnswer> answers = answerService.getAnswersByQuestionId(questionId);
        if (null != answers) {
            answers.forEach(answer -> notifyService.notify2Statistic(answer.getId(), BpType.Answer.getValue(), EliteNotifyType.ELITE_NOTIFY_INSERT.getValue()));
        }
        return true;
    }
    
	@Override
	public boolean rebuildQuestionsForSpecial(long specialId, int specialType) throws TException {
		List<EliteQuestion> questions = questionService.getQuestionsBySpeical(specialId, specialType);
		if(null != questions){
			for(EliteQuestion question : questions){
				rebuildQuestion(question.getId());
			}
		}
		return true;
	}

    @Override
	public boolean setEliteFragmentHistory(TEliteFragment fragment) throws TException {
		return eliteFragmentService.setEliteFragmentHistory(ConvertUtil.convert(fragment));
	}

	@Override
	public int getFragmentCount() throws TException {
		return eliteFragmentService.getFragmentCount();
	}



	@Override
	public TEliteFragment getFragmentById(long id) throws TException {
		return ConvertUtil.convert(eliteFragmentService.getFragmentById(id));
	}

	@Override
	public int getFragmentCountByType(TEliteFragmentType type) throws TException {
		return eliteFragmentService.getFragmentCountByType(type.getValue());
	}

	@Override
	public List<TEliteFragment> getFragmentByType(TEliteFragmentType type) throws TException {
		List<TEliteFragment> tEliteFragments = new ArrayList<>();
		List<EliteFragment> eliteFragments = eliteFragmentService.getFragmentByType(type.getValue());
		for(EliteFragment eliteFragment : eliteFragments)
		{
			TEliteFragment tEliteFragment = ConvertUtil.convert(eliteFragment);
			tEliteFragments.add(tEliteFragment);
		}
		return tEliteFragments;
	}

	@Override
	public List<TEliteFragment> getAllFragment() throws TException {
		List<TEliteFragment> tEliteFragments = new ArrayList<>();
		List<EliteFragment> eliteFragments = eliteFragmentService.getAllFragment();
		for(EliteFragment eliteFragment : eliteFragments)
		{
			TEliteFragment tEliteFragment = ConvertUtil.convert(eliteFragment);
			tEliteFragments.add(tEliteFragment);
		}
		return tEliteFragments;
	}

	@Override
	public long setEliteFollow(TEliteFollow follow) throws TException {
		return eliteFollowService.setEliteFollow(ConvertUtil.convert(follow));
	}

	@Override
	public boolean updateEliteFollow(TEliteFollow follow) throws TException {
		return eliteFollowService.updateEliteFollow(ConvertUtil.convert(follow));
	}

	@Override
	public int getEliteFollowCountByType(TEliteFollowType type) throws TException {
		return eliteFollowService.getEliteFollowCountByType(EliteFollowType.valueMap.get(type.getValue()));
	}

	@Override
	public List<TEliteFollow> getEliteFollowByType(TEliteFollowType type, int start, int count) throws TException {
		List<TEliteFollow> tEliteFollows = new ArrayList<>();
		List<EliteFollow> eliteFollows = eliteFollowService.getEliteFollowBtType(EliteFollowType.valueMap.get(type.getValue()), start, count);
		if(null != eliteFollows && eliteFollows.size() > 0)
		{
			for(EliteFollow eliteFollow : eliteFollows)
			{
				tEliteFollows.add(ConvertUtil.convert(eliteFollow));
			}
		}
		return tEliteFollows;
	}

	@Override
	public TEliteFollow getEliteFollowById(long id) throws TException {
		return ConvertUtil.convert(eliteFollowService.getEliteFollowById(id));
	}

	@Override
	public long setEliteColumn(TEliteColumn column) throws TException {
		return columnService.insert(ConvertUtil.convert(column));
	}

	@Override
	public boolean updateEliteColumn(TEliteColumn column) throws TException {
		columnService.update(ConvertUtil.convert(column));
		return true;
	}

	@Override
	public TEliteColumn getEliteColumnById(long columnId) throws TException {
		return ConvertUtil.convert(columnService.getEliteColumnById(columnId));
	}

	@Override
	public TColumnListResult getAllEliteColumn(int start, int count) throws TException {
		TColumnListResult listResult = new TColumnListResult();
		List<TEliteColumn> tEliteColumns = new ArrayList<>();
		listResult.setTotal(columnService.getEliteColumnCount());
		if(count != 0){
			List<EliteColumn> eliteColumns = columnService.getAllEliteColumn(start, count);
			eliteColumns.forEach(eliteColumn -> tEliteColumns.add(ConvertUtil.convert(eliteColumn)));
		}
		listResult.setColumns(tEliteColumns);
		return listResult;
	}

	@Override
	public TColumnListResult getAllEliteColumnByStatus(int start, int count, int status) throws TException {
		TColumnListResult listResult = new TColumnListResult();
		List<TEliteColumn> tEliteColumns = new ArrayList<>();
		listResult.setTotal(columnService.getEliteColumnCountByStatus(status));
		if(count != 0){
			List<EliteColumn> eliteColumns = columnService.getAllEliteColumnByStatus(start, count, status);
			eliteColumns.forEach(eliteColumn -> tEliteColumns.add(ConvertUtil.convert(eliteColumn)));
		}
		listResult.setColumns(tEliteColumns);
		return listResult;
	}

	@Override
	public List<TEliteQuestion> getQuestionsBySpecial(long specialId, int specialType) throws TException {
		List<EliteQuestion> questions = questionService.getQuestionsBySpeical(specialId, specialType);
		List<TEliteQuestion> tEliteQuestions = new ArrayList<>();
		if(null != questions && questions.size() > 0){
			questions.forEach(question -> tEliteQuestions.add(ConvertUtil.convert(question)));
		}
		return tEliteQuestions;
	}

    @Override
    public int getUserQuestionNumByStatus(long bpId, List<Integer> statusList) throws TException {
        return questionService.getUserQuestionNumByStatus(bpId, statusList);
    }

    @Override
    public TUserListResult getAuditingExperts(int start, int count) throws TException {
        TUserListResult userListResult = new TUserListResult(new ArrayList<TEliteUser>(), 0);
        ListResult listResult = userService.getAuditingExperts(start, count);
        if (null != listResult.getEntities()) {
            userListResult.setTotal(listResult.getTotal());
            List<TEliteUser> list = new ArrayList<TEliteUser>();
            listResult.getEntities().forEach(object -> {
                list.add(ConvertUtil.convert((EliteUser)object));
            });
            userListResult.setUsers(list);
        }
        return userListResult;
    }

    @Override
    public TUserListResult getExperts(int start, int count) throws TException {
        TUserListResult userListResult = new TUserListResult(new ArrayList<TEliteUser>(), 0);
        ListResult listResult = userService.getExperts(start, count);
        if (null != listResult.getEntities()) {
            userListResult.setTotal(listResult.getTotal());
            List<TEliteUser> list = new ArrayList<TEliteUser>();
            listResult.getEntities().forEach(object -> {
                list.add(ConvertUtil.convert((EliteUser)object));
            });
            userListResult.setUsers(list);
        }
        return userListResult;
    }

    @Override
    public boolean reloadExpertsCache() throws TException {
        userService.reloadExpertsCache();
        return true;
    }

    @Override
    public boolean passExpert(long bpId) throws TException {
        return userService.passExpert(bpId);
    }

    @Override
    public boolean rejectExpert(long bpId, String reason) throws TException {
        return userService.rejectExpert(bpId, reason);
    }

    @Override
    public long insertExpertTeam(TEliteExpertTeam expert) throws TException {
        return expertTeamService.save(ConvertUtil.convert(expert));
    }

    @Override
    public boolean updateExpertTeam(TEliteExpertTeam expert) throws TException {
        return expertTeamService.update(ConvertUtil.convert(expert));
    }

    @Override
    public TEliteExpertTeam getExpertTeamByBpId(long bpId) throws TException {
        return ConvertUtil.convert(expertTeamService.get(bpId));
    }

    @Override
    public List<TEliteExpertTeam> getBatchExpertTeams(List<Long> expertIds) throws TException {
       if (null == expertIds) return null;
       List<TEliteExpertTeam> list = new ArrayList<TEliteExpertTeam>();
       expertIds.forEach(id -> list.add(ConvertUtil.convert(expertTeamService.get(id))));
       return list;
    }

    @Override
    public boolean addExpertNewPush(long bpId, long questionId) throws TException {
        return expertTeamService.addNewMessage(bpId, questionId);
    }

    @Override
    public boolean addExpertNewAnswered(long bpId, long questionId) throws TException {
        return expertTeamService.addNewAnswered(bpId, questionId);
    }

    @Override
    public boolean addExpertBatchNewPush(List<Long> bpIds, long questionId) throws TException {
        return expertTeamService.addBatchNewMessage(bpIds, questionId);
    }

    @Override
    public boolean batchAuditExpert(List<Long> passIds, List<Long> rejectIds, String reason) throws TException {
        return userService.batchAudit(passIds, rejectIds, reason);
    }
    
    @Deprecated
    @Override
    public TExpertTeamListResult getExpertTeamsBySortFields(int start, int count, String sortField)
            throws TException {
        List<EliteExpertTeam> list = expertTeamService.getExpertTeamsBySortField(start, count, sortField);
        Long total = userService.getExpertsNum();
        List<TEliteExpertTeam> expertList = new ArrayList<TEliteExpertTeam>();
        list.forEach(expert -> expertList.add(ConvertUtil.convert(expert)));
        TExpertTeamListResult listResult = new TExpertTeamListResult(expertList, total);
        return listResult;
    }

    @Override
    public boolean saveEliteAdmin(TEliteAdmin eliteAdmin) throws TException {
        EliteAdmin admin = new EliteAdmin();
        admin.setId(eliteAdmin.getBpId());
        admin.setStatus(eliteAdmin.getStatus());
        return eliteAdminService.save(admin);
    }

    @Override
    public boolean updateEliteAdmin(TEliteAdmin eliteAdmin) throws TException {
        EliteAdmin admin = new EliteAdmin();
        admin.setId(eliteAdmin.getBpId());
        admin.setStatus(eliteAdmin.getStatus());
        return eliteAdminService.update(admin);
    }

    @Override
    public int getEliteAdminStatus(long bpId) throws TException {
        return eliteAdminService.getAdminStatus(bpId);
    }

    @Override
    public boolean superAdmin(long bpId) throws TException {
        return eliteAdminService.superAdmin(bpId);
    }

    @Override
    public TEliteUser getExpert(long bpId) throws TException {
        return ConvertUtil.convert(userService.getExpert(bpId));
    }

    @Override
    public boolean addExpertTag(int tagId) throws TException {
        return expertTeamService.addExpertTag(tagId);
    }

    @Override
    public boolean removeExpertTag(int tagId) throws TException {
        return expertTeamService.removeExpertTag(tagId);
    }

    @Override
    public List<Integer> getExpertTagIds() throws TException {
        return expertTeamService.getExpertTagIds();
    }

    @Override
    public boolean postMessage(long bpId, TEliteMessagePushType messageType, TEliteMessageData messageData,
            TEliteMessageStrategy strategy) throws TException {
        return notifyService.postMessage(bpId, messageType, messageData, strategy);
    }
    
    @Override
    public boolean likeAnswer(long answerId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
            throws TException {
        return userActionService.likeAnswer(answerId, bpId, ip, port, time, messageFlag, feedFlag);
    }

    @Override
    public boolean unlikeAnswer(long answerId, long bpId) throws TException {
        return userActionService.unlikeAnswer(answerId, bpId);
    }

    @Override
    public boolean favoriteAnswer(long answerId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
            throws TException {
        return userActionService.favoriteAnswer(answerId, bpId, ip, port, time, messageFlag, feedFlag);
    }

    @Override
    public boolean unfavoriteAnswer(long answerId, long bpId) throws TException {
        return userActionService.unfavoriteAnswer(answerId, bpId);
    }

    @Override
    public boolean followQuestion(long questionId, long bpId, long ip, int port, long time, boolean messageFlag, boolean feedFlag)
            throws TException {
        return userActionService.followQuestion(questionId, bpId, ip, port, time, messageFlag, feedFlag);
    }

    @Override
    public boolean unfollowQuestion(long questionId, long bpId, long ip, int port, long time) throws TException {
        return userActionService.unfollowQuestion(questionId, bpId, ip, port, time);
    }

    @Override
    public boolean followPeople(long followedId, long followId, long ip, int port, long time, boolean messageFlag)
            throws TException {
        return userActionService.followPeople(followedId, followId, ip, port, time, messageFlag);
    }

    @Override
    public boolean unfollowPeople(long followedId, long followId, long ip, int port, long time) throws TException {
        return userActionService.unfollowPeople(followedId, followId, ip, port, time);
    }

    @Override
    public boolean commentAnswer(long answerId, long bpId, String content, long ip, int port, long time,
            boolean messageFlag, boolean feedFlag) throws TException {
        return userActionService.commentAnswer(answerId, bpId, content, ip, port, time, messageFlag, feedFlag);
    }

    @Override
    public boolean treadAnswer(long answerId, long bpId, long ip, int port, long time) throws TException {
        return userActionService.treadAnswer(answerId, bpId, ip, port, time);
    }

    @Override
    public boolean untreadAnswer(long answerId, long bpId) throws TException {
        return userActionService.unTreadAnswer(answerId, bpId);
    }

    @Override
    public long insertExpertTeamInfo(TEliteExpertTeamInfo teamInfo) throws TException {
        return expertTeamInfoService.insert(ConvertUtil.convert(teamInfo));
    }

    @Override
    public boolean updateExpertTeamInfo(TEliteExpertTeamInfo teamInfo) throws TException {
        return expertTeamInfoService.update(ConvertUtil.convert(teamInfo));
    }

    @Override
    public Map<Long, String> getExpertTeamInfoMap() throws TException {
        return expertTeamInfoService.getTeamMap();
    }

    @Override
    public List<TEliteExpertTeamInfo> getExpertTeamInfos() throws TException {
        List<TEliteExpertTeamInfo> tEliteExpertTeamInfos = new ArrayList<TEliteExpertTeamInfo>();
        List<EliteExpertTeamInfo> eliteExpertTeamInfos = expertTeamInfoService.getList();
        if (null != eliteExpertTeamInfos && eliteExpertTeamInfos.size() > 0) {
            eliteExpertTeamInfos.forEach(teamInfo -> tEliteExpertTeamInfos.add(ConvertUtil.convert(teamInfo)));
        }
        return tEliteExpertTeamInfos;
    }

    @Override
    public TEliteExpertTeamInfo getExpertTeamInfoById(long id) throws TException {
        return ConvertUtil.convert(expertTeamInfoService.getById(id));
    }

    @Override
    public boolean favoriteTargetItem(int targetType, long targetId, long bpId, long ip, int port, long time)
            throws TException {
        return userActionService.favoriteTarget(targetId, targetType, bpId, ip, port, time);
    }

    @Override
    public boolean unfavoriteTargetItem(int targetType, long targetId, long bpId) throws TException {
        return userActionService.unfavoriteTarget(targetId, targetType, bpId);
    }

    @Override
    public int getNewSquareNum(long latestTime) throws TException {
        return squareService.getNewSquareNum(latestTime);
    }

    @Override
    public List<TEliteSquareItem> getNewSquareList(long latestTime) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getNewSquareList(latestTime);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public List<TEliteSquareItem> getSquareBackward(long feedId, int feedType, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getBackward(feedId, FeedType.getFeedTypeByValue(feedType), count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public boolean insertSquare(long feedId, int feedType) throws TException {
        return squareService.insertSquare(feedId, FeedType.getFeedTypeByValue(feedType));
    }

    @Override
    public boolean flushSquare() throws TException {
        squareService.flushSquare();
        return true;
    }

    @Override
    public boolean removeSquareItem(long feedId, int feedType) throws TException {
        return squareService.removeSquareItem(feedId, FeedType.getFeedTypeByValue(feedType));
    }

    @Override
    public int getNewSelectedSquareNum(long latestTime) throws TException {
        return squareService.getNewSelectedSquareNum(latestTime);
    }

    @Override
    public List<TEliteSquareItem> getNewSelectedSquareList(long latestTime) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getNewSelectedSquareList(latestTime);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public List<TEliteSquareItem> getSelectedSquareBackward(long feedId, int feedType, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getSelectedSquareBackward(feedId, FeedType.getFeedTypeByValue(feedType), count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public boolean insertSelectedSquare(long feedId, int feedType) throws TException {
        return squareService.insertSelectedSquare(feedId, FeedType.getFeedTypeByValue(feedType));
    }

    @Override
    public boolean flushSelectedSquare() throws TException {
        squareService.flushSelectedSquare();
        return true;
    }

    @Override
    public boolean removeSelectedSquareItem(long feedId, int feedType) throws TException {
        return squareService.removeSelectedSquareItem(feedId, FeedType.getFeedTypeByValue(feedType));
    }

    @Override
    public boolean isInSelectedSquare(long feedId, int feedType) throws TException {
        return squareService.isInSelectedSquare(feedId, FeedType.getFeedTypeByValue(feedType));
    }

	@Override
	public List<TEliteSquareItem> getSquareBackwardPage(int start, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getBackward(start, count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
	}

	@Override
	public List<TEliteSquareItem> getSelectedSquareBackwardPage(int start, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getSelectedSquareBackward(start, count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
	}

    @Override
    public List<TEliteSquareItem> getSelectedSquareBackwardByOldestTime(long oldestTime, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getSelectedSquareBackward(oldestTime, count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }

    @Override
    public List<TEliteSquareItem> getSelectedSquareForwardByLatestTime(long latestTime, int count) throws TException {
        List<TEliteSquareItem> items = new ArrayList<TEliteSquareItem>();
        List<Long> complexIds = squareService.getSelectedSquareForward(latestTime, count);
        if (null != complexIds && complexIds.size() > 0) {
            items = complexIds.stream().map(ConvertUtil::convert).collect(Collectors.toList());
        }
        return items;
    }
}
