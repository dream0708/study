package com.sohu.bp.elite.service.web;

import java.util.List;
import java.util.concurrent.Future;

import com.sohu.bp.elite.model.TEliteQuestion;

/**
 * 专家团service
 * @author zhijungou
 * 2017年2月27日
 */
public interface ExpertTeamService {
    /**
     * 判断一用户是否为专家
     * @param bpId
     * @return
     */
    boolean isExpert(Long bpId);
    /**
     * 专家回答问题后更新持久化
     * @param bpId
     * @param questionId
     */
    void addNewAnswered(Long bpId, Long questionId);
    /**
     * 发送邀请信息(站内信，微信/短信)
     * @param scheme
     * @param name
     * @param invitedId
     * @param question
     * @param tags
     * @return
     */
    Future<Boolean> postMessage(String name, Long invitedId, TEliteQuestion question, String tags);
    /**
     * 将系统的邀请状态存入缓存
     * @param questionId
     * @param invitedIds
     * @return
     */
    boolean saveInviteStatusInCache(Long questionId, List<Long> invitedIds);
}
