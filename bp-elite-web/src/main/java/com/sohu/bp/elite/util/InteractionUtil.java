package com.sohu.bp.elite.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.decoration.outer.api.adapter.BpDecorationOuterApiAdapterFactory;
import com.sohu.bp.decoration.outer.api.adapter.BpInteractionServiceAdapter;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.enums.BpType;
import com.sohu.bp.elite.enums.EliteAnswerStatus;
import com.sohu.bp.elite.enums.EliteQuestionStatus;
import com.sohu.bp.elite.model.TAnswerListResult;
import com.sohu.bp.elite.model.TEliteAnswer;
import com.sohu.bp.elite.model.TQuestionListResult;
import com.sohu.bp.elite.model.TSearchAnswerCondition;
import com.sohu.bp.elite.model.TSearchQuestionCondition;
import com.sohu.bp.elite.service.web.EliteCacheService;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangzhihao
 *         2016/8/11
 */
public class InteractionUtil {

	private static Logger logger = LoggerFactory.getLogger(InteractionUtil.class);
    private static EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static BpExtendServiceAdapter extendServiceAdapter = BpServiceAdapterFactory.getBpExtendServiceAdapter();
    private static BpThalloServiceAdapter thalloServiceAdapter = BpThalloServiceAdapterFactory.getBpThalloServiceAdapter();
    private static BpInteractionServiceAdapter bpInteractionServiceAdapter = BpDecorationOuterApiAdapterFactory.getBpInteractionServiceAdapter();
    private static EliteCacheService eliteCacheService = SpringUtil.getBean("eliteCacheService", EliteCacheService.class);

    //获取问题社交相关数量 [0]关注数量，[1]回答数量，跟数组下标对应
    public static int[] getInteractionNumsForQuestion(Long questionId){
        int[] nums = new int[2];
        if(null == questionId || questionId.longValue() <= 0)
        	return nums;
        try {
            ObjectUserRelationListResult listResult1 = thalloServiceAdapter.getReactiveListByUserType(questionId, BpType.Question.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
            nums[0] = (int)listResult1.getTotal();

            TSearchAnswerCondition condition = new TSearchAnswerCondition();
            condition.setQuestionId(questionId)
                    .setFrom(0)
                    .setCount(1)
                    .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

            TAnswerListResult listResult2 = eliteAdapter.searchAnswer(condition);
            nums[1] = (int) listResult2.getTotal();
        } catch (TException e) {
        	logger.error("", e);
        }
        return nums;
    }
    
    //获取晒图等各种态度回答的数量，[0]赞成，[1]反对
    public static int[] getInteractionNumsForOption(Long questionId) {
        int[] nums = new int[2];
        if (null == questionId || questionId <= 0) return nums;
        try {
            TSearchAnswerCondition condition = new TSearchAnswerCondition();
            condition.setQuestionId(questionId).setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
            condition.setSpecialId(1);
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            nums[0] = (int) listResult.getTotal();
            condition.setSpecialId(2);
            listResult = eliteAdapter.searchAnswer(condition);
            nums[1] = (int) listResult.getTotal();
        } catch (Exception  e) {
            logger.error("", e);
        }
        return nums;
    }

    //获取回答社交相关数量 [0]点赞数量，[1] 踩数量, [2]评论数量，跟数组下标对应
    public static int[] getInteractionNumsForAnswer(Long answerId){
        int[] nums = new int[3];
        if(null == answerId || answerId.longValue() <= 0)
        	return nums;
        try
        {
	        CodeMsgData responseData = extendServiceAdapter.getBpInteractionStatisticsByTarget(answerId, BpInteractionTargetType.ELITE_ANSWER);
	        if(ResponseConstants.OK == responseData.getCode()){
	            JSONObject jsonObject = JSONObject.fromObject(responseData.getData());
	            nums[0] = jsonObject.getInt(String.valueOf(BpInteractionType.LIKE.getValue()));
	            nums[1] = jsonObject.getInt(String.valueOf(BpInteractionType.TREAD.getValue()));
                nums[2] = jsonObject.getInt(String.valueOf(BpInteractionType.COMMENT.getValue()));
            }
        }catch(Exception e)
        {
        	logger.error("", e);
        }
        return nums;
    }

    //获取用户社交相关数量 [0]关注数量，[1]问题数量，[2]回答数量，[3]点赞数量（所有回答点赞总数），[4]用户收藏回答数量 跟数组下标对应
    public static int[] getInteractionNumsForUser(Long bpId){
        int[] nums = new int[5];
        if(null == bpId || bpId.longValue() <= 0)
        	return nums;
        try {
            ObjectUserRelationListResult listResult0 = thalloServiceAdapter.getReactiveListByUserType(bpId, BpType.Elite_User.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
            nums[0] = (int)listResult0.getTotal();

            TSearchQuestionCondition condition1 = new TSearchQuestionCondition();
            condition1.setBpId(bpId)
                    .setFrom(0)
                    .setCount(1)
                    .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));

            TQuestionListResult listResult1 = eliteAdapter.searchQuestion(condition1);
            nums[1] = (int) listResult1.getTotal();

            TSearchAnswerCondition condition2 = new TSearchAnswerCondition();
            condition2.setBpId(bpId)
                    .setFrom(0)
                    .setCount(1)
                    .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));

