package com.sohu.bp.elite.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.bp.bean.ListResult;
import com.sohu.bp.bean.UserInfo;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapter;
import com.sohu.bp.decoration.adapter.BpDecorationServiceAdapterFactory;
import com.sohu.bp.decoration.model.Tag;
import com.sohu.bp.decoration.model.TagType;
import com.sohu.bp.elite.adapter.EliteServiceAdapterFactory;
import com.sohu.bp.elite.adapter.EliteThriftServiceAdapter;
import com.sohu.bp.elite.constants.Constants;
import com.sohu.bp.elite.enums.EliteUserIdentity;
import com.sohu.bp.elite.enums.EliteUserStatus;
import com.sohu.bp.elite.model.TEliteExpertTeam;
import com.sohu.bp.elite.model.TEliteQuestion;
import com.sohu.bp.elite.model.TSearchUserCondition;
import com.sohu.bp.elite.model.TUserIdListResult;
import com.sohu.bp.elite.service.external.UserInfoService;
import com.sohu.bp.elite.service.web.ExpertTeamService;
import com.sohu.bp.elite.strategy.EliteExpertTeamScoreStrategy;
import com.sohu.bp.elite.strategy.EliteExpertTeamStrategy;
import com.sohu.bp.elite.util.SpringUtil;


/**
 * 完成专家团推送逻辑的工作类
 * @author zhijungou
 * 2017年2月24日
 */
