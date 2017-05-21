package com.sohu.bp.elite.api.api;

import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.cache.CacheManager;
import com.sohu.bp.cache.redis.RedisCache;
import com.sohu.bp.decoration.adapter.BpAreaMapServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.AreaMap;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.api.api.bean.EcologyAnswer;
import com.sohu.bp.elite.api.api.bean.EcologyIndexData;
import com.sohu.bp.elite.api.api.bean.EcologyQuestion;
import com.sohu.bp.elite.api.api.bean.EcologyUser;
import com.sohu.bp.elite.api.constants.CacheConstants;
import com.sohu.bp.elite.api.constants.Constants;
import com.sohu.bp.elite.api.service.UserInfoService;
import com.sohu.bp.elite.api.util.ContentUtil;
import com.sohu.bp.elite.api.util.EliteUrlGenerator;
import com.sohu.bp.elite.api.util.ResponseJSON;
import com.sohu.bp.elite.enums.*;
import com.sohu.bp.elite.model.*;
import com.sohu.bp.model.BpInteractionTargetType;
import com.sohu.bp.model.BpInteractionType;
import com.sohu.bp.service.adapter.BpExtendServiceAdapter;
import com.sohu.bp.service.adapter.BpServiceAdapterFactory;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapter;
import com.sohu.bp.thallo.adapter.BpThalloServiceAdapterFactory;
import com.sohu.bp.thallo.model.ObjectUserRelationListResult;
import com.sohu.bp.thallo.model.RelationStatus;
import com.sohu.nuc.model.CodeMsgData;
import com.sohu.nuc.model.ResponseConstants;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.incrementer.SybaseAnywhereMaxValueIncrementer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by nicholastang on 2017/3/9.
 */
@Controller
@RequestMapping("/innerapi/ecology")
public class EcologyApi {
    private static final Logger logger = LoggerFactory.getLogger(EcologyApi.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private static final BpAreaMapServiceAdapter areaMapServiceAdapter = BpDecorationServiceAdapterFactory.getBpAreaMapServiceAdapter();
    private static final BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    private static final BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();


    private static final int EXPIRE_SECONDS= 3600;
    private static final int RESULT_EXPIRE_SECONDS = 1800;
    private static final int MAX_TALENT_LENGTH = 10;
    private static final int MAX_QUESTION_LENGTH = 30;
    private static final String ECOLOGY_INDEX_QUESTION_DATA = "INDEX_QUESTION";
    private static final String ECOLOGY_INDEX_USER_DATA = "INDEX_USER";
    private static final String ECOLOGY_CUSTOM_QUESTION_DATA = "CUSTOM_QUESTION";
    private static final String ECOLOGY_CUSTOM_USER_DATA = "CUSTOM_USER";
    private static final String ECOLOGY_RESULT_QUESTION = "RESULT_QUESTION";
    private static final String ECOLOGY_RESULT_USER = "RESULT_USER";
    private static final ReadWriteLock indexQuestionLock = new ReentrantReadWriteLock();
    private static final ReadWriteLock indexUserLock = new ReentrantReadWriteLock();
    private static final ReadWriteLock customQuestionLock = new ReentrantReadWriteLock();
    private static final ReadWriteLock customUserLock = new ReentrantReadWriteLock();


    private static final int FORCE_TRUE = 1;
    private static final int FORCE_FALSE = 0;
    @Resource
    private CacheManager redisCacheManager;
    @Resource
    private UserInfoService userInfoService;

    private RedisCache ecologyCache;

    @PostConstruct
    public void init(){
        ecologyCache = (RedisCache)redisCacheManager.getCache(CacheConstants.CACHE_ECOLOGY);

    }

    @ResponseBody
    @RequestMapping(value = "index", produces = "application/json;charset=utf-8")
    public String getIndexData(int start, int count, int talent, long areaCode, int force){
        JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();
        if (start < 0 || count < 0 || count > MAX_QUESTION_LENGTH || talent < 0 || talent > MAX_TALENT_LENGTH || (force != FORCE_FALSE && force != FORCE_TRUE)) {
            resJSON = ResponseJSON.getErrorParametersErrorJSON();
            return resJSON.toString();
        }

        EcologyIndexData indexData = new EcologyIndexData();

        String resultQuestionKey = new StringBuilder(ECOLOGY_RESULT_QUESTION).append(Constants.DEFAULT_SPLIT_CHAR).append(areaCode).toString();
        String resultUserKey = new StringBuilder(ECOLOGY_RESULT_USER).append(Constants.DEFAULT_SPLIT_CHAR).append(areaCode).toString();


        long flagTime = System.currentTimeMillis();
        List<EcologyQuestion> ecologyQuestionList = new ArrayList<>();
        int oriStop = start + count;
        if (oriStop > 0 && oriStop <= MAX_QUESTION_LENGTH) {
            List<EcologyQuestion> totalEQList = (List<EcologyQuestion>)ecologyCache.get(resultQuestionKey);
            if (FORCE_TRUE == force || null == totalEQList || totalEQList.size() < oriStop) {
                //此处使用的是subList方法，因此在ecologyQuestionList使用结束之前，不要修改ecologyQuestionList或totalEQList
                ecologyQuestionList = this.getEQListByIdCache(areaCode, start, count, (FORCE_TRUE == force));
            } else {
                //此处使用的是subList方法，因此在ecologyQuestionList使用结束之前，不要修改ecologyQuestionList或totalEQList
                ecologyQuestionList = totalEQList.subList(start, oriStop);
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime - flagTime));
        flagTime = endTime;
        indexData.setQuestions(ecologyQuestionList);

        List<EcologyUser> ecologyUserList = new ArrayList<>();
        if (talent > 0) {
            List<EcologyUser> totalEUList = (List<EcologyUser>)ecologyCache.get(resultUserKey);
            if (FORCE_TRUE == force || null == totalEUList || totalEUList.size() < talent) {
                ecologyUserList = this.getEUListByIdCache(areaCode, talent, (FORCE_TRUE == force));
            } else {
                ecologyUserList = totalEUList.subList(0, talent);
            }
        }
        endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime - flagTime));
        indexData.setTalents(ecologyUserList);