            TAnswerListResult listResult2 = eliteAdapter.searchAnswer(condition2);
            nums[2] = (int) listResult2.getTotal();
            
            //deprecated
            //nums[3] = eliteCacheService.getTotalLikeNumOfUser(bpId);
            nums[3] = 0;
            
            com.alibaba.fastjson.JSONObject jsonObject = bpInteractionServiceAdapter.getFavoriteTargets(bpId, BpInteractionTargetType.ELITE_ANSWER, 0, 1);
            nums[4] = jsonObject.getIntValue("total");
        } catch (TException e) {
            logger.error("", e);
        }
        return nums;
    }
    
    //获取关注人id列表
    public static List<Long> getFollowUserList(Long targetId, BpType targetType, int start, int count) {
        List<Long> ids = new ArrayList<Long>();
        if (null == targetId || targetId <= 0 || null == targetType) return ids;
        try {
            ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(targetId, targetType.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, start, count);
            if (null != listResult && listResult.getTotal() > 0) {
                listResult.getObjectUserRelationList().forEach(relation -> ids.add(relation.getUserId()));
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return ids;
    }

    //获取tag相关数量[0]关注数量，[1]相关问题数，[2]相关回答数，跟数组下标对应
    public static int[] getInteractionNumsForTag(Integer tagId){
        int[] nums = new int[4];
        if(null == tagId || tagId.longValue() <= 0)
        	return nums;
        try {
            ObjectUserRelationListResult listResult = thalloServiceAdapter.getReactiveListByUserType(tagId, BpType.Tag.getValue(), BpType.Elite_User.getValue(), RelationStatus.FOLLOW, 0, 1);
            if(listResult != null && listResult.getTotal() > 0){
                nums[0] = (int) listResult.getTotal();
            }

            TSearchQuestionCondition searchQuestionCondition = new TSearchQuestionCondition();
            searchQuestionCondition.setTagIds(String.valueOf(tagId))
                                    .setFrom(0)
                                    .setCount(1)
                                    .setStatusArray(EliteStatusUtil.merge(EliteQuestionStatus.PUBLISHED.getValue(), EliteQuestionStatus.PASSED.getValue()));
            TQuestionListResult questionListResult = eliteAdapter.searchQuestion(searchQuestionCondition);
            nums[1] = (int) questionListResult.getTotal();

            TSearchAnswerCondition searchAnswerCondition = new TSearchAnswerCondition();
            searchAnswerCondition.setTagIds(String.valueOf(tagId))
                                .setFrom(0)
                                .setCount(1)
                                .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue()));
            TAnswerListResult answerListResult = eliteAdapter.searchAnswer(searchAnswerCondition);
            nums[2] = (int) answerListResult.getTotal();
        } catch (TException e) {
            logger.error("", e);
        }
        return nums;

    }
    
    /**
     * 判断userId是否关注了objectId
     * @param userId
     * @param userType
     * @param objectId
     * @param objectType
     * @return
     */
    public static boolean hasFollowed(Long userId, int userType, Long objectId, int objectType)
    {
    	boolean hasFollowed = false;
    	if(null == userId || userId.longValue() <= 0 || null == objectId || objectId.longValue() <= 0)
    		return hasFollowed;
    	try {
            List<RelationStatus> relationStatusList = thalloServiceAdapter.getRelation(userId, userType, objectId, objectType);
            if (null != relationStatusList && relationStatusList.contains(RelationStatus.FOLLOW)) {
            	hasFollowed = true;
            }
        } catch (TException e) {
            logger.error("", e);
            hasFollowed = false;
        }
    	
    	return hasFollowed;
    }
    
    public static boolean hasFollowed(Long bpId, Long objectId, int objectType) {
        return hasFollowed(bpId, BpType.Elite_User.getValue(), objectId, objectType);
    }
    
    /**
     * 是否点赞了
     * @param bpId
     * @param answerId
     * @return
     */
    public static boolean hasLiked(Long bpId, Long answerId){
        if (bpId == null || answerId == null || bpId <= 0 || answerId <= 0){
            return false;
        }

        CodeMsgData responseData1 = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, bpId, BpInteractionType.LIKE);
        if (ResponseConstants.OK == responseData1.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(responseData1.getData());
            if (interactionIds != null && interactionIds.size() > 0) {
                return true;
            }
        }
        return false;
    }
    /**
     * 是否踩了
     * @param bpId
     * @param answerId
     * @return
     */
    public static boolean hasTread(Long bpId, Long answerId) {
        if (null == bpId || answerId == null || bpId <= 0 || answerId <= 0) {
            return false;
        }
        
        CodeMsgData response = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(answerId, BpInteractionTargetType.ELITE_ANSWER, bpId, BpInteractionType.TREAD);
        if (ResponseConstants.OK == response.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(response.getData());
            if (null != interactionIds && interactionIds.size() > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 是否收藏
     * @param bpId
     * @param answerId
     * @return
     */
    public static boolean hasFavorited(Long bpId, Long answerId){
        return hasFavorited(bpId, answerId, null);
    }
    
    public static boolean hasFavorited(Long bpId, Long itemId, BpInteractionTargetType targetType) {
        if (bpId == null || itemId == null || bpId <= 0 || itemId <= 0){
            return false;
        }
        if (null == targetType) {
            targetType = BpInteractionTargetType.ELITE_ANSWER;
        }
        CodeMsgData responseData2 = extendServiceAdapter.getAllBpInteractionByTargetBpidInteraction(itemId, targetType, bpId, BpInteractionType.FAVORITE);
        if (ResponseConstants.OK == responseData2.getCode()) {
            JSONArray interactionIds = JSONArray.fromObject(responseData2.getData());
            if (interactionIds != null && interactionIds.size() > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 是否回答并站队
     * @param bpId
     * @param questionId
     * @return 1:是否回答 2:是否站队或投票
     */
    public static boolean[] hasAnsweredAndChoosed(Long bpId, Long questionId) {
        boolean[] flags = new boolean[]{false, false};
        if (null == bpId || null == questionId || bpId <= 0 || questionId <= 0) {
            return flags;
        }
        try {
            TSearchAnswerCondition condition = new TSearchAnswerCondition().setBpId(bpId).setQuestionId(questionId)
                    .setStatusArray(EliteStatusUtil.merge(EliteAnswerStatus.PUBLISHED.getValue(), EliteAnswerStatus.PASSED.getValue(), EliteAnswerStatus.CHOOSE.getValue(), EliteAnswerStatus.REJECTED.getValue()))
                    .setFrom(0).setCount(1);
            TAnswerListResult listResult = eliteAdapter.searchAnswer(condition);
            if (null != listResult && listResult.getTotal() > 0) {
                TEliteAnswer answer = listResult.getAnswers().get(0);
                if (StringUtils.isNotBlank(answer.getContent())) flags[0] = true;
                if (answer.getSpecialId() > 0) flags[1] = true;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return flags;
    }
}