public class EliteExpertTeamAsyncTask implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(EliteExpertTeamAsyncTask.class);
    private static final EliteThriftServiceAdapter eliteAdapter = EliteServiceAdapterFactory.getEliteThriftServiceAdapter();
    private static final BpDecorationServiceAdapter decorationAdapter = BpDecorationServiceAdapterFactory.getBpDecorationServiceAdapter();
    private UserInfoService userInfoService;
    private ExpertTeamService expertTeamService;
    
    
    private Long questionId;
    private Long inviteId;
    private EliteExpertTeamStrategy strategy;
    private Future<Boolean> taskResult;
    
    public EliteExpertTeamAsyncTask(Long questionId, Long inviteId, Future<Boolean> taskResult) {
        this.questionId = questionId;
        this.inviteId = inviteId;
        this.taskResult = taskResult;
        userInfoService = SpringUtil.getBean("userInfoService", UserInfoService.class);
        expertTeamService = SpringUtil.getBean("expertTeamService", ExpertTeamService.class);
        this.strategy = new EliteExpertTeamScoreStrategy();
    }
    
    public void setStrategy(EliteExpertTeamStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public void run() {
        //等待问题的标签更新完毕
        if (null != taskResult) {
            try {
                taskResult.get();
                log.info("content resave has finished, questionid = {}", new Object[]{questionId});
            } catch (Exception e) {
               log.error("", e);
            } 
        }
        TEliteQuestion question = null;
        try {
         question = eliteAdapter.getQuestionById(questionId);
        } catch (Exception e) {
            log.error("", e);
            return;
        }
        String tagIds = question.getTagIds();
        String[] ids = tagIds.split(Constants.TAG_IDS_SEPARATOR);
        Set<Long> expertIds = new HashSet<Long>();
        Map<Long, TEliteExpertTeam> expertMap = new HashMap<Long, TEliteExpertTeam>();
        Map<Long, String> tagMap = new HashMap<Long, String>();
        TSearchUserCondition condition = new TSearchUserCondition();
        condition.setStatus(EliteUserStatus.VALID.getValue()).setIdentity(EliteUserIdentity.EXPERT.getValue()).setFrom(0).setCount(Integer.MAX_VALUE);
        List<Integer> tags = new ArrayList<Integer>();
        List<Integer> tagRange = new ArrayList<Integer>();
        try {
            tagRange = eliteAdapter.getExpertTagIds(); 
        } catch (Exception e) {
            log.error("", e);
        }
        for (String id : ids) {
            try{
                if (StringUtils.isBlank(id)) continue;
                Tag tag = decorationAdapter.getTagById(Integer.valueOf(id));
                //根据tag来寻找匹配专家
                if (tag.getType() == TagType.ELITE_LOCATION_TAG) {
                    condition.setTagIds(String.valueOf(tag.getId()));
                    TUserIdListResult listResult = eliteAdapter.searchUserId(condition);
                    fillTagMap(listResult.getUserIds(), expertIds, tagMap, tag);
                    String parentsStr = tag.getParents();
                    String[] idsStr = parentsStr.split(Constants.TAG_IDS_SEPARATOR);
                    for (int  i = 2; i < idsStr.length; i++) {
                        tag = decorationAdapter.getTagById(Integer.valueOf(idsStr[i]));
                        condition.setTagIds(idsStr[i]);
                        listResult = eliteAdapter.searchUserId(condition);
                        fillTagMap(listResult.getUserIds(), expertIds, tagMap, tag);
                    }
                    continue;
                }  
                String parentsStr = tag.getParents();
                //原始按照一级标签进行匹配的逻辑
//                String[] idsStr = parentsStr.split(Constants.TAG_IDS_SEPARATOR);
//                Integer tagId = null;
//                if (idsStr.length >= 2) {
//                    tagId = Integer.valueOf(idsStr[1]);
//                } else if (idsStr.length == 1) {
//                    tagId = Integer.valueOf(id);
//                }
//                if (null != tagId && !tags.contains(tagId)) {
//                    tags.add(tagId);
//                    tag = decorationAdapter.getTagById(tagId);
//                    condition.setTagIds(String.valueOf(tag.getId()));
//                    TUserIdListResult listResult = eliteAdapter.searchUserId(condition);
//                    fillTagMap(listResult.getUserIds(), expertIds, tagMap, tag);
//                }
                
                //新的标签匹配，按照admin后台设置的专家标签进行匹配
                List<Integer> parentIds = new ArrayList<Integer>();
                String[] idsStr = parentsStr.split(Constants.TAG_IDS_SEPARATOR);
                for (String idStr : idsStr) {
                    parentIds.add(Integer.valueOf(idStr));
                }
                parentIds.add(Integer.valueOf(id));
                for (Integer tagId : parentIds) {
                    if (tagRange.contains(tagId) && !tags.contains(tagId)) {
                        tags.add(tagId);
                          tag = decorationAdapter.getTagById(tagId);
                          condition.setTagIds(String.valueOf(tag.getId()));
                          TUserIdListResult listResult = eliteAdapter.searchUserId(condition);
                          fillTagMap(listResult.getUserIds(), expertIds, tagMap, tag);
                    }
                }
                
            } catch (Exception e) {
                log.error("", e);
            }
        }
        try {
            List<TEliteExpertTeam> expertTeams = eliteAdapter.getBatchExpertTeams(new ArrayList<>(expertIds));
            if (null != expertTeams && expertTeams.size() > 0) {
                expertTeams.forEach(expert -> expertMap.put(expert.getBpId(), expert));
            }
        } catch (Exception e) {
            log.error("", e);
        }
        log.info("questionId = {}, tagMap = {}", questionId, tagMap);
        log.info("questionId = {}, get origin expert team nums = {}, id = {}", new Object[]{questionId, expertIds.size(), expertIds});
        List<Long> selectedId = strategy.getSelectedExpertTeams(expertIds, tagMap, expertMap);
        log.info("questionId = {}, get selected expert team nums = {}, id = {}", new Object[]{questionId, selectedId.size(), selectedId});
        try {
            UserInfo userInfo = userInfoService.getUserInfoByBpid(inviteId);
            for (Long bpId : selectedId) {
                String tag = tagMap.get(bpId);
                Future<Boolean> result  = expertTeamService.postMessage(userInfo.getNick(), bpId, question, tag);
                if (result.get() == true) {
                    log.info("bpId = {}, questionId = {} elite expert team message has succeed push!", new Object[]{bpId, questionId});
                    eliteAdapter.addExpertNewPush(bpId, questionId);
                } else {
                    log.info("bpId = {}, questionId = {} elite expert team message push failed! push num doesn't change", new Object[]{bpId, questionId});
                }
                }
        } catch (Exception e) {
            log.error("", e);
        }
        expertTeamService.saveInviteStatusInCache(questionId, selectedId);
    }    
    
    private void fillTagMap(List<Long> list, Set<Long> bpIds, Map<Long, String> tagMap, Tag tag) {
        if (null == list || list.size() <= 0 ) return;
        for (Long bpId : list) {
            bpIds.add(bpId);
            tagMap.compute(bpId, (k, v) -> null == v ?  tag.getName() : v.concat(Constants.TAG_IDS_SEPARATOR).concat(tag.getName()));
        }
    }
}