        try {
            JSONObject dataJSON = JSONObject.fromObject(indexData);
            resJSON.put("data", dataJSON);
        }catch (Exception e) {
            logger.error("", e);
            resJSON = ResponseJSON.getErrorInteralErrorJSON();
        }
        return resJSON.toString();

    }

    @ResponseBody
    @RequestMapping(value = "set-data", produces = "application/json;charset=utf-8")
    public String setCustomData(String questionIds, String userIds, long areaCode){
        String customQuestionKey = new StringBuilder(ECOLOGY_CUSTOM_QUESTION_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        String customUserKey = new StringBuilder(ECOLOGY_CUSTOM_USER_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        JSONObject resJSON = ResponseJSON.getDefaultResponseJSON();

        customQuestionLock.writeLock().lock();
        ecologyCache.remove(customQuestionKey);
        try {
            if (StringUtils.isNotBlank(questionIds)) {
                String[] questionIdArray = questionIds.split(Constants.DEFAULT_SPLIT_CHAR);
                for(String questionIdStr : questionIdArray) {
                    TEliteQuestion question = eliteAdapter.getQuestionById(Long.parseLong(questionIdStr));
                    if (null == question
                            || (question.getStatus() != EliteQuestionStatus.PUBLISHED.getValue()
                            && question.getStatus() != EliteQuestionStatus.PASSED.getValue()
                            && question.getStatus() != EliteQuestionStatus.AUDITING.getValue())) {
                        continue;
                    }
                    ecologyCache.rPush(customQuestionKey, questionIdStr);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            resJSON = ResponseJSON.getErrorInteralErrorJSON();
            return resJSON.toString();
        } finally {
            customQuestionLock.writeLock().unlock();
        }

        customUserLock.writeLock().lock();
        ecologyCache.remove(customUserKey);
        try {
            if (StringUtils.isNotBlank(userIds)) {
                String[] userIdArray = userIds.split(Constants.DEFAULT_SPLIT_CHAR);
                for(String userIdStr : userIdArray) {
                    TEliteUser user = eliteAdapter.getUserByBpId(Long.parseLong(userIdStr));
                    if (null == user || user.getStatus() == EliteUserStatus.INVALID.getValue()) {
                        continue;
                    }
                    ecologyCache.rPush(customUserKey, userIdStr);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            resJSON = ResponseJSON.getErrorInteralErrorJSON();
            return resJSON.toString();
        } finally {
            customUserLock.writeLock().unlock();
        }

        return resJSON.toString();
    }

    public boolean updateEcologyQuestionData(long areaCode) {
        String indexQuestionKey = new StringBuilder(ECOLOGY_INDEX_QUESTION_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
        StringBuilder areaNames = new StringBuilder("");
        StringBuilder areaIds = new StringBuilder("");
        AreaMap areaMap = null;
        long flagTime = System.currentTimeMillis();
        long endTime = flagTime;
        //获取地域
        try {
            areaMap = areaMapServiceAdapter.getAreaByCode(areaCode);
            if (null != areaMap) {
                areaNames.append(areaMap.getName()).append(" ");
                areaIds.append(areaMap.getId()).append(Constants.DEFAULT_SPLIT_CHAR);
            }
            while (areaMap.getParent() > 0) {
                AreaMap parentAreaMap = areaMapServiceAdapter.getAreaByCode(areaMap.getParent());
                if (null != parentAreaMap) {
                    areaNames.append(parentAreaMap.getName()).append(" ");
                    areaIds.append(areaMap.getId()).append(Constants.DEFAULT_SPLIT_CHAR);

                    areaMap = parentAreaMap;
                }
            }
            endTime = System.currentTimeMillis();
            logger.info("waste time:" + (endTime-flagTime));
            flagTime = endTime;
            if (!("").equals(areaNames.toString())) {
                searchQuestionCondition.setKeywords(areaNames.toString());
            }
        }catch (Exception e) {
            logger.error("", e);
        }
        searchQuestionCondition.setStatusArray(new StringBuilder("").append(EliteQuestionStatus.AUDITING.getValue())
                .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PASSED.getValue())
                .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PUBLISHED.getValue()).toString());
        searchQuestionCondition.setFrom(0);
        searchQuestionCondition.setCount(MAX_QUESTION_LENGTH);
        searchQuestionCondition.setAutoComplete(1);
        searchQuestionCondition.setSortField("updateTime");
        searchQuestionCondition.setMinAnswerNum(1);
        searchQuestionCondition.setSortType(SortType.DESC);

        TQuestionIdListResult questionIdListResult = null;
        try {
            questionIdListResult  = eliteAdapter.searchQuestionId(searchQuestionCondition);
        }catch (Exception e) {
            logger.error("");
            return false;
        }
        endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime-flagTime));
        flagTime = endTime;
        if (null == questionIdListResult || questionIdListResult.getTotal() < MAX_QUESTION_LENGTH) {
            logger.info("get question list for areaCode="+areaCode+" failed.");
            return false;
        }
        List<Long> questionIds = questionIdListResult.getQuestionIds();
        if (null != questionIds) {
            try{
                ecologyCache.remove(indexQuestionKey);
                logger.info("cache length:"+ecologyCache.lLen(indexQuestionKey));
                questionIds.forEach(questionId -> ecologyCache.rPush(indexQuestionKey, String.valueOf(questionId)));
                ecologyCache.expire(indexQuestionKey, EXPIRE_SECONDS);
            } catch (Exception e) {
                logger.error("", e);
                return false;
            }
        }
        return true;
    }

    public boolean updateEcologyUserData(long areaCode) {
        String indexUserKey = new StringBuilder(ECOLOGY_INDEX_USER_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        TSearchUserCondition searchUserCondition = new TSearchUserCondition();
        searchUserCondition.setIdentity(EliteUserIdentity.EXPERT.getValue());
        searchUserCondition.setStatus(EliteUserStatus.VALID.getValue());
        searchUserCondition.setAutoComplete(1);
        searchUserCondition.setFrom(0).setCount(MAX_TALENT_LENGTH);
        TUserIdListResult userIdListResult = null;
        long flagTime = System.currentTimeMillis();
        try {
            userIdListResult = eliteAdapter.searchUserId(searchUserCondition);
        } catch (Exception e) {
            logger.error("", e);
        }
        long endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime-flagTime));
        if (null == userIdListResult || userIdListResult.getTotal() < MAX_TALENT_LENGTH) {
            logger.info("get talent list for areaCode="+areaCode+" failed.");
            return false;
        }
        List<Long> userIds =userIdListResult.getUserIds();
        if (null != userIds) {
            try {
                ecologyCache.remove(indexUserKey);
                userIds.forEach(userId -> ecologyCache.rPush(indexUserKey, String.valueOf(userId)));
                ecologyCache.expire(indexUserKey, EXPIRE_SECONDS);
            } catch (Exception e) {
                logger.error("", e);
                return false;
            }
        }
        return true;
    }

    public boolean updateEcologyData(long areaCode) {
        String indexQuestionKey = new StringBuilder(ECOLOGY_INDEX_QUESTION_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        String indexUserKey = new StringBuilder(ECOLOGY_INDEX_USER_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();

        TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
        StringBuilder areaNames = new StringBuilder("");
        AreaMap areaMap = null;
        //获取地域
        try {
            areaMap = areaMapServiceAdapter.getAreaByCode(areaCode);
            if (null != areaMap) {
                areaNames.append(areaMap.getName()).append(" ");
            }
            while (areaMap.getParent() > 0) {
                AreaMap parentAreaMap = areaMapServiceAdapter.getAreaByCode(areaMap.getParent());
                if (null != parentAreaMap) {
                    areaNames.append(parentAreaMap.getName()).append(" ");
                    areaMap = parentAreaMap;
                }
            }
            if (!("").equals(areaNames.toString())) {
                searchQuestionCondition.setKeywords(areaNames.toString());
            }
        }catch (Exception e) {
            logger.error("", e);
        }
        searchQuestionCondition.setStatusArray(new StringBuilder("").append(EliteQuestionStatus.AUDITING.getValue())
                .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PASSED.getValue())
                .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PUBLISHED.getValue()).toString());
        searchQuestionCondition.setFrom(0);
        searchQuestionCondition.setCount(MAX_QUESTION_LENGTH);
        searchQuestionCondition.setAutoComplete(1);
        searchQuestionCondition.setSortField("updateTime");
        searchQuestionCondition.setMinAnswerNum(1);
        searchQuestionCondition.setSortType(SortType.DESC);

        TQuestionListResult questionListResult = null;
        try {
            questionListResult  = eliteAdapter.searchQuestion(searchQuestionCondition);
        }catch (Exception e) {
            logger.error("");
            return false;
        }
        if (null == questionListResult || questionListResult.getTotal() < MAX_QUESTION_LENGTH) {
            logger.info("get question list for areaCode="+areaCode+" failed.");
            return false;
        }
        List<TEliteQuestion> questions = questionListResult.getQuestions();
        if (null != questions) {
            indexQuestionLock.writeLock().lock();
            try{
                ecologyCache.remove(indexQuestionKey);
                logger.info("cache length:"+ecologyCache.lLen(indexQuestionKey));
                questions.forEach(eliteQuestion -> ecologyCache.rPush(indexQuestionKey, String.valueOf(eliteQuestion.getId())));
                ecologyCache.expire(indexQuestionKey, EXPIRE_SECONDS);
            } catch (Exception e) {
                logger.error("", e);
                return false;
            } finally {
                indexQuestionLock.writeLock().unlock();
            }
        }

        TSearchUserCondition searchUserCondition = new TSearchUserCondition();
        searchUserCondition.setIdentity(EliteUserIdentity.EXPERT.getValue());
        searchUserCondition.setStatus(EliteUserStatus.VALID.getValue());
        searchUserCondition.setAutoComplete(1);
        searchUserCondition.setFrom(0).setCount(MAX_TALENT_LENGTH);
        TUserListResult userListResult = null;
        try {
            userListResult = eliteAdapter.searchUser(searchUserCondition);
        } catch (Exception e) {
            logger.error("", e);
        }
        if (null == userListResult || userListResult.getTotal() < MAX_TALENT_LENGTH) {
            logger.info("get talent list for areaCode="+areaCode+" failed.");
            return false;
        }
        List<TEliteUser> users =userListResult.getUsers();
        if (null != users) {
            indexUserLock.writeLock().lock();
            try {
                ecologyCache.remove(indexUserKey);
                users.forEach(eliteUser -> ecologyCache.rPush(indexUserKey, String.valueOf(eliteUser.getBpId())));
                ecologyCache.expire(indexUserKey, EXPIRE_SECONDS);
            } catch (Exception e) {
                logger.error("", e);
                return false;
            } finally {
                indexUserLock.writeLock().unlock();
            }
        }

        return true;
    }



    public List<EcologyQuestion> getEQListByIdCache(long areaCode, int start, int count, boolean force) {
        List<EcologyQuestion> ecologyQuestionList = new ArrayList<>();
        List<EcologyQuestion> resultQuestionList = new ArrayList<>();
        List<String> questionIdList = new ArrayList<>();
        String indexQuestionKey = new StringBuilder(ECOLOGY_INDEX_QUESTION_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        String resultQuestionKey = new StringBuilder(ECOLOGY_RESULT_QUESTION).append(Constants.DEFAULT_SPLIT_CHAR).append(areaCode).toString();
        //TODO 后台干涉数据获取

        if (force || ecologyCache.lLen(indexQuestionKey) < MAX_QUESTION_LENGTH) {
            if (!this.updateEcologyQuestionData(areaCode)) {
                return resultQuestionList;
            }
        }
        long flagTime = System.currentTimeMillis();
        try {
            questionIdList = ecologyCache.lRange(indexQuestionKey, 0, MAX_QUESTION_LENGTH - 1);
            for (String questionIdStr : questionIdList) {
                ecologyQuestionList.add(this.getEcologyQuestion(Long.parseLong(questionIdStr)));
            }
            ecologyCache.put(resultQuestionKey, ecologyQuestionList);
            ecologyCache.expire(resultQuestionKey, RESULT_EXPIRE_SECONDS);
            resultQuestionList = ecologyQuestionList.subList(start, start + count);
        } catch (Exception e) {
            logger.error("", e);
            resultQuestionList = new ArrayList<>();
        }
        long endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime-flagTime));
        return resultQuestionList;

    }

    public List<EcologyUser> getEUListByIdCache(long areaCode, int count, boolean force) {
        List<EcologyUser> ecologyUserList = new ArrayList<>();
        List<EcologyUser> resultUserList = new ArrayList<>();
        List<String> userIdList = new ArrayList<>();
        String resultUserKey = new StringBuilder(ECOLOGY_RESULT_USER).append(Constants.DEFAULT_SPLIT_CHAR).append(areaCode).toString();
        String indexUserKey = new StringBuilder(ECOLOGY_INDEX_USER_DATA).append(Constants.DEFAULT_CACHE_SPLIT_CHAR).append(areaCode).toString();
        //TODO 后台干涉数据获取

        if (force || ecologyCache.lLen(indexUserKey) < MAX_TALENT_LENGTH) {
            if (!this.updateEcologyUserData(areaCode)) {
                return resultUserList;
            }
        }
        long flagTime = System.currentTimeMillis();
        try {
            userIdList = ecologyCache.lRange(indexUserKey, 0, MAX_TALENT_LENGTH - 1);
            for (String userIdStr : userIdList) {
                ecologyUserList.add(this.getEcologyUser(Long.parseLong(userIdStr)));
            }
            ecologyCache.put(resultUserKey, ecologyUserList);
            ecologyCache.expire(resultUserKey, RESULT_EXPIRE_SECONDS);
            resultUserList = ecologyUserList.subList(0, count);
        } catch (Exception e) {
            logger.error("", e);
            resultUserList = new ArrayList<>();
        }
        long endTime = System.currentTimeMillis();
        logger.info("waste time:" + (endTime-flagTime));
        return resultUserList;
    }

    public EcologyQuestion getEcologyQuestion(long questionId) {
        TEliteQuestion question = null;
        try {
            question = eliteAdapter.getQuestionById(questionId);
        }catch (Exception e){
            logger.error("", e);
            return null;
        }
        if(null == question) {
            return null;
        }
        EcologyQuestion ecologyQuestion = new EcologyQuestion();
        ecologyQuestion.setId(question.getId());
        ecologyQuestion.setTitle(question.getTitle());
        ecologyQuestion.setLink(EliteUrlGenerator.getQuestionUrl(question.getId()));
        int followedNum = 0;
        try {
            ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(question.getId(), BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
            if (null != listResult) {
                followedNum = (int)listResult.getTotal();
            }
        }catch (Exception e) {
            logger.error("", e);
        }
        ecologyQuestion.setFollowedNum(followedNum);
        TSearchAnswerCondition condition = new TSearchAnswerCondition();
        condition.setQuestionId(question.getId())
                .setFrom(0)
                .setCount(1)
                .setSortField("updateTime")
                .setSortType(SortType.DESC)

                .setStatusArray(new StringBuilder("").append(EliteQuestionStatus.AUDITING.getValue())
                        .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PASSED.getValue())
                        .append(Constants.DEFAULT_SPLIT_CHAR).append(EliteQuestionStatus.PUBLISHED.getValue()).toString());

        List<EcologyAnswer> answers = new ArrayList<>();
        try {
            TAnswerListResult answerListResult = eliteAdapter.searchAnswer(condition);
            List<TEliteAnswer> answerList = answerListResult.getAnswers();
            answerList.forEach(eliteAnswer -> answers.add(this.getEcologyAnswer(eliteAnswer)));
        }catch (Exception e) {
            logger.error("", e);
        }
        ecologyQuestion.setAnswerNum(answers.size());
        ecologyQuestion.setAnswers(answers);
        ecologyQuestion.setFiedls(this.getFields(question.getTagIds()));
        return ecologyQuestion;
    }

    public EcologyAnswer getEcologyAnswer(TEliteAnswer answer) {
        EcologyAnswer ecologyAnswer = new EcologyAnswer();
        ecologyAnswer.setId(answer.getId());
        ecologyAnswer.setLink(EliteUrlGenerator.getAnswerUrl(answer.getId()));
        ecologyAnswer.setContent(ContentUtil.parseContent(answer.getContent(), TEliteSourceType.findByValue(answer.getSource())).getPlainText());
        ecologyAnswer.setFavoriteNum(0);
        int likeNum = 0;
        try {
            CodeMsgData responseData = extendServiceAdapter.getBpInteractionStatisticsByTarget(answer.getId(), BpInteractionTargetType.ELITE_ANSWER);
            if(ResponseConstants.OK == responseData.getCode()){
                JSONObject jsonObject = JSONObject.fromObject(responseData.getData());
                likeNum = jsonObject.getInt(String.valueOf(BpInteractionType.LIKE.getValue()));
            }
        }catch (Exception e){
            logger.error("", e);
        }
        ecologyAnswer.setLikeNum(likeNum);
        ecologyAnswer.setAuthor(this.getEcologyUser(answer.getBpId()));
        return ecologyAnswer;
    }

    public EcologyUser getEcologyUser(long bpid) {
        EcologyUser ecologyUser = new EcologyUser();
        ecologyUser.setId(bpid);
        ecologyUser.setLink(EliteUrlGenerator.getUserUrl(bpid));
        ecologyUser.setAsk_link(EliteUrlGenerator.getUserAskUrl(bpid));
        try {
            UserInfo userInfo = userInfoService.getUserInfoByBpid(bpid);
            if (null != userInfo) {
                ecologyUser.setAvatar(userInfo.getAvatar().replace("http", "https"));
                ecologyUser.setNick(userInfo.getNick());
            }
            TEliteUser eliteUser = eliteAdapter.getUserByBpId(bpid);
            if (null != eliteUser && eliteUser.getBpId() > 0) {
                List<Integer> answerStatusList = new ArrayList<>();
                answerStatusList.add(EliteAnswerStatus.AUDITING.getValue());
                answerStatusList.add(EliteAnswerStatus.PUBLISHED.getValue());
                answerStatusList.add(EliteAnswerStatus.PASSED.getValue());
                ecologyUser.setAnswerNum(eliteAdapter.getUserAnswerNumByStatus(bpid, answerStatusList));
                ecologyUser.setDesc(eliteUser.getDescription());
                ecologyUser.setType(eliteUser.getIdentity());
                ecologyUser.setFields(this.getFields(eliteUser.getTagIds()));

            }
            int followedNum = 0;
            try {
                ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(bpid, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
                if (null != listResult) {
                    followedNum = (int)listResult.getTotal();
                }
            }catch (Exception e) {
                logger.error("", e);
            }
            ecologyUser.setFollowedNum(followedNum);

        }catch (Exception e) {
            logger.error("", e);
        }
        return ecologyUser;
    }

    public List<String> getFields(String tagIds) {
        List<String> fields = new ArrayList<>();
        if (StringUtils.isNotBlank(tagIds)) {
            String[] tagIdArray = tagIds.split(Constants.DEFAULT_SPLIT_CHAR);
            for(String tagIdStr : tagIdArray) {
                try {
                    Tag tag = decorationAdapter.getTagById(Integer.parseInt(tagIdStr));
                    if (null != tag) {
                        fields.add(tag.getName());
                    }
                }catch (Exception e) {
                    logger.error("", e);
                }
            }

        }
        return fields;
    }

}
