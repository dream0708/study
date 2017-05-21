package com.sohu.bp.elite.dao;

import com.sohu.bp.elite.persistence.EliteAnswerBybpid;

import java.util.List;

/**
 * @author zhangzhihao
 *         2016/7/11
 */
public interface EliteAnswerByBpIdDao {

    /**
     * 插入新数据到关联表
     * @param answerBybpid
     * @return
     */
    Long save(EliteAnswerBybpid answerBybpid);

    /**
     * 根据bpId 和 问题id 得到关联数据
     * 用于更新状态等数据
     * @param bpId
     * @param answerId
     * @return
     */
    EliteAnswerBybpid get(Long bpId, Long answerId);

    /**
     * 更新关联表
     * @param answerBybpid
     */
    boolean update(EliteAnswerBybpid answerBybpid);

    /**
     * 根据bp id 获取所有回答id
     * 不包括删除状态的数据
     * @param bpId
     * @return
     */
    List<Long> getAnswerIds(Long bpId);

    /**
     * 获取用户某些状态的回答
     * @param bpId
     * @param statusList
     * @return
     */
    List<Long> getUserAnswersByStatus(Long bpId, List<Integer> statusList);

    int getUserAnswerNumByStatus(Long bpId, List<Integer> statusList);

}
